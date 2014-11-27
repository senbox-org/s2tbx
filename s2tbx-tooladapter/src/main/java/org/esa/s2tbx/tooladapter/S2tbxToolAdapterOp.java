package org.esa.s2tbx.tooladapter;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductWriter;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.annotations.TargetProduct;
import org.esa.beam.util.io.FileUtils;
import org.esa.s2tbx.tooladapter.model.Tool;
import org.esa.s2tbx.tooladapter.model.ToolConverter;
import org.esa.s2tbx.tooladapter.model.exceptions.ToolAdapterException;

import java.io.*;

/**
 * Sentinel-2 Tool Adapter operator
 *
 * @author Lucian Barbulescu
 */
@OperatorMetadata(alias = "ToolAdapterOp",
        category = "Tools",
        version = "1.0",
        description = "Sentinel-2 Tool Adapter Operator")
public class S2tbxToolAdapterOp extends Operator {

    /** The tool to be executed. */
    @Parameter(
            converter = ToolConverter.class,
            notNull = true,
            description = "The structure containing all data required to run an external tool"
    )
    private Tool tool;

    @SourceProduct
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    /** The path to the source product used by the tool. */
    private String sourceProductPath;

    /** The path to the target product used by the tool. */
    private String targetProductPath;

    /** Consume the output created by a tool. */
    private ProcessOutputCounsumer consumer;

    /** Stop the tool's execution. */
    private boolean stop;

    /** Synchronization lock. */
    private Object lock;
    /**
     * Constructor.
     */
    public S2tbxToolAdapterOp() {
        this.consumer = null;
        this.stop = false;
        this.lock = new Object();
    }

    /** Set a consumer for the tool's output.
     * @param consumer the output consumer.
     */
    public void setConsumer(ProcessOutputCounsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Command to stop the tool.
     * <p>
     *     This method is synchronized.
     * </p>
     */
    public void stopTool() {
        synchronized (this.lock) {
            this.stop = true;
        }
    }

    /** Check if a stop command was issued.
     * <p>
     *     This method is synchronized.
     * </p>
     * @return true if the execution of the tool must be stopped.
     */
    private boolean isStopped() {
        synchronized (this.lock) {
            return this.stop;
        }
    }

    /**
     * Initialise and run the defined tool.
     * <p>
     *     This method will block until the tool finishes its execution.
     * </p>
     * @throws OperatorException
     */
    @Override
    public void initialize() throws OperatorException {
        //Validate the input
        validateInput();

        //Prepare tool run
        prepareToolRun();

        //Run tool
        runTool();

        //Load target product
        loadFinalProduct();
    }

    /** Set a new value for the Tool object.
     * @param tool the tool data.
     */
    public void setTool(Tool tool) {
        this.tool = tool;
    }

    /**
     * Fill the templates with data and prepare the source product.
     * @throws org.esa.beam.framework.gpf.OperatorException in case of an error
     */
    private void prepareToolRun() throws OperatorException {

        try {
            //Create the source product in the working directory

            ProductWriter productWriter = ProductIO.getProductWriter(this.tool.getSourceType());
            File sourceProductFile = new File(this.tool.getWorkingDirectory(), this.sourceProduct.getName());
            sourceProductFile = FileUtils.ensureExtension(sourceProductFile, productWriter.getWriterPlugIn().getDefaultFileExtensions()[0]);
            productWriter.writeProductNodes(this.sourceProduct, sourceProductFile);
            this.sourceProductPath = sourceProductFile.getAbsolutePath();

            //Define the path to the target product
            this.targetProductPath = new File(this.tool.getWorkingDirectory(), "target.tif").getAbsolutePath();

            //prepare files
            this.tool.fillTemplates(this.sourceProductPath, this.targetProductPath);
        } catch (ToolAdapterException e) {
            throw new OperatorException("Invalid tool definition!", e);
        } catch (IOException e) {
            throw new OperatorException("Cannot create source product in working folder!", e);
        }
    }

    /** Run the tool.
     * @throws OperatorException in case of an error.
     * @return the return value of the process.
     */
    private int runTool() throws OperatorException {
        Process proc = null;
        BufferedReader outReader = null;
        int ret = -1;
        try {
            //initialise stop flag.
            synchronized (this.lock) {
                this.stop = false;
            }

            //create the process builder object
            ProcessBuilder pb = new ProcessBuilder(S2tbxToolAdapterOp.this.tool.getToolWithCommandLine());

            //redirect the error of the tool to the standard output
            pb.redirectErrorStream(true);

            //set the working directory
            pb.directory(S2tbxToolAdapterOp.this.tool.getWorkingDirectory());

            //start the process
            proc = pb.start();

            //get the process output
            outReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            while (!isStopped()) {
                while (outReader.ready()) {
                    //read the process output line by line
                    String line = outReader.readLine();

                    //consume the line if possible
                    if (this.consumer != null) {
                        this.consumer.consumeOutpuLine(line);
                    }
                }
                // check if the project finished execution
                if (!proc.isAlive()) {
                    //stop the loop
                    stopTool();
                } else {
                    //yield the control to other threads
                    Thread.yield();
                }
            }
        } catch (IOException e) {
            throw new OperatorException("Error running tool " + S2tbxToolAdapterOp.this.tool.getName(), e);
        } finally {
            if (proc != null) {
                // if the process is still running, force it to stop
                if (proc.isAlive()) {
                    //destroy the process
                    proc.destroyForcibly();
                }
                try {
                    //wait for the project to end.
                    ret = proc.waitFor();
                } catch (InterruptedException e) {
                    throw new OperatorException("Error running tool " + S2tbxToolAdapterOp.this.tool.getName(), e);
                }

                //close the reader
                closeStream(outReader);
                //close all streams
                closeStream(proc.getErrorStream());
                closeStream(proc.getInputStream());
                closeStream(proc.getOutputStream());
            }
        }

        return ret;
    }

    /** Close any stream without triggering exceptions.
     * @param stream input or output stream.
     */
    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                //nothing to do.
            }
        }
    }

    /** Load the result of the tool's execution.
     *
     * @throws OperatorException in case of an error
     */
    private void loadFinalProduct() throws OperatorException {
        try {
            final File input = new File(this.targetProductPath);
            final ProductReader productReader = ProductIO.getProductReaderForInput(input);
            if (productReader == null) {
                throw new OperatorException("No product reader found for '" + this.targetProductPath + "'");
            }
            this.targetProduct = productReader.readProductNodes(input, null);
            if (this.targetProduct.getProductReader() == null) {
                this.targetProduct.setProductReader(productReader);
            }
        } catch (IOException e) {
            throw new OperatorException("Error reading product '" + this.targetProductPath + "'");
        }
    }

    /** Verify that the input data is valid
     * @throws OperatorException in case of an error
     */
    private void validateInput() throws OperatorException {
        //TODO: add validation
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2tbxToolAdapterOp.class);
        }
    }
}
