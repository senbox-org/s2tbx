package org.esa.s2tbx.tooladapter;

import com.bc.ceres.core.runtime.RuntimeContext;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.descriptor.DefaultOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.OperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author Lucian Barbulescu.
 */
public class S2tbxToolAdapterOpSpi extends OperatorSpi {

    /**
     * Default constructor.
     */
    public S2tbxToolAdapterOpSpi() {
        super(S2tbxToolAdapterOp.class);
    }

    /** Constructor.
     * @param operatorDescriptor the operator descriptor to be used.
     */
    public S2tbxToolAdapterOpSpi(OperatorDescriptor operatorDescriptor) {
        super(operatorDescriptor);

    }

    static {
        try {
            BeamLogManager.getSystemLogger().fine("Scanning for registered tools.");
            Enumeration<URL> resources = RuntimeContext.getResources(S2tbxToolAdapterConstants.TOOL_ADAPTER_REPO + S2tbxToolAdapterConstants.TOOL_ADAPTER_DB);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try {
                    BeamLogManager.getSystemLogger().fine("Tools list file found: " + url);
                    registerToolsModule(url);
                } catch (IOException e) {
                    BeamLogManager.getSystemLogger().warning("Failed to register tools seen in " + url + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            BeamLogManager.getSystemLogger().severe("Failed scan for Tools descriptors: I/O problem: " + e.getMessage());
        }
    }

    /** Read the tools list file and try to load anything that is described there.
     * @param url the url of the tools list file
     * @throws IOException if the file cannot be read or interpreted
     */
    private static void registerToolsModule(URL url) throws IOException {

        //Create a file reader
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(url.openStream()));
        while (true) {
            //read the file line by line
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                try {
                    registerModule(line);
                } catch (OperatorException e) {
                    BeamLogManager.getSystemLogger().warning(String.format("Invalid Tool name entry in %s (line %d): %s", url, reader.getLineNumber(), line));
                    BeamLogManager.getSystemLogger().warning(String.format("Caused by an I/O problem: %s", e.getMessage()));
                }
            }
        }
    }

    /** Register a tool as an operator.
     * @param toolName the name of the tool
     * @throws OperatorException in case of an error
     */
    public static void registerModule(String toolName) throws OperatorException {
        OperatorSpi operatorSpi = readOperatorFromFile(toolName);

        String operatorName = operatorSpi.getOperatorDescriptor().getName() != null ? operatorSpi.getOperatorDescriptor().getName() : operatorSpi.getOperatorDescriptor().getAlias();
        if(GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(operatorName) != null){
            throw new OperatorException("An operator with the name " + operatorName + " is already registered; replace the name in the folder " + S2tbxToolAdapterConstants.TOOL_ADAPTER_REPO);
        }
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(operatorName, operatorSpi);
        BeamLogManager.getSystemLogger().info(String.format("Tool operator '%s' registered!", toolName));
    }

    public static OperatorSpi readOperatorFromFile(String toolName) throws  OperatorException{
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
            operatorDescriptor = S2tbxOperatorDescriptor.fromXml(toolInfoXmlFile, S2tbxToolAdapterOpSpi.class.getClassLoader());
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

}
