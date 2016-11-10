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

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.runtime.Activator;
import org.esa.snap.utils.FileHelper;
import org.esa.snap.utils.NativeLibraryUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.lang.SystemUtils.*;

/**
 * Activator class for deploying GDAL binaries to the aux data dir
 *
 * @author Cosmin Cara
 */
public class GdalActivator implements Activator {
    private static final String SRC_PATH = "auxdata/gdal";
    private static final String BIN_PATH = "bin";
    private static final String JAR_PATH = "bin/gdal/java";
    private static final String PLUGINS_PATH = "bin/gdal/plugins";
    private static final String EXT_PATH = "bin/gdal/plugins-external";
    private static final String OPT_PATH = "bin/gdal/plugins-optional";

    @Override
    public void start() {
        Path sourceDirPath = ResourceInstaller.findModuleCodeBasePath(getClass()).resolve(SRC_PATH);
        Path auxdataDirectory = getGDALAuxDataPath();
        if (auxdataDirectory == null) {
            SystemUtils.LOG.severe("GDAL configuration error: failed to retrieve auxdata path");
            return;
        }
        final ResourceInstaller resourceInstaller = new ResourceInstaller(sourceDirPath, auxdataDirectory);

        try {
            resourceInstaller.install(".*", ProgressMonitor.NULL);
            fixUpPermissions(auxdataDirectory);
        } catch (IOException e) {
            SystemUtils.LOG.severe("GDAL configuration error: failed to create " + auxdataDirectory);
            return;
        }

        Path zipPath = auxdataDirectory.resolve(OSCategory.getOSCategory().getArchivePath());
        Path destFolder = zipPath.getParent();
        try {
            FileHelper.unzip(zipPath, destFolder);
        } catch (IOException e) {
            SystemUtils.LOG.severe("GDAL configuration error: failed to unzip to " + destFolder);
            return;
        }

        NativeLibraryUtils.registerNativePath(destFolder.resolve(BIN_PATH).toString());
        NativeLibraryUtils.registerNativePath(destFolder.resolve(PLUGINS_PATH).toString());
        NativeLibraryUtils.registerNativePath(destFolder.resolve(EXT_PATH).toString());
        NativeLibraryUtils.registerNativePath(destFolder.resolve(OPT_PATH).toString());
        NativeLibraryUtils.registerPath(destFolder.resolve(JAR_PATH).toString());
        NativeLibraryUtils.registerPath(destFolder.resolve(PLUGINS_PATH).toString());
        NativeLibraryUtils.registerPath(destFolder.resolve(EXT_PATH).toString());
        NativeLibraryUtils.registerPath(destFolder.resolve(OPT_PATH).toString());
        try {
            Files.deleteIfExists(zipPath);
        } catch (IOException e) {
            SystemUtils.LOG.warning("GDAL configuration error: failed to delete zip after decompression");
        }
    }

    @Override
    public void stop() {
        // Purposely no-op
    }

    public static Path getGDALAuxDataPath() {
        return SystemUtils.getAuxDataPath().resolve("gdal");
    }

    private static void fixUpPermissions(Path destPath) throws IOException {
        Stream<Path> files = Files.list(destPath);
        files.forEach(path -> {
            if (Files.isDirectory(path)) {
                try {
                    fixUpPermissions(path);
                } catch (IOException e) {
                    SystemUtils.LOG.severe("GDAL configuration error: failed to fix permissions on " + path);
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
                SystemUtils.LOG.severe("Can't set execution permissions for executable " + executablePathName.toString() +
                        ". If required, please ask an authorised user to make the file executable.");
            }
        }
    }

    private enum OSCategory {
        WIN_32("gdal-2.1.0-win32", "release-1800-gdal-2-1-0-mapserver-7-0-1.zip"),
        WIN_64("gdal-2.1.0-win64", "release-1800-x64-gdal-2-1-0-mapserver-7-0-1.zip"),
        LINUX_64(null, null),
        MAC_OS_X(null, null),
        UNSUPPORTED(null, null);


        String directory;
        String zipFile;

        OSCategory(String directory, String zipFile) {
            this.directory = directory;
            this.zipFile = zipFile;
        }

        Path getArchivePath() {
            return Paths.get(directory, zipFile);
        }

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
