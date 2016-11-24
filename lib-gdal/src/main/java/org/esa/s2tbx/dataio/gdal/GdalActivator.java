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
import org.esa.snap.utils.PostExecAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
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
    private static final String JAR_PATH = BIN_PATH + "/gdal/java";
    private static final String APPS_PATH = BIN_PATH + "/gdal/apps";
    private static final String PLUGINS_PATH = BIN_PATH + "/gdal/plugins";
    private static final String DATA_PATH = BIN_PATH + "/gdal-data";
    private static final String EXT_PATH = BIN_PATH + "/gdal/plugins-external";
    private static final String OPT_PATH = BIN_PATH + "/gdal/plugins-optional";

    @Override
    public void start() {
        Map<String, String> environment = System.getenv();
        try {
            System.loadLibrary("gdaljni");
            String searchStr = "gdal" + File.separator + "bin" + File.pathSeparator;
            String envPath = environment.get("Path");
            int start = envPath.indexOf(searchStr);
            if (start > 0) {
                start = Math.max(envPath.lastIndexOf(File.pathSeparator, start - 1), 0);
                int end = envPath.indexOf(File.pathSeparator, start);
                String binPath = envPath.substring(start, end);
                GdalInstallInfo.setBinLocation(Paths.get(binPath));
                Path root = GdalInstallInfo.getBinLocation().getParent();
                GdalInstallInfo.setAppsLocation(root.resolve(APPS_PATH));
                GdalInstallInfo.setDriversLocation(root.resolve(PLUGINS_PATH));
                GdalInstallInfo.setDataLocation(root.resolve(DATA_PATH));
            }
        } catch (UnsatisfiedLinkError ignored) {
        }
        if (GdalInstallInfo.getBinLocation() == null) {
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
            Path binPath = destFolder.resolve(BIN_PATH);

            try {
                FileHelper.unzip(zipPath, destFolder, true);
                String[] jniFiles = OSCategory.getOSCategory().getJniFiles();
                if (jniFiles == null || jniFiles.length == 0) {
                    throw new IOException("No JNI wrappers found");
                }
                for (String file : jniFiles) {
                    if (!Files.exists(binPath.resolve(file))) {
                        Files.move(destFolder.resolve(JAR_PATH).resolve(file), binPath.resolve(file));
                    }
                }
            } catch (IOException e) {
                SystemUtils.LOG.severe(String.format("GDAL configuration error: failed to unzip to %s [Reason: %s]", destFolder, e.getMessage()));
            }

            NativeLibraryUtils.registerNativePaths(binPath);

            if (!environment.get("Path").contains(binPath.toString())) {
                String[] args = OSCategory.getOSCategory().getPathCmd();
                if (args != null && args.length > 2) {
                    args[2] = args[2].replace("$1", binPath.toString() + File.pathSeparator + destFolder.resolve(APPS_PATH).toString());
                    PostExecAction.register("lib-gdal [$PATH]", args);
                }
            }
            if (!environment.containsKey("GDAL_DRIVER_PATH")) {
                String[] args = OSCategory.getOSCategory().getDriverPathCmd();
                if (args != null && args.length > 2) {
                    args[2] = args[2].replace("$1", destFolder.resolve(PLUGINS_PATH).toString());
                    PostExecAction.register("lib-gdal [$GDAL_DRIVER_PATH]", args);
                }
            }
            if (!environment.containsKey("GDAL_DATA")) {
                String[] args = OSCategory.getOSCategory().getDataPathCmd();
                if (args != null && args.length > 2) {
                    args[2] = args[2].replace("$1", destFolder.resolve(DATA_PATH).toString());
                    PostExecAction.register("lib-gdal [$GDAL_DATA]", args);
                }
            }

            try {
                Files.deleteIfExists(zipPath);
            } catch (IOException e) {
                SystemUtils.LOG.warning("GDAL configuration error: failed to delete zip after decompression");
            }
        }
    }

    @Override
    public void stop() {
        // Purposely no-op
    }

    private static Path getGDALAuxDataPath() {
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
        WIN_32("gdal-2.1.0-win32", "release-1500-gdal-2-1-0-mapserver-7-0-1.zip",
                new String[] { "SETX", "PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DRIVER_PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DATA", "\"$1\"" },
                "gdaljni.dll", "gdalconstjni.dll", "ogrjni.dll", "osrjni.dll"),
        WIN_64("gdal-2.1.0-win64", "release-1500-x64-gdal-2-1-0-mapserver-7-0-1.zip",
                new String[] { "SETX", "PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DRIVER_PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DATA", "\"$1\"" },
                "gdaljni.dll", "gdalconstjni.dll", "ogrjni.dll", "osrjni.dll"),
        LINUX_64("gdal-2.1.0-linux64", "release-1500-gdal-2-1-0-mapserver-7-0-1.zip",
                new String[] { "SET", "LD_LIBRARY_PATH", "\"$1:%LD_LIBRARY_PATH%\"" },
                new String[] { "SET", "GDAL_DRIVER_PATH", "\"$1\"" },
                new String[] { "SET", "GDAL_DATA", "\"$1\"" },
                "gdaljni.so", "gdalconstjni.so", "ogrjni.so", "osrjni.so"),
        MAC_OS_X(null, null, null, null, null),
        UNSUPPORTED(null, null, null, null, null);

        String directory;
        String zipFile;
        String[] jniFiles;
        String[] cmdPath;
        String[] cmdDriverPath;
        String[] cmdDataPath;

        OSCategory(String directory, String zipFile, String[] cmdLine, String[] cmdDriverPath, String[] cmdDataPath, String... jniFiles) {
            this.directory = directory;
            this.zipFile = zipFile;
            this.cmdPath = cmdLine;
            this.cmdDriverPath = cmdDriverPath;
            this.cmdDataPath = cmdDataPath;
            this.jniFiles = jniFiles;
        }

        Path getArchivePath() {
            return directory != null && zipFile != null ? Paths.get(directory, zipFile) : null;
        }

        String[] getPathCmd() { return this.cmdPath; }

        String[] getDriverPathCmd() { return this.cmdDriverPath; }

        String[] getDataPathCmd() { return this.cmdDataPath; }

        String[] getJniFiles() { return this.jniFiles; }

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
