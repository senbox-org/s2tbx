package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class BMPDriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public BMPDriverProductReaderPlugInTest() {
        super(".bmp", "BMP", new BMPDriverProductReaderPlugIn());
    }
}
