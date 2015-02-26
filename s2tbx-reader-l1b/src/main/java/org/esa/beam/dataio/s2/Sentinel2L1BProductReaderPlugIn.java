package org.esa.beam.dataio.s2;

import org.esa.beam.dataio.s2.filepatterns.S2L1bProductFilename;
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
public class Sentinel2L1BProductReaderPlugIn implements ProductReaderPlugIn {

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        BeamLogManager.getSystemLogger().fine("Getting decoders...");

        File file = new File(input.toString());
        DecodeQualification deco = S2L1bProductFilename.isProductFilename(file.getName()) ? DecodeQualification.SUITABLE : DecodeQualification.UNABLE;
        if (deco.equals(DecodeQualification.SUITABLE)) {
            String semantic = S2L1bProductFilename.create(file.getName()).fileSemantic;
            if (semantic.contains("L1B")) {
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

        return new Sentinel2L1BProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{S2L1bConfig.FORMAT_NAME};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{S2L1bConfig.MTD_EXT};
    }

    @Override
    public String getDescription(Locale locale) {
        return "Sentinel-2 MSI L1C";
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new BeamFileFilter(S2L1bConfig.FORMAT_NAME,
                                  getDefaultFileExtensions(),
                                  "Sentinel-2 MSI L1C product or tile");
    }
}
