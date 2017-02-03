package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class MG4LidarDriverProductReaderPlugInTest extends AbstractDriverProductReaderPlugInTest {

    public MG4LidarDriverProductReaderPlugInTest() {
        super(".view", "MG4Lidar", new MG4LidarDriverProductReaderPlugIn());
    }
}
