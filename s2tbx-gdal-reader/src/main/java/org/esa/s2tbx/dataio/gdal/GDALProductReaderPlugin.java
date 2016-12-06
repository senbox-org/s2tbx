package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.gdal.gdal.gdal;

import java.io.File;
import java.util.Locale;

/**
 * @author Jean Coravu
 */
public class GDALProductReaderPlugin implements ProductReaderPlugIn {
    private static final Class[] INPUT_TYPES = new Class[] { String.class, File.class };
    public static final String FORMAT_NAME = "GDAL-READER";
    private static final String[] DEFAULT_EXTENSIONS = new String[] { ".*" };
    private static final String DESCRIPTION = "Raster Files";

    static {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            gdal.AllRegister(); // GDAL init drivers
        }
    }

    public GDALProductReaderPlugin() {
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            return DecodeQualification.SUITABLE;
        }
        return DecodeQualification.UNABLE;
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
        String[] formatNames = GdalInstallInfo.INSTANCE.getFormatNames();
        return formatNames != null ? formatNames :  new String[] { FORMAT_NAME };
    }

    @Override
    public String[] getDefaultFileExtensions() {
        String[] extensions = GdalInstallInfo.INSTANCE.getExtensions();
        return extensions != null ? extensions : DEFAULT_EXTENSIONS;
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
