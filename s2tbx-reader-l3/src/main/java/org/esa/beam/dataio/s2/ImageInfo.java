package org.esa.beam.dataio.s2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by opicas-p on 08/12/2014.
 */
public class ImageInfo {
    String fileName;
    Map<String, String> attributes;

    public ImageInfo(String fileName) {
        this.fileName = fileName;
        attributes = new HashMap<String, String>();
    }

    public String put(String key, String value) {
        return attributes.put(key, value);
    }

    public String get(Object key) {
        return attributes.get(key);
    }

    public boolean containsKey(Object key) {
        return attributes.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return attributes.containsValue(value);
    }

    public String getFileName() {
        return fileName;
    }
}
