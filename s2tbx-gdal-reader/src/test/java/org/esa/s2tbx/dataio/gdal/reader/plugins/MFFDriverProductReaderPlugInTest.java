package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.MFFDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class MFFDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public MFFDriverProductReaderPlugInTest() {
        super(".hdr", "MFF", new MFFDriverProductReaderPlugIn());
    }
}
