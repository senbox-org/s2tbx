package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class SAGADriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public SAGADriverProductReaderPlugInTest() {
        super(".sdat", "SAGA", new SAGADriverProductReaderPlugIn());
    }
}
