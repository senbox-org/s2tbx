package org.esa.s2tbx.gdal.reader.plugins;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class NITFDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public NITFDriverProductReaderPlugIn() {
        super(".ntf", "NITF", "National Imagery Transmission Format");
    }
}
