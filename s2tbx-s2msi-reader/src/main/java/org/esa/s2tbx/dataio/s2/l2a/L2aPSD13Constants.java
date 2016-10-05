package org.esa.s2tbx.dataio.s2.l2a;

/**
 * Created by obarrile on 04/10/2016.
 */
public class L2aPSD13Constants {

    /*
    * Product metadata element paths
   */
    public static final String PATH_PRODUCT_METADATA_DATATAKE = "/Level-2A_User_Product/General_Info/Product_Info/Datatake/datatakeIdentifier";
    public static final String PATH_PRODUCT_METADATA_SPACECRAFT = "/Level-2A_User_Product/General_Info/Product_Info/Datatake/SPACECRAFT_NAME";
    public static final String PATH_PRODUCT_METADATA_DATATAKE_TYPE = "/Level-2A_User_Product/General_Info/Product_Info/Datatake/DATATAKE_TYPE";
    public static final String PATH_PRODUCT_METADATA_SENSING_START = "/Level-2A_User_Product/General_Info/Product_Info/Datatake/DATATAKE_SENSING_START";
    public static final String PATH_PRODUCT_METADATA_SENSING_ORBIT_NUMBER = "/Level-2A_User_Product/General_Info/Product_Info/Datatake/SENSING_ORBIT_NUMBER";
    public static final String PATH_PRODUCT_METADATA_ORBIT_DIRECTION = "/Level-2A_User_Product/General_Info/Product_Info/Datatake/SENSING_ORBIT_DIRECTION";

    public static final String PATH_PRODUCT_METADATA_PRODUCT_START_TIME = "/Level-2A_User_Product/General_Info/Product_Info/PRODUCT_START_TIME";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_STOP_TIME = "/Level-2A_User_Product/General_Info/Product_Info/PRODUCT_STOP_TIME";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_URI = "/Level-2A_User_Product/General_Info/Product_Info/PRODUCT_URI";
    public static final String PATH_PRODUCT_METADATA_PROCESSING_LEVEL = "/Level-2A_User_Product/General_Info/Product_Info/PROCESSING_LEVEL";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_TYPE = "/Level-2A_User_Product/General_Info/Product_Info/PRODUCT_TYPE";
    public static final String PATH_PRODUCT_METADATA_PROCESSING_BASELINE = "/Level-2A_User_Product/General_Info/Product_Info/PROCESSING_BASELINE";
    public static final String PATH_PRODUCT_METADATA_GENERATION_TIME = "/Level-2A_User_Product/General_Info/Product_Info/GENERATION_TIME";
    public static final String PATH_PRODUCT_METADATA_PREVIEW_IMAGE_URL = "/Level-2A_User_Product/General_Info/Product_Info/PREVIEW_IMAGE_URL";
    public static final String PATH_PRODUCT_METADATA_PREVIEW_GEO_INFO = "/Level-2A_User_Product/General_Info/Product_Info/PREVIEW_GEO_INFO";

    public static final String PATH_PRODUCT_METADATA_METADATA_LEVEL = "/Level-2A_User_Product/General_Info/L2A_Product_Info/Query_Options/METADATA_LEVEL";
    public static final String PATH_PRODUCT_METADATA_GRANULE_LIST = "/Level-2A_User_Product/General_Info/L2A_Product_Info/L2A_Product_Organisation/Granule_List/Granules/granuleIdentifier";
    public static final String PATH_PRODUCT_METADATA_DATASTRIP_LIST = "/Level-2A_User_Product/General_Info/L2A_Product_Info/L2A_Product_Organisation/Granule_List/Granules/datastripIdentifier";
    public static final String PATH_PRODUCT_METADATA_IMAGE_ID = "/Level-2A_User_Product/General_Info/L2A_Product_Info/L2A_Product_Organisation/Granule_List/Granules/IMAGE_ID_2A";


    public static final String PATH_PRODUCT_METADATA_L1C_TOA_QUANTIFICATION_VALUE = "/Level-2A_User_Product/General_Info/L2A_Product_Image_Characteristics/L1C_L2A_Quantification_Values_List/L1C_TOA_QUANTIFICATION_VALUE";
    public static final String PATH_PRODUCT_METADATA_L2A_BOA_QUANTIFICATION_VALUE = "/Level-2A_User_Product/General_Info/L2A_Product_Image_Characteristics/L1C_L2A_Quantification_Values_List/L2A_BOA_QUANTIFICATION_VALUE";
    public static final String PATH_PRODUCT_METADATA_L2A_AOT_QUANTIFICATION_VALUE = "/Level-2A_User_Product/General_Info/L2A_Product_Image_Characteristics/L1C_L2A_Quantification_Values_List/L2A_AOT_QUANTIFICATION_VALUE";
    public static final String PATH_PRODUCT_METADATA_L2A_WVP_QUANTIFICATION_VALUE = "/Level-2A_User_Product/General_Info/L2A_Product_Image_Characteristics/L1C_L2A_Quantification_Values_List/L2A_WVP_QUANTIFICATION_VALUE";

