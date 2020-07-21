package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.dataio.gdal.GDALVersion;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.dataio.geotiff.Utils;

import java.nio.file.Path;

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

        addExtension(".tif");
        addExtension(".tiff");
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        final DecodeQualification qualification = super.getDecodeQualification(input);
        if (qualification == DecodeQualification.UNABLE) {
            return qualification;
        }
        final Path filePath = getInput(input);
        try {
            // 2020-07-21 CC Added COG check
            return Utils.isCOGGeoTIFF(filePath) ? DecodeQualification.INTENDED : DecodeQualification.UNABLE;
        } catch (Exception e) {
            return DecodeQualification.UNABLE;
        }
    }
}
