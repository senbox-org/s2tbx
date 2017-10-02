package org.esa.s2tbx.fcc;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.fcc.mahalanobis.StorageMatrix;
import org.esa.s2tbx.fcc.trimming.MovingWindow;
import org.esa.snap.core.util.math.MathUtils;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by rdumitrascu on 9/28/2017.
 */
public class mainFCC {

    public static void main(String[] args) {
        IntMatrix matrix = new IntMatrix(21, 21);
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 21; j++) {
                matrix.setValueAt(i, j, ForestCoverChangeConstants.NO_DATA_VALUE);
            }
        }

        matrix.setValueAt(1, 1, 1);
        matrix.setValueAt(1, 2, 1);
        matrix.setValueAt(2, 1, 1);
        matrix.setValueAt(2, 2, 1);

        matrix.setValueAt(1, 6, 2);
        matrix.setValueAt(1, 7, 2);
        matrix.setValueAt(1, 8, 2);
        matrix.setValueAt(2, 6, 2);
        matrix.setValueAt(2, 7, 2);
        matrix.setValueAt(2, 8, 2);

        matrix.setValueAt(3, 11, 3);
        matrix.setValueAt(3, 12, 3);
        matrix.setValueAt(3, 13, 3);
        matrix.setValueAt(4, 11, 3);
        matrix.setValueAt(4, 12, 3);
        matrix.setValueAt(4, 13, 3);
        matrix.setValueAt(5, 11, 3);
        matrix.setValueAt(5, 12, 3);
        matrix.setValueAt(5, 13, 3);
        matrix.setValueAt(6, 11, 3);
        matrix.setValueAt(6, 12, 3);
        matrix.setValueAt(6, 13, 3);

        matrix.setValueAt(1, 16, 4);
        matrix.setValueAt(1, 17, 4);
        matrix.setValueAt(1, 18, 4);
        matrix.setValueAt(2, 16, 4);
        matrix.setValueAt(2, 17, 4);
        matrix.setValueAt(2, 18, 4);

        matrix.setValueAt(9, 1, 5);
        matrix.setValueAt(9, 2, 5);
        matrix.setValueAt(9, 3, 5);
        matrix.setValueAt(10, 1, 5);
        matrix.setValueAt(10, 2, 5);
        matrix.setValueAt(10, 3, 5);

        matrix.setValueAt(8, 8, 6);
        matrix.setValueAt(8, 9, 6);
        matrix.setValueAt(8, 10, 6);
        matrix.setValueAt(9, 8, 6);
        matrix.setValueAt(9, 9, 6);
        matrix.setValueAt(9, 10, 6);
        matrix.setValueAt(10, 8, 6);
        matrix.setValueAt(10, 9, 6);
        matrix.setValueAt(10, 10, 6);

        matrix.setValueAt(15, 4, 8);
        matrix.setValueAt(15, 5, 8);
        matrix.setValueAt(16, 4, 8);
        matrix.setValueAt(16, 5, 8);

        matrix.setValueAt(15, 12, 7);
        matrix.setValueAt(15, 13, 7);
        matrix.setValueAt(16, 12, 7);
        matrix.setValueAt(16, 13, 7);

        matrix.setValueAt(16, 0, 9);
        matrix.setValueAt(17, 0, 9);

        matrix.setValueAt(7, 19, 10);
        matrix.setValueAt(8, 19, 10);

        MovingWindow movingWindow = new MovingWindow(matrix);
        int imageWidth = matrix.getColumnCount();
        int imageHeight = matrix.getRowCount();
        int movingWindowHeight = 10;
        int movingWindowWidth = 10;
        int movingStep = 5;

        if (movingWindowHeight > imageHeight) {
            throw new IllegalArgumentException("movingWindow height must be smaller than image height");
        }
        if (movingWindowWidth > imageWidth) {
            throw new IllegalArgumentException("movingWindow width must be smaller than image width");
        }
        if (movingStep > movingWindowWidth || movingStep > movingWindowHeight) {
            throw new IllegalArgumentException("movingStep  must be smaller than movingWindow size");
        }
        int columns = MathUtils.ceilInt(imageWidth / movingWindowWidth);
        int rows = MathUtils.ceilInt(imageHeight / movingWindowHeight);
        for (int row = 0; row <= rows; row++) {
            for (int col = 0; col <= columns; col++) {
                    IntSet validRegions = movingWindow.runTile(movingStep * col, movingStep * row, movingWindowWidth, movingWindowHeight);
                    Iterator it = validRegions.iterator();
                    System.out.println("Tile start " + movingStep * row + " and " + movingStep * col);
                    while (it.hasNext()) {
                        System.out.println(" values: " + it.next());
                    }
            }
        }
        if (rows * movingWindowHeight < imageHeight) {
            int tempMovingWindowHeight = imageHeight - (rows+1) * movingStep;
            for (int col = 0; col <= columns; col++) {
                    IntSet validRegions = movingWindow.runTile(movingStep * col, movingStep * (rows + 1), movingWindowWidth, tempMovingWindowHeight);
                    Iterator it = validRegions.iterator();
                    System.out.println("Tile start " + movingStep * (rows + 1) + " and " + movingStep * col);
                    while (it.hasNext()) {
                        System.out.println(" values: " + it.next());
                    }
            }
        }

        if (columns * movingWindowWidth < imageWidth) {
            int tempMovingWindowWidth = imageWidth - (columns+1) * movingStep;
            for (int row = 0; row <= rows; row++) {
                    IntSet validRegions = movingWindow.runTile(movingStep * (columns + 1), movingStep * row, tempMovingWindowWidth, movingWindowHeight);
                    Iterator it = validRegions.iterator();
                    System.out.println("Tile start " + movingStep * row + " and " + movingStep * (columns + 1));
                    while (it.hasNext()) {
                        System.out.println(" values: " + it.next());
                    }
            }
        }

        if (rows * movingWindowHeight < imageHeight || columns * movingWindowWidth < imageWidth) {
            {
                int tempMovingWindowWidth = imageWidth - ( rows + 1) * movingStep;
                int tempMovingWindowHeight = imageHeight - (columns + 1) * movingStep;
                if ((tempMovingWindowWidth != 0) && (tempMovingWindowHeight != 0)) {
                        IntSet validRegions = movingWindow.runTile(movingStep * (columns + 1), movingStep * (rows + 1), tempMovingWindowWidth, tempMovingWindowHeight);
                        Iterator it = validRegions.iterator();
                        System.out.println("Tile start " + movingStep * (rows + 1) + " and " + movingStep * (columns + 1));
                        while (it.hasNext()) {
                            System.out.println(" values: " + it.next());
                        }
                }
            }
        }
    }
}
