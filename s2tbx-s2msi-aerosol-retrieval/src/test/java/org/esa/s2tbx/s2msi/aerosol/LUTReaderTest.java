package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.aerosol.lut.LUTAccessor;
import org.esa.s2tbx.s2msi.aerosol.lut.LUTReader;
import org.junit.Before;

import java.io.File;
import java.io.IOException;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 16.12.2016
 * Time: 15:08
 *
 * @author olafd
 */
public class LUTReaderTest {

    private LUTReader lutReader;
    private LUTAccessor lutAccessor;

    @Before
    public void setUp() throws IOException {
        final File lutFile = new File(LUTReaderTest.class.getResource("sentinel-2a_lut_smsi_v0.6.memmap.d").getPath());
        lutAccessor = new LUTAccessor(lutFile);
        lutReader = new LUTReader(lutAccessor);
    }

}
