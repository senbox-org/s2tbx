package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.aerosol.lut.LutAccessor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 * @author olafd
 */
@Ignore("Produces NPE in setup")
public class LutAccessorTest {

    private LutAccessor firstAccessor;
    private String firstLUTPath;

    private final String[] firstTargetNames =
            new String[]{"sza", "vza", "aza", "sal", "a_pig", "a_dg", "b_part", "b_part_ratio", "a_dg_ratio"};

    @Before
    public void setUp() throws IOException {
        final File firstLutFile =
                new File(LutAccessor.class.getResource("../../../../../../sentinel-2a_lut_smsi_v0.6.memmap.d").getPath());
        firstLUTPath = firstLutFile.getAbsolutePath();
        firstAccessor = new LutAccessor(firstLutFile);
    }

    @Test
    public void test_InitWithDescriptionFile() throws IOException {
        final File descriptionFile =
                new File(LutAccessor.class.getResource("../../../../../../sentinel-2a_lut_smsi_v0.6.dims.jsn").getPath());
        final LutAccessor accessor = new LutAccessor(descriptionFile);
        assertEquals(9, accessor.getNumberOfNonSpectralProperties());
        assertEquals(9, accessor.getNumberOfNonSpectralProperties());
        assertArrayEquals(firstTargetNames, accessor.getTargetNames());
    }

}
