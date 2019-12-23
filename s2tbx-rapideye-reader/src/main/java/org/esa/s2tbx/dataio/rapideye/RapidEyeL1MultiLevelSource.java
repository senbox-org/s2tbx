package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.nitf.NITFReaderWrapper;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;
import java.awt.image.RenderedImage;

/**
 * Created by jcoravu on 23/12/2019.
 */
public class RapidEyeL1MultiLevelSource extends AbstractMosaicSubsetMultiLevelSource<Void> {

    private final NITFReaderWrapper nitfReader;

    public RapidEyeL1MultiLevelSource(NITFReaderWrapper nitfReader, Rectangle visibleImageBounds, Dimension tileSize, GeoCoding geoCoding) {
        super(visibleImageBounds, tileSize, geoCoding);

        this.nitfReader = nitfReader;
    }

    @Override
    protected SourcelessOpImage buildTileOpImage(Rectangle visibleBounds, int level, Point tileOffset, Dimension tileSize, Void tileData) {
        return new RapidEyeL1TileOpImage(this.nitfReader, getModel(), 1, 1, visibleBounds, tileSize, tileOffset, level);
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
