package org.esa.s2tbx.dataio.ikonos.internal;

import java.io.File;

/**
 * Holder class for constants.
 * The band gain values and band unit are from https://www.geosystems.de/fileadmin/redaktion/Downloads/Produkte/ATCOR/ATCOR_Info_on_SensorGeometry_and_Calibration_02-2014.pdf
 *
 * @author Denisa Stefanescu
 */


public class IkonosConstants {
    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String[] FORMAT_NAMES = new String[]{"IkonosGeoTIFF"};
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml", ".zip"};
    public static final String DESCRIPTION = "Ikonos Data Products";
    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[]{
            "IK2_OPER_OSA_GEO_\\d{1}\\w{1}_\\d{8}T\\d{6}_\\w{1}\\d{2}-\\d{3}_\\w{1}\\d{3}-\\d{3}_\\d{4}.*.ZIP",
            "IK2_OPER_OSA_GEO_\\d{1}\\w{1}_\\d{8}T\\d{6}_\\w{1}\\d{2}-\\d{3}_\\w{1}\\d{3}-\\d{3}_\\d{4}.MD.XML"
    };
    public static final String PATH_ZIP_FILE_NAME_PATTERN = "\\d{14}_po_\\d{7}_\\d{7}";
    public static final String[] IKONOS_RGB_PROFILE = new String[]{"Red", "Green", "Blue"};
    public static final String[] BAND_NAMES = new String[]{"1", "2", "3", "4", "Pan"};
    public static final String[] FILE_NAMES = new String[]{"blu", "grn", "red", "nir", "pan"};
    public static final Double[] BAND_GAIN = new Double[]{0.00137, 0.00137, 0.00105, 0.00119};
    public static final String BAND_MEASURE_UNIT = "mW/cm^2*sr";

    public static final String METADATA_FILE_SUFFIX = ".MD.XML";
    public static final String IMAGE_METADATA_EXTENSION = ".hdr";
    public static final String IMAGE_COMMON_METADATA_EXTENSION = "_metadata.txt";
    public static final String IMAGE_ARCHIVE_EXTENSION = ".tif.gz";
    public static final String IMAGE_EXTENSION = ".tif";

    public static final String PRODUCT_GENERIC_NAME = "Ikonos Product";
    public static final String IKONOS_PRODUCT = "Ikonos Product";
    public static final String PRODUCT_FILE_SUFFIX = ".SIP.ZIP";
    public static final String ARCHIVE_FILE_EXTENSION = ".zip";

    public static final String IKONOS_UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String LAT_DS_NAME = "latitude";
    public static final String LON_DS_NAME = "longitude";

    /*
     * Package (volume) metadata element paths
     */
    public static final String PATH_START_TIME = "/EarthObservation/phenomenonTime/TimePeriod/beginPosition";
    public static final String PATH_END_TIME = "/EarthObservation/phenomenonTime/TimePeriod/endPosition";
    public static final String PATH_TIE_POINT_GRID = "/EarthObservation/featureOfInterest/Footprint/multiExtentOf/MultiSurface/surfaceMember/Polygon/exterior/LinearRing/posList";
    public static final String PATH_ORIGIN = "/EarthObservation/featureOfInterest/Footprint/centerOf/Point/pos";
    public static final String PATH_CRS_NAME = "/EarthObservation/result/EarthObservationResult/browse/BrowseInformation/referenceSystemIdentifier";
    public static final String PATH_ID = "/EarthObservation/metaDataProperty/EarthObservationMetaData/identifier";
    public static final String PATH_PRODUCT_TYPE = "/EarthObservation/metaDataProperty/EarthObservationMetaData/productType";

    /*
     * Raster metadata element paths
     */
    public static final String TAG_BITS_PER_PIXEL = "Bits/Pixel:";
    public static final String TAG_NUMBER_COLUMNS_IMAGE = "Columns:";
    public static final String TAG_NUMBER_ROWS_IMAGE = "Rows:";
    public static final String TAG_PIXEL_SIZE_X = "Pixel Size X:";
    public static final String TAG_PIXEL_SIZE_Y = "Pixel Size Y:";

    public static final String TAG_NOMINAL_AZIMUTH = "Nominal Collection Azimuth:";
    public static final String TAG_NOMINAL_ELEVATION = "Nominal Collection Elevation:";
    public static final String TAG_SUN_ANGLE_ELEVATION = "Sun Angle Elevation:";
    public static final String TAG_SUN_ANGLE_AZIMUTH = "Sun Angle Azimuth:";

    public static final String TAG_ORDER_PIXEL_SIZE = "Product Order Pixel Size:";

}
