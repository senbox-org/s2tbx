package org.esa.beam.dataio;

import org.esa.beam.framework.datamodel.Band;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a matrix arrangement of multiple images (tiles) that make up
 * a single product scene. Normally, these images are already orhtorectified, so that
 * pixels align to the projected coordinates.
 *
 * @author  Cosmin Cara
 */
public class BandMatrix {

    private int numRows;
    private int numCols;
    private boolean isChecked;
    private boolean isConsistent;

    private int currentRow;
    private int currentCol;

    private BandMatrixCell[] cachedCells;

    /**
     * This class represents a cell in the matrix of "bands".
     * Besides acting as a container for Band references, this class exposes methods for
     * computing the overlapping area of two cells and for computing intersection of a cell
     * with a given rectangle (useful when requesting a smaller area to a band reader).
     */
    public class BandMatrixCell {
        /**
         * The associated cell band
         */
        public Band band;
        /**
         * The origin (in meters) of the band image
         */
        public Point2D origin;
        /**
         * The pixel size (in meters) on horizontal axis
         */
        public double dX;
        /**
         * The pixel size (in meters) on vertical axis
         */
        public double dY;
        /**
         * The width (in meters) of the cell.
         */
        public double cellWidth;
        /**
         * The height (in meters) of the cell.
         */
        public double cellHeight;
        /**
         * The raster abscissa of the origin pixel. For the first image,
         * it should be 0. For the subsequent images on the same matrix row,
         * it should be the sum of previous cells widths.
         */
        public int cellStartPixelX;
        /**
         * The raster ordinate of the origin pixel. For the first image,
         * it should be 0. For the subsequent images on the same matrix row,
         * it should be the sum of previous cells heights.
         */
        public int cellStartPixelY;
        /**
         * The width of the cell, in pixels.
         */
        public int cellPixelWidth;
        /**
         * The height of the cell, in pixels.
         */
        public int cellPixelHeight;
        /**
         * The number of pixels (from left edge) this cell overlaps the left one.
         */
        public int cellOffsetX;
        /**
         * The number of pixels (from top edge) this cells overlaps the upper one.
         */
        public int cellOffsetY;

        /**
         * The only (package protected) constructor of the cell.
         * A cell is not intended to be created but only by a <code>BandMatrix</code> object.
         *
         * @param band      The associated band
         * @param origin    The origin point (coordinates expressed in meters) of the image
         * @param stepX     The horizontal pixel size (in meters)
         * @param stepY     The vertical pixel size (in meters)
         */
        BandMatrixCell(Band band, Point2D origin, double stepX, double stepY) {
            this.band = band;
            this.origin = origin;
            this.dX = stepX;
            this.dY = stepY;
            if (band != null) {
                this.cellPixelWidth = band.getRasterWidth();
                this.cellPixelHeight = band.getRasterHeight();
            }
            this.cellWidth = (double)cellPixelWidth * dX;
            this.cellHeight = (double)cellPixelHeight * dY;
        }

        /**
         * Gets the intersection area for the rectangle specified by the given top-left point and dimensions and this cell.
         * @param x         The ordinate of the top-left point of the test area
         * @param y         The abscissa of the top-left point of the test area
         * @param width     The width of the test area
         * @param height    The height of the test area
         * @return  A <code>java.awt.Rectangle</code> if the intersection area between this cell and the constructed rectangle
         *          has positive width and height, <code>null</code> otherwise.
         */
        public Rectangle intersection(int x, int y, int width, int height) {
            return intersection(new Rectangle(x, y, width, height));
        }

        /**
         * Gets the intersection area for the given rectangle and this cell.
         *
         * @param rectangle The rectangle to test
         * @return  A <code>java.awt.Rectangle</code> if the intersection area between this cell and the given rectangle
         *          has positive width and height, <code>null</code> otherwise.
         */
        public Rectangle intersection(Rectangle rectangle) {
            Rectangle result = new Rectangle();
            Rectangle.intersect(new Rectangle(cellStartPixelX + cellOffsetX, cellStartPixelY + cellOffsetY, cellPixelWidth - cellOffsetX, cellPixelHeight - cellOffsetY),
                                rectangle,
                                result);
            return (result.width > 0 && result.height > 0) ? result : null;
        }

