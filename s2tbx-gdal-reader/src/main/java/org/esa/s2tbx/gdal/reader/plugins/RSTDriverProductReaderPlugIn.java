package org.esa.s2tbx.gdal.reader.plugins;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class RSTDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public RSTDriverProductReaderPlugIn() {
        super(".rst", "RST", "Idrisi Raster A.1");
    }
}
