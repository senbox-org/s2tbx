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

package org.esa.s2tbx.dataio.s2;

import org.geotools.geometry.Envelope2D;

import java.awt.Rectangle;
import java.io.File;

/**
 * @author Norman Fomferra
 */
public class S2L2AProductInfo {
    /*
public final int numBands;

public final int numL1cTiles;

ProductInfo getProductInfo();

File getJp2ImageFile(int bandIndex, int tileIndex);

BandInfo getBandInfo(int bandIndex);

L1cTileInfo getL1cTileInfo(int tileIndex);
     */
    public class ProductInfo {
        public final File productDir;
        public final Envelope2D sceneEnvelope;
        public final Rectangle sceneRectangle;

        public ProductInfo(File productDir, Envelope2D sceneEnvelope, Rectangle sceneRectangle) {
            this.productDir = productDir;
            this.sceneEnvelope = sceneEnvelope;
            this.sceneRectangle = sceneRectangle;
        }
    }

    public class BandInfo {
        public final int index;
        public final S2SpatialResolution resolution;
        public final double wavelength;
        public final double solarFlux;
        public final double wavelengthMin;
        public final double wavelengthMax;
        public final Jp2ImageInfo jp2ImageLayout;

        public BandInfo(int index, S2SpatialResolution resolution, double wavelength, double wavelengthMin, double wavelengthMax, double solarFlux, Jp2ImageInfo jp2ImageLayout) {
            this.index = index;
            this.resolution = resolution;
            this.wavelength = wavelength;
            this.solarFlux = solarFlux;
            this.wavelengthMin = wavelengthMin;
            this.wavelengthMax = wavelengthMax;
            this.jp2ImageLayout = jp2ImageLayout;
        }
    }

    public class L1cTileInfo {
        public final int index;
        public final String id;
        public final Envelope2D envelope;
        public final Rectangle rectangle;

        public L1cTileInfo(int index, String id, Envelope2D envelope, Rectangle rectangle) {
            this.envelope = envelope;
            this.index = index;
            this.id = id;
            this.rectangle = rectangle;
        }
    }

    public class Jp2ImageInfo {
        public final int width;
        public final int height;
        public final int tileWidth;
        public final int tileHeight;
        public final int numXTiles;
        public final int numYTiles;
        public final int numResolutions;

        public Jp2ImageInfo(int width, int height, int tileWidth, int tileHeight, int numXTiles, int numYTiles, int numResolutions) {
            this.height = height;
            this.width = width;
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.numXTiles = numXTiles;
            this.numYTiles = numYTiles;
            this.numResolutions = numResolutions;
        }
    }
}
