package org.esa.s2tbx.grm.segmentation.tiles;

import com.bc.ceres.glevel.MultiLevelImage;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.DifferenceTileDataSourceImpl;
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
public class DifferenceFirstTileParallelComputing extends AbstractImageTilesParallelComputing {
    private final AbstractTileSegmenter tileSegmenter;
    private final Product currentSourceProduct;
    private final Product previousSourceProduct;
    private final String[] currentSourceBandNames;
    private final String[] previousSourceBandNames;

    public DifferenceFirstTileParallelComputing(Product currentSourceProduct, String[] currentSourceBandNames, Product previousSourceProduct,
                                                String[] previousSourceBandNames, AbstractTileSegmenter tileSegmenter) {

        super(tileSegmenter.getImageWidth(), tileSegmenter.getImageHeight(), tileSegmenter.getTileWidth(), tileSegmenter.getTileHeight());

        this.tileSegmenter = tileSegmenter;
        this.currentSourceProduct = currentSourceProduct;
        this.previousSourceProduct = previousSourceProduct;
        this.currentSourceBandNames = currentSourceBandNames;
        this.previousSourceBandNames = previousSourceBandNames;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IllegalAccessException, IOException, InterruptedException {

        ProcessingTile currentTile = this.tileSegmenter.buildTile(tileLeftX, tileTopY, tileWidth, tileHeight);
        TileDataSource[] sourceTiles = buildSourceTiles(currentTile.getRegion());
        this.tileSegmenter.runTileFirstSegmentation(sourceTiles, currentTile, localRowIndex, localColumnIndex);
    }

    private TileDataSource[] buildSourceTiles(BoundingBox tileRegion) {
        TileDataSource[] sourceTiles = new TileDataSource[this.currentSourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        for (int i=0; i<this.currentSourceBandNames.length; i++) {
            Band currentBand = this.currentSourceProduct.getBand(this.currentSourceBandNames[i]);
            Band previousBand = this.previousSourceProduct.getBand(this.previousSourceBandNames[i]);
            Tile currentTile = null;
            Tile previousTile = null;
            synchronized (this) {
                currentTile = buildTile(currentBand, rectangleToRead);
                previousTile = buildTile(previousBand, rectangleToRead);
            }
            sourceTiles[i] = new DifferenceTileDataSourceImpl(currentTile, previousTile);
        }
        return sourceTiles;
    }

    private static Tile buildTile(Band band, Rectangle rectangleToRead) {
        MultiLevelImage image = band.getSourceImage();
        Raster awtRaster = image.getData(rectangleToRead);
        return new TileImpl(band, awtRaster);
    }
}