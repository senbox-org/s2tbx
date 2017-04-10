/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.gdal;

import org.esa.s2tbx.jni.EnvironmentVariables;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.utils.FileHelper;
import org.esa.snap.utils.NativeLibraryUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.apache.commons.lang.SystemUtils.*;

/**
 * Activator class for deploying GDAL binaries to the aux data dir
 *
 * @author Cosmin Cara
 */
public class GDALInstaller {
    private static final Logger logger = Logger.getLogger(GDALInstaller.class.getName());

    private static final String SRC_PATH = "auxdata/gdal";
    private static final String BIN_PATH = "bin";
    private static final String APPS_PATH = "gdal/apps";
    private static final String PLUGINS_PATH = "gdal/plugins";
    private static final String DATA_PATH = "gdal-data";

    public GDALInstaller() {
    }

    /**
     * Install the GDAL library if missing.
     *
     * @throws IOException
     */
    public void install() throws IOException {
        // install the GDAL library from the distribution
        OSCategory osCategory = OSCategory.getOSCategory();
        if (osCategory.getDirectory() == null) {
            logger.log(Level.INFO, "No distribution folder found on " + osCategory.getOperatingSystemName() + " operation system.");
            return;
        }
        if (osCategory.getZipFileName() == null) {
            logger.log(Level.INFO, "No library zip file name found on " + osCategory.getOperatingSystemName() + " operation system.");
            return;
        }

        logger.log(Level.INFO, "Install the GDAL library from the distribution on " + osCategory.getOperatingSystemName() + " operation system.");

        Path gdalApplicationFolderPath = getGDALFolderPath();
        if (gdalApplicationFolderPath == null) {
            logger.log(Level.INFO, "No folder path to install the GDAL integration on the local disk.");
            return;
        }
        if (!Files.exists(gdalApplicationFolderPath)) {
            Files.createDirectories(gdalApplicationFolderPath);
        }
        installDistribution(gdalApplicationFolderPath, osCategory);
    }

    private void installDistribution(Path gdalApplicationFolderPath, OSCategory osCategory) throws IOException {
        // the library file does not exist on  the local disk among the folders from path environment
        String zipArchivePath = osCategory.getDirectory() + "/" + osCategory.getZipFileName();
        Path zipFilePathOnLocalDisk = gdalApplicationFolderPath.resolve(zipArchivePath);
        Path gdalDistributionRootFolderPath = zipFilePathOnLocalDisk.getParent();

        fixUpPermissions(gdalApplicationFolderPath);

        if (!Files.exists(gdalDistributionRootFolderPath)) {
            Files.createDirectories(gdalDistributionRootFolderPath);
            try {
                String zipFilePathFromSources = SRC_PATH + "/" + zipArchivePath;
                URL zipFileURLFromSources = getClass().getClassLoader().getResource(zipFilePathFromSources);
                FileHelper.copyFile(zipFileURLFromSources, zipFilePathOnLocalDisk);
                FileHelper.unzip(zipFilePathOnLocalDisk, gdalDistributionRootFolderPath, true);
            } finally {
                try {
                    Files.deleteIfExists(zipFilePathOnLocalDisk);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "GDAL configuration error: failed to delete zip after decompression.", e);
                }
            }
        }

        String libraryFileName = System.mapLibraryName("environment-variables");
        Path libraryFilePath = gdalApplicationFolderPath.resolve(libraryFileName);
        if (!Files.exists(libraryFilePath)) {
            String libraryFilePathFromSources = SRC_PATH + "/" + libraryFileName;
            URL libraryFileURLFromSources = getClass().getClassLoader().getResource(libraryFilePathFromSources);
            FileHelper.copyFile(libraryFileURLFromSources, libraryFilePath);
        }
        NativeLibraryUtils.registerNativePaths(libraryFilePath.getParent());

