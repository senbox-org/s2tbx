package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class PNMDriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public PNMDriverProductReaderPlugInTest() {
        super(".pnm", "PNM", new PNMDriverProductReaderPlugIn());
    }
}
