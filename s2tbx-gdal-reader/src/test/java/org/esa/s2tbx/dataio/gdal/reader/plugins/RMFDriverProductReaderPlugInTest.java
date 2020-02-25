package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.RMFDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class RMFDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public RMFDriverProductReaderPlugInTest() {
        super(".rsw", "RMF", new RMFDriverProductReaderPlugIn());
    }
}
