package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.SAGADriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class SAGADriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public SAGADriverProductReaderPlugInTest() {
        super(".sdat", "SAGA", new SAGADriverProductReaderPlugIn());
    }
}
