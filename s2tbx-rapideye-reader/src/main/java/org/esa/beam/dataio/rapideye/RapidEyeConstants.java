package org.esa.beam.dataio.rapideye;

import org.esa.beam.framework.datamodel.ProductData;

import java.io.File;

/**
 * Utility class for holding various string constants for Rapid Eye reader.
 *
 * @author Cosmin Cara
 */
public class RapidEyeConstants {
    // constants for plugins
    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String L1_DESCRIPTION = "RapidEye L1 Data Products";
    public static final String L3_DESCRIPTION = "RapidEye L3 Data Products";
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml", ".XML", ".zip", ".ZIP"};
    public static final String[] L1_FORMAT_NAMES = new String[]{"RapidEyeNITF"};
    public static final String[] L3_FORMAT_NAMES = new String[]{"RapidEyeGeoTIFF"};
    //public static final String L1_FOLDER_PATTERN = "(?:19[0-9]{2}|2[0-9]{3})-(?:0[1-9]|1[012])-(?:[123]0|[012][1-9]|31)T(?:[01][0-9]|2[0-3])(?:[0-5][0-9])(?:[0-5][0-9])_RE\\d{1}_([1-3][ABC])-NAC_\\d{8}_\\d{6}";
    //public static final String L3_FOLDER_PATTERN = "\\d{7}_(?:19[0-9]{2}|2[0-9]{3})-(?:0[1-9]|1[012])-(?:[123]0|[012][1-9]|31)_RE\\d{1}_(3[ABC])_\\d{6}";
    public static final String[] L1_FILENAME_PATTERNS = new String[]{
            ".*zip",
            ".*_band[1-6]\\.ntf",
            ".*_metadata\\.xml",
                                                                       /*".*_rpc\\xml",
                                                                       ".*_sci\\.xml",
                                                                       ".*_udm\\.(tif|tiff)",
                                                                       ".*_browse\\.(tif|tiff)"*/};
    public static final String[] L1_MINIMAL_PRODUCT_PATTERNS = new String[]{
            ".*_band[1-6]\\.ntf",
            ".*_metadata\\.xml"};

