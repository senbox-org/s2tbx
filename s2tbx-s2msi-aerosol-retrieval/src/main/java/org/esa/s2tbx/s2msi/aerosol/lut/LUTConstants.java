package org.esa.s2tbx.s2msi.aerosol.lut;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 16.12.2016
 * Time: 14:37
 *
 * @author olafd
 */
public class LUTConstants {

    public static final String[] dimNames =
            new String[]{"water_vapour",
                    "aerosol_depth",
                    "sun_zenith_angle",
                    "view_zenith_angle",
                    "relative_azimuth",
                    "altitude",
                    "aerosol_type",
                    "model_type",
                    "ozone_content",
                    "co2_mixing_ratio",
                    "wavelengths"};

    public static final double[][] dimValues = new double[][]{
            {500, 1000, 1500, 2000, 3000, 4000, 5000},     // water_vapour
            {0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2},  // aerosol_depth
            {0, 10, 20, 30, 40, 50, 60, 70},   // sun_zenith_angle
            {0, 10, 20, 30, 40, 50, 60},   // view_zenith_angle
            {0, 30, 60, 90, 120, 150, 180},   // relative_azimuth
            {0.0, 0.5, 1.0, 2.0, 3.0, 4.0},   // altitude
            {0.0, 1.0, 2.0, 3.0},   // aerosol_type (as double)
            {0.0},   // model_type (as double)
            {0.33176},   // ozone_content
            {380},   // co2_mixing_ratio
            {0.443, 0.49, 0.56, 0.665, 0.705, 0.74, 0.783, 0.842, 0.865, 0.945, 1.375, 1.61, 2.19}};  // wavelengths

    public static final int LUT_RESULT_VECTOR_LENGTH = 7;
}
