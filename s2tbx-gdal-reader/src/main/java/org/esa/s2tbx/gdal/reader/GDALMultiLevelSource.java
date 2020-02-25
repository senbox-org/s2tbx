package org.esa.s2tbx.gdal.reader;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.UncompressedTileOpImageCallback;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.nio.file.Path;

/**
 *  A single banded multi-level image source for products imported with the GDAL library.
 *
 * @author Jean Coravu
 */
class GDALMultiLevelSource extends AbstractMosaicSubsetMultiLevelSource implements UncompressedTileOpImageCallback<Void> {

    private final Path sourceLocalFile;
    private final int dataBufferType;
    private final int bandIndex;

    GDALMultiLevelSource(Path sourceLocalFile, int dataBufferType, Rectangle imageReadBounds, Dimension tileSize, int bandIndex, int levelCount, GeoCoding geoCoding) {
        super(levelCount, imageReadBounds, tileSize, geoCoding);

        this.sourceLocalFile = sourceLocalFile;
        this.dataBufferType = dataBufferType;
        this.bandIndex = bandIndex;
    }

    @Override
    public SourcelessOpImage buildTileOpImage(Rectangle imageCellReadBounds, int level, Point tileOffset, Dimension tileSize, Void tileData) {
        return new GDALTileOpImage(this.sourceLocalFile, this.bandIndex, getModel(), this.dataBufferType, imageCellReadBounds, tileSize, tileOffset, level);
    }

    @Override
    protected RenderedImage createImage(int level) {
        java.util.List<RenderedImage> tileImages = buildUncompressedTileImages(level, this.imageReadBounds, this.tileSize, 0.0f, 0.0f, this, null);
        if (tileImages.size() > 0) {
            return buildMosaicOp(level, tileImages,false);
        }
        return null;
    }
}
