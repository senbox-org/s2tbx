package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;
import java.awt.image.RenderedImage;

/**
 * Created by jcoravu on 11/12/2019.
 */
public class SpotViewMultiLevelSource extends AbstractMosaicSubsetMultiLevelSource<Void> {

    private final SpotViewImageReader spotViewImageReader;
    private final int dataBufferType;
    private final int bandIndex;

    public SpotViewMultiLevelSource(SpotViewImageReader spotViewImageReader, int dataBufferType, Rectangle visibleImageBounds, Dimension tileSize, int bandIndex, GeoCoding geoCoding) {
        super(visibleImageBounds, tileSize, geoCoding);

        this.spotViewImageReader = spotViewImageReader;
        this.dataBufferType = dataBufferType;
        this.bandIndex = bandIndex;
    }

    @Override
    protected SourcelessOpImage buildTileOpImage(Rectangle visibleBounds, int level, Point tileOffset, Dimension tileSize, Void tileData) {
        return new SpotViewTileOpImage(this.spotViewImageReader, getModel(), this.dataBufferType, this.bandIndex, visibleBounds, tileSize, tileOffset, level);
    }

    @Override
    protected RenderedImage createImage(int level) {
        java.util.List<RenderedImage> tileImages = buildTileImages(level, this.visibleImageBounds, 0.0f, 0.0f, null);
        if (tileImages.size() > 0) {
            return buildMosaicOp(level, tileImages);
        }
        return null;
    }
}
