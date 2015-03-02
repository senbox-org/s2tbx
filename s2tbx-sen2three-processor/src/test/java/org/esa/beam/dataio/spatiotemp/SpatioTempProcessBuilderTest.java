package org.esa.beam.dataio.spatiotemp;

import org.esa.beam.dataio.spatiotemp.SpatioTempProcessBuilder;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Tonio Fincke
 */
public class SpatioTempProcessBuilderTest {

    //this test will only work when the spatio temporal synthesis processor is installed
    @Test
    @Ignore
    public void testSpatioTempProcessBuilder() throws Exception {
        SpatioTempProcessBuilder builder = new SpatioTempProcessBuilder();
        String l1cProductPath = "/home/tonio/S2L3APP/testdata/Level-1C_User_Product";
        int resolution = 60;
        builder.call(l1cProductPath, resolution, false);
    }

}
