package org.esa.beam.dataio.s2;

import org.esa.beam.framework.datamodel.ProductData;

/**
 * @author Norman Fomferra
 */
public interface Config {
    String OPJ_DECOMPRESSOR_EXE = System.getProperty("openjpeg2.decompressor.path", "opj_decompress");

    int DEFAULT_TILE_SIZE = 512;

    int SAMPLE_DATA_TYPE = ProductData.TYPE_UINT16;
    int SAMPLE_ELEM_SIZE = 2;

    short FILL_CODE_NO_FILE = (short) 1000;
    short FILL_CODE_NO_INTERSECTION = (short) 1;
    short FILL_CODE_OUT_OF_X_BOUNDS = (short) 2;
    short FILL_CODE_OUT_OF_Y_BOUNDS = (short) 3;
    short FILL_CODE_MOSAIC_BG = (short) 4;

    // these numbers should actually been read from the JP2 files,
    // because they are likely to change if prod. spec. changes
    //
    L1cTileLayout[] L1C_TILE_LAYOUTS = new L1cTileLayout[]{
            new L1cTileLayout(10690, 10690, 4096, 4096, 3, 3, 6),
            new L1cTileLayout(5480, 5480, 4096, 4096, 2, 2, 6),
            new L1cTileLayout(1826, 1826, 1826, 1826, 1, 1, 6),
    };

    /**
     * Only used, if manifest header is not found.
     */
    S2WavebandInfo[] S2_WAVEBAND_INFOS = new S2WavebandInfo[]{
            new S2WavebandInfo(0, "B1", SpatialResolution.R60M, 443, 20, 1895.27, 3413),
            new S2WavebandInfo(1, "B2", SpatialResolution.R10M, 490, 65, 1962.16, 3413),
            new S2WavebandInfo(2, "B3", SpatialResolution.R10M, 560, 35, 1822.88, 3413),
            new S2WavebandInfo(3, "B4", SpatialResolution.R10M, 665, 30, 1511.88, 3413),
            new S2WavebandInfo(4, "B5", SpatialResolution.R20M, 705, 15, 1420.58, 3413),
            new S2WavebandInfo(5, "B6", SpatialResolution.R20M, 740, 15, 1292.17, 3413),
            new S2WavebandInfo(6, "B7", SpatialResolution.R20M, 775, 20, 1165.87, 3413),
            new S2WavebandInfo(7, "B8", SpatialResolution.R10M, 842, 115, 1037.44, 3413),
            new S2WavebandInfo(8, "B8a", SpatialResolution.R20M, 865, 20, 959.53, 3413),
            new S2WavebandInfo(9, "B9", SpatialResolution.R60M, 940, 20, 814.1, 3413),
            new S2WavebandInfo(10, "B10", SpatialResolution.R60M, 1380, 30, 363.67, 3413),
            new S2WavebandInfo(11, "B11", SpatialResolution.R20M, 1610, 90, 246.28, 3413),
            new S2WavebandInfo(12, "B12", SpatialResolution.R20M, 2190, 180, 86.98, 3413),
    };
}
