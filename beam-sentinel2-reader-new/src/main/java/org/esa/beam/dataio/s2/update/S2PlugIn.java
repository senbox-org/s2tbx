package org.esa.beam.dataio.s2.update;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: tonio
 * Date: 08.07.13
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class S2PlugIn implements ProductReaderPlugIn {

    private static final Class[] SUPPORTED_INPUT_TYPES = new Class[]{String.class, File.class};
    final Pattern directoryNamePattern = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_(L1C|L2A)_TL_.*_(\\d{2}[A-Z]{3})");
    final static Pattern metadataNamePattern = Pattern.compile("S2.?_([A-Z]{4})_MTD_(DMP|SAF)L(1C|2A)_.*.xml");
    final static Pattern metadataName1CTilePattern = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_L1C_TL_.*.xml");
    final static Pattern metadataName2ATilePattern = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_L2A_TL_.*.xml");
    private static final String FORMAT_NAME = "SENTINEL-2";
    private final String[] fileExtensions = new String[]{".xml"};
    private final String description = "Sentinel-2 products";

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
        return parentFile != null && isValidInputFileName(inputFile.getName()) && isValidDirectoryName(
                parentFile.getName());
    }

    private boolean isValidDirectoryName(String name) {
        return directoryNamePattern.matcher(name).matches();
    }

    private boolean isValidInputFileName(String name) {
        return metadataNamePattern.matcher(name).matches() || metadataName1CTilePattern.matcher(name).matches() ||
                metadataName2ATilePattern.matcher(name).matches();
    }

    @Override
    public Class[] getInputTypes() {
        return SUPPORTED_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new S2ProductReader(this);
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
