package org.esa.s2tbx.dataio.s2.l1c;

/**
 * Created by diana on 26/07/2024.
 */
public class L1cPSD150Constants {

    private final static String PRODUCT_SCHEMA_FILE_PATH = "schemas/PSD150/S2_User_Product_Level-1C_Metadata.xsd";
    private final static String GRANULE_SCHEMA_FILE_PATH = "schemas/PSD150/S2_PDI_Level-1C_Tile_Metadata.xsd";
    private final static String DATASTRIP_SCHEMA_FILE_PATH = "schemas/PSD150/S2_PDI_Level-1C_Datastrip_Metadata.xsd";
    private final static String SCHEMA150_BASE_PATH = "schemas/PSD150/";

    public static String[] getProductSchemaLocations() {

        String[] locations = new String[1];
        locations[0] = PRODUCT_SCHEMA_FILE_PATH;

        return locations;
    }

    public static String[] getGranuleSchemaLocations() {

        String[] locations = new String[1];
        locations[0] = GRANULE_SCHEMA_FILE_PATH;

        return locations;
    }

    public static String[] getDatastripSchemaLocations() {

        String[] locations = new String[1];
        locations[0] = DATASTRIP_SCHEMA_FILE_PATH;

        return locations;
    }

    public static String getProductSchemaBasePath() {
        return SCHEMA150_BASE_PATH;
    }

    public static String getDatastripSchemaBasePath() {
        return SCHEMA150_BASE_PATH;
    }

    public static String getGranuleSchemaBasePath() {
        return SCHEMA150_BASE_PATH;
    }


    /*
     * Default values
     */
    public static double DEFAULT_TOA_QUANTIFICATION = 10000;
    public static int DEFAULT_ANGLES_RESOLUTION = 5000;


    /*
     * Product metadata element paths
    */
    public static final String PATH_PRODUCT_METADATA_DATATAKE = "/Level-1C_User_Product/General_Info/Product_Info/Datatake/datatakeIdentifier";
    public static final String PATH_PRODUCT_METADATA_SPACECRAFT = "/Level-1C_User_Product/General_Info/Product_Info/Datatake/SPACECRAFT_NAME";
    public static final String PATH_PRODUCT_METADATA_DATATAKE_TYPE = "/Level-1C_User_Product/General_Info/Product_Info/Datatake/DATATAKE_TYPE";
    public static final String PATH_PRODUCT_METADATA_SENSING_START = "/Level-1C_User_Product/General_Info/Product_Info/Datatake/DATATAKE_SENSING_START";
    public static final String PATH_PRODUCT_METADATA_SENSING_ORBIT_NUMBER = "/Level-1C_User_Product/General_Info/Product_Info/Datatake/SENSING_ORBIT_NUMBER";
    public static final String PATH_PRODUCT_METADATA_ORBIT_DIRECTION = "/Level-1C_User_Product/General_Info/Product_Info/Datatake/SENSING_ORBIT_DIRECTION";

    public static final String PATH_PRODUCT_METADATA_PRODUCT_START_TIME = "/Level-1C_User_Product/General_Info/Product_Info/PRODUCT_START_TIME";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_STOP_TIME = "/Level-1C_User_Product/General_Info/Product_Info/PRODUCT_STOP_TIME";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_URI = "/Level-1C_User_Product/General_Info/Product_Info/PRODUCT_URI";
    public static final String PATH_PRODUCT_METADATA_PROCESSING_LEVEL = "/Level-1C_User_Product/General_Info/Product_Info/PROCESSING_LEVEL";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_TYPE = "/Level-1C_User_Product/General_Info/Product_Info/PRODUCT_TYPE";
    public static final String PATH_PRODUCT_METADATA_PROCESSING_BASELINE = "/Level-1C_User_Product/General_Info/Product_Info/PROCESSING_BASELINE";
    public static final String PATH_PRODUCT_METADATA_GENERATION_TIME = "/Level-1C_User_Product/General_Info/Product_Info/GENERATION_TIME";
    public static final String PATH_PRODUCT_METADATA_PREVIEW_IMAGE_URL = "/Level-1C_User_Product/General_Info/Product_Info/PREVIEW_IMAGE_URL";
    public static final String PATH_PRODUCT_METADATA_PREVIEW_GEO_INFO = "/Level-1C_User_Product/General_Info/Product_Info/PREVIEW_GEO_INFO";

