package org.esa.snap.utils;

import org.esa.snap.core.util.math.MathUtils;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractImageTilesHelper {
    private final int tileCountX;
    private final int tileCountY;

    private int rowIndex;
    private int columnIndex;
    private int threadCounter;
    private int imageWidth;
    private int imageHeight;
    private int tileWidth;
    private int tileHeight;

    protected AbstractImageTilesHelper(int imageWidth, int imageHeight, int tileWidth, int tileHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        this.rowIndex = 0;
        this.columnIndex = 0;
        this.threadCounter = 0;

        this.tileCountX = MathUtils.ceilInt(imageWidth / (double) tileWidth);
        this.tileCountY = MathUtils.ceilInt(imageHeight / (double) tileHeight);
    }

    protected abstract void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                                    throws IOException, IllegalAccessException, InterruptedException;

    public final void executeUsingThreads(int threadCount, Executor threadPool) throws IllegalAccessException, IOException, InterruptedException {
        for (int i=0; i<threadCount; i++) {
            TileSegmentationRunnable segmentationRunnable = new TileSegmentationRunnable(this);
            threadPool.execute(segmentationRunnable);
        }
        executeSegmentation();
        waitToFinish();
    }

    private synchronized void incrementThreadCounter() {
        this.threadCounter++;
    }

    private synchronized void decrementThreadCounter() {
        this.threadCounter--;
        if (this.threadCounter <= 0) {
            notifyAll();
        }
    }

    private synchronized void waitToFinish() throws InterruptedException {
        if (this.threadCounter > 0) {
            wait();
        }
    }

    private int getImageHeight() {
        return imageHeight;
    }

    private int getImageWidth() {
        return imageWidth;
    }

    private int getTileHeight() {
        return tileHeight;
    }

    private int getTileWidth() {
        return tileWidth;
    }

    private void executeSegmentation() throws IOException, IllegalAccessException, InterruptedException {
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
        } while (localRowIndex >= 0 && localColumnIndex >= 0);
    }

    private static class TileSegmentationRunnable implements Runnable {
        private static final Logger logger = Logger.getLogger(TileSegmentationRunnable.class.getName());

        private final AbstractImageTilesHelper imageTilesHelper;

        public TileSegmentationRunnable(AbstractImageTilesHelper imageTilesHelper) {
            this.imageTilesHelper = imageTilesHelper;
            this.imageTilesHelper.incrementThreadCounter();
        }

        @Override
        public void run() {
            try {
                this.imageTilesHelper.executeSegmentation();
            } catch (Exception exception) {
                logger.log(Level.SEVERE, "Failed to execute the image tiles.", exception);
            } finally {
                this.imageTilesHelper.decrementThreadCounter();
            }
        }
    }
}
