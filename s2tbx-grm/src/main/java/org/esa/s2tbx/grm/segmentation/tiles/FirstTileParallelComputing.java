package org.esa.s2tbx.grm.segmentation.tiles;

import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.OutputMarkerMatrixHelper;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.awt.Rectangle;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * @author Jean Coravu
 */
public class FirstTileParallelComputing extends AbstractImageTilesParallelComputing {
    private final AbstractTileSegmenter tileSegmenter;
    private final SegmentationSourceProductPair segmentationSourceProducts;

    public FirstTileParallelComputing(SegmentationSourceProductPair segmentationSourceProducts, AbstractTileSegmenter tileSegmenter) {
        super(tileSegmenter.getImageWidth(), tileSegmenter.getImageHeight(), tileSegmenter.getTileWidth(), tileSegmenter.getTileHeight());

        this.tileSegmenter = tileSegmenter;
        this.segmentationSourceProducts = segmentationSourceProducts;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IllegalAccessException, IOException, InterruptedException {

        ProcessingTile currentTile = this.tileSegmenter.buildTile(tileLeftX, tileTopY, tileWidth, tileHeight);
        TileDataSource[] sourceTiles = this.segmentationSourceProducts.buildSourceTiles(tileLeftX, tileTopY, tileWidth, tileHeight, localRowIndex, localColumnIndex, currentTile.getRegion());
        this.tileSegmenter.runTileFirstSegmentation(sourceTiles, currentTile, localRowIndex, localColumnIndex);

        WeakReference<TileDataSource[]> referenceSourceTiles = new WeakReference<TileDataSource[]>(sourceTiles);
        referenceSourceTiles.clear();
    }
}