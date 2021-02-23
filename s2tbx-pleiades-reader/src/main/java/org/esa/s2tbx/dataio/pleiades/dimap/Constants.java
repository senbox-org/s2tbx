package org.esa.s2tbx.dataio.pleiades.dimap;

import java.io.File;

/**
 * Holder for various constants related to Pleiades products.
 * @author Cosmin Cara
 */
public class Constants {

    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String[] DEFAULT_BAND_NAMES = {"B0", "B1", "B2", "B3"};
    // constants for plugins
    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String DIMAP_DESCRIPTION = "Pleiades Data Products";
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml", ".zip"};
    public static final String[] FORMAT_NAMES = new String[]{"PleiadesDimap"};
    public static final String ROOT_METADATA = "VOL_PHR.XML";
    // https://senbox.atlassian.net/browse/SIITBX-441 : update regex for dim_phr1*.xml and img_phr1*.xml
    public static final String[] MINIMAL_PATTERN_LIST = new String[] {
            "vol_phr\\.xml",
            "img_phr1[ab]_\\w{1,3}_\\d{3}",
            "img_phr1[ab]_\\w{1,3}_\\d{3}[/\\\\]dim_phr1[ab]_\\w{1,3}_\\d{15}_(sen|ort|prj)_\\d{9,10}(-\\d{1,3})?\\.xml",
            "img_phr1[ab]_\\w{1,3}_\\d{3}[/\\\\]img_phr1[ab]_\\w{1,3}_\\d{15}_(sen|ort|prj)_\\d{9,10}(-\\d{1,3})?_r\\d{1}c\\d{1}\\.(jp2|tif)"};
    public static final String[] RGB_PROFILE = new String[] { "B2", "B1", "B0" };
    public static final String ROOT_METADATA_FILE = "VOL_PHR.XML";

    public static final String ATTR_VERSION = "version";
    public static final String ATTR_HREF = "href";

    public static final String VALUE_NOT_AVAILABLE = "N/A";
    public static final String METADATA_FORMAT = "DIMAP";
    public static final String MASK_COMPONENT_TYPE = "MASK";
    public static final String PRODUCT = "Pleiades 1A/B Product";
    public static final String STRING_ZERO = "0";
    public static final String STRING_ONE = "1";
    public static final String STRING_4095 = "4095";
    public static final String NODATA = "NODATA";
    public static final String SATURATED = "SATURATED";
    public static final String DEFAULT_PIXEL_SIZE = "16";
    public static final String DEFAULT_SIGN = "UNSIGNED";
    public static final String INTEGER_TYPE = "INTEGER";
    public static final String PROCESSING_SENSOR = "SENSOR";
    public static final double MS_RESOLUTION = 2;
    public static final double P_RESOLUTION = 0.5;

    /*
     * Package (volume) metadata element paths
     */
    public static final String PATH_VOL_METADATA_FORMAT = "/dimap_document/metadata_identification/metadata_format";
    public static final String PATH_VOL_METADATA_FORMAT_VERSION = "/dimap_document/metadata_identification/metadata_format/version";
    public static final String PATH_VOL_METADATA_PROFILE = "/dimap_document/metadata_identification/metadata_profile";
    public static final String PATH_VOL_METADATA_SUBPROFILE = "/dimap_document/metadata_identification/metadata_subprofile";
    public static final String PATH_VOL_METADATA_LANGUAGE = "/dimap_document/metadata_identification/metadata_language";
    public static final String PATH_VOL_DATASET_NAME = "/dimap_document/dataset_identification/dataset_name";
    public static final String PATH_VOL_DATASET_ID = "/dimap_document/dataset_identification/dataset_id";
    public static final String PATH_VOL_PRODUCER_NAME = "/dimap_document/product_information/producer_information/producer_name";
    public static final String PATH_VOL_PRODUCER_URL = "/dimap_document/product_information/producer_information/producer_url/href";
    public static final String PATH_VOL_PRODUCER_CONTACT = "/dimap_document/product_information/producer_information/producer_contact";
    public static final String PATH_VOL_PRODUCER_ADDRESS = "/dimap_document/product_information/producer_information/producer_address";
    public static final String PATH_VOL_DISTRIBUTOR_NAME = "/dimap_document/product_information/distributor_information/distributor_name";
    public static final String PATH_VOL_DISTRIBUTOR_URL = "/dimap_document/product_information/distributor_information/distributor_url/href";
    public static final String PATH_VOL_DISTRIBUTOR_CONTACT = "/dimap_document/product_information/distributor_information/distributor_contact";
    public static final String PATH_VOL_DISTRIBUTOR_ADDRESS = "/dimap_document/product_information/distributor_information/distributor_address";
    public static final String PATH_VOL_PRODUCTION_DATE = "/dimap_document/product_information/delivery_identification/production_date";
    public static final String PATH_VOL_PRODUCT_TYPE = "/dimap_document/product_information/delivery_identification/product_type";
    public static final String PATH_VOL_PRODUCT_CODE = "/dimap_document/product_information/delivery_identification/product_code";
    public static final String PATH_VOL_PRODUCT_INFO = "/dimap_document/product_information/delivery_identification/product_info";
    public static final String PATH_VOL_JOB_ID = "/dimap_document/product_information/delivery_identification/job_id";
    public static final String PATH_VOL_CUSTOMER_REFERENCE = "/dimap_document/product_information/delivery_identification/order_identification/customer_reference";
    public static final String PATH_VOL_INTERNAL_REFERENCE = "/dimap_document/product_information/delivery_identification/order_identification/internal_reference";
    public static final String PATH_VOL_COMMERCIAL_REFERENCE = "/dimap_document/product_information/delivery_identification/order_identification/commercial_reference";
    public static final String PATH_VOL_COMMERCIAL_ITEM = "/dimap_document/product_information/delivery_identification/order_identification/commercial_item";
    public static final String PATH_VOL_COMMENT = "/dimap_document/product_information/delivery_identification/order_identification/comment";
    public static final String PATH_VOL_COMPONENT_TITLE = "/dimap_document/dataset_content/dataset_components/component/component_title";
    public static final String PATH_VOL_COMPONENT_TITLE_ALT = "/dimap_document/dataset_components/component/component_title";
    public static final String PATH_VOL_COMPONENT_TYPE = "/dimap_document/dataset_content/dataset_components/component/component_type";
    public static final String PATH_VOL_COMPONENT_TYPE_ALT = "/dimap_document/dataset_components/component/component_type";
    public static final String PATH_VOL_COMPONENT_PATH = "/dimap_document/dataset_content/dataset_components/component/component_path/href";
    public static final String PATH_VOL_COMPONENT_PATH_ALT = "/dimap_document/dataset_components/component/component_path/href";
    public static final String PATH_VOL_COMPONENT_TN_PATH = "/dimap_document/dataset_content/dataset_components/component/component_tn_path/href";
    public static final String PATH_VOL_COMPONENT_TN_PATH_ALT = "/dimap_document/dataset_components/component/component_tn_path/href";
    public static final String PATH_VOL_COMPONENT_TN_FORMAT = "/dimap_document/dataset_content/dataset_components/component/component_tn_format";
    public static final String PATH_VOL_COMPONENT_TN_FORMAT_ALT = "/dimap_document/dataset_components/component/component_tn_format";

    /*
     * Raster metadata element paths
     */
    public static final String PATH_IMG_NBANDS = "/dimap_document/raster_data/raster_dimensions/nbands";
    public static final String PATH_IMG_NROWS = "/dimap_document/raster_data/raster_dimensions/nrows";
    public static final String PATH_IMG_NCOLS = "/dimap_document/raster_data/raster_dimensions/ncols";
    public static final String PATH_IMG_NBITS = "/dimap_document/raster_data/raster_encoding/nbits";
    public static final String PATH_IMG_SIGN = "/dimap_document/raster_data/raster_encoding/sign";
    public static final String PATH_IMG_DATA_TYPE = "/dimap_document/raster_data/raster_encoding/data_type";
    public static final String PATH_IMG_NTILES = "/dimap_document/raster_data/raster_dimensions/tile_set/ntiles";
    public static final String PATH_IMG_NTILES_SIZE_NROWS = "/dimap_document/raster_data/raster_dimensions/tile_set/regular_tiling/ntiles_size/nrows";
    public static final String PATH_IMG_NTILES_SIZE_NCOLS = "/dimap_document/raster_data/raster_dimensions/tile_set/regular_tiling/ntiles_size/ncols";
    public static final String PATH_IMG_NTILES_COUNT_ROWS = "/dimap_document/raster_data/raster_dimensions/tile_set/regular_tiling/ntiles_count/ntiles_R";
    public static final String PATH_IMG_NTILES_COUNT_COLS = "/dimap_document/raster_data/raster_dimensions/tile_set/regular_tiling/ntiles_count/ntiles_C";
    public static final String PATH_IMG_OVERLAP_ROW = "/dimap_document/raster_data/raster_dimensions/tile_set/regular_tiling/overlap_row";
    public static final String PATH_IMG_OVERLAP_COL = "/dimap_document/raster_data/raster_dimensions/tile_set/regular_tiling/overlap_col";
    public static final String PATH_IMG_DATA_FILE_PATH = "/dimap_document/raster_data/data_access/data_files/data_file/data_file_path/href";
    public static final String PATH_IMG_DATA_FILE_ROW = "/dimap_document/raster_data/data_access/data_files/data_file/tile_R";
    public static final String PATH_IMG_DATA_FILE_COL = "/dimap_document/raster_data/data_access/data_files/data_file/tile_C";
    public static final String PATH_IMG_DATASET_NAME = "/dimap_document/dataset_identification/dataset_name";
    public static final String PATH_IMG_METADATA_FORMAT = "/dimap_document/metadata_identification/metadata_format";
    public static final String PATH_IMG_METADATA_PROFILE = "/dimap_document/metadata_identification/metadata_profile";
    public static final String PATH_IMG_LOCATION_TYPE = "/dimap_document/geometric_data/use_area/located_geometric_values/location_type";
    public static final String PATH_IMG_TIME = "/dimap_document/geometric_data/use_area/located_geometric_values/time";
    public static final String PATH_IMG_TIME_RANGE_START = "/dimap_document/geometric_data/refined_model/time/start";
    public static final String PATH_IMG_TIME_RANGE_MIDDLE = "/dimap_document/geometric_data/refined_model/time/middle";
    public static final String PATH_IMG_TIME_RANGE_END = "/dimap_document/geometric_data/refined_model/time/endt";
    public static final String PATH_IMG_BAND_ID = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_spectral_range/band_id";
    public static final String PATH_IMG_BAND_MEASURE = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_radiance/measure_unit";
    public static final String PATH_IMG_BAND_MIN = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_spectral_range/min";
    public static final String PATH_IMG_BAND_MAX = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_spectral_range/max";
    public static final String PATH_IMG_BAND_GAIN = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_radiance/gain";
    public static final String PATH_IMG_BAND_BIAS = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_radiance/bias";
    public static final String PATH_IMG_SPECIAL_VALUE_TEXT = "/dimap_document/raster_data/raster_display/special_value/special_value_text";
    public static final String PATH_IMG_SPECIAL_VALUE_COUNT = "/dimap_document/raster_data/raster_display/special_value/special_value_count";
    public static final String PATH_IMG_ADJUSTMENT_BAND_ID = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_radiance/band_id";
    public static final String PATH_IMG_ADJUSTMENT_BIAS = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_radiance/bias";
    public static final String PATH_IMG_ADJUSTMENT_SLOPE = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_radiance/gain";
    public static final String PATH_IMG_HISTOGRAM_BAND = "/dimap_document/radiometric_data/histogram_band_list/histogram_band/band_id";
    public static final String PATH_IMG_HISTOGRAM_MIN = "/dimap_document/radiometric_data/histogram_band_list/histogram_band/min";
    public static final String PATH_IMG_HISTOGRAM_MAX = "/dimap_document/radiometric_data/histogram_band_list/histogram_band/max";
    public static final String PATH_IMG_HISTOGRAM_MEAN = "/dimap_document/radiometric_data/histogram_band_list/histogram_band/mean";
    public static final String PATH_IMG_HISTOGRAM_STDEV = "/dimap_document/radiometric_data/histogram_band_list/histogram_band/stdv";
    public static final String PATH_IMG_HISTOGRAM_VALUES = "/dimap_document/radiometric_data/histogram_band_list/histogram_band/values";
    public static final String PATH_IMG_BAND_IRRADIANCE_BAND_ID = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_solar_irradiance/band_id";
    public static final String PATH_IMG_BAND_IRRADIANCE_VALUE = "/dimap_document/radiometric_data/radiometric_calibration/instrument_calibration/band_measurement_list/band_solar_irradiance/value";
    public static final String PATH_IMG_RASTER_GEOMETRY = "/dimap_document/geoposition/raster_crs/raster_geometry";
    public static final String PATH_IMG_RPC_PATH = "/dimap_document/geoposition/geoposition_models/rational_function_model/component/component_path/href";
    public static final String PATH_IMG_GEODETIC_CRS_CODE = "/dimap_document/coordinate_reference_system/geodetic_crs/geodetic_crs_code";
    public static final String PATH_IMG_GEOPOSITION_INSERT_ULXMAP = "/dimap_document/geoposition/geoposition_insert/ulxmap";
    public static final String PATH_IMG_GEOPOSITION_INSERT_ULYMAP = "/dimap_document/geoposition/geoposition_insert/ulymap";
    public static final String PATH_IMG_GEOPOSITION_INSERT_XDIM = "/dimap_document/geoposition/geoposition_insert/xdim";
    public static final String PATH_IMG_GEOPOSITION_INSERT_YDIM = "/dimap_document/geoposition/geoposition_insert/ydim";
    public static final String PATH_IMG_EXTENT_VERTEX_LON = "/dimap_document/dataset_content/dataset_extent/vertex/lon";
    public static final String PATH_IMG_EXTENT_VERTEX_LAT = "/dimap_document/dataset_content/dataset_extent/vertex/lat";
    public static final String PATH_IMG_EXTENT_VERTEX_COL = "/dimap_document/dataset_content/dataset_extent/vertex/col";
    public static final String PATH_IMG_EXTENT_VERTEX_ROW = "/dimap_document/dataset_content/dataset_extent/vertex/row";
    public static final String PATH_IMG_PROJECTED_CRS_CODE = "/dimap_document/coordinate_reference_system/projected_crs/projected_crs_code";
    public static final String PATH_IMG_SPECTRAL_PROCESSING = "/dimap_document/processing_information/product_settings/spectral_processing";
    public static final String PATH_IMG_PROCESSING_LEVEL = "/dimap_document/processing_information/product_settings/processing_level";

