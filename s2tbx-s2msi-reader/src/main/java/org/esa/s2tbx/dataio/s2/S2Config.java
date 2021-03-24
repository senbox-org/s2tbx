/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2;

import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.lib.openjpeg.jp2.TileLayout;
import org.esa.snap.lib.openjpeg.utils.OpenJpegExecRetriever;

import java.awt.image.DataBuffer;
import java.util.logging.Logger;

/**
 * Class to store S2 readers paramteters: static const, openjpeg executables path, tile layouts, ...
 *
 * @author Nicolas Ducoin
 * @author Norman Fomferra
 */
public class S2Config {

    public enum Sentinel2ProductLevel {
        L1A,
        L1B,
        L1C,
        L2A,
        L2H,
        L2F,
        L3,
        UNKNOWN
    }

    public enum Sentinel2ProductMission {
        S2A,
        S2B,
        LS8,
        UNKNOWN
    }

    public enum Sentinel2InputType {
        INPUT_TYPE_PRODUCT_METADATA,
        INPUT_TYPE_GRANULE_METADATA
    }


    public static final String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_.*";

    public static final boolean DEBUG = Boolean.getBoolean("org.esa.s2tbx.dataio.s2.l1c.S2Config.DEBUG");
    public static final boolean NODUMP = Boolean.getBoolean("org.esa.s2tbx.dataio.s2.l1c.S2Config.NODUMP");

    public static final String LOG_JPEG = DEBUG ? "INFO" : "FINEST";
    public static final String LOG_SCENE = DEBUG ? "INFO" : "FINEST";

    public static final String OPJ_DECOMPRESSOR_EXE = OpenJpegExecRetriever.getOpjDecompress();
    public static final String OPJ_INFO_EXE = OpenJpegExecRetriever.getOpjDump();

    public static final int DEFAULT_JAI_TILE_SIZE = 512;

    public static final int SAMPLE_PRODUCT_DATA_TYPE = ProductData.TYPE_UINT16;
    public static final int SAMPLE_DATA_BUFFER_TYPE = DataBuffer.TYPE_USHORT;
    public static final int SAMPLE_BYTE_COUNT = 2;

    public static final short FILL_CODE_NO_FILE = DEBUG ? (short) 1000 : 0;
    public static final short FILL_CODE_NO_INTERSECTION = DEBUG ? (short) 1 : 0;
    public static final short FILL_CODE_OUT_OF_X_BOUNDS = DEBUG ? (short) 2 : 0;
    public static final short FILL_CODE_OUT_OF_Y_BOUNDS = DEBUG ? (short) 3 : 0;
    public static final short FILL_CODE_MOSAIC_BG = DEBUG ? (short) 4 : 0;

    public static final double FILL_CODE_MOSAIC_ANGLES = Double.NaN;

    public static final short RAW_NO_DATA_THRESHOLD = DEBUG ? (short) 4 : (short) 0;

    public static final String MTD_EXT = ".xml";

    private TileLayout[] tileLayouts = new TileLayout[5];


    /**
     * returns the TileLayout for a given resolution. If no TileLayout was set for the resolution
     * returns the default tile layout
     *
     * @param spatialResolution the resolution for which we want the tile layout
     * @return the tile layout at the given resolution
     */
    public TileLayout getTileLayout(S2SpatialResolution spatialResolution) {
        TileLayout tileLayoutForResolution;
        int tileIndex = spatialResolution.id;
        tileLayoutForResolution = tileLayouts[tileIndex];

        // TODO : Rewrite ! This code is too messy and misses comments to explain the rationale
        if (tileLayoutForResolution == null) {
            TileLayout nonNullTileLayout = null;
            int resolutionForNonNullTileLayout = 0;
            if (spatialResolution == S2SpatialResolution.R10M) {
                if (tileLayouts[S2SpatialResolution.R20M.id] != null) {
                    nonNullTileLayout = tileLayouts[S2SpatialResolution.R20M.id];
                    resolutionForNonNullTileLayout = S2SpatialResolution.R20M.resolution;
                } else if (tileLayouts[S2SpatialResolution.R60M.id] != null) {
                    nonNullTileLayout = tileLayouts[S2SpatialResolution.R60M.id];
                    resolutionForNonNullTileLayout = S2SpatialResolution.R60M.resolution;
                }

                if (nonNullTileLayout != null) {
                    int factor = resolutionForNonNullTileLayout / S2SpatialResolution.R10M.resolution;
                    int width = nonNullTileLayout.width * factor;
                    int height = nonNullTileLayout.height * factor;
                    int tileWidth = nonNullTileLayout.tileWidth * factor;
                    int tileHeight = nonNullTileLayout.tileHeight * factor;
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


    /**
     * returns the TileLayout for a given resolution. If no TileLayout was set for the resolution
     * returns the default tile layout
     *
     * @param resolution the resolution for which we want the tile layout
     * @return the tile layout at the given resolution
     */
    public TileLayout getTileLayout(int resolution) {
        return getTileLayout(S2SpatialResolution.valueOfResolution(resolution));
    }


    /**
     * Update the tile layout for the resolution. The existing tile layout is replaced
     *
     * @param resolution the resolution for which we want to replace the tile layout
     * @param tileLayout the new tile layout
     */
    public void updateTileLayout(S2SpatialResolution resolution, TileLayout tileLayout) {
        tileLayouts[resolution.id] = tileLayout;
    }

    public static Sentinel2ProductLevel levelString2ProductLevel(String level){
        if(level.equals("L1A")) { return Sentinel2ProductLevel.L1A;}
        if(level.equals("L1B")) { return Sentinel2ProductLevel.L1B;}
        if(level.equals("L1C")) { return Sentinel2ProductLevel.L1C;}
        if(level.equals("L2A")) { return Sentinel2ProductLevel.L2A;}
        if(level.equals("L2H")) { return Sentinel2ProductLevel.L2H;}
        if(level.equals("L2F")) { return Sentinel2ProductLevel.L2F;}
        if(level.equals("L03")) { return Sentinel2ProductLevel.L3;}
        return Sentinel2ProductLevel.UNKNOWN;
    }

}
