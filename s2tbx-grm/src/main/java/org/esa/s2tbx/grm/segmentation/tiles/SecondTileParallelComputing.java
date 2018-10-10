package org.esa.s2tbx.grm.segmentation.tiles;

import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.io.IOException;

/**
 * @author Jean Coravu
 */
public class SecondTileParallelComputing extends AbstractImageTilesParallelComputing {
    private final AbstractTileSegmenter tileSegmenter;
    private final int iteration;
    private final int numberOfNeighborLayers;

    public SecondTileParallelComputing(int iteration, int numberOfNeighborLayers, AbstractTileSegmenter tileSegmenter) {
        super(tileSegmenter.getImageWidth(), tileSegmenter.getImageHeight(), tileSegmenter.getTileWidth(), tileSegmenter.getTileHeight());

        this.tileSegmenter = tileSegmenter;
        this.iteration = iteration;
        this.numberOfNeighborLayers = numberOfNeighborLayers;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException, InterruptedException {
        this.tileSegmenter.runTileSecondSegmentation(this.iteration, localRowIndex, localColumnIndex, this.numberOfNeighborLayers);
    }
}