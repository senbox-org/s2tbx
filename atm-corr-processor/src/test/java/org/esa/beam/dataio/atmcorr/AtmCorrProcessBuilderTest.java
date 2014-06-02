package org.esa.beam.dataio.atmcorr;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Tonio Fincke
 */
public class AtmCorrProcessBuilderTest {

    //this test will only work when the atmospheric correction processor is installed
    @Test
    @Ignore
    public void testAtmCorrProcessBuilder() throws Exception {
        AtmCorrProcessBuilder builder = new AtmCorrProcessBuilder();
        String l1cProductPath = "/home/tonio/S2L2APP/testdata/Level-1C_User_Product";
        int resolution = 60;
        builder.call(l1cProductPath, resolution, false);
    }

}
