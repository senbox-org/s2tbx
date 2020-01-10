package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;
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

    public SpotViewMultiLevelSource(SpotViewImageReader spotViewImageReader, int dataBufferType, Rectangle imageReadBounds, Dimension tileSize,
                                    int bandIndex, int bandCount, GeoCoding geoCoding) {

        super(imageReadBounds, tileSize, geoCoding);

        this.spotViewImageReader = spotViewImageReader;
        this.dataBufferType = dataBufferType;
        this.bandIndex = bandIndex;
        this.bandCount = bandCount;
    }

    @Override
    public SourcelessOpImage buildTileOpImage(Rectangle imageCellReadBounds, int level, Point tileOffset, Dimension tileSize, Void tileData) {
        return new SpotViewTileOpImage(this.spotViewImageReader, getModel(), this.dataBufferType, this.bandIndex, this.bandCount, imageCellReadBounds, tileSize, tileOffset, level);
    }

    @Override
    protected RenderedImage createImage(int level) {
        java.util.List<RenderedImage> tileImages = buildUncompressedTileImages(level, this.imageReadBounds, this.tileSize, 0.0f, 0.0f, this, null);
        if (tileImages.size() > 0) {
            return buildMosaicOp(level, tileImages, false);
        }
        return null;
    }
}
