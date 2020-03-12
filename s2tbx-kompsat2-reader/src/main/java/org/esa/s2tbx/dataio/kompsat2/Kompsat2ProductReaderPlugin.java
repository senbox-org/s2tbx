package org.esa.s2tbx.dataio.kompsat2;

import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Plugin for reading Kompsat 2 files.
 * The files are GeoTIFF with DIMAP metadata.
 *
 * @author Razvan Dumitrascu
 */
public class Kompsat2ProductReaderPlugin extends BaseProductReaderPlugIn {

    private static final String COLOR_PALETTE_FILE_NAME = "Kompsat_color_palette.cpd";

    public Kompsat2ProductReaderPlugin() {
        super("org/esa/s2tbx/dataio/kompsat2/" + Kompsat2ProductReaderPlugin.COLOR_PALETTE_FILE_NAME);
        this.folderDepth = 1;
    }

    @Override
    public Class[] getInputTypes() {
        return Kompsat2Constants.READER_INPUT_TYPES;
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new Kompsat2MetadataInspector();
    }

    @Override
    public ProductReader createReaderInstance() {
        return new Kompsat2ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return Kompsat2Constants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return Kompsat2Constants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return Kompsat2Constants.DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() {
        return Kompsat2Constants.MINIMAL_PRODUCT_PATTERNS;
    }

    @Override
    protected String[] getExclusionPatternList() {
        return new String[0];
    }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("Kompsat 2", Kompsat2Constants.KOMSAT2_RGB_PROFILE));
    }
}
