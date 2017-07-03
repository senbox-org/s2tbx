package org.esa.s2tbx.grm.segmentation.tiles;

import com.bc.ceres.glevel.MultiLevelImage;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.internal.TileImpl;
import org.esa.snap.core.util.math.MathUtils;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.io.IOException;

/**
 * @author  Jean Coravu
 */
public abstract class AbstractTileSegmentationHelper {
    protected final AbstractTileSegmenter tileSegmenter;

    private final int tileCountX;
    private final int tileCountY;

    private int rowIndex;
    private int columnIndex;
    private int threadCounter;

    public AbstractTileSegmentationHelper(AbstractTileSegmenter tileSegmenter) {
        this.tileSegmenter = tileSegmenter;

        this.rowIndex = 0;
        this.columnIndex = 0;
        this.threadCounter = 0;

        this.tileCountX = MathUtils.ceilInt(this.tileSegmenter.getImageWidth() / (double) this.tileSegmenter.getTileWidth());
        this.tileCountY = MathUtils.ceilInt(this.tileSegmenter.getImageHeight() / (double) this.tileSegmenter.getTileHeight());
    }

    protected abstract void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException;

    public final synchronized void incrementThreadCounter() {
        this.threadCounter++;
    }

    public final synchronized void decrementThreadCounter() {
        this.threadCounter--;
        if (this.threadCounter <= 0) {
            notifyAll();
        }
    }

    public final synchronized void waitToFinish() throws InterruptedException {
        if (this.threadCounter > 0) {
            wait();
        }
    }

    public final void executeSegmentation() throws IOException, IllegalAccessException {
        int localRowIndex = -1;
        int localColumnIndex = -1;
        do {
            localRowIndex = -1;
            localColumnIndex = -1;
            synchronized (this) {
                if (this.rowIndex < this.tileCountY) {
                    if (this.columnIndex < this.tileCountX) {
                        localColumnIndex = this.columnIndex;
                        localRowIndex = this.rowIndex;
                    } else {
                        this.columnIndex = 0; // reset the column index
                        localColumnIndex = this.columnIndex;

                        this.rowIndex++; // increment the row index
                        if (this.rowIndex < this.tileCountY) {
                            localRowIndex = this.rowIndex;
                        }
                    }
                    this.columnIndex++;
                }
            }
            if (localRowIndex >= 0 && localColumnIndex >= 0) {
                int localTileLeftX = localColumnIndex * this.tileSegmenter.getTileWidth();
                int localTileTopY = localRowIndex * this.tileSegmenter.getTileHeight();
                int localTileWidth = this.tileSegmenter.getTileWidth();
                int localTileHeight = this.tileSegmenter.getTileHeight();
                if (localTileLeftX + localTileWidth > this.tileSegmenter.getImageWidth()) {
                    localTileWidth = this.tileSegmenter.getImageWidth() - localTileLeftX;
                }
                if (localTileTopY + localTileHeight > this.tileSegmenter.getImageHeight()) {
                    localTileHeight = this.tileSegmenter.getImageHeight() - localTileTopY;
                }
                runTile(localTileLeftX, localTileTopY, localTileWidth, localTileHeight, localRowIndex, localColumnIndex);
            }
        } while (localRowIndex >= 0 && localColumnIndex >= 0);
    }
}