package org.esa.beam.dataio.rapideye;

import org.esa.beam.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.beam.dataio.readers.BaseProductReaderPlugIn;
import org.esa.beam.framework.dataio.ProductReader;

import java.util.Locale;

/**
 * Reader plugin class for RapidEye L3 products.
 * RE L3 products have a GeoTIFF raster.
 */
public class RapidEyeL3ReaderPlugin extends BaseProductReaderPlugIn {

    @Override
    public Class[] getInputTypes() {
        return RapidEyeConstants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new RapidEyeL3Reader(this);
    }

    @Override
    public String[] getFormatNames() {
        return RapidEyeConstants.L3_FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return RapidEyeConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return RapidEyeConstants.L3_DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() { return RapidEyeConstants.L3_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return RapidEyeConstants.NOT_L3_FILENAME_PATTERNS; }

}
