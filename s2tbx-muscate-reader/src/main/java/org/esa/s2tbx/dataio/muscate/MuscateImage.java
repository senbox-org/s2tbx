package org.esa.s2tbx.dataio.muscate;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by obarrile on 26/01/2017.
 */
public class MuscateImage {
    String nature;
    String format;
    String encoding;
    String endianness;
    String compression;
    private ArrayList<String> imageFileList = new ArrayList<>(17);

    public void addMuscateImageFile(String path) {
        imageFileList.add(path);
    }

    public ArrayList<String> getImageFiles() {
        return imageFileList;
    }

}
