/*
 *
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.openjpeg;


import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.runtime.EngineConfig;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.SystemUtils.*;


/**
 * Utility class to get executables from OpenJpeg module
 *
 * @author Oscar Picas-Puig
 */
public class OpenJpegExecRetriever {

    private static String OPENJPEG_EXEC_PATH_PROPERTY = "s2tbx.openjpeg.dir";

    /**
     * Compute the path to the openjpeg dump utility and
     * fix up permissions for it on mac os/linux
     *
     * @return The path to opj_dump
     */
    public static String getSafeInfoExtractorAndUpdatePermissions() {
        String infoExtractorPathString = null;
        Path infoExtractorPath = findOpenJpegExecPath(getOSCategory().getDump());

        if (infoExtractorPath != null) {
            setExecutablePermissions(infoExtractorPath);
            infoExtractorPathString = infoExtractorPath.toString();
        }

        return infoExtractorPathString;
    }

    /**
     * Compute the path to the openjpeg decompressor utility and
     * fix up permissions for it on mac os/linux
     *
     * @return The path to opj_decompress
     */
    public static String getSafeDecompressorAndUpdatePermissions() {
        String decompressorPathString = null;
        Path decompressorPath = findOpenJpegExecPath(getOSCategory().getDecompressor());

        if (decompressorPath != null) {
            setExecutablePermissions(decompressorPath);
            decompressorPathString = decompressorPath.toString();
        }

        return decompressorPathString;
    }

    private static Path findOpenJpegExecPath(String endPath) {
        return SystemUtils.getAuxDataPath().resolve("openjpeg").resolve(endPath);
        /*
        Path pathToExec = null;

        String openJpegDir = EngineConfig.instance("s2tbx").preferences().get(OPENJPEG_EXEC_PATH_PROPERTY, null);

        // openjpeg executables should be in the install dir or in the user dir.
        // it is also possible to specify its path
        // check the config first, then then user jar dir and finally the class dir
        if (openJpegDir != null) {
            pathToExec = Paths.get(openJpegDir).resolve(endPath);
        }


        if (pathToExec == null || !Files.exists(pathToExec)) {

            try {
                URI thisJarURI = OpenJpegExecRetriever.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                endPath = "ext/org.esa.s2tbx.lib-openjpeg/" + endPath;

                SystemUtils.LOG.fine("OpenJpegExecRetriever jar location URI: " + thisJarURI.toString());

                if (thisJarURI.toString().startsWith("jar:")) {
                    //int lastSepPosition = thisJarString.substring(0, thisJarString.length() - 1).lastIndexOf('/');
                    //thisJarString = thisJarString.substring(10, lastSepPosition);
                    thisJarURI = URI.create(thisJarURI.toString().substring(4));
                    Path thisJarDirPath = Paths.get(thisJarURI).getParent();
                    pathToExec = thisJarDirPath.resolve(endPath);
                } else if (thisJarURI.toString().endsWith(".jar")) {
                    Path thisJarDirPath = Paths.get(thisJarURI).getParent();
                    pathToExec = thisJarDirPath.resolve(endPath);
                } else {
                    // should be in dev mode
                    Path thisJarPath = Paths.get(thisJarURI);
                    pathToExec = thisJarPath.getParent().getParent().getParent()
                            .resolve("lib-openjpeg/target/nbm/netbeans/s2tbx/modules")
                            .resolve(endPath);
                }

                if (!Files.exists(pathToExec)) {
                    pathToExec = null;
                    SystemUtils.LOG.severe("Could not find OpenJpeg executable " + endPath);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return pathToExec;
        */
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
                SystemUtils.LOG.warning("Can't set execution permissions for executable " + executablePathName.toString() +
                                                ". If required, please ask an authorised user to make the file executable.");
            }
        }
    }

    /* The different OS for which OpenJPEG executables are released */
    private enum OSCategory {
        WIN_32("openjpeg-2.1.0-win32-x86_dyn", "bin/opj_decompress.exe", "bin/opj_dump.exe"),
        LINUX_32("openjpeg-2.1.0-Linux-i386", "bin/opj_decompress", "bin/opj_dump"),
        LINUX_64("openjpeg-2.1.0-Linux-x64", "bin/opj_decompress", "bin/opj_dump"),
        MAC_OS_X("openjpeg-2.1.0-Darwin-i386", "bin/opj_decompress", "bin/opj_dump"),
        WIN_64("openjpeg-2.1.0-win32-x64_dyn", "bin/opj_decompress.exe", "bin/opj_dump.exe"),
        UNSUPPORTED(null, null, null);

        String directory;
        String decompressor;
        String dump;

        OSCategory(String directory, String decompressor, String dump) {
            this.directory = directory;
            this.decompressor = decompressor;
            this.dump = dump;
        }

        String getDecompressor() {
            return String.format("%s%s%s", directory, File.separator, decompressor);
        }

        String getDump() {
            return String.format("%s%s%s", directory, File.separator, dump);
        }
    }

    private static OSCategory getOSCategory() {
        OSCategory category;
        if (IS_OS_LINUX) {
            category = OSCategory.LINUX_32;
            try {
                Process p = Runtime.getRuntime().exec("uname -m");
                p.waitFor();

                String osArch = OpenJpegUtils.convertStreamToString(p.getInputStream());

                if (!osArch.equalsIgnoreCase("i686")) {
                    category = OSCategory.LINUX_64;
                }
            } catch (IOException | InterruptedException e) {
                // by default we use the 32 bits path as it works also on 64 bits platform
                SystemUtils.LOG.warning(
                        "Could not find system architecture 32/64 bits, openjpeg executables for 32 bits will be used: " +
                                e.getMessage());
            }
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
