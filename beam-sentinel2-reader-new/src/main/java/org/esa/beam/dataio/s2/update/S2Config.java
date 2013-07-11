package org.esa.beam.dataio.s2.update;

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

    short RAW_NO_DATA_THRESHOLD = DEBUG ? (short) 4 : (short) 1;

    // these numbers should actually been read from the JP2 files,
    // because they are likely to change if prod. spec. changes
    //
    TileLayout[] TILE_LAYOUTS = new TileLayout[]{
            new TileLayout(10960, 10960, 4096, 4096, 3, 3, 6),
            new TileLayout(5480, 5480, 4096, 4096, 2, 2, 6),
            new TileLayout(1826, 1826, 1826, 1826, 1, 1, 6),
    };

    String FORMAT_NAME = "SENTINEL-2-MSI-L1C";
    String MTD_EXT = ".xml";
    String JP2_EXT = ".jp2";
}
