package org.esa.beam.framework.gpf.descriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ramona Manda
 */
public class TemplateParameterDescriptor extends ToolParameterDescriptor{
    private List<ToolParameterDescriptor> toolParameterDescriptors;

    public TemplateParameterDescriptor(String name, Class<?> type){
        super(name, type);
        this.setTemplateFile(true);
        this.toolParameterDescriptors = new ArrayList<>();
    }

    public TemplateParameterDescriptor(DefaultParameterDescriptor object) {
        super(object);
        this.setTemplateFile(true);
        this.toolParameterDescriptors = new ArrayList<>();
    }

    public TemplateParameterDescriptor(ToolParameterDescriptor object) {
        super(object);
        this.setTemplateFile(true);
        this.toolParameterDescriptors = new ArrayList<>();
    }

    public void addParameterDescriptor(ToolParameterDescriptor descriptor){
        this.toolParameterDescriptors.add(descriptor);
    }

    public void removeParameterDescriptor(ToolParameterDescriptor descriptor){
        this.toolParameterDescriptors.remove(descriptor);
    }

}
