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
import org.esa.s2tbx.dataio.s2.S2Config;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Norman Fomferra
 */
public class S2L1bConfig implements S2Config {

    private static S2L1bConfig instance;

    private S2L1bConfig() {}

    public static S2L1bConfig getInstance() {
        if(instance == null) {
            instance = new S2L1bConfig();
        }
        return instance;
    }


    //todo these numbers should actually been read from the JP2 files, because they are likely to change if prod. spec. changes
    //todo use classes from jp2 package to read the data
    private TileLayout[] L1B_TILE_LAYOUTS = new TileLayout[]{
            new TileLayout(2552, 2304, 2552, 2304, 1, 1, 5), // 10
            new TileLayout(1274, 1152, 1274, 1152, 1, 1, 5), // 20
            new TileLayout(424, 384, 424, 384, 1, 1, 5), // 60
    };

    static Set<TileLayout> REAL_TILE_LAYOUT = new HashSet<>();

    String FORMAT_NAME = "SENTINEL-2-MSI-L1B";

    @Override
    public TileLayout[] getTileLayouts() {
        return L1B_TILE_LAYOUTS;
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
    }
}
