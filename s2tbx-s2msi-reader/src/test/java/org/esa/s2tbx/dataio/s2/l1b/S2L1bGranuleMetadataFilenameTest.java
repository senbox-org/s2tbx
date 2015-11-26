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

package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleMetadataFilename;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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
