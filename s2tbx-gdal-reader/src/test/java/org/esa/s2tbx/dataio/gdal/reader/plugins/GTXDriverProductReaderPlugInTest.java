package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class GTXDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public GTXDriverProductReaderPlugInTest() {
        super(".gtx", "GTX", new GTXDriverProductReaderPlugIn());
    }
}
