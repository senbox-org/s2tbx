package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class GTiffDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public GTiffDriverProductReaderPlugIn() {
        super("GTiff", "GeoTIFF");

        addExtensin(".tif");
        addExtensin(".tiff");
    }
}
