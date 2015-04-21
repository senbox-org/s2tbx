package org.esa.snap.utils;

import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterIO;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.*;

//import org.esa.snap.ui.tooladapter.ModuleInstaller;

/**
 * Utility class for creating at runtime a NetBeans jar module
 * for a tool adapter, so that it can be independently deployed.
 *
 * @author Cosmin Cara
 */
public final class NetBeansModulePackager {

    private static final File modulesPath = ToolAdapterIO.getUserModulesPath();

    public static void packAdapterJar(ToolAdapterOperatorDescriptor descriptor, File jarFile) throws IOException {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(new Attributes.Name("OpenIDE-Module-Java-Dependencies"), "Java > 1.8");
        attributes.put(new Attributes.Name("OpenIDE-Module-Display-Category"), "SNAP");
        attributes.put(new Attributes.Name("OpenIDE-Module-Short-Description"), "<p>" + descriptor.getAlias() + "</p>");
//        attributes.put(new Attributes.Name("OpenIDE-Module-Install"), ModuleInstaller.class.getName());
        File moduleFolder = new File(modulesPath, descriptor.getAlias());
        try (FileOutputStream fOut = new FileOutputStream(jarFile)) {
            try (JarOutputStream jarOut = new JarOutputStream(fOut, manifest)) {
                File[] files = moduleFolder.listFiles();
                for (File child : files) {
                    addFile(child, jarOut);
                }
//                addFile(ModuleInstaller.class, jarOut, moduleFolder);
                jarOut.close();
            }
            fOut.close();
        }
    }

    public static void unpackAdapterJar(File jarFile) throws IOException {
        JarFile jar = new JarFile(jarFile);
        Enumeration enumEntries = jar.entries();
        File unpackFolder = new File(modulesPath, jarFile.getName().replace(".jar", ""));
        unpackFolder.mkdir();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            File f = new File(unpackFolder, file.getName());
            if (file.isDirectory()) { // if its a directory, create it
                f.mkdir();
                continue;
            } else {
                f.getParentFile().mkdirs();
            }
            try (InputStream is = jar.getInputStream(file)) {
                try (FileOutputStream fos = new FileOutputStream(f)) {
                    while (is.available() > 0) {  // write contents of 'is' to 'fos'
                        fos.write(is.read());
                    }
                    fos.close();
                }
                is.close();
            }
        }
    }

    private static void addFile(File source, JarOutputStream target) throws IOException {
        BufferedInputStream in = null;
        try {
            String entryName = source.getPath().replace(modulesPath.getAbsolutePath(), "").replace("\\", "/").substring(1);
            entryName = entryName.substring(entryName.indexOf("/") + 1);
            if (source.isDirectory()) {
                //String name = source.getPath().replace("\\", "/");
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
            in = new BufferedInputStream(new FileInputStream(source));
            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                }
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private static void addFile(Class fromClass, JarOutputStream target, File adapterFolder) throws IOException {
        String classEntry = fromClass.getName().replace('.', '/') + ".class";
        BufferedInputStream in = null;
        try {
        JarEntry entry = new JarEntry(classEntry);
        target.putNextEntry(entry);
        URL classURL = fromClass.getClassLoader().getResource(classEntry);
        in = new BufferedInputStream(new FileInputStream(classURL.getFile()));
        byte[] buffer = new byte[1024];
        while (true) {
            int count = in.read(buffer);
            if (count == -1) {
                break;
            }
            target.write(buffer, 0, count);
        }
        target.closeEntry();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

}
