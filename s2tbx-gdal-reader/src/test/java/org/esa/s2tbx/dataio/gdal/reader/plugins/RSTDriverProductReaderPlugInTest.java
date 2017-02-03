package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class RSTDriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public RSTDriverProductReaderPlugInTest() {
        super(".rst", "RST", new RSTDriverProductReaderPlugIn());
    }
}
