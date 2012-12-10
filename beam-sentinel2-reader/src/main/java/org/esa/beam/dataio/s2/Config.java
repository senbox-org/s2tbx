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

    S2WavebandInfo[] S2_WAVEBAND_INFOS = new S2WavebandInfo[]{
            new S2WavebandInfo(0, "B1", 443, 20, SpatialResolution.R60M),
            new S2WavebandInfo(1, "B2", 490, 65, SpatialResolution.R10M),
            new S2WavebandInfo(2, "B3", 560, 35, SpatialResolution.R10M),
            new S2WavebandInfo(3, "B4", 665, 30, SpatialResolution.R10M),
            new S2WavebandInfo(4, "B5", 705, 15, SpatialResolution.R20M),
            new S2WavebandInfo(5, "B6", 740, 15, SpatialResolution.R20M),
            new S2WavebandInfo(6, "B7", 775, 20, SpatialResolution.R20M),
            new S2WavebandInfo(7, "B8", 842, 115, SpatialResolution.R10M),
            new S2WavebandInfo(8, "B8a", 865, 20, SpatialResolution.R20M),
            new S2WavebandInfo(9, "B9", 940, 20, SpatialResolution.R60M),
            new S2WavebandInfo(10, "B10", 1380, 30, SpatialResolution.R60M),
            new S2WavebandInfo(11, "B11", 1610, 90, SpatialResolution.R20M),
            new S2WavebandInfo(12, "B12", 2190, 180, SpatialResolution.R20M),
    };
}