        /**
         * Returns the overlapping area (i.e. common area) between two cells.
         * @param otherCell    The second cell to test for overlapping with.
         * @return      A rectangle with integer bounds representing pixels.
         */
        public Rectangle overlapping(BandMatrixCell otherCell) {
            Rectangle2D otherCellArea = new Rectangle2D.Double(otherCell.origin.getX(), otherCell.origin.getY(), otherCell.cellWidth, otherCell.cellHeight);
            Rectangle2D cellArea = new Rectangle2D.Double(origin.getX(), origin.getY(), cellWidth, cellHeight);
            Rectangle2D overlap2D = cellArea.createIntersection(otherCellArea);
            Rectangle overlap = overlap2D.getBounds();
            return new Rectangle((int)(overlap.getX() - origin.getX()),
                                 (int)(overlap.getY() - origin.getY()),
                                 (int)(overlap.getWidth() - 1),         // subtract 1 because for cells continuing each other width is 1
                                 (int)(overlap.getHeight() - 1));
        }
    }

    private final BandMatrixCell[][] internal;

    /**
     * Creates a band matrix having <code>rows</code> rows and <code>cols</code> columns
     * @param rows  Number of matrix rows
     * @param cols  Number of matrix columns
     */
    public BandMatrix(int rows, int cols) {
        this.numRows = rows;
        this.numCols = cols;
        this.internal = new BandMatrixCell[rows][cols];
    }

    /**
     * Gets all the cells of this matrix, ordered from top-left cell to bottom-right cell,
     * row by row.
     *
     * @return  An array of matrix cells
     */
    public BandMatrixCell[] getCells() {
        if (cachedCells == null || cachedCells.length != numRows * numCols) {
            synchronized (internal) {
                List<BandMatrixCell> cells = new ArrayList<BandMatrixCell>();
                for (int col = 0; col < numCols; col++) {
                    for (int row = 0; row < numRows; row++) {
                        cells.add(internal[row][col]);
                    }
                }
                BandMatrixCell[] retArr = new BandMatrixCell[cells.size()];
                cachedCells = cells.toArray(retArr);
            }
        }
        return cachedCells;
    }

    /**
     * Gets the number of rows of this matrix.
     *
     * @return  Integer representing the number of rows
     */
    public int getNumRows() { return numRows; }

    /**
     * Gets the number of columns of this matrix.
     *
     * @return  Integer representing the number of columns
     */
    public int getNumCols() { return numCols; }

    /**
     * Adds (creates) a new cell to this instance, at the next available position.
     * If the current (last) position is already at the bottom-right cell, an exception
     * is thrown.
     *
     * @param band          The associated band
     * @param cellOrigin    The origin of the image (in meters)
     * @param stepX         The horizontal pixel size (in meters)
     * @param stepY         The vertical pixel size (in meters)
     */
    public void addCell(Band band, Point2D cellOrigin, double stepX, double stepY) {
        if (currentRow == numRows && currentCol == numCols)
            throw new IllegalArgumentException("Cannot add cell past to the matrix size");
        addCellAt(currentRow, currentCol, band, cellOrigin, stepX, stepY);
        if (currentCol == numCols - 1) {
            currentCol = 0;
            currentRow++;
        }
        currentCol++;
    }

