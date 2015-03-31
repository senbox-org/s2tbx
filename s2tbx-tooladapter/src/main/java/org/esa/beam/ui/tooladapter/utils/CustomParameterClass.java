package org.esa.beam.ui.tooladapter.utils;

import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterConstants;

import java.io.File;

/**
 * @author Ramona Manda
 */
public class CustomParameterClass {

    private Class<?> aClass;
    private int typeMask;

    public CustomParameterClass(Class<?> aClass, int typeMask){
        this.aClass = aClass;
        this.typeMask = typeMask;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public boolean isTemplateParameter() {
        return (typeMask & ToolAdapterConstants.TEMPLATE_PARAM_MASK) != 0;
    }

    public boolean isTemplateBefore() {
        return (typeMask & ToolAdapterConstants.TEMPLATE_BEFORE_MASK) != 0;
    }

    public boolean isTemplateAfter() {
        return (typeMask & ToolAdapterConstants.TEMPLATE_AFTER_MASK) != 0;
    }

    public boolean isParameter() {
        return (typeMask & ToolAdapterConstants.REGULAR_PARAM_MASK) != 0;
    }

    public int getTypeMask(){ return this.typeMask; }

    public static CustomParameterClass getObject(Class<?> aClass, int typeMask){
        CustomParameterClass result = matchClass(TemplateFileClass, aClass, typeMask);
        if(result == null){
            result = matchClass(BeforeTemplateFileClass, aClass, typeMask);
        }
        if(result == null){
            result = matchClass(AfterTemplateFileClass, aClass, typeMask);
        }
        if(result == null){
            result = matchClass(RegularFileClass, aClass, typeMask);
        }
        if(result == null){
            result = matchClass(StringClass, aClass, typeMask);
        }
        if(result == null){
            result = matchClass(IntegerClass, aClass, typeMask);
        }
        if(result == null){
            result = matchClass(ListClass, aClass, typeMask);
        }
        if(result == null){
            result = matchClass(BooleanClass, aClass, typeMask);
        }
        return result;
    }

    private static CustomParameterClass matchClass(CustomParameterClass paramClass, Class<?> aClass, int typeMask){
        if(paramClass.getaClass().equals(aClass) && paramClass.typeMask == typeMask){
            return paramClass;
        }
        return null;
    }

    public static final CustomParameterClass BeforeTemplateFileClass = new CustomParameterClass(File.class, ToolAdapterConstants.TEMPLATE_BEFORE_MASK);
    public static final CustomParameterClass AfterTemplateFileClass = new CustomParameterClass(File.class, ToolAdapterConstants.TEMPLATE_AFTER_MASK);
    public static final CustomParameterClass TemplateFileClass = new CustomParameterClass(File.class, ToolAdapterConstants.TEMPLATE_PARAM_MASK);
    public static final CustomParameterClass RegularFileClass = new CustomParameterClass(File.class, ToolAdapterConstants.REGULAR_PARAM_MASK);
    public static final CustomParameterClass StringClass = new CustomParameterClass(String.class, ToolAdapterConstants.REGULAR_PARAM_MASK);
    public static final CustomParameterClass IntegerClass = new CustomParameterClass(Integer.class, ToolAdapterConstants.REGULAR_PARAM_MASK);
    public static final CustomParameterClass ListClass = new CustomParameterClass(String[].class, ToolAdapterConstants.REGULAR_PARAM_MASK);
    public static final CustomParameterClass BooleanClass = new CustomParameterClass(Boolean.class, ToolAdapterConstants.REGULAR_PARAM_MASK);
}
