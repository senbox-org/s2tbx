package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class HFADriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public HFADriverProductReaderPlugInTest() {
        super(".img", "HFA", new HFADriverProductReaderPlugIn());
    }
}
