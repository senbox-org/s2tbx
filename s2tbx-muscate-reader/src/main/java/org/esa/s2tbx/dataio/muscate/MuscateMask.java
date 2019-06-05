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
    private ArrayList<MuscateMaskFile> maskFileList = new ArrayList<>(17);

    public void addMuscateMaskFile(MuscateMaskFile muscateMaskFile) {
        maskFileList.add(muscateMaskFile);
    }

    public ArrayList<MuscateMaskFile> getMaskFiles() {
        return maskFileList;
    }
}
