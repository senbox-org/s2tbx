package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class AVCE00DriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public AVCE00DriverProductReaderPlugInTest() {
        super(".e00", "AVCE00", new AVCE00DriverProductReaderPlugIn());
    }
}
