package org.esa.s2tbx.dataio.jp2.internal;

import org.esa.s2tbx.dataio.jp2.JP2ImageFile;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;
import org.esa.snap.core.util.ImageUtils;

import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.SourcelessOpImage;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A single banded multi-level image source for JP2 files.
 *
 * @author Cosmin Cara
 */
public class CopyOfJP2MultiLevelSource extends AbstractMosaicSubsetMultiLevelSource<Void> {

    private final int dataBufferType;
    private final int bandIndex;
    private final JP2ImageFile jp2ImageFile;
    private final Path localCacheFolder;
    private final Dimension defaultImageSize;
    private final int bandCount;

    public CopyOfJP2MultiLevelSource(JP2ImageFile jp2ImageFile, Path localCacheFolder, Dimension defaultImageSize, Rectangle imageReadBounds, int bandCount, int bandIndex,
                                     Dimension decompresedTileSize, int levelCount, int dataBufferType, GeoCoding geoCoding) {

        super(levelCount, imageReadBounds, decompresedTileSize, geoCoding);

        this.jp2ImageFile = jp2ImageFile;
        this.localCacheFolder = localCacheFolder;
        this.defaultImageSize = defaultImageSize;
        this.dataBufferType = dataBufferType;
        this.bandCount = bandCount;
        this.bandIndex = bandIndex;
    }

    @Override
    protected SourcelessOpImage buildTileOpImage(Rectangle imageCellReadBounds, int level, Point tileOffsetFromCellReadBounds, Dimension tileSize, Void tileData) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    protected RenderedImage createImage(int level) {
        List<RenderedImage> tileImages = buildTileImages(level, this.imageReadBounds, this.tileSize, this.defaultImageSize.width, 0.0f, 0.0f);
        if (tileImages.size() > 0) {
            return buildMosaicOp(level, tileImages, true);
        }
        return null;
    }

    private java.util.List<RenderedImage> buildTileImages(int level, Rectangle imageCellReadBounds, Dimension decompresedTileSize, int defaultImageWidth,
                                                          float translateLevelOffsetX, float translateLevelOffsetY) {

        int startTileColumnIndex = imageCellReadBounds.x / decompresedTileSize.width;
        int endTileColumnIndex = computeEndTileIndex(startTileColumnIndex, imageCellReadBounds.x, imageCellReadBounds.width, decompresedTileSize.width);

        int startTileRowIndex = imageCellReadBounds.y / decompresedTileSize.height;
        int endTileRowIndex = computeEndTileIndex(startTileColumnIndex, imageCellReadBounds.y, imageCellReadBounds.height, decompresedTileSize.height);

        float levelImageWidth = computeLevelImageSize(imageCellReadBounds.width, (endTileColumnIndex - startTileColumnIndex) + 1, level);
        float levelImageHeight = computeLevelImageSize(imageCellReadBounds.height, (endTileRowIndex - startTileRowIndex) + 1, level);

        int defaultColumnTileCount = ImageUtils.computeTileCount(defaultImageWidth, decompresedTileSize.width);
        List<RenderedImage> tileImages = new ArrayList<>();
        float levelTranslateY = translateLevelOffsetX;
        int currentImageTileTopY = imageCellReadBounds.y;
        for (int tileRowIndex = startTileRowIndex; tileRowIndex <= endTileRowIndex; tileRowIndex++) {
            int currentTileHeight = computeTileSize(startTileRowIndex, endTileRowIndex, tileRowIndex, currentImageTileTopY, imageCellReadBounds.y, imageCellReadBounds.height, decompresedTileSize.height);
            int levelImageTileHeight = ImageUtils.computeLevelSize(currentTileHeight, level);
            int tileOffsetYFromDecompressedImage = currentImageTileTopY - (tileRowIndex * decompresedTileSize.height);
            if (tileOffsetYFromDecompressedImage < 0) {
                throw new IllegalStateException("The tile offset Y from the decompressed image file is negative.");
            }

            float levelTranslateX = translateLevelOffsetY;
            int currentImageTileLeftX = imageCellReadBounds.x;
            for (int tileColumnIndex = startTileColumnIndex; tileColumnIndex <= endTileColumnIndex; tileColumnIndex++) {
                int currentTileWidth = computeTileSize(startTileColumnIndex, endTileColumnIndex, tileColumnIndex, currentImageTileLeftX, imageCellReadBounds.x, imageCellReadBounds.width, decompresedTileSize.width);
                int levelImageTileWidth = ImageUtils.computeLevelSize(currentTileWidth, level);

                int tileOffsetXFromDecompressedImage = currentImageTileLeftX - (tileColumnIndex * decompresedTileSize.width);
                if (tileOffsetXFromDecompressedImage < 0) {
                    throw new IllegalStateException("The tile offset X from the decompressed image file is negative.");
                }

                int decompressTileIndex = tileColumnIndex + (tileRowIndex * defaultColumnTileCount);

                Dimension currentTileSize = new Dimension(currentTileWidth, currentTileHeight);
                Point tileOffsetFromDecompressedImage = new Point(tileOffsetXFromDecompressedImage, tileOffsetYFromDecompressedImage);
                Point tileOffsetFromImage = new Point(currentImageTileLeftX, currentImageTileTopY);

                CopyOfJP2TileOpImage tileOpImage = new CopyOfJP2TileOpImage(this.jp2ImageFile, this.localCacheFolder, getModel(), decompresedTileSize, this.bandCount, this.bandIndex,
                                                                            this.dataBufferType, currentTileSize, tileOffsetFromDecompressedImage, tileOffsetFromImage, decompressTileIndex, level);
                validateTileImageSize(tileOpImage, levelImageTileWidth, levelImageTileHeight);
                this.tileImageDisposer.registerForDisposal(tileOpImage);

                levelTranslateX = computeTranslateOffset(tileColumnIndex, endTileColumnIndex, levelTranslateX, tileOpImage.getWidth(), levelImageWidth);
                levelTranslateY = computeTranslateOffset(tileRowIndex, endTileRowIndex, levelTranslateY, tileOpImage.getHeight(), levelImageHeight);

                PlanarImage opImage = TranslateDescriptor.create(tileOpImage, levelTranslateX, levelTranslateY, Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);
                tileImages.add(opImage);

                levelTranslateX += (float) ImageUtils.computeLevelSizeAsDouble(currentTileWidth, level);
                currentImageTileLeftX += currentTileWidth;
            }

            levelTranslateY += (float) ImageUtils.computeLevelSizeAsDouble(currentTileHeight, level);
            currentImageTileTopY += currentTileHeight;
        }
        return tileImages;
    }

