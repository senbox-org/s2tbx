package org.esa.s2tbx.dataio.spot6;

import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.spot6.dimap.Spot6Constants;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.io.File;
import java.util.Locale;

/**
 * Created by kraftek on 12/7/2015.
 */
public class Spot6ProductReaderPlugin extends BaseProductReaderPlugIn {

    public Spot6ProductReaderPlugin() {
        super();
        folderDepth = 4;
    }

    @Override
    public Class[] getInputTypes() {
        return Spot6Constants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new Spot6ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return Spot6Constants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return Spot6Constants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return Spot6Constants.DIMAP_DESCRIPTION;
    }

    public File getFileInput(Object input) {
        return super.getFileInput(input);
    }

    @Override
    protected String[] getMinimalPatternList() {
        return Spot6Constants.MINIMAL_PATTERN_LIST;
    }

    @Override
    protected String[] getExclusionPatternList() {
        return new String[0];
    }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("SPOT 6/7", Spot6Constants.SPOT6_RGB_PROFILE));
    }
}
