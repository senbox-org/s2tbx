package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.BTDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class BTDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public BTDriverProductReaderPlugInTest() {
        super(".bt", "BT", new BTDriverProductReaderPlugIn());
    }
}
