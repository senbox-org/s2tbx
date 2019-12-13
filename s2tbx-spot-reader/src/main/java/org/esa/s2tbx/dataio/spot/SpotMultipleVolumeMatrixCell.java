package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;

/**
 * Created by jcoravu on 13/12/2019.
 */
public class SpotMultipleVolumeMatrixCell implements MosaicMatrix.MatrixCell {

    private final int cellWidth;
    private final int cellHeight;
    private final GeoTiffImageReader geoTiffImageReader;
    private final int dataBufferType;

    public SpotMultipleVolumeMatrixCell(int cellWidth, int cellHeight, GeoTiffImageReader geoTiffImageReader, int dataBufferType) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.geoTiffImageReader = geoTiffImageReader;
        this.dataBufferType = dataBufferType;
    }

    @Override
    public int getCellWidth() {
        return cellWidth;
    }

    @Override
    public int getCellHeight() {
        return cellHeight;
    }

    public int getDataBufferType() {
        return dataBufferType;
    }

    public GeoTiffImageReader getGeoTiffImageReader() {
        return geoTiffImageReader;
    }
}

