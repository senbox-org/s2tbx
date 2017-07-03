package org.esa.s2tbx.grm.segmentation.tiles;

import java.io.IOException;

/**
 * @author  Jean Coravu
 */
public class TileStabilityMarginSecondSegmentationHelper extends AbstractTileSegmentationHelper {
    private final int iteration;
    private final int numberOfNeighborLayers;

    public TileStabilityMarginSecondSegmentationHelper(int iteration, int numberOfNeighborLayers, AbstractTileSegmenter tileSegmenter) {
        super(tileSegmenter);

        this.iteration = iteration;
        this.numberOfNeighborLayers = numberOfNeighborLayers;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException {
        this.tileSegmenter.runTileStabilityMarginSecondSegmentation(this.iteration, localRowIndex, localColumnIndex, this.numberOfNeighborLayers);
    }
}