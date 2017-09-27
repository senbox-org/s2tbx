package org.esa.snap.utils;

import org.esa.snap.core.util.math.MathUtils;

import java.io.IOException;

/**
 * @author Jean Coravu
 */
public abstract class AbstractImageTilesParallelComputing extends AbstractMatrixCellsParallelComputing {
    private int imageWidth;
    private int imageHeight;
    private int tileWidth;
    private int tileHeight;

    protected AbstractImageTilesParallelComputing(int imageWidth, int imageHeight, int tileWidth, int tileHeight) {
        super(MathUtils.ceilInt(imageWidth / (double) tileWidth), MathUtils.ceilInt(imageHeight / (double) tileHeight));

        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    protected abstract void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                                    throws IOException, IllegalAccessException, InterruptedException;

    @Override
    protected final void runTile(int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException, InterruptedException {
        int localTileLeftX = localColumnIndex * getTileWidth();
        int localTileTopY = localRowIndex * getTileHeight();
        int localTileWidth = getTileWidth();
        int localTileHeight = getTileHeight();
        if (localTileLeftX + localTileWidth > getImageWidth()) {
            localTileWidth = getImageWidth() - localTileLeftX;
        }
        if (localTileTopY + localTileHeight > getImageHeight()) {
            localTileHeight = getImageHeight() - localTileTopY;
        }
        runTile(localTileLeftX, localTileTopY, localTileWidth, localTileHeight, localRowIndex, localColumnIndex);
    }

    protected final int getImageHeight() {
        return imageHeight;
    }

    protected final int getImageWidth() {
        return imageWidth;
    }

    private int getTileHeight() {
        return tileHeight;
    }

    private int getTileWidth() {
        return tileWidth;
    }
}