    /**
     * Adds (creates) a new cell to this instance, at the specified row and column (0-based).
     * If any of the row/column numbers exceed the respective matrix size, an exception is thrown.
     *
     * @param row           The row at which to insert the new cell
     * @param col           The column at which to insert the new cell
     * @param band          The associated band
     * @param cellOrigin    The origin of the image (in meters)
     * @param stepX         The horizontal pixel size (in meters)
     * @param stepY         The vertical pixel size (in meters)
     */
    public void addCellAt(int row, int col, Band band, Point2D cellOrigin, double stepX, double stepY) {
        if (row < 0 || row > numRows - 1)
            throw new IllegalArgumentException("Invalid row index");
        if (col < 0 || col > numCols - 1)
            throw new IllegalArgumentException("Invalid row index");
        BandMatrixCell cell = new BandMatrixCell(band, cellOrigin, stepX, stepY);
        this.internal[row][col] = cell;
        if (col > 0) {
            BandMatrixCell leftCell = this.internal[row][col - 1];
            if (cell.cellPixelHeight != leftCell.cellPixelHeight)
                throw new IllegalArgumentException("Band height is different from that of previously added bands");
            cell.cellStartPixelX = leftCell.cellStartPixelX + leftCell.cellPixelWidth;
            Rectangle leftOverlap = cell.overlapping(leftCell);
            cell.cellOffsetX = leftOverlap.width;
        } else {
            cell.cellStartPixelX = 0;
        }
        if (row > 0) {
            BandMatrixCell upperCell = this.internal[row - 1][col];
            if (cell.cellPixelWidth != upperCell.cellPixelWidth)
                throw new IllegalArgumentException("Band width is different from that of previously added bands");
            cell.cellStartPixelY = upperCell.cellStartPixelY + upperCell.cellPixelHeight;
            Rectangle upperOverlap = cell.overlapping(upperCell);
            cell.cellOffsetY = upperOverlap.height;
        } else {
            cell.cellStartPixelY = 0;
        }
    }

    /**
     * Gets the total width, in pixels, of this matrix.
     * This is the sum of all cell widths from any row.
     * If this matrix contains at least one unassigned cell, an exception is thrown.
     *
     * @return  Integer representing the total width (in pixels) of the matrix.
     */
    public int getTotalWidth() {
        if (!isConsistent()) {
            throw new UnsupportedOperationException("Current matrix has unassigned cells!");
        }
        int total = 0;
        for (int i = 0; i < numCols; i++) {
            total += this.internal[0][i].cellPixelWidth;
        }
        return total;
    }

    /**
     * Gets the total height, in pixels, of this matrix.
     * This is the sum of all cell heights from any column.
     * If this matrix contains at least one unassigned cell, an exception is thrown.
     *
     * @return  Integer representing the total height (in pixels) of the matrix.
     */
    public int getTotalHeight() {
        if (!isConsistent()) {
            throw new UnsupportedOperationException("Current matrix has unassigned cells!");
        }
        int total = 0;
        for (int i = 0; i < numRows; i++) {
            total += this.internal[i][0].cellPixelHeight;
        }
        return total;
    }

    /**
     * Given the input rectangle, it returns all cells which are intersected by this rectangle.
     * For example:
     *      Given a matrix 3 x 3 cells like:
     *          [   (0,0,100,100)   (100,0,100,100)     (200,0,100,100)
     *              (0,100,100,100) (100,100,100,100)   (200,100,100,100)
     *              (0,200,100,100) (100,200,100,100)   (200,200,100,100)   ]
     *      and the rectangle (75,75,50,50),
     *      the intersecting cells returned would be (in this order):
     *      { (0,0,100,100), (100,0,100,100), (0,100,100,100), (100,100,100,100) }
     *
     * If this matrix contains at least one unassigned cell, an exception is thrown.
     *
     * @param rectangle     The rectangle for which to compute the list of intersecting cells.
     * @return  Array of matrix cells
     */
    public BandMatrixCell[] findIntersectingCells(Rectangle rectangle) {
        if (!isConsistent()) {
            throw new UnsupportedOperationException("Current matrix is not consistent!");
        }
        List<BandMatrixCell> cells = new ArrayList<BandMatrixCell>();
        if (rectangle != null && rectangle.width > 0 && rectangle.height > 0) {
            for (int col = 0; col < numCols; col++) {
                for (int row = 0; row < numRows; row++) {
                    if (this.internal[row][col].intersection(rectangle) != null) {
                        cells.add(this.internal[row][col]);
                    }
                }
            }
        }
        return cells.toArray(new BandMatrixCell[cells.size()]);
    }

    /**
     * Checks if there are unassigned cells inside this matrix.
     * A matrix with at least one unassigned cell is considered not to be consistent.
     */
    private boolean isConsistent() {
        if (!this.isChecked) {
            this.isConsistent = true;
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    this.isConsistent &= (this.internal[i][j] != null);
                }
            }
            this.isChecked = true;
        }
        return this.isConsistent;
    }
}
