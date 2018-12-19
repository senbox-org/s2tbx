package org.esa.s2tbx.dataio.alosAV2.internal;

import java.io.File;

/**
 * Holder class for string constants.
 *
 * @author Denisa Stefanescu
 */
public class AlosAV2Constants {

    public static final String VALUE_NOT_AVAILABLE = "N/A";
    public static final  String DEFAULT_PIXEL_STEP_SIZE = "10.000000";
    public static final String STRING_ZERO = "0";
    public static final String STRING_ONE = "1";
    public static final String NODATA = "no data";
    public static final String DEFAULT_PIXEL_SIZE = "8";
    public static final String DEFAULT_SIGN = "UNSIGNED";
    public static final String INTEGER_TYPE = "INTEGER";

    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String[] FORMAT_NAMES = new String[]{"AlosAV2GeoTIFF"};
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml", ".zip"};
    public static final String DESCRIPTION = "Alos AVNIR2 Data Products";
    public static final String PRODUCT_GENERIC_NAME = "Alos AVNIR2 Product";
    public static final String PRODUCT_TYPE = "Alos AVNIR2 Product";

    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[]{
            "AL\\d{1,2}_[A-Z]{4}_AV2_OBS_1(B|C)_\\d{8}T\\d{6}_\\d{8}T\\d{6}_0\\d{5}_\\d{4}_\\d{4}_\\d{4}.*.ZIP",
            "AL\\d{1,2}_[A-Z]{4}_AV2_OBS_1(B|C)_\\d{8}T\\d{6}_\\d{8}T\\d{6}_0\\d{5}_\\d{4}_\\d{4}_\\d{4}.MD.XML"
    };
    public static final String[] ALOSAV2_RGB_PROFILE = new String[]{"3", "2", "1"};
    public static final String[] BAND_NAMES = new String[]{"1", "2", "3", "4"};

    public static final String METADATA_FILE_SUFFIX = ".MD.XML";
    public static final String IMAGE_METADATA_EXTENSION = ".DIMA";
    public static final String IMAGE_EXTENSION = ".GTIF";
    public static final String PRODUCT_FILE_SUFFIX = ".SIP.ZIP";
    public static final String ARCHIVE_FILE_EXTENSION = ".zip";

    public static final String ALOSAV2_UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String LAT_DS_NAME = "latitude";
    public static final String LON_DS_NAME = "longitude";

    /*
     * Package (volume) metadata element paths
     */
    public static final String PATH_START_TIME = "/EarthObservation/phenomenonTime/TimePeriod/beginPosition";
    public static final String PATH_END_TIME = "/EarthObservation/phenomenonTime/TimePeriod/endPosition";
    public static final String PATH_IDENTIFIER = "/EarthObservation/metaDataProperty/EarthObservationMetaData/identifier";
    public static final String PATH_TIE_POINT_GRID = "/EarthObservation/featureOfInterest/Footprint/multiExtentOf/MultiSurface/surfaceMember/Polygon/exterior/LinearRing/posList";
    public static final String PATH_ORIGIN = "/EarthObservation/featureOfInterest/Footprint/centerOf/Point/pos";
    public static final String PATH_CRS_NAME = "/EarthObservation/result/EarthObservationResult/browse/BrowseInformation/referenceSystemIdentifier";



    /*
     * Raster metadata element paths
     */
    public static final String PATH_IMG_METADATA_FORMAT = "/Dimap_Document/Metadata_Id/METADATA_FORMAT";
    public static final String PATH_IMG_DATASET_NAME = "/Dimap_Document/Dataset_Id/DATASET_NAME";
    public static final String PATH_IMG_METADATA_PROFILE = "/Dimap_Document/Dataset_Id/METADATA_PROFILE";
    public static final String PATH_IMG_DATA_FILE_PATH = "/Dimap_Document/Data_Access/Data_File/DATA_FILE_PATH/href";
    public static final String PATH_TIME_FIRST_LINE="/Dimap_Document/Data_Strip/Satellite_Time/TIME_FIRST_LINE";
    public static final String PATH_TIME_CENTER_LINE="/Dimap_Document/Data_Strip/Satellite_Time/TIME_CENTER_LINE";
    public static final String PATH_TIME_LAST_LINE="/Dimap_Document/Data_Strip/Satellite_Time/TIME_LAST_LINE";
   public static final String PATH_IMG_NUM_BANDS = "/Dimap_Document/Raster_Dimensions/NBANDS";
    public static final String PATH_IMG_NUM_ROWS = "/Dimap_Document/Raster_Dimensions/NROWS";
    public static final String PATH_IMG_NUM_COLS = "/Dimap_Document/Raster_Dimensions/NCOLS";
    public static final String PATH_IMG_SPECIAL_VALUE_TEXT = "/Dimap_Document/Image_Display/Special_Value/SPECIAL_VALUE_TEXT";
    public static final String PATH_IMG_SPECIAL_VALUE_INDEX = "/Dimap_Document/Image_Display/Special_Value/SPECIAL_VALUE_INDEX";
    public static final String PATH_IMG_NBITS = "/Dimap_Document/Raster_Encoding/NBITS";
    public static final String PATH_IMG_DATA_TYPE = "/Dimap_Document/Raster_Encoding/DATA_TYPE";
    public static final String PATH_IMG_SIGN = "/Dimap_Document/Raster_Encoding/DATA_SIGN";

    public static final String PATH_XDIM = "/Dimap_Document/Geoposition/XDIM";
    public static final String PATH_YDIM = "/Dimap_Document/Geoposition/YDIM";
    public static final String PATH_ULXMAP = "/Dimap_Document/Geoposition/ULXMAP";
    public static final String PATH_ULYMAP = "/Dimap_Document/Geoposition/ULYMAP";
    public static final String PATH_PHYSICAL_GAIN = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/PHYSICAL_GAIN";
    public static final String PATH_PHYSICAL_BIAS = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/PHYSICAL_BIAS";
    public static final String PATH_BAND_INDEX = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/BAND_INDEX";
    public static final String PATH_UNIT = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/PHYSICAL_UNIT";
    public static final String PATH_IMG_BAND_MIN = "/Dimap_Document/Image_Display/Band_Statistics/STX_MIN";
    public static final String PATH_IMG_BAND_MAX = "/Dimap_Document/Image_Display/Band_Statistics/STX_MAX";
    public static final String PATH_IMG_BAND_MEAN = "/Dimap_Document/Image_Display/Band_Statistics/STX_MEAN";
    public static final String PATH_IMG_BAND_STDV = "/Dimap_Document/Image_Display/Band_Statistics/STX_STDV";
    public static final String PATH_IMG_PROCESSING_LEVEL = "/Dimap_Document/Dataset_Sources/Source_Information/Scene_Source/SCENE_PROCESSING_LEVEL";
    public static final String PATH_IMG_AZIMUTH = "/Dimap_Document/Dataset_Sources/Source_Information/Scene_Source/SUN_AZIMUTH";
    public static final String PATH_IMG_ELEVATION = "/Dimap_Document/Dataset_Sources/Source_Information/Scene_Source/SUN_ELEVATION";


}