    /*
     * RPC metadata elements paths
     */
    public static final String PATH_RPC_DM_SAMP_DEN = "/dimap_document/rational_function_model/global_rfm/direct_model/samp_den_coeff_";
    public static final String PATH_RPC_DM_SAMP_NUM = "/dimap_document/rational_function_model/global_rfm/direct_model/samp_num_coeff_";
    public static final String PATH_RPC_DM_LINE_DEN = "/dimap_document/rational_function_model/global_rfm/direct_model/line_den_coeff";
    public static final String PATH_RPC_DM_LINE_NUM = "/dimap_document/rational_function_model/global_rfm/direct_model/line_num_coeff";

    /*
     * GML masks elements paths
     */
    public static final String PATH_GML_MEASURE_NAME = "/dimap_document/quality_assessment/imaging_quality_measurement/measure_name";
    public static final String PATH_GML_MEASURE_DESC = "/dimap_document/quality_assessment/imaging_quality_measurement/measure_desc";
    public static final String PATH_GML_COMPONENT_TITLE = "/dimap_document/quality_assessment/imaging_quality_measurement/quality_mask/component/component_title";
    public static final String PATH_GML_COMPONENT_CONTENT = "/dimap_document/quality_assessment/imaging_quality_measurement/quality_mask/component/component_content";
    public static final String PATH_GML_COMPONENT_TYPE = "/dimap_document/quality_assessment/imaging_quality_measurement/quality_mask/component/component_type";
    public static final String PATH_GML_COMPONENT_PATH = "/dimap_document/quality_assessment/imaging_quality_measurement/quality_mask/component/component_path/href";
}
