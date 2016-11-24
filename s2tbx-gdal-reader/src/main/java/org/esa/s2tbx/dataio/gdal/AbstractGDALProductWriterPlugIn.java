package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.dataio.EncodeQualification;
import org.esa.snap.core.dataio.ProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.gdal.gdal.gdal;

import java.io.File;

/**
 * @author Jean Coravu
 */
public abstract class AbstractGDALProductWriterPlugIn implements ProductWriterPlugIn {
    private final SnapFileFilter fileFilter;
    private final String driverName;

    static {
        if (GdalInstallInfo.isPresent())
            gdal.AllRegister(); // GDAL init drivers
    }

    protected AbstractGDALProductWriterPlugIn(String driverName) {
        this.driverName = driverName;
        this.fileFilter = new SnapFileFilter(getFormatNames()[0], getDefaultFileExtensions(), getDescription(null));
    }

    @Override
    public final SnapFileFilter getProductFileFilter() {
        return this.fileFilter;
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
    public final ProductWriter createWriterInstance() {
        return new GDALProductWriter(this, this.driverName);
    }
}
