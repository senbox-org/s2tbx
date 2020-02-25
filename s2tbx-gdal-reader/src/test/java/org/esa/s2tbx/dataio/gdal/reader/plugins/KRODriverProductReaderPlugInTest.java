package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.KRODriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class KRODriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public KRODriverProductReaderPlugInTest() {
        super(".kro", "KRO", new KRODriverProductReaderPlugIn());
    }
}
