package org.esa.s2tbx.dataio.spot;

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
 * Created by jcoravu on 11/12/2019.
 */
public class SpotViewMultiLevelSource extends AbstractMosaicSubsetMultiLevelSource implements UncompressedTileOpImageCallback<Void>, SpotViewBandSource {

    private final SpotViewImageReader spotViewImageReader;
    private final int dataBufferType;
    private final int bandIndex;
    private final int bandCount;
    private final Double noDataValue;
    private final Dimension defaultJAIReadTileSize;

    public SpotViewMultiLevelSource(SpotViewImageReader spotViewImageReader, int dataBufferType, Rectangle imageReadBounds, Dimension tileSize,
                                    int bandIndex, int bandCount, GeoCoding geoCoding, Double noDataValue, Dimension defaultJAIReadTileSize) {

        super(imageReadBounds, tileSize, geoCoding);

        this.spotViewImageReader = spotViewImageReader;
        this.dataBufferType = dataBufferType;
        this.bandIndex = bandIndex;
        this.bandCount = bandCount;
        this.noDataValue = noDataValue;
        this.defaultJAIReadTileSize = defaultJAIReadTileSize;
    }

    @Override
    protected ImageLayout buildMosaicImageLayout(int level) {
        return null; // no image layout to configure the mosaic image since the tile images are configured
    }

    @Override
    public SourcelessOpImage buildTileOpImage(ImageReadBoundsSupport imageReadBoundsSupport, int tileWidth, int tileHeight,
                                              int tileOffsetFromReadBoundsX, int tileOffsetFromReadBoundsY, Void tileData) {

        return new SpotViewTileOpImage(this.spotViewImageReader, this, this.dataBufferType, tileWidth, tileHeight,
                                       tileOffsetFromReadBoundsX, tileOffsetFromReadBoundsY, imageReadBoundsSupport, this.defaultJAIReadTileSize);
    }

    @Override
    protected RenderedImage createImage(int level) {
        java.util.List<RenderedImage> tileImages = buildUncompressedTileImages(level, this.imageReadBounds, this.tileSize.width, this.tileSize.height, 0.0f, 0.0f, this, null);
        if (tileImages.size() > 0) {
            return buildMosaicOp(level, tileImages, false);
        }
        return null;
    }

    @Override
    protected double[] getMosaicOpBackgroundValues() {
        if (this.noDataValue == null) {
            return super.getMosaicOpBackgroundValues();
        }
        return new double[] { this.noDataValue.doubleValue() };
    }

    @Override
    public int getBandIndex() {
        return this.bandIndex;
    }

    @Override
    public int getBandCount() {
        return this.bandCount;
    }

    public ImageLayout buildMultiLevelImageLayout() {
        int topLeftTileWidth = computeTopLeftUncompressedTileWidth(this.imageReadBounds, this.tileSize.width);
        int topLeftTileHeight = computeTopLeftUncompressedTileHeight(this.imageReadBounds, this.tileSize.height);
        return ImageUtils.buildImageLayout(this.dataBufferType, this.imageReadBounds.width, this.imageReadBounds.height, 0, this.defaultJAIReadTileSize, topLeftTileWidth, topLeftTileHeight);
    }
}
