package org.esa.s2tbx.s2msi.idepix.util;

/**
 * IDEPIX constants
 *
 * @author Olaf Danne
 */
public class IdepixConstants {

    public static final int F_INVALID = 0;
    public static final int F_CLOUD = 1;
    public static final int F_CLOUD_AMBIGUOUS = 2;
    public static final int F_CLOUD_SURE = 3;
    public static final int F_CLOUD_BUFFER = 4;
    public static final int F_CLOUD_SHADOW = 5;
    public static final int F_COASTLINE = 6;
    public static final int F_CLEAR_SNOW = 7;
    public static final int F_CLEAR_LAND = 8;
    public static final int F_CLEAR_WATER = 9;
    public static final int F_LAND = 10;
    public static final int F_WATER = 11;
    public static final int F_BRIGHT = 12;
    public static final int F_WHITE = 13;
    public static final int F_BRIGHTWHITE = 14;
    public static final int F_HIGH = 15;
    public static final int F_VEG_RISK = 16;
    public static final int F_SEAICE = 17;

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
            "view_zenith",
            "sun_azimuth",
            "view_azimuth",
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


    public static final String INPUT_INCONSISTENCY_ERROR_MESSAGE =
            "Selected cloud screening algorithm cannot be used with given input product. \n\n" +
                    "Valid are: MERIS, VGT, AATSR, AVHRR, MODIS, Landsat8, SeaWIFS, colocated MERIS/AATSR L1b products.";

}
