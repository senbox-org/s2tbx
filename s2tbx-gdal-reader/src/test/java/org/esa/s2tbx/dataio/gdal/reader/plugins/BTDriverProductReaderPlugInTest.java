package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class BTDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public BTDriverProductReaderPlugInTest() {
        super(".bt", "BT", new BTDriverProductReaderPlugIn());
    }
}
