package org.esa.beam.dataio.s2;

import jp2.TileLayout;
import org.esa.beam.framework.datamodel.ProductData;
import org.openjpeg.JpegUtils;

import java.awt.image.DataBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Norman Fomferra
 */
public interface S2L1bConfig {
    public boolean DEBUG = Boolean.getBoolean("org.esa.beam.dataio.s2.S2Config.DEBUG");
    public boolean NODUMP = Boolean.getBoolean("org.esa.beam.dataio.s2.S2Config.NODUMP");

    /**
     * Path to "opj_decompress" executable from OpenJPEG 2.1.0 package
     */

    // fixme paramatrize log levels
    String LOG_JPEG = DEBUG ? "INFO" : "FINEST";
    String LOG_SCENE = DEBUG ? "INFO" : "FINEST";
    String LOG_OPS = DEBUG ? "FINE" : "FINEST";
    String LOG_DEBUG = DEBUG ? "WARNING" : "FINEST";

    String OPJ_DECOMPRESSOR_EXE = JpegUtils.getExecutable(L1bMetadataProc.tryGetModulesDir());
    String OPJ_INFO_EXE = JpegUtils.getInfoExecutable(L1bMetadataProc.tryGetModulesDir());

    int DEFAULT_JAI_TILE_SIZE = 512;

    int SAMPLE_PRODUCT_DATA_TYPE = ProductData.TYPE_UINT16;
    int SAMPLE_DATA_BUFFER_TYPE = DataBuffer.TYPE_USHORT;
    int SAMPLE_BYTE_COUNT = 2;

    short FILL_CODE_NO_FILE = DEBUG ? (short) 1000 : 0;
    short FILL_CODE_NO_INTERSECTION = DEBUG ? (short) 1 : 0;
    short FILL_CODE_OUT_OF_X_BOUNDS = DEBUG ? (short) 2 : 0;
    short FILL_CODE_OUT_OF_Y_BOUNDS = DEBUG ? (short) 3 : 0;
    short FILL_CODE_MOSAIC_BG = DEBUG ? (short) 4 : 0;
    short FILL_CODE_DEBUG = DEBUG ? (short) 1000 : 1000;

    short RAW_NO_DATA_THRESHOLD = DEBUG ? (short) 4 : (short) 1;

    //todo these numbers should actually been read from the JP2 files, because they are likely to change if prod. spec. changes
    //todo use classes from jp2 package to read the data
    //todo future improvement: use opj_dump.exe to retrieve the data

    TileLayout[] L1B_TILE_LAYOUTS = new TileLayout[]{
            new TileLayout(2548, 2304, 1024, 1024, 3, 3, 6), // 10
            new TileLayout(1274, 1152, 1024, 1024, 2, 2, 6), // 20
            new TileLayout(424, 384, 424, 384, 1, 1, 6), // 60
    };

    Set<TileLayout> REAL_TILE_LAYOUT = new HashSet<>();

    Map<Integer, Integer> LAYOUTMAP = new HashMap<Integer, Integer>() {
        {
            put(10, 0);
            put(20, 1);
            put(60, 2);
        }
        ;
    };

    String FORMAT_NAME = "SENTINEL-2-MSI-L1B";
    String MTD_EXT = ".xml";
}
