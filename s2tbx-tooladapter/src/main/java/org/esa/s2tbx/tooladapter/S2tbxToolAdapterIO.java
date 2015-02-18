package org.esa.s2tbx.tooladapter;

import com.bc.ceres.core.runtime.RuntimeContext;
import org.esa.beam.framework.gpf.*;
import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

/**
 * Created by ramonag on 2/14/2015.
 */
public class S2tbxToolAdapterIO {

    static String basePath;

    static {
        try {
            basePath = S2tbxToolAdapterIO.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        } catch (Exception e) {
            BeamLogManager.getSystemLogger().severe(e.getMessage());
        }
    }

    public static OperatorSpi readOperatorFromFile(String toolName) throws OperatorException {
        final File toolModuleDir;

        try {
            //Look for the defined tool folder
            Enumeration<URL> resources = RuntimeContext.getResources(S2tbxToolAdapterConstants.TOOL_ADAPTER_REPO + toolName);
            if (!resources.hasMoreElements()) {
                throw new OperatorException("No configuration data for tool: " + toolName);
            }
            toolModuleDir = new File(resources.nextElement().toURI());
        } catch (IOException e) {
            throw new OperatorException(e.getMessage());
        } catch (URISyntaxException e) {
            throw new OperatorException(e.getMessage());
        }

        if (!toolModuleDir.exists()) {
            throw new OperatorException("Tool folder not found: " + toolModuleDir);
        }

        //Look for the descriptor
        File toolInfoXmlFile = new File(toolModuleDir, toolName + S2tbxToolAdapterConstants.OPERATOR_FILE_SUFIX);
        S2tbxOperatorDescriptor operatorDescriptor;
        if (toolInfoXmlFile.exists()) {
            operatorDescriptor = S2tbxOperatorDescriptor.fromXml(toolInfoXmlFile, S2tbxToolAdapterIO.class.getClassLoader());
        } else {
            operatorDescriptor = new S2tbxOperatorDescriptor(toolName, S2tbxToolAdapterOp.class);
            BeamLogManager.getSystemLogger().warning(String.format("Missing operator metadata file '%s'", toolInfoXmlFile));
        }

        S2tbxToolAdapterOpSpi operatorSpi = new S2tbxToolAdapterOpSpi(operatorDescriptor) {

            @Override
            public Operator createOperator() throws OperatorException {
                S2tbxToolAdapterOp toolOperator = (S2tbxToolAdapterOp) super.createOperator();

                toolOperator.setParameterDefaultValues();
                toolOperator.setToolDescFolder(toolModuleDir.getPath());
                toolOperator.setToolName(toolName);
                return toolOperator;
            }
        };
        return operatorSpi;
    }

    public static String readOperatorTemplate(String toolName) throws IOException{
        File file = getTemplateFile(toolName);
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return new String(encoded, Charset.defaultCharset());
    }

    public static void writeOperatorTemplate(String toolName, String content) throws IOException{
        File file = getTemplateFile(toolName);
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    public static void saveAndRegisterOperator(S2tbxOperatorDescriptor operator, String templateContent) throws IOException{
        OperatorSpi spi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(operator.getName());
        String toolModuleDir = S2tbxToolAdapterConstants.TOOL_ADAPTER_REPO + operator.getAlias() + File.separator;
        if(spi == null){
            S2tbxToolAdapterOpSpi operatorSpi = new S2tbxToolAdapterOpSpi(operator) {

                @Override
                public Operator createOperator() throws OperatorException {
                    S2tbxToolAdapterOp toolOperator = (S2tbxToolAdapterOp) super.createOperator();

                    toolOperator.setParameterDefaultValues();
                    toolOperator.setToolDescFolder(toolModuleDir);
                    toolOperator.setToolName(operator.getName());
                    return toolOperator;
                }
            };
            GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(operator.getName(), operatorSpi);
        } else {
            //TODO SHOULD BE REMOVED, and added again, or the refrence get completly changed?
        }
        File toolInfoXmlFile = new File(toolModuleDir + operator.getAlias() + S2tbxToolAdapterConstants.OPERATOR_FILE_SUFIX);
        if (!toolInfoXmlFile.exists()) {
            toolInfoXmlFile.createNewFile();
        }
        String xmlContent = operator.toXml(S2tbxToolAdapterIO.class.getClassLoader());
        FileWriter writer = new FileWriter(toolInfoXmlFile);
        writer.write(xmlContent);
        writer.flush();
        writer.close();
        writeOperatorTemplate(operator.getName(), templateContent);
    }

    private static File getTemplateFile(String toolName) {
        OperatorSpi spi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(toolName);
        String templateFile = ((S2tbxOperatorDescriptor)spi.getOperatorDescriptor()).getTemplateFileLocation();
        return new File(basePath, S2tbxToolAdapterConstants.TOOL_ADAPTER_REPO + spi.getOperatorAlias() + File.separator + templateFile);
    }
}
