package org.esa.s2tbx.grm.segmentation;

import org.esa.snap.core.gpf.Tile;

/**
 * @author  Jean Coravu
 */
public class DifferenceTileDataSourceImpl implements TileDataSource {
    private final Tile currentTile;
    private final Tile previousTile;

    public DifferenceTileDataSourceImpl(Tile currentTile, Tile previousTile) {
        this.currentTile = currentTile;
        this.previousTile = previousTile;
    }

    @Override
    public float getSampleFloat(int x, int y) {
        //return this.currentTile.getSampleFloat(x, y) - this.previousTile.getSampleFloat(x, y);

        //TODO Jean remove
        float a = this.currentTile.getSampleFloat(x, y);
        return a - this.previousTile.getSampleFloat(x, y);
    }
}