package org.esa.beam.framework.gpf.operators.tooladapter;

import com.bc.ceres.binding.Property;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductIOPlugInManager;
import org.esa.beam.framework.dataio.ProductWriterPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.framework.gpf.internal.OperatorContext;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.ProductUtils;
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

    private static final String INTERMEDIATE_PRODUCT_NAME = "interimProduct";
    private static final String[] DEFAULT_EXTENSIONS = { ".tif", ".tiff", ".nc", ".hdf", ".pgx", ".png", ".gif", ".jpg", ".bmp", ".pnm", ".pbm", ".pgm", ".ppm" };
    public static final String VELOCITY_LINE_SEPARATOR = "\r\n|\n";
    /**
     * Consume the output created by a tool.
     */
    private ProcessOutputConsumer consumer;

    /**
     * Stop the tool's execution.
     */
    private volatile boolean isStopped;

    private ToolAdapterOperatorDescriptor descriptor;

    private ProgressMonitor progressMonitor;

    private File intermediateProductFile;

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
        try {
            accessibleContext = (OperatorContext) PrivilegedAccessor.getValue(this, "context");
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
        }
        Velocity.init();
        this.progressMonitor = ProgressMonitor.NULL;
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

    public void setProgressMonitor(ProgressMonitor monitor) { this.progressMonitor = monitor; }

    /**
     * Command to isStopped the tool.
     * <p>
     * This method is synchronized.
     * </p>
     */
    public void stop() {
        this.isStopped = true;
    }

    /**
     * Check if a isStopped command was issued.
     * <p>
     * This method is synchronized.
     * </p>
     *
     * @return true if the execution of the tool must be stopped.
     */
    private boolean isStopped() {
        return this.isStopped;
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
        OperatorException exception = null;
        if (descriptor == null) {
            descriptor = ((ToolAdapterOperatorDescriptor) accessibleContext.getOperatorSpi().getOperatorDescriptor());
        }
        try {
            validateDescriptor();
        } catch (OperatorException validationException) {
            exception = validationException;
        }
        if (this.consumer == null ) {
            this.consumer = new DefaultOutputConsumer(descriptor.getProgressPattern(), descriptor.getErrorPattern(), this.progressMonitor);
        }
        if (!isStopped && exception == null) {
            try {
                beforeExecute();
            } catch (OperatorException beforeException) {
                exception = beforeException;
            }
        }
        if (!isStopped && exception == null) {
            try {
                execute();
            } catch (OperatorException executionException) {
                exception = executionException;
            }
        }
        if (this.consumer != null) {
            Date finalDate = new Date();
            this.consumer.consumeOutput("Finished tool execution in " + (finalDate.getTime() - currentTime.getTime()) / 1000 + " seconds");
        }
        if (exception == null) {
            try {
                postExecute();
            } catch (OperatorException afterException) {
                exception = afterException;
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    /**
     * Verify that the data provided withing the operator descriptor is valid.
     *
     * @throws OperatorException in case of an error
     */
    private void validateDescriptor() throws OperatorException {

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
     * Fill the templates with data and prepare the source product.
     *
     * @throws org.esa.beam.framework.gpf.OperatorException in case of an error
     */
    private void beforeExecute() throws OperatorException {
        if (descriptor.shouldWriteBeforeProcessing()) {
            String sourceFormatName = descriptor.getProcessingWriter();
            if (sourceFormatName != null) {
                this.progressMonitor.beginTask("Writing source product as " + sourceFormatName, 0);
                ProductIOPlugInManager registry = ProductIOPlugInManager.getInstance();
                Iterator<ProductWriterPlugIn> writerPlugIns = registry.getWriterPlugIns(sourceFormatName);
                ProductWriterPlugIn writerPlugIn = writerPlugIns.next();
                final Product selectedProduct = getSourceProduct();
                String sourceDefaultExtension = writerPlugIn.getDefaultFileExtensions()[0];
                File outFile = new File(descriptor.getWorkingDir(), INTERMEDIATE_PRODUCT_NAME + sourceDefaultExtension);
                boolean hasDeleted = false;
                while (outFile.exists() && !hasDeleted) {
                    hasDeleted = outFile.canWrite() && outFile.delete();
                    if (!hasDeleted) {
                        getLogger().warning(String.format("Could not delete previous temporary image %s", outFile.getName()));
                        outFile = new File(descriptor.getWorkingDir(), INTERMEDIATE_PRODUCT_NAME + "_" + new Date().getTime() + sourceDefaultExtension);
                    }
                }
                Product interimProduct = new Product(outFile.getName(), selectedProduct.getProductType(),
                        selectedProduct.getSceneRasterWidth(), selectedProduct.getSceneRasterHeight());
                try {
                    ProductUtils.copyProductNodes(selectedProduct, interimProduct);
                    for (Band sourceBand : selectedProduct.getBands()) {
                        ProductUtils.copyBand(sourceBand.getName(), selectedProduct, interimProduct, true);
                    }
                    ProductIO.writeProduct(interimProduct, outFile, sourceFormatName, true, SubProgressMonitor.create(progressMonitor, 50));
                } catch (IOException e) {
                    getLogger().severe("Cannot write to " + sourceFormatName + " format");
                    stop();
                } finally {
                    try {
                        interimProduct.closeIO();
                        interimProduct.dispose();
                    } catch (IOException e) {
                    }
                    interimProduct = null;
                }
                if (outFile.exists()) {
                    intermediateProductFile = outFile;
                } else {
                    stop();
                }
            }
        }
    }

    /**
     * Run the tool.
     *
     * @return the return value of the process.
     * @throws OperatorException in case of an error.
     */
    private int execute() throws OperatorException {
        Process process = null;
        BufferedReader outReader = null;
        int ret = -1;
        try {
            this.progressMonitor.setTaskName("Starting tool execution");
            List<String> cmdLine = getCommandLineTokens();
            logCommandLine(cmdLine);
            ProcessBuilder pb = new ProcessBuilder(cmdLine);
            //redirect the error of the tool to the standard output
            pb.redirectErrorStream(true);
            //set the working directory
            pb.directory(descriptor.getWorkingDir());
            //start the process
            process = pb.start();
            //get the process output
            outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (!isStopped()) {
                while (outReader.ready()) {
                    //read the process output line by line
                    String line = outReader.readLine();
                    //consume the line if possible
                    if (line != null && !"".equals(line.trim())) {
                        this.consumer.consumeOutput(line);
                    }
                }
                // check if the project finished execution
                if (!process.isAlive()) {
                    //isStopped the loop
                    stop();
                } else {
                    //yield the control to other threads
                    Thread.yield();
                }
            }
            if (process.exitValue() != 0) {
                throw new IOException(String.format("Process exited with value %d", process.exitValue()));
            }
        } catch (IOException e) {
            throw new OperatorException("Error running tool " + descriptor.getName(), e);
        } finally {
            if (process != null) {
                // if the process is still running, force it to isStopped
                if (process.isAlive()) {
                    //destroy the process
                    process.destroyForcibly();
                }
                try {
                    //wait for the project to end.
                    ret = process.waitFor();
                } catch (InterruptedException e) {
                    //noinspection ThrowFromFinallyBlock
                    throw new OperatorException("Error running tool " + descriptor.getName(), e);
                }

                //close the reader
                closeStream(outReader);
                //close all streams
                closeStream(process.getErrorStream());
                closeStream(process.getInputStream());
                closeStream(process.getOutputStream());
            }
        }

        return ret;
    }

    /**
     * Load the result of the tool's execution.
     *
     * @throws OperatorException in case of an error
     */
    private void postExecute() throws OperatorException {
        this.progressMonitor.setTaskName("Trying to open the new product");
        File input = (File) getParameter(ToolAdapterConstants.TOOL_TARGET_PRODUCT_FILE);
        if (input != null) {
            try {
                if (intermediateProductFile != null && intermediateProductFile.exists()) {
                    if (!(intermediateProductFile.canWrite() && intermediateProductFile.delete())) {
                        getLogger().warning(String.format("Temporary image %s could not be deleted", intermediateProductFile.getName()));
                    }
                }
                Product target = ProductIO.readProduct(input);
                for (Band band : target.getBands()) {
                    ImageManager.getInstance().getSourceImage(band, 0);
                }
                setTargetProduct(target);
            } catch (IOException e) {
                throw new OperatorException("Error reading product '" + input.getPath() + "'");
            }
        }
        if (this.consumer != null && this.consumer instanceof DefaultOutputConsumer) {
            ((DefaultOutputConsumer) this.consumer).close();
        }
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
        sb.append("Executing tool '").append(this.descriptor.getName()).append("' with command line: ");
        sb.append('\'').append(cmdLine.get(0));
        for (int i = 1; i < cmdLine.size(); i++) {
            sb.append(' ').append(cmdLine.get(i));
        }
        sb.append('\'');

        getLogger().log(Level.INFO, sb.toString());
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
    private List<String> getCommandLineTokens() throws OperatorException {
        final List<String> tokens = new ArrayList<>();
        String templateFile = ((ToolAdapterOperatorDescriptor) (getSpi().getOperatorDescriptor())).getTemplateFileLocation();
        if (templateFile != null) {
            tokens.add(descriptor.getMainToolFileLocation().getAbsolutePath());
            if (templateFile.endsWith(ToolAdapterConstants.TOOL_VELO_TEMPLATE_SUFIX)) {
                tokens.addAll(transformTemplate(new File(this.adapterFolder, templateFile)));
            } else {
                throw new OperatorException("Invalid Velocity template");
            }
        }
        return tokens;
    }

    private List<String> transformTemplate(File templateFile) {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("file.resource.loader.path", templateFile.getParent());
        ve.init();
        Template t = ve.getTemplate(templateFile.getName());
        VelocityContext velContext = new VelocityContext();
        Property[] params = accessibleContext.getParameterSet().getProperties();
        for (Property param : params) {
            velContext.put(param.getName(), param.getValue());
        }
        Product[] sourceProducts = getSourceProducts();
        velContext.put(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID,
                       sourceProducts.length == 1 ? sourceProducts[0] : sourceProducts);
        File[] rasterFiles = new File[sourceProducts.length];
        for (int i = 0; i < sourceProducts.length; i++) {
            File productFile = intermediateProductFile != null ? intermediateProductFile : sourceProducts[i].getFileLocation();
            rasterFiles[i] = productFile.isFile() ? productFile : selectCandidateRasterFile(productFile);
        }
        velContext.put(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_FILE,
                       rasterFiles.length == 1 ? rasterFiles[0] : rasterFiles);

        StringWriter writer = new StringWriter();
        t.merge(velContext, writer);
        String result = writer.toString();
        return Arrays.asList(result.split(VELOCITY_LINE_SEPARATOR));
    }

    private File selectCandidateRasterFile(File folder) {
        File rasterFile = null;
        List<File> candidates = getRasterFiles(folder);
        int numFiles = candidates.size() - 1;
        if (numFiles >= 0) {
            candidates.sort(Comparator.comparingLong(File::length));
            rasterFile = candidates.get(numFiles);
            getLogger().info(rasterFile.getName() + " was selected as raster file");
        }
        return rasterFile;
    }

    private List<File> getRasterFiles(File folder) {
        List<File> rasters = new ArrayList<>();
        for (String extension : DEFAULT_EXTENSIONS) {
            File[] files = folder.listFiles((File dir, String name) -> name.endsWith(extension));
            if (files != null) {
                rasters.addAll(Arrays.asList(files));
            }
            File[] subFolders = folder.listFiles(File::isDirectory);
            for(File subFolder : subFolders) {
                List<File> subCandidates = getRasterFiles(subFolder);
                if (subCandidates != null) {
                    rasters.addAll(subCandidates);
                }
            }
        }
        return rasters;
    }

}
