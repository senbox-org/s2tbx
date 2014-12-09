package org.esa.beam.dataio.spot;

import org.esa.beam.dataio.readers.BaseProductReaderPlugIn;
import org.esa.beam.dataio.spot.dimap.SpotConstants;
import org.esa.beam.framework.dataio.ProductReader;

import java.util.Locale;

/**
 * Visat plugin for reading SPOT-1 to SPOT-5 scene files.
 * The scene files are GeoTIFF with DIMAP metadata.
 * @author Cosmin Cara
 */
public class SpotDimapProductReaderPlugin extends BaseProductReaderPlugIn {

    @Override
    public Class[] getInputTypes() {
        return SpotConstants.DIMAP_READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new SpotDimapProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return SpotConstants.DIMAP_FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return SpotConstants.DIMAP_DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return SpotConstants.DIMAP_DESCRIPTION;
    }

    /*@Override
    protected String[] getProductFilePatterns() { return SpotConstants.DIMAP_FILENAME_PATTERNS; }*/

    @Override
    protected String[] getMinimalPatternList() { return SpotConstants.DIMAP_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }

}
