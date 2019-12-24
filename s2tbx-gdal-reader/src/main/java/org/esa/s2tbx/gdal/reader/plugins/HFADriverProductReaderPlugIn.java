package org.esa.s2tbx.gdal.reader.plugins;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class HFADriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public HFADriverProductReaderPlugIn() {
        super(".img", "HFA", "Erdas Imagine Images");
    }
}
