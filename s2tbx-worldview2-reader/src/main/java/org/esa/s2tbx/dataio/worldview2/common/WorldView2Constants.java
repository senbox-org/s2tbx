package org.esa.s2tbx.dataio.worldview2.common;

import java.io.File;

/**
 * Holder class for string constants.
 *
 * @author Razvan Dumitrascu
 */

public class WorldView2Constants {

    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String[] FORMAT_NAMES = new String[]{"WorldView2GeoTIFF"};
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml"};
    public static final String DESCRIPTION = "WorldView-2 Data Products";
    public static final String IMAGE_EXTENSION = ".TIF";
    public static final String PRODUCT_GENERIC_NAME = "WorldView-2 Product";
    public static final String PRODUCT_TYPE = "WorldView-2 Product";
    public static final String METADATA_EXTENSION = ".XML";
    public static final String[] NATURAL_COLORS =
            new String[]{"Red", "Green", "Blue"};
    public static final String[] BAND_NAMES_MULTISPECTRAL_4_BANDS =
            new String[]{"Blue", "Green", "Red", "NIR1", "Pan"};
    public static final String[] BAND_NAMES_MULTISPECTRAL_8_BANDS =
            new String[]{"Coastal", "Blue", "Green", "Yellow", "Red", "Red Edge", "NIR1", "NIR2", "Pan"};

    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[] {
            ".*_README\\.XML"};

    public static final String[] WORLDVIEW2_RGB_PROFILE = new String[] { "Red", "Green", "Blue" };
    public static final String WORLDVIEW2_UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    /*
     * Package (volume) metadata element paths
     */
    public static final String PATH_FILE_LIST = "/README/FILELIST/FILE";
    public static final String PATH_START_TIME = "/README/COLLECTIONSTART";
    public static final String PATH_END_TIME = "/README/COLLECTIONSTOP";

   /*
    * Raster metadata element paths
    */
    public static final String PATH_NUM_ROWS = "/isd/imd/@numrows";
    public static final String PATH_NUM_COLUMNS = "/isd/imd/@numcolumns";
    public static final String PATH_BITS_PER_PIXEL = "/isd/imd/@bitsperpixel";
    public static final String PATH_BAND_ID = "/isd/til/@bandid";
    public static final String PATH_ORIGIN_X = "/isd/imd/map_projected_product/@originx";
    public static final String PATH_ORIGIN_Y = "/isd/imd/map_projected_product/@originy";
    public static final String PATH_PIXEL_STEP_SIZE = "/isd/imd/map_projected_product/@productgsd";
    public static final String PATH_MAP_ZONE = "/isd/imd/map_projected_product/@mapzone";
    public static final String PATH_MAP_HEMISPHERE = "/isd/imd/map_projected_product/@maphemi";

    public static final String PATH_NUMBER_OF_TILES = "/isd/til/@numtiles";
    public static final String PATH_TILE_FILENAME= "/isd/til/tile/@filename";

    /**
     * each tile location (x, y coordinates)
     */
    public static final String PATH_UPPER_LEFT_COLUMN_OFFSET = "/isd/til/tile/@ulcoloffset";
    public static final String PATH_UPPER_LEFT_ROW_OFFSET = "/isd/til/tile/@ulrowoffset";
    public static final String PATH_UPPER_RIGHT_COLUMN_OFFSET = "/isd/til/tile/@urcoloffset";
    public static final String PATH_UPPER_RIGHT_ROW_OFFSET = "/isd/til/tile/@urrowoffset";
    public static final String PATH_LOWER_LEFT_COLUMN_OFFSET = "/isd/til/tile/@lrcoloffset";
    public static final String PATH_LOWER_LEFT_ROW_OFFSET = "/isd/til/tile/@lrrowoffset";
    public static final String PATH_LOWER_RIGHT_COLUMN_OFFSET = "/isd/til/tile/@llcoloffset";
    public static final String PATH_LOWER_RIGHT_ROW_OFFSET = "/isd/til/tile/@llrowoffset";



}
