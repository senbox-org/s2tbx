package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.utils.matrix.IntMatrix;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 */

public class MovingWindow {
    private final IntMatrix colorFillerMatrix;
    private final IntSet validSegmentIds;
    private final IntSet invalidSegmentIds;

    public MovingWindow(IntMatrix colorFillerMatrix) {
        this.colorFillerMatrix = colorFillerMatrix;

        this.validSegmentIds = new IntOpenHashSet();
        this.invalidSegmentIds = new IntOpenHashSet();
    }

    public IntSet runTile(int tileLeftX, int tileTopY, int tileRightX, int tileBottomY) {
        for (int rowIndex = tileTopY; rowIndex < tileBottomY; rowIndex++) {
            int pixelValueLeft = this.colorFillerMatrix.getValueAt(rowIndex, tileLeftX);
            int pixelValueRight = this.colorFillerMatrix.getValueAt(rowIndex, tileRightX - 1);

            if (pixelValueLeft != ForestCoverChangeConstants.NO_DATA_VALUE) {
                if (!invalidSegmentIds.contains(pixelValueLeft)) {
                    validateElementLeft(pixelValueLeft, tileLeftX, rowIndex);
                }
            }
            if (pixelValueRight != ForestCoverChangeConstants.NO_DATA_VALUE) {
                if (!invalidSegmentIds.contains(pixelValueRight)) {
                    validateElementRight(pixelValueRight, tileRightX, rowIndex);
                }
            }
        }

        for (int column = tileLeftX; column < tileRightX; column++) {
            int pixelValueTop = this.colorFillerMatrix.getValueAt(tileTopY, column);
            int pixelValueBottom = this.colorFillerMatrix.getValueAt(tileBottomY - 1, column);
            if (pixelValueTop != ForestCoverChangeConstants.NO_DATA_VALUE) {
                if (!invalidSegmentIds.contains(pixelValueTop)) {
                    validateElementTop(pixelValueTop, tileTopY, column);
                }
            }
            if (pixelValueBottom != ForestCoverChangeConstants.NO_DATA_VALUE) {
                if (!invalidSegmentIds.contains(pixelValueBottom)) {
                    validateElementBottom(pixelValueBottom, tileBottomY, column);
                }
            }
        }

        for (int row = tileTopY + 1; row < tileBottomY - 1; row++) {
            for (int column = tileLeftX + 1; column < tileRightX - 1; column++) {
                int pixelValue = this.colorFillerMatrix.getValueAt(row, column);
                if (pixelValue != ForestCoverChangeConstants.NO_DATA_VALUE) {
                    if (!invalidSegmentIds.contains(pixelValue)) {
                        validSegmentIds.add(pixelValue);
                    }
                }
            }
        }
        return this.validSegmentIds;
    }

    private void validateElementBottom(int pixelValueBottom, int tileBottomY, int column) {
        if (tileBottomY < this.colorFillerMatrix.getRowCount()) {
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
    private void validateElementRight(int pixelValueRight, int tileRightX, int row) {
        if (tileRightX < this.colorFillerMatrix.getColumnCount()) {
            int localPixelValue = this.colorFillerMatrix.getValueAt(row, tileRightX);
            processPixelValue(pixelValueRight, localPixelValue);
        } else {
            addValidElement(pixelValueRight);
        }
    }

    private void processPixelValue(int pixelValue, int comparablePixelValue) {
        if (comparablePixelValue == pixelValue) {
            invalidSegmentIds.add(pixelValue);
            removeFromValidElementList(pixelValue);
        } else {
            addValidElement(pixelValue);
        }
    }

    private void addValidElement(int pixelToAdd) {
        validSegmentIds.add(pixelToAdd);
    }

    private void removeFromValidElementList(int pixelToRemove) {
        validSegmentIds.rem(pixelToRemove);
    }
}

