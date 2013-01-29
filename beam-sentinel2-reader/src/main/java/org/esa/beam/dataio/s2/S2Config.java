package org.esa.beam.dataio.s2;

import org.esa.beam.framework.datamodel.ProductData;

import java.awt.image.DataBuffer;

/**
 * @author Norman Fomferra
 */
public interface S2Config {
    boolean DEBUG = Boolean.getBoolean("org.esa.beam.dataio.s2.S2Config.DEBUG");

    /**
     * Path to "opj_decompress" executable from OpenJPEG 2.0.0 package
     */
    String OPJ_DECOMPRESSOR_EXE = System.getProperty("openjpeg2.decompressor.path", "opj_decompress");

    int DEFAULT_JAI_TILE_SIZE = 512;

    int SAMPLE_PRODUCT_DATA_TYPE = ProductData.TYPE_UINT16;
    int SAMPLE_DATA_BUFFER_TYPE = DataBuffer.TYPE_USHORT;
    int SAMPLE_BYTE_COUNT = 2;

    short FILL_CODE_NO_FILE = DEBUG ? (short) 1000 : 0;
    short FILL_CODE_NO_INTERSECTION = DEBUG ? (short) 1 : 0;
    short FILL_CODE_OUT_OF_X_BOUNDS = DEBUG ? (short) 2 : 0;
    short FILL_CODE_OUT_OF_Y_BOUNDS = DEBUG ? (short) 3 : 0;
    short FILL_CODE_MOSAIC_BG = DEBUG ? (short) 4 : 0;

    // these numbers should actually been read from the JP2 files,
    // because they are likely to change if prod. spec. changes
    //
    L1cTileLayout[] L1C_TILE_LAYOUTS = new L1cTileLayout[]{
            new L1cTileLayout(10960, 10960, 4096, 4096, 3, 3, 6),
            new L1cTileLayout(5480, 5480, 4096, 4096, 2, 2, 6),
            new L1cTileLayout(1826, 1826, 1826, 1826, 1, 1, 6),
    };

    /**
     * Only used, if metadata header (manifest file) is not found.
     */
    S2WavebandInfo[] S2_WAVEBAND_INFOS = new S2WavebandInfo[]{
            new S2WavebandInfo(0, "B1", S2SpatialResolution.R60M, 443, 20, 1895.27, 3413, 1.030577302),
            new S2WavebandInfo(1, "B2", S2SpatialResolution.R10M, 490, 65, 1962.16, 3413, 1.030577302),
            new S2WavebandInfo(2, "B3", S2SpatialResolution.R10M, 560, 35, 1822.88, 3413, 1.030577302),
            new S2WavebandInfo(3, "B4", S2SpatialResolution.R10M, 665, 30, 1511.88, 3413, 1.030577302),
            new S2WavebandInfo(4, "B5", S2SpatialResolution.R20M, 705, 15, 1420.58, 3413, 1.030577302),
            new S2WavebandInfo(5, "B6", S2SpatialResolution.R20M, 740, 15, 1292.17, 3413, 1.030577302),
            new S2WavebandInfo(6, "B7", S2SpatialResolution.R20M, 775, 20, 1165.87, 3413, 1.030577302),
            new S2WavebandInfo(7, "B8", S2SpatialResolution.R10M, 842, 115, 1037.44, 3413, 1.030577302),
            new S2WavebandInfo(8, "B8a", S2SpatialResolution.R20M, 865, 20, 959.53, 3413, 1.030577302),
            new S2WavebandInfo(9, "B9", S2SpatialResolution.R60M, 940, 20, 814.1, 3413, 1.030577302),
            new S2WavebandInfo(10, "B10", S2SpatialResolution.R60M, 1380, 30, 363.67, 3413, 1.030577302),
            new S2WavebandInfo(11, "B11", S2SpatialResolution.R20M, 1610, 90, 246.28, 3413, 1.030577302),
            new S2WavebandInfo(12, "B12", S2SpatialResolution.R20M, 2190, 180, 86.98, 3413, 1.030577302),
    };

    String FORMAT_NAME = "SENTINEL-2-MSI-L1C";
    String MTD_EXT = ".xml";
    String JP2_EXT = ".jp2";
}
