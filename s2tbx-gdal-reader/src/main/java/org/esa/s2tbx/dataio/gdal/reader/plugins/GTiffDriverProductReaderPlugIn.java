package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.dataio.gdal.GDALVersion;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class GTiffDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public GTiffDriverProductReaderPlugIn() {
        //super("GTiff", "GeoTIFF");
        // make a difference between GDAL GeoTIFF and SNAP GeoTIFF reader, also display GDAL version in order to help users knowing if their GDAL version supports Cloud Optimized GeoTIFF,
        // in case this driver is enabled from GDAL
        super("GTiff", "GeoTIFF (GDAL " + GDALVersion.getGDALVersion().getId() + ")");

        addExtensin(".tif");
        addExtensin(".tiff");
    }
}
