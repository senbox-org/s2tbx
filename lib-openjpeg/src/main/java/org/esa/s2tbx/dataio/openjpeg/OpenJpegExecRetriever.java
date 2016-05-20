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

package org.esa.s2tbx.dataio.openjpeg;


import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.apache.commons.lang.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang.SystemUtils.IS_OS_MAC_OSX;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

/**
 * Utility class to get executables from OpenJpeg module
 *
 * @author Oscar Picas-Puig
 */
public class OpenJpegExecRetriever {

    /**
     * Compute the path to the openjpeg compressor utility
     *
     * @return The path to opj_compress
     */
    public static String getOpjCompress() {
        return findOpenJpegExecPath(OSCategory.getOSCategory().getCompressor());
    }

    /**
     * Compute the path to the openjpeg decompressor utility
     *
     * @return The path to opj_decompress
     */
    public static String getOpjDecompress() {
        return findOpenJpegExecPath(OSCategory.getOSCategory().getDecompressor());
    }

    /**
     * Compute the path to the openjpeg dump utility
     *
     * @return The path to opj_dump
     */
    public static String getOpjDump() {
        return findOpenJpegExecPath(OSCategory.getOSCategory().getDump());
    }

    public static Path getOpenJPEGAuxDataPath() {
        Path versionFile = ResourceInstaller.findModuleCodeBasePath(OpenJpegExecRetriever.class).resolve("version/version.properties");
        Properties versionProp = new Properties();

        try {
            InputStream inputStream = Files.newInputStream(versionFile);
            versionProp.load(inputStream);
        } catch (IOException e) {
            SystemUtils.LOG.severe("OpenJPEG configuration error: failed to read " + versionFile.toString());
            return null;
        }

        String version = versionProp.getProperty("project.version");
        if (version == null)
        {
            SystemUtils.LOG.severe("OpenJPEG configuration error: unable to get property project.version from " + versionFile.toString());
            return null;
        }

        return SystemUtils.getAuxDataPath().resolve("openjpeg").resolve(version);
    }

    private static String findOpenJpegExecPath(String endPath) {
        if (endPath == null) {
            return null;
        }

        Path path = getOpenJPEGAuxDataPath().resolve(endPath);
        String pathString = null;
        if (path != null) {
            pathString = path.toString();
        }
        return pathString;
    }

    /* The different OS for which OpenJPEG executables are released */
    private enum OSCategory {
        WIN_32("openjpeg-2.1.0-win32", "bin/opj_compress.exe", "bin/opj_decompress.exe", "bin/opj_dump.exe"),
        WIN_64("openjpeg-2.1.0-win64", "bin/opj_compress.exe", "bin/opj_decompress.exe", "bin/opj_dump.exe"),
        LINUX_64("openjpeg-2.1.0-linux64",   "bin/opj_compress",     "bin/opj_decompress",     "bin/opj_dump"),
        MAC_OS_X("openjpeg-2.1.0-macosx", "bin/opj_compress",     "bin/opj_decompress",     "bin/opj_dump"),
        UNSUPPORTED(null, null, null, null);

        String directory;
        String compressor;
        String decompressor;
        String dump;

        OSCategory(String directory, String compressor, String decompressor, String dump) {
            this.directory = directory;
            this.compressor = compressor;
            this.decompressor = decompressor;
            this.dump = dump;
        }

        String getCompressor() {
            return String.format("%s%s%s", directory, File.separator, decompressor);
        }

        String getDecompressor() {
            return String.format("%s%s%s", directory, File.separator, decompressor);
        }

        String getDump() {
            return String.format("%s%s%s", directory, File.separator, dump);
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
