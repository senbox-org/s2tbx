package org.esa.beam.framework.gpf.operators.tooladapter;

import com.bc.ceres.binding.Property;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.framework.gpf.internal.OperatorContext;
import org.esa.beam.utils.PrivilegedAccessor;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Tool Adapter operator
 *
 * @author Lucian Barbulescu
 */
@OperatorMetadata(alias = "ToolAdapterOp",
        category = "Tools",
        version = "1.0",
        description = "Tool Adapter Operator")
public class ToolAdapterOp extends Operator {

    /**
     * Consume the output created by a tool.
     */
    private ProcessOutputConsumer consumer;

    /**
     * Stop the tool's execution.
     */
    private boolean stop;

    /**
     * Synchronization lock.
     */
    private final Object lock;

    private ToolAdapterOperatorDescriptor descriptor;

    /**
     * The folder where the tool descriptors reside.
     */
    private File adapterFolder;

    private OperatorContext accessibleContext;

    /**
     * Constructor.
     */
    public ToolAdapterOp() {
        super();
        this.consumer = null;
        this.stop = false;
        this.lock = new Object();
        try {
            accessibleContext = (OperatorContext) PrivilegedAccessor.getValue(this, "context");
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
        }
        //this.descriptor = ((ToolAdapterOperatorDescriptor) accessibleContext.getOperatorSpi().getOperatorDescriptor());
    }

