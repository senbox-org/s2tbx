package org.esa.s2tbx.dataio.spot6;

import org.esa.snap.core.image.AbstractMatrixMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.DecompressedTileOpImageCallback;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.jp2.reader.internal.JP2TileOpImage;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;

/**
 * Created by jcoravu on 9/4/2020.
 */
public class JP2MatrixBandMultiLevelSource extends AbstractMatrixMosaicSubsetMultiLevelSource implements DecompressedTileOpImageCallback<JP2MosaicBandMatrixCell> {

    private final int bandIndex;
    private final Double mosaicOpSourceThreshold;
    private final Double mosaicOpBackgroundValue;

    public JP2MatrixBandMultiLevelSource(int levelCount, MosaicMatrix mosaicMatrix, Rectangle imageMatrixReadBounds, AffineTransform imageToModelTransform,
                                         int bandIndex, Double mosaicOpBackgroundValue, Double mosaicOpSourceThreshold) {

        super(levelCount, mosaicMatrix, imageMatrixReadBounds, new Dimension(1, 1), imageToModelTransform);

        this.bandIndex = bandIndex;
        this.mosaicOpBackgroundValue = mosaicOpBackgroundValue;
        this.mosaicOpSourceThreshold = mosaicOpSourceThreshold;
    }

    @Override
    public SourcelessOpImage buildTileOpImage(Dimension decompresedTileSize, Dimension tileSize, Point tileOffsetFromDecompressedImage, Point tileOffsetFromImage,
                                              int decompressTileIndex, int level, JP2MosaicBandMatrixCell matrixCell) {

        return new JP2TileOpImage(matrixCell.getJp2ImageFile(), matrixCell.getLocalCacheFolder(), getModel(), decompresedTileSize, matrixCell.getBandCount(),
                                  this.bandIndex, matrixCell.getDataBufferType(), tileSize, tileOffsetFromDecompressedImage,
                                  tileOffsetFromImage, decompressTileIndex, level);
    }

    @Override
    protected java.util.List<RenderedImage> buildMatrixCellTileImages(int level, Rectangle imageCellReadBounds, float cellTranslateLevelOffsetX, float cellTranslateLevelOffsetY,
                                                                      MosaicMatrix.MatrixCell matrixCell) {

        JP2MosaicBandMatrixCell mosaicMatrixCell = (JP2MosaicBandMatrixCell)matrixCell;
        return buildDecompressedTileImages(level, imageCellReadBounds, mosaicMatrixCell.getDecompressedTileSize(), mosaicMatrixCell.getDefaultImageSize().width,
                                           cellTranslateLevelOffsetX, cellTranslateLevelOffsetY, this, mosaicMatrixCell);
    }

    @Override
    protected double[] getMosaicOpBackgroundValues() {
        if (this.mosaicOpBackgroundValue == null) {
            return super.getMosaicOpBackgroundValues();
        }
        return new double[] { this.mosaicOpBackgroundValue.doubleValue() };
    }

    @Override
    protected double[][] getMosaicOpSourceThreshold() {
        if (this.mosaicOpSourceThreshold == null) {
            return super.getMosaicOpSourceThreshold();
        }
        return new double[][]{ {this.mosaicOpSourceThreshold.doubleValue()} };
    }
}
