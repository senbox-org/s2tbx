/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

/**
 * Simple library class for working with JNI (Java Native Interface)
 *
 * @author Adam Heirnich <adam@adamh.cz>, http://www.adamh.cz
 * @author Modified by Cosmin Cara <cosmin.cara@c-s.ro> to work with file system libraries,
 *         and also to detect the OS and processor type.
 * @see "http://frommyplayground.com/how-to-load-native-jni-library-from-jar"
 */
public class NativeLibraryLoader {

    /**
     * Private constructor - this class will never be instanced
     */
    private NativeLibraryLoader() {
    }

    /**
     * Loads library either from the current JAR archive, or from file system
     * <p>
     * The file from JAR is copied into system temporary directory and then loaded.
     *
     * @param path          The path from which the load is attempted
     * @param libraryName   The name of the library to be loaded (without extension)
     * @throws IOException              If temporary file creation or read/write operation fails
     */
    public static void loadLibrary(String path, String libraryName) throws IOException {
        path = URLDecoder.decode(path, "UTF-8");
        String libPath = "/lib/" + NativeLibraryLoader.getOSFamily() + "/" + System.mapLibraryName(libraryName);
        if (path.contains(".jar!") || path.endsWith(".jar")) {
            try (InputStream in = NativeLibraryLoader.class.getResourceAsStream(libPath)) {
                File fileOut = new File(System.getProperty("java.io.tmpdir"), libPath);
                try (OutputStream out = FileUtils.openOutputStream(fileOut)) {
                    IOUtils.copy(in, out);
                }
                path = fileOut.getAbsolutePath();
            }
        } else {
            path = new File(path, libPath).getAbsolutePath();
        }
        System.load(path);
    }

    public static String getOSFamily() {
        String ret;
        String sysName = System.getProperty("os.name").toLowerCase();
        String sysArch = System.getProperty("os.arch").toLowerCase();
        if (sysName.contains("windows")) {
            if (sysArch.contains("amd64") || sysArch.contains("x86_x64")) {
                ret = "win64";
            } else {
                ret = "win32";
            }
        } else if (sysName.contains("linux")) {
            ret = "linux";
        } else if (sysName.contains("mac")) {
            ret = "macosx";
        } else {
            throw new NotImplementedException();
        }
        return ret;
    }
}
