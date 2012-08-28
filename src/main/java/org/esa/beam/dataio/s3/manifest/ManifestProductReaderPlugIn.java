package org.esa.beam.dataio.s3.manifest;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

public abstract class ManifestProductReaderPlugIn implements ProductReaderPlugIn {

    private static final Class[] SUPPORTED_INPUT_TYPES = new Class[]{String.class, File.class};

    private final String formatName;
    private final String manifestFileBasename;
    private final String[] fileExtensions;
    private final Pattern directoryNamePattern;
    private final String description;
    private final String[] formatNames;

    protected ManifestProductReaderPlugIn(String formatName, String description, String directoryNamePattern,
                                          String manifestFileBasename, String... fileExtensions) {
        this.formatName = formatName;
        this.fileExtensions = fileExtensions;
        this.directoryNamePattern = Pattern.compile(directoryNamePattern);
        this.description = description;
        this.formatNames = new String[]{formatName};
        this.manifestFileBasename = manifestFileBasename;
    }

    @Override
    public final DecodeQualification getDecodeQualification(Object input) {
        if (isInputValid(input)) {
            return DecodeQualification.INTENDED;
        } else {
            return DecodeQualification.UNABLE;
        }
    }

    @Override
    public final Class[] getInputTypes() {
        return SUPPORTED_INPUT_TYPES;
    }

    @Override
    public final String[] getFormatNames() {
        return formatNames;
    }

    @Override
    public final String[] getDefaultFileExtensions() {
        return fileExtensions;
    }

    @Override
    public final String getDescription(Locale locale) {
        return description;
    }

    @Override
    public final BeamFileFilter getProductFileFilter() {
        return new BeamFileFilter(formatName, fileExtensions, description);
    }

    private boolean isValidInputFileName(String name) {
        for (final String fileExtension : fileExtensions) {
            final String manifestFileName = manifestFileBasename + fileExtension;
            if (manifestFileName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
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
}
