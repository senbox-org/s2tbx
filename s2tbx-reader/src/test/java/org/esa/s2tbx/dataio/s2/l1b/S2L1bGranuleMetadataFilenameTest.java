package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleMetadataFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleMetadataFilename;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by opicas-p on 24/06/2014.
 */
public class S2L1bGranuleMetadataFilenameTest {

    @Test
    public void testFileName() throws Exception
    {
        S2L1BGranuleMetadataFilename s2gf = (S2L1BGranuleMetadataFilename) S2L1BGranuleMetadataFilename.create("S2A_OPER_MTD_L1B_GR_MPS__20140926T120000_S20130707T171927_D06.xml");
        assertNotNull(s2gf);
        assertEquals("S2A_OPER_MTD_L1B_GR_MPS__20140926T120000_S20130707T171927_D06.xml", s2gf.name);
        assertEquals("S2A", s2gf.missionID);
        assertEquals("OPER", s2gf.fileClass);
        assertEquals("MTD_", s2gf.fileCategory);
        assertEquals("L1B_GR", s2gf.fileSemantic);
        assertEquals("MPS_", s2gf.siteCentre);
        assertEquals("20140926T120000", s2gf.creationDate);
        assertEquals("20130707T171927", s2gf.startDate);

        assertEquals("D06", s2gf.detectorId);
    }
}
