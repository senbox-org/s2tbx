package org.esa.s2tbx.s2msi.aerosol.lut;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 16.12.2016
 * Time: 14:37
 *
 * @author olafd
 */
public class S2LutConstants {

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

    public static final float[][] dimValues = new float[][]{
            {500, 1000, 1500, 2000, 3000, 4000, 5000},     // water_vapour
            {0.05f, 0.075f, 0.1f, 0.125f, 0.15f, 0.175f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f},  // aerosol_depth
            {0, 10, 20, 30, 40, 50, 60, 70},   // sun_zenith_angle
            {0, 10, 20, 30, 40, 50, 60},   // view_zenith_angle
            {0, 30, 60, 90, 120, 150, 180},   // relative_azimuth
            {0.0f, 0.5f, 1.0f, 2.0f, 3.0f, 4.0f},   // altitude
            {0.0f, 1.0f, 2.0f, 3.0f},   // aerosol_type (as float)
            {0.0f},   // model_type (as float)
            {0.33176f},   // ozone_content
            {380},   // co2_mixing_ratio
            {0.443f, 0.49f, 0.56f, 0.665f, 0.705f, 0.74f, 0.783f, 0.842f, 0.865f, 0.945f, 1.375f, 1.61f, 2.19f}};  // wavelengths

    public static final int LUT_RESULT_VECTOR_LENGTH = 7;
}
