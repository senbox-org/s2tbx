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

import com.bc.ceres.glevel.support.AbstractMultiLevelSource;

import org.junit.Before;
import org.junit.Test;

import static java.lang.Math.pow;
import static org.junit.Assert.assertEquals;

/**
 * @author Norman Fomferra
 */
public class Jp2MultiLevelModelTest {
    @Before
    public void setup() {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    }
    @Test
    public void testModel() throws Exception {
        assertEquals(4096, doit(4096, 0));
        assertEquals(2048, doit(4096, 1));
        assertEquals(1024, doit(4096, 2));
        assertEquals(512, doit(4096, 3));
        assertEquals(256, doit(4096, 4));
        assertEquals(128, doit(4096, 5));

        assertEquals(1826, doit(1826, 0));
        assertEquals(913, doit(1826, 1));
        assertEquals(457, doit(1826, 2));
        assertEquals(229, doit(1826, 3));
        assertEquals(115, doit(1826, 4));
        assertEquals(58, doit(1826, 5));

        assertEquals(1826, doit2(1826, 0));
        assertEquals(913, doit2(1826, 1));
        assertEquals(456, doit2(1826, 2));
        assertEquals(228, doit2(1826, 3));
        assertEquals(114, doit2(1826, 4));
        assertEquals(57, doit2(1826, 5));
    }

    private int doit2(int width, int level) {
        return AbstractMultiLevelSource.getImageDimension(width, 2*width, pow(2.0, level)).width;
    }

    private int doit(int w, int r) {
        return (int) Math.ceil((w) / Math.pow(2, r));
        /*
        final int i = w >> r;
        if (w > i << r) {
            return i + 1;
        }
        return i;
        */
    }


}
