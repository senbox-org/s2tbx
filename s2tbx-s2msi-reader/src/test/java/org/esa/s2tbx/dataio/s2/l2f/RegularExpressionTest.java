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

package org.esa.s2tbx.dataio.s2.l2f;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * @author Florian Douziech
 */

public class RegularExpressionTest {

    @Before
    public void setup() {
    }

    @Test
    public void testFileMatch() throws Exception
    {   
        Pattern pat = Pattern.compile("(S2A|S2B|S2_|LS8)_([A-Z|0-9|_]{6})_([0-9]{8}T[0-9]{6})_N([0-9]{4})_R([0-9]{3})_.*");

        assertTrue(pat.matcher("S2A_MSIL2F_20200101T105441_N9999_R051_T31UFS_20200101T112309").matches());

        assertTrue(pat.matcher("LS8_OLIL2F_20200121T103424_N9999_R198_T31UFS_20200128T084427").matches());

    }


}
