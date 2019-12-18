package org.esa.s2tbx.dataio.muscate;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.engine_utilities.util.FileSystemUtils;

import java.io.FileNotFoundException;

/**
 * Created by jcoravu on 17/12/2019.
 */
class ProductFilePathsHelper {

    private final String[] filePaths;
    private final String fileSystemSeparator;

    ProductFilePathsHelper(String[] filePaths, String fileSystemSeparator) {
        this.filePaths = filePaths;
        this.fileSystemSeparator = fileSystemSeparator;
    }

    private String findFile(String relativeFilePath) {
        for (int i=0; i<this.filePaths.length; i++) {
            if (this.filePaths[i].equalsIgnoreCase(relativeFilePath)) {
                return relativeFilePath;
            }
        }
        return null;
    }

    String computeImageRelativeFilePath(VirtualDirEx productDirectory, String tiffImageRelativeFilePath) throws FileNotFoundException {
        if (productDirectory.isArchive()) {
            // the product path is an archive
            if (findFile(tiffImageRelativeFilePath) == null) {
                // the tiff image does not exist
                String name = productDirectory.getBaseFile().getName();
                int extensionIndex = name.lastIndexOf(".");
                if (extensionIndex > 0) {
                    name = name.substring(0, extensionIndex);
                }
                String relativeFilePath = name + this.fileSystemSeparator + FileSystemUtils.replaceFileSeparator(tiffImageRelativeFilePath, this.fileSystemSeparator);
                String tiffImageFilePath = findFile(relativeFilePath);
                if (tiffImageFilePath == null) {
                    throw new FileNotFoundException("The tiff image file '" + tiffImageRelativeFilePath + "' does not exist.");
                }
                return tiffImageFilePath;
            }
        }
        return tiffImageRelativeFilePath;
    }
}
