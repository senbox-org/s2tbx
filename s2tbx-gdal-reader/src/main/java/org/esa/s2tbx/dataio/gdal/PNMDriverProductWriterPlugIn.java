
package org.esa.s2tbx.dataio.gdal;

import java.util.Locale;

/**
 * @author Jean Coravu
 */
public class PNMDriverProductWriterPlugIn extends AbstractGDALProductWriterPlugIn {

    public PNMDriverProductWriterPlugIn() {
        super("PNM");
    }

    @Override
    public String getDescription(Locale locale) {
        return "Portable Pixmap Format (netpbm)";
    }

    @Override
    public String[] getFormatNames() {
        return new String[] {"GDAL-NITF-PNM"};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[] {".ppm", ".pgm"};
    }
}
