package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class PCIDSKDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public PCIDSKDriverProductReaderPlugInTest() {
        super(".pix", "PCIDSK", new PCIDSKDriverProductReaderPlugIn());
    }
}
