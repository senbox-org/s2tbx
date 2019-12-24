package org.esa.s2tbx.gdal.reader.plugins;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class VRTDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public VRTDriverProductReaderPlugIn() {
        super(".vrt", "VRT", "Virtual Raster");
    }
}
