package org.esa.s2tbx.dataio.s2.tiles;

import org.esa.snap.core.image.BandMatrixCell;

import java.awt.image.DataBuffer;

/**
 * Created by jcoravu on 10/1/2020.
 */
public class TileIndexBandMatrixCell implements BandMatrixCell {

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

    @Override
    public int getDataBufferType() {
        return DataBuffer.TYPE_SHORT;
    }

    public short getBandValue() {
        return this.bandValue;
    }
}
