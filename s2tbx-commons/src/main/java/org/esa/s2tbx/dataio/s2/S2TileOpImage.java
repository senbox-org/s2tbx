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

package org.esa.s2tbx.dataio.s2;

import com.bc.ceres.glevel.MultiLevelModel;
import jp2.TileLayout;
import org.esa.snap.jai.ResolutionLevel;
import org.esa.snap.jai.SingleBandedOpImage;

import java.awt.*;
import java.io.File;


// todo - better log problems during read process, see {@report "Problem detected..."} code marks

/**
 * @author Norman Fomferra
 * @author Nicolas Ducoin
 */
public class S2TileOpImage extends SingleBandedOpImage {


    public S2TileOpImage(
                  Point imagePos,
                  TileLayout l1cTileLayout,
                  MultiLevelModel imageModel,
                  int level) {
        super(S2Config.SAMPLE_DATA_BUFFER_TYPE,
              imagePos,
              l1cTileLayout.width,
              l1cTileLayout.height,
              getTileDimAtResolutionLevel(l1cTileLayout.tileWidth, l1cTileLayout.tileHeight, level),
              null,
              ResolutionLevel.create(imageModel, level));
    }

    static Dimension getTileDimAtResolutionLevel(int fullTileWidth, int fullTileHeight, int level) {
        int width = getSizeAtResolutionLevel(fullTileWidth, level);
        int height = getSizeAtResolutionLevel(fullTileHeight, level);
        return getTileDim(width, height);
    }

    /**
     * Computes a new size at a given resolution level in the style of JPEG2000.
     *
     * @param fullSize the full size
     * @param level    the resolution level
     * @return the reduced size at the given level
     */
    static int getSizeAtResolutionLevel(int fullSize, int level) {
        int size = fullSize >> level;
        int sizeTest = size << level;
        if (sizeTest < fullSize) {
            size++;
        }

        return size;
    }

    static Dimension getTileDim(int width, int height) {
        return new Dimension(width < S2Config.DEFAULT_JAI_TILE_SIZE ? width : S2Config.DEFAULT_JAI_TILE_SIZE,
                             height < S2Config.DEFAULT_JAI_TILE_SIZE ? height : S2Config.DEFAULT_JAI_TILE_SIZE);
    }
}
