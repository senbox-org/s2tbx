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

/**
 * @author Nicolas Ducoin
 */
public abstract class S2SceneDescription {

    private final int tileSize10M;
    private final double pixelResolution10M;
    private final double tileResolution10M;

    public S2SceneDescription(S2Config config) {
        tileSize10M = config.getTileLayouts()[0].width;
        pixelResolution10M = S2SpatialResolution.R10M.resolution;;
        tileResolution10M = pixelResolution10M * tileSize10M;
    }

    public double getTileResolution10M() {
        return tileResolution10M;
    }
}
