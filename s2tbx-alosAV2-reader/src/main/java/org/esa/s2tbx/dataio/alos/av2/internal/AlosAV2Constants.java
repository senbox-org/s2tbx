package org.esa.s2tbx.dataio.alos.av2.internal;

import java.io.File;

/**
 * Holder class for string constants.
 *
 * @author Denisa Stefanescu
 */
public class AlosAV2Constants {

    public static final String VALUE_NOT_AVAILABLE = "N/A";
    public static final String DIMAP = "DIMAP";
    public static final String ALOSAV2 = "ALOS";
    public static final String[] DEFAULT_BAND_NAMES = { "blue", "green", "red", "near_infrared"};
    public static final String DEFAULT_UNIT = "W.M-2.SR-1.uM-1";
    public static final String STRING_ZERO = "0";
    public static final String NODATA = "no data";
    public static final String SATURATED = "saturated";
    public static final double MAX_LEVEL = 255.0;
    public static final String PROCESSING_1B = "1B1";

    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String[] FORMAT_NAMES = new String[]{"AlosAV2Dimap"};
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml", ".zip", ".dima"};
    public static final String DESCRIPTION = "Alos AVNIR-2 Data Products";

    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[]{
            "(AL\\d{1,2}_[A-Z]{4}_AV2_OBS_1(B|C)_\\d{8}T\\d{6}_\\d{8}T\\d{6}_0\\d{5}_\\d{4}_\\d{4}_\\d{4}(\\.[A-Z]{3}\\.ZIP|\\.MD\\.XML)|AL\\d{1,2}_AV2_OBS_1(B|C)_\\d{8}T\\d{6}_\\d{8}T\\d{6}_\\w{3}_\\d{6}_\\w{4}\\.DIMA)"
    };
    public static final String[] ALOSAV2_RGB_PROFILE = new String[]{"red", "green", "blue"};

    public static final String PRODUCT_ARCHIVE_FILE_SUFFIX = ".SIP.ZIP";
    public static final String PRODUCT_FOLDER_SUFFIX = ".SIP";
    public static final String IMAGE_METADATA_EXTENSION = ".DIMA";
    public static final String IMAGE_FILE_EXTENSION = ".GTIF";
    public static final String IMAGE_ARCHIVE_FILE_EXTENSION = ".ZIP";

    public static final String ALOSAV2_UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    /*
     * Raster metadata element paths
     */
    public static final String PATH_SOURCE_ID = "/Dimap_Document/Dataset_Sources/Source_Information/SOURCE_ID";
    public static final String PATH_IMG_METADATA_FORMAT = "/Dimap_Document/Metadata_Id/METADATA_FORMAT";
    public static final String PATH_IMG_METADATA_PROFILE = "/Dimap_Document/Metadata_Id/METADATA_PROFILE";
    public static final String PATH_IMG_DATA_FILE_PATH = "/Dimap_Document/Data_Access/Data_File/DATA_FILE_PATH/href";
    public static final String PATH_TIME_FIRST_LINE="/Dimap_Document/Data_Strip/Satellite_Time/TIME_FIRST_LINE";
    public static final String PATH_TIME_CENTER_LINE="/Dimap_Document/Data_Strip/Satellite_Time/TIME_CENTER_LINE";
    public static final String PATH_TIME_LAST_LINE="/Dimap_Document/Data_Strip/Satellite_Time/TIME_LAST_LINE";
    public static final String PATH_IMG_NUM_BANDS = "/Dimap_Document/Raster_Dimensions/NBANDS";
    public static final String PATH_IMG_NUM_ROWS = "/Dimap_Document/Raster_Dimensions/NROWS";
    public static final String PATH_IMG_NUM_COLS = "/Dimap_Document/Raster_Dimensions/NCOLS";
    public static final String PATH_IMG_SPECIAL_VALUE_TEXT = "/Dimap_Document/Image_Display/Special_Value/SPECIAL_VALUE_TEXT";
    public static final String PATH_IMG_SPECIAL_VALUE_INDEX = "/Dimap_Document/Image_Display/Special_Value/SPECIAL_VALUE_INDEX";
    public static final String PATH_SPECIAL_VALUE_COLOR_RED_LEVEL = "/dimap_document/image_display/special_value/special_value_color/red_level";
    public static final String PATH_SPECIAL_VALUE_COLOR_GREEN_LEVEL = "/dimap_document/image_display/special_value/special_value_color/green_level";
    public static final String PATH_SPECIAL_VALUE_COLOR_BLUE_LEVEL = "/dimap_document/image_display/special_value/special_value_color/blue_level";
    public static final String PATH_IMG_NBITS = "/Dimap_Document/Raster_Encoding/NBITS";

    public static final String PATH_XDIM = "/Dimap_Document/Geoposition/Geoposition_Insert/XDIM";
    public static final String PATH_YDIM = "/Dimap_Document/Geoposition/Geoposition_Insert/YDIM";
    public static final String PATH_ULXMAP = "/Dimap_Document/Geoposition/Geoposition_Insert/ULXMAP";
    public static final String PATH_ULYMAP = "/Dimap_Document/Geoposition/Geoposition_Insert/ULYMAP";
    public static final String PATH_GEOPOSITION_INSERT = "/Dimap_Document/Geoposition/Geoposition_Insert";
    public static final String PATH_SPECTRAL_BAND_INFO = "Dimap_Document/Image_Interpretation/Spectral_Band_Info";
    public static final String PATH_BAND_DESCRIPTION = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/BAND_DESCRIPTION";
    public static final String PATH_PHYSICAL_GAIN = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/PHYSICAL_GAIN";
    public static final String PATH_PHYSICAL_BIAS = "/Dimap_Document/Dataset_Sources/Source_Information/Scene_Source/Image_Interpretation/Spectral_Band_Info/OFFSET_BAND";
    public static final String PATH_BAND_UNIT = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/PHYSICAL_UNIT";
    public static final String PATH_IMG_PROCESSING_LEVEL = "/Dimap_Document/Dataset_Sources/Source_Information/Scene_Source/SCENE_PROCESSING_LEVEL";

    public static final String PATH_TIE_POINT_DATA_X = "/dimap_document/geoposition/geoposition_points/tie_point/tie_point_data_x";
    public static final String PATH_TIE_POINT_DATA_Y = "/dimap_document/geoposition/geoposition_points/tie_point/tie_point_data_y";
    public static final String PATH_TIE_POINT_CRS_X = "/dimap_document/geoposition/geoposition_points/tie_point/tie_point_crs_x";
    public static final String PATH_TIE_POINT_CRS_Y = "/dimap_document/geoposition/geoposition_points/tie_point/tie_point_crs_y";
}

