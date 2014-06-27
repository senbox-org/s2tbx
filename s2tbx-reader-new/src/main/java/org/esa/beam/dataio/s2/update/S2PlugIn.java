package org.esa.beam.dataio.s2.update;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.io.File;
import java.util.Locale;

/**
 *
 * @author Tonio fincke
 */
public class S2PlugIn implements ProductReaderPlugIn {

    private static final Class[] SUPPORTED_INPUT_TYPES = new Class[]{String.class, File.class};
    private static final String FORMAT_NAME = "SENTINEL-2-MSI";
    private final String[] fileExtensions = new String[]{".xml"};
    private final String description = "Sentinel-2 products";

    private ProductType type;

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        if (isInputValid(input)) {
            return DecodeQualification.INTENDED;
        } else {
            return DecodeQualification.UNABLE;
        }
    }

    private boolean isInputValid(Object input) {
        final File inputFile = new File(input.toString());
        final File parentFile = inputFile.getParentFile();
        return parentFile != null && isValidInputFileName(inputFile.getName()) && isValidDirectoryName(parentFile.getName());
    }

    private boolean isValidDirectoryName(String name) {
        if(S2Config.TILE_DIRECTORY_1C_PATTERN.matcher(name).matches() || S2Config.DIRECTORY_1C_PATTERN_ALT.matcher(name).matches()) {
            type = ProductType.L1C;
            return true;
        } else if(S2Config.TILE_DIRECTORY_2A_PATTERN.matcher(name).matches() || S2Config.DIRECTORY_2A_PATTERN_ALT.matcher(name).matches()) {
            type = ProductType.L2A;
            return true;
        }
        return false;
    }

    private boolean isValidInputFileName(String name) {
        return S2Config.METADATA_NAME_1C_PATTERN.matcher(name).matches() ||
                S2Config.METADATA_NAME_1C_PATTERN_ALT.matcher(name).matches() ||
                S2Config.METADATA_NAME_2A_PATTERN.matcher(name).matches()  ||
                S2Config.METADATA_NAME_1C_TILE_PATTERN.matcher(name).matches() ||
                S2Config.METADATA_NAME_2A_TILE_PATTERN.matcher(name).matches();
    }

    @Override
    public Class[] getInputTypes() {
        return SUPPORTED_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        switch (type) {
            case L1C:
                return new S2L1CProductReader(this);
            case L2A:
                return new S2L2AProductReader(this);
        }
        return null;
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{FORMAT_NAME};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return fileExtensions;
    }

    @Override
    public String getDescription(Locale locale) {
        return description;
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new BeamFileFilter(FORMAT_NAME, fileExtensions, description);
    }

}
