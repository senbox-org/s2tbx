package org.esa.s2tbx.dataio.muscate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by obarrile on 26/01/2017.
 */
public class MuscateMask {
    String nature;
    String format;
    String encoding;
    String endianness;
    private ArrayList<String> maskFileList = new ArrayList<>(17);

    public void addMuscateMaskFile(String path) {
        maskFileList.add(path);
    }

    public ArrayList<String> getMaskFiles() {
        return maskFileList;
    }
}
