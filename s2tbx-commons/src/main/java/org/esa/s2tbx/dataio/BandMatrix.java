/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio;

import com.bc.ceres.core.Assert;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PixelPos;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a matrix arrangement of multiple images (tiles) that make up
 * a single product scene. Normally, these images are already orhtorectified, so that
 * pixels align to the projected coordinates.
 *
 * @author Cosmin Cara
 */
public class BandMatrix {

    private int numRows;
    private int numCols;

    private int currentRow;
    private int currentCol;

    private BandMatrixCell[] cachedCells;

    public int maxWidth;
    public int maxHeight;

    public int maxCellWidth;
    public int maxCellHeight;

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

        public int row;

        public int column;

        /**
         * The only (package protected) constructor of the cell.
         * A cell is not intended to be created but only by a <code>BandMatrix</code> object.
         *
         * @param band   The associated band
         * @param origin The origin point (coordinates expressed in meters) of the image
         * @param stepX  The horizontal pixel size (in meters)
         * @param stepY  The vertical pixel size (in meters)
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
            this.cellWidth = (double) cellPixelWidth * dX;
            this.cellHeight = (double) cellPixelHeight * dY;
        }

        BandMatrixCell(int row, int col, Point2D origin, double stepX, double stepY) {
            this.origin = origin;
            this.dX = stepX;
            this.dY = stepY;
            this.cellPixelWidth = maxWidth > (col + 1) * maxCellWidth ? maxCellWidth : (col + 1) * maxCellWidth - maxWidth;
            this.cellPixelHeight = maxHeight > (row + 1) * maxCellHeight ? maxCellHeight : (row + 1) * maxCellHeight - maxHeight;
            this.cellWidth = (double) cellPixelWidth * dX;
            this.cellHeight = (double) cellPixelHeight * dY;
        }

        /**
         * Gets the intersection area for the rectangle specified by the given top-left point and dimensions and this cell.
         *
         * @param x      The ordinate of the top-left point of the test area
         * @param y      The abscissa of the top-left point of the test area
         * @param width  The width of the test area
         * @param height The height of the test area
         * @return A <code>java.awt.Rectangle</code> if the intersection area between this cell and the constructed rectangle
         * has positive width and height, <code>null</code> otherwise.
         */
        public Rectangle intersection(int x, int y, int width, int height) {
            return intersection(new Rectangle(x, y, width, height));
        }

        /**
         * Gets the intersection area for the given rectangle and this cell.
         *
         * @param rectangle The rectangle to test
         * @return A <code>java.awt.Rectangle</code> if the intersection area between this cell and the given rectangle
         * has positive width and height, <code>null</code> otherwise.
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
         *
         * @param otherCell The second cell to test for overlapping with.
         * @return A rectangle with integer bounds representing pixels.
         */
        public Rectangle overlapping(BandMatrixCell otherCell) {
            Rectangle2D otherCellArea = new Rectangle2D.Double(otherCell.origin.getX(), otherCell.origin.getY(), otherCell.cellWidth, otherCell.cellHeight);
            Rectangle2D cellArea = new Rectangle2D.Double(origin.getX(), origin.getY(), cellWidth, cellHeight);
            Rectangle2D overlap2D = cellArea.createIntersection(otherCellArea);
            Rectangle overlap = overlap2D.getBounds();
            return new Rectangle((int) (overlap.getX() - origin.getX()),
                                 (int) (overlap.getY() - origin.getY()),
                                 overlap.getWidth() > 0 ? (int) (overlap.getWidth() - 1) : 0,         // subtract 1 because for cells continuing each other width is 1
                                 overlap.getHeight() > 0 ? (int) (overlap.getHeight() - 1) : 0);
        }

