package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.fcc.common.SegmentUtil;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rdumitrascu on 9/27/2017.
 */
public class MovingWindow {
    private static final Logger logger = Logger.getLogger(MovingWindow.class.getName());

    private final IntMatrix colorFillerMatrix;
    private final Product extractedBandsProduct;
    private final List<SegmentUtil> productSegments;

    public MovingWindow(IntMatrix colorFillerMatrix, final List<SegmentUtil> productSegments, Product extractedBandsProduct) {
        this.colorFillerMatrix = colorFillerMatrix;
        this.extractedBandsProduct = extractedBandsProduct;
        this.productSegments = productSegments;
    }

    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight)
            throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute moving window for tile region starting at : row index: bounds [x=" + tileLeftX + ", y=" + tileTopY + ", width=" + tileWidth + ", height=" + tileHeight + "]");
        }

        IntSet validSegmentList = new IntOpenHashSet();
        IntSet invalidSegmentList = new IntOpenHashSet();

        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int row = tileTopY; row < tileBottomY; row++) {
            for (int column = tileLeftX; column < tileRightX; column++) {
                int pixelValue = this.colorFillerMatrix.getValueAt(row, column);
                if (pixelValue != ForestCoverChangeConstants.NO_DATA_VALUE) {
                    if (row == tileTopY || row == tileBottomY || column == tileLeftX || column == tileRightX) {
                        if (row == tileTopY && tileTopY != 0) {
                            int localPixelValue = this.colorFillerMatrix.getValueAt(row - 1, column);
                            if (localPixelValue == pixelValue) {
                                invalidSegmentList.add(pixelValue);
                            }
                            if (!invalidSegmentList.contains(pixelValue)) {
                                validSegmentList.add(pixelValue);
                            }
                        } else if (row == tileTopY && tileTopY == 0) {
                            if (!invalidSegmentList.contains(pixelValue)) {
                                validSegmentList.add(pixelValue);
                            }
                        }
                        if (row == tileBottomY && tileBottomY != this.extractedBandsProduct.getSceneRasterHeight()) {
                            int localPixelValue = this.colorFillerMatrix.getValueAt(row + 1, column);
                            if (localPixelValue == pixelValue) {
                                invalidSegmentList.add(pixelValue);
                            }
                            if (!invalidSegmentList.contains(pixelValue)) {
                                validSegmentList.add(pixelValue);
                            }
                        } else if (row == tileBottomY && tileBottomY != this.extractedBandsProduct.getSceneRasterHeight()) {
                            if (!invalidSegmentList.contains(pixelValue)) {
                                validSegmentList.add(pixelValue);
                            }
                        }
                        if (column == tileLeftX && tileLeftX != 0) {
                            int localPixelValue = this.colorFillerMatrix.getValueAt(row, column - 1);
                            if (localPixelValue == pixelValue) {
                                invalidSegmentList.add(pixelValue);
                            }
                            if (!invalidSegmentList.contains(pixelValue)) {
                                validSegmentList.add(pixelValue);
                            }
                        } else if (column == tileLeftX && tileLeftX == 0) {
                            if (!invalidSegmentList.contains(pixelValue)) {
                                validSegmentList.add(pixelValue);
                            }
                        }
                        if (column == tileRightX && tileLeftX != this.extractedBandsProduct.getSceneRasterWidth()) {
                            int localPixelValue = this.colorFillerMatrix.getValueAt(row, column + 1);
                            if (localPixelValue == pixelValue) {
                                invalidSegmentList.add(pixelValue);
                            }
                            if (!invalidSegmentList.contains(pixelValue)) {
                                validSegmentList.add(pixelValue);
                            }
                        } else if (column == tileLeftX && tileLeftX == this.extractedBandsProduct.getSceneRasterWidth()) {
                            if (!invalidSegmentList.contains(pixelValue)) {
                                validSegmentList.add(pixelValue);
                            }
                        }
                    } else {
                        if (!invalidSegmentList.contains(pixelValue)) {
                            if (!validSegmentList.contains(pixelValue)) {
                                validSegmentList.add(pixelValue);
                            }
                        }
                        while (column < tileRightX - 1) {
                            column++;
                            int localPixelValue = this.colorFillerMatrix.getValueAt(row, column);
                            if (localPixelValue != pixelValue) {
                                column--;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
