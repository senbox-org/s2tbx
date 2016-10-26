package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;

import java.io.File;
import java.util.Locale;

/**
 * @author Jean Coravu
 */
public class GDALProductReaderPlugin implements ProductReaderPlugIn {
    private static final Class[] INPUT_TYPES = new Class[] { String.class, File.class };
    private static final String[] FORMAT_NAMES = new String[] { "GDAL" };
    private static final String[] DEFAULT_EXTENSIONS = new String[] { ".jp2" };
    private static final String DESCRIPTION = "GDAL Files";

    static {
        gdal.AllRegister(); // GDAL init drivers
    }

    public GDALProductReaderPlugin() {
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        return DecodeQualification.SUITABLE;
    }

    @Override
    public Class[] getInputTypes() {
        return INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new GDALProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return DESCRIPTION;
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(getFormatNames()[0], getDefaultFileExtensions()[0], DESCRIPTION);
    }
}
