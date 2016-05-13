/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.s2msi.idepix.operators;


import org.esa.snap.core.gpf.Tile;

import java.awt.*;

/**
 * cloud buffer algorithms
 */
public class Sentinel2CloudBuffer {

    public static void computeSimpleCloudBuffer(int x, int y,
                                                Tile targetTile,
                                                Rectangle extendedRectangle,
                                                int cloudBufferWidth,
                                                int cloudBufferFlagBit) {
        Rectangle rectangle = targetTile.getRectangle();
        int LEFT_BORDER = Math.max(x - cloudBufferWidth, extendedRectangle.x);
        int RIGHT_BORDER = Math.min(x + cloudBufferWidth, extendedRectangle.x + extendedRectangle.width - 1);
        int TOP_BORDER = Math.max(y - cloudBufferWidth, extendedRectangle.y);
        int BOTTOM_BORDER = Math.min(y + cloudBufferWidth, extendedRectangle.y + extendedRectangle.height - 1);

        for (int i = LEFT_BORDER; i <= RIGHT_BORDER; i++) {
            for (int j = TOP_BORDER; j <= BOTTOM_BORDER; j++) {
                if (rectangle.contains(i, j) && extendedRectangle.contains(i, j)) {
                    targetTile.setSample(i, j, cloudBufferFlagBit, true);
                }
            }
        }
    }
}
