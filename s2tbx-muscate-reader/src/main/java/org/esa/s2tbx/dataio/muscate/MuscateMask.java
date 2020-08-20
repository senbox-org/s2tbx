package org.esa.s2tbx.dataio.muscate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by obarrile on 26/01/2017.
 */
public class MuscateMask {

    public static final String AOT_INTERPOLATION_MASK = "AOT_Interpolation";
    public static final String DETAILED_CLOUD_MASK = "Detailed_Cloud";
    public static final String CLOUD_MASK = "Cloud";
    public static final String CLOUD_SHADOW_MASK = "Cloud_Shadow";
    public static final String EDGE_MASK = "Edge";
    public static final String SATURATION_MASK = "Saturation";
    public static final String GEOPHYSICS_MASK = "Geophysics";
    public static final String DETECTOR_FOOTPRINT_MASK = "Detector_Footprint";
    public static final String DEFECTIVE_PIXEL_MASK = "Defective_Pixel";
    public static final String HIDDEN_SURFACE_MASK = "Hidden_Surface";
    public static final String SNOW_MASK = "Snow";
    public static final String SUN_TOO_LOW_MASK = "Sun_Too_Low";
    public static final String TANGENT_SUN_MASK = "Tangent_Sun";
    public static final String TOPOGRAPHY_SHADOW_MASK = "Topography_Shadow";
    public static final String WATER_MASK = "Water";
    public static final String WVC_INTERPOLATION_MASK = "WVC_Interpolation";

    public static final float AOT_INTERPOLATION_MASK_VERSION = 1.55f;
    public static final float CLOUD_MASK_VERSION = 1.95f;
    public static final float CLOUD_SHADOW_MASK_VERSION = 2.05f;

    String nature;
    String format;
    String encoding;
    String endianness;
    private final List<MuscateMaskFile> maskFileList;

    public MuscateMask() {
        this.maskFileList = new ArrayList<>(17);
    }

    public void addMuscateMaskFile(MuscateMaskFile muscateMaskFile) {
        maskFileList.add(muscateMaskFile);
    }

    public List<MuscateMaskFile> getMaskFiles() {
        return maskFileList;
    }
}
