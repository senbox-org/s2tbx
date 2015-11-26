/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.s2.l2a;

import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author opicas-p
 */
public class S2L2aProductFilenameTest {

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
