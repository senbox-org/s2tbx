package org.esa.beam.dataio.s2;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.io.File;
import java.util.Locale;

/**
 * @author Norman Fomferra
 */
public class Sentinel2ProductReaderPlugIn implements ProductReaderPlugIn {

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        File file = new File(input.toString());
        return S2MetadataFilename.isMetadataFilename(file.getName()) ? DecodeQualification.INTENDED :
                S2ImageFilename.isImageFilename(file.getName()) ? DecodeQualification.SUITABLE :
                        DecodeQualification.UNABLE;
    }

    @Override
    public Class[] getInputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public ProductReader createReaderInstance() {
        return new Sentinel2ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{S2Config.FORMAT_NAME};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{S2Config.JP2_EXT, S2Config.MTD_EXT};
    }

    @Override
    public String getDescription(Locale locale) {
        return "Sentinel-2 MSI L1C";
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new BeamFileFilter(S2Config.FORMAT_NAME,
                                  getDefaultFileExtensions(),
                                  "Sentinel-2 MSI L1C product or tile");
    }
}
