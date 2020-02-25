package org.esa.s2tbx.dataio.muscate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by obarrile on 26/01/2017.
 */
public class MuscateImage {

    public static final String AEROSOL_OPTICAL_THICKNESS_IMAGE = "Aerosol_Optical_Thickness";
    public static final String FLAT_REFLECTANCE_IMAGE = "Flat_Reflectance";
    public static final String SURFACE_REFLECTANCE_IMAGE = "Surface_Reflectance";
    public static final String WATER_VAPOR_CONTENT_IMAGE = "Water_Vapor_Content";

    String nature;
    String format;
    String encoding;
    String endianness;
    String compression;
    private final List<String> imageFileList;

    public MuscateImage() {
        this.imageFileList = new ArrayList<>(17);
    }

    public void addMuscateImageFile(String path) {
        imageFileList.add(path);
    }

    public List<String> getImageFiles() {
        return imageFileList;
    }
}
