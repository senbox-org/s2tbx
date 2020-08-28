package org.esa.s2tbx.dataio.pleiades;

import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.MosaicMatrix;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by jcoravu on 11/8/2020.
 */
public interface BandMatrixData {

    public int getLevelCount();

    public MosaicMatrix getMosaicMatrix();

    public DefaultMultiLevelImage buildBandSourceImage(int bandLevelCount, double noDataValue, Dimension defaultJAIReadTileSize, int bandIndex,
                                                       Rectangle bandBounds, GeoCoding bandGeoCoding, AffineTransform imageToModelTransform);
}