    private static float computeTranslateOffset(int currentTileIndex, int endTileIndex, float levelTranslateOffset, int levelImageTileSize, float levelImageTotalSize) {
        float translateOffset = levelTranslateOffset;
        if (translateOffset + levelImageTileSize > levelImageTotalSize) {
            if (currentTileIndex == endTileIndex) {
                if (levelImageTotalSize < levelImageTileSize) {
                    throw new IllegalStateException("Invalid values: imageLevelTotalSize="+levelImageTotalSize+", imageSize="+levelImageTileSize);
                }
                translateOffset = levelImageTotalSize - levelImageTileSize; // the last row or column
            } else {
                throw new IllegalStateException("Invalid values: levelTranslateOffset="+levelTranslateOffset+", levelImageTileSize="+levelImageTileSize+", imageLevelTotalSize="+levelImageTotalSize);
            }
        }
        if (translateOffset < 0.0f) {
            throw new IllegalStateException("The translate offset is negative: "+ translateOffset);
        }
        return translateOffset;
    }

    private static int computeEndTileIndex(int startTileIndex, int imageReadOffset, int imageReadSize, int tileSize) {
        int endTileIndex = startTileIndex;
        if (imageReadSize > tileSize) {
            int imageReadEndPosition = imageReadOffset + imageReadSize;
            endTileIndex = imageReadEndPosition / tileSize;
            if (imageReadEndPosition % tileSize == 0) {
                endTileIndex--;
            }
        }
        return endTileIndex;
    }

    private static int computeTileSize(int startTileIndex, int endTileIndex, int currentTileIndex, int currentImageTileOffset, int imageReadOffset, int imageReadSize, int tileSize) {
        int imageReadEndPosition = imageReadOffset + imageReadSize;
        int currentTileHeight = tileSize;
        if (currentTileIndex == startTileIndex) {
            // the first tile
            if (currentTileIndex == endTileIndex) {
                currentTileHeight = imageReadEndPosition - currentImageTileOffset; // only one tile
            } else {
                int tileEndPosition = (currentTileIndex + 1) * tileSize;
                currentTileHeight = tileEndPosition - currentImageTileOffset;
            }
        } else if (currentTileIndex == endTileIndex) {
            currentTileHeight = imageReadEndPosition - currentImageTileOffset; // the last tile
        }
        return currentTileHeight;
    }
}
