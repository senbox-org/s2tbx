package org.esa.beam.dataio;

import org.esa.beam.util.logging.BeamLogManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.URLDecoder;

/**
 * Simple library class for working with JNI (Java Native Interface)
 *
 * @author Adam Heirnich <adam@adamh.cz>, http://www.adamh.cz
 * @author Slightly modified by Cosmin Cara <cosmin.cara@c-s.ro> to work with file system libraries,
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
     * <p/>
     * The file from JAR is copied into system temporary directory and then loaded. The temporary file is deleted after exiting.
     * Method uses String as filename because the pathname is "abstract", not system-dependent.
     *
     * @param path The filename inside JAR as absolute path (beginning with '/'), e.g. /package/File.ext
     * @throws IOException              If temporary file creation or read/write operation fails
     * @throws IllegalArgumentException If source file (param path) does not exist
     * @throws IllegalArgumentException If the filename is shorter than three characters (restriction of {@see File#createTempFile(java.lang.String, java.lang.String)}).
     */
    public static void loadLibraryFromJar(String path) throws IOException {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;

        // Split filename to prefix and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "." + parts[parts.length - 1] : null; // Thanks, davs! :-)
        }
        // Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }
        // Open and check input stream
        InputStream is = NativeLibraryLoader.class.getResourceAsStream(path);
        String extension = getExtension();
        // for Linux/Solaris, the file name has to begin with "lib*".
        if (extension.contains("so"))
            path = path.replace(filename, "lib" + filename);
        path += getExtension();
        if (is == null) {
            // maybe we are not inside a JAR
            try {
                String parentPath = URLDecoder.decode(NativeLibraryLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
                File test = new File(parentPath + "/lib");

                if (test.exists())
                    path = parentPath + path.replace("/resources/", "");
                System.load(path);
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(e.getMessage());
            }
        } else {
            // Prepare temporary file
            File temp = File.createTempFile(prefix, suffix);
            temp.deleteOnExit();
            if (!temp.exists()) {
                throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
            }
            // Prepare buffer for data copying
            byte[] buffer = new byte[1024];
            int readBytes;
            // Open output stream and copy data between source file in JAR and the temporary file
            OutputStream os = new FileOutputStream(temp);
            try {
                while ((readBytes = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readBytes);
                }
            } finally {
                // If read/write fails, close streams safely before throwing an exception
                os.close();
                is.close();
            }
            // Finally, load the library
            System.load(temp.getAbsolutePath());
            final String libraryPrefix = prefix;
            final String lockSuffix = ".lock";
            // create lock file
            final File lock = new File(temp.getAbsolutePath() + lockSuffix);
            lock.createNewFile();
            lock.deleteOnExit();
            // file filter for library file (without .lock files)
            FileFilter tmpDirFilter = new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().startsWith(libraryPrefix) && !pathname.getName().endsWith(lockSuffix);
                }
            };

            // get all library files from temp folder
            String tmpDirName = System.getProperty("java.io.tmpdir");
            File tmpDir = new File(tmpDirName);
            File[] tmpFiles = tmpDir.listFiles(tmpDirFilter);
            // delete all files which don't have n accompanying lock file
            for (File tmpFile : tmpFiles) {
                // Create a file to represent the lock and test.
                File lockFile = new File(tmpFile.getAbsolutePath() + lockSuffix);
                if (!lockFile.exists()) {
                    tmpFile.delete();
                }
            }
        }
    }

    public static String getExtension() {
        String os = getOSFamily();
        if (os.startsWith("win")) return ".dll";
        else return ".so";
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