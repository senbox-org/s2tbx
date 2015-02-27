package org.esa.beam.framework.gpf.descriptor;

import org.esa.beam.ui.tooladapter.utils.PropertyAttributeException;

import java.lang.reflect.Method;

/**
 * @author Ramona Manda
 */
public class ToolParameterDescriptor extends DefaultParameterDescriptor {

    public ToolParameterDescriptor(String name, Class<?> type){
        super(name, type);
    }

    public ToolParameterDescriptor(DefaultParameterDescriptor object) {
        super(object.getName(), object.getDataType());
        super.setAlias(object.getAlias());
        super.setDefaultValue(object.getDefaultValue());
        super.setDescription(object.getDescription());
        super.setLabel(object.getLabel());
        super.setUnit(object.getUnit());
        super.setInterval(object.getInterval());
        super.setValueSet(object.getValueSet());
        super.setCondition(object.getCondition());
        super.setPattern(object.getPattern());
        super.setFormat(object.getFormat());
        super.setNotNull(object.isNotNull());
        super.setNotEmpty(object.isNotEmpty());
        super.setRasterDataNodeClass(object.getRasterDataNodeClass());
        super.setValidatorClass(object.getValidatorClass());
        super.setConverterClass(object.getConverterClass());
        super.setDomConverterClass(object.getDomConverterClass());
        super.setItemAlias(object.getItemAlias());
    }

    //TODO throws specific exception, also in the calling methods!
    public Object getAttribute(String propertyName) throws PropertyAttributeException {
        Method getter;
        try {
            //TODO
            getter = DefaultParameterDescriptor.class.getDeclaredMethod("is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
            return getter.invoke(this);
        } catch (Exception ex) {
        }
        //the "is..." getter could not be called
        try {
            getter = DefaultParameterDescriptor.class.getDeclaredMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
            return getter.invoke(this);
        } catch (Exception e) {
            throw new PropertyAttributeException("Exception on getting the value of the attribute '" + propertyName + "' message: " + e.getMessage());
        }
    }

    public void setAttribute(String propertyName, Object obj) throws PropertyAttributeException {
        Method setter;
        try {
            setter = DefaultParameterDescriptor.class.getDeclaredMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), obj.getClass());
            setter.invoke(this, obj);
        } catch (Exception e) {
            throw new PropertyAttributeException("Exception on setting the value '" + obj.toString() + "' to the attribute '" + propertyName + "' message: " + e.getMessage());
        }
    }
}
