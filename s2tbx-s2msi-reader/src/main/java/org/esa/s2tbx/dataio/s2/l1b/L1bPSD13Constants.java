package org.esa.s2tbx.dataio.s2.l1b;

/**
 * Created by obarrile on 07/10/2016.
 */
public class L1bPSD13Constants {

    //TODO
    private final static String PRODUCT_SCHEMA_FILE_PATH = "schemas/PSD13/S2_User_Product_Level-1B_Metadata.xsd";
    private final static String GRANULE_SCHEMA_FILE_PATH = "schemas/PSD13/S2_PDI_Level-1B_Granule_Metadata.xsd";
    private final static String DATASTRIP_SCHEMA_FILE_PATH = "schemas/PSD13/S2_PDI_Level-1B_Datastrip_Metadata.xsd";
    private final static String SCHEMA13_BASE_PATH = "schemas/PSD13/";

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
        return SCHEMA13_BASE_PATH;
    }

    public static String getDatastripSchemaBasePath() {
        return SCHEMA13_BASE_PATH;
    }

    public static String getGranuleSchemaBasePath() {
        return SCHEMA13_BASE_PATH;
    }


    /*
     * Product metadata element paths
    */
    public static final String PATH_PRODUCT_METADATA_DATATAKE = "/Level-1B_User_Product/General_Info/Product_Info/Datatake/datatakeIdentifier";
    public static final String PATH_PRODUCT_METADATA_SPACECRAFT = "/Level-1B_User_Product/General_Info/Product_Info/Datatake/SPACECRAFT_NAME";
    public static final String PATH_PRODUCT_METADATA_DATATAKE_TYPE = "/Level-1B_User_Product/General_Info/Product_Info/Datatake/DATATAKE_TYPE";
    public static final String PATH_PRODUCT_METADATA_SENSING_START = "/Level-1B_User_Product/General_Info/Product_Info/Datatake/DATATAKE_SENSING_START";
    public static final String PATH_PRODUCT_METADATA_SENSING_ORBIT_NUMBER = "/Level-1B_User_Product/General_Info/Product_Info/Datatake/SENSING_ORBIT_NUMBER";
    public static final String PATH_PRODUCT_METADATA_ORBIT_DIRECTION = "/Level-1B_User_Product/General_Info/Product_Info/Datatake/SENSING_ORBIT_DIRECTION";

    public static final String PATH_PRODUCT_METADATA_PRODUCT_START_TIME = "/Level-1B_User_Product/General_Info/Product_Info/PRODUCT_START_TIME";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_STOP_TIME = "/Level-1B_User_Product/General_Info/Product_Info/PRODUCT_STOP_TIME";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_URI = "/Level-1B_User_Product/General_Info/Product_Info/PRODUCT_URI";
    public static final String PATH_PRODUCT_METADATA_PROCESSING_LEVEL = "/Level-1B_User_Product/General_Info/Product_Info/PROCESSING_LEVEL";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_TYPE = "/Level-1B_User_Product/General_Info/Product_Info/PRODUCT_TYPE";
    public static final String PATH_PRODUCT_METADATA_PROCESSING_BASELINE = "/Level-1B_User_Product/General_Info/Product_Info/PROCESSING_BASELINE";
    public static final String PATH_PRODUCT_METADATA_GENERATION_TIME = "/Level-1B_User_Product/General_Info/Product_Info/GENERATION_TIME";
    public static final String PATH_PRODUCT_METADATA_PREVIEW_IMAGE_URL = "/Level-1B_User_Product/General_Info/Product_Info/PREVIEW_IMAGE_URL";
    public static final String PATH_PRODUCT_METADATA_PREVIEW_GEO_INFO = "/Level-1B_User_Product/General_Info/Product_Info/PREVIEW_GEO_INFO";

    public static final String PATH_PRODUCT_METADATA_METADATA_LEVEL = "/Level-1B_User_Product/General_Info/Product_Info/Query_Options/METADATA_LEVEL";
    public static final String PATH_PRODUCT_METADATA_GRANULE_LIST = "/Level-1B_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granules/granuleIdentifier";
    public static final String PATH_PRODUCT_METADATA_GRANULE_LIST_ALT = "/Level-1B_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granule/granuleIdentifier";
    public static final String PATH_PRODUCT_METADATA_DATASTRIP_LIST = "/Level-1B_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granules/datastripIdentifier";
    public static final String PATH_PRODUCT_METADATA_DATASTRIP_LIST_ALT = "/Level-1B_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granule/datastripIdentifier";

    public static final String PATH_PRODUCT_METADATA_IMAGE_ID = "/Level-1B_User_Product/General_Info/Product_Info/Product_Organisation/Granule_List/Granules/IMAGE_ID";

    public static final String PATH_PRODUCT_METADATA_BAND_LIST = "/Level-1B_User_Product/General_Info/Product_Info/Query_Options/Band_List";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_FORMAT = "/Level-1B_User_Product/General_Info/Product_Info/Query_Options/PRODUCT_FORMAT";


    /*
     * Granule metadata element path
     */
    public static final String PATH_GRANULE_METADATA_TILE_ID = "/Level-1B_Granule_ID/General_Info/GRANULE_ID";
    public static final String PATH_GRANULE_METADATA_DETECTOR_ID = "/Level-1B_Granule_ID/General_Info/DETECTOR_ID";
    public static final String PATH_GRANULE_METADATA_DATASTRIP_ID = "/Level-1B_Granule_ID/General_Info/DATASTRIP_ID";
    public static final String PATH_GRANULE_METADATA_SENSING_TIME = "/Level-1B_Granule_ID/General_Info/SENSING_TIME";

    public static final String PATH_GRANULE_METADATA_GRANULE_CORNERS = "/Level-1B_Granule_ID/Geometric_Info/Granule_Footprint/Granule_Footprint/Footprint/EXT_POS_LIST";
    public static final String PATH_GRANULE_METADATA_GRANULE_POSITION = "/Level-1B_Granule_ID/Geometric_Info/Granule_Position/POSITION";
    public static final String PATH_GRANULE_METADATA_SIZE_RESOLUTION = "/Level-1B_Granule_ID/Geometric_Info/Granule_Dimensions/Size/resolution";
    public static final String PATH_GRANULE_METADATA_SIZE_NROWS = "/Level-1B_Granule_ID/Geometric_Info/Granule_Dimensions/Size/NROWS";
    public static final String PATH_GRANULE_METADATA_SIZE_NCOLS = "/Level-1B_Granule_ID/Geometric_Info/Granule_Dimensions/Size/NCOLS";

    public static final String PATH_GRANULE_METADATA_MASK_FILENAME = "/Level-1B_Granule_ID/Quality_Indicators_Info/Pixel_Level_QI/MASK_FILENAME";
    public static final String PATH_GRANULE_METADATA_MASK_TYPE = "/Level-1B_Granule_ID/Quality_Indicators_Info/Pixel_Level_QI/MASK_FILENAME/type";
    public static final String PATH_GRANULE_METADATA_MASK_BAND = "/Level-1B_Granule_ID/Quality_Indicators_Info/Pixel_Level_QI/MASK_FILENAME/bandId";
    public static final String PATH_GRANULE_METADATA_MASK_DETECTOR = "/Level-1B_Granule_ID/Quality_Indicators_Info/Pixel_Level_QI/MASK_FILENAME/detectorId";
}
