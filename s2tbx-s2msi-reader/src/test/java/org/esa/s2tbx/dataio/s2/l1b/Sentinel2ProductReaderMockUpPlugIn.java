package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.util.io.SnapFileFilter;

import java.io.File;
import java.util.Locale;

/**
 * @author Norman Fomferra
 */
public class Sentinel2ProductReaderMockUpPlugIn implements ProductReaderPlugIn {
    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        if (new File(input.toString()).getName().equals("pom.xml")) {
            return DecodeQualification.SUITABLE;
        }
        return DecodeQualification.UNABLE;
    }

    @Override
    public Class[] getInputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public ProductReader createReaderInstance() {
        return new Sentinel2ProductReaderMockUp(this);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{"SENTINEL-2-MSI"};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{".xml"};
    }

    @Override
    public String getDescription(Locale locale) {
        return "Sentinel-2 MSI Data Product";
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(getFormatNames()[0],
                                  getDefaultFileExtensions()[0],
                                  getDescription(null));
    }
}