    /**
     * Set a consumer for the tool's output.
     *
     * @param consumer the output consumer.
     */
    public void setConsumer(ProcessOutputConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Command to stop the tool.
     * <p>
     * This method is synchronized.
     * </p>
     */
    public void stopTool() {
        synchronized (this.lock) {
            this.stop = true;
        }
    }

    /**
     * Check if a stop command was issued.
     * <p>
     * This method is synchronized.
     * </p>
     *
     * @return true if the execution of the tool must be stopped.
     */
    private boolean isStopped() {
        synchronized (this.lock) {
            return this.stop;
        }
    }

    public void setAdapterFolder(File folder) {
        this.adapterFolder = folder;
    }

    /**
     * Initialise and run the defined tool.
     * <p>
     * This method will block until the tool finishes its execution.
     * </p>
     *
     * @throws org.esa.beam.framework.gpf.OperatorException
     */
    @Override
    public void initialize() throws OperatorException {
        Date currentTime = new Date();

        if (descriptor == null) {
            descriptor = ((ToolAdapterOperatorDescriptor) accessibleContext.getOperatorSpi().getOperatorDescriptor());
        }

        //Validate the input
        validateDescriptorInput();

        //Prepare tool run
        prepareToolRun();

        //Run tool
        runTool();

        if (this.consumer != null) {
            Date finalDate = new Date();
            this.consumer.consumeOutputLine("Finished tool execution in " + (finalDate.getTime() - currentTime.getTime()) / 1000 + " seconds");
        }

        try {
            //Load target product
            loadFinalProduct();
        } catch (Exception ex) {
            throw new OperatorException("Could not load final product in memory : " + ex.getMessage());
        }
    }

    /**
     * Fill the templates with data and prepare the source product.
     *
     * @throws org.esa.beam.framework.gpf.OperatorException in case of an error
     */
    private void prepareToolRun() throws OperatorException {
        //TODO run preprocessing tool, get output and give as input to the main tool
    }

    /**
     * Run the tool.
     *
     * @return the return value of the process.
     * @throws OperatorException in case of an error.
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

            List<String> cmdLine = getToolCommandLine();
            logCommandLine(cmdLine);
            ProcessBuilder pb = new ProcessBuilder(cmdLine);

            //redirect the error of the tool to the standard output
            pb.redirectErrorStream(true);

            //set the working directory
            pb.directory(descriptor.getWorkingDir());

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
            throw new OperatorException("Error running tool " + descriptor.getName(), e);
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
                    throw new OperatorException("Error running tool " + descriptor.getName(), e);
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

    /**
     * Close any stream without triggering exceptions.
     *
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

    /**
     * Add the tool's command line to the log.
     *
     * @param cmdLine the command line
     */
    private void logCommandLine(List<String> cmdLine) {
        StringBuilder sb = new StringBuilder();
        sb.append("Running tool '").append(this.descriptor.getName()).append("' with command line: ");
        sb.append('\'').append(cmdLine.get(0));
        for (int i = 1; i < cmdLine.size(); i++) {
            sb.append(' ').append(cmdLine.get(i));
        }
        sb.append('\'');

        getLogger().log(Level.INFO, sb.toString());
    }

    /**
     * Load the result of the tool's execution.
     *
     * @throws OperatorException in case of an error
     */
    private void loadFinalProduct() throws OperatorException {
        File input = (File) getParameter(ToolAdapterConstants.TOOL_TARGET_PRODUCT_FILE);
        if (input == null) {
            //no target product, means the source product was changed
            //TODO all input files should be (re)-loaded since we do not know which one was changed
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

    /**
     * Verify that the data provided withing the operator descriptor is valid.
     *
     * @throws OperatorException in case of an error
     */
    private void validateDescriptorInput() throws OperatorException {

        //Get the tool file
        File toolFile = descriptor.getMainToolFileLocation();
        if (toolFile == null) {
            throw new OperatorException("Tool file not defined!");
        }
        // check if the tool file exists
        if (!toolFile.exists() || !toolFile.isFile()) {
            throw new OperatorException(String.format("Invalid tool file: '%s'!", toolFile.getAbsolutePath()));
        }

        //Get the tool's working directory
        File toolWorkingDirectory = descriptor.getWorkingDir();
        if (toolWorkingDirectory == null) {
            throw new OperatorException("Tool working directory not defined!");
        }
        // check if the tool file exists
        if (!toolWorkingDirectory.exists() || !toolWorkingDirectory.isDirectory()) {
            throw new OperatorException(String.format("Invalid tool working directory: '%s'!", toolWorkingDirectory.getAbsolutePath()));
        }

    }

    /**
     * Build the list of command line parameters.
     * <p>
     * If no command line template defined then only the tool's file is returned
     * </p>
     *
     * @return the list of command line parameters
     * @throws org.esa.beam.framework.gpf.OperatorException in case of an error.
     */
    private List<String> getToolCommandLine() throws OperatorException {
        final List<String> ret = new ArrayList<>();
        ret.add(descriptor.getMainToolFileLocation().getAbsolutePath());
        String cmdLineFileName = ((ToolAdapterOperatorDescriptor) (getSpi().getOperatorDescriptor())).getTemplateFileLocation();

        if (cmdLineFileName != null) {
            if (cmdLineFileName.endsWith(ToolAdapterConstants.TOOL_VELO_TEMPLATE_SUFIX)) {
                String result = transformVelocityTemplate(new File(this.adapterFolder, cmdLineFileName));
                ret.addAll(Arrays.asList(result.split("\r\n|\n")));
            } else {
                ret.addAll(getCommandLineParameters(cmdLineFileName));
            }
        }
        return ret;
    }

    /**
     * Get the list of command line parameters.
     *
     * @param cmdLineFileName the command line template file name
     * @return the list of parameters
     * @throws org.esa.beam.framework.gpf.OperatorException in case of an error
     */
    private List<String> getCommandLineParameters(final String cmdLineFileName) throws OperatorException {
        final List<String> ret = new ArrayList<>();

        //open the command line template file
        final File cmdLineTemplate = new File(this.adapterFolder, cmdLineFileName);
        try {
            //read the file line by line
            final LineNumberReader reader = new LineNumberReader(new FileReader(cmdLineTemplate));
            String line;
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

    /**
     * Process a template line by extracting all tags and replacing them with the corresponding values.
     *
     * @param line       the line of text to process
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
            if (tag.startsWith(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID)) {
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
                } else if (sourceProducts.length == 0) {
                    srcFileLocation = ((File) getParameter(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID, null)).getAbsolutePath();
                    //TODO check if exists!
                    if (srcFileLocation.length() <= 0 || !(new File(srcFileLocation)).exists()) {
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

    public String transformVelocityTemplate(File templateFile) {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", templateFile.getParent());
        Velocity.init(p);
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        Template t = Velocity.getTemplate(templateFile.getName());
        VelocityContext velContext = new VelocityContext();
        Property[] params = accessibleContext.getParameterSet().getProperties();
        for (Property param : params) {
            velContext.put(param.getName(), param.getValue());
        }
        Product[] sourceProducts = getSourceProducts();
        velContext.put(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID, sourceProducts[0]);
        File rasterFile = null;
        File productFile = sourceProducts[0].getFileLocation();
        if (productFile.isFile()) {
            rasterFile = productFile;
        } else {
            File[] files = productFile.listFiles((File dir, String name) -> name.endsWith(".tif"));
            if (files != null && files.length > 0) {
                rasterFile = files[0];
            }
        }
        velContext.put(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_FILE, rasterFile);
        for (int i = 0; i < sourceProducts.length; i++) {
            velContext.put(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID + ToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + (i + 1), sourceProducts[i]);
        }
        //velContext.put(ToolAdapterConstants.TOOL_TARGET_PRODUCT_ID, new Product("output", descriptor.getProcessingWriter(), sourceProducts[0].getSceneRasterWidth(), sourceProducts[0].getSceneRasterHeight()));
        StringWriter writer = new StringWriter();
        t.merge(velContext, writer);
        return writer.toString();
    }
}
