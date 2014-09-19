package org.esa.beam.dataio.spot;

import com.bc.ceres.core.VirtualDir;
import org.esa.beam.dataio.ProductContentEnforcer;
import org.esa.beam.dataio.TarVirtualDir;
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
 * Visat plugin for reading SPOT4 TAKE5 scene files.
 * The scene files are GeoTIFF with XML metadata.
 * @author Ramona Manda
 */
public class SpotTake5ProductReaderPlugin implements ProductReaderPlugIn {


    private static boolean isSpotTake5Filename(String filename) {
        boolean isMatch = false;
        for (String pattern : SpotConstants.SPOT4_TAKE5_FILENAME_PATTERNS) {
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

        ZipVirtualDir virtualDir = new ZipVirtualDir(inputFile);
        return virtualDir;
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
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification retVal = DecodeQualification.UNABLE;
        ZipVirtualDir virtualDir;
        try {
            virtualDir = getInput(input);
            if (virtualDir != null) {
                String[] allFiles = virtualDir.listAll();
                ProductContentEnforcer enforcer = ProductContentEnforcer.create(SpotConstants.SPOTTAKE5_MINIMAL_PRODUCT_PATTERNS);
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
    public ProductReader createReaderInstance() {
        return new SpotTake5ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return SpotConstants.SPOT4_TAKE5_FORMAT_NAME;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return SpotConstants.SPOT4_TAKE5_DEFAULT_EXTENSION;
    }

    @Override
    public String getDescription(Locale locale) {
        return SpotConstants.SPOT4_TAKE5_DESCRIPTION;
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new BeamFileFilter(SpotConstants.SPOT4_TAKE5_FORMAT_NAME[0],
                SpotConstants.SPOT4_TAKE5_DEFAULT_EXTENSION,
                SpotConstants.SPOT4_TAKE5_DESCRIPTION);
    }

    @Override
    public Class[] getInputTypes() {
        return SpotConstants.SPOT4_TAKE5_READER_INPUT_TYPES;
    }

}
