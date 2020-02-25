package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.HFADriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class HFADriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public HFADriverProductReaderPlugInTest() {
        super(".img", "HFA", new HFADriverProductReaderPlugIn());
    }
}
