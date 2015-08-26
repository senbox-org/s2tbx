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

import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.snap.framework.datamodel.ProductData;

import java.awt.image.DataBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.esa.s2tbx.dataio.openjpeg.OpenJpegExecRetriever.getSafeDecompressorAndUpdatePermissions;
import static org.esa.s2tbx.dataio.openjpeg.OpenJpegExecRetriever.getSafeInfoExtractorAndUpdatePermissions;

/**
 * @author Norman Fomferra
 */
public class S2Config {
    public static final boolean DEBUG = Boolean.getBoolean("org.esa.s2tbx.dataio.s2.l1c.S2Config.DEBUG");
    public static final boolean NODUMP = Boolean.getBoolean("org.esa.s2tbx.dataio.s2.l1c.S2Config.NODUMP");

    public static final String LOG_JPEG = DEBUG ? "INFO" : "FINEST";
    public static final String LOG_SCENE = DEBUG ? "INFO" : "FINEST";
    public static final String LOG_DEBUG = DEBUG ? "WARNING" : "FINEST";

    public static final String OPJ_DECOMPRESSOR_EXE = getSafeDecompressorAndUpdatePermissions();
    public static final String OPJ_INFO_EXE = getSafeInfoExtractorAndUpdatePermissions();

    public static final int DEFAULT_JAI_TILE_SIZE = 512;

    public static final int SAMPLE_PRODUCT_DATA_TYPE = ProductData.TYPE_UINT16;
    public static final int SAMPLE_DATA_BUFFER_TYPE = DataBuffer.TYPE_USHORT;
    public static final int SAMPLE_BYTE_COUNT = 2;

    public static final short FILL_CODE_NO_FILE = DEBUG ? (short) 1000 : 0;
    public static final short FILL_CODE_NO_INTERSECTION = DEBUG ? (short) 1 : 0;
    public static final short FILL_CODE_OUT_OF_X_BOUNDS = DEBUG ? (short) 2 : 0;
    public static final short FILL_CODE_OUT_OF_Y_BOUNDS = DEBUG ? (short) 3 : 0;
    public static final short FILL_CODE_MOSAIC_BG = DEBUG ? (short) 4 : 0;

    public static final short RAW_NO_DATA_THRESHOLD = DEBUG ? (short) 4 : (short) 0;

    public static final String MTD_EXT = ".xml";

    /**
     * Map between resolution and index in the tile layout
     */
    public static final Map<Integer, Integer> LAYOUTMAP = new HashMap<Integer, Integer>() {
        {
            put(10, 0);
            put(20, 1);
            put(60, 2);
        }
    };

    private TileLayout[] tileLayouts = new TileLayout[3];

    /**
     * returns the TileLayout for a given resolution. If no TileLayout was set for the resolution
     * returns the default tile layout
     *
     * @param resolution the resolution for which we want the tile layout
     * @return the tile layout at the given resolution
     */
    public TileLayout getTileLayout(int resolution){
        TileLayout tileLayoutForResolution;
        int tileIndex = LAYOUTMAP.get(resolution);
        tileLayoutForResolution = tileLayouts[tileIndex];

        if(tileLayoutForResolution == null) {
            TileLayout nonNullTileLayout = null;
            int resolutionForNonNullTileLayout = 0;
            if(resolution == S2SpatialResolution.R10M.resolution) {
                if(tileLayouts[S2SpatialResolution.R20M.id] != null) {
                    nonNullTileLayout = tileLayouts[S2SpatialResolution.R20M.id];
                    resolutionForNonNullTileLayout = S2SpatialResolution.R20M.resolution;
                } else if(tileLayouts[S2SpatialResolution.R60M.id] != null) {
                    nonNullTileLayout = tileLayouts[S2SpatialResolution.R60M.id];
                    resolutionForNonNullTileLayout = S2SpatialResolution.R60M.resolution;
                }

                if(nonNullTileLayout != null) {
                    float factor = resolutionForNonNullTileLayout / resolution;
                    int width = Math.round(nonNullTileLayout.width * factor);
                    int height = Math.round(nonNullTileLayout.height * factor);
                    int tileWidth = Math.round(nonNullTileLayout.tileWidth * factor);
                    int tileHeight = Math.round(nonNullTileLayout.tileHeight * factor);
                    tileLayoutForResolution =
                            new TileLayout(width, height,
                                           tileWidth, tileHeight,
                                           nonNullTileLayout.numXTiles, nonNullTileLayout.numYTiles,
                                           nonNullTileLayout.numResolutions);
                }
            }
        }

        return tileLayoutForResolution;
    }

    public void updateTileLayout(int resolution, TileLayout tileLayout) {
        int tileIndex = LAYOUTMAP.get(resolution);
        tileLayouts[tileIndex] = tileLayout;
    }

    /**
     * Returns the tileLayouts map, somme elements of this map can be null
     *
     * Deprecated, instead use getDefaultTileLayout(int resolution)
     *
     * @return the tileLayouts map
     */
    @Deprecated
    public TileLayout[] getTileLayouts(){
        TileLayout[] tileLayoutsToReturn = new TileLayout[3];

        tileLayoutsToReturn[S2SpatialResolution.R10M.id] = getTileLayout(S2SpatialResolution.R10M.resolution);
        tileLayoutsToReturn[S2SpatialResolution.R20M.id] = getTileLayout(S2SpatialResolution.R20M.resolution);
        tileLayoutsToReturn[S2SpatialResolution.R60M.id] = getTileLayout(S2SpatialResolution.R60M.resolution);

        return tileLayoutsToReturn;
    }
}
