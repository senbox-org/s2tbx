package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.dataio.EncodeQualification;
import org.esa.snap.core.dataio.ProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.SnapFileFilter;

import java.io.File;
import java.util.Locale;

/**
 * @author Jean Coravu
 */
public class GDALProductWriterPlugIn implements ProductWriterPlugIn {

    public GDALProductWriterPlugIn() {
    }

    @Override
    public String[] getFormatNames() {
        return new String[] {"GDAL-NITF"};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[] {".ntf"};
    }

    @Override
    public String getDescription(Locale locale) {
        return "GDAL product writer";
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return null;
    }

    @Override
    public EncodeQualification getEncodeQualification(Product product) {
        return new EncodeQualification(EncodeQualification.Preservation.PARTIAL);
    }

    @Override
    public Class[] getOutputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public ProductWriter createWriterInstance() {
        return new GDALProductWriter(this);
    }
}
