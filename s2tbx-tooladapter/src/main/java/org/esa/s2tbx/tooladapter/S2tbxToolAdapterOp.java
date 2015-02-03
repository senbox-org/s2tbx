package org.esa.s2tbx.tooladapter;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.descriptor.OperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;
import org.esa.s2tbx.tooladapter.ProcessOutputConsumer;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterConstants;
import org.geotools.xml.xsi.XSISimpleTypes;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

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

//    /** The tool to be executed. */
//    @Parameter(
//            converter = ToolConverter.class,
//            notNull = true,
//            description = "The structure containing all data required to run an external tool"
//    )
//    private Tool tool;
//
//    @SourceProduct
//    private Product sourceProduct;
//
//    @TargetProduct
//    private Product targetProduct;
//
//    /** The path to the source product used by the tool. */
//    private String sourceProductPath;
//
//    /** The path to the target product used by the tool. */
//    private String targetProductPath;

    /** Consume the output created by a tool. */
    private ProcessOutputConsumer consumer;

    /** Stop the tool's execution. */
    private boolean stop;

    /** Synchronization lock. */
    private Object lock;

    /** The folder where the tool descriptors reside. */
    private String toolDescFolder;

    /** The name of the tool. */
    private String toolName;

    /** The tool's file. */
    private File toolFile;

    /** The tool's working directory. */
    private File toolWorkingDirectory;


    /**
     * Constructor.
     */
    public S2tbxToolAdapterOp() {
        this.consumer = null;
        this.stop = false;
        this.lock = new Object();
    }

    /** Get the tool's descriptor folder
     * @return the tool's descriptor folder
     */
    public String getToolDescFolder() {
        return this.toolDescFolder;
    }

    /** Set the tool's descriptor folder.
     * @param toolDescFolder the tool's descriptor folder
     */
    public void setToolDescFolder(String toolDescFolder) {
        this.toolDescFolder = toolDescFolder;
    }

    /** Get the tool's name
     * @return the tool's name
     */
    public String getToolName() {
        return this.toolName;
    }

    /** Set the tool's name.
     * @param toolName the new tool name.
     */
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    /** Set a consumer for the tool's output.
     * @param consumer the output consumer.
     */
    public void setConsumer(ProcessOutputConsumer consumer) {
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
        Date currentTime = new Date();
        //Validate the input
        validateDescriptorInput();

        //Prepare tool run
        prepareToolRun();

        //Run tool
        runTool();

        if (this.consumer != null) {
            Date finalDate = new Date();
            this.consumer.consumeOutputLine("Finished tool execution in " + (finalDate.getTime() - currentTime.getTime())/1000 + " seconds");
        }

        try {
            //Load target product
            loadFinalProduct();
        }catch (Exception ex){
            throw new OperatorException("Could not load final product in memory : "+ex.getMessage());
        }
    }
