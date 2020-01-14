package org.esa.s2tbx.dataio.s2.tiles;

import org.esa.snap.core.image.MosaicMatrix;

/**
 * Created by jcoravu on 10/1/2020.
 */
public class TileIndexBandMatrixCell implements MosaicMatrix.MatrixCell {

    private final int cellWidth;
    private final int cellHeight;
    private final short bandValue;

    public TileIndexBandMatrixCell(int cellWidth, int cellHeight, short bandValue) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.bandValue = bandValue;
    }

    @Override
    public int getCellWidth() {
        return this.cellWidth;
    }

    @Override
    public int getCellHeight() {
        return this.cellHeight;
    }

    public short getBandValue() {
        return this.bandValue;
    }
}
