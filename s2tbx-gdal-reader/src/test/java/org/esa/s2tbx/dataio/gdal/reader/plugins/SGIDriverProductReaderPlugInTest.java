package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class SGIDriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public SGIDriverProductReaderPlugInTest() {
        super(".rgb", "SGI", new SGIDriverProductReaderPlugIn());
    }
}
