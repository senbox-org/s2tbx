package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.util.SystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GDAL Version enum for defining compatible GDAL versions with SNAP.
 *
 * @author Adrian Drăghici
 */
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

    private static final Logger logger = Logger.getLogger(GDALVersion.class.getName());

    private static final GDALVersion internalVersion = retrieveInternalVersion();
    private static final GDALVersion installedVersion = retrieveInstalledVersion();

    String id;
    String name;
    String location;
    boolean jni;
    OSCategory osCategory;

    /**
     * Creates new instance for this enum.
     *
     * @param id   the id of version
     * @param name the name of version
     * @param jni  the type of version: {@code true} if version is JNI driver
     */
    GDALVersion(String id, String name, boolean jni) {
        this.id = id;
        this.name = name;
        this.jni = jni;
    }

    /**
     * Gets the installed GDAL version when found or internal GDAL version otherwise.
     *
     * @return the installed GDAL version when found or internal GDAL version otherwise
     */
    public static GDALVersion getGDALVersion() {
        if (GDALLoaderConfig.getInstance().useInstalledGDALLibrary() && installedVersion != null) {
            logger.log(Level.INFO, () -> "Installed GDAL " + installedVersion.getId() + " set to be used by SNAP.");
            return installedVersion;
        }
        logger.log(Level.INFO, () -> "Internal GDAL " + internalVersion.getId() + " set to be used by SNAP.");
        return internalVersion;
    }

    /**
     * Gets installed GDAL version.
     *
     * @return the installed GDAL version or {@code null} if not found
     */
    public static GDALVersion getInstalledVersion() {
        return installedVersion;
    }

    private static String fetchProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (InputStream commandInputStream = process.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(commandInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            boolean isStopped = false;
            long startTime = System.currentTimeMillis();
            int runningTime = 30;// allow only 30 seconds of running time for the process
            while (!isStopped && runningTime > 0) {
                if (process.isAlive()) {
                    Thread.yield(); // yield the control to other threads
                } else {
                    isStopped = true;
                }
                while (bufferedReader.ready()) {
                    String line = bufferedReader.readLine();
                    if (line != null && !line.isEmpty()) {
                        output.append(line).append("\n");
                    } else {
                        break;
                    }
                }
                long endTime = System.currentTimeMillis();
                runningTime -= (endTime - startTime) / 1000;
            }
            return output.toString();
        }
    }

    /**
     * Retrieves the installed GDAl version on host OS by invoking 'gdalinfo --version' command and parsing the output.
     *
     * @return the installed GDAl version on host OS or {@code null} if not found
     */
    private static GDALVersion retrieveInstalledVersion() {
        GDALVersion gdalVersion = null;
        try {
            Process checkGDALVersionProcess = Runtime.getRuntime().exec(GDAL_INFO_CMD);
            String result = fetchProcessOutput(checkGDALVersionProcess);
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
                        logger.log(Level.INFO, () -> "GDAL not found on system. Internal GDAL " + internalVersion.id + " from distribution will be used. (f0)");
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
            logger.log(Level.INFO, () -> "GDAL not found on system. Internal GDAL " + internalVersion.id + " from distribution will be used. (f1)");
        }
        return gdalVersion;
    }

    /**
     * Gets internal GDAL version.
     *
     * @return the internal GDAL version
     */
    public static GDALVersion getInternalVersion() {
        return internalVersion;
    }

    /**
     * Retrieves internal GDAL version from SNAP distribution packages.
     *
     * @return the internal GDAL version
     */
    private static GDALVersion retrieveInternalVersion() {
        GDALVersion gdalVersion = GDAL_300_FULL;
        gdalVersion.setOsCategory(OSCategory.getOSCategory());
        Path internalPath = gdalVersion.getNativeLibrariesRootFolderPath().resolve(gdalVersion.getDirName());
        gdalVersion.setLocation(internalPath.toString());
        return gdalVersion;
    }

    /**
     * Gets the id of this version.
     *
     * @return the id of this version
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the id for this version.
     *
     * @param id the new id
     */
    void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the location of this version.
     *
     * @return the location of this version
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Sets the location of this version.
     *
     * @param location the new location
     */
    private void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the OS category of this version.
     *
     * @return the OS category of this version
     */
    public OSCategory getOsCategory() {
        return this.osCategory;
    }

    /**
     * Sets the OS category of this version
     *
     * @param osCategory the new OS category
     */
    private void setOsCategory(OSCategory osCategory) {
        this.osCategory = osCategory;
    }

    /**
     * Gets whether this version is JNI driver.
     *
     * @return {@code true} if this version is JNI driver
     */
    public boolean isJni() {
        return this.jni;
    }

    /**
     * Gets the name of directory for this version.
     *
     * @return the name of directory for this version
     */
    private String getDirName() {
        if (this.jni) {
            return DIR_NAME.replace(VERSION_NAME, this.name).replace(JNI_NAME, "-jni");
        } else {
            return DIR_NAME.replace(VERSION_NAME, this.name).replace(JNI_NAME, "");
        }
    }

    /**
     * Gets the name of ZIP archive for this version.
     *
     * @return the name of ZIP archive for this version
     */
    private String getZipName() {
        if (this.jni) {
            return ZIP_NAME.replace(VERSION_NAME, this.name).replace(JNI_NAME, "-jni");
        } else {
            return ZIP_NAME.replace(VERSION_NAME, this.name).replace(JNI_NAME, "");
        }
    }

    /**
     * Gets the relative path of the directory based on OS category for this version.
     *
     * @return the relative path of the directory based on OS category for this version
     */
    private String getDirectory() {
        return this.osCategory.getOperatingSystemName() + "/" + this.osCategory.getArchitecture();
    }

    /**
     * Gets the ZIP archive URL from SNAP distribution packages for this version.
     *
     * @return the ZIP archive URL from SNAP distribution packages for this version
     */
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

    /**
     * Gets the ZIP archive root directory path for install this version.
     *
     * @return the ZIP archive root directory path for install this version
     */
    public Path getZipFilePath() {
        Path zipFileDirectory = getNativeLibrariesRootFolderPath();
        return zipFileDirectory.resolve(getDirName()).resolve(getZipName());
    }

    /**
     * Gets the environment variables native library URL from SNAP distribution packages for this version.
     *
     * @return the environment variables native library URL from SNAP distribution packages for this version
     */
    public URL getEnvironmentVariablesFilePathFromSources() {
        String evFileNameFromSources = System.mapLibraryName(this.osCategory.getEnvironmentVariablesFileName());
        String evFileDirectoryFromSources = GDAL_NATIVE_LIBRARIES_SRC + "/" + getDirectory() + "/" + evFileNameFromSources;
        try {
            return getClass().getClassLoader().getResource(evFileDirectoryFromSources.replace(File.separator, "/"));
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Gets the environment variables native library root directory path for install this version.
     *
     * @return the environment variables native library root directory path for install this version
     */
    public Path getEnvironmentVariablesFilePath() {
        Path zipFileDirectory = getNativeLibrariesRootFolderPath();
        String evFileNameFromSources = System.mapLibraryName(this.osCategory.getEnvironmentVariablesFileName());
        return zipFileDirectory.resolve(evFileNameFromSources);
    }

    /**
     * Gets the root directory path for install this version.
     *
     * @return the root directory path for install this version
     */
    public Path getNativeLibrariesRootFolderPath() {
        Path snapNativeLibrariesRootPath = SystemUtils.getAuxDataPath();
        return snapNativeLibrariesRootPath.resolve(GDAL_NATIVE_LIBRARIES_ROOT);
    }

    /**
     * Gets the path for JNI drivers of this version.
     *
     * @return the path for JNI drivers of this version
     */
    public Path getJNILibraryFilePath() {
        return getNativeLibrariesRootFolderPath().resolve(getDirName()).resolve(GDAL_JNI_LIBRARY_FILE);
    }
}

