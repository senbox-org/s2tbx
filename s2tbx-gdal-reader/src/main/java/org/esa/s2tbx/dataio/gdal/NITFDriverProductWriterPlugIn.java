
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
public class NITFDriverProductWriterPlugIn extends AbstractGDALProductWriterPlugIn {
    public static final String[] FORMAT_NAMES = new String[] { "GDAL-NITF-DRIVER" };

    public NITFDriverProductWriterPlugIn() {
        super("NITF");
    }

    @Override
    public String getDescription(Locale locale) {
        return "National Imagery Transmission Format";
    }

    @Override
    public String[] getFormatNames() {
        return FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[] {".ntf"};
    }
}
