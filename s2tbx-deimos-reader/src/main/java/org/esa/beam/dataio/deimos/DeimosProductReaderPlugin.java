package org.esa.beam.dataio.deimos;

import org.esa.beam.dataio.ProductContentEnforcer;
import org.esa.beam.dataio.ZipVirtualDir;
import org.esa.beam.dataio.deimos.dimap.DeimosConstants;
import org.esa.beam.dataio.deimos.internal.DeimosVirtualDir;
import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by kraftek on 9/22/2014.
 */
public class DeimosProductReaderPlugin implements ProductReaderPlugIn {
    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification retVal = DecodeQualification.UNABLE;
        ZipVirtualDir virtualDir;
        try {
            virtualDir = getInput(input);
            if (virtualDir != null) {
                String[] allFiles = virtualDir.listAll();
                ProductContentEnforcer enforcer = ProductContentEnforcer.create(DeimosConstants.MINIMAL_PRODUCT_PATTERNS);
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
        return DeimosConstants.DIMAP_READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new DeimosProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return DeimosConstants.DIMAP_FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return DeimosConstants.DIMAP_DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return DeimosConstants.DIMAP_DESCRIPTION;
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new DeimosFileFilter();
    }

    public static class DeimosFileFilter extends BeamFileFilter {

        public DeimosFileFilter() {
            super();
            setFormatName(DeimosConstants.DIMAP_FORMAT_NAMES[0]);
            setDescription(DeimosConstants.DIMAP_DESCRIPTION);
            setExtensions(DeimosConstants.DIMAP_DEFAULT_EXTENSIONS);
        }

        @Override
        public boolean accept(File file) {
            boolean shouldAccept = super.accept(file);
            if (file.isFile()) {
                for (String pattern : DeimosConstants.FILENAME_PATTERNS) {
                    shouldAccept = file.getName().matches(pattern);
                    if (shouldAccept) break;
                }
            }
            return shouldAccept;
        }
    }

    static DeimosVirtualDir getInput(Object input) throws IOException {
        File inputFile = getFileInput(input);

        if (inputFile.isFile() && !ZipVirtualDir.isCompressedFile(inputFile)) {
            final File absoluteFile = inputFile.getAbsoluteFile();
            inputFile = absoluteFile.getParentFile();
            if (inputFile == null) {
                throw new IOException("Unable to retrieve parent to file: " + absoluteFile.getAbsolutePath());
            }
        }
        return new DeimosVirtualDir(inputFile);
    }

    private static File getFileInput(Object input) {
        if (input instanceof String) {
            return new File((String) input);
        } else if (input instanceof File) {
            return (File) input;
        }
        return null;
    }
}
