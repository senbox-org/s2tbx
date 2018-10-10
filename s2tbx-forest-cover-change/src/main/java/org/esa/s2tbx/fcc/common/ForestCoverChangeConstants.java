package org.esa.s2tbx.fcc.common;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class ForestCoverChangeConstants {

    public static final String CCI_LAND_COVER_NAME = "CCILandCover-2015";
    public static final String CCI_LAND_COVER_MAP_INDICES = "40, 50, 60, 61, 62, 70, 71, 72, 80, 81, 82, 90, 100, 110, 160, 170";

    public static final int PREVIOUS_MASK_NO_DATA_VALUE = -1;
    public static final int CURRENT_MASK_NO_DATA_VALUE = -2;

    public static final int NO_DATA_VALUE = 0;
    public static final int PREVIOUS_VALUE = 1;
    public static final int CURRENT_VALUE = 2;
    public static final int COMMON_VALUE = 3;
    public static final int PREVIOUS_MASK_VALUE = 4;
    public static final int CURRENT_MASK_VALUE = 5;

    public static final float CONFIDENCE_LEVEL_90 = 0.9f;
    public static final float CONFIDENCE_LEVEL_95 = 0.95f;
    public static final float CONFIDENCE_LEVEL_99 = 0.99f;
    public static final float MINIMUM_SPECTRAL_WAVE_LENGTH_RED_BAND = 630.0f;
    public static final float MAXIMUM_SPECTRAL_WAVE_LENGTH_RED_BAND = 690.0f;
    public static final float MINIMUM_SPECTRAL_WAVE_LENGTH_NIR_BAND = 760.0f;
    public static final float MAXIMUM_SPECTRAL_WAVE_LENGTH_NIR_BAND = 900.0f;
    public static final float MINIMUM_SPECTRAL_WAVE_LENGTH_SWIR_BAND = 1550.0f;
    public static final float MAXIMUM_SPECTRAL_WAVE_LENGTH_SWIR_BAND = 1750.0f;
    public static final float MINIMUM_SPECTRAL_WAVE_LENGTH_SWIR2_BAND = 2080.0f;
    public static final float MAXIMUM_SPECTRAL_WAVE_LENGTH_SWIR2_BAND = 2350.0f;

    public static final String[] SENTINEL_MASK_NAMES;
    static {
        SENTINEL_MASK_NAMES = new String[]{"scl_dark_feature_shadow", "scl_cloud_shadow", "scl_water", "scl_cloud_low_proba",
                                            "scl_cloud_medium_proba", "scl_cloud_high_proba", "scl_thin_cirrus", "scl_snow_ice"};
    }
}
