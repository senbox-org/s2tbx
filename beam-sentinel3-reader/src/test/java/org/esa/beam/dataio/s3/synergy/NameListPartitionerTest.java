package org.esa.beam.dataio.s3.synergy;/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NameListPartitionerTest {

    @Test
    public void testPartitioning() throws Exception {
        final String[] names = {
                "B1_CAM1", "B1_CAM2", "B1_CAM3", "B1_CAM4", "B1_CAM5",
                "B2_CAM1", "B2_CAM2", "B2_CAM3", "B2_CAM4", "B2_CAM5",
        };

        final Map<String, List<String>> map = Partitioner.partition(names, "_CAM");

        assertNotNull(map);
        assertEquals(2, map.size());

        final List<String> listB1 = map.get("B1");
        assertNotNull(listB1);

        final List<String> listB2 = map.get("B2");
        assertNotNull(listB2);

        assertEquals(5, listB1.size());
        assertEquals(5, listB2.size());

        assertEquals("B1_CAM1", listB1.get(0));
        assertEquals("B1_CAM2", listB1.get(1));
        assertEquals("B1_CAM3", listB1.get(2));
        assertEquals("B1_CAM4", listB1.get(3));
        assertEquals("B1_CAM5", listB1.get(4));

        assertEquals("B2_CAM1", listB2.get(0));
        assertEquals("B2_CAM2", listB2.get(1));
        assertEquals("B2_CAM3", listB2.get(2));
        assertEquals("B2_CAM4", listB2.get(3));
        assertEquals("B2_CAM5", listB2.get(4));
    }


}
