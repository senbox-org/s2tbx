package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum GDALVersion {

    GDAL_300_FULL("3-0-0", false),
    GDAL_30X_JNI("3-0-X", true),
    GDAL_24X_JNI("2-4-X", true),
    GDAL_23X_JNI("2-3-X", true),
    GDAL_22X_JNI("2-2-X", true),
    GDAL_21X_JNI("2-1-X", true),
    GDAL_20X_JNI("2-0-X", true);

    private static final String VERSION_NAME = "{version}";
    private static final String JNI_NAME = "{jni}";
    private static final String DIR_NAME = "gdal-" + VERSION_NAME + JNI_NAME;
    private static final String ZIP_NAME = DIR_NAME + ".zip";
    private static final String GDAL_NATIVE_LIBRARIES_ROOT = "gdal";
    private static final String GDAL_NATIVE_LIBRARIES_SRC = "auxdata/gdal";
    private static final String GDAL_JNI_LIBRARY_FILE = "java/gdal.jar";

    private static final String GDAL_INFO_CMD = "gdalinfo --version";
    private static final String GDAL_V30X = "3.0.X";
    private static final String GDAL_V24X = "2.4.X";
    private static final String GDAL_V23X = "2.3.X";
    private static final String GDAL_V22X = "2.2.X";
    private static final String GDAL_V21X = "2.1.X";
    private static final String GDAL_V20X = "2.0.X";

    private static final Logger logger = Logger.getLogger(GDALInstaller.class.getName());

    String name;
    boolean jni;
    OSCategory osCategory;

    GDALVersion(String name, boolean jni) {
        this.name = name;
        this.jni = jni;
    }

    public static GDALVersion getGDALVersion(OSCategory osCategory) {
        GDALVersion gdalVersion = GDAL_300_FULL;
        try (java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(GDAL_INFO_CMD).getInputStream()).useDelimiter("\\A")) {
            String result = s.hasNext() ? s.next() : "";
            String version = result.replaceAll("[\\s\\S]*?(\\d*\\.\\d*)[\\s\\S]*$", "$1.X");
            switch (version) {
                case GDAL_V30X:
                    gdalVersion = GDAL_30X_JNI;
                    break;
                case GDAL_V24X:
                    gdalVersion = GDAL_24X_JNI;
                    break;
                case GDAL_V23X:
                    gdalVersion = GDAL_23X_JNI;
                    break;
                case GDAL_V22X:
                    gdalVersion = GDAL_22X_JNI;
                    break;
                case GDAL_V21X:
                    gdalVersion = GDAL_21X_JNI;
                    break;
                case GDAL_V20X:
                    gdalVersion = GDAL_20X_JNI;
                    break;
                default:
                    if (!version.isEmpty()) {
                        throw new ExceptionInInitializerError("Incompatible GDAL version found: " + version + ". Please uninstall!");
                    } else {
                        logger.log(Level.INFO, "GDAL not found on system. Internal GDAL 3.0.0 from distribution will be used.");
                    }
            }
            logger.log(Level.INFO, "GDAL "+version+" found on system. JNI driver will be used.");
        } catch (IOException ignored) {
            logger.log(Level.INFO, "GDAL not found on system. Internal GDAL 3.0.0 from distribution will be used.");
        }

        gdalVersion.setOsCategory(osCategory);
        return gdalVersion;
    }

    public boolean isJni() {
        return jni;
    }

    public OSCategory getOsCategory() {
        return osCategory;
    }

    private void setOsCategory(OSCategory osCategory) {
        this.osCategory = osCategory;
    }

    private String getDirName() {
        if (jni) {
            return DIR_NAME.replace(VERSION_NAME, this.name).replace(JNI_NAME, "-jni");
        } else {
            return DIR_NAME.replace(VERSION_NAME, this.name).replace(JNI_NAME, "");
        }
    }

    private String getZipName() {
        if (jni) {
            return ZIP_NAME.replace(VERSION_NAME, this.name).replace(JNI_NAME, "-jni");
        } else {
            return ZIP_NAME.replace(VERSION_NAME, this.name).replace(JNI_NAME, "");
        }
    }

    private String getDirectory() {
        return osCategory.operatingSystemName + "/" + osCategory.architecture;
    }

    public URL getZipFileURLFromSources() {
        String zipFileDirectoryFromSources = GDAL_NATIVE_LIBRARIES_SRC + "/" + getDirectory() + "/" + getZipName();
        try {
            return getClass().getClassLoader().getResource(zipFileDirectoryFromSources.replace(File.separator, "/"));
        } catch (Exception ignored) {
            return null;
        }
    }

    public Path getZipFilePath() {
        Path zipFileDirectory = getNativeLibrariesRootFolderPath();
        return zipFileDirectory.resolve(getDirName()).resolve(getZipName());
    }

    public URL getEnvironmentVariablesFilePathFromSources() {
        String evFileNameFromSources = System.mapLibraryName(osCategory.getEnvironmentVariablesFileName());
        String evFileDirectoryFromSources = GDAL_NATIVE_LIBRARIES_SRC + "/" + getDirectory() + "/" + evFileNameFromSources;
        try {
            return getClass().getClassLoader().getResource(evFileDirectoryFromSources.replace(File.separator, "/"));
        } catch (Exception ignored) {
            return null;
        }
    }

    public Path getEnvironmentVariablesFilePath() {
        Path zipFileDirectory = getNativeLibrariesRootFolderPath();
        String evFileNameFromSources = System.mapLibraryName(osCategory.getEnvironmentVariablesFileName());
        return zipFileDirectory.resolve(evFileNameFromSources);
    }

    public Path getNativeLibrariesRootFolderPath() {
        Path snapNativeLibrariesRootPath = SystemUtils.getAuxDataPath();
        return snapNativeLibrariesRootPath.resolve(GDAL_NATIVE_LIBRARIES_ROOT);
    }

    public Path getJNILibraryFilePath() {
        return getNativeLibrariesRootFolderPath().resolve(getDirName()).resolve(GDAL_JNI_LIBRARY_FILE);
    }
}

