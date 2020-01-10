package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.nitf.NITFReaderWrapper;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.UncompressedTileOpImageCallback;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;
import java.awt.image.RenderedImage;

/**
 * Created by jcoravu on 23/12/2019.
 */
public class RapidEyeL1MultiLevelSource extends AbstractMosaicSubsetMultiLevelSource implements UncompressedTileOpImageCallback<Void> {

    private final NITFReaderWrapper nitfReader;
    private final int dataBufferType;

    public RapidEyeL1MultiLevelSource(NITFReaderWrapper nitfReader, int dataBufferType, Rectangle imageReadBounds, Dimension tileSize, GeoCoding geoCoding) {
        super(imageReadBounds, tileSize, geoCoding);

        this.nitfReader = nitfReader;
        this.dataBufferType = dataBufferType;
    }

    @Override
    public SourcelessOpImage buildTileOpImage(Rectangle imageCellReadBounds, int level, Point tileOffsetFromReadBounds, Dimension tileSize, Void tileData) {
        return new RapidEyeL1TileOpImage(this.nitfReader, getModel(), dataBufferType, imageCellReadBounds, tileSize, tileOffsetFromReadBounds, level);
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
