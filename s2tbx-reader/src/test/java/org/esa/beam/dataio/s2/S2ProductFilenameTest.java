package org.esa.beam.dataio.s2;

import org.esa.beam.dataio.s2.filepatterns.S2ProductFilename;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class S2ProductFilenameTest {

    @Test
    public void testFileName() throws Exception
    {
        S2ProductFilename s2gf = S2ProductFilename.create("S2A_OPER_PRD_MSIL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.SAFE");
        assertNotNull(s2gf);
        assertEquals("065", s2gf.relativeOrbitNumber);
        assertEquals("20091211T165928_20091211T170025", s2gf.applicabilityPeriod);
        assertEquals("20130621T120000", s2gf.creationDate);
    }
}
