package org.esa.s2tbx.dataio.kompsat2.internal;


import java.io.File;

/**
 * Holder class for string constants.
 *
 * The band wavelength range is from https://earth.esa.int/web/guest/missions/3rd-party-missions/instruments/msc
 *
 * @author Razvan Dumitrascu
 * @author Denisa Stefanescu
 */

public class Kompsat2Constants {

    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String[] FORMAT_NAMES = new String[]{"Kompsat2GeoTIFF"};
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml", ".zip"};
    public static final String DESCRIPTION = "Kompsat 2 Data Products";
    public static final String METADATA_FILE_SUFFIX = ".MD.xml";
    public static final String PRODUCT_FILE_SUFFIX = ".SIP.ZIP";
    public static final String IMAGE_EXTENSION = ".tif";
    public static final String IMAGE_METADATA_EXTENSION = ".txt";
    public static final String ARCHIVE_FILE_EXTENSION = ".zip";
    public static final String PRODUCT_GENERIC_NAME = "Kompsat2 Product";
    public static final String[] BAND_NAMES = new String[]{"MS1", "MS2", "MS3", "MS4", "PAN"};
    public static final String[] FILE_NAMES = new String[]{"M1", "M2", "M3", "M4", "P"};
    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[]{
            "KO2_OPER_MSC_MUL_\\d{1}\\w{1}_\\d{8}T\\d{6}_\\d{8}T\\d{6}_\\d{6}_\\d{4}_\\d{4}_\\d{4}\\.MD.XML",
            "KO2_OPER_MSC_MUL_\\d{1}\\w{1}_\\d{8}T\\d{6}_\\d{8}T\\d{6}_\\d{6}_\\d{4}_\\d{4}_\\d{4}.*.ZIP"};
    public static final String[] KOMSAT2_RGB_PROFILE = new String[]{"MS4", "MS1", "MS2"};
//    public static final String KOMPSAT2_UNIT = "mW cm-2 sr-1 μm-1";// issues on windows testing platform with special characters, therefore use the characters codes instead
    public static final String KOMPSAT2_UNIT = "mW cm-2 sr-1 \u03bcm-1";
    public static final Double[] KOMPSAT2_GAIN_VALUES = new Double[]{0.0015092, 0.0021630, 0.0017513, 0.0014689};

    public static final String KOMPSAT2_UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String KOMPSAT2_PRODUCT = "Kompsat 2 Product";
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
    public static final String PATH_ZIP_FILE_NAME = "/EarthObservation/metaDataProperty/EarthObservationMetaData/vendorSpecific/SpecificInformation/localValue";
    public static final String PATH_ID = "/EarthObservation/metaDataProperty/EarthObservationMetaData/identifier";
    public static final String PATH_PRODUCT_TYPE = "/EarthObservation/metaDataProperty/EarthObservationMetaData/productType";


    /*
     * Raster metadata element paths
     */
    public static final String TAG_BITS_PER_PIXEL = "AUX_BITS_PER_PIXEL";
    public static final String TAG_NUMBER_COLUMNS_MS_IMAGE = "AUX_SAMPLES_PER_LINE_MS";
    public static final String TAG_NUMBER_COLUMNS_PAN_IMAGE = "AUX_SAMPLES_PER_LINE_PAN";
    public static final String TAG_NUMBER_ROWS_MS_IMAGE = "AUX_LINES_PER_IMAGE_MS";
    public static final String TAG_NUMBER_ROWS_PAN_IMAGE = "AUX_LINES_PER_IMAGE_PAN";
    public static final String TAG_PIXEL_SIZE = "AUX_IMAGE_GSD_METER";
    public static final String TAG_BAND_WIDTH = "INST_BAND_WIDTH";
    public static final String TAG_AZIMUTH_ANGLE = "AUX_IMAGE_SATELLITE_AZIMUTH_DEG";
    public static final String TAG_INCIDENCE_ANGLE = "AUX_IMAGE_SATELLITE_INCIDENCE_DEG";

    public enum BandWaveLengthConstants {
        PAN("PAN", 500, 900),
        MS4("MS4", 760, 900),
        MS3("MS3", 630, 690),
        MS2("MS2", 520, 600),
        MS1("MS1", 450, 520);

        private String physicalName;
        private float wavelengthMin;
        private float wavelengthMax;

        BandWaveLengthConstants(String physicalName,
                                float wavelengthMin,
                                float wavelengthMax) {
            this.physicalName = physicalName;
            this.wavelengthMin = wavelengthMin;
            this.wavelengthMax = wavelengthMax;
        }

        public String getPhysicalName() {
            return physicalName;
        }

        public float getWavelengthMin() {
            return wavelengthMin;
        }

        public float getWavelengthMax() {
            return wavelengthMax;
        }

        public float getWavelengthCentral() {
            return (getWavelengthMax() + getWavelengthMin()) / 2;
        }

        public static float getWavelengthCentral(String physicalName) {
            for (BandWaveLengthConstants band : BandWaveLengthConstants.values()) {
                if (band.getPhysicalName().equalsIgnoreCase(physicalName)) {
                    return band.getWavelengthCentral();
                }
            }
            return 0.0f;
        }
    }
}
