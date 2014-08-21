package org.esa.beam.dataio.rapideye;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.io.File;
import java.util.Locale;

/**
 * Reader plugin class for RapidEye L3 products.
 * RE L3 products have a GeoTIFF raster.
 */
public class RapidEyeL3ReaderPlugin implements ProductReaderPlugIn {

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification qualification = DecodeQualification.UNABLE;
        File file = new File(input.toString());
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(RapidEyeConstants.METADATA_FILE_SUFFIX)) {
            File folder = file.getParentFile();
            File[] files = folder.listFiles();
            if (files != null) {
                boolean consistentProduct = true;
                for (String namePattern : RapidEyeConstants.L3_FILENAME_PATTERNS) {
                    boolean patternMatched = false;
                    for (File f : files) {
                        patternMatched |= f.getName().matches(namePattern);
                    }
                    consistentProduct &= patternMatched;
                }
                if (consistentProduct)
                    qualification = DecodeQualification.INTENDED;
            }
        }
        return qualification;
    }

    @Override
    public Class[] getInputTypes() {
        return RapidEyeConstants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new RapidEyeL3Reader(this);
    }

    @Override
    public String[] getFormatNames() {
        return RapidEyeConstants.L3_FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return RapidEyeConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return RapidEyeConstants.L3_DESCRIPTION;
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        //return new BeamFileFilter(RapidEyeConstants.L3_FORMAT_NAMES[0], RapidEyeConstants.DEFAULT_EXTENSIONS[0], RapidEyeConstants.L3_DESCRIPTION);
        return new RapidEyeL3Filter();
    }

    /**
     * Filter for RapidEye L3 product files
     */
    public static class RapidEyeL3Filter extends BeamFileFilter {

        public RapidEyeL3Filter() {
            super();
            setFormatName(RapidEyeConstants.L3_FORMAT_NAMES[0]);
            setDescription(RapidEyeConstants.L3_DESCRIPTION);
            setExtensions(RapidEyeConstants.DEFAULT_EXTENSIONS);
        }

        @Override
        public boolean accept(File file) {
            boolean shouldAccept = super.accept(file);
            if (file.isFile()) {
                String lcName = file.getName().toLowerCase();
                for (String pattern : RapidEyeConstants.L1_FILENAME_PATTERNS) {
                    shouldAccept = lcName.matches(pattern) && (lcName.endsWith(RapidEyeConstants.METADATA_FILE_SUFFIX) || lcName.endsWith(".zip"));
                    if (shouldAccept) break;
                }
            }
            return shouldAccept;
        }
    }
}
