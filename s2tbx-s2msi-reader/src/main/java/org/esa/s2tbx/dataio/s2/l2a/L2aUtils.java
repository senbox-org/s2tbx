package org.esa.s2tbx.dataio.s2.l2a;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by obarrile on 14/06/2016.
 */
public class L2aUtils {

    public static boolean checkGranuleSpecificFolder(File fileGranule, String specificFolder) {

        if (specificFolder.equals("Multi"))
            return true;
        Path rootPath = fileGranule.toPath().getParent();
        File imgFolder = rootPath.resolve("IMG_DATA").toFile();
        File[] files = imgFolder.listFiles();

        if (files != null) {
            for (File imgData : files) {
                if (imgData.isDirectory()) {
                    if (imgData.getName().equals("R" + specificFolder)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkMetadataSpecificFolder(File fileMetadata, String specificFolder) {

        if (specificFolder.equals("Multi"))
            return true;
        Path rootPath = fileMetadata.toPath().getParent();
        File granuleFolder = rootPath.resolve("GRANULE").toFile();
        File[] files = granuleFolder.listFiles();

        if (files != null) {
            for (File granule : files) {
                if (granule.isDirectory()) {
                    Path granulePath = new File(granule.toString()).toPath();
                    File internalGranuleFolder = granulePath.resolve("IMG_DATA").toFile();
                    File[] files2 = internalGranuleFolder.listFiles();
                    if (files2 != null) {
                        for (File imgData : files2) {
                            if (imgData.isDirectory()) {
                                if (imgData.getName().equals("R" + specificFolder)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
