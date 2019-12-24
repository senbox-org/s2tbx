package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.GSBGDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class GSBGDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public GSBGDriverProductReaderPlugInTest() {
        super(".grd", "GSBG", new GSBGDriverProductReaderPlugIn());
    }
}
