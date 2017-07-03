package org.esa.s2tbx.grm.segmentation.tiles;

import com.bc.ceres.glevel.MultiLevelImage;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.internal.TileImpl;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.io.IOException;

/**
 * @author  Jean Coravu
 */
public class TileFirstSegmentationHelper extends AbstractTileSegmentationHelper {
    private Product sourceProduct;
    private String[] sourceBandNames;

    public TileFirstSegmentationHelper(Product sourceProduct, String[] sourceBandNames, AbstractTileSegmenter tileSegmenter) {
        super(tileSegmenter);

        this.sourceProduct = sourceProduct;
        this.sourceBandNames = sourceBandNames;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException {
        ProcessingTile currentTile = this.tileSegmenter.buildTile(tileLeftX, tileTopY, tileWidth, tileHeight);
        Tile[] sourceTiles = buildSourceTiles(currentTile.getRegion());
        this.tileSegmenter.runOneTileFirstSegmentation(sourceTiles, currentTile);
    }

    private Tile[] buildSourceTiles(BoundingBox tileRegion) {
        Tile[] sourceTiles = new Tile[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            MultiLevelImage image = band.getSourceImage();
            Raster awtRaster = image.getData(rectangleToRead);
            sourceTiles[i] = new TileImpl(band, awtRaster);
        }
        return sourceTiles;
    }
}