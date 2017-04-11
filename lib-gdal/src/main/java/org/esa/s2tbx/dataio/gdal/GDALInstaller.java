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

    public GDALInstaller() {
    }

    public final Path copyDistribution(Path gdalApplicationFolderPath, OSCategory osCategory) throws IOException {
        if (!Files.exists(gdalApplicationFolderPath)) {
            Files.createDirectories(gdalApplicationFolderPath);
        }

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

        return gdalDistributionRootFolderPath;
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

    public enum OSCategory {
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

        public String getDirectory() { return this.directory; }

        public String getZipFileName() { return this.zipFileName; }

        public static OSCategory getOSCategory() {
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
