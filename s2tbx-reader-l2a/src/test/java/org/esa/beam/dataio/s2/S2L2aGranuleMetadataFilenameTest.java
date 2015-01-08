package org.esa.beam.dataio.s2;

import org.esa.beam.dataio.s2.filepatterns.S2L2aGranuleMetadataFilename;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class S2L2aGranuleMetadataFilenameTest {

    @Test
    public void testFileName() throws Exception
    {
        S2L2aGranuleMetadataFilename s2gf = S2L2aGranuleMetadataFilename.create("S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLD.xml");
        assertNotNull(s2gf);
        assertEquals("S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLD.xml", s2gf.name);
        assertEquals("S2A", s2gf.missionID);
        assertEquals("OPER", s2gf.fileClass);
        assertEquals("MTD_", s2gf.fileCategory);
        assertEquals("L1C_TL", s2gf.fileSemantic);
        assertEquals("CGS1", s2gf.siteCentre);
        assertEquals("20130621T120000", s2gf.creationDate);
        assertEquals("_A000065", s2gf.absoluteOrbit);

        assertEquals("_T14SLD", s2gf.tileNumber);
    }
}
