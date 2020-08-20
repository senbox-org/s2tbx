package org.esa.s2tbx.dataio.s2.tiles;

import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.snap.core.image.MosaicMatrix;

/**
 * Created by jcoravu on 10/1/2020.
 */
public interface MosaicMatrixCellCallback {

    public MosaicMatrix.MatrixCell buildMatrixCell(String tileId, Sentinel2ProductReader.BandInfo tileBandInfo, int sceneCellWidth, int sceneCellHeight);
}
