package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class KRODriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public KRODriverProductReaderPlugInTest() {
        super(".kro", "KRO", new KRODriverProductReaderPlugIn());
    }
}
