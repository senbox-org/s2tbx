package org.esa.beam.framework.gpf.descriptor;

/**
 * @author Ramona Manda
 */
public class SystemVariable {
    String key;
    String value;

    SystemVariable() {
        this.key = "";
        this.value = "";
    }

    public SystemVariable(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SystemVariable createCopy() {
        SystemVariable newVariable = new SystemVariable();
        newVariable.setKey(this.key);
        newVariable.setValue(this.value);
        return newVariable;
    }

    //TODO delete this if not necessary!
    /*public static class XStreamConverter implements com.thoughtworks.xstream.converters.Converter {

        public boolean canConvert(Class aClass) {
            return SystemVariable.class.equals(aClass);
        }

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            SystemVariable headerParameter = (SystemVariable) source;
            writer.addAttribute("key", headerParameter.getKey());
            writer.addAttribute("value", headerParameter.getValue());
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            SystemVariable headerParameter = new SystemVariable();

            headerParameter.setKey(reader.getAttribute("key"));
            headerParameter.setValue(reader.getAttribute("value"));

            return headerParameter;
        }
    }*/
}
