package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class VRTDriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public VRTDriverProductReaderPlugInTest() {
        super(".vrt", "VRT", new VRTDriverProductReaderPlugIn());
    }
}
