package org.esa.beam.dataio.deimos.dimap;

import java.io.File;

/**
 * Created by kraftek on 9/22/2014.
 */
public class DeimosConstants {
    public static final String METADATA_EXTENSION = ".dim";
    public static final String DEFAULT_METADATA_NAME = "DIMAP Metadata";
    public static final String DIMAP = "DIMAP";
    public static final String TAG_RASTER_DIMENSIONS = "Raster_Dimensions";
    public static final String TAG_NCOLS = "NCOLS";
    public static final String TAG_NBANDS = "NBANDS";
    public static final String TAG_DATA_ACCESS = "Data_Access";
    public static final String TAG_DATA_FILE = "Data_File";
    public static final String TAG_DATA_FILE_PATH = "DATA_FILE_PATH";
    public static final String TAG_IMAGE_INTERPRETATION = "Image_Interpretation";
    public static final String TAG_BAND_DESCRIPTION = "BAND_DESCRIPTION";
    public static final String TAG_IMAGE_DISPLAY = "Image_Display";
    public static final String TAG_SPECIAL_VALUE_TEXT = "SPECIAL_VALUE_TEXT";
    public static final String TAG_SPECIAL_VALUE_INDEX = "SPECIAL_VALUE_INDEX";
    public static final String NODATA_VALUE = "nodata";
    public static final String TAG_DATA_STRIP = "Data_Strip";
    public static final String TAG_SENSOR_CONFIGURATION = "Sensor_Configuration";
    public static final String TAG_SCENE_CENTER_TIME = "SCENE_CENTER_TIME";
    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
    public static final String TAG_COORDINATE_REFERENCE_SYSTEM = "Coordinate_Reference_System";
    public static final String TAG_HORIZONTAL_CS = "Horizontal_CS";
    public static final String TAG_HORIZONTAL_CS_NAME = "HORIZONTAL_CS_NAME";
    public static final String TAG_HORIZONTAL_CS_CODE = "HORIZONTAL_CS_CODE";
    public static final String TAG_SENSOR_CALIBRATION = "Sensor_Calibration";
    public static final String TAG_SPECTRAL_SENSITIVITIES = "Spectral_Sensitivities";
    public static final String TAG_FIRST_WAVELENGTH_VALUE = "FIRST_WAVELENGTH_VALUE";
    public static final int UNIT_MULTIPLIER = 1000000000;
    public static final String TAG_DATA_PROCESSING = "Data_Processing";
    public static final String TAG_PROCESSING_OPTIONS = "Processing_Options";
    public static final String TAG_DYNAMIC_STRETCH = "Dynamic_Stretch";
    public static final String TAG_LOW_THRESHOLD = "LOW_THRESHOLD";
    public static final String TAG_HIGH_THRESHOLD = "HIGH_THRESHOLD";
    public static final String TAG_WAVELENGTH_STEP = "WAVELENGTH_STEP";
    public static final String TAG_VOL_METADATA_FORMAT = "METADATA_FORMAT";
    public static final String TAG_VOL_PRODUCER_URL = "PRODUCER_URL";
    public static final String TAG_VOL_COMPONENT = "Component";
    public static final String TAG_VOL_COMPONENT_PATH = "COMPONENT_PATH";
    public static final String TAG_VOL_COMPONENT_TN_PATH = "COMPONENT_TN_PATH";
    public static final String TAG_VOL_DATASET_NAME = "DATASET_NAME";
    public static final String TAG_VOL_DATASET_PRODUCER_NAME = "DATASET_PRODUCER_NAME";
    public static final String TAG_VOL_DATASET_PRODUCTION_DATE = "DATASET_PRODUCTION_DATE";
    public static final String TAG_VOL_COMPONENT_TITLE = "COMPONENT_TITLE";
    public static final String TAG_VOL_COMPONENT_TYPE = "COMPONENT_TYPE";
    public static final String TAG_VOL_METADATA_PROFILE = "METADATA_PROFILE";
    public static final String ATTR_VERSION = "version";
    public static final String ATTR_HREF = "href";
    public static final String TAG_NROWS = "NROWS";
    public static final String DEFAULT_BAND_NAME_PREFIX = "band_";
    public static final String DEFAULT_SPOT_UNIT = "W/(m^2*sr*µm)";
    public static final String TAG_DATASET_ID = "Dataset_Id";
    public static final String TAG_DATASET_NAME = "DATASET_NAME";
    public static final String TAG_TIME_STAMP = "Time_Stamp";
    public static final String TAG_PHYSICAL_UNIT = "PHYSICAL_UNIT";
    public static final String TAG_DATASET_SOURCES = "Dataset_Sources";
    public static final String TAG_SOURCE_INFORMATION = "Source_Information";
    public static final String TAG_SOURCE_ID = "SOURCE_ID";
    public static final String TAG_SOURCE_DESCRIPTION = "Source_Description";

