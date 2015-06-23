/*
 *
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

package org.esa.s2tbx.dataio.s2;


import jp2.TileLayout;

/**
 * Represents the Sentinel-2 MSI XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Nicolas Ducoin
 */
public abstract class S2Metadata {

    private TileLayout[] tileLayouts;

    public S2Metadata(TileLayout[] tileLayouts) {
        this.tileLayouts = tileLayouts;
    }

    public TileLayout[] getTileLayouts() {
        return tileLayouts;
    }
}
