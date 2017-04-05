package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class RSTDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public RSTDriverProductReaderPlugInTest() {
        super(".rst", "RST", new RSTDriverProductReaderPlugIn());
    }
}
