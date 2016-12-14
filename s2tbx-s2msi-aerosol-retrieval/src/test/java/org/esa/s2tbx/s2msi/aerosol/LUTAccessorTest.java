package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.aerosol.lut.LUTAccessor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author olafd
 */
public class LUTAccessorTest {

    private LUTAccessor firstAccessor;
    private String firstLUTPath;

    private final String[] firstTargetNames =
            new String[]{"sza", "vza", "aza", "sal", "a_pig", "a_dg", "b_part", "b_part_ratio", "a_dg_ratio"};

    @Before
    public void setUp() throws IOException {
        final File firstLutFile =
                new File(LUTAccessor.class.getResource("../../../../../../sentinel-2a_lut_smsi_v0.6.memmap.d").getPath());
        firstLUTPath = firstLutFile.getAbsolutePath();
        firstAccessor = new LUTAccessor(firstLutFile);
    }

    @Test
    public void test_InitWithDescriptionFile() throws IOException {
        final File descriptionFile =
                new File(LUTAccessor.class.getResource("../../../../../../sentinel-2a_lut_smsi_v0.6.dims.jsn").getPath());
        final LUTAccessor accessor = new LUTAccessor(descriptionFile);
        assertEquals(9, accessor.getNumberOfNonSpectralProperties());
        assertEquals(9, accessor.getNumberOfNonSpectralProperties());
        assertArrayEquals(firstTargetNames, accessor.getTargetNames());
    }

}
