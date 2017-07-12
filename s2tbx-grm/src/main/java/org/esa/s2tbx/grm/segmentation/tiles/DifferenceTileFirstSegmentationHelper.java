package org.esa.s2tbx.grm.segmentation.tiles;

import com.bc.ceres.glevel.MultiLevelImage;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.s2tbx.grm.segmentation.TileDataSourceImpl;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.internal.TileImpl;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.io.IOException;

/**
 * @author Jean Coravu
 */
public class DifferenceTileFirstSegmentationHelper extends AbstractImageTilesParallelComputing {
    private final AbstractTileSegmenter tileSegmenter;
    private final Product currentSourceProduct;
    private final Product previousSourceProduct;
    private final String[] sourceBandNames;

    public DifferenceTileFirstSegmentationHelper(Product currentSourceProduct, Product previousSourceProduct, String[] sourceBandNames, AbstractTileSegmenter tileSegmenter) {
        super(tileSegmenter.getImageWidth(), tileSegmenter.getImageHeight(), tileSegmenter.getTileWidth(), tileSegmenter.getTileHeight());

        this.tileSegmenter = tileSegmenter;
        this.currentSourceProduct = currentSourceProduct;
        this.previousSourceProduct = previousSourceProduct;
        this.sourceBandNames = sourceBandNames;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex) throws IllegalAccessException, IOException, InterruptedException {
        ProcessingTile currentTile = this.tileSegmenter.buildTile(tileLeftX, tileTopY, tileWidth, tileHeight);
        TileDataSource[] sourceTiles = buildSourceTiles(currentTile.getRegion());
        this.tileSegmenter.runTileFirstSegmentation(sourceTiles, currentTile);
    }

    private TileDataSource[] buildSourceTiles(BoundingBox tileRegion) {
        TileDataSource[] sourceTiles = new TileDataSource[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band currentBand = this.currentSourceProduct.getBand(this.sourceBandNames[i]);
            Tile currentTile = buildTile(currentBand, rectangleToRead);
            Band previousBand = this.previousSourceProduct.getBand(this.sourceBandNames[i]);
            Tile previousTile = buildTile(previousBand, rectangleToRead);
            sourceTiles[i] = new DifferenceTileDataSourceImpl(currentTile, previousTile);
        }
        return sourceTiles;
    }

    private static Tile buildTile(Band band, Rectangle rectangleToRead) {
        MultiLevelImage image = band.getSourceImage();
        Raster awtRaster = image.getData(rectangleToRead);
        return new TileImpl(band, awtRaster);
    }

    private static class DifferenceTileDataSourceImpl implements TileDataSource {
        private final Tile currentTile;
        private final Tile previousTile;

        public DifferenceTileDataSourceImpl(Tile currentTile, Tile previousTile) {
            this.currentTile = currentTile;
            this.previousTile = previousTile;
        }

        @Override
        public float getSampleFloat(int x, int y) {
            return this.currentTile.getSampleFloat(x, y) - this.previousTile.getSampleFloat(x, y);
        }
    }

}