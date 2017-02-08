package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class MFFDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public MFFDriverProductReaderPlugInTest() {
        super(".hdr", "MFF", new MFFDriverProductReaderPlugIn());
    }
}
