package org.esa.s2tbx.gdal.reader.plugins;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class ECWDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public ECWDriverProductReaderPlugIn() {
        super(".ecw", "ECW", "ERDAS Compressed Wavelets");
    }
}
