package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum GDALVersion {

    GDAL_300_FULL("3.0.0", "3-0-0", false),
    GDAL_30X_JNI("3.0.X", "3-0-X", true),
    GDAL_24X_JNI("2.4.x", "2-4-X", true),
    GDAL_23X_JNI("2.3.x", "2-3-X", true),
    GDAL_22X_JNI("2.2.x", "2-2-X", true),
    GDAL_21X_JNI("2.1.x", "2-1-X", true),
    GDAL_20X_JNI("2.0.x", "2-0-X", true);

    private static final String VERSION_NAME = "{version}";
    private static final String JNI_NAME = "{jni}";
    private static final String DIR_NAME = "gdal-" + VERSION_NAME + JNI_NAME;
    private static final String ZIP_NAME = DIR_NAME + ".zip";
    private static final String GDAL_NATIVE_LIBRARIES_ROOT = "gdal";
    private static final String GDAL_NATIVE_LIBRARIES_SRC = "auxdata/gdal";
    private static final String GDAL_JNI_LIBRARY_FILE = "java/gdal.jar";

    private static final String GDALINFIO_EXECUTABLE_NAME = "gdalinfo";
    private static final String GDAL_INFO_CMD = GDALINFIO_EXECUTABLE_NAME + " --version";
    private static final String GDAL_V30X = "3.0.X";
    private static final String GDAL_V24X = "2.4.X";
    private static final String GDAL_V23X = "2.3.X";
    private static final String GDAL_V22X = "2.2.X";
    private static final String GDAL_V21X = "2.1.X";
    private static final String GDAL_V20X = "2.0.X";

    private static final Logger logger = Logger.getLogger(GDALInstaller.class.getName());

    private static final GDALVersion internalVersion = retrieveInternalVersion();
    private static final GDALVersion installedVersion = retrieveInstalledVersion();

    String id;
    String name;
    String location;
    boolean jni;
    OSCategory osCategory;

    GDALVersion(String id, String name, boolean jni) {
        this.id = id;
        this.name = name;
        this.jni = jni;
    }

    public static GDALVersion getGDALVersion() {
        if (GDALLoaderConfig.getInstance().useInstalledGDALLibrary() && installedVersion != null) {
            logger.log(Level.INFO, () -> "Installed GDAL " + installedVersion.getId() + " set to be used by SNAP.");
            return installedVersion;
        }
        logger.log(Level.INFO, () -> "Internal GDAL " + internalVersion.getId() + " set to be used by SNAP.");
        return internalVersion;
    }

    public static GDALVersion getInstalledVersion() {
        return installedVersion;
    }

    private static GDALVersion retrieveInstalledVersion() {
        GDALVersion gdalVersion = null;
        try (java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(GDAL_INFO_CMD).getInputStream()).useDelimiter("\\A")) {
            String result = s.hasNext() ? s.next() : "";
            String versionId = result.replaceAll("[\\s\\S]*?(\\d*\\.\\d*\\.\\d*)[\\s\\S]*$", "$1");
            String version = versionId.replaceAll("(\\d*\\.\\d*)[\\s\\S]*$", "$1.X");
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
                    if (version.isEmpty()) {
                        logger.log(Level.INFO, () -> "GDAL not found on system. Internal GDAL " + internalVersion.id + " from distribution will be used.");
                    } else {
                        logger.log(Level.INFO, () -> "Incompatible GDAL " + versionId + " found on system. Internal GDAL " + internalVersion.id + " from distribution will be used.");
                    }
            }
            if (gdalVersion != null) {
                gdalVersion.setId(versionId);
                OSCategory osCategory = OSCategory.getOSCategory();
                gdalVersion.setOsCategory(osCategory);
                gdalVersion.setLocation(osCategory.getExecutableLocation(GDALINFIO_EXECUTABLE_NAME));
                logger.log(Level.INFO, () -> "GDAL " + versionId + " found on system. JNI driver will be used.");
            }
        } catch (IOException ignored) {
            logger.log(Level.INFO, () -> "GDAL not found on system. Internal GDAL " + internalVersion.id + " from distribution will be used.");
        }
        return gdalVersion;
    }

    public static GDALVersion getInternalVersion() {
        return internalVersion;
    }

    private static GDALVersion retrieveInternalVersion() {
        GDALVersion gdalVersion = GDAL_300_FULL;
        gdalVersion.setOsCategory(OSCategory.getOSCategory());
        Path internalPath = gdalVersion.getNativeLibrariesRootFolderPath().resolve(gdalVersion.getDirName());
        gdalVersion.setLocation(internalPath.toString());
        return gdalVersion;
    }

    public String getId() {
        return this.id;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    private void setLocation(String location) {
        this.location = location;
    }

    public OSCategory getOsCategory() {
        return osCategory;
    }

    private void setOsCategory(OSCategory osCategory) {
        this.osCategory = osCategory;
    }

    public boolean isJni() {
        return jni;
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
        return osCategory.getOperatingSystemName() + "/" + osCategory.getArchitecture();
    }

    public URL getZipFileURLFromSources() {
        String zipFileDirectoryFromSources = GDAL_NATIVE_LIBRARIES_SRC + "/" + getDirectory() + "/" + getZipName();
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "version zip archive URL from sources: '" + zipFileDirectoryFromSources + "'.");
            }
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