        /**
         * Returns the rotation angle of otherCell relative to the current cell.
         * @param otherCell The second cell to get the rotation from.
         * @return      The angle value (radians)
         */
        public double rotation(BandMatrixCell otherCell) {
            Assert.notNull(otherCell);
            Assert.notNull(otherCell.band);
            double angle = 0.0;
            GeoCoding otherGeoCoding = otherCell.band.getGeoCoding();
            if (otherGeoCoding != null) {
                GeoPos brCorner = new GeoPos();
                otherGeoCoding.getGeoPos(new PixelPos(otherCell.band.getRasterWidth() - 1, otherCell.band.getRasterHeight() - 1), brCorner);
                angle = (brCorner.getLat() - otherCell.origin.getY()) / (brCorner.getLon() - otherCell.origin.getX());
            }
            return angle;
        }
    }

    private final BandMatrixCell[][] internal;

    /**
     * Creates a band matrix having <code>rows</code> rows and <code>cols</code> columns
     *
     * @param rows Number of matrix rows
     * @param cols Number of matrix columns
     */
    public BandMatrix(int rows, int cols) {
        this.numRows = rows;
        this.numCols = cols;
        this.internal = new BandMatrixCell[rows][cols];
    }

    public BandMatrix(int rows, int cols, int width, int height, int cellWidth, int cellHeight) {
        this(rows, cols);
        this.maxWidth = width;
        this.maxHeight = height;
        this.maxCellWidth = cellWidth;
        this.maxCellHeight = cellHeight;
    }

    /**
     * Gets all the cells of this matrix, ordered from top-left cell to bottom-right cell,
     * row by row.
     *
     * @return An array of matrix cells
     */
    public BandMatrixCell[] getCells() {
        if (cachedCells == null || cachedCells.length != numRows * numCols) {
            synchronized (internal) {
                List<BandMatrixCell> cells = new ArrayList<>();
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
     * @return Integer representing the number of rows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Gets the number of columns of this matrix.
     *
     * @return Integer representing the number of columns
     */
    public int getNumCols() {
        return numCols;
    }

    public BandMatrixCell getCellAt(int row, int col) {
        return internal[row][col];
    }

    /**
     * Adds (creates) a new cell to this instance, at the next available position.
     * If the current (last) position is already at the bottom-right cell, an exception
     * is thrown.
     *
     * @param band       The associated band
     * @param cellOrigin The origin of the image (in meters)
     * @param stepX      The horizontal pixel size (in meters)
     * @param stepY      The vertical pixel size (in meters)
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
     * @param row        The row at which to insert the new cell
     * @param col        The column at which to insert the new cell
     * @param band       The associated band
     * @param cellOrigin The origin of the image (in meters)
     * @param stepX      The horizontal pixel size (in meters)
     * @param stepY      The vertical pixel size (in meters)
     */
    public void addCellAt(int row, int col, Band band, Point2D cellOrigin, double stepX, double stepY) {
        if (row < 0 || row > numRows - 1)
            throw new IllegalArgumentException("Invalid row index");
        if (col < 0 || col > numCols - 1)
            throw new IllegalArgumentException("Invalid row index");
        BandMatrixCell cell = this.internal[row][col];
        if (cell == null) {
            cell = band != null ?
                    new BandMatrixCell(band, cellOrigin, stepX, stepY) :
                    new BandMatrixCell(row, col, cellOrigin, stepX, stepY);
            this.internal[row][col] = cell;
            cell.row = row;
            cell.column = col;

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
        } else {
            cell.band = band;
        }
    }

    /**
     * Gets the total width, in pixels, of this matrix.
     * This is the sum of all cell widths from any row.
     * If this matrix contains at least one unassigned cell, an exception is thrown.
     *
     * @return Integer representing the total width (in pixels) of the matrix.
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
     * @return Integer representing the total height (in pixels) of the matrix.
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
     * Given the input rectangle given by its origin coordinates, width and height,
     * it returns all cells which are intersected by this rectangle.
     * For example:
     * Given a matrix 3 x 3 cells like:
     * [    (0,0,100,100)   (100,0,100,100)     (200,0,100,100)
     *      (0,100,100,100) (100,100,100,100)   (200,100,100,100)
     *      (0,200,100,100) (100,200,100,100)   (200,200,100,100)   ]
     * and the rectangle (75,75,50,50),
     * the intersecting cells returned would be (in this order):
     * { (0,0,100,100), (100,0,100,100), (0,100,100,100), (100,100,100,100) }
     *
     * If this matrix contains at least one unassigned cell, an exception is thrown.
     *
     * @param originX   The abscissa of the area origin
     * @param originY   The ordinate of the area origin
     * @param width     THe width of the area
     * @param height    The height of the area
     * @return Array of matrix cells
     */
    public BandMatrixCell[] findIntersectingCells(int originX, int originY, int width, int height) {
        return findIntersectingCells(new Rectangle(originX, originY, width, height));
    }

    /**
     * Given the input rectangle, it returns all cells which are intersected by this rectangle.
     * For example:
     * Given a matrix 3 x 3 cells like:
     * [    (0,0,100,100)   (100,0,100,100)     (200,0,100,100)
     *      (0,100,100,100) (100,100,100,100)   (200,100,100,100)
     *      (0,200,100,100) (100,200,100,100)   (200,200,100,100)   ]
     * and the rectangle (75,75,50,50),
     * the intersecting cells returned would be (in this order):
     * { (0,0,100,100), (100,0,100,100), (0,100,100,100), (100,100,100,100) }
     *
     * If this matrix contains at least one unassigned cell, an exception is thrown.
     *
     * @param rectangle The rectangle for which to compute the list of intersecting cells.
     * @return Array of matrix cells
     */
    public BandMatrixCell[] findIntersectingCells(Rectangle rectangle) {
        if (!isConsistent()) {
            throw new UnsupportedOperationException("Current matrix is not consistent!");
        }
        List<BandMatrixCell> cells = new ArrayList<>();
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
     * Determines the cells of this matrix that intersect the rectangle area given by its
     * origin coordinates (bottom-left), width and height.
     *
     * @param originX   The abscissa of the area origin
     * @param originY   The ordinate of the area origin
     * @param width     THe width of the area
     * @param height    The height of the area
     * @return          A (not-null) map of the cells intersecting the rectangle together with
     *                  the intersecting area of each cell.
     */
    public Map<BandMatrixCell, Rectangle> computeIntersection(int originX, int originY, int width, int height) {
        return computeIntersection(new Rectangle(originX, originY, width, height));
    }

    /**
     * Determines the cells of this matrix that intersect the given rectangle.
     *
     * @param rectangle The rectangle to intersect with
     * @return          A (not-null) map of the cells intersecting the rectangle together with
     *                  the intersecting area of each cell.
     */
    public Map<BandMatrixCell, Rectangle> computeIntersection(Rectangle rectangle) {
        if (!isConsistent()) {
            throw new UnsupportedOperationException("Current matrix is not consistent!");
        }
        Map<BandMatrixCell, Rectangle> cells = new LinkedHashMap<>();
        if (rectangle != null && rectangle.width > 0 && rectangle.height > 0) {
            Rectangle intersection;
            for (int col = 0; col < numCols; col++) {
                for (int row = 0; row < numRows; row++) {
                    if ((intersection = this.internal[row][col].intersection(rectangle)) != null) {
                        cells.put(this.internal[row][col], intersection);
                    }
                }
            }
        }
        return cells;
    }

    /**
     * Checks if there are unassigned cells inside this matrix.
     * A matrix with at least one unassigned cell is considered not to be consistent.
     */
    private boolean isConsistent() {
        boolean isConsistent = true;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (!(isConsistent &= (this.internal[i][j] != null)))
                    break;
            }
        }
        return isConsistent;
    }
}
