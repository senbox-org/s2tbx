package org.esa.s2tbx.fcc.trimming;

import org.esa.snap.utils.AbstractParallelComputing;

import java.io.IOException;

/**
 * @author Jean Coravu
 */
public class MovingWindowTileParallelComputing extends AbstractParallelComputing {
    private final int imageWidth;
    private final int imageHeight;
    private final int tileWidth;
    private final int tileHeight;
    private final int movingStepWidth;

    private int currentTopY;
    private int currentLeftX;

    public MovingWindowTileParallelComputing(int imageWidth, int imageHeight, int tileWidth, int tileHeight, int movingStepWidth) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.movingStepWidth = movingStepWidth;
    }

    @Override
    protected void execute() throws Exception {
        int localTopY = -1;
        int localLeftX = -1;
        do {
            localTopY = -1;
            localLeftX = -1;
            synchronized (this) {
                if (this.threadException != null) {
                    return;
                }
                if (this.currentTopY < this.imageHeight) {
                    if (this.currentLeftX < this.imageWidth) {
                        localLeftX = this.currentLeftX;
                        localTopY = this.currentTopY;
                    } else {
                        this.currentLeftX = 0; // reset the column index
                        localLeftX = this.currentLeftX;

                        this.currentTopY += this.tileHeight; // increment the row index
                        if (this.currentTopY < this.imageHeight) {
                            localTopY = this.currentTopY;
                        }
                    }
                    this.currentLeftX += this.tileWidth;
                }
            }
            if (localTopY >= 0 && localLeftX >= 0) {
                runTile2(localTopY, localLeftX, tileWidth, tileHeight);
            }
        } while (localTopY >= 0 && localLeftX >= 0);
    }

    private void runTile2(int localTopY, int localLeftX, int tileWidth, int tileHeight) {

    }

//    @Override
//    protected void execute() throws Exception {
//        int localRowIndex = -1;
//        int localColumnIndex = -1;
//        do {
//            localRowIndex = -1;
//            localColumnIndex = -1;
//            synchronized (this) {
//                if (this.threadException != null) {
//                    return;
//                }
//                if (this.currentRowIndex < this.rowCount) {
//                    if (this.currentColumnIndex < this.columnCount) {
//                        localColumnIndex = this.currentColumnIndex;
//                        localRowIndex = this.currentRowIndex;
//                    } else {
//                        this.currentColumnIndex = 0; // reset the column index
//                        localColumnIndex = this.currentColumnIndex;
//
//                        this.currentRowIndex++; // increment the row index
//                        if (this.currentRowIndex < this.rowCount) {
//                            localRowIndex = this.currentRowIndex;
//                        }
//                    }
//                    this.currentColumnIndex++;
//                }
//            }
//            if (localRowIndex >= 0 && localColumnIndex >= 0) {
//                //runTile(localRowIndex, localColumnIndex);
//            }
//        } while (localRowIndex >= 0 && localColumnIndex >= 0);
//    }
}
