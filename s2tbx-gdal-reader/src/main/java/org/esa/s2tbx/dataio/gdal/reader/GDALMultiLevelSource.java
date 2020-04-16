package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.drivers.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.DecompressedTileOpImageCallback;
import org.esa.snap.core.image.UncompressedTileOpImageCallback;
import org.esa.snap.core.util.ImageUtils;

import javax.media.jai.PlanarImage;
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
class GDALMultiLevelSource extends AbstractMosaicSubsetMultiLevelSource implements UncompressedTileOpImageCallback<Void> {

    private final Path sourceLocalFile;
    private final int dataBufferType;
    private final int bandIndex;
    private final Double noDataValue;

    GDALMultiLevelSource(Path sourceLocalFile, int dataBufferType, Rectangle imageReadBounds, Dimension tileSize, int bandIndex, int levelCount, GeoCoding geoCoding, Double noDataValue) {
        super(levelCount, imageReadBounds, tileSize, geoCoding);

        this.sourceLocalFile = sourceLocalFile;
        this.dataBufferType = dataBufferType;
        this.bandIndex = bandIndex;
        this.noDataValue = noDataValue;
    }


    @Override
    public PlanarImage buildTileOpImage(Rectangle imageCellReadBounds, int level, Point tileOffsetFromCellReadBounds, Dimension tileSize, Void tileData) {
        return new GDALTileOpImage(this.sourceLocalFile, this.bandIndex, getModel(), this.dataBufferType, this.imageReadBounds, tileSize, tileOffsetFromCellReadBounds, level);
    }

    @Override
    protected RenderedImage createImage(int level) {
        java.util.List<RenderedImage> tileImages = buildUncompressedTileImages(level, this.imageReadBounds, this.tileSize, 0.0f, 0.0f, this, null);
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
