package org.esa.beam.framework.gpf.descriptor;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.esa.beam.util.StringUtils;

/**
 * Created by Ramona Manda on 2/13/2015.
 */
public class S2tbxSystemVariable {
    String key;
    String value;

    S2tbxSystemVariable(){
        this.key = "";
        this.value = "";
    }

    S2tbxSystemVariable(String key, String value){
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

    public static class XStreamConverter implements com.thoughtworks.xstream.converters.Converter {

        public boolean canConvert(Class aClass) {
            return S2tbxSystemVariable.class.equals(aClass);
        }

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            S2tbxSystemVariable headerParameter = (S2tbxSystemVariable) source;
            writer.addAttribute("key", headerParameter.getKey());
            writer.addAttribute("value", headerParameter.getValue());
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            S2tbxSystemVariable headerParameter = new S2tbxSystemVariable();

            headerParameter.setKey(reader.getAttribute("key"));
            headerParameter.setValue(reader.getAttribute("value"));

            return headerParameter;
        }
    }
}
