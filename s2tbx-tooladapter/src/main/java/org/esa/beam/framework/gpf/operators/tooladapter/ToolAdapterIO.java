package org.esa.beam.framework.gpf.operators.tooladapter;

import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ramona Manda
 */
public class ToolAdapterIO {

    static String basePath;
    static Logger logger;

    static {
        logger = BeamLogManager.getSystemLogger();
        try {
            // TODO: This code will be removed when the location will be read from snap.properties
            basePath = ToolAdapterIO.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (!basePath.endsWith(File.separator))
                basePath += File.separator;
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    public static OperatorSpi readOperator(File operatorFolder) throws OperatorException {
        //Look for the descriptor
        File descriptorFile = new File(operatorFolder, ToolAdapterConstants.DESCRIPTOR_FILE);
        ToolAdapterOperatorDescriptor operatorDescriptor;
        if (descriptorFile.exists()) {
            operatorDescriptor = ToolAdapterOperatorDescriptor.fromXml(descriptorFile, ToolAdapterIO.class.getClassLoader());
        } else {
            operatorDescriptor = new ToolAdapterOperatorDescriptor(operatorFolder.getName(), ToolAdapterOp.class);
            logger.warning(String.format("Missing operator metadata file '%s'", descriptorFile));
        }

        return new ToolAdapterOpSpi(operatorDescriptor, operatorFolder);
    }

    public static String readOperatorTemplate(String toolName) throws IOException {
        File file = getTemplateFile(toolName);
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return new String(encoded, Charset.defaultCharset());
    }

    public static void writeOperatorTemplate(String toolName, String content) throws IOException {
        File file = getTemplateFile(toolName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
            writer.close();
        }
    }

    public static void saveAndRegisterOperator(ToolAdapterOperatorDescriptor operator, String templateContent) throws IOException, URISyntaxException {
        OperatorSpi spi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(operator.getName());
        File rootFolder = new File(getModulesPath());
        File moduleFolder = new File(rootFolder, operator.getAlias());
        if (!moduleFolder.exists()) {
            if (!moduleFolder.mkdir()) {
                throw new OperatorException("Operator folder " + moduleFolder + " could not be created!");
            }
        }
        if (spi == null) {
            ToolAdapterOpSpi operatorSpi = new ToolAdapterOpSpi(operator, moduleFolder);
            GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(operator.getName(), operatorSpi);
        } else {
            //TODO SHOULD BE REMOVED, and added again, or the refrence get completly changed?
        }
        File descriptorFile = new File(moduleFolder, ToolAdapterConstants.DESCRIPTOR_FILE);
        if (!descriptorFile.exists()) {
            if (!descriptorFile.createNewFile()) {
                throw new OperatorException("Operator file " + descriptorFile + " could not be created!");
            }
        }
        String xmlContent = operator.toXml(ToolAdapterIO.class.getClassLoader());
        try (FileWriter writer = new FileWriter(descriptorFile)) {
            writer.write(xmlContent);
            writer.flush();
            writer.close();
        }
        writeOperatorTemplate(operator.getName(), templateContent);
    }

    private static File getTemplateFile(String toolName) {
        OperatorSpi spi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(toolName);
        String templateFile = ((ToolAdapterOperatorDescriptor) spi.getOperatorDescriptor()).getTemplateFileLocation();
        return new File(getModulesPath(), spi.getOperatorAlias() + File.separator + templateFile);
    }

    public static String getAbsolutePath(String relativeToolFileLocation) {
        return new File(getModulesPath(), relativeToolFileLocation).getAbsolutePath();
    }

    public static List<File> scanForModules(String path) throws IOException {
        File root = new File(path);
        if (!root.exists() || !root.isDirectory()) {
            throw new FileNotFoundException(path);
        }
        File[] moduleFolders = root.listFiles();
        List<File> modules = new ArrayList<>();
        if (moduleFolders != null) {
            for (File moduleFolder : moduleFolders) {
                File descriptorFile = new File(moduleFolder, ToolAdapterConstants.DESCRIPTOR_FILE);
                if (descriptorFile.exists()) {
                    /*File spiFile = new File(moduleFolder, ToolAdapterConstants.SPI_FILE);
                    if (spiFile.exists()) {
                        try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(spiFile)))) {
                            String theOnlyLine = reader.readLine();
                            if (theOnlyLine != null && ToolAdapterConstants.SPI_FILE_CONTENT.equals(theOnlyLine)) {*/
                                modules.add(moduleFolder);
                            /*}
                            reader.close();
                        }
                    }*/
                }
            }
        }
        return modules;
    }

    /**
     * TODO: remove when reading the path from snap.properties
     * @return
     */
    private static String getModulesPath() {
        return basePath + ToolAdapterConstants.TOOL_ADAPTER_REPO;
    }
}
