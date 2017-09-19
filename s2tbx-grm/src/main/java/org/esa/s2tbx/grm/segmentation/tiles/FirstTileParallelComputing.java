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
    private final SegmentationSourceProductPair[] segmentationSourcePairs;

    public FirstTileParallelComputing(SegmentationSourceProductPair[] segmentationSourcePairs, AbstractTileSegmenter tileSegmenter) {
        super(tileSegmenter.getImageWidth(), tileSegmenter.getImageHeight(), tileSegmenter.getTileWidth(), tileSegmenter.getTileHeight());

        this.tileSegmenter = tileSegmenter;
        this.segmentationSourcePairs = segmentationSourcePairs;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex) throws IllegalAccessException, IOException, InterruptedException {
        ProcessingTile currentTile = this.tileSegmenter.buildTile(tileLeftX, tileTopY, tileWidth, tileHeight);
        TileDataSource[] sourceTiles = buildSourceTiles(currentTile.getRegion());
        this.tileSegmenter.runTileFirstSegmentation(sourceTiles, currentTile, localRowIndex, localColumnIndex);
    }

    private TileDataSource[] buildSourceTiles(BoundingBox tileRegion) {
        int totalBandCount = 0;
        for (int i=0; i<this.segmentationSourcePairs.length; i++) {
            totalBandCount += this.segmentationSourcePairs[i].getSourceBandNames().length;
        }

        TileDataSource[] sourceTiles = new TileDataSource[totalBandCount];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        int index = 0;
        for (int k=0; k<this.segmentationSourcePairs.length; k++) {
            String[] sourceBandNames = this.segmentationSourcePairs[k].getSourceBandNames();
            Product sourceProduct = this.segmentationSourcePairs[k].getSourceProduct();
            for (int i=0; i<sourceBandNames.length; i++) {
                Band band = sourceProduct.getBand(sourceBandNames[i]);
                Tile tile = null;
                synchronized (this) {
                    MultiLevelImage image = band.getSourceImage();
                    Raster awtRaster = image.getData(rectangleToRead);
                    tile = new TileImpl(band, awtRaster);
                }
                sourceTiles[index++] = new TileDataSourceImpl(tile);
            }
        }
        return sourceTiles;
    }
}