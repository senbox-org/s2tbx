package org.esa.beam.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by kraftek on 2/20/2015.
 */
public class FileHelper {

    /**
     * Gets a file (if it exists) or creates a new one.
     * If intermediate directories do not exist, they will be created.
     *
     * @param basePath  The parent folder
     * @param pathFragments Additional subfolders that should end with the file name
     * @return  The File object
     * @throws IOException
     */
    public static File getFile(String basePath, String...pathFragments) throws IOException {
        File file = new File(basePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (pathFragments != null) {
            for (int i = 0; i < pathFragments.length; i++) {
                file = new File(file, pathFragments[i]);
                if (i < pathFragments.length - 1) {
                    file.mkdirs();
                } else {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                }
            }
        }
        return file;
    }
}
