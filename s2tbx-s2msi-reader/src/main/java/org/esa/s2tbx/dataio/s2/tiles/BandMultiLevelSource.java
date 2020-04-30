package org.esa.s2tbx.dataio.s2.tiles;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.snap.jp2.reader.internal.JP2TileOpImage;
import org.esa.snap.core.image.AbstractMatrixMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.DecompressedTileOpImageCallback;
import org.esa.snap.core.image.MosaicMatrix;
import javax.media.jai.SourcelessOpImage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;

/**
 * @author Denisa Stefanescu
 */
public class BandMultiLevelSource extends AbstractMatrixMosaicSubsetMultiLevelSource implements DecompressedTileOpImageCallback<S2MosaicBandMatrixCell> {

    public BandMultiLevelSource(int levelCount, MosaicMatrix mosaicMatrix, Rectangle imageMatrixReadBounds, AffineTransform imageToModelTransform) {
        super(levelCount, mosaicMatrix, imageMatrixReadBounds, new Dimension(1, 1), imageToModelTransform);
    }

    @Override
    public SourcelessOpImage buildTileOpImage(Dimension decompresedTileSize, Dimension tileSize, Point tileOffsetFromDecompressedImage, Point tileOffsetFromImage, int decompressTileIndex, int level, S2MosaicBandMatrixCell matrixCell) {
        return new JP2TileOpImage(matrixCell.getJp2ImageFile(), matrixCell.getCacheDir(), getModel(), decompresedTileSize, matrixCell.getBandCount(), matrixCell.getBandIndex(),
                                        matrixCell.getDataBufferType(), tileSize, tileOffsetFromDecompressedImage, tileOffsetFromImage, decompressTileIndex, level);
    }

    @Override
    protected java.util.List<RenderedImage> buildMatrixCellTileImages(int level, Rectangle imageCellReadBounds, float cellTranslateLevelOffsetX, float cellTranslateLevelOffsetY,
                                                                      MosaicMatrix.MatrixCell matrixCell) {
        S2MosaicBandMatrixCell mosaicMatrixCell = (S2MosaicBandMatrixCell)matrixCell;
        return buildDecompressedTileImages(level, imageCellReadBounds, mosaicMatrixCell.getDecompressedTileSize(), mosaicMatrixCell.getDefaultImageSize().width ,cellTranslateLevelOffsetX, cellTranslateLevelOffsetY, this, mosaicMatrixCell);
    }

    @Override
    protected double[] getMosaicOpBackgroundValues() {
        return new double[]{S2Config.FILL_CODE_MOSAIC_BG};
    }

    @Override
    protected double[][] getMosaicOpSourceThreshold() {
        return new double[][]{{1.0}};
    }
}
