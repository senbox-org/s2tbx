package org.esa.s2tbx.dataio.j2k;

import org.esa.s2tbx.dataio.j2k.internal.J2KProductReaderConstants;
import org.esa.snap.framework.dataio.DecodeQualification;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.util.io.FileUtils;
import org.esa.snap.util.io.SnapFileFilter;

import java.io.File;
import java.util.Locale;

/**
 * Plugin for reading JP2 files.
 *
 * @author Cosmin Cara
 */
public class J2kProductReaderPlugin implements ProductReaderPlugIn {
    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification result = DecodeQualification.UNABLE;
        if (input != null) {
            File fileInput = null;
            if (input instanceof String) {
                fileInput = new File((String) input);
            } else if (input instanceof File) {
                fileInput = (File) input;
            }
            if (fileInput != null) {
                final String ext = FileUtils.getExtension(fileInput);
                if (ext.equalsIgnoreCase(".jp2")) {
                    result = DecodeQualification.SUITABLE;
                }
            }
        }
        return result;
    }

    @Override
    public Class[] getInputTypes() {
        return J2KProductReaderConstants.INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new J2KProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return J2KProductReaderConstants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return J2KProductReaderConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return J2KProductReaderConstants.DESCRIPTION;
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(getFormatNames()[0], getDefaultFileExtensions()[0], J2KProductReaderConstants.DESCRIPTION);
    }
}
