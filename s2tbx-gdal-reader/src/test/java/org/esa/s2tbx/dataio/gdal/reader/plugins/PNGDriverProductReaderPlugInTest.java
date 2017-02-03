package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class PNGDriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public PNGDriverProductReaderPlugInTest() {
        super(".png", "PNG", new PNGDriverProductReaderPlugIn());
    }
}
