package org.esa.s2tbx.dataio.worldview2;

import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Plugin for reading WorldView 2 files.
 * The files are GeoTIFF with xml metadata.
 *
 * @author Razvan Dumitrascu
 */

public class WorldView2ProductReaderPlugin extends BaseProductReaderPlugIn {

    private static final String COLOR_PALETTE_FILE_NAME = "WorldView_color_palette.cpd";

    public  WorldView2ProductReaderPlugin() {
        super("org/esa/s2tbx/dataio/worldview2/" + WorldView2ProductReaderPlugin.COLOR_PALETTE_FILE_NAME);
        this.folderDepth = 1;
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new WorldView2MetadataInspector();
    }

    @Override
    public Class[] getInputTypes() {
        return WorldView2Constants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new WorldView2ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return WorldView2Constants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return WorldView2Constants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return WorldView2Constants.DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() {
        return WorldView2Constants.MINIMAL_PRODUCT_PATTERNS;
    }

    @Override
    protected String[] getExclusionPatternList() {
        return new String[0];
    }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("WorldView-2", WorldView2Constants.WORLDVIEW2_RGB_PROFILE));
    }
}
