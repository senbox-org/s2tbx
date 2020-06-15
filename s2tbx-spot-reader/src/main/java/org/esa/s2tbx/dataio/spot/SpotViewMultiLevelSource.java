package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.ImageReadBoundsSupport;
import org.esa.snap.core.image.UncompressedTileOpImageCallback;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;
import java.awt.image.RenderedImage;

/**
 * Created by jcoravu on 11/12/2019.
 */
public class SpotViewMultiLevelSource extends AbstractMosaicSubsetMultiLevelSource implements UncompressedTileOpImageCallback<Void> {

    private final SpotViewImageReader spotViewImageReader;
    private final int dataBufferType;
    private final int bandIndex;
    private final int bandCount;
    private final Double noDataValue;

    public SpotViewMultiLevelSource(SpotViewImageReader spotViewImageReader, int dataBufferType, Rectangle imageReadBounds, Dimension tileSize,
                                    int bandIndex, int bandCount, GeoCoding geoCoding, Double noDataValue) {

        super(imageReadBounds, tileSize, geoCoding);

        this.spotViewImageReader = spotViewImageReader;
        this.dataBufferType = dataBufferType;
        this.bandIndex = bandIndex;
        this.bandCount = bandCount;
        this.noDataValue = noDataValue;
    }

    @Override
    public SourcelessOpImage buildTileOpImage(ImageReadBoundsSupport imageReadBoundsSupport, int tileWidth, int tileHeight,
                                              int tileOffsetFromReadBoundsX, int tileOffsetFromReadBoundsY, Void tileData) {

        return new SpotViewTileOpImage(this.spotViewImageReader, this.dataBufferType, this.bandIndex, this.bandCount,
                                       tileWidth, tileHeight, tileOffsetFromReadBoundsX, tileOffsetFromReadBoundsY, imageReadBoundsSupport);
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
}
