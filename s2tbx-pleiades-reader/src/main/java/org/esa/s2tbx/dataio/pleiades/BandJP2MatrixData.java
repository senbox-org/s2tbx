package org.esa.s2tbx.dataio.pleiades;

import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.jp2.reader.internal.JP2MatrixBandMultiLevelSource;

import javax.media.jai.ImageLayout;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by jcoravu on 11/8/2020.
 */
public class BandJP2MatrixData implements BandMatrixData {

    private final int dataType;
    private final int levelCount;
    private final MosaicMatrix mosaicMatrix;

    public BandJP2MatrixData(int dataType, int levelCount, MosaicMatrix mosaicMatrix) {
        this.dataType = dataType;
        this.levelCount = levelCount;
        this.mosaicMatrix = mosaicMatrix;
    }

    public int getDataType() {
        return dataType;
    }

    @Override
    public int getLevelCount() {
        return levelCount;
    }

    @Override
    public MosaicMatrix getMosaicMatrix() {
        return mosaicMatrix;
    }

    @Override
    public DefaultMultiLevelImage buildBandSourceImage(int bandLevelCount, double noDataValue, Dimension defaultJAIReadTileSize, int bandIndex,
                                                       Rectangle bandBounds, GeoCoding bandGeoCoding, AffineTransform imageToModelTransform) {

        JP2MatrixBandMultiLevelSource multiLevelSource = new JP2MatrixBandMultiLevelSource(getLevelCount(), getMosaicMatrix(), bandBounds, imageToModelTransform, bandIndex,
                                                                                           noDataValue, null, defaultJAIReadTileSize);
        ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
        return new DefaultMultiLevelImage(multiLevelSource, imageLayout);
    }
}