    public static final String TAG_IMAGE = "Image";
    public static final String TAG_CHANNELS = "CHANNELS";
    public static final String TAG_COLUMNS = "COLUMNS";
    public static final String TAG_ROWS = "ROWS";
    public static final String SPOTVIEW_RASTER_FILENAME = "imagery.bil";
    public static final String SPOTVIEW_GEOLAYER_FILENAME = "geolayer.bil";
    public static final String TAG_GEOLAYER = "Geolayer";
    public static final String TAG_BYTEORDER = "BYTEORDER";
    public static final String TAG_BITS_PER_PIXEL = "BITS_PER_PIXEL";
    public static final String TAG_PRODUCTION = "Production";
    public static final String TAG_GEO_INFORMATION = "GeoInformation";
    public static final String TAG_XGEOREF = "XGEOREF";
    public static final String TAG_YGEOREF = "YGEOREF";
    public static final String TAG_XCELLRES = "XCELLRES";
    public static final String TAG_YCELLRES = "YCELLRES";
    public static final String TAG_SPECTRAL_BAND_INFO = "Spectral_Band_Info";
    public static final String TAG_PHYSICAL_BIAS = "PHYSICAL_BIAS";
    public static final String TAG_PHYSICAL_GAIN = "PHYSICAL_GAIN";
    public static final String TAG_DATASET_FRAME = "Dataset_Frame";
    public static final String TAG_VERTEX = "Vertex";
    public static final String TAG_FRAME_LON = "FRAME_LON";
    public static final String TAG_FRAME_LAT = "FRAME_LAT";
    public static final String TAG_SCENE_ORIENTATION = "SCENE_ORIENTATION";
    public static final String TAG_SCENE_CENTER = "Scene_Center";
    public static final String TAG_PROJECTION = "PROJECTION";
    public static final String EPSG_3035 = "epsg:3035";
    public static final String SATURATED_VALUE = "SATURATED";
    public static final String TAG_SPECIAL_VALUE_COLOR = "Special_Value_Color";
    public static final String TAG_RED_LEVEL = "RED_LEVEL";
    public static final String TAG_GREEN_LEVEL = "GREEN_LEVEL";
    public static final String TAG_BLUE_LEVEL = "BLUE_LEVEL";
    public static final String TAG_METADATA_ID = "Metadata_Id";
    public static final String TAG_METADATA_FORMAT = "METADATA_FORMAT";
    public static final String TAG_METADATA_PROFILE = "METADATA_PROFILE";
    public static final String TAG_GEOPOSITION = "Geoposition";
    public static final String TAG_GEOPOSITION_POINTS = "Geoposition_Points";
    public static final String TAG_TIE_POINT_DATA_X = "TIE_POINT_DATA_X";
    public static final String TAG_TIE_POINT_DATA_Y = "TIE_POINT_DATA_Y";
    public static final String TAG_GEOPOSITION_INSERT = "Geoposition_Insert";
    public static final String TAG_TIE_POINT_CRS_X = "TIE_POINT_CRS_X";
    public static final String TAG_TIE_POINT_CRS_Y = "TIE_POINT_CRS_Y";
    public static final String TAG_TIE_POINT_CRS_Z = "TIE_POINT_CRS_Z";
    public static final String TAG_ULXMAP = "ULXMAP";
    public static final String TAG_ULYMAP = "ULYMAP";
    public static final String TAG_XDIM = "XDIM";
    public static final String TAG_YDIM = "YDIM";
    public static final String PROFILE_VOLUME = "VOLUME";
    public static final String PROFILE_MULTI_VOLUME = "MULTI-VOLUME";
    public static final String TAG_RASTER_ENCODING = "Raster_Encoding";
    public static final String TAG_NBITS = "NBITS";
    public static final String TAG_BAND_STATISTICS = "Band_Statistics";
    public static final String TAG_STX_MIN = "STX_MIN";
    public static final String TAG_STX_MAX = "STX_MAX";
    public static final String TAG_STX_MEAN = "STX_MEAN";
    public static final String TAG_STX_STDV = "STX_STDV";
    public static final String TAG_BAND_INDEX = "BAND_INDEX";
    public static final String TAG_STX_LIN_MIN = "STX_LIN_MIN";
    public static final String TAG_STX_LIN_MAX = "STX_LIN_MAX";
    public static final String VALUE_NOT_AVAILABLE = "N/A";
    // constants for plugins
    public static final Class[] DIMAP_READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String DIMAP_DESCRIPTION = "DEIMOS Data Products";
    public static final String[] DIMAP_DEFAULT_EXTENSIONS = new String[]{".dim", ".DIM", ".zip", ".ZIP"};
    public static final String[] DIMAP_FORMAT_NAMES = new String[]{"DEIMOSDimap"};
    public static final String[] FILENAME_PATTERNS = new String[] {
            "(.+)\\.zip",
            "DE[0-1][0-9]_SL[1-9]_\\d{1,3}[PST]_([0-1][RGT]|2R)_\\d{8}T\\d{6}_\\d{8}T\\d{6}_([A-Z]+)_\\d+_[0-9a-f]+\\.dim"
            };
    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[] {
            "DE[0-1][0-9]_SL[1-9]_\\d{1,3}[PST]_([0-1][RGT]|2R)_\\d{8}T\\d{6}_\\d{8}T\\d{6}_([A-Z]+)_\\d+_[0-9a-f]+(\\w+)\\.dim",
            "DE[0-1][0-9]_SL[1-9]_\\d{1,3}[PST]_([0-1][RGT]|2R)_\\d{8}T\\d{6}_\\d{8}T\\d{6}_([A-Z]+)_\\d+_[0-9a-f]+(\\w+)\\.tif"
            };
}
