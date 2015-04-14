package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.s2tbx.dataio.s2.l1b.filepatterns.S2L1bProductFilename;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class S2L1bProductFilenameTest {

    @Test
    public void testFileName() throws Exception
    {
        S2L1bProductFilename s2gf = S2L1bProductFilename.create("S2A_OPER_PRD_MSIL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.SAFE");
        assertNotNull(s2gf);
        assertEquals("065", s2gf.relativeOrbitNumber);
        assertEquals("20091211T165928_20091211T170025", s2gf.applicabilityPeriod);
        assertEquals("20130621T120000", s2gf.creationDate);
    }
}
