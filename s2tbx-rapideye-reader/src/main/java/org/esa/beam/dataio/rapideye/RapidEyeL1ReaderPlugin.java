package org.esa.beam.dataio.rapideye;

import org.esa.beam.dataio.ZipVirtualDir;
import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.StringUtils;
import org.esa.beam.util.io.BeamFileFilter;
import org.esa.beam.util.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Reader plugin class for RapidEye L1 products.
 * RE L1 products have rasters in NITF format.
 *
 * @author  Cosmin Cara
 */
public class RapidEyeL1ReaderPlugin implements ProductReaderPlugIn {

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification qualification = DecodeQualification.UNABLE;
        File file = new File(input.toString());
        String fileName = file.getName().toLowerCase();
        String[] files = null;
        if (fileName.endsWith(".zip")) {
            try {
                files = RapidEyeReader.getInput(input).list(".");
            } catch (IOException e) {//if the content files cannot be listed, the plugin will return unable!
            }
        } else if (fileName.endsWith(RapidEyeConstants.METADATA_FILE_SUFFIX)) {
            File folder = file.getParentFile();
            files = folder.list();
        }
        if (files != null) {
            boolean consistentProduct = true;
            for (String namePattern : RapidEyeConstants.L1_FILENAME_PATTERNS) {
                if (!namePattern.endsWith("zip")) {
                    boolean patternMatched = false;
                    for (String f : files) {
                        patternMatched |= f.matches(namePattern);
                    }
                    consistentProduct &= patternMatched;
                }
            }
            if (consistentProduct)
                qualification = DecodeQualification.INTENDED;
        }
        return qualification;
    }

    @Override
    public Class[] getInputTypes() { return RapidEyeConstants.READER_INPUT_TYPES; }

    @Override
    public ProductReader createReaderInstance() {
        return new RapidEyeL1Reader(this);
    }

    @Override
    public String[] getFormatNames() { return RapidEyeConstants.L1_FORMAT_NAMES; }

    @Override
    public String[] getDefaultFileExtensions() { return RapidEyeConstants.DEFAULT_EXTENSIONS; }

    @Override
    public String getDescription(Locale locale) { return RapidEyeConstants.L1_DESCRIPTION; }

    @Override
    public BeamFileFilter getProductFileFilter() {
        //return new BeamFileFilter(RapidEyeConstants.L1_FORMAT_NAMES[0], RapidEyeConstants.DEFAULT_EXTENSIONS[0], RapidEyeConstants.L1_DESCRIPTION);
        return new RapidEyeL1Filter();
    }

    /**
     * Filter for RapidEye L1 product files
     */
    public static class RapidEyeL1Filter extends BeamFileFilter {

        public RapidEyeL1Filter() {
            super();
            setFormatName(RapidEyeConstants.L1_FORMAT_NAMES[0]);
            setDescription(RapidEyeConstants.L1_DESCRIPTION);
            setExtensions(RapidEyeConstants.DEFAULT_EXTENSIONS);
        }

        @Override
        public boolean accept(File file) {
            boolean shouldAccept = super.accept(file);
            if (file.isFile() && !file.getName().endsWith(".zip")) {
                File folder = file.getParentFile();
                String[] list = folder.list();
                boolean consistent = true;
                for (String pattern : RapidEyeConstants.L1_FILENAME_PATTERNS) {
                    for (String fName : list) {
                        String lcName = fName.toLowerCase();
                        if (!pattern.endsWith("zip"))
                            shouldAccept = lcName.matches(pattern);
                        if (shouldAccept) break;
                    }
                    consistent &= shouldAccept;
                }
                shouldAccept = consistent;
            }
            return shouldAccept;
        }
    }
}
