package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.nitf.NITFReaderWrapper;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.ImageReadBoundsSupport;
import org.esa.snap.core.image.UncompressedTileOpImageCallback;
import org.esa.snap.core.util.ImageUtils;

import javax.media.jai.ImageLayout;
import javax.media.jai.SourcelessOpImage;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;

/**
 * Created by jcoravu on 23/12/2019.
 */
public class RapidEyeL1MultiLevelSource extends AbstractMosaicSubsetMultiLevelSource implements UncompressedTileOpImageCallback<Void> {

    private final NITFReaderWrapper nitfReader;
    private final int dataBufferType;
    private final Dimension defaultJAIReadTileSize;

    public RapidEyeL1MultiLevelSource(NITFReaderWrapper nitfReader, int dataBufferType, Rectangle imageReadBounds, Dimension tileSize, GeoCoding geoCoding,
                                      Dimension defaultJAIReadTileSize) {

        super(imageReadBounds, tileSize, geoCoding);

        this.nitfReader = nitfReader;
        this.dataBufferType = dataBufferType;
        this.defaultJAIReadTileSize = defaultJAIReadTileSize;
    }

    @Override
    protected ImageLayout buildMosaicImageLayout(int level) {
        return null; // no image layout to configure the mosaic image since the tile images are configured
    }

    @Override
    public SourcelessOpImage buildTileOpImage(ImageReadBoundsSupport imageReadBoundsSupport, int tileWidth, int tileHeight,
                                              int tileOffsetFromReadBoundsX, int tileOffsetFromReadBoundsY, Void tileData) {

        return new RapidEyeL1TileOpImage(this.nitfReader, this.dataBufferType, tileWidth, tileHeight, tileOffsetFromReadBoundsX,
                                         tileOffsetFromReadBoundsY, imageReadBoundsSupport, this.defaultJAIReadTileSize);
    }

    @Override
    protected RenderedImage createImage(int level) {
        java.util.List<RenderedImage> tileImages = buildUncompressedTileImages(level, this.imageReadBounds, this.tileSize.width, this.tileSize.height, 0.0f, 0.0f, this, null);
        if (tileImages.size() > 0) {
            return buildMosaicOp(level, tileImages, false);
        }
        return null;
    }

    public ImageLayout buildMultiLevelImageLayout() {
        int topLeftTileWidth = computeTopLeftUncompressedTileWidth(this.imageReadBounds, this.tileSize.width);
        int topLeftTileHeight = computeTopLeftUncompressedTileHeight(this.imageReadBounds, this.tileSize.height);
        return ImageUtils.buildImageLayout(this.dataBufferType, this.imageReadBounds.width, this.imageReadBounds.height, 0, this.defaultJAIReadTileSize, topLeftTileWidth, topLeftTileHeight);
    }
}
