package org.esa.s2tbx.dataio.worldview2esa;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMatrixMosaicSubsetMultiLevelSource;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.dataio.geotiff.GeoTiffTileOpImage;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;

/**
 * Created by jcoravu on 3/1/2020.
 */
public class WorldViewESAMosaicMultiLevelSource extends AbstractMatrixMosaicSubsetMultiLevelSource {

    private final int bandIndex;

    public WorldViewESAMosaicMultiLevelSource(MosaicMatrix spotBandMatrix, Rectangle imageMatrixReadBounds, Dimension tileSize, int bandIndex, GeoCoding geoCoding) {
        super(spotBandMatrix, imageMatrixReadBounds, tileSize, geoCoding);

        this.bandIndex = bandIndex;
    }

    @Override
    protected SourcelessOpImage buildTileOpImage(Rectangle imageCellReadBounds, int level, Point tileOffset, Dimension tileSize, MosaicMatrix.MatrixCell matrixCell) {
        WorldViewESAMatrixCell volumeMatrixCell = (WorldViewESAMatrixCell)matrixCell;
        return new GeoTiffTileOpImage(volumeMatrixCell.getGeoTiffImageReader(), getModel(), volumeMatrixCell.getDataBufferType(), this.bandIndex, imageCellReadBounds, tileSize, tileOffset, level, false);
    }
}
