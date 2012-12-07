package org.esa.beam.dataio.s2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Norman Fomferra
 */
public class S2FileNameInfoTest {

    @Test
    public void testValidFileName() throws Exception {

        final S2FilenameInfo fni = S2FilenameInfo.create("IMG_GPPL1C_054_20091210235100_20091210235130_01_000000_15TVE.jp2");

        assertNotNull(fni);
        assertEquals("L1C",fni.procLevel);
        assertEquals("054",fni.orbitNo);
        assertEquals("20091210235100", fni.start);
        assertEquals("20091210235130", fni.stop);
        assertEquals("01", fni.band);
        assertEquals("15TVE", fni.tileId);

        assertEquals(1, fni.getBand());
        assertEquals(5, fni.getBand("IMG_GPPL1C_054_20091210235100_20091210235130_05_000000_15TVE.jp2"));
        assertEquals(11, fni.getBand("IMG_GPPL1C_054_20091210235100_20091210235130_11_000000_15TVE.jp2"));
        assertEquals(-1, fni.getBand("IMG_GPPL1C_054_20091210235100_20091210235130_11_000000_15SUD.jp2"));
    }

    @Test
    public void testInvalidFileName() throws Exception {
        assertNull(S2FilenameInfo.create("MTD_GPPL1C_054_20091210235100_20091210235130_0001.xml"));
        assertNull(S2FilenameInfo.create("TBN_GPPL1C_054_20091210235100_20091210235130_000000_15SUD.jpg"));
    }
}