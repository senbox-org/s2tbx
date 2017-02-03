package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class KEADriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public KEADriverProductReaderPlugInTest() {
        super(".kea", "KEA", new KEADriverProductReaderPlugIn());
    }
}
