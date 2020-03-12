package org.esa.s2tbx.dataio.alos.pri;

import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIConstants;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;

import java.util.Locale;

/**
 * Plugin for reading ALOS PRISM files.
 * The files are GeoTIFF with DIMAP metadata
 *
 * @author Denisa Stefanescu
 */

public class AlosPRIProductReaderPlugin extends BaseProductReaderPlugIn {
    private static final String COLOR_PALETTE_FILE_NAME = "AlosPRI_color_palette.cpd";

    public AlosPRIProductReaderPlugin() {
        super("org/esa/s2tbx/dataio/alos/pri/" + AlosPRIProductReaderPlugin.COLOR_PALETTE_FILE_NAME);
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new AlosPRIMetadataInspector();
    }

    @Override
    public Class[] getInputTypes() {
        return AlosPRIConstants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new AlosPRIProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return AlosPRIConstants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return AlosPRIConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return AlosPRIConstants.DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() {
        return AlosPRIConstants.MINIMAL_PRODUCT_PATTERNS;
    }

    @Override
    protected String[] getExclusionPatternList() {
        return new String[0];
    }

    @Override
    protected void registerRGBProfile() {}

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        return super.getDecodeQualification(input);
    }
}
