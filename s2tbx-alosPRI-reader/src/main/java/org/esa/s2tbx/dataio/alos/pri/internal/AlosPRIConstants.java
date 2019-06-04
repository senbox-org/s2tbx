package org.esa.s2tbx.dataio.alos.pri.internal;

import java.io.File;

/**
 * Holder class for string constants.
 *
 * @author Denisa Stefanescu
 */

public class AlosPRIConstants {

    public static final String VALUE_NOT_AVAILABLE = "N/A";
    public static final String STRING_ZERO = "0";
    public static final String STRING_4095 = "4095";
    public static final String ALOSPRI = "ALOS";
    public static final String NODATA = "no data";
    public static final String SATURATED = "saturated";
    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml", ".zip", ".dima"};
    public static final String DEFAULT_UNIT = "W.M-2.SR-1.uM-1";
    public static final String DESCRIPTION = "Alos PRISM Data Products";
    public static final String[] FORMAT_NAMES = new String[]{"AlosPRIDimap"};
    public static final String PRODUCT_GENERIC_NAME = "Alos PRISM Product";
    public static final String DIMAP = "DIMAP";
    public static final String DEFAULT_PIXEL_SIZE = "2.500000";

    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[]{
            "AL\\d{1,2}_[A-Z]{4}_PSM_OB\\d{1}_1(B|C)_\\d{8}T\\d{6}_\\d{8}T\\d{6}_0\\d{5}_\\d{4}_\\d{4}_\\d{4}(\\.[A-Z]{3}\\.ZIP|\\.MD\\.XML)"
    };


    public static final String METADATA_FILE_SUFFIX = ".MD.XML";
    public static final String PRODUCT_FILE_SUFFIX = ".SIP.ZIP";
    public static final String IMAGE_METADATA_EXTENSION = ".DIMA";
    public static final String IMAGE_EXTENSION = ".GTIF";
    public static final String ARCHIVE_FILE_EXTENSION = ".ZIP";
    protected static final String[] DEFAULT_BAND_NAMES = new String[]{"Band_1(pan)"};
    public static final String DEFAULT_BAND_DESCRIPTION = "Band 1 (pan, 0.52 uM to 0.77 uM)";

    public static final String ALOSPRI_UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
    public static final String LAT_DS_NAME = "latitude";
    public static final String LON_DS_NAME = "longitude";

    /*
     *Product metadata elements paths
     */
    public static final String PATH_START_TIME = "/EarthObservation/phenomenonTime/TimePeriod/beginPosition";
    public static final String PATH_END_TIME = "/EarthObservation/phenomenonTime/TimePeriod/endPosition";
    public static final String PATH_ID = "/EarthObservation/metaDataProperty/EarthObservationMetaData/identifier";

    /*
     * Raster metadata element paths
     */

    public static final String PATH_IMG_METADATA_FORMAT = "/Dimap_Document/Metadata_Id/METADATA_FORMAT";

    public static final String PATH_IMG_METADATA_PROFILE = "/Dimap_Document/Metadata_Id/METADATA_PROFILE";

    public static final String PATH_IMG_NUM_BANDS = "/Dimap_Document/Raster_Dimensions/NBANDS";
    public static final String PATH_IMG_NUM_ROWS = "/Dimap_Document/Raster_Dimensions/NROWS";
    public static final String PATH_IMG_NUM_COLS = "/Dimap_Document/Raster_Dimensions/NCOLS";
    public static final String PATH_IMG_NBITS = "/Dimap_Document/Raster_Encoding/NBITS";

    public static final String PATH_IMG_SPECIAL_VALUE_TEXT = "/dimap_document/Image_Display/Special_Value/SPECIAL_VALUE_TEXT";
    public static final String PATH_IMG_SPECIAL_VALUE_COUNT = "/dimap_document/Image_Display/Special_Value/SPECIAL_VALUE_INDEX";

    public static final String PATH_IMG_DATA_FILE_PATH = "/Dimap_Document/Data_Access/Data_File/DATA_FILE_PATH/href";

    public static final String PATH_SOURCE_ID = "/Dimap_Document/Dataset_Sources/Source_Information/SOURCE_ID";
    public static final String PATH_CRS_CODE = "/Dimap_Document/Coordinate_Reference_System/Horizontal_CS/HORIZONTAL_CS_CODE";

    public static final String PATH_BAND_DESCRIPTION = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/BAND_DESCRIPTION";
    public static final String PATH_PHYSICAL_GAIN = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/PHYSICAL_GAIN";
    public static final String PATH_BAND_UNIT = "/Dimap_Document/Image_Interpretation/Spectral_Band_Info/PHYSICAL_UNIT";
    public static final String PATH_BAND_COLS = "/Dimap_Document/Dataset_Sources/Source_Information/Scene_Source/Image_Interpretation/Spectral_Band_Info/NCOLS";
    public static final String PATH_BAND_ROWS = "/Dimap_Document/Dataset_Sources/Source_Information/Scene_Source/Image_Interpretation/Spectral_Band_Info/NROWS";

    public static final String PATH_TIME_FIRST_LINE = "/Dimap_Document/Data_Strip/Satellite_Time/TIME_FIRST_LINE";
    public static final String PATH_TIME_CENTER_LINE = "/Dimap_Document/Data_Strip/Satellite_Time/TIME_CENTER_LINE";
    public static final String PATH_TIME_LAST_LINE = "/Dimap_Document/Data_Strip/Satellite_Time/TIME_LAST_LINE";

    public static final String PATH_IMG_GEOPOSITION_INSERT_ULXMAP = "/Dimap_Document/Geoposition/Geoposition_Insert/ULXMAP";
    public static final String PATH_IMG_GEOPOSITION_INSERT_ULYMAP = "/Dimap_Document/Geoposition/Geoposition_Insert/ULYMAP";
    public static final String PATH_IMG_GEOPOSITION_INSERT_XDIM = "/Dimap_Document/Geoposition/Geoposition_Insert/XDIM";
    public static final String PATH_IMG_GEOPOSITION_INSERT_YDIM = "/Dimap_Document/Geoposition/Geoposition_Insert/YDIM";
    public static final String PATH_IMG_EXTENT_VERTEX_LON = "/Dimap_Document/Dataset_Sources/Source_Information/Source_Frame/Vertex/FRAME_LON";
    public static final String PATH_IMG_EXTENT_VERTEX_LAT = "/Dimap_Document/Dataset_Sources/Source_Information/Source_Frame/Vertex/FRAME_LAT";
    public static final String PATH_IMG_EXTENT_VERTEX_COL = "/Dimap_Document/Dataset_Sources/Source_Information/Source_Frame/Vertex/FRAME_COL";
    public static final String PATH_IMG_EXTENT_VERTEX_ROW = "/Dimap_Document/Dataset_Sources/Source_Information/Source_Frame/Vertex/FRAME_ROW";
}
