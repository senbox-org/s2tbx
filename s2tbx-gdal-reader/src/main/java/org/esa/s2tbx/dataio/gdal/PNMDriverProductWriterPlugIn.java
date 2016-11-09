
package org.esa.s2tbx.dataio.gdal;

import java.util.Locale;

/**
 * @author Jean Coravu
 */
public class PNMDriverProductWriterPlugIn extends AbstractGDALProductWriterPlugIn {
    public static final String[] FORMAT_NAMES = new String[] { "GDAL-PNM-DRIVER" };

    public PNMDriverProductWriterPlugIn() {
        super("PNM");
    }

    @Override
    public String getDescription(Locale locale) {
        return "Portable Pixmap Format (netpbm)";
    }

    @Override
    public String[] getFormatNames() {
        return FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[] {".ppm", ".pgm"};
    }
}
