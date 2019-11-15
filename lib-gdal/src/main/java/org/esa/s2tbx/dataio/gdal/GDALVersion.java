package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum GDALVersion {

    GDAL_300_FULL("3-0-0", false),
    GDAL_300_JNI("3-0-0", true),
    GDAL_242_JNI("2-4-2", true),
    GDAL_241_JNI("2-4-1", true),
    GDAL_240_JNI("2-4-0", true),
    GDAL_233_JNI("2-3-3", true),
    GDAL_232_JNI("2-3-2", true),
    GDAL_231_JNI("2-3-1", true),
    GDAL_230_JNI("2-3-0", true),
    GDAL_223_JNI("2-2-3", true),
    GDAL_213_JNI("2-1-3", true);

    private static final String VERSION_NAME = "{version}";
    private static final String JNI_NAME = "{jni}";
    private static final String DIR_NAME = "gdal-" + VERSION_NAME + JNI_NAME;
    private static final String ZIP_NAME = DIR_NAME + ".zip";
    private static final String GDAL_NATIVE_LIBRARIES_ROOT = "gdal";
    private static final String GDAL_NATIVE_LIBRARIES_SRC = "auxdata/gdal";
    private static final String GDAL_JNI_LIBRARY_FILE = "java/gdal.jar";

    private static final String GDAL_INFO_CMD = "gdalinfo --version";
    private static final String GDAL_V300 = "3.0.0";
    private static final String GDAL_V242 = "2.4.2";
    private static final String GDAL_V241 = "2.4.1";
    private static final String GDAL_V240 = "2.4.0";
    private static final String GDAL_V233 = "2.3.3";
    private static final String GDAL_V232 = "2.3.2";
    private static final String GDAL_V231 = "2.3.1";
    private static final String GDAL_V230 = "2.3.0";
    private static final String GDAL_V223 = "2.2.3";
    private static final String GDAL_V213 = "2.1.3";

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
            String version = result.replaceAll("[\\s\\S]*?(\\d*\\.\\d*\\.\\d*)[\\s\\S]*$", "$1");
            switch (version) {
                case GDAL_V300:
                    gdalVersion = GDAL_300_JNI;
                    break;
                case GDAL_V242:
                    gdalVersion = GDAL_242_JNI;
                    break;
                case GDAL_V241:
                    gdalVersion = GDAL_241_JNI;
                    break;
                case GDAL_V240:
                    gdalVersion = GDAL_240_JNI;
                    break;
                case GDAL_V233:
                    gdalVersion = GDAL_233_JNI;
                    break;
                case GDAL_V232:
                    gdalVersion = GDAL_232_JNI;
                    break;
                case GDAL_V231:
                    gdalVersion = GDAL_231_JNI;
                    break;
                case GDAL_V230:
                    gdalVersion = GDAL_230_JNI;
                    break;
                case GDAL_V223:
                    gdalVersion = GDAL_223_JNI;
                    break;
                case GDAL_V213:
                    gdalVersion = GDAL_213_JNI;
                    break;
                default:
                    if (!version.isEmpty()) {
                        throw new ExceptionInInitializerError("Incompatible GDAL version found: " + version + ". Please uninstall!");
                    } else {
                        logger.log(Level.INFO, "GDAL not found on system. Internal GDAL 2.4.1 from distribution will be used.");
                    }
            }
        } catch (IOException ignored) {
            //nothing
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

    public Path getZipFilePathFromSources() {
        Path zipFileDirectoryFromSources = Paths.get(GDAL_NATIVE_LIBRARIES_SRC).resolve(getDirectory()).resolve(getZipName());
        try {
            URL sourceZipFileDirectoryURL = getClass().getClassLoader().getResource(zipFileDirectoryFromSources.toString().replace(File.separator, "/"));
            if (sourceZipFileDirectoryURL != null) {
                return Paths.get(sourceZipFileDirectoryURL.toURI());
            }
        } catch (Exception e) {
            //
        }
        return null;
    }

    public Path getZipFilePath() {
        Path zipFileDirectory = getNativeLibrariesRootFolderPath();
        return zipFileDirectory.resolve(getDirName()).resolve(getZipName());
    }

    public Path getEnvironmentVariablesFilePathFromSources() {
        String evFileNameFromSources = System.mapLibraryName(osCategory.getEnvironmentVariablesFileName());
        Path evFileDirectoryFromSources = Paths.get(GDAL_NATIVE_LIBRARIES_SRC).resolve(getDirectory()).resolve(evFileNameFromSources);
        try {
            URL sourceEnvFileDirectoryURL = getClass().getClassLoader().getResource(evFileDirectoryFromSources.toString().replace(File.separator, "/"));
            if (sourceEnvFileDirectoryURL != null) {
                return Paths.get(sourceEnvFileDirectoryURL.toURI());
            }
        } catch (Exception e) {
            //
        }
        return null;
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

