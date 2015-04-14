package org.esa.s2tbx.dataio.refl2rad;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Tonio Fincke
 */
public class Refl2RadProcessBuilderTest {

    //this test will only work when the atmospheric correction processor is installed
    @Test
    @Ignore
    public void testRefl2RadProcessBuilder() throws Exception {
        Refl2RadProcessBuilder builder = new Refl2RadProcessBuilder();
        String l1cProductPath = "/home/tonio/S2L2APP/testdata/Level-1C_User_Product";
        int resolution = 60;
        Refl2RadProcessBuilder.call(l1cProductPath, resolution, false);
    }

}
