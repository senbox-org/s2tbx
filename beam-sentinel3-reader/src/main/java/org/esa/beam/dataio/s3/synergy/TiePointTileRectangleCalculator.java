package org.esa.beam.dataio.s3.synergy;

import org.esa.beam.util.math.MathUtils;

import java.awt.Rectangle;

class TiePointTileRectangleCalculator implements TileRectangleCalculator {

    @Override
    public Rectangle[] calculateTileRectangles(int columnCount, int rowCount) {
        final int tileCountX = 1;
        final int tileCountY = rowCount / columnCount;

        return MathUtils.subdivideRectangle(columnCount, rowCount, tileCountX, tileCountY, 1);
    }
}
