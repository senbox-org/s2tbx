package org.esa.beam.dataio.spot;

import org.esa.beam.dataio.ZipVirtualDir;
import org.esa.beam.dataio.spot.dimap.SpotConstants;
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

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        String fileName = new File(input.toString()).getName();
        if (!isSpotViewFilename(fileName)) {
            return DecodeQualification.UNABLE;
        }

        ZipVirtualDir virtualDir;
        try {
            virtualDir = getInput(input);
        } catch (IOException e) {
            return DecodeQualification.UNABLE;
        }

        if (virtualDir == null) {
            return DecodeQualification.UNABLE;
        }

        String[] list;
        try {
            list = virtualDir.list("");
            if (list == null || list.length == 0) {
                return DecodeQualification.UNABLE;
            }
        } catch (IOException e) {
            return DecodeQualification.UNABLE;
        }

        for (String fName : list) {
            try {
                File file = virtualDir.getFile(fName);
                if (isMetadataFile(file)) {
                    return DecodeQualification.INTENDED;
                }
            } catch (IOException ignore) {
                // file is broken, but be tolerant here
            }
        }
        // didn't find the expected metadata file
        return DecodeQualification.UNABLE;

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

        if (inputFile.isFile() && !isCompressedFile(inputFile)) {
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

    private static boolean isCompressedFile(File file) {
        String extension = FileUtils.getExtension(file);
        if (StringUtils.isNullOrEmpty(extension)) {
            return false;
        }

        extension = extension.toLowerCase();

        return extension.contains("zip")
                || extension.contains("tar")
                || extension.contains("tgz")
                || extension.contains("gz");
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
