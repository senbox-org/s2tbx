package org.esa.s2tbx.dataio.worldviewESAarchive.common;

import java.io.File;

/**
 * Holder class for constants.
 *
 * @author Denisa Stefanescu
 */

public class WorldViewESAarchiveConstants {

    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String[] FORMAT_NAMES = new String[]{"WorldViewGeoTIFF"};
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml", ".zip"};
    public static final String DESCRIPTION = "WorldView Data Products";
    public static final String PRODUCT_GENERIC_NAME = "WorldView Product";
    public static final String PRODUCT_TYPE = "WorldView Product";

    public static final String IMAGE_EXTENSION = ".TIF";
    public static final String METADATA_FILE_SUFFIX = ".MD.XML";
    public static final String PRODUCT_FILE_SUFFIX = ".SIP.ZIP";
    public static final String ARCHIVE_FILE_EXTENSION = ".ZIP";
    public static final String METADATA_EXTENSION = ".XML";
    public static final String DEFAULT_PIXEL_SIZE = "16";

    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[]{
            "WV2_\\w{4}_WV-\\d{3}_.*_\\d{8}T\\d{6}_\\w\\d{2}-\\d{3}_\\w\\d{3}-\\d{3}_\\d{4}\\.MD\\.XML",
            "WV2_\\w{4}_WV-\\d{3}_.*_\\d{8}T\\d{6}_\\w\\d{2}-\\d{3}_\\w\\d{3}-\\d{3}_\\d{4}.*\\.ZIP"};

    public static final String PATH_ZIP_FILE_NAME_PATTERN = "\\d{12}_\\d{2}_[A-Z]\\d{3}_[A-Z]{3}";

    public static final String[] NATURAL_COLORS =
            new String[]{"Red", "Green", "Blue"};
    public static final String[] BAND_NAMES_MULTISPECTRAL_4_BANDS =
            new String[]{"Blue", "Green", "Red", "NIR1", "Pan"};
    public static final String[] BAND_NAMES_MULTISPECTRAL_8_BANDS =
            new String[]{"Coastal", "Blue", "Green", "Yellow", "Red", "Red Edge", "NIR1", "NIR2", "Pan"};

    public static final String[] BAND_MS1_ABSCALFACTOR_PATTERNS = new String[]{"/isd/imd/band_b/@abscalfactor",
            "/isd/imd/band_g/@abscalfactor",
            "/isd/imd/band_r/@abscalfactor",
            "/isd/imd/band_n/@abscalfactor",
            "/isd/imd/band_n2/@abscalfactor",
            "/isd/imd/band_re/@abscalfactor",
            "/isd/imd/band_y/@abscalfactor",
            "/isd/imd/band_c/@abscalfactor"
    };
    public static final String[] BAND_MS1_EFFECTIVEBANDWIDTH_PATTERNS = new String[]{"/isd/imd/band_b/@effectivebandwidth",
            "/isd/imd/band_g/@effectivebandwidth",
            "/isd/imd/band_r/@effectivebandwidth",
            "/isd/imd/band_n/@effectivebandwidth",
            "/isd/imd/band_n2/@effectivebandwidth",
            "/isd/imd/band_re/@effectivebandwidth",
            "/isd/imd/band_y/@effectivebandwidth",
            "/isd/imd/band_c/@effectivebandwidth"
    };

    public static final String BAND_P_ABSCALFACTOR = "/isd/imd/band_p/@abscalfactor";
    public static final String BAND_P_EFFECTIVEBANDWIDTH = "/isd/imd/band_p/@effectivebandwidth";

    public static final String[] WORLDVIEW2_RGB_PROFILE = new String[]{"Red", "Green", "Blue"};
    public static final String WORLDVIEW2_UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /*
     * Package (volume) metadata element paths
     */
    public static final String PATH_START_TIME = "/EarthObservation/phenomenonTime/TimePeriod/beginPosition";
    public static final String PATH_END_TIME = "/EarthObservation/phenomenonTime/TimePeriod/endPosition";
    public static final String PATH_ID = "/EarthObservation/metaDataProperty/EarthObservationMetaData/identifier";
    public static final String PATH_PRODUCT_TYPE = "/EarthObservation/metaDataProperty/EarthObservationMetaData/productType";

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
    public static final String PATH_TILE_SIZE_X = "/isd/til/@tilesizex";
    public static final String PATH_TILE_SIZE_Y = "/isd/til/@tilesizey";

    public static final String PATH_NUMBER_OF_TILES = "/isd/til/@numtiles";
    public static final String PATH_TILE_FILENAME = "/isd/til/tile/@filename";

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
