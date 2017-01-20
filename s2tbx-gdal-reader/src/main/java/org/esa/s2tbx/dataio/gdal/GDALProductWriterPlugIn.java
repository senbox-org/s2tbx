package org.esa.s2tbx.dataio.gdal;

import org.esa.s2tbx.dataio.gdal.activator.GDALDriverInfo;
import org.esa.snap.core.dataio.EncodeQualification;
import org.esa.snap.core.dataio.ProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.gdal.gdal.gdal;

import java.io.File;
import java.util.Locale;

/**
 * Writer plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class GDALProductWriterPlugIn implements ProductWriterPlugIn {
    public static final String FORMAT_NAME = "GDAL-WRITER";

    private final GDALDriverInfo[] writerDrivers;

    public GDALProductWriterPlugIn(GDALDriverInfo[] writerDrivers) {
        this.writerDrivers = writerDrivers;
    }

    @Override
    public String getDescription(Locale locale) {
        return null;
    }

    @Override
    public String[] getFormatNames() {
        return new String[] { FORMAT_NAME };
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return null;
    }

    @Override
    public final SnapFileFilter getProductFileFilter() {
        return null;
    }

    @Override
    public final EncodeQualification getEncodeQualification(Product product) {
        return new EncodeQualification(EncodeQualification.Preservation.FULL);
    }

    @Override
    public final Class[] getOutputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public ProductWriter createWriterInstance() {
        return new GDALProductWriter(this, this.writerDrivers);
    }

    public GDALDriverInfo[] getWriterDrivers() {
        return this.writerDrivers;
    }
}
