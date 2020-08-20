package org.esa.s2tbx.dataio.pleiades;

import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixMultiLevelSource;

import javax.media.jai.ImageLayout;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by jcoravu on 11/8/2020.
 */
public class BandGeoTiffMatrixData implements BandMatrixData {

    private final int dataType;
    private final int levelCount;
    private final MosaicMatrix mosaicMatrix;

    public BandGeoTiffMatrixData(int dataType, int levelCount, MosaicMatrix mosaicMatrix) {
        this.dataType = dataType;
        this.levelCount = levelCount;
        this.mosaicMatrix = mosaicMatrix;
    }

    public int getDataType() {
        return dataType;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public MosaicMatrix getMosaicMatrix() {
        return mosaicMatrix;
    }

    @Override
    public DefaultMultiLevelImage buildBandSourceImage(int bandLevelCount, double noDataValue, Dimension defaultJAIReadTileSize, int bandIndex,
                                                       Rectangle bandBounds, GeoCoding bandGeoCoding, AffineTransform imageToModelTransform) {
        GeoTiffMatrixMultiLevelSource multiLevelSource = new GeoTiffMatrixMultiLevelSource(bandLevelCount, mosaicMatrix, bandBounds, bandIndex, bandGeoCoding, null, defaultJAIReadTileSize);
        ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
        return new DefaultMultiLevelImage(multiLevelSource, imageLayout);
    }
}
