package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMatrixMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.dataio.geotiff.GeoTiffTileOpImage;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;

/**
 * Created by jcoravu on 13/12/2019.
 */
public class SpotMultipleVolumeMosaicMultiLevelSource extends AbstractMatrixMosaicSubsetMultiLevelSource {

    private final int bandIndex;

    public SpotMultipleVolumeMosaicMultiLevelSource(MosaicMatrix spotBandMatrix, Rectangle imageMatrixReadBounds, Dimension tileSize, int bandIndex, GeoCoding geoCoding) {
        super(spotBandMatrix, imageMatrixReadBounds, tileSize, geoCoding);

        this.bandIndex = bandIndex;
    }

    @Override
    protected SourcelessOpImage buildTileOpImage(Rectangle imageCellReadBounds, int level, Point tileOffset, Dimension tileSize, MosaicMatrix.MatrixCell matrixCell) {
        SpotMultipleVolumeMatrixCell volumeMatrixCell = (SpotMultipleVolumeMatrixCell)matrixCell;
        return new GeoTiffTileOpImage(volumeMatrixCell.getGeoTiffImageReader(), getModel(), volumeMatrixCell.getDataBufferType(), this.bandIndex, imageCellReadBounds, tileSize, tileOffset, level, false);
    }
}
