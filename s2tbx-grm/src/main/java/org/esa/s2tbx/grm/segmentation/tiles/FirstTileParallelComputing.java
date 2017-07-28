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
public class FirstTileParallelComputing extends AbstractImageTilesParallelComputing {
    private final AbstractTileSegmenter tileSegmenter;
    private final Product sourceProduct;
    private final String[] sourceBandNames;

    public FirstTileParallelComputing(Product sourceProduct, String[] sourceBandNames, AbstractTileSegmenter tileSegmenter) {
        super(tileSegmenter.getImageWidth(), tileSegmenter.getImageHeight(), tileSegmenter.getTileWidth(), tileSegmenter.getTileHeight());

        this.tileSegmenter = tileSegmenter;
        this.sourceProduct = sourceProduct;
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
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            Tile tile = null;
            synchronized (this) {
                MultiLevelImage image = band.getSourceImage();
                Raster awtRaster = image.getData(rectangleToRead);
                tile = new TileImpl(band, awtRaster);
            }
            sourceTiles[i] = new TileDataSourceImpl(tile);
        }
        return sourceTiles;
    }
}