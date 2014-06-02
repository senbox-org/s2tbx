package org.esa.beam.dataio.s2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Norman Fomferra
 */
public class S2ImageFilenameTest {

    @Test
    public void testValidFileName() throws Exception {
        S2ImageFilename filename = S2ImageFilename.create("IMG_GPPL1C_054_20091210235100_20091210235130_01_000000_15TVE.jp2");
        assertNotNull(filename);
        assertEquals("L1C", filename.procLevel);
        assertEquals("054", filename.orbitNo);
        assertEquals("20091210235100", filename.start);
        assertEquals("20091210235130", filename.stop);
        assertEquals("01", filename.band);
        assertEquals("15TVE", filename.tileId);
    }

    @Test
    public void testGetBand() throws Exception {
        S2ImageFilename filename = S2ImageFilename.create("IMG_GPPL1C_054_20091210235100_20091210235130_01_000000_15TVE.jp2");
        assertEquals(1, filename.getBand());
        assertEquals(5, filename.getBand("IMG_GPPL1C_054_20091210235100_20091210235130_05_000000_15TVE.jp2"));
        assertEquals(11, filename.getBand("IMG_GPPL1C_054_20091210235100_20091210235130_11_000000_15TVE.jp2"));
        assertEquals(-1, filename.getBand("IMG_GPPL1C_054_20091210235100_20091210235130_11_000000_15SUD.jp2"));
    }

    @Test
    public void testInvalidFileName() throws Exception {
        assertNull(S2ImageFilename.create("MTD_GPPL1C_054_20091210235100_20091210235130_0001.xml"));
        assertNull(S2ImageFilename.create("TBN_GPPL1C_054_20091210235100_20091210235130_000000_15SUD.jpg"));
    }
}