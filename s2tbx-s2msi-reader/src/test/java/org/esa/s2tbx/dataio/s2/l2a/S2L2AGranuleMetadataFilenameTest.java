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

import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleMetadataFilename;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Oscar Picas-Puig
 */
public class S2L2AGranuleMetadataFilenameTest {

    @Test
    public void testFileName() throws Exception
    {
        S2OrthoGranuleMetadataFilename s2gf = S2OrthoGranuleMetadataFilename.create("S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLD.xml");
        assertNotNull(s2gf);
        assertEquals("S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLD.xml", s2gf.name);
        assertEquals("S2A", s2gf.missionID);
        assertEquals("OPER", s2gf.fileClass);
        assertEquals("MTD_", s2gf.fileCategory);
        assertEquals("L1C_TL", s2gf.fileSemantic);
        assertEquals("CGS1", s2gf.siteCentre);
        assertEquals("20130621T120000", s2gf.creationDate);
        assertEquals("A000065", s2gf.absoluteOrbit);

        assertEquals("T14SLD", s2gf.tileNumber);
    }
}
