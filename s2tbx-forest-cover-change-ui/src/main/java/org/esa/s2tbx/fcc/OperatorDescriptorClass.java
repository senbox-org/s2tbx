package org.esa.s2tbx.fcc;

import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.descriptor.*;

/**
 * Created by rdumitrascu on 7/18/2017.
 */
public class OperatorDescriptorClass implements OperatorDescriptor {
    private ParameterDescriptor[] params;
    private SourceProductDescriptor[] sourceProducts;

    public OperatorDescriptorClass(ParameterDescriptor[] params, SourceProductDescriptor[] sourceProducts ) {
        this.params  = params;
        this.sourceProducts = sourceProducts;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getAuthors() {
        return null;
    }

    @Override
    public String getCopyright() {
        return null;
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public boolean isAutoWriteDisabled() {
        return false;
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
