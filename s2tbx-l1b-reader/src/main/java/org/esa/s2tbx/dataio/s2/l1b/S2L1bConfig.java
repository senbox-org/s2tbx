/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.l1b;

import jp2.TileLayout;
import org.esa.snap.framework.datamodel.ProductData;
import org.esa.s2tbx.openjpeg.OpenJpegUtils;

import java.awt.image.DataBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Norman Fomferra
 */
public interface S2L1bConfig {

    //todo these numbers should actually been read from the JP2 files, because they are likely to change if prod. spec. changes
    //todo use classes from jp2 package to read the data
    TileLayout[] L1B_TILE_LAYOUTS = new TileLayout[]{
            new TileLayout(2548, 2304, 1024, 1024, 3, 3, 6), // 10
            new TileLayout(1274, 1152, 1024, 1024, 2, 2, 6), // 20
            new TileLayout(424, 384, 424, 384, 1, 1, 6), // 60
    };

    Set<TileLayout> REAL_TILE_LAYOUT = new HashSet<>();

    Map<Integer, Integer> LAYOUTMAP = new HashMap<Integer, Integer>() {
        {
            put(10, 0);
            put(20, 1);
            put(60, 2);
        }

        ;
    };

    String FORMAT_NAME = "SENTINEL-2-MSI-L1B";
}
