package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class GSBGDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public GSBGDriverProductReaderPlugInTest() {
        super(".grd", "GSBG", new GSBGDriverProductReaderPlugIn());
    }
}