    public static final String PATH_PRODUCT_METADATA_BAND_LIST = "/Level-2A_User_Product/General_Info/L2A_Product_Info/Query_Options/Band_List";
    public static final String PATH_PRODUCT_METADATA_PRODUCT_FORMAT = "/Level-2A_User_Product/General_Info/L2A_Product_Info/Query_Options/PRODUCT_FORMAT";

    /*
     * Granule metadata element path
     */
    public static final String PATH_GRANULE_METADATA_TILE_ID = "/Level-2A_Tile_ID/General_Info/TILE_ID_2A";
    public static final String PATH_GRANULE_METADATA_HORIZONTAL_CS_NAME = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/HORIZONTAL_CS_NAME";
    public static final String PATH_GRANULE_METADATA_HORIZONTAL_CS_CODE = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/HORIZONTAL_CS_CODE";
    public static final String PATH_GRANULE_METADATA_SIZE_RESOLUTION = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/Size/resolution";
    public static final String PATH_GRANULE_METADATA_SIZE_NROWS = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/Size/NROWS";
    public static final String PATH_GRANULE_METADATA_SIZE_NCOLS = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/Size/NCOLS";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/resolution";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_ULX = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/ULX";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_ULY = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/ULY";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_XDIM = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/XDIM";
    public static final String PATH_GRANULE_METADATA_GEOPOSITION_YDIM = "/Level-2A_Tile_ID/Geometric_Info/Tile_Geocoding/Geoposition/YDIM";
    public static final String PATH_GRANULE_METADATA_ANGLE_RESOLUTION = "/Level-2A_Tile_ID/Geometric_Info/Tile_Angles/Sun_Angles_Grid/Zenith/COL_STEP";
    public static final String PATH_GRANULE_METADATA_SUN_ZENITH_ANGLES = "/Level-2A_Tile_ID/Geometric_Info/Tile_Angles/Sun_Angles_Grid/Zenith/Values_List/VALUES";
    public static final String PATH_GRANULE_METADATA_SUN_AZIMUTH_ANGLES = "/Level-2A_Tile_ID/Geometric_Info/Tile_Angles/Sun_Angles_Grid/Azimuth/Values_List/VALUES";
    public static final String PATH_GRANULE_METADATA_VIEWING_ZENITH_ANGLES = "/Level-2A_Tile_ID/Geometric_Info/Tile_Angles/Viewing_Incidence_Angles_Grids/Zenith/Values_List/VALUES";
    public static final String PATH_GRANULE_METADATA_VIEWING_AZIMUTH_ANGLES = "/Level-2A_Tile_ID/Geometric_Info/Tile_Angles/Viewing_Incidence_Angles_Grids/Azimuth/Values_List/VALUES";
    public static final String PATH_GRANULE_METADATA_VIEWING_BAND_ID = "/Level-2A_Tile_ID/Geometric_Info/Tile_Angles/Viewing_Incidence_Angles_Grids/bandId";

    public static final String PATH_GRANULE_METADATA_MASK_FILENAME = "/Level-2A_Tile_ID/Quality_Indicators_Info/L1C_Pixel_Level_QI/MASK_FILENAME";
    public static final String PATH_GRANULE_METADATA_MASK_TYPE = "/Level-2A_Tile_ID/Quality_Indicators_Info/L1C_Pixel_Level_QI/MASK_FILENAME/type";
    public static final String PATH_GRANULE_METADATA_MASK_BAND = "/Level-2A_Tile_ID/Quality_Indicators_Info/L1C_Pixel_Level_QI/MASK_FILENAME/bandId";

    public static final String PATH_GRANULE_METADATA_CLOUD_CONFIDENCE_MASK = "/Level-2A_Tile_ID/Quality_Indicators_Info/L2A_Pixel_Level_QI/CLOUD_CONFIDENCE_MASK";
    public static final String PATH_GRANULE_METADATA_SNOW_ICE_CONFIDENCE_MASK = "/Level-2A_Tile_ID/Quality_Indicators_Info/L2A_Pixel_Level_QI/SNOW_ICE_CONFIDENCE_MASK";
    public static final String PATH_GRANULE_METADATA_PVI_FILENAME = "/Level-2A_Tile_ID/Quality_Indicators_Info/PVI_FILENAME";
}
