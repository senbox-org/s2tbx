/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */
package org.esa.snap.ui.tooladapter.model;

import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterConstants;

import java.io.File;

/**
 * @author Ramona Manda
 */
public class CustomParameterClass {

    private Class<?> aClass;
    private String typeMask;

    public CustomParameterClass(Class<?> aClass, String typeMask){
        this.aClass = aClass;
        this.typeMask = typeMask;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public boolean isTemplateParameter() {
        return typeMask.equals(ToolAdapterConstants.TEMPLATE_PARAM_MASK);
    }

    public boolean isTemplateBefore() {
        return typeMask.equals(ToolAdapterConstants.TEMPLATE_BEFORE_MASK);
    }

    public boolean isTemplateAfter() {
        return typeMask.equals(ToolAdapterConstants.TEMPLATE_AFTER_MASK);
    }

    public boolean isParameter() {
        return typeMask.equals(ToolAdapterConstants.REGULAR_PARAM_MASK);
    }

    public String getTypeMask(){ return this.typeMask; }

    public static CustomParameterClass getObject(Class<?> aClass, String typeMask){
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

    private static CustomParameterClass matchClass(CustomParameterClass paramClass, Class<?> aClass, String typeMask){
        if(paramClass.getaClass().equals(aClass) && paramClass.typeMask.equals(typeMask)){
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
