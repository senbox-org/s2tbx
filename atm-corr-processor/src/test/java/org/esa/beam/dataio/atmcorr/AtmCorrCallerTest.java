package org.esa.beam.dataio.atmcorr;

import org.junit.Test;

/**
 * @author Tonio Fincke
 */
public class AtmCorrCallerTest {

    @Test
    public void testAtmCorrCaller() throws Exception {
        AtmCorrCaller caller = new AtmCorrCaller();
        String l1cProductPath = "/home/tonio/S2L2APP/testdata/Level-1C_User_Product";
        int resolution = 60;
        caller.call(l1cProductPath, resolution, false, false);
    }

}