        if (org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS) {
            processInstalledWindowsDistribution(gdalDistributionRootFolderPath);
        } else if (org.apache.commons.lang.SystemUtils.IS_OS_LINUX) {
            processInstalledLinuxDistribution(gdalDistributionRootFolderPath);
        }
    }

    private void processInstalledLinuxDistribution(Path gdalDistributionRootFolderPath) throws IOException {
        Path nativeFolderPath = gdalDistributionRootFolderPath.resolve("lib/jni");
        NativeLibraryUtils.registerNativePaths(nativeFolderPath);

        Path libraryFolderPath = gdalDistributionRootFolderPath.resolve("lib");
        StringBuilder newPathValue = new StringBuilder();
        newPathValue.append("LD_LIBRARY_PATH")
                .append("=")
                .append(libraryFolderPath.toString());
        EnvironmentVariables.setEnvironmentVariable(newPathValue.toString());

        Path gdalDataFolderPath = gdalDistributionRootFolderPath.resolve("share/gdal");
        StringBuilder gdalDataValue = new StringBuilder();
        gdalDataValue.append("GDAL_DATA")
                .append("=")
                .append(gdalDataFolderPath.toString());
        EnvironmentVariables.setEnvironmentVariable(gdalDataValue.toString());

        GdalInstallInfo gdalInstallInfo = GdalInstallInfo.INSTANCE;
        gdalInstallInfo.setLocations(gdalDistributionRootFolderPath, null, null, null);
    }

    private void processInstalledWindowsDistribution(Path gdalDistributionRootFolderPath) throws IOException {
        Path gdalBinFolderPath = gdalDistributionRootFolderPath.resolve(BIN_PATH);

        Path nativeFolderPath = gdalBinFolderPath.resolve("gdal/java");
        NativeLibraryUtils.registerNativePaths(nativeFolderPath);

        Path gdalAppsFolderPath = gdalBinFolderPath.resolve(APPS_PATH);

        String pathEnvironment = EnvironmentVariables.getEnvironmentVariable("PATH");
        boolean foundBinFolderInPath = findFolderInPathEnvironment(gdalBinFolderPath, pathEnvironment);
        if (!foundBinFolderInPath) {
            StringBuilder newPathValue = new StringBuilder();
            newPathValue.append("PATH")
                    .append("=")
                    .append(gdalBinFolderPath.toString())
                    .append(File.pathSeparator)
                    .append(gdalAppsFolderPath.toString())
                    .append(File.pathSeparator)
                    .append(pathEnvironment);
            EnvironmentVariables.setEnvironmentVariable(newPathValue.toString());
        }

        Path gdalDataFolderPath = gdalBinFolderPath.resolve(DATA_PATH);
        StringBuilder gdalDataValue = new StringBuilder();
        gdalDataValue.append("GDAL_DATA")
                .append("=")
                .append(gdalDataFolderPath.toString());
        EnvironmentVariables.setEnvironmentVariable(gdalDataValue.toString());

        Path gdalDriverFolderPath = gdalBinFolderPath.resolve(PLUGINS_PATH);
        StringBuilder gdalDriverValue = new StringBuilder();
        gdalDriverValue.append("GDAL_DRIVER_PATH")
                .append("=")
                .append(gdalDriverFolderPath.toString());
        EnvironmentVariables.setEnvironmentVariable(gdalDriverValue.toString());

        GdalInstallInfo gdalInstallInfo = GdalInstallInfo.INSTANCE;
        gdalInstallInfo.setLocations(gdalBinFolderPath, null, null, null);
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

    private static Path getGDALFolderPath() {
        return SystemUtils.getAuxDataPath().resolve("gdal");
    }

    private static void fixUpPermissions(Path destPath) throws IOException {
        Stream<Path> files = Files.list(destPath);
        files.forEach(path -> {
            if (Files.isDirectory(path)) {
                try {
                    fixUpPermissions(path);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "GDAL configuration error: failed to fix permissions on " + path, e);
                }
            }
            else {
                setExecutablePermissions(path);
            }
        });
    }

    private static void setExecutablePermissions(Path executablePathName) {
        if (IS_OS_UNIX) {
            Set<PosixFilePermission> permissions = new HashSet<>(Arrays.asList(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.GROUP_READ,
                    PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.OTHERS_READ,
                    PosixFilePermission.OTHERS_EXECUTE));
            try {
                Files.setPosixFilePermissions(executablePathName, permissions);
            } catch (IOException e) {
                // can't set the permissions for this file, eg. the file was installed as root
                // send a warning message, user will have to do that by hand.
                logger.log(Level.SEVERE, "Can't set execution permissions for executable " + executablePathName.toString() +
                        ". If required, please ask an authorised user to make the file executable.", e);
            }
        }
    }

    private enum OSCategory {
        WIN_32("gdal-2.1.0-win32", "release-1500-gdal-2-1-0-mapserver-7-0-1.zip", "Windows"),
        WIN_64("gdal-2.1.0-win64", "release-1500-x64-gdal-2-1-0-mapserver-7-0-1.zip", "Windows"),
        LINUX_64("gdal-2.1.3-linux", "gdal-2.1.3-linux-bin.zip", "Linux"),
        MAC_OS_X(null, null, "Macintosh"),
        UNSUPPORTED(null, null, "");

        String directory;
        String zipFileName;
        String operatingSystemName;

        OSCategory(String directory, String zipFileName, String operatingSystemName) {
            this.directory = directory;
            this.zipFileName = zipFileName;
            this.operatingSystemName = operatingSystemName;
        }

        public String getOperatingSystemName() {
            return operatingSystemName;
        }

        String getDirectory() { return this.directory; }

        String getZipFileName() { return this.zipFileName; }

        static OSCategory getOSCategory() {
            OSCategory category;
            if (IS_OS_LINUX) {
                category = OSCategory.LINUX_64;
            } else if (IS_OS_MAC_OSX) {
                category = OSCategory.MAC_OS_X;
            } else if (IS_OS_WINDOWS) {
                String sysArch = System.getProperty("os.arch").toLowerCase();
                if (sysArch.contains("amd64") || sysArch.contains("x86_x64")) {
                    category = OSCategory.WIN_64;
                } else {
                    category = OSCategory.WIN_32;
                }
            } else {
                // we should never be here since we do not release installers for other systems.
                category = OSCategory.UNSUPPORTED;
            }
            return category;
        }
    }
}
