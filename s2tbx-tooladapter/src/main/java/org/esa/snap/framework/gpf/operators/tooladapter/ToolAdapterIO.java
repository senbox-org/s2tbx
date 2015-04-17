package org.esa.snap.framework.gpf.operators.tooladapter;

import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.OperatorException;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.framework.gpf.OperatorSpiRegistry;
import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.util.SystemUtils;

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for performing various operations needed by ToolAdapterOp.
 *
 * @author Ramona Manda
 */
public class ToolAdapterIO {

    private static final String NB_USERDIR = "netbeans.user";
    private static final String[] SYS_SUBFOLDERS = { "modules", "extensions", "adapters" };
    private static final String[] USER_SUBFOLDERS = { "extensions", "adapters" };
    private static File systemModulePath;
    private static File userModulePath;
    private static Logger logger = Logger.getLogger(ToolAdapterIO.class.getName());

    /**
     * Constructs an OperatorSpi from a given folder.
     *
     * @param operatorFolder    The path containing the file/folder operator structure
     * @return                  An SPI for the read operator.
     * @throws OperatorException
     */
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

    /**
     * Reads the content of the operator Velocity template
     *
     * @param toolName      The name of the operator
     * @return
     * @throws IOException
     */
    public static String readOperatorTemplate(String toolName) throws IOException {
        File file = getTemplateFile(toolName);
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return new String(encoded, Charset.defaultCharset());
    }

    /**
     * Writes the content of the operator Velocity template.
     *
     * @param toolName      The name of the operator
     * @param content       The content of the template
     * @throws IOException
     */
    public static void writeOperatorTemplate(String toolName, String content) throws IOException {
        File file = getTemplateFile(toolName);
        saveFileContent(file, content);
    }

    /**
     * Removes the operator both from the SPI registry and from disk.
     *
     * @param operator      The operator descriptor to be removed
     */
    public static void removeOperator(ToolAdapterOperatorDescriptor operator) {
        removeOperator(operator, true);
    }

    /**
     * Saves any changes to the operator and registers it (in case of newly created ones).
     *
     * @param operator          The operator descriptor
     * @param templateContent   The content of the Velocity template
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void saveAndRegisterOperator(ToolAdapterOperatorDescriptor operator, String templateContent) throws IOException, URISyntaxException {
        removeOperator(operator, false);
        File rootFolder = getUserModulesPath();
        File moduleFolder = new File(rootFolder, operator.getAlias());
        if (!moduleFolder.exists()) {
            if (!moduleFolder.mkdir()) {
                throw new OperatorException("Operator folder " + moduleFolder + " could not be created!");
            }
        }
        ToolAdapterOpSpi operatorSpi = new ToolAdapterOpSpi(operator, moduleFolder);
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(operator.getName(), operatorSpi);
        File descriptorFile = new File(moduleFolder, ToolAdapterConstants.DESCRIPTOR_FILE);
        if (!descriptorFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            descriptorFile.getParentFile().mkdirs();
            if (!descriptorFile.createNewFile()) {
                throw new OperatorException("Operator file " + descriptorFile + " could not be created!");
            }
        }
        String xmlContent = operator.toXml(ToolAdapterIO.class.getClassLoader());
        saveFileContent(descriptorFile, xmlContent);
        writeOperatorTemplate(operator.getName(), templateContent);
    }

    /**
     * Scans for adapter modules in the system and user paths.
     *
     * @return
     * @throws IOException
     */
    public static List<File> scanForModules() throws IOException {
        logger.log(Level.INFO, "Loading external tools...");
        List<File> modules = new ArrayList<>();
        File systemModulesPath = getSystemModulesPath();
        logger.info("Scanning for external tools adapters: " + systemModulesPath.getAbsolutePath());
        modules.addAll(scanForModules(systemModulesPath));
        File userModulesPath = getUserModulesPath();
        logger.info("Scanning for external tools adapters: " + userModulesPath.getAbsolutePath());
        modules.addAll(scanForModules(userModulesPath));
        return modules;
    }

    /**
     * Returns the location of the user-defined modules.
     * @return
     */
    public static File getUserModulesPath() {
        if (userModulePath == null) {
            String moduleFolder = System.getProperty(NB_USERDIR);
            if (moduleFolder != null) {
                userModulePath = new File(moduleFolder, SystemUtils.getApplicationContextId());
                for (String subFolder : USER_SUBFOLDERS) {
                    userModulePath = new File(userModulePath, subFolder);
                }
            }
            if (!userModulePath.exists() && !userModulePath.mkdirs()) {
                logger.severe("Cannot create user folder for external tool adapter extensions");
            }
        }
        return userModulePath;
    }

    /**
     * Writes the given content in the specified file.
     *
     * @param file          The target file
     * @param content       The content to be written
     * @throws IOException
     */
    public static void saveFileContent(File file, String content) throws IOException{
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
            writer.close();
        }
    }

    private static File getSystemModulesPath() {
        if (systemModulePath == null) {
            String applicationHomePropertyName = SystemUtils.getApplicationHomePropertyName();
            if (applicationHomePropertyName == null) {
                applicationHomePropertyName = "user.dir";
            }
            String homeFolder = System.getProperty(applicationHomePropertyName);
            if (homeFolder == null) {
                homeFolder = System.getProperty("user.dir");
            }
            systemModulePath = new File(homeFolder);
            for (String subFolder : SYS_SUBFOLDERS) {
                systemModulePath = new File(systemModulePath, subFolder);
            }
            if (!systemModulePath.exists() && !systemModulePath.mkdirs()) {
                logger.severe("Cannot create system folder for external tool adapter extensions");
            }
        }
        return systemModulePath;
    }

    private static List<File> scanForModules(File path) throws IOException {
        if (!path.exists() || !path.isDirectory()) {
            throw new FileNotFoundException(path.getAbsolutePath());
        }
        File[] moduleFolders = path.listFiles();
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
        File template = new File(getSystemModulesPath(), spi.getOperatorAlias() + File.separator + templateFile);
        if (!template.exists()) {
            template = new File(getUserModulesPath(), spi.getOperatorAlias() + File.separator + templateFile);
        }
        return template;
    }

    private static void removeOperator(ToolAdapterOperatorDescriptor operator, boolean removeOperatorFolder) {
        OperatorSpiRegistry operatorSpiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
        OperatorSpi spi = operatorSpiRegistry.getOperatorSpi(operator.getName());
        if (spi != null) {
            operatorSpiRegistry.removeOperatorSpi(spi);
        }
        if (removeOperatorFolder) {
            File rootFolder = getUserModulesPath();
            File moduleFolder = new File(rootFolder, operator.getAlias());
            if (moduleFolder.exists()) {
                if (!moduleFolder.delete()) {
                    logger.warning(String.format("Folder %s cannot be deleted", moduleFolder.getAbsolutePath()));
                }
            }
        }
    }
}
