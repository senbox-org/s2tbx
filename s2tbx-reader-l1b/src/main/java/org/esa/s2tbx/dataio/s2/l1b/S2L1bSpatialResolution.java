/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.l1b;

/**
 * @author Norman Fomferra
 */
public enum S2L1bSpatialResolution {
    R10M(0, 10), R20M(1, 20), R60M(2, 60);

    public final int id;
    public final int resolution;

    S2L1bSpatialResolution(int id, int resolution) {
        this.id = id;
        this.resolution = resolution;
    }

    public static S2L1bSpatialResolution valueOfId(int id) {
        for (S2L1bSpatialResolution value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("id=" + id);
    }

    public static S2L1bSpatialResolution valueOfResolution(int resolution) {
        for (S2L1bSpatialResolution value : values()) {
            if (value.resolution == resolution) {
                return value;
            }
        }
        throw new IllegalArgumentException("resolution=" + resolution);
    }
}
