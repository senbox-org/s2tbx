package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.GTXDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class GTXDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public GTXDriverProductReaderPlugInTest() {
        super(".gtx", "GTX", new GTXDriverProductReaderPlugIn());
    }
}
