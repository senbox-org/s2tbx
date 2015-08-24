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

package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.S2Config;

/**
 * @author Norman Fomferra
 */
public class S2L1CConfig extends S2Config {

    private static S2L1CConfig instance;

    private S2L1CConfig() {}

    public static S2L1CConfig getInstance() {
        if(instance == null) {
            instance = new S2L1CConfig();
        }
        return instance;
    }

    TileLayout[] L1C_DEFAULT_TILE_LAYOUTS = new TileLayout[]{
            new TileLayout(10980, 10980, 2048, 2048, 6, 6, 6),
            new TileLayout(5490, 5490, 2048, 2048, 3, 3, 6),
            new TileLayout(1830, 1830, 2048, 2048, 1, 1, 6),
    };

    final String FORMAT_NAME = "SENTINEL-2-MSI-L1C";

    @Override
    public TileLayout getDefaultTileLayout(int resolution) {
        int tileIndex = LAYOUTMAP.get(resolution);
        return L1C_DEFAULT_TILE_LAYOUTS[tileIndex];
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
    }
}
