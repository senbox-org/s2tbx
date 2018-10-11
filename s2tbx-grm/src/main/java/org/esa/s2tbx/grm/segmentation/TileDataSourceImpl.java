package org.esa.s2tbx.grm.segmentation;

import org.esa.snap.core.gpf.Tile;

/**
 * @author  Jean Coravu
 */
public class TileDataSourceImpl implements TileDataSource {
    private final Tile tile;

    public TileDataSourceImpl(Tile tile) {
        this.tile = tile;
    }

    @Override
    public float getSampleFloat(int x, int y) {
        return this.tile.getSampleFloat(x, y);
    }
}
