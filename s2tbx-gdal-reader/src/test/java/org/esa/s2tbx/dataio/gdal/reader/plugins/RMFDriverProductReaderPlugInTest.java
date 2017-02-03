package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class RMFDriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public RMFDriverProductReaderPlugInTest() {
        super(".rsw", "RMF", new RMFDriverProductReaderPlugIn());
    }
}
