package org.esa.beam.ui.tooladapter.utils;

import java.io.File;
import java.util.List;

/**
 * Created by ramonag on 3/19/2015.
 */
public class CustomParameterClass {

    private Class<?> aClass;
    private boolean isTemplate;

    public CustomParameterClass(Class<?> aClass, boolean isTemplate){
        this.aClass = aClass;
        this.isTemplate = isTemplate;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public static CustomParameterClass getObject(Class<?> aClass, boolean isTemplate){
        if(TemplateFileClass.getaClass().equals(aClass) && TemplateFileClass.isTemplate == isTemplate){
            return TemplateFileClass;
        }
        if(RegularFileClass.getaClass().equals(aClass) && RegularFileClass.isTemplate == isTemplate){
            return RegularFileClass;
        }
        if(StringClass.getaClass().equals(aClass) && StringClass.isTemplate == isTemplate){
            return StringClass;
        }
        if(IntegerClass.getaClass().equals(aClass) && IntegerClass.isTemplate == isTemplate){
            return IntegerClass;
        }
        if(ListClass.getaClass().equals(aClass) && ListClass.isTemplate == isTemplate){
            return ListClass;
        }
        if(BooleanClass.getaClass().equals(aClass) && BooleanClass.isTemplate == isTemplate){
            return BooleanClass;
        }
        return null;
    }

    public static final CustomParameterClass TemplateFileClass = new CustomParameterClass(File.class, true);
    public static final CustomParameterClass RegularFileClass = new CustomParameterClass(File.class, false);
    public static final CustomParameterClass StringClass = new CustomParameterClass(String.class, false);
    public static final CustomParameterClass IntegerClass = new CustomParameterClass(Integer.class, false);
    public static final CustomParameterClass ListClass = new CustomParameterClass(List.class, false);
    public static final CustomParameterClass BooleanClass = new CustomParameterClass(Boolean.class, false);
}
