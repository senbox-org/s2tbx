package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class NITFDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public NITFDriverProductReaderPlugInTest() {
        super(".ntf", "NITF", new NITFDriverProductReaderPlugIn());
    }
}
