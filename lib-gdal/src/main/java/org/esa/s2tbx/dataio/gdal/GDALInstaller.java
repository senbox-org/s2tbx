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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
    private static final String APPS_PATH = BIN_PATH + "/gdal/apps";
    private static final String PLUGINS_PATH = BIN_PATH + "/gdal/plugins";
    private static final String DATA_PATH = BIN_PATH + "/gdal-data";

    public GDALInstaller() {
    }

    /**
     * Install the GDAL library if missing.
     *
     * @throws IOException
     */
    public void install() throws IOException {
        Path auxdataFolderPath = getGDALAuxDataPath();
        if (auxdataFolderPath == null) {
            logger.log(Level.SEVERE, "GDAL configuration error: failed to retrieve auxdata path.");
            return;
        }
        OSCategory osCategory = OSCategory.getOSCategory();
        String[] jniFiles = osCategory.getJniFiles();
        if (jniFiles == null || jniFiles.length == 0) {
            logger.log(Level.SEVERE, "No JNI wrappers found.");
            return;
        }
        if (osCategory.getDirectory() == null) {
            logger.log(Level.SEVERE, "No folder found.");
            return;
        }
        if (osCategory.getZipFileName() == null) {
            logger.log(Level.SEVERE, "No zip file name found.");
            return;
        }

        String zipArchivePath = osCategory.getDirectory() + "/" + osCategory.getZipFileName();

        Path zipFilePath = auxdataFolderPath.resolve(zipArchivePath);
        Path destinationFolder = zipFilePath.getParent();
        Path javaJNIFolderPath = destinationFolder.resolve("java-jni");

        String zipFilePathFromSources = SRC_PATH + "/" + zipArchivePath;
        URL zipFileURLFromSources = getClass().getClassLoader().getResource(zipFilePathFromSources);

        if (!Files.exists(javaJNIFolderPath)) {
            Files.createDirectories(javaJNIFolderPath);
            try {
                FileHelper.copyFile(zipFileURLFromSources, zipFilePath);
                copyFilesFromZip(zipFilePath.toFile(), javaJNIFolderPath, jniFiles);
            } finally {
                try {
                    Files.deleteIfExists(zipFilePath);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "GDAL configuration error: failed to delete zip after decompression.", e);
                }
            }
        }
        NativeLibraryUtils.registerNativePaths(javaJNIFolderPath);

        String pathEnvironment = System.getenv("PATH");
        String mapLibraryName = System.mapLibraryName("gdal201");
        Path existingBinPath = findExistingBinPath(pathEnvironment, mapLibraryName);
        if (existingBinPath == null) {
            fixUpPermissions(auxdataFolderPath);
            try {
                FileHelper.copyFile(zipFileURLFromSources, zipFilePath);
                FileHelper.unzip(zipFilePath, destinationFolder, true);
            } finally {
                try {
                    Files.deleteIfExists(zipFilePath);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "GDAL configuration error: failed to delete zip after decompression.", e);
                }
            }

            existingBinPath = findExistingBinPath(pathEnvironment, mapLibraryName);

            Path binPath = destinationFolder.resolve(BIN_PATH);
            if (!pathEnvironment.contains(binPath.toString())) {
                String[] args = osCategory.getPathCmd();
                if (args != null && args.length > 2) {
                    args[2] = args[2].replace("$1", binPath.toString() + File.pathSeparator + destinationFolder.resolve(APPS_PATH).toString());
                    PostExecAction.register("lib-gdal [$PATH]", args);
                }
            }
            if (System.getenv("GDAL_DRIVER_PATH") == null) {
                String[] args = osCategory.getDriverPathCmd();
                if (args != null && args.length > 2) {
                    args[2] = args[2].replace("$1", destinationFolder.resolve(PLUGINS_PATH).toString());
                    PostExecAction.register("lib-gdal [$GDAL_DRIVER_PATH]", args);
                }
            }
            if (System.getenv("GDAL_DATA") == null) {
                String[] args = osCategory.getDataPathCmd();
                if (args != null && args.length > 2) {
                    args[2] = args[2].replace("$1", destinationFolder.resolve(DATA_PATH).toString());
                    PostExecAction.register("lib-gdal [$GDAL_DATA]", args);
                }
            }
        }
        if (existingBinPath != null) {
            Path root = existingBinPath.getParent();
            Path appsLocation = root.resolve(APPS_PATH);
            Path driversLocation = root.resolve(PLUGINS_PATH);
            Path dataLocation = root.resolve(DATA_PATH);
            GdalInstallInfo gdalInstallInfo = GdalInstallInfo.INSTANCE;
            gdalInstallInfo.setLocations(existingBinPath, appsLocation, driversLocation, dataLocation);
        }
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
                    logger.log(Level.SEVERE, "GDAL configuration error: failed to fix permissions on " + path, e);
                }
            }
            else {
                setExecutablePermissions(path);
            }
        });
    }

    private static Path findExistingBinPath(String pathEnvironment, String mapLibraryName) {
        StringTokenizer str = new StringTokenizer(pathEnvironment, File.pathSeparator);
        while (str.hasMoreTokens()) {
            String folderPath = str.nextToken();
            Path pathItem = Paths.get(folderPath, mapLibraryName);
            if (Files.exists(pathItem)) {
                return Paths.get(folderPath);
            }
        }
        return null;
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

    private static void copyFilesFromZip(File sourceZipFile, Path destinationFolder, String[] filesToCopy) throws IOException {
        if (sourceZipFile == null || destinationFolder == null) {
            throw new IllegalArgumentException("One of the arguments is null");
        }
        if (!Files.exists(destinationFolder)) {
            Files.createDirectory(destinationFolder);
        }
        byte[] buffer;
        try (ZipFile zipFile = new ZipFile(sourceZipFile)) {
            ZipEntry entry;
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                for (int i=0; i<filesToCopy.length; i++) {
                    if (entry.getName().equalsIgnoreCase(filesToCopy[i])) {
                        Path filePath = destinationFolder.resolve(entry.getName());
                        Path strippedFilePath = destinationFolder.resolve(filePath.getFileName());
                        try (InputStream inputStream = zipFile.getInputStream(entry)) {
                            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(strippedFilePath.toFile()))) {
                                buffer = new byte[4096];
                                int read;
                                while ((read = inputStream.read(buffer)) > -1) {
                                    bos.write(buffer, 0, read);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private enum OSCategory {
        WIN_32("gdal-2.1.0-win32", "release-1500-gdal-2-1-0-mapserver-7-0-1.zip",
                new String[] { "SETX", "PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DRIVER_PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DATA", "\"$1\"" },
                "bin/gdal/java/gdaljni.dll", "bin/gdal/java/gdalconstjni.dll", "bin/gdal/java/ogrjni.dll", "bin/gdal/java/osrjni.dll"),
        WIN_64("gdal-2.1.0-win64", "release-1500-x64-gdal-2-1-0-mapserver-7-0-1.zip",
                new String[] { "SETX", "PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DRIVER_PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DATA", "\"$1\"" },
                "bin/gdal/java/gdaljni.dll", "bin/gdal/java/gdalconstjni.dll", "bin/gdal/java/ogrjni.dll", "bin/gdal/java/osrjni.dll"),
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

        String[] getPathCmd() { return this.cmdPath; }

        String[] getDriverPathCmd() { return this.cmdDriverPath; }

        String[] getDataPathCmd() { return this.cmdDataPath; }

        String[] getJniFiles() { return this.jniFiles; }

        String getDirectory() { return this.directory; }

        String getZipFileName() { return this.zipFile; }

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
