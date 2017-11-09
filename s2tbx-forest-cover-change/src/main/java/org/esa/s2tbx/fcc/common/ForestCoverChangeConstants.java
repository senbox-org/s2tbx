package org.esa.s2tbx.fcc.common;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class ForestCoverChangeConstants {
    public static final String LAND_COVER_NAME = "CCILandCover-2015";

    public static final int NO_DATA_VALUE = 0;
    public static final int PREVIOUS_VALUE = 1;
    public static final int CURRENT_VALUE = 2;
    public static final int COMMON_VALUE = 3;

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
    public static final IntSet LAND_COVER_VALID_PIXELS = new IntOpenHashSet();
    static {
        SENTINEL_MASK_NAMES = new String[]{"scl_dark_feature_shadow", "scl_cloud_shadow", "scl_water", "scl_cloud_low_proba",
                                            "scl_cloud_medium_proba", "scl_cloud_high_proba", "scl_thin_cirrus", "scl_snow_ice"};

        LAND_COVER_VALID_PIXELS.add(40);
        LAND_COVER_VALID_PIXELS.add(50);
        LAND_COVER_VALID_PIXELS.add(60);
        LAND_COVER_VALID_PIXELS.add(61);
        LAND_COVER_VALID_PIXELS.add(62);
        LAND_COVER_VALID_PIXELS.add(70);
        LAND_COVER_VALID_PIXELS.add(71);
        LAND_COVER_VALID_PIXELS.add(72);
        LAND_COVER_VALID_PIXELS.add(80);
        LAND_COVER_VALID_PIXELS.add(81);
        LAND_COVER_VALID_PIXELS.add(82);
        LAND_COVER_VALID_PIXELS.add(90);
        LAND_COVER_VALID_PIXELS.add(100);
        LAND_COVER_VALID_PIXELS.add(110);
        LAND_COVER_VALID_PIXELS.add(160);
        LAND_COVER_VALID_PIXELS.add(170);
    }
}