    public static final String PATH_PRODUCT_METADATA_METADATA_LEVEL = "/Level-1C_User_Product/General_Info/Product_Info/Query_Options/METADATA_LEVEL";
    public static final String PATH_PRODUCT_METADATA_GRANULE_LIST = "/Level-1C_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granules/granuleIdentifier";
    public static final String PATH_PRODUCT_METADATA_GRANULE_LIST_ALT = "/Level-1C_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granule/granuleIdentifier";
    public static final String PATH_PRODUCT_METADATA_DATASTRIP_LIST = "/Level-1C_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granules/datastripIdentifier";
    public static final String PATH_PRODUCT_METADATA_DATASTRIP_LIST_ALT = "/Level-1C_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granule/datastripIdentifier";
    public static final String PATH_PRODUCT_METADATA_IMAGE_ID = "/Level-1C_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granules/IMAGE_ID";
    public static final String PATH_PRODUCT_METADATA_IMAGE_ID_ALT = "/Level-1C_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granule/IMAGE_ID";
    public static final String PATH_PRODUCT_METADATA_IMAGE_FILE = "/Level-1C_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granules/IMAGE_FILE";
    public static final String PATH_PRODUCT_METADATA_IMAGE_FILE_ALT = "/Level-1C_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granule/IMAGE_FILE";
    public static final String PATH_PRODUCT_METADATA_QUANTIFICATION_VALUE = "/Level-1C_User_Product/General_Info/Product_Image_Characteristics/QUANTIFICATION_VALUE";

    public static final String PATH_PRODUCT_METADATA_BAND_LIST = "/Level-1C_User_Product/General_Info/Product_Info/Query_Options/Band_List";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_FORMAT = "/Level-1C_User_Product/General_Info/Product_Info/Query_Options/PRODUCT_FORMAT";
    public static final String PATH_PRODUCT_METADATA_RADIO_OFFSET_VALUES_LIST = "/Level-1C_User_Product/General_Info/Product_Image_Characteristics/Radiometric_Offset_List/RADIO_ADD_OFFSET";

    /*
     * Granule metadata element path
     */
    public static final String PATH_GRANULE_METADATA_TILE_ID = "/Level-1C_Tile_ID/General_Info/TILE_ID";
    public static final String PATH_GRANULE_METADATA_HORIZONTAL_CS_NAME = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/HORIZONTAL_CS_NAME";
    public static final String PATH_GRANULE_METADATA_HORIZONTAL_CS_CODE = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/HORIZONTAL_CS_CODE";
    public static final String PATH_GRANULE_METADATA_SIZE_RESOLUTION = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/Size/resolution";
    public static final String PATH_GRANULE_METADATA_SIZE_NROWS = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/Size/NROWS";
    public static final String PATH_GRANULE_METADATA_SIZE_NCOLS = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/Size/NCOLS";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/resolution";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_ULX = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/ULX";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_ULY = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/ULY";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_XDIM = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/XDIM";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_YDIM = "/Level-1C_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/YDIM";
    public static final String PATH_GRANULE_METADATA_ANGLE_RESOLUTION = "/Level-1C_Tile_ID/Geometric_Info/Tile_Angles/Sun_Angles_Grid/Zenith/COL_STEP";
    public static final String PATH_GRANULE_METADATA_SUN_ZENITH_ANGLES = "/Level-1C_Tile_ID/Geometric_Info/Tile_Angles/Sun_Angles_Grid/Zenith/Values_List/VALUES";
    public static final String PATH_GRANULE_METADATA_SUN_AZIMUTH_ANGLES = "/Level-1C_Tile_ID/Geometric_Info/Tile_Angles/Sun_Angles_Grid/Azimuth/Values_List/VALUES";
    public static final String PATH_GRANULE_METADATA_VIEWING_ZENITH_ANGLES = "/Level-1C_Tile_ID/Geometric_Info/Tile_Angles/Viewing_Incidence_Angles_Grids/Zenith/Values_List/VALUES";
    public static final String PATH_GRANULE_METADATA_VIEWING_AZIMUTH_ANGLES = "/Level-1C_Tile_ID/Geometric_Info/Tile_Angles/Viewing_Incidence_Angles_Grids/Azimuth/Values_List/VALUES";
    public static final String PATH_GRANULE_METADATA_VIEWING_BAND_ID = "/Level-1C_Tile_ID/Geometric_Info/Tile_Angles/Viewing_Incidence_Angles_Grids/bandId";
    public static final String PATH_GRANULE_METADATA_MASK_FILENAME = "/Level-1C_Tile_ID/Quality_Indicators_Info/Pixel_Level_QI/MASK_FILENAME";
    public static final String PATH_GRANULE_METADATA_MASK_TYPE = "/Level-1C_Tile_ID/Quality_Indicators_Info/Pixel_Level_QI/MASK_FILENAME/type";
    public static final String PATH_GRANULE_METADATA_MASK_BAND = "/Level-1C_Tile_ID/Quality_Indicators_Info/Pixel_Level_QI/MASK_FILENAME/bandId";
    public static final String PATH_GRANULE_METADATA_PVI_FILENAME = "/Level-1C_Tile_ID/Quality_Indicators_Info/PVI_FILENAME";

}
