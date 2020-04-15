package org.esa.s2tbx.fcc;

import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.descriptor.*;

/**
 * Created by rdumitrascu on 7/18/2017.
 */
public class OperatorDescriptorClass implements OperatorDescriptor {

    private final OperatorDescriptor baseOperatorDescriptor;
    private ParameterDescriptor[] params;
    private SourceProductDescriptor[] sourceProducts;

    public OperatorDescriptorClass(OperatorDescriptor baseOperatorDescriptor, ParameterDescriptor[] params, SourceProductDescriptor[] sourceProducts) {
        if (baseOperatorDescriptor == null) {
            throw new NullPointerException("The operator descriptor is null.");
        }
        this.baseOperatorDescriptor = baseOperatorDescriptor;
        this.params = params;
        this.sourceProducts = sourceProducts;
    }

    @Override
    public String getName() {
        return this.baseOperatorDescriptor.getName();
    }

    @Override
    public String getAlias() {
        return this.baseOperatorDescriptor.getAlias();
    }

    @Override
    public String getLabel() {
        return this.baseOperatorDescriptor.getLabel();
    }

    @Override
    public String getDescription() {
        return this.baseOperatorDescriptor.getDescription();
    }

    @Override
    public String getVersion() {
        return this.baseOperatorDescriptor.getVersion();
    }

    @Override
    public String getAuthors() {
        return this.baseOperatorDescriptor.getAuthors();
    }

    @Override
    public String getCopyright() {
        return this.baseOperatorDescriptor.getCopyright();
    }

    @Override
    public boolean isInternal() {
        return this.baseOperatorDescriptor.isInternal();
    }

    @Override
    public boolean isAutoWriteDisabled() {
        return this.baseOperatorDescriptor.isAutoWriteDisabled();
    }

    @Override
    public Class<? extends Operator> getOperatorClass() {
        return null;
    }

    @Override
    public SourceProductDescriptor[] getSourceProductDescriptors() {
        return this.sourceProducts;
    }

    @Override
    public SourceProductsDescriptor getSourceProductsDescriptor() {
        return null;
    }

    @Override
    public ParameterDescriptor[] getParameterDescriptors() {
        return this.params;
    }

    @Override
    public TargetProductDescriptor getTargetProductDescriptor() {
        return null;
    }

    @Override
    public TargetPropertyDescriptor[] getTargetPropertyDescriptors() {
        return new TargetPropertyDescriptor[0];
    }
}
