package org.esa.beam.ui.tooladapter.utils;

import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.ValueRange;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.binding.converters.ArrayConverter;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyEditor;
import com.bc.ceres.swing.binding.PropertyEditorRegistry;
import org.apache.commons.lang.StringUtils;
import org.esa.beam.framework.gpf.descriptor.ParameterDescriptor;
import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.ToolParameterDescriptor;
import org.esa.beam.framework.gpf.ui.OperatorParameterSupport;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author Ramona Manda
 */
public class PropertyMemberUIWrapperFactory {

    public static PropertyMemberUIWrapper buildPropertyWrapper(String attributeName, ToolParameterDescriptor property, ToolAdapterOperatorDescriptor context, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        if (attributeName.equals("name")) {
            return buildNamePropertyWrapper(attributeName, property, context, 100, callback);
        }
        if (attributeName.equals("dataType")) {
            return buildTypePropertyWrapper(attributeName, property, context, 150, callback);
        }
        if (attributeName.equals("defaultValue")) {
            return buildValuePropertyEditorWrapper(attributeName, property, context, 250, callback);
        }
        Method getter = null;
        try {
            getter = property.getClass().getSuperclass().getDeclaredMethod("is" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1));
        } catch (NoSuchMethodException ex) {
        }
        Object value = null;
        try {
            value = property.getAttribute(attributeName);
        } catch (Exception ex) {
        }
        //TODO class/superclass!
        if (getter != null || (value != null && value.getClass().getSuperclass().equals(Boolean.class))) {
            return buildBooleanPropertyWrapper(attributeName, property, context, 30, callback);
        }
        try {
            getter = property.getClass().getSuperclass().getDeclaredMethod("get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1));
        } catch (NoSuchMethodException ex) {
        }
        if (getter != null && getter.getReturnType().equals(String.class)) {
            return buildStringPropertyWrapper(attributeName, property, context, 100, callback);
        }
        if (attributeName.equals("valueRange") || attributeName.equals("pattern") || attributeName.equals("valueSet")) {
            return buildStringPropertyWrapper(attributeName, property, context, 150, callback);
        }
        return buildEmptyWrapper(attributeName, property, context, 100, callback);
    }

