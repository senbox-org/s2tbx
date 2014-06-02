package org.esa.beam.dataio.s2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Norman Fomferra
 */
public class S2MetadataFilenameTest {

    @Test
    public void testValidFileName() throws Exception {
        S2MetadataFilename filename = S2MetadataFilename.create("MTD_GPPL1C_054_20091210235100_20091210235130_0001.xml");
        assertNotNull(filename);
        assertEquals("L1C", filename.procLevel);
        assertEquals("054", filename.orbitNo);
        assertEquals("20091210235100", filename.start);
        assertEquals("20091210235130", filename.stop);
        assertEquals("0001", filename.sceneId);
    }

    @Test
    public void testGetBand() throws Exception {
        S2MetadataFilename filename = S2MetadataFilename.create("MTD_GPPL1C_054_20091210235100_20091210235130_0001.xml");
        assertEquals("IMG_GPPL1C_054_20091210235100_20091210235130_08_000000_15SUC.jp2",
                     filename.getImgFilename(8, "15SUC"));
        assertEquals("IMG_GPPL1C_054_20091210235100_20091210235130_10_000000_15TVE.jp2",
                     filename.getImgFilename(10, "15TVE"));
    }

    @Test
    public void testInvalidFileName() throws Exception {
        assertNull(S2MetadataFilename.create("IMG_GPPL1C_054_20091210235100_20091210235130_02_000000_15SUC.jp2"));
        assertNull(S2MetadataFilename.create("MTD_GPPL1C_054_20091210235100_20091210235130_0001.ml"));
    }
}