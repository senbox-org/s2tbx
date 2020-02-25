package org.esa.s2tbx.dataio.ikonos;

import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
import org.esa.s2tbx.dataio.ikonos.metadata.IkonosMetadataInspector;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Plugin for reading Ikonos files.
 * The files are GeoTIFF with DIMAP metadata.
 *
 * @author Denisa Stefanescu
 */
public class IkonosProductReaderPlugin extends BaseProductReaderPlugIn {

    private static final String COLOR_PALETTE_FILE_NAME = "Ikonos_color_palette.cpd";

    public IkonosProductReaderPlugin() {
        super("org/esa/s2tbx/dataio/ikonos/" + IkonosProductReaderPlugin.COLOR_PALETTE_FILE_NAME);
        //TODO Jean remove
        this.folderDepth = 1;
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new IkonosMetadataInspector();
    }
    
    @Override
    public Class[] getInputTypes() {
        return IkonosConstants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new IkonosProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return IkonosConstants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return IkonosConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return IkonosConstants.DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() {
        return IkonosConstants.MINIMAL_PRODUCT_PATTERNS;
    }

    @Override
    protected String[] getExclusionPatternList() {
        return new String[0];
    }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("Ikonos", IkonosConstants.IKONOS_RGB_PROFILE));
    }
}