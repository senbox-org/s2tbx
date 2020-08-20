package org.esa.s2tbx.dataio.muscate;

import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Created by obarrile on 26/01/2017.
 */
public class MuscateProductReaderPlugin extends BaseProductReaderPlugIn {

    public MuscateProductReaderPlugin() {
        super(null);
    }

    @Override
    public Class[] getInputTypes() {
        return MuscateConstants.MUSCATE_READER_INPUT_TYPES;
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new MuscateMetadataInspector();
    }

    @Override
    public ProductReader createReaderInstance() {
        return new MuscateProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return MuscateConstants.MUSCATE_FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return MuscateConstants.MUSCATE_DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return MuscateConstants.MUSCATE_DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() { return MuscateConstants.MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager manager = RGBImageProfileManager.getInstance();
        manager.addProfile(new RGBImageProfile("MUSCATE Surface Natural Colors", new String[]{"Surface_Reflectance_B4", "Surface_Reflectance_B3", "Surface_Reflectance_B2"}));
        manager.addProfile(new RGBImageProfile("MUSCATE Flat Natural Colors", new String[]{"Flat_Reflectance_B4", "Flat_Reflectance_B3", "Flat_Reflectance_B2"}));
        manager.addProfile(new RGBImageProfile("MUSCATE Surface False-color Infrared", new String[]{"Surface_Reflectance_B8", "Surface_Reflectance_B4", "Surface_Reflectance_B3"}));
        manager.addProfile(new RGBImageProfile("MUSCATE Flat False-color Infrared", new String[]{"Flat_Reflectance_B8", "Flat_Reflectance_B4", "Flat_Reflectance_B3"}));
    }
}
