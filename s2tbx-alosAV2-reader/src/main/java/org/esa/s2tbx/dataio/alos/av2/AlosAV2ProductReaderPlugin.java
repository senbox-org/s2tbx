package org.esa.s2tbx.dataio.alos.av2;

import org.esa.s2tbx.dataio.alos.av2.internal.AlosAV2Constants;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Plugin for reading ALOS AVNIR-2 files.
 * The files are GeoTIFF with DIMAP metadata
 *
 * @author Denisa Stefanescu
 */
public class AlosAV2ProductReaderPlugin extends BaseProductReaderPlugIn {

    private static final String COLOR_PALETTE_FILE_NAME = "AlosAV2_color_palette.cpd";

    public AlosAV2ProductReaderPlugin(){
        super("org/esa/s2tbx/dataio/alos/av2/"+AlosAV2ProductReaderPlugin.COLOR_PALETTE_FILE_NAME);
        this.folderDepth = 1;
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new AlosAV2MetadataInspector();
    }

    @Override
    public Class[] getInputTypes() {
        return AlosAV2Constants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new AlosAV2ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return AlosAV2Constants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return AlosAV2Constants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return AlosAV2Constants.DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() {
        return AlosAV2Constants.MINIMAL_PRODUCT_PATTERNS;
    }

    @Override
    protected String[] getExclusionPatternList() {
        return new String[0];
    }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("Alos AVNIR-2", AlosAV2Constants.ALOSAV2_RGB_PROFILE, new String[] {"ALOS", "*AV2*", "Alos*"}));
    }
}
