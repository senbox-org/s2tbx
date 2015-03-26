package org.esa.beam.framework.gpf.descriptor;

import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ramona Manda
 */
public class TemplateParameterDescriptor extends ToolParameterDescriptor{
    private List<ToolParameterDescriptor> toolParameterDescriptors;

    public TemplateParameterDescriptor(String name, Class<?> type, int typeMask){
        super(name, type);
        super.setParameterTypeMask(typeMask);
        this.setParameterTypeMask(ToolAdapterConstants.TEMPLATE_PARAM_MASK);
        this.toolParameterDescriptors = new ArrayList<>();
    }

    public TemplateParameterDescriptor(DefaultParameterDescriptor object, int typeMask) {
        super(object, typeMask);
        this.toolParameterDescriptors = new ArrayList<>();
    }

    public TemplateParameterDescriptor(ToolParameterDescriptor object) {
        super(object, object.getParameterTypeMask());
        this.toolParameterDescriptors = new ArrayList<>();
    }

    public void addParameterDescriptor(ToolParameterDescriptor descriptor){
        this.toolParameterDescriptors.add(descriptor);
    }

    public void removeParameterDescriptor(ToolParameterDescriptor descriptor){
        this.toolParameterDescriptors.remove(descriptor);
    }

    public List<ToolParameterDescriptor> getToolParameterDescriptors(){
        return this.toolParameterDescriptors;
    }

}