    private static Object parseAttributeValue(String attributeName, String value, ToolParameterDescriptor property) throws PropertyAttributeException {
        if (value == null || value.length() == 0) {
            return null;
        }
        Method getter = null;
        try {
            getter = property.getClass().getSuperclass().getDeclaredMethod("get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1));
            if (getter != null && getter.getReturnType().equals(String.class)) {
                return value;
            }
            if (getter != null && getter.getReturnType().equals(String[].class)) {
                Object[] items = ValueSet.parseValueSet(value, String.class).getItems();
                String[] result = new String[items.length];
                for (int i = 0; i < items.length; i++) {
                    result[i] = items[i].toString();
                }
                return result;
            }
            if (getter != null && getter.getReturnType().equals(ValueRange.class)) {
                return ValueRange.parseValueRange(value);
            }
            if (getter != null && getter.getReturnType().equals(Pattern.class)) {
                return Pattern.compile(value);
            }
        } catch (Exception ex) {
            throw new PropertyAttributeException("Error on parsing the value " + value + " in order to set the value for attribute " + attributeName);
        }
        return value;
    }

    private static PropertyMemberUIWrapper buildStringPropertyWrapper(String attributeName, ToolParameterDescriptor property, ToolAdapterOperatorDescriptor context, int width, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        return new PropertyMemberUIWrapper(attributeName, property, context, width, callback) {
            public String getErrorValueMessage(Object value) {
                return null;
            }

            @Override
            protected void setMemberValue(Object value) throws PropertyAttributeException {
                property.setAttribute(attributeName, value);
            }

            @Override
            public String getMemberValue() throws PropertyAttributeException {
                Object obj = property.getAttribute(attributeName);
                if (obj == null) {
                    return "";
                }
                if (obj instanceof String[]) {
                    return StringUtils.join(((String[]) obj), ArrayConverter.SEPARATOR);
                }
                return obj.toString();
            }

            @Override
            protected Component buildUIComponent() throws Exception {
                JTextField field = new JTextField(getMemberValue());
                return field;
            }

            @Override
            protected Object getValueFromUIComponent() throws PropertyAttributeException {
                return PropertyMemberUIWrapperFactory.parseAttributeValue(attributeName, ((JTextField) UIComponent).getText(), property);
            }
        };
    }

    private static PropertyMemberUIWrapper buildBooleanPropertyWrapper(String attributeName, ToolParameterDescriptor property, ToolAdapterOperatorDescriptor context, int width, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        return new PropertyMemberUIWrapper(attributeName, property, context, width, callback) {

            @Override
            protected void setMemberValue(Object value) throws PropertyAttributeException {
                property.setAttribute(attributeName, value);
            }

            @Override
            public Boolean getMemberValue() throws PropertyAttributeException {
                Object obj = property.getAttribute(attributeName);
                if (obj == null) {
                    return false;
                }
                return (boolean) obj;
            }

            @Override
            protected Component buildUIComponent() throws PropertyAttributeException {
                JCheckBox button = new JCheckBox();
                button.setSelected(getMemberValue());
                return button;
            }

            @Override
            protected Boolean getValueFromUIComponent() throws PropertyAttributeException {
                return ((JCheckBox) UIComponent).isSelected();
            }
        };
    }

    private static PropertyMemberUIWrapper buildNamePropertyWrapper(String attributeName, ToolParameterDescriptor property, ToolAdapterOperatorDescriptor context, int width, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        return new PropertyMemberUIWrapper(attributeName, property, context, width, callback) {
            public String getErrorValueMessage(Object value) {
                if (value == null || !(value instanceof String) || ((String) value).length() == 0) {
                    return "Name of the property cannot be empty and must be a string!";
                }
                if (property.getName().equals(value)) {
                    return null;
                }
                //check if there is any other property with the same name, it should not!
                for (ParameterDescriptor prop : context.getParameterDescriptors()) {
                    if (prop != property && prop.getName().equals((value))) {
                        return "The operator must not have more then one parameter with the same name!";
                    }
                }
                return null;
            }

            @Override
            protected void setMemberValue(Object value) throws PropertyAttributeException {
                property.setName(value.toString());
            }

            @Override
            public String getMemberValue() {
                return property.getName();
            }

            @Override
            protected Component buildUIComponent() {
                JTextField field = new JTextField(getMemberValue());
                return field;
            }

            @Override
            protected String getValueFromUIComponent() throws PropertyAttributeException {
                return ((JTextField) UIComponent).getText();
            }
        };
    }

    private static PropertyMemberUIWrapper buildTypePropertyWrapper(String attributeName, ToolParameterDescriptor property, ToolAdapterOperatorDescriptor context, int width, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        return new PropertyMemberUIWrapper(attributeName, property, context, width, callback) {
            public String getErrorValueMessage(Object value) {
                if (value == null) {
                    return "Type of the property cannot be empty!";
                }
                return null;
            }

            @Override
            protected void setMemberValue(Object value) throws PropertyAttributeException {
                property.setDataType((Class<?>) value);
            }

            @Override
            public Class<?> getMemberValue() {
                return property.getDataType();
            }

            @Override
            protected Component buildUIComponent() {
                JTextField field = new JTextField(getMemberValue().getCanonicalName());
                return field;
            }

            @Override
            public boolean propertyUIComponentsNeedsRevalidation() {
                return true;
            }

            @Override
            protected Class<?> getValueFromUIComponent() throws PropertyAttributeException {
                try {
                    return Class.forName(((JTextField) UIComponent).getText());
                } catch (ClassNotFoundException ex) {
                    throw new PropertyAttributeException("Type of the property not found in the libraries");
                }
            }
        };
    }

    private static PropertyMemberUIWrapper buildValuePropertyEditorWrapper(String attributeName, ToolParameterDescriptor property, ToolAdapterOperatorDescriptor context, int width, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        return new PropertyMemberUIWrapper(attributeName, property, context, width, callback) {
            public String getErrorValueMessage(Object value) {
                return null;
            }

            @Override
            public void setMemberValue(Object value) throws PropertyAttributeException {
            }

            @Override
            public Object getMemberValue() {
                return null;
            }

            @Override
            protected Component buildUIComponent() {
                //                PropertySet propertySet = new OperatorParameterSupport(duplicatedOperatorSpi.getOperatorDescriptor()).getPropertySet();
                PropertySet propertySet = new OperatorParameterSupport(context).getPropertySet();
                PropertyEditor propertyEditor = PropertyEditorRegistry.getInstance().findPropertyEditor(
                        new PropertyDescriptor(property.getName(), property.getDataType()));
                JComponent editorComponent = propertyEditor.createEditorComponent(new PropertyDescriptor(property.getName(), property.getDataType()),
                        new BindingContext(propertySet));
                return editorComponent;
            }

            @Override
            protected Object getValueFromUIComponent() throws PropertyAttributeException {
                return null;
            }
        };
    }

    public static PropertyMemberUIWrapper buildEmptyWrapper(String attributeName, ToolParameterDescriptor property, ToolAdapterOperatorDescriptor context, int width, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        return new PropertyMemberUIWrapper(attributeName, property, context, width, callback) {
            @Override
            public String getErrorValueMessage(Object value) {
                return null;
            }

            @Override
            protected void setMemberValue(Object value) throws PropertyAttributeException {
            }

            @Override
            public Object getMemberValue() {
                return null;
            }

            @Override
            protected Component buildUIComponent() {
                return new JLabel("");
            }

            @Override
            protected Object getValueFromUIComponent() {
                return null;
            }
        };
    }

}
