package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 */

public class MovingWindow extends MovingWindowRegionParallelComputing {
    private static final Logger logger = Logger.getLogger(MovingWindow.class.getName());

    private final IntMatrix colorFillerMatrix;
    private final IntSet validSegmentList;
    private final IntSet invalidSegmentList;

    public MovingWindow(IntMatrix colorFillerMatrix,  int tileWidth, int tileHeight) {
        super(colorFillerMatrix.getColumnCount(),colorFillerMatrix.getRowCount(),tileWidth, tileHeight);
        this.colorFillerMatrix = colorFillerMatrix;
        this.validSegmentList = new IntOpenHashSet();
        this.invalidSegmentList = new IntOpenHashSet();
    }

    public IntSet runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int imageWidth, int imageHeight)
            throws IOException, IllegalAccessException, InterruptedException {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute moving window for tile region starting at : row index: bounds [x=" + tileLeftX + ", y=" + tileTopY + ", width=" + tileWidth + ", height=" + tileHeight + "]");
        }
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;

        for (int row = tileTopY; row < tileBottomY; row++) {
            int pixelValueLeft = this.colorFillerMatrix.getValueAt(row, tileLeftX);
            int pixelValueRight = this.colorFillerMatrix.getValueAt(row, tileRightX - 1);

            if (pixelValueLeft != ForestCoverChangeConstants.NO_DATA_VALUE) {
                if (!invalidSegmentList.contains(pixelValueLeft)) {
                    validateElementLeft(pixelValueLeft, tileLeftX, row);
                }
            }
            if (pixelValueRight != ForestCoverChangeConstants.NO_DATA_VALUE) {
                if (!invalidSegmentList.contains(pixelValueRight)) {
                    validateElementRight(pixelValueRight, tileRightX, imageWidth, row);
                }
            }
        }

        for (int column = tileLeftX; column < tileRightX ; column++) {
            int pixelValueTop = this.colorFillerMatrix.getValueAt(tileTopY, column);
            int pixelValueBottom = this.colorFillerMatrix.getValueAt(tileBottomY - 1, column);
            if (pixelValueTop != ForestCoverChangeConstants.NO_DATA_VALUE ) {
                if (!invalidSegmentList.contains(pixelValueTop)) {
                    validateElementTop(pixelValueTop, tileTopY, column);
                }
            }
            if(pixelValueBottom != ForestCoverChangeConstants.NO_DATA_VALUE ) {
               if (!invalidSegmentList.contains(pixelValueBottom)) {
                   validateElementBottom(pixelValueBottom, tileBottomY, imageHeight, column);
               }
            }
       }

        for (int row = tileTopY+1; row < tileBottomY-1; row++) {
            for (int column = tileLeftX+1; column < tileRightX-1; column++) {
                int pixelValue = this.colorFillerMatrix.getValueAt(row, column);
                if (pixelValue != ForestCoverChangeConstants.NO_DATA_VALUE) {
                    if (!invalidSegmentList.contains(pixelValue)) {
                        validSegmentList.add(pixelValue);
                    }
                }
            }
        }
        return this.validSegmentList;
    }

    private void validateElementBottom(int pixelValueBottom, int tileBottomY, int imageHeight, int column) {
        if (tileBottomY < imageHeight) {
            int localPixelValue = this.colorFillerMatrix.getValueAt(tileBottomY, column);
            processPixelValue(pixelValueBottom, localPixelValue);
        } else {
            addValidElement(pixelValueBottom);
        }
    }

    private void validateElementTop(int pixelValueTop, int tileTopY, int column) {
        if (tileTopY > 0) {
            int localPixelValue = this.colorFillerMatrix.getValueAt(tileTopY - 1, column);
            processPixelValue(pixelValueTop, localPixelValue);
        } else {
            addValidElement(pixelValueTop);
        }
    }

    private void validateElementLeft(int pixelValueLeft, int tileLeftX, int row) {
        if (tileLeftX > 0) {
            int localPixelValue = this.colorFillerMatrix.getValueAt(row, tileLeftX - 1);
            processPixelValue(pixelValueLeft, localPixelValue);
        } else {
            addValidElement(pixelValueLeft);
        }
    }
    private void validateElementRight(int pixelValueRight, int tileRightX, int imageWidth, int row) {
        if (tileRightX < imageWidth) {
            int localPixelValue = this.colorFillerMatrix.getValueAt(row, tileRightX);
            processPixelValue(pixelValueRight, localPixelValue);
        } else {
            addValidElement(pixelValueRight);
        }
    }

    private void processPixelValue(int pixelValue, int comparablePixelValue){
        if (comparablePixelValue == pixelValue) {
            invalidSegmentList.add(pixelValue);
            removeFromValidElementList(pixelValue);
        } else {
            addValidElement(pixelValue);
        }
    }

    private void addValidElement(int pixelValueTop) {
            validSegmentList.add(pixelValueTop);
    }

    private void removeFromValidElementList(int pixelValueTop) {
            validSegmentList.rem(pixelValueTop);
    }
}