    public static final String[] L3_FILENAME_PATTERNS = new String[]{
            ".*zip",
            ".*\\.tif",
            ".*_metadata\\.xml",
                                                                       /*".*_udm\\.(tif|tiff)",
                                                                       ".*_browse\\.(tif|tiff)"*/};
    public static final String[] L3_MINIMAL_PRODUCT_PATTERNS = new String[]{
            ".*\\.tif",
            ".*_metadata\\.xml"};
    public static final String[] NOT_L3_FILENAME_PATTERNS = {".*\\.ntf"};
    public static final String L1_FILENAME_PATTERNS_ALL = "(.*\\.zip)|(.*_band[1-6]\\.ntf)|(*_metadata\\.xml)";
    public static final String METADATA_FILE_SUFFIX = "metadata.xml";
    public static final String BROWSE_FILE_SUFFIX = "browse.tif";
    public static final String METADATA_DISPLAY_NAME = "XML Metadata";
    public static final String PRODUCT_GENERIC_NAME = "RapidEye Product";

    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    public static final double RADIOMETRIC_SCALE_FACTOR = 0.01;
    public static final String TAG_EARTH_OBSERVATION_META_DATA = "EarthObservationMetaData";
    public static final String TAG_IDENTIFIER = "identifier";
    public static final String TAG_RESULT_OF = "resultOf";
    public static final String TAG_PRODUCT = "product";
    public static final String TAG_PRODUCT_INFORMATION = "ProductInformation";
    public static final String TAG_PRODUCT_FORMAT = "productFormat";
    public static final String TAG_NUM_ROWS = "numRows";
    public static final String TAG_NUM_COLUMNS = "numColumns";
    public static final String TAG_FILE_NAME = "fileName";
    public static final String TOKEN_BAND_N = "bandN";
    public static final String TOKEN_BAND_X = "band%d";
    public static final String TAG_SPATIAL_REFERENCE_SYSTEM = "spatialReferenceSystem";
    public static final String TAG_EPSG_CODE = "epsgCode";
    public static final String TAG_GEODETIC_DATUM = "geodeticDatum";
    public static final String TAG_PROJECTION = "projection";
    public static final String TAG_PROJECTION_ZONE = "projectionZone";
    public static final String TAG_START_DATE_TIME = "startDateTime";
    public static final String TAG_END_DATE_TIME = "endDateTime";
    public static final String TAG_BAND_SPECIFIC_METADATA = "bandSpecificMetadata";
    public static final String TAG_RADIOMETRIC_SCALE_FACTOR = "radiometricScaleFactor";
    public static final String TAG_PIXEL_FORMAT = "pixelFormat";
    public static final String TAG_EARTH_OBSERVATION_RESULT = "EarthObservationResult";
    public static final String TAG_BAND_NUMBER = "bandNumber";
    public static final String TAG_NUM_BANDS = "numBands";
    public static final String TAG_TOP_LEFT = "topLeft";
    public static final String TAG_TOP_RIGHT = "topRight";
    public static final String TAG_BOTTOM_LEFT = "bottomLeft";
    public static final String TAG_BOTTOM_RIGHT = "bottomRight";
    public static final String TAG_TARGET = "target";
    public static final String TAG_FOOTPRINT = "Footprint";
    public static final String TAG_GEOGRAPHIC_LOCATION = "geographicLocation";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String NTF_EXTENSION = ".ntf";
    public static final String TIF_EXTENSION = ".tif";
    public static final String METADATA_EXTENSION = ".xml";
    public static final String TAG_META_DATA_PROPERTY = "metaDataProperty";
    public static final String TAG_BROWSE = "browse";
    public static final String TAG_BROWSE_INFORMATION = "BrowseInformation";
    public static final String UDM_FILE_SUFFIX = "udm.tif";
    public static final String TAG_PRODUCT_TYPE = "productType";
    public static final String PROFILE_L1 = "L1";
    public static final String PROFILE_L3 = "L3";
    public static final String TAG_RPC = "Rational Polynomial Coefficients";
    public static final String TAG_LINEOFF = "lineOff";
    public static final String TAG_SAMPLEOFF = "sampleOff";
    public static final String TAG_LATOFF = "latOff";
    public static final String TAG_LONGOFF = "longOff";
    public static final String TAG_HEIGHTOFF = "heightOff";
    public static final String TAG_LINESCALE = "lineScale";
    public static final String TAG_SAMPLESCALE = "sampleScale";
    public static final String TAG_LATSCALE = "latScale";
    public static final String TAG_LONGSCALE = "longScale";
    public static final String TAG_HEIGHTSCALE = "heightScale";
    public static final String TAG_LINENUMCOEFF = "lineNumCoeff";
    public static final String TAG_LINEDENCOEFF = "lineDenCoeff";
    public static final String TAG_SAMPLENUMCOEFF = "sampleNumCoeff";
    public static final String TAG_SAMPLEDENCOEFF = "sampleDenCoeff";

    public static final String[] BAND_NAMES = new String[]{"blue", "green", "red", "red_edge", "near_infrared"};
    public static final float[] WAVELENGTHS = new float[]{440, 520, 630, 690, 760};
    public static final float[] BANDWIDTHS = new float[]{70, 70, 55, 40, 90};
    public static double[] SCALING_FACTORS = new double[]{510 / 4095 * RADIOMETRIC_SCALE_FACTOR, 590 / 4095 * RADIOMETRIC_SCALE_FACTOR, 685 / 4095 * RADIOMETRIC_SCALE_FACTOR, 730 / 4095 * RADIOMETRIC_SCALE_FACTOR, 850 / 4095 * RADIOMETRIC_SCALE_FACTOR};
    public static double[] SCALING_OFFSETS = new double[]{440 * RADIOMETRIC_SCALE_FACTOR, 520 * RADIOMETRIC_SCALE_FACTOR, 630 * RADIOMETRIC_SCALE_FACTOR, 690 * RADIOMETRIC_SCALE_FACTOR, 760 * RADIOMETRIC_SCALE_FACTOR};

    public enum PixelFormat {

        UNSIGNED_INTEGER("16U", ProductData.TYPE_UINT16),
        SIGNED_INTEGER("SI", ProductData.TYPE_INT16);

        private final String value;
        private final int dataType;

        private PixelFormat(String value, int dataType) {
            this.value = value;
            this.dataType = dataType;
        }

        @Override
        public String toString() {
            return value;
        }

        public int getDataType() {
            return dataType;
        }
    }
}
