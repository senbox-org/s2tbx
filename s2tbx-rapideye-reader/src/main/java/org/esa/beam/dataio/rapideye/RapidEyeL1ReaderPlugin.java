package org.esa.beam.dataio.rapideye;

import org.esa.beam.dataio.readers.BaseProductReaderPlugIn;
import org.esa.beam.framework.dataio.ProductReader;

import java.util.Locale;

/**
 * Reader plugin class for RapidEye L1 products.
 * RE L1 products have rasters in NITF format.
 *
 * @author  Cosmin Cara
 */
public class RapidEyeL1ReaderPlugin extends BaseProductReaderPlugIn {

    @Override
    public Class[] getInputTypes() { return RapidEyeConstants.READER_INPUT_TYPES; }

    @Override
    public ProductReader createReaderInstance() {
        return new RapidEyeL1Reader(this);
    }

    @Override
    public String[] getFormatNames() { return RapidEyeConstants.L1_FORMAT_NAMES; }

    @Override
    public String[] getDefaultFileExtensions() { return RapidEyeConstants.DEFAULT_EXTENSIONS; }

    @Override
    public String getDescription(Locale locale) { return RapidEyeConstants.L1_DESCRIPTION; }

    @Override
    protected String[] getProductFilePatterns() { return RapidEyeConstants.L1_FILENAME_PATTERNS; }

    @Override
    protected String[] getMinimalPatternList() { return RapidEyeConstants.L1_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }
}
