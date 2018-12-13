package org.esa.s2tbx.dataio.worldviewESAarchive;

import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.worldviewESAarchive.common.WorldViewESAarchiveConstants;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Plugin for reading WorldView ESA archive files
 */

public class WorldViewESAarchiveProductReaderPlugin extends BaseProductReaderPlugIn {
    private static final String COLOR_PALETTE_FILE_NAME = "WorldViewESAarchive_color_palette.cpd";

    public WorldViewESAarchiveProductReaderPlugin() {
        super("org/esa/s2tbx/dataio/worldviewESAarchive/" + WorldViewESAarchiveProductReaderPlugin.COLOR_PALETTE_FILE_NAME);
        this.folderDepth = 1;
    }

    @Override
    public Class[] getInputTypes() {
        return WorldViewESAarchiveConstants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new WorldViewESAarchiveProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return WorldViewESAarchiveConstants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return WorldViewESAarchiveConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return WorldViewESAarchiveConstants.DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() {
        return WorldViewESAarchiveConstants.MINIMAL_PRODUCT_PATTERNS;
    }

    @Override
    protected String[] getExclusionPatternList() {
        return new String[0];
    }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("WorldView", WorldViewESAarchiveConstants.WORLDVIEW2_RGB_PROFILE));
    }
}
