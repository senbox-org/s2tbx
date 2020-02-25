package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.RSTDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class RSTDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public RSTDriverProductReaderPlugInTest() {
        super(".rst", "RST", new RSTDriverProductReaderPlugIn());
    }
}
