package org.esa.beam.framework.gpf.operators.tooladapter;

import org.esa.beam.framework.gpf.*;
import org.esa.beam.framework.gpf.descriptor.OperatorDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The SPI class for ToolAdapterOp.
 *
 * @author Lucian Barbulescu.
 */
public class ToolAdapterOpSpi extends OperatorSpi {

    private File adapterFolder;

    static {
        registerModules();
    }

    /**
     * Scans for modules in the system and user paths and registers all
     * the modules that have been found.
     *
     * @return  A list of registered OperatorSPIs
     */
    public static Set<OperatorSpi> registerModules() {
        Logger logger = Logger.getLogger(ToolAdapterOpSpi.class.getName());
        Set<OperatorSpi> spis = new HashSet<>();
        try {
            List<File> moduleFolders = ToolAdapterIO.scanForModules();
            spis.addAll(moduleFolders.stream().map(ToolAdapterOpSpi::registerModule).collect(Collectors.toList()));
        } catch (IOException e) {
            logger.severe("Failed scan for Tools descriptors: I/O problem: " + e.getMessage());
        }
        return spis;
    }

    /**
     * Register a tool as an operator.
     *
     * @param moduleFolder the folder of the tool adapter
     * @throws OperatorException in case of an error
     */
    public static OperatorSpi registerModule(File moduleFolder) throws OperatorException {
        OperatorSpi operatorSpi = ToolAdapterIO.readOperator(moduleFolder);
        OperatorDescriptor operatorDescriptor = operatorSpi.getOperatorDescriptor();
        String operatorName = operatorDescriptor.getName() != null ? operatorDescriptor.getName() : operatorDescriptor.getAlias();
        OperatorSpiRegistry operatorSpiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
        if (operatorSpiRegistry.getOperatorSpi(operatorName) == null) {
            operatorSpiRegistry.addOperatorSpi(operatorName, operatorSpi);
        }
        return operatorSpi;
    }

    /**
     * Default constructor.
     */
    public ToolAdapterOpSpi() {
        super(ToolAdapterOp.class);
    }

    /**
     * Constructor.
     *
     * @param operatorDescriptor the operator adapterFolder to be used.
     */
    public ToolAdapterOpSpi(OperatorDescriptor operatorDescriptor) {
        super(operatorDescriptor);
    }

    public ToolAdapterOpSpi(OperatorDescriptor operatorDescriptor, File adapterFolder) {
        this(operatorDescriptor);
        this.adapterFolder = adapterFolder;
    }

    @Override
    public Operator createOperator() throws OperatorException {
        ToolAdapterOp toolOperator = (ToolAdapterOp) super.createOperator();
        toolOperator.setParameterDefaultValues();
        toolOperator.setAdapterFolder(this.adapterFolder);
        return toolOperator;
    }

}
