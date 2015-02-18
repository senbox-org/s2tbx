package org.esa.beam.dataio.spot;

import org.esa.beam.dataio.readers.BaseProductReaderPlugIn;
import org.esa.beam.dataio.spot.dimap.SpotConstants;
import org.esa.beam.framework.dataio.ProductReader;

import java.util.Locale;

/**
 * Visat plugin for reading SPOT4 TAKE5 scene files.
 * The scene files are GeoTIFF with XML metadata.
 * @author Ramona Manda
 */
public class SpotTake5ProductReaderPlugin extends BaseProductReaderPlugIn {

    @Override
    public ProductReader createReaderInstance() {
        return new SpotTake5ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return SpotConstants.SPOT4_TAKE5_FORMAT_NAME;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return SpotConstants.SPOT4_TAKE5_DEFAULT_EXTENSION;
    }

    @Override
    public String getDescription(Locale locale) {
        return SpotConstants.SPOT4_TAKE5_DESCRIPTION;
    }

    @Override
    public Class[] getInputTypes() {
        return SpotConstants.SPOT4_TAKE5_READER_INPUT_TYPES;
    }

    /*@Override
    protected String[] getProductFilePatterns() { return SpotConstants.SPOT4_TAKE5_FILENAME_PATTERNS; }*/

    @Override
    protected String[] getMinimalPatternList() { return SpotConstants.SPOTTAKE5_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }
}
