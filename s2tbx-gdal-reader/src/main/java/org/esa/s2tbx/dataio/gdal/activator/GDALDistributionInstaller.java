package org.esa.s2tbx.dataio.gdal.activator;

import org.esa.s2tbx.dataio.gdal.GDALInstaller;
import org.esa.s2tbx.dataio.gdal.GDALUtils;
import org.esa.s2tbx.dataio.gdal.OSCategory;
import org.esa.s2tbx.jni.EnvironmentVariables;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.SystemUtils;
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
public class GDALDistributionInstaller {
    private static final Logger logger = Logger.getLogger(GDALDistributionInstaller.class.getName());

    private GDALDistributionInstaller() {
    }

    /**
     * Install the GDAL library if missing.
     *
     * @throws IOException
     */
    public static void install() throws IOException {
        // install the GDAL library from the distribution
        OSCategory osCategory = OSCategory.getOSCategory();
        if (osCategory.getDirectory() == null) {
            logger.log(Level.INFO, "No distribution folder found on " + osCategory.getOperatingSystemName() + ".");
            return;
        }
        if (osCategory.getZipFileName() == null) {
            logger.log(Level.INFO, "No library zip file name found on " + osCategory.getOperatingSystemName() + ".");
            return;
        }

        Path gdalApplicationFolderPath = SystemUtils.getAuxDataPath().resolve("gdal");
        if (gdalApplicationFolderPath == null) {
            logger.log(Level.INFO, "No folder path to install the GDAL integration on the local disk.");
            return;
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Install the GDAL library from the distribution on " + osCategory.getOperatingSystemName() + ".");
        }

        GDALInstaller installer = new GDALInstaller();
        Path gdalDistributionRootFolderPath = installer.copyDistribution(gdalApplicationFolderPath, osCategory);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.INFO, "The GDAL library has been copied on the local disk.");
        }

        if (org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Process the GDAL library on Windows.");
            }

            processInstalledWindowsDistribution(gdalDistributionRootFolderPath);
            GDALInstallInfo.INSTANCE.setLocations(gdalDistributionRootFolderPath);

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Init the GDAL drivers on Windows.");
            }

            GDALUtils.initDrivers();
        } else if (org.apache.commons.lang.SystemUtils.IS_OS_LINUX) {
            String currentFolderPath = EnvironmentVariables.getCurrentDirectory();
            try {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Process the GDAL library on Linux. The current folder is '"+currentFolderPath+"'.");
                }

                if (processInstalledLinuxDistribution(gdalDistributionRootFolderPath)) {
                    GDALInstallInfo.INSTANCE.setLocations(gdalDistributionRootFolderPath);

                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.INFO, "Init the GDAL drivers on Linux.");
                    }

                    GDALUtils.initDrivers();
                }
            } finally {
                EnvironmentVariables.changeCurrentDirectory(currentFolderPath);
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "The GDAL library has been successfully installed.");
        }
    }

    private static boolean processInstalledLinuxDistribution(Path gdalDistributionRootFolderPath) throws IOException {
        // check if the LD_LIBRARY_PATH contains the current folder '.'
        String libraryPathEnvironmentKey = "LD_LIBRARY_PATH";
        String libraryPathEnvironmentValue = EnvironmentVariables.getEnvironmentVariable(libraryPathEnvironmentKey);
        if (StringUtils.isNullOrEmpty(libraryPathEnvironmentValue)) {
            StringBuilder exceptionMessage = new StringBuilder();
            exceptionMessage.append("The environment variable ")
                    .append(libraryPathEnvironmentKey)
                    .append(" is not set. It must contain the current folder '.'.");
            //throw new IllegalArgumentException(exceptionMessage.toString());
            logger.log(Level.SEVERE, exceptionMessage.toString());
            return false;
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "The value of the environment variable " + libraryPathEnvironmentKey+" is '" + libraryPathEnvironmentValue+"'.");
            }

            StringTokenizer str = new StringTokenizer(libraryPathEnvironmentValue, File.pathSeparator);
            boolean hasCurrentFolder = false;
            while (!hasCurrentFolder && str.hasMoreTokens()) {
                String folderPath = str.nextToken();
                if (".".equals(folderPath)) {
                    hasCurrentFolder = true;
                }
            }
            if (!hasCurrentFolder) {
                StringBuilder exceptionMessage = new StringBuilder();
                exceptionMessage.append("The environment variable ")
                        .append(libraryPathEnvironmentKey)
                        .append(" does not contain the current folder '.'. Its value is '")
                        .append(libraryPathEnvironmentValue)
                        .append("'.");
                //throw new IllegalArgumentException(exceptionMessage.toString());
                logger.log(Level.SEVERE, exceptionMessage.toString());
                return false;
            }
        }

        Path libFolderPath = gdalDistributionRootFolderPath.resolve("lib");
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Register native lib paths on Linux for folder '"+ libFolderPath.toString()+"'.");
        }
        NativeLibraryUtils.registerNativePaths(libFolderPath);

        Path nativeFolderPath = libFolderPath.resolve("jni");
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Register jni paths on Linux for folder '"+ nativeFolderPath.toString()+"'.");
        }
        NativeLibraryUtils.registerNativePaths(nativeFolderPath);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Change the current directory on Linux with folder '"+ libFolderPath.toString() +"'.");
        }
        EnvironmentVariables.changeCurrentDirectory(libFolderPath.toString());

        Path gdalDataFolderPath = gdalDistributionRootFolderPath.resolve("share/gdal");
        StringBuilder gdalDataValue = new StringBuilder();
        gdalDataValue.append("GDAL_DATA")
                .append("=")
                .append(gdalDataFolderPath.toString());
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Set the GDAL_DATA environment variable on Linux with '"+ gdalDataValue.toString() +"'.");
        }
        EnvironmentVariables.setEnvironmentVariable(gdalDataValue.toString());

        return true;
    }

    private static void processInstalledWindowsDistribution(Path gdalDistributionRootFolderPath) throws IOException {
        Path gdalBinFolderPath = gdalDistributionRootFolderPath.resolve("bin");

        Path nativeFolderPath = gdalBinFolderPath.resolve("gdal/java");
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Register native lib paths on Windows for folder '"+ nativeFolderPath.toString()+"'.");
        }
        NativeLibraryUtils.registerNativePaths(nativeFolderPath);

        String pathEnvironment = EnvironmentVariables.getEnvironmentVariable("PATH");
        boolean foundBinFolderInPath = findFolderInPathEnvironment(gdalBinFolderPath, pathEnvironment);
        if (!foundBinFolderInPath) {
            Path gdalAppsFolderPath = gdalBinFolderPath.resolve("gdal/apps");
            StringBuilder newPathValue = new StringBuilder();
            newPathValue.append("PATH")
                    .append("=")
                    .append(gdalBinFolderPath.toString())
                    .append(File.pathSeparator)
                    .append(gdalAppsFolderPath.toString())
                    .append(File.pathSeparator)
                    .append(pathEnvironment);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Set the PATH environment variable on Windows with '"+ newPathValue.toString() +"'.");
            }
            EnvironmentVariables.setEnvironmentVariable(newPathValue.toString());
        }

        Path gdalDataFolderPath = gdalBinFolderPath.resolve("gdal-data");
        StringBuilder gdalDataValue = new StringBuilder();
        gdalDataValue.append("GDAL_DATA")
                .append("=")
                .append(gdalDataFolderPath.toString());
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Set the GDAL_DATA environment variable on Windows with '"+ gdalDataValue.toString() +"'.");
        }
        EnvironmentVariables.setEnvironmentVariable(gdalDataValue.toString());
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
}
