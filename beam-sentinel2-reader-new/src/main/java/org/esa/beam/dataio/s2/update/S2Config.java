package org.esa.beam.dataio.s2.update;

import org.esa.beam.framework.datamodel.ProductData;

import java.awt.image.DataBuffer;
import java.util.regex.Pattern;

/**
 * @author Norman Fomferra
 */
public interface S2Config {
    boolean DEBUG = Boolean.getBoolean("org.esa.beam.dataio.s2.S2Config.DEBUG");

    /**
     * Path to "opj_decompress" executable from OpenJPEG 2.0.0 package
     */
    String OPJ_DECOMPRESSOR_EXE = System.getProperty("openjpeg2.decompressor.path", "opj_decompress");

    final String dir1CRegex =
            "(S2.?)_([A-Z]{4})_PRD_MSIL1C_R([0-9]{3})_V([0-9]{8})T([0-9]{6})_([0-9]{8})T([0-9]{6})_C([0-9]{3}).(DIMAP|SAFE)";
    final Pattern PRODUCT_DIRECTORY_1C_PATTERN = Pattern.compile(dir1CRegex);

    final Pattern TILE_DIRECTORY_1C_PATTERN = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_(L1C)_TL_.*_(\\d{2}[A-Z]{3})");
    final Pattern DIRECTORY_1C_PATTERN_ALT = Pattern.compile("Level-1C_User_Product");
    final Pattern TILE_DIRECTORY_2A_PATTERN = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_(L2A)_TL_.*_(\\d{2}[A-Z]{3})");
    final Pattern DIRECTORY_2A_PATTERN_ALT = Pattern.compile("Level-2A_User_Product");
    final static String metadataName1CRegex =
            "(S2.?)_([A-Z]{4})_MTD_(DMP|SAF)(L1C)_R([0-9]{3})_V([0-9]{8})T([0-9]{6})_([0-9]{8})T([0-9]{6})_C([0-9]{3}).*.xml";
    final static Pattern METADATA_NAME_1C_PATTERN = Pattern.compile(metadataName1CRegex);
    final static Pattern METADATA_NAME_1C_PATTERN_ALT = Pattern.compile("Product_Metadata_File.xml");
    final String metadataName2ARegex =
            "((S2.?)_([A-Z]{4})_MTD_(DMP|SAF)L2A_R([0-9]{3})_V([0-9]{8})(T|0)([0-9]{6})_([0-9]{8})T([0-9]{6})_C([0-9]{3}).*.xml)";
    final static Pattern METADATA_NAME_2A_PATTERN = Pattern.compile(metadataName2ARegex);
    final static Pattern METADATA_NAME_1C_TILE_PATTERN = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_L1C_TL_.*.xml");
    final static Pattern METADATA_NAME_2A_TILE_PATTERN = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_L2A_TL_.*.xml");
    final static Pattern IMAGE_NAME_PATTERN =
            Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_(L2A|L1C)_TL_.*_(\\d{2}[A-Z]{3})_(|AOT|WVP|DEM|B[0-9A]{2})(_([1-6]{1}0)m)?.jp2");
    //todo use only one pattern
    final static Pattern SPECIFICATION_MASK_IMAGE_NAME_PATTERN =
            Pattern.compile("S2.?_([A-Z]{4})_(MSK)_(CLOUDS|TECQUA|LANWAT|DETFOO|DEFECT|SATURA|NODATA)_.*(\\d{2}[A-Z]{3}).jp2");

    //use this pattern to read preview image
//    final static Pattern USED_MASK_IMAGE_NAME_PATTERN =
//            Pattern.compile("S2.?_([A-Z]{4})_(MSK|PVI)_(L2A|CLD|SNW)_TL_.*_(\\d{2}[A-Z]{3}).jp2");

    final static Pattern USED_MASK_IMAGE_NAME_PATTERN =
            Pattern.compile("S2.?_([A-Z]{4})_(MSK)_(CLD|SNW)_TL_.*_(\\d{2}[A-Z]{3}).jp2");

    int DEFAULT_JAI_TILE_SIZE = 512;

    int SAMPLE_PRODUCT_DATA_TYPE = ProductData.TYPE_UINT16;
    int SAMPLE_PRODUCT_MASK_DATA_TYPE = ProductData.TYPE_UINT8;
    int SAMPLE_DATA_BUFFER_TYPE = DataBuffer.TYPE_USHORT;
    int SAMPLE_MASK_DATA_BUFFER_TYPE = DataBuffer.TYPE_BYTE;
    int SAMPLE_BYTE_COUNT = 2;
    int SAMPLE_MASK_BYTE_COUNT = 1;

    short FILL_CODE_NO_FILE = DEBUG ? (short) 1000 : 0;
    byte FILL_CODE_NO_FILE_BYTE = DEBUG ? (byte) 1000 : 0;
    short FILL_CODE_NO_INTERSECTION = DEBUG ? (short) 1 : 0;
    byte FILL_CODE_NO_INTERSECTION_BYTE = DEBUG ? (byte) 1 : 0;
    short FILL_CODE_OUT_OF_X_BOUNDS = DEBUG ? (short) 2 : 0;
    byte FILL_CODE_OUT_OF_X_BOUNDS_BYTE = DEBUG ? (byte) 2 : 0;
    short FILL_CODE_OUT_OF_Y_BOUNDS = DEBUG ? (short) 3 : 0;
    byte FILL_CODE_OUT_OF_Y_BOUNDS_BYTE = DEBUG ? (byte) 3 : 0;

    short FILL_CODE_MOSAIC_BG = DEBUG ? (short) 4 : 0;

    short RAW_NO_DATA_THRESHOLD = DEBUG ? (short) 4 : (short) 1;

    // these numbers should actually been read from the JP2 files,
    // because they are likely to change if prod. spec. changes
    //
    TileLayout[] TILE_LAYOUTS = new TileLayout[]{
            new TileLayout(3093, 9483, 3093, 9483, 1, 1, 6),
            new TileLayout(1546, 4741, 1546, 4741, 1, 1, 6),
            new TileLayout(515, 1580, 515, 1580, 1, 1, 6),
    };

    TileLayout[] TILE_LAYOUTS_ORIG = new TileLayout[]{
            new TileLayout(10960, 10960, 4096, 4096, 3, 3, 6),
            new TileLayout(5480, 5480, 4096, 4096, 2, 2, 6),
            new TileLayout(1826, 1826, 1826, 1826, 1, 1, 6),
    };

    String FORMAT_NAME = "SENTINEL-2-MSI";
    String MTD_EXT = ".xml";
    String JP2_EXT = ".jp2";
}
