package org.esa.s2tbx.s2msi.idepix.util;

/**
 * IDEPIX constants
 *
 * @author Olaf Danne
 */
public class S2IdepixConstants {

    public static final int IDEPIX_INVALID = 0;
    public static final int IDEPIX_CLOUD = 1;
    public static final int IDEPIX_CLOUD_AMBIGUOUS = 2;
    public static final int IDEPIX_CLOUD_SURE = 3;
    public static final int IDEPIX_CLOUD_BUFFER = 4;
    public static final int IDEPIX_CLOUD_SHADOW = 5;
    public static final int IDEPIX_CIRRUS_AMBIGUOUS = 6;
    public static final int IDEPIX_CIRRUS_SURE = 7;
    public static final int IDEPIX_COASTLINE = 8;
    public static final int IDEPIX_CLEAR_SNOW = 9;
    public static final int IDEPIX_CLEAR_LAND = 10;
    public static final int IDEPIX_CLEAR_WATER = 11;
    public static final int IDEPIX_LAND = 12;
    public static final int IDEPIX_WATER = 13;
    public static final int IDEPIX_BRIGHT = 14;
    public static final int IDEPIX_WHITE = 15;
    public static final int IDEPIX_BRIGHTWHITE = 16;
    public static final int IDEPIX_HIGH = 17;
    public static final int IDEPIX_VEG_RISK = 18;
    public static final int IDEPIX_SEAICE = 19;

    public static final int NO_DATA_VALUE = -1;


    public static final String[] S2_MSI_REFLECTANCE_BAND_NAMES = {
            "B1",
            "B2",
            "B3",
            "B4",
            "B5",
            "B6",
            "B7",
            "B8",
            "B8A",
            "B9",
            "B10",
            "B11",
            "B12"
    };

    public static final String[] S2_MSI_ANNOTATION_BAND_NAMES = {
            "sun_zenith",
            "view_zenith_mean",
            "sun_azimuth",
            "view_azimuth_mean",
    };

    public static final float[] S2_MSI_WAVELENGTHS = {
            443.0f,     // B1
            490.0f,     // B2
            560.0f,     // B3
            665.0f,     // B4
            705.0f,     // B5
            740.0f,     // B6
            783.0f,     // B7
            842.0f,     // B8
            865.0f,     // B8A
            945.0f,     // B9
            1375.0f,    // B10
            1610.0f,    // B11
            2190.0f     // B12
    };

    public static final double[] S2_SOLAR_IRRADIANCES = {
            1913.57,     // B1
            1941.63,     // B2
            1822.61,     // B3
            1512.79,     // B4
            1425.56,     // B5
            1288.32,     // B6
            1163.19,     // B7
            1036.39,     // B8
            955.19,      // B8A
            813.04,      // B9
            367.15,      // B10
            245.59,      // B11
            85.25        // B12
    };

    public static final String INPUT_INCONSISTENCY_ERROR_MESSAGE =
            "Selected cloud screening algorithm cannot be used with given input product. \n\n" +
                    "Input product must be S2 MSI L1C product.";

}
