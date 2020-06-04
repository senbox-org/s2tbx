package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.dataio.gdal.GDALVersion;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class GTiffDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public GTiffDriverProductReaderPlugIn() {
        super("GTiff", "GeoTIFF (GDAL " + GDALVersion.getGDALVersion().getId() + ")");

        addExtensin(".tif");
        addExtensin(".tiff");
    }
}
