package org.esa.beam.framework.gpf.descriptor;

import com.bc.ceres.binding.Converter;
import com.bc.ceres.binding.Validator;
import com.bc.ceres.binding.dom.DomConverter;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.s2tbx.tooladapter.ui.utils.PropertyAttributeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ramonag on 1/25/2015.
 */
public class S2tbxParameterDescriptor extends DefaultParameterDescriptor {


    public S2tbxParameterDescriptor(DefaultParameterDescriptor object){
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


    public S2tbxParameterDescriptor(String name, Class<?> dataType){
        super(name, dataType);
    }

    //TODO throws specific exception, also in the calling methods!
    public Object getAttribute(String propertyName) throws PropertyAttributeException{
        Method getter = null;
        try {
            //TODO
            getter = DefaultParameterDescriptor.class.getDeclaredMethod("is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
            return getter.invoke(this);
        }catch (Exception ex){}
        //the "is..." getter could not be called
        try {
            getter = DefaultParameterDescriptor.class.getDeclaredMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
            return getter.invoke(this);
        }catch (InvocationTargetException e) {
           throw new PropertyAttributeException("Exception on getting the value of the attribute '"+propertyName+"' message: "+e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new PropertyAttributeException("Exception on getting the value of the attribute '"+propertyName+"' message: "+e.getMessage());
        } catch (IllegalAccessException e) {
            throw new PropertyAttributeException("Exception on getting the value of the attribute '"+propertyName+"' message: "+e.getMessage());
        }
    }

    public void setAttribute(String propertyName, Object obj) throws PropertyAttributeException{
        Method setter = null;
        try {
            setter = DefaultParameterDescriptor.class.getDeclaredMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), obj.getClass());
            setter.invoke(this, obj);
        } catch (IllegalAccessException e) {
            throw new PropertyAttributeException("Exception on setting the value '"+obj.toString()+"' to the attribute '"+propertyName+"' message: "+e.getMessage());
        } catch (InvocationTargetException e) {
            throw new PropertyAttributeException("Exception on setting the value '"+obj.toString()+"' to the attribute '"+propertyName+"' message: "+e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new PropertyAttributeException("Exception on setting the value '"+obj.toString()+"' to the attribute '"+propertyName+"' message: "+e.getMessage());
        }
    }
}
