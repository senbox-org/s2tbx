package org.esa.s2tbx.fcc.trimming;

import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.io.IOException;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 * @since 5.0.6
 */

public abstract class MovingWindowRegionParallelComputing extends MovingWindowImageTileParallelComputing {

    protected MovingWindowRegionParallelComputing(int imageWidth, int imageHeight, int tileWidth, int tileHeight) {
        super(imageWidth, imageHeight, tileWidth, tileHeight);
    }
}
