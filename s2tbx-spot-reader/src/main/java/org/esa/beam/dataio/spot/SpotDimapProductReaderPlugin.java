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
 * Visat plugin for reading SPOT-1 to SPOT-5 scene files.
 * The scene files are GeoTIFF with DIMAP metadata.
 * @author Cosmin Cara
 */
public class SpotDimapProductReaderPlugin implements ProductReaderPlugIn {

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification retVal = DecodeQualification.UNABLE;
        SpotVirtualDir virtualDir;
        try {
            virtualDir = getInput(input);
            if (virtualDir != null) {
                String[] allFiles = virtualDir.listAll();
                ProductContentEnforcer enforcer = ProductContentEnforcer.create(SpotConstants.DIMAP_MINIMAL_PRODUCT_PATTERNS);
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
        return SpotConstants.DIMAP_READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new SpotDimapProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return SpotConstants.DIMAP_FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return SpotConstants.DIMAP_DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return SpotConstants.DIMAP_DESCRIPTION;
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new SpotDimapFileFilter();
    }

    private static boolean isDimapFilename(String filename)
    {
        boolean isMatch = false;
        for (String pattern : SpotConstants.DIMAP_FILENAME_PATTERNS) {
            isMatch = filename.matches(pattern);
            if (isMatch) break;
        }
        return isMatch;
    }

    static SpotVirtualDir getInput(Object input) throws IOException {
        File inputFile = getFileInput(input);

        if (inputFile.isFile() && !ZipVirtualDir.isCompressedFile(inputFile)) {
            final File absoluteFile = inputFile.getAbsoluteFile();
            inputFile = absoluteFile.getParentFile();
            if (inputFile == null) {
                throw new IOException("Unable to retrieve parent to file: " + absoluteFile.getAbsolutePath());
            }
        }
        return new SpotVirtualDir(inputFile);
    }

    private static File getFileInput(Object input) {
        if (input instanceof String) {
            return new File((String) input);
        } else if (input instanceof File) {
            return (File) input;
        }
        return null;
    }

    private static boolean isMetadataFile(String file)
    {
        return (file.toLowerCase().endsWith(".dim"));
    }

    /**
     * Filter for SPOT Dimap files
     */
    public static class SpotDimapFileFilter extends BeamFileFilter {

        public SpotDimapFileFilter() {
            super();
            setFormatName(SpotConstants.DIMAP_FORMAT_NAMES[0]);
            setDescription(SpotConstants.DIMAP_DESCRIPTION);
            setExtensions(SpotConstants.DIMAP_DEFAULT_EXTENSIONS);
        }

        @Override
        public boolean accept(File file) {
            boolean shouldAccept = super.accept(file);
            if (file.isFile()) {
                for (String pattern : SpotConstants.DIMAP_FILENAME_PATTERNS) {
                    shouldAccept = file.getName().matches(pattern);
                    if (shouldAccept) break;
                }
            }
            return shouldAccept;
        }
    }
}
