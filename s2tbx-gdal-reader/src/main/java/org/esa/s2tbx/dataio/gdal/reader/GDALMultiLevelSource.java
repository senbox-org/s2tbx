package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.drivers.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.DecompressedTileOpImageCallback;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.nio.file.Path;

/**
 * A single banded multi-level image source for products imported with the GDAL library.
 *
 * @author Jean Coravu
 * @author Adrian Draghici
 */
class GDALMultiLevelSource extends AbstractMosaicSubsetMultiLevelSource implements DecompressedTileOpImageCallback<Void> {

    private final Path sourceLocalFile;
    private final int dataBufferType;
    private final int bandIndex;
    private final Double noDataValue;
    private final Dimension defaultImageSize;

    GDALMultiLevelSource(Path sourceLocalFile, int dataBufferType, Dimension defaultImageSize, Rectangle imageReadBounds, Dimension tileSize, int bandIndex, int levelCount, GeoCoding geoCoding, Double noDataValue) {
        super(levelCount, imageReadBounds, tileSize, geoCoding);

        this.sourceLocalFile = sourceLocalFile;
        this.dataBufferType = dataBufferType;
        this.defaultImageSize = defaultImageSize;
        this.bandIndex = bandIndex;
        this.noDataValue = noDataValue;
    }

    @Override
    public SourcelessOpImage buildTileOpImage(Dimension decompresedTileSize, Dimension tileSize, Point tileOffsetFromDecompressedImage, Point tileOffsetFromImage, int decompressTileIndex, int level, Void tileData) {

        if (tileOffsetFromDecompressedImage.x >= tileOffsetFromImage.x) {
            tileOffsetFromDecompressedImage.x = tileOffsetFromDecompressedImage.x - tileOffsetFromImage.x;
        }
        if (tileOffsetFromDecompressedImage.y >= tileOffsetFromImage.y) {
            tileOffsetFromDecompressedImage.y = tileOffsetFromDecompressedImage.y - tileOffsetFromImage.y;
        }

        Rectangle imageReadBounds = new Rectangle(tileOffsetFromImage, decompresedTileSize);
        Band gdalband = GDALTileOpImage.getGDALLevelBand(this.sourceLocalFile, this.bandIndex, level);
        Dimension subTileSize = new Dimension(Math.min(tileSize.width, gdalband.getBlockXSize()), Math.min(tileSize.height, gdalband.getBlockYSize()));
        return new GDALTileOpImage(this.sourceLocalFile, this.bandIndex, getModel(), this.dataBufferType, imageReadBounds, tileSize, subTileSize, tileOffsetFromDecompressedImage, level);
    }

    @Override
    protected RenderedImage createImage(int level) {
        java.util.List<RenderedImage> tileImages = buildDecompressedTileImages(level, this.imageReadBounds, this.tileSize, this.defaultImageSize.width, 0.0f, 0.0f, this, null);
        if (!tileImages.isEmpty()) {
            return buildMosaicOp(level, tileImages, false);
        }
        return null;
    }

    @Override
    protected double[] getMosaicOpBackgroundValues() {
        if (this.noDataValue == null) {
            return super.getMosaicOpBackgroundValues();
        }
        return new double[]{this.noDataValue};
    }
}
