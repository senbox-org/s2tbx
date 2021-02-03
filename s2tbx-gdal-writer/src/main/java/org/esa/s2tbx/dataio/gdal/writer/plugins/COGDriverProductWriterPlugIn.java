package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.GDALLoader;
import org.esa.snap.core.dataio.EncodeQualification;
import org.esa.snap.core.datamodel.Product;

/**
 * Writer plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class COGDriverProductWriterPlugIn extends AbstractDriverProductWriterPlugIn {

    public COGDriverProductWriterPlugIn() {
        super(".tif", "COG", "Cloud Optimized GeoTIFF", "Byte UInt16 Int16 UInt32 Int32 Float32 Float64 CInt16 CInt32 CFloat32 CFloat64");
    }

    public final EncodeQualification getEncodeQualification(Product product) {
        EncodeQualification encodeQualification = super.getEncodeQualification(product);
        if (encodeQualification.getPreservation().equals(EncodeQualification.Preservation.FULL) && !GDALLoader.getInstance().getGdalVersion().isCOGCapable()) {
            return new EncodeQualification(EncodeQualification.Preservation.UNABLE, "GDAL COG writer not supported by the used GDAL version (" + GDALLoader.getInstance().getGdalVersion().getId() + "). Upgrade to 3.1.0 or higher version.");
        }
        return encodeQualification;
    }
}
