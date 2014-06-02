package org.esa.beam.dataio.s3.synergy;

import java.awt.Rectangle;

interface TileRectangleCalculator {

    Rectangle[] calculateTileRectangles(int columnCount, int rowCount);
}
