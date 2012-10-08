package org.esa.beam.dataio.s2;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.io.File;
import java.util.Locale;

/**
 * Dummy class so that Sentinel3ProductReader can be instantiated.
 * @author Norman Fomferra
 */
public class Sentinel2ProductReaderPlugIn implements ProductReaderPlugIn {
    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        String name = new File(input.toString()).getName();
        // All tiles --> mosaic on-the-fly
        if (name.endsWith(".xml")
                && (name.startsWith("MTD_GPPL1B_") || name.startsWith("MTD_GPPL1C_"))) {
            return DecodeQualification.INTENDED;
        }
        // Single tile
        if (name.endsWith(".jp2")
                && (name.startsWith("IMG_GPPL1B_") || name.startsWith("IMG_GPPL1C_"))) {
            return DecodeQualification.INTENDED;
        }
        return DecodeQualification.UNABLE;
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
        return new String[]{"SENTINEL-2-MSI"};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{".xml", ".jp2"};
    }

    @Override
    public String getDescription(Locale locale) {
        return "Sentinel-2 MSI Data Product";
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new BeamFileFilter(getFormatNames()[0],
                                  getDefaultFileExtensions()[0],
                                  getDescription(null));
    }
}
