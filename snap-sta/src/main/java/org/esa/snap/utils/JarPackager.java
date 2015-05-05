/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */
package org.esa.snap.utils;

import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterIO;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.*;

/**
 * Utility class for creating at runtime a jar module
 * for a tool adapter, so that it can be independently deployed.
 *
 * @author Cosmin Cara
 */
public final class JarPackager {

    private static final Manifest _manifest;
    private static final Attributes.Name ATTR_DESCRIPTION_NAME;
    private static final File modulesPath;

    static {
        _manifest = new Manifest();
        Attributes attributes = _manifest.getMainAttributes();
        ATTR_DESCRIPTION_NAME = new Attributes.Name("OpenIDE-Module-Short-Description");
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(new Attributes.Name("OpenIDE-Module-Java-Dependencies"), "Java > 1.8");
        attributes.put(new Attributes.Name("OpenIDE-Module-Module-Dependencies"), "org.esa.snap.snap.sta.ui");
        attributes.put(new Attributes.Name("OpenIDE-Module-Display-Category"), "SNAP");
        attributes.put(ATTR_DESCRIPTION_NAME, "External tool adapter");
        modulesPath = ToolAdapterIO.getUserAdapterPath();
    }

    /**
     * Packs the files associated with the given tool adapter operator descriptor into
     * a jar file.
     *
     * @param descriptor    The tool adapter descriptor
     * @param jarFile       The target jar file
     * @throws IOException
     */
    public static void packAdapterJar(ToolAdapterOperatorDescriptor descriptor, File jarFile, Class moduleInstallerClass) throws IOException {
        _manifest.getMainAttributes().put(ATTR_DESCRIPTION_NAME, "<p>" + descriptor.getAlias() + "</p>");
        File moduleFolder = new File(modulesPath, descriptor.getAlias());
        try (FileOutputStream fOut = new FileOutputStream(jarFile)) {
            if (moduleInstallerClass != null) {
                _manifest.getMainAttributes().put(new Attributes.Name("OpenIDE-Module-Install"), moduleInstallerClass.getName().replace('.', '/') + ".class");
            }
            try (JarOutputStream jarOut = new JarOutputStream(fOut, _manifest)) {
                File[] files = moduleFolder.listFiles();
                if (files != null) {
                    for (File child : files) {
                        addFile(child, jarOut);
                    }
                }
                if (moduleInstallerClass != null) {
                    addFile(moduleInstallerClass, jarOut);
                }
                jarOut.close();
            }
            fOut.close();
        }
    }

    /**
     * Unpacks a jar file into the user modules location.
     *
     * @param jarFile   The jar file to be unpacked
     * @throws IOException
     */
    public static void unpackAdapterJar(File jarFile) throws IOException {
        JarFile jar = new JarFile(jarFile);
        Enumeration enumEntries = jar.entries();
        File unpackFolder = new File(modulesPath, jarFile.getName().replace(".jar", ""));
        unpackFolder.mkdir();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            File f = new File(unpackFolder, file.getName());
            if (file.isDirectory()) {
                f.mkdir();
                continue;
            } else {
                f.getParentFile().mkdirs();
            }
            try (InputStream is = jar.getInputStream(file)) {
                try (FileOutputStream fos = new FileOutputStream(f)) {
                    while (is.available() > 0) {
                        fos.write(is.read());
                    }
                    fos.close();
                }
                is.close();
            }
        }
    }

    /**
     * Adds a file to the target jar stream.
     *
     * @param source    The file to be added
     * @param target    The target jar stream
     * @throws IOException
     */
    private static void addFile(File source, JarOutputStream target) throws IOException {
        String entryName = source.getPath().replace(modulesPath.getAbsolutePath(), "").replace("\\", "/").substring(1);
        entryName = entryName.substring(entryName.indexOf("/") + 1);
        if (source.isDirectory()) {
            if (!entryName.isEmpty()) {
                if (!entryName.endsWith("/")) {
                    entryName += "/";
                }
                JarEntry entry = new JarEntry(entryName);
                entry.setTime(source.lastModified());
                target.putNextEntry(entry);
                target.closeEntry();
            }
            File[] files = source.listFiles();
            if (files != null) {
                for (File nestedFile : files) {
                    addFile(nestedFile, target);
                }
            }
            return;
        }

        JarEntry entry = new JarEntry(entryName);
        entry.setTime(source.lastModified());
        target.putNextEntry(entry);
        writeBytes(source, target);
        target.closeEntry();
    }

    /**
     * Adds a compiled class file to the target jar stream.
     *
     * @param fromClass     The class to be added
     * @param target        The target jar stream
     * @throws IOException
     */
    private static void addFile(Class fromClass, JarOutputStream target) throws IOException {
        String classEntry = fromClass.getName().replace('.', '/') + ".class";
        URL classURL = fromClass.getClassLoader().getResource(classEntry);
        if (classURL != null) {
            JarEntry entry = new JarEntry(classEntry);
            target.putNextEntry(entry);
            String fileName = classURL.getFile();
            writeBytes(fileName, target);
            target.closeEntry();
        }
    }

    private static void writeBytes(String fileName, JarOutputStream target) throws IOException {
        writeBytes(new File(fileName), target);
    }

    private static void writeBytes(File file, JarOutputStream target) throws IOException {
        try (FileInputStream fileStream = new FileInputStream(file)) {
            try (BufferedInputStream inputStream = new BufferedInputStream(fileStream)) {
                byte[] buffer = new byte[1024];
                while (true) {
                    int count = inputStream.read(buffer);
                    if (count == -1) {
                        break;
                    }
                    target.write(buffer, 0, count);
                }
            }
        }
    }

}
