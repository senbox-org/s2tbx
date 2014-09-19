package org.esa.beam.dataio.spot;

import org.esa.beam.dataio.ProductContentEnforcer;
import org.esa.beam.dataio.ZipVirtualDir;
import org.esa.beam.dataio.spot.dimap.SpotConstants;
import org.esa.beam.dataio.spot.internal.SpotVirtualDir;
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
 * Visat plugin for reading SPOT-4 and SPOT-5 view files which are not
 * in the "official" (DIMAP+GeoTIFF) format.
 *
 * @author  Cosmin Cara
 */
public class SpotViewProductReaderPlugin implements ProductReaderPlugIn {

    private static ProductContentEnforcer enforcer = ProductContentEnforcer.create(SpotConstants.SPOTVIEW_MINIMAL_PRODUCT_PATTERNS);

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

    @Override
    public Class[] getInputTypes() {
        return SpotConstants.SPOTVIEW_READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new SpotViewProductReader(this);
    }

    @Override
    public String[] getFormatNames() { return SpotConstants.SPOTVIEW_FORMAT_NAMES; }

    @Override
    public String[] getDefaultFileExtensions() {
        return SpotConstants.SPOTVIEW_DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return SpotConstants.SPOTVIEW_DESCRIPTION;
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new SpotViewFileFilter();
    }

    private static boolean isSpotViewFilename(String filename)
    {
        boolean isMatch = false;
        for (String pattern : SpotConstants.SPOTVIEW_FILENAME_PATTERNS) {
            isMatch = filename.matches(pattern);
            if (isMatch) break;
        }
        return isMatch;
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

    private static boolean isMetadataFile(File file)
    {
        return (file.getName().toLowerCase().endsWith(".xml"));
    }

    /**
     * Filter for SPOTView files that are not in DIMAP format
     */
    public static class SpotViewFileFilter extends BeamFileFilter {

        public SpotViewFileFilter() {
            super();
            setFormatName(SpotConstants.SPOTVIEW_FORMAT_NAMES[0]);
            setDescription(SpotConstants.SPOTVIEW_DESCRIPTION);
            setExtensions(SpotConstants.SPOTVIEW_DEFAULT_EXTENSIONS);
        }

        @Override
        public boolean accept(File file) {
            boolean shouldAccept = super.accept(file);
            if (file.isFile()) {
                for (String pattern : SpotConstants.SPOTVIEW_FILENAME_PATTERNS) {
                    shouldAccept = file.getName().matches(pattern);
                    if (shouldAccept) break;
                }
            }
            return shouldAccept;
        }
    }
}
