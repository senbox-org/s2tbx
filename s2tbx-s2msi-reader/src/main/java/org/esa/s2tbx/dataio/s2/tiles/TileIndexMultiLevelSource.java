package org.esa.s2tbx.dataio.s2.tiles;

import org.esa.snap.core.image.AbstractMatrixMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.ImageReadBoundsSupport;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.core.image.UncompressedTileOpImageCallback;
import org.esa.snap.core.util.ImageUtils;

import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.ConstantDescriptor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;

/**
 * Created by jcoravu on 9/1/2020.
 */
public class TileIndexMultiLevelSource extends AbstractMatrixMosaicSubsetMultiLevelSource implements UncompressedTileOpImageCallback<TileIndexBandMatrixCell> {

    private final Double mosaicOpSourceThreshold;
    private final double mosaicOpBackgroundValue;

    public TileIndexMultiLevelSource(int levelCount, MosaicMatrix mosaicMatrix, Rectangle imageMatrixReadBounds, Dimension preferredTileSize,
                                     AffineTransform imageToModelTransform, Double mosaicOpSourceThreshold, double mosaicOpBackgroundValue) {

        super(levelCount, mosaicMatrix, imageMatrixReadBounds, preferredTileSize, imageToModelTransform);

        this.mosaicOpSourceThreshold = mosaicOpSourceThreshold; // the threshold value may be null
        this.mosaicOpBackgroundValue = mosaicOpBackgroundValue;
    }

    @Override
    protected ImageLayout buildMosaicImageLayout(int level) {
        // create the image layout object of the mosaic image because the tiles are not confired with sub-tiles
        return ImageUtils.buildImageLayout(null, this.imageReadBounds.width, this.imageReadBounds.height, level, this.tileSize);
    }

    @Override
    public PlanarImage buildTileOpImage(ImageReadBoundsSupport imageReadBoundsSupport, int tileWidth, int tileHeight,
                                        int tileOffsetFromReadBoundsX, int tileOffsetFromReadBoundsY, TileIndexBandMatrixCell matrixCell) {

        int levelImageTileWidth = ImageUtils.computeLevelSize(tileWidth, imageReadBoundsSupport.getLevel());
        int levelImageTileHeight = ImageUtils.computeLevelSize(tileHeight, imageReadBoundsSupport.getLevel());
        // the constant tile image is not configured with sub-tiles
        return ConstantDescriptor.create((float)levelImageTileWidth, (float)levelImageTileHeight, new Short[]{matrixCell.getBandValue()}, null);
    }

    @Override
    protected java.util.List<RenderedImage> buildMatrixCellTileImages(int level, Rectangle imageCellReadBounds, float cellTranslateLevelOffsetX, float cellTranslateLevelOffsetY,
                                                                      MosaicMatrix.MatrixCell matrixCell) {

        TileIndexBandMatrixCell tileIndexBandMatrixCell = (TileIndexBandMatrixCell)matrixCell;
        return buildUncompressedTileImages(level, imageCellReadBounds, imageCellReadBounds.width, imageCellReadBounds.height,
                                           cellTranslateLevelOffsetX, cellTranslateLevelOffsetY, this, tileIndexBandMatrixCell);
    }

    @Override
    protected double[] getMosaicOpBackgroundValues() {
        return new double[]{this.mosaicOpBackgroundValue};
    }

    @Override
    protected double[][] getMosaicOpSourceThreshold() {
        if (this.mosaicOpSourceThreshold == null) {
            return super.getMosaicOpSourceThreshold();
        }
        return new double[][]{ {this.mosaicOpSourceThreshold.doubleValue()} };
    }
}
