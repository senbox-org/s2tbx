package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.SGIDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class SGIDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public SGIDriverProductReaderPlugInTest() {
        super(".rgb", "SGI", new SGIDriverProductReaderPlugIn());
    }
}
