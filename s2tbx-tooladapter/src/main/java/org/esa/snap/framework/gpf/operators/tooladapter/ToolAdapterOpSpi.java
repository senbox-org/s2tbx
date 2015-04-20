package org.esa.snap.framework.gpf.operators.tooladapter;

import org.esa.snap.framework.gpf.Operator;
import org.esa.snap.framework.gpf.OperatorException;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.framework.gpf.descriptor.OperatorDescriptor;

import java.io.File;

/**
 * The SPI class for ToolAdapterOp.
 *
 * @author Lucian Barbulescu.
 */
public class ToolAdapterOpSpi extends OperatorSpi {

    private File adapterFolder;

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
