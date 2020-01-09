package org.esa.s2tbx.dataio.s2.l1b.tiles;

import org.esa.s2tbx.dataio.jp2.internal.CopyOfJP2TileOpImage;
import org.esa.snap.core.image.AbstractMatrixMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.DecompressedTileOpImageCallback;
import org.esa.snap.core.image.MosaicMatrix;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.util.List;

/**
 * Created by jcoravu on 8/1/2020.
 */
public class CopyOfBandL1bSceneMultiLevelSource extends AbstractMatrixMosaicSubsetMultiLevelSource implements DecompressedTileOpImageCallback<L1bMosaicMatrixCell> {

    public CopyOfBandL1bSceneMultiLevelSource(int levelCount, MosaicMatrix mosaicMatrix, Rectangle imageMatrixReadBounds, AffineTransform imageToModelTransform) {
        super(levelCount, mosaicMatrix, imageMatrixReadBounds, null, imageToModelTransform);
    }

    @Override
    protected List<RenderedImage> buildMatrixCellTileImages(int level, Rectangle imageCellReadBounds, float translateLevelOffsetX, float translateLevelOffsetY, MosaicMatrix.MatrixCell matrixCell) {
        L1bMosaicMatrixCell l1bMosaicMatrixCell = (L1bMosaicMatrixCell)matrixCell;
        return buildDecompressedTileImages(level, imageCellReadBounds, l1bMosaicMatrixCell.getDecompresedTileSize(), l1bMosaicMatrixCell.getDefaultImageSize().width,
                                           translateLevelOffsetX, translateLevelOffsetY, this, l1bMosaicMatrixCell);
    }

    @Override
    public SourcelessOpImage buildTileOpImage(Dimension decompresedTileSize, Dimension tileSize, Point tileOffsetFromDecompressedImage,
                                              Point tileOffsetFromImage, int decompressTileIndex, int level, L1bMosaicMatrixCell l1bMosaicMatrixCell) {

        return new CopyOfJP2TileOpImage(l1bMosaicMatrixCell.getJp2ImageFile(), l1bMosaicMatrixCell.getCacheDir(), getModel(), decompresedTileSize, l1bMosaicMatrixCell.getBandCount(),
                                        l1bMosaicMatrixCell.getBandIndex(), l1bMosaicMatrixCell.getDataBufferType(), tileSize, tileOffsetFromDecompressedImage,
                                        tileOffsetFromImage, decompressTileIndex, level);
    }
}
