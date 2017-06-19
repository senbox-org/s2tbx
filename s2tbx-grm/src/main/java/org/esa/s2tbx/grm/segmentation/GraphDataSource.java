package org.esa.s2tbx.grm.segmentation;

import org.esa.snap.core.gpf.Tile;

/**
 * @author Jean Coravu
 */
public class GraphDataSource {
    private final Tile sourceTile;

    public GraphDataSource(Tile sourceTile) {
        this.sourceTile = sourceTile;
    }

    public float getSampleFloat(int x, int y) {
        return this.sourceTile.getSampleFloat(x, y);
    }
}
