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

import org.esa.s2tbx.dataio.gdal.preferences.GdalOptionsController;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.rcp.util.Dialogs;
import org.esa.snap.utils.FileHelper;
import org.esa.snap.utils.NativeLibraryUtils;
import org.esa.snap.utils.PostExecAction;
import org.openide.util.NbPreferences;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
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
        OSCategory osCategory = OSCategory.getOSCategory();
        String[] jniFiles = osCategory.getJniFiles();
        if (jniFiles == null || jniFiles.length == 0) {
            logger.log(Level.SEVERE, "No JNI wrappers found.");
            return;
        }

        Preferences preferences = NbPreferences.forModule(Dialogs.class);
        String gdalBinFolderPathAsString = preferences.get(GdalOptionsController.PREFERENCE_KEY_GDAL_BIN_PATH, null);
        String mapLibraryName = System.mapLibraryName("gdal201");
        String pathEnvironment = System.getenv("PATH");
        if (StringUtils.isNullOrEmpty(gdalBinFolderPathAsString)) {
            installDistribution(osCategory, mapLibraryName, pathEnvironment);

            // save in the preferences the GDAL bin folder path if it is installed
            GdalInstallInfo gdalInstallInfo = GdalInstallInfo.INSTANCE;
            if (gdalInstallInfo.isPresent()) {
                preferences.put(GdalOptionsController.PREFERENCE_KEY_GDAL_BIN_PATH, gdalInstallInfo.getBinLocation().toString());
            }
        } else {
            Path gdalBinFolderPath = Paths.get(gdalBinFolderPathAsString);
            processInstalledDistribution(gdalBinFolderPath, osCategory, mapLibraryName, pathEnvironment);
        }
    }

    private void processInstalledDistribution(Path gdalBinFolderPath, OSCategory osCategory, String mapLibraryName, String pathEnvironment) throws IOException {
        Path pathItem = gdalBinFolderPath.resolve(mapLibraryName);
        if (Files.exists(pathItem)) {
            // the library file exists on the local disk
            if (registerNativePaths(gdalBinFolderPath, osCategory)) {
                boolean canSetLocations = true;

                Path gdalAppsFolderPath = gdalBinFolderPath.resolve(APPS_PATH);

                boolean foundBinFolderInPath = findFolderInPathEnvironment(gdalBinFolderPath, pathEnvironment);
                if (!foundBinFolderInPath) {
                    canSetLocations = false;
                    String[] args = osCategory.getPathCmd();
                    if (args != null && args.length > 2) {
                        args[2] = args[2].replace("$1", gdalBinFolderPath.toString() + File.pathSeparator + gdalAppsFolderPath.toString());
                        PostExecAction.register("lib-gdal [$PATH]", args);
                    }
                }

                Path gdalDataFolderPath = gdalBinFolderPath.resolve(DATA_PATH);
                String gdalDataEnvironment = System.getenv("GDAL_DATA");
                boolean canSetGDALDataEnvironment = false;
                if (gdalDataEnvironment == null) {
                    canSetGDALDataEnvironment = true;
                } else {
                    canSetGDALDataEnvironment = !findFolderInPathEnvironment(gdalDataFolderPath, gdalDataEnvironment);
                }
                if (canSetGDALDataEnvironment) {
                    canSetLocations = false;
                    String[] args = osCategory.getDataPathCmd();
                    if (args != null && args.length > 2) {
                        args[2] = args[2].replace("$1", gdalDataFolderPath.toString());
                        PostExecAction.register("lib-gdal [$GDAL_DATA]", args);
                    }
                }

                Path gdalDriverFolderPath = gdalBinFolderPath.resolve(PLUGINS_PATH);
                String gdalDriverPathEnvironment = System.getenv("GDAL_DRIVER_PATH");
                boolean canSetGDALDriverPathEnvironment = false;
                if (gdalDriverPathEnvironment == null) {
                    canSetGDALDriverPathEnvironment = true;
                } else {
                    canSetGDALDriverPathEnvironment = !findFolderInPathEnvironment(gdalDriverFolderPath, gdalDriverPathEnvironment);
                }
                if (canSetGDALDriverPathEnvironment) {
                    canSetLocations = false;
                    String[] args = osCategory.getDriverPathCmd();
                    if (args != null && args.length > 2) {
                        args[2] = args[2].replace("$1", gdalDriverFolderPath.toString());
                        PostExecAction.register("lib-gdal [$GDAL_DRIVER_PATH]", args);
                    }
                }

                if (canSetLocations) {
                    GdalInstallInfo gdalInstallInfo = GdalInstallInfo.INSTANCE;
                    gdalInstallInfo.setLocations(gdalBinFolderPath, gdalAppsFolderPath, gdalDriverFolderPath, gdalDataFolderPath);
                }
            }
        } else {
            logger.log(Level.SEVERE, "The GDAL bin folder '"+gdalBinFolderPath.toString()+"' does not contain the library '" + mapLibraryName + "'.");
        }
    }

    private static boolean registerNativePaths(Path gdalBinFolderPath, OSCategory osCategory) throws IOException {
        Path[] array = findFolderPathsForJNIFiles(gdalBinFolderPath, osCategory);
        if (array == null || array.length == 0) {
            logger.log(Level.SEVERE, "No JNI wrappers found in the folder '" + gdalBinFolderPath.toString() + "'.");
            return false;
        }
        NativeLibraryUtils.registerNativePaths(array);
        return true;
    }

    private void installDistribution(OSCategory osCategory, String mapLibraryName, String pathEnvironment) throws IOException {
        Path gdalBinFolderPath = findFileNameInFoldersOfPathEnvironment(pathEnvironment, mapLibraryName);
        if (gdalBinFolderPath == null) {
            Path auxdataFolderPath = getGDALAuxDataPath();
            if (auxdataFolderPath == null) {
                logger.log(Level.SEVERE, "Failed to retrieve the GDAL folder path from the local disk.");
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

            // the library file does not exist on  the local disk among the folders from path environment
            String zipArchivePath = osCategory.getDirectory() + "/" + osCategory.getZipFileName();

            String zipFilePathFromSources = SRC_PATH + "/" + zipArchivePath;
            URL zipFileURLFromSources = getClass().getClassLoader().getResource(zipFilePathFromSources);
            Path zipFilePath = auxdataFolderPath.resolve(zipArchivePath);
            Path gdalRootFolderPath = zipFilePath.getParent();

            if (!Files.exists(gdalRootFolderPath)) {
                Files.createDirectories(gdalRootFolderPath);
            }

            fixUpPermissions(auxdataFolderPath);
            try {
                FileHelper.copyFile(zipFileURLFromSources, zipFilePath);
                FileHelper.unzip(zipFilePath, gdalRootFolderPath, true);
            } finally {
                try {
                    Files.deleteIfExists(zipFilePath);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "GDAL configuration error: failed to delete zip after decompression.", e);
                }
            }
            gdalBinFolderPath = gdalRootFolderPath.resolve(BIN_PATH);
        }
        processInstalledDistribution(gdalBinFolderPath, osCategory, mapLibraryName, pathEnvironment);
    }

    private static Path getGDALAuxDataPath() {
        return SystemUtils.getAuxDataPath().resolve("gdal");
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

    private static Path[] findFolderPathsForJNIFiles(Path gdalBinFolderPath, OSCategory osCategory) throws IOException {
        String[] jniFileNames = osCategory.getJniFiles();
        Map<String, Path> jniPaths = new HashMap<String, Path>();
        for (int i=0; i<jniFileNames.length; i++) {
            String jniLibraryFileName = System.mapLibraryName(jniFileNames[i]);
            jniPaths.put(jniLibraryFileName, null);
        }

        Set<Path> uniqueFolderPaths = new HashSet<Path>(jniFileNames.length);
        Stack<Path> stack = new Stack<Path>();
        stack.push(gdalBinFolderPath);
        boolean findAllJNIFolders = false;
        while (!stack.isEmpty() && !findAllJNIFolders) {
            Path currentFolderPath = stack.pop();
            Iterator<Map.Entry<String, Path>> it = jniPaths.entrySet().iterator();
            boolean canContinue = false;
            while (it.hasNext()) {
                Map.Entry<String, Path> entry = it.next();
                String jniLibraryFileName = entry.getKey();
                if (entry.getValue() == null) {
                    canContinue = true;
                    Path jniPathItem = currentFolderPath.resolve(jniLibraryFileName);
                    if (Files.exists(jniPathItem)) {
                        entry.setValue(currentFolderPath);
                        uniqueFolderPaths.add(currentFolderPath);
                    }
                }
            }
            if (canContinue) {
                Stream<Path> result = Files.list(currentFolderPath);
                Iterator<Path> itItems = result.iterator();
                while (itItems.hasNext()) {
                    Path itemPath = itItems.next();
                    if (itemPath.toFile().isDirectory()) {
                        stack.push(itemPath);
                    }
                }
            } else {
                findAllJNIFolders = true;
            }
        }
        if (findAllJNIFolders) {
            Path[] array = new Path[uniqueFolderPaths.size()];
            uniqueFolderPaths.toArray(array);
            return array;
        }
        return null;
    }

    private static Path findFileNameInFoldersOfPathEnvironment(String pathEnvironment, String fileNameToCheck) {
        StringTokenizer str = new StringTokenizer(pathEnvironment, File.pathSeparator);
        while (str.hasMoreTokens()) {
            String currentFolderPath = str.nextToken();
            Path pathItem = Paths.get(currentFolderPath, fileNameToCheck);
            if (Files.exists(pathItem)) {
                return Paths.get(currentFolderPath);
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

    private enum OSCategory {
        WIN_32("gdal-2.1.0-win32", "release-1500-gdal-2-1-0-mapserver-7-0-1.zip",
                new String[] { "SETX", "PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DRIVER_PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DATA", "\"$1\"" },
                "gdaljni", "gdalconstjni", "ogrjni", "osrjni"),
        WIN_64("gdal-2.1.0-win64", "release-1500-x64-gdal-2-1-0-mapserver-7-0-1.zip",
                new String[] { "SETX", "PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DRIVER_PATH", "\"$1;%PATH%\"" },
                new String[] { "SETX", "GDAL_DATA", "\"$1\"" },
                "gdaljni", "gdalconstjni", "ogrjni", "osrjni"),
        LINUX_64(null, null,
                new String[] { "SET", "LD_LIBRARY_PATH", "\"$1:%LD_LIBRARY_PATH%\"" },
                new String[] { "SET", "GDAL_DRIVER_PATH", "\"$1\"" },
                new String[] { "SET", "GDAL_DATA", "\"$1\"" },
                "gdaljni", "gdalconstjni", "ogrjni", "osrjni"),
        MAC_OS_X(null, null, null, null, null),
        UNSUPPORTED(null, null, null, null, null);

        String directory;
        String zipFileName;
        String[] jniFiles;
        String[] cmdPath;
        String[] cmdDriverPath;
        String[] cmdDataPath;

        OSCategory(String directory, String zipFileName, String[] cmdLine, String[] cmdDriverPath, String[] cmdDataPath, String... jniFiles) {
            this.directory = directory;
            this.zipFileName = zipFileName;
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