//
//    /** Set a new value for the Tool object.
//     * @param tool the tool data.
//     */
//    public void setTool(Tool tool) {
//        this.tool = tool;
//    }

    /**
     * Fill the templates with data and prepare the source product.
     * @throws org.esa.beam.framework.gpf.OperatorException in case of an error
     */
    private void prepareToolRun() throws OperatorException {

//        try {
//            //Create the source product in the working directory
//
//            ProductWriter productWriter = ProductIO.getProductWriter(this.tool.getSourceType());
//            File sourceProductFile = new File(this.tool.getWorkingDirectory(), this.sourceProduct.getName());
//            sourceProductFile = FileUtils.ensureExtension(sourceProductFile, productWriter.getWriterPlugIn().getDefaultFileExtensions()[0]);
//            productWriter.writeProductNodes(this.sourceProduct, sourceProductFile);
//            this.sourceProductPath = sourceProductFile.getAbsolutePath();
//
//            //Define the path to the target product
//            this.targetProductPath = new File(this.tool.getWorkingDirectory(), "target.tif").getAbsolutePath();
//
//            //prepare files
//            this.tool.fillTemplates(this.sourceProductPath, this.targetProductPath);
//        } catch (ToolAdapterException e) {
//            throw new OperatorException("Invalid tool definition!", e);
//        } catch (IOException e) {
//            throw new OperatorException("Cannot create source product in working folder!", e);
//        }
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
            //ProcessBuilder pb = new ProcessBuilder(S2tbxToolAdapterOp.this.tool.getToolWithCommandLine());
            List<String> cmdLine = getToolCommandLine();
            logCommandLine(cmdLine);
            ProcessBuilder pb = new ProcessBuilder(cmdLine);

            //redirect the error of the tool to the standard output
            pb.redirectErrorStream(true);

            //set the working directory
            pb.directory(this.toolWorkingDirectory);

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
                        this.consumer.consumeOutputLine(line);
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
            throw new OperatorException("Error running tool " + this.toolName, e);
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
                    throw new OperatorException("Error running tool " + this.toolName, e);
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

    /** Add the tool's command line to the log.
     * @param cmdLine the command line
     */
    private void logCommandLine(List<String> cmdLine) {
        StringBuilder sb = new StringBuilder();
        sb.append("Running tool '").append(this.toolName).append("' with command line: ");
        sb.append('\'').append(cmdLine.get(0));
        for (int i = 1; i < cmdLine.size(); i++) {
            sb.append(' ').append(cmdLine.get(i));
        }
        sb.append('\'');

        getLogger().log(Level.INFO, sb.toString());
    }

    /** Load the result of the tool's execution.
     *
     * @throws OperatorException in case of an error
     */
    private void loadFinalProduct() throws OperatorException {
        File input = (File)getParameter(S2tbxToolAdapterConstants.TOOL_TARGET_PRODUCT_FILE_ID);
        if(input == null){
            //no target product, means the source product was changed
            //TODO all input files should be loaded since we do not know which one was changed
            input = getSourceProducts()[0].getFileLocation();
        }
        try {
            final ProductReader productReader = ProductIO.getProductReaderForInput(input);
            if (productReader == null) {
                throw new OperatorException("No product reader found for '" + input.getPath() + "'");
            }
            Product target = productReader.readProductNodes(input, null);
            if (target.getProductReader() == null) {
                target.setProductReader(productReader);
            }
            setTargetProduct(target);
        } catch (IOException e) {
            throw new OperatorException("Error reading product '" + input.getPath() + "'");
        }
    }

    /** Verify that the data provided withing the operator descriptor is valid.
     * @throws OperatorException in case of an error
     */
    private void validateDescriptorInput() throws OperatorException {

        //Get the tool file
        this.toolFile = ((S2tbxOperatorDescriptor)(getSpi().getOperatorDescriptor())).getMainToolFileLocation();
        if (this.toolFile == null) {
            throw new OperatorException("Tool file not defined!");
        }
        // check if the tool file exists
        if(!this.toolFile.exists() || !this.toolFile.isFile()) {
            throw new OperatorException(String.format("Invalid tool file: '%s'!",  this.toolFile.getAbsolutePath()));
        }

        //Get the tool's working directory
        this.toolWorkingDirectory = ((S2tbxOperatorDescriptor)(getSpi().getOperatorDescriptor())).getTemporaryFolder();
        if (this.toolWorkingDirectory == null) {
            throw new OperatorException("Tool working directory not defined!");
        }
        // check if the tool file exists
        if(!this.toolWorkingDirectory.exists() || !this.toolWorkingDirectory.isDirectory()) {
            throw new OperatorException(String.format("Invalid tool working directory: '%s'!", this.toolWorkingDirectory.getAbsolutePath()));
        }

    }

    /** Build the list of command line parameters.
     * <p>
     *      If no command line template defined then only the tool's file is returned
     * </p>
     * @return the list of command line parameters
     * @throws org.esa.beam.framework.gpf.OperatorException in case of an error.
     */
    private List<String> getToolCommandLine() throws OperatorException {
        final List<String> ret = new ArrayList<String>();

        //the first element is always the tool file
        ret.add(this.toolFile.getAbsolutePath());

        //get the command line parameter
        final String cmdLineFileName = ((S2tbxOperatorDescriptor)(getSpi().getOperatorDescriptor())).getCommandLineTemplate();
        if (cmdLineFileName != null) {
            ret.addAll(getCommandLineParameters(cmdLineFileName));
        }

        //return the list
        return ret;
    }

    /** Get the list of command line parameters.
     * @param cmdLineFileName the command line template file name
     * @return the list of parameters
     * @throws org.esa.beam.framework.gpf.OperatorException in case of an error
     */
    private List<String> getCommandLineParameters(final String cmdLineFileName) throws OperatorException {
        final List<String> ret = new ArrayList<String>();
        final OperatorDescriptor opDesc = this.getSpi().getOperatorDescriptor();

        //open the command line template file
        final File cmdLineTemplate = new File(this.toolDescFolder, cmdLineFileName);
        try {
            //read the file line by line
            final LineNumberReader reader = new LineNumberReader(new FileReader(cmdLineTemplate));
            String line = null;
            while ((line = reader.readLine()) != null) {
                //replace any found tag with the corresponding parameter values
                line = processTemplateLine(line, reader.getLineNumber());
                //add the processed line to the list
                ret.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new OperatorException(String.format("The command line template file '%s' does not exist!", cmdLineTemplate.getAbsolutePath()), e);
        } catch (IOException e) {
            throw new OperatorException(String.format("Error reading the command line template file '%s'!", cmdLineTemplate.getAbsolutePath()), e);
        }

        return ret;
    }

    /** Process a template line by extracting all tags and replacing them with the corresponding values.
     * @param line the line of text to process
     * @param lineNumber the numer of the line within the template file
     * @return the processed value.
     * @throws org.esa.beam.framework.gpf.OperatorException if the line cannot be processed
     */
    private String processTemplateLine(final String line, final int lineNumber) throws OperatorException {
        String result = line;
        while (true) {
            //look for a tag
            final int startTagIndex = result.indexOf("${");
            if (startTagIndex == -1) {
                //no more tags. Break the loop and return the result
                break;
            }
            final int endTagIndex = result.indexOf("}", startTagIndex);
            if (endTagIndex == -1) {
                throw new OperatorException(String.format("Missing end '}' at line %d ", lineNumber));
            }

            //get the parameter tag.
            final String tag = result.substring(startTagIndex + 2, endTagIndex);

            //Check if the tag is referring to a sourceProduct
            if (tag.startsWith(S2tbxToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID)) {
                Product[] sourceProducts = getSourceProducts();
                //This tag is related to a source product.
                String[] sourceTag = tag.split("\\.");
                int id = 0;
                if (sourceTag.length == 2) {
                    //the id of the source is after the '.'
                    id = Integer.parseInt(sourceTag[1]);
                }
                String srcFileLocation;
                if (id < sourceProducts.length) {
                    srcFileLocation = sourceProducts[id].getFileLocation().getAbsolutePath();
                } else if(sourceProducts.length == 0){
                    srcFileLocation = ((File)getParameter(S2tbxToolAdapterConstants.TOOL_SOURCE_PRODUCT_FILE, null)).getAbsolutePath();
                    //TODO check if exists!
                    if(srcFileLocation.length() <= 0 || !(new File(srcFileLocation)).exists()){
                        throw new OperatorException("The source product file not existing!");
                    }
                } else {
                    throw new OperatorException(String.format("The source id '%d' exceeds the number of existing sources (%d)!", id, sourceProducts.length));
                }
                result = result.replaceAll("\\$\\{" + sourceTag[0] + "\\}", srcFileLocation.replace("\\", "\\\\"));
            } else {
                //This tag is related to a parameter.
                final Object value = getParameter(tag, null);
                if (value != null) {
                    result = result.replaceAll("\\$\\{" + tag + "\\}", value.toString().replace("\\", "\\\\"));
                } else {
                    throw new OperatorException(String.format("The parameter '%s' was not found!", tag));
                }
            }
        }

        return result;
    }
}
