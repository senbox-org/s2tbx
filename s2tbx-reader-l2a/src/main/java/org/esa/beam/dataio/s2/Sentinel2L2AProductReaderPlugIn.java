package org.esa.beam.dataio.s2;

import org.esa.beam.dataio.s2.filepatterns.S2L2aProductFilename;
import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.util.Locale;

/**
 * @author Norman Fomferra
 */
public class Sentinel2L2AProductReaderPlugIn implements ProductReaderPlugIn {

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        BeamLogManager.getSystemLogger().fine("Getting decoders...");

        File file = new File(input.toString());
        DecodeQualification deco = S2L2aProductFilename.isProductFilename(file.getName()) ? DecodeQualification.SUITABLE : DecodeQualification.UNABLE;
        if (deco.equals(DecodeQualification.SUITABLE)) {
            if (S2L2aProductFilename.create(file.getName()).fileSemantic.contains("L2A")) {
                deco = DecodeQualification.INTENDED;
            }
        }

        return deco;
    }

    @Override
    public Class[] getInputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public ProductReader createReaderInstance() {
        BeamLogManager.getSystemLogger().info("Building product reader...");

        return new Sentinel2L2AProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{S2L2AConfig.FORMAT_NAME};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{S2L2AConfig.MTD_EXT};
    }

    @Override
    public String getDescription(Locale locale) {
        return "Sentinel-2 MSI L2A";
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new BeamFileFilter(S2L2AConfig.FORMAT_NAME,
                                  getDefaultFileExtensions(),
                                  "Sentinel-2 MSI L2A product or tile");
    }
}
