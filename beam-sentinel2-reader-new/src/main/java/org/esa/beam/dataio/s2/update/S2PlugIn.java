package org.esa.beam.dataio.s2.update;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 *
 * @author Tonio fincke
 */
public class S2PlugIn implements ProductReaderPlugIn {

    private static final Class[] SUPPORTED_INPUT_TYPES = new Class[]{String.class, File.class};

    //todo use second versions
    final Pattern directory1CPattern = Pattern.compile("(S2.?_([A-Z]{4})_([A-Z]{3})_(L1C)_TL_.*_(\\d{2}[A-Z]{3})|Level-1C_User_Product)");
    final Pattern directory2APattern = Pattern.compile("(S2.?_([A-Z]{4})_([A-Z]{3})_(L2A)_TL_.*_(\\d{2}[A-Z]{3})|Level-2A_User_Product)");
    final static String metadataName1CRegex =
            "((S2.?)_([A-Z]{4})_MTD_(DMP|SAF)(L1C)_R([0-9]{3})_V([0-9]{8})T([0-9]{6})_([0-9]{8})T([0-9]{6})_C([0-9]{3}).*.xml|Product_Metadata_File.xml)";
    final static Pattern metadataName1CPattern = Pattern.compile(metadataName1CRegex);
//    final static Pattern metadataNamePattern = Pattern.compile("S2.?_([A-Z]{4})_MTD_(DMP|SAF)L(1C|2A)_.*.xml");
//    final static String metadataName1CRegex =
//        "(S2.?)_([A-Z]{4})_MTD_(DMP|SAF)(L1C)_R([0-9]{3})_V([0-9]{8})T([0-9]{6})_([0-9]{8})T([0-9]{6})_C([0-9]{3}).*.xml";
//    final static Pattern metadataName1CPattern = Pattern.compile(metadataName1CRegex);
    final static Pattern metadataName2APattern = Pattern.compile("S2.?_([A-Z]{4})_MTD_(DMP|SAF)(L2A)_.*.xml");
    final static Pattern metadataName1CTilePattern = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_L1C_TL_.*.xml");
    final static Pattern metadataName2ATilePattern = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_L2A_TL_.*.xml");
    private static final String FORMAT_NAME = "SENTINEL-2";
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
        if(directory1CPattern.matcher(name).matches()) {
            type = ProductType.L1C;
            return true;
        } else if(directory2APattern.matcher(name).matches()) {
            type = ProductType.L2A;
            return true;
        }
        return false;
    }

    private boolean isValidInputFileName(String name) {
        return metadataName1CPattern.matcher(name).matches() || metadataName2APattern.matcher(name).matches()  ||
                metadataName1CTilePattern.matcher(name).matches() || metadataName2ATilePattern.matcher(name).matches();
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
