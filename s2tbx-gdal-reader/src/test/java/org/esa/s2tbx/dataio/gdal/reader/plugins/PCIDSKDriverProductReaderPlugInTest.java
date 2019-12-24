package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.PCIDSKDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class PCIDSKDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public PCIDSKDriverProductReaderPlugInTest() {
        super(".pix", "PCIDSK", new PCIDSKDriverProductReaderPlugIn());
    }
}
