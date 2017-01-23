package org.esa.s2tbx.dataio.jp2;

import org.esa.s2tbx.dataio.jp2.internal.JP2Constants;
import org.esa.snap.core.dataio.EncodeQualification;
import org.esa.snap.core.dataio.ProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.SnapFileFilter;
import java.util.Locale;

/**
 * Plugin for writing JP2 files
 *
 *  @author  Razvan Dumitrascu
 *  @since 5.0.2
 */
public class JP2ProductWriterPlugIn implements ProductWriterPlugIn {
    @Override
    public EncodeQualification getEncodeQualification(Product product) {

        GeoCoding geoCoding = product.getSceneGeoCoding();
        if (geoCoding == null) {
            return new EncodeQualification(EncodeQualification.Preservation.PARTIAL,
                    "The product is not geo-coded. A usual JP2 file will be written instead.");
        } else if (!(geoCoding instanceof CrsGeoCoding)) {
            return new EncodeQualification(EncodeQualification.Preservation.PARTIAL,
                    "The product is geo-coded but seems not rectified. Geo-coding information may not be properly preserved.");
        } else if (product.isMultiSize()) {
            return new EncodeQualification(EncodeQualification.Preservation.UNABLE,
                    "Cannot write multisize products. Consider resampling the product first.");
        } else {
            return new EncodeQualification(EncodeQualification.Preservation.FULL);
        }
    }

    @Override
    public Class[] getOutputTypes() {
        return JP2Constants.OUTPUT_TYPES;
    }

    @Override
    public ProductWriter createWriterInstance() {
        return new JP2ProductWriter(this);
    }

    @Override
    public String[] getFormatNames() {
        return JP2Constants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return JP2Constants.FILE_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return JP2Constants.DESCRIPTION;
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(getFormatNames()[0], getDefaultFileExtensions(), JP2Constants.DESCRIPTION);
    }
}
