
package org.esa.s2tbx.dataio.gdal;

import java.util.Locale;

/**
 * @author Jean Coravu
 */
public class RMFDriverProductWriterPlugIn extends AbstractGDALProductWriterPlugIn {
    public static final String[] FORMAT_NAMES = new String[] { "GDAL-RMF-DRIVER" };

    public RMFDriverProductWriterPlugIn() {
        super("RMF");
    }

    @Override
    public String getDescription(Locale locale) {
        return "Raster Matrix Format";
    }

    @Override
    public String[] getFormatNames() {
        return FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[] {".rmf"};
    }
}
