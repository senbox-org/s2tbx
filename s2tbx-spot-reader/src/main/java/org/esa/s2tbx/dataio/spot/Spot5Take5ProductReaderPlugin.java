package org.esa.s2tbx.dataio.spot;

import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.framework.datamodel.RGBImageProfile;
import org.esa.snap.framework.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Created by kraftek on 9/9/2015.
 */
public class Spot5Take5ProductReaderPlugin extends BaseProductReaderPlugIn {

    @Override
    public ProductReader createReaderInstance() {
        return new SpotTake5ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return SpotConstants.SPOT5_TAKE5_FORMAT_NAME;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return SpotConstants.SPOT4_TAKE5_DEFAULT_EXTENSION;
    }

    @Override
    public String getDescription(Locale locale) {
        return SpotConstants.SPOT5_TAKE5_DESCRIPTION;
    }

    @Override
    public Class[] getInputTypes() {
        return SpotConstants.SPOT4_TAKE5_READER_INPUT_TYPES;
    }

    @Override
    protected String[] getMinimalPatternList() { return SpotConstants.SPOT5TAKE5_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("SPOT", new String[] { "XS1", "XS2", "XS3" }));
    }
}
