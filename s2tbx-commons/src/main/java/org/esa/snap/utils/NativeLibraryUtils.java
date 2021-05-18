package org.esa.snap.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Helper methods for native libraries registration.
 *
 * @author Cosmin Cara
 * @since 5.0.0
 * @deprecated since 8.0.0, use {@link org.esa.snap.core.util.NativeLibraryUtils} instead
 */
@Deprecated()
public class NativeLibraryUtils {

    public static void registerNativePaths(Path... paths) {
        org.esa.snap.core.util.NativeLibraryUtils.registerNativePaths(Arrays.stream(paths).map(Path::toString).collect(Collectors.toList()).toArray(new String[paths.length]));
    }

    public static void registerNativePaths(String... paths) {
        org.esa.snap.core.util.NativeLibraryUtils.registerNativePaths(paths);
    }

    /**
     * Loads library either from a JAR archive, or from file system
     * The file from JAR is copied into system temporary directory and then loaded.
     *
     * @param path          The path from which the load is attempted
     * @param libraryName   The name of the library to be loaded (without extension)
     * @throws IOException              If temporary file creation or read/write operation fails
     */
    public static void loadLibrary(String path, String libraryName) throws IOException {
        org.esa.snap.core.util.NativeLibraryUtils.loadLibrary(path, libraryName);
    }

    public static String getOSFamily() {
        return org.esa.snap.core.util.NativeLibraryUtils.getOSFamily();
    }
}
