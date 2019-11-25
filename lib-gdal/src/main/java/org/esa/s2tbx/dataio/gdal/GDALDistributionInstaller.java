package org.esa.s2tbx.dataio.gdal;

import org.esa.s2tbx.jni.EnvironmentVariables;
import org.esa.snap.utils.NativeLibraryUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
class GDALDistributionInstaller {
    private static final Logger logger = Logger.getLogger(GDALDistributionInstaller.class.getName());

    private GDALDistributionInstaller() {
    }

    /**
     * Install the GDAL library if missing.
     *
     * @throws IOException When IO error occurs
     */
    private static Path installDistribution(GDALVersion gdalVersion) throws IOException {
        // install the GDAL library from the distribution
        OSCategory osCategory = gdalVersion.getOsCategory();
        if (osCategory.getArchitecture() == null) {
            String msg = "No distribution folder found on " + osCategory.getOperatingSystemName() + ".";
            logger.log(Level.INFO, msg);
            throw new IllegalStateException(msg);
        }
        Path gdalNativeLibrariesRootFolderPath = gdalVersion.getNativeLibrariesRootFolderPath();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Install the GDAL library from the distribution on " + osCategory.getOperatingSystemName() + ".");
        }

        GDALInstaller installer = new GDALInstaller();
        Path gdalDistributionRootFolderPath = installer.copyDistribution(gdalNativeLibrariesRootFolderPath, gdalVersion);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.INFO, "The GDAL library has been copied on the local disk.");
        }

        if (org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Process the GDAL library on Windows.");
            }

            processInstalledWindowsDistribution(gdalDistributionRootFolderPath);
        } else if (org.apache.commons.lang.SystemUtils.IS_OS_LINUX) {
            String currentFolderPath = EnvironmentVariables.getCurrentDirectory();
            try {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Process the GDAL library on Linux. The current folder is '" + currentFolderPath + "'.");
                }
                processInstalledLinuxDistribution(gdalDistributionRootFolderPath);
            } finally {
                EnvironmentVariables.changeCurrentDirectory(currentFolderPath);
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "The GDAL library has been successfully installed.");
        }
        return gdalDistributionRootFolderPath;
    }

    /**
     * Install the GDAL JNI drivers if missing.
     *
     * @throws IOException When IO error occurs
     */
    private static Path installJNI(GDALVersion gdalVersion) throws IOException {
        // install the GDAL JNI drivers from the distribution
        OSCategory osCategory = gdalVersion.getOsCategory();
        if (osCategory.getArchitecture() == null) {
            String msg = "No distribution folder found on " + osCategory.getOperatingSystemName() + ".";
            logger.log(Level.INFO, msg);
            throw new IllegalStateException(msg);
        }
        Path gdalNativeLibrariesRootFolderPath = gdalVersion.getNativeLibrariesRootFolderPath();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Install the GDAL JNI drivers from the distribution on " + osCategory.getOperatingSystemName() + ".");
        }

        GDALInstaller installer = new GDALInstaller();
        Path gdalDistributionRootFolderPath = installer.copyDistribution(gdalNativeLibrariesRootFolderPath, gdalVersion);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.INFO, "The GDAL JNI drivers has been copied on the local disk.");
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Process the GDAL JNI drivers on " + gdalVersion.getOsCategory().getOperatingSystemName() + ".");
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Register native lib paths on " + gdalVersion.getOsCategory().getOperatingSystemName() + " for folder '" + gdalDistributionRootFolderPath.toString() + "'.");
        }
        NativeLibraryUtils.registerNativePaths(gdalDistributionRootFolderPath);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "The GDAL library has been successfully installed.");
        }
        return gdalDistributionRootFolderPath;
    }

    private static void processInstalledLinuxDistribution(Path gdalDistributionRootFolderPath) {
        Path libFolderPath = gdalDistributionRootFolderPath.resolve("lib/jni");
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Register native lib paths on Linux for folder '" + libFolderPath.toString() + "'.");
        }
        NativeLibraryUtils.registerNativePaths(libFolderPath);

        Path gdalDataFolderPath = gdalDistributionRootFolderPath.resolve("share/gdal");
        StringBuilder gdalDataValue = new StringBuilder();
        gdalDataValue.append("GDAL_DATA")
                .append("=")
                .append(gdalDataFolderPath.toString());
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Set the GDAL_DATA environment variable on Linux with '" + gdalDataValue.toString() + "'.");
        }
        EnvironmentVariables.setEnvironmentVariable(gdalDataValue.toString());
        Path gdalPluginsFolderPath = libFolderPath.resolve("gdalplugins");
        StringBuilder gdalPluginsValue = new StringBuilder();
        gdalPluginsValue.append("GDAL_PLUGINS")
                .append("=")
                .append(gdalPluginsFolderPath.toString());
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Set the GDAL_DATA environment variable on Linux with '" + gdalPluginsValue.toString() + "'.");
        }
        EnvironmentVariables.setEnvironmentVariable(gdalPluginsValue.toString());
    }

    private static void processInstalledWindowsDistribution(Path gdalDistributionRootFolderPath) throws IOException {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Register native lib paths on Windows for folder '" + gdalDistributionRootFolderPath.toString() + "'.");
        }
        NativeLibraryUtils.registerNativePaths(gdalDistributionRootFolderPath);

        String pathEnvironment = EnvironmentVariables.getEnvironmentVariable("PATH");
        boolean foundBinFolderInPath = findFolderInPathEnvironment(gdalDistributionRootFolderPath, pathEnvironment);
        if (!foundBinFolderInPath) {
            StringBuilder newPathValue = new StringBuilder();
            newPathValue.append("PATH")
                    .append("=")
                    .append(gdalDistributionRootFolderPath.toString())
                    .append(File.pathSeparator)
                    .append(pathEnvironment);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Set the PATH environment variable on Windows with '" + newPathValue.toString() + "'.");
            }
            EnvironmentVariables.setEnvironmentVariable(newPathValue.toString());
        }

        Path gdalDataFolderPath = gdalDistributionRootFolderPath.resolve("gdal-data");
        StringBuilder gdalDataValue = new StringBuilder();
        gdalDataValue.append("GDAL_DATA")
                .append("=")
                .append(gdalDataFolderPath.toString());
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Set the GDAL_DATA environment variable on Windows with '" + gdalDataValue.toString() + "'.");
        }
        EnvironmentVariables.setEnvironmentVariable(gdalDataValue.toString());
        Path gdalPluginsFolderPath = gdalDistributionRootFolderPath.resolve("gdalplugins");
        StringBuilder gdalPluginsValue = new StringBuilder();
        gdalPluginsValue.append("GDAL_PLUGINS")
                .append("=")
                .append(gdalPluginsFolderPath.toString());
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Set the GDAL_DATA environment variable on Windows with '" + gdalPluginsValue.toString() + "'.");
        }
        EnvironmentVariables.setEnvironmentVariable(gdalPluginsValue.toString());
    }

    private static boolean findFolderInPathEnvironment(Path folderPathToCheck, String pathEnvironment) throws IOException {
        String fullFolderPath = folderPathToCheck.toFile().getCanonicalPath();
        boolean foundFolderInPath = false;
        StringTokenizer str = new StringTokenizer(pathEnvironment, File.pathSeparator);
        while (str.hasMoreTokens() && !foundFolderInPath) {
            String currentFolderPathAsString = str.nextToken();
            Path currentFolderPath = Paths.get(currentFolderPathAsString);
            String currentFullFolderPath = currentFolderPath.toFile().getCanonicalPath();
            if (currentFullFolderPath.equals(fullFolderPath)) {
                foundFolderInPath = true;
            }
        }
        return foundFolderInPath;
    }

    static Path setupJNI(GDALVersion gdalVersion) throws IOException {
        if (gdalVersion.isJni()) {
            return installJNI(gdalVersion);
        } else {
            return installDistribution(gdalVersion);
        }
    }
}
