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

    private static File modulePath;
    private static Logger logger = BeamLogManager.getSystemLogger();

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
        File rootFolder = getModulesPath();
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
            descriptorFile.getParentFile().mkdirs();
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
        if (spi == null) {
            throw new OperatorException("Cannot find the operator SPI");
        }
        ToolAdapterOperatorDescriptor operatorDescriptor = (ToolAdapterOperatorDescriptor) spi.getOperatorDescriptor();
        if (operatorDescriptor == null) {
            throw new OperatorException("Cannot read the operator template file");
        }
        String templateFile = operatorDescriptor.getTemplateFileLocation();
        return new File(getModulesPath(), spi.getOperatorAlias() + File.separator + templateFile);
    }

    public static String getAbsolutePath(String relativeToolFileLocation) {
        return new File(getModulesPath(), relativeToolFileLocation).getAbsolutePath();
    }

    public static List<File> scanForModules() throws IOException {
        File root = getModulesPath();
        if (!root.exists() || !root.isDirectory()) {
            throw new FileNotFoundException(root.getAbsolutePath());
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

    private static File getModulesPath() {
        if (modulePath == null) {
            String homeFolder = System.getProperty("snap.home");
            String moduleFolder = System.getProperty("snap.tooladapter.moduleFolder");
            if (moduleFolder == null) {
                modulePath = new File(new File(homeFolder, "extensions"), "adapters");
            } else {
                if (moduleFolder.startsWith("${snap.home}")) {
                    moduleFolder = homeFolder + moduleFolder.substring(12);
                }
                modulePath = new File(moduleFolder);
            }
            if (!modulePath.mkdirs()) {
                logger.severe("Cannot create folder for external tool adapter extensions");
            }
        }
        return modulePath;
    }
}
