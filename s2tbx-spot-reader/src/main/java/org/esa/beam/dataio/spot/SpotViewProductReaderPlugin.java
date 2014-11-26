package org.esa.beam.dataio.spot;

import org.esa.beam.dataio.readers.BaseProductReaderPlugIn;
import org.esa.beam.dataio.spot.dimap.SpotConstants;
import org.esa.beam.framework.dataio.ProductReader;

import java.util.Locale;

/**
 * Visat plugin for reading SPOT-4 and SPOT-5 view files which are not
 * in the "official" (DIMAP+GeoTIFF) format.
 *
 * @author  Cosmin Cara
 */
public class SpotViewProductReaderPlugin extends BaseProductReaderPlugIn {

    @Override
    public Class[] getInputTypes() {
        return SpotConstants.SPOTVIEW_READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new SpotViewProductReader(this);
    }

    @Override
    public String[] getFormatNames() { return SpotConstants.SPOTVIEW_FORMAT_NAMES; }

    @Override
    public String[] getDefaultFileExtensions() {
        return SpotConstants.SPOTVIEW_DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return SpotConstants.SPOTVIEW_DESCRIPTION;
    }

    @Override
    protected String[] getProductFilePatterns() { return SpotConstants.SPOTVIEW_FILENAME_PATTERNS; }

    @Override
    protected String[] getMinimalPatternList() { return SpotConstants.SPOTVIEW_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }

}
