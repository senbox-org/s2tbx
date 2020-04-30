package org.esa.s2tbx.dataio.s2.tiles;

import org.esa.snap.core.image.MosaicMatrix;

/**
 * Created by jcoravu on 30/4/2020.
 */
public interface BandMatrixCell extends MosaicMatrix.MatrixCell {

    public int getDataBufferType();
}
