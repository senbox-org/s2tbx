package org.esa.beam.dataio.rapideye;

import org.esa.beam.dataio.ProductContentEnforcer;
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

    public static final ProductContentEnforcer enforcer = ProductContentEnforcer.create(RapidEyeConstants.L1_MINIMAL_PRODUCT_PATTERNS);

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification retVal = DecodeQualification.UNABLE;
        ZipVirtualDir virtualDir;
        try {
            virtualDir = getInput(input);
            if (virtualDir != null) {
                String[] allFiles = virtualDir.listAll();
                if (enforcer.isConsistent(allFiles)) {
                    retVal = DecodeQualification.INTENDED;
                }
            }
        } catch (IOException e) {
            retVal = DecodeQualification.UNABLE;
        }
        return retVal;
    }

    static ZipVirtualDir getInput(Object input) throws IOException {
        File inputFile = getFileInput(input);

        if (inputFile.isFile() && !ZipVirtualDir.isCompressedFile(inputFile)) {
            final File absoluteFile = inputFile.getAbsoluteFile();
            inputFile = absoluteFile.getParentFile();
            if (inputFile == null) {
                throw new IOException("Unable to retrieve parent to file: " + absoluteFile.getAbsolutePath());
            }
        }
        return new ZipVirtualDir(inputFile);
    }

    private static File getFileInput(Object input) {
        if (input instanceof String) {
            return new File((String) input);
        } else if (input instanceof File) {
            return (File) input;
        }
        return null;
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
