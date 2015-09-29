package org.esa.s2tbx.dataio.jp2;

import org.esa.s2tbx.dataio.jp2.internal.JP2ProductReaderConstants;
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
public class JP2ProductReaderPlugin implements ProductReaderPlugIn {
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
                if (".jp2".equalsIgnoreCase(ext)) {
                    result = DecodeQualification.SUITABLE;
                }
            }
        }
        return result;
    }

    @Override
    public Class[] getInputTypes() {
        return JP2ProductReaderConstants.INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new JP2ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return JP2ProductReaderConstants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return JP2ProductReaderConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return JP2ProductReaderConstants.DESCRIPTION;
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(getFormatNames()[0], getDefaultFileExtensions()[0], JP2ProductReaderConstants.DESCRIPTION);
    }
}
