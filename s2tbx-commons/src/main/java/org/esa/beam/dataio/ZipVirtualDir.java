package org.esa.beam.dataio;

import com.bc.ceres.core.VirtualDir;
import org.esa.beam.util.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an extension of com.bc.ceres.core.VirtualDir class for
 * accessing files and folders inside a ZIP archive.
 * Also, this represents an abstraction of the underlying source, so that
 * the source can be either file system (i.e. uncompressed product structure) or a zip file.
 * @author Cosmin Cara
 * @see com.bc.ceres.core.VirtualDir
 */
public class ZipVirtualDir {
    protected boolean isZipFile;
    protected boolean shouldConvertCase;
    protected String unnecessaryPath;
    protected VirtualDir wrappedVirtualDir;

    /**
     * Constructor that wraps a virtual directory over a file or folder source.
     * @param source    The source file or folder.
     * @throws IOException
     */
    public ZipVirtualDir(File source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("Input file shall not be null");
        }
        wrappedVirtualDir = VirtualDir.create(source);
        if (wrappedVirtualDir == null)
            throw new IOException("Failed to open " + source.getName());
        isZipFile = isZip(source.getName());
        unnecessaryPath = "";
        if (isZipFile) {
            correctCapitalisation();
        }
    }

    /**
     * Returns a file by its name from this virtual directory.
     * @param relativePath  The path of the file, relative to this virtual directory.
     * @return  A <code>File</code> object if the file was found.
     * @throws IOException  Exception thrown if the path cannot be properly accessed.
     */
    public File getFile(String relativePath) throws IOException {
        if (!isZipFile) {
            relativePath = relativePath.replaceAll("/", "\\\\");
        } else {
            if (unnecessaryPath != null && !relativePath.startsWith(unnecessaryPath)) {
                relativePath = unnecessaryPath + "/" + relativePath;
            }
        }
        return shouldConvertCase ? wrappedVirtualDir.getFile(relativePath.toUpperCase()) : wrappedVirtualDir.getFile(relativePath);
    }

    /**
     * Lists the entries of this virtual directory (only the first level).
     *
     * @param path      The path to look into. Set it to "" to return all files from the first level.
     * @return          A list of file names.
     * @throws IOException
     */
    public String[] list(String path) throws IOException {
        return wrappedVirtualDir.list(path);
    }

    /**
     * Tries to findFirst the first file that contains the given string.
     * @param pattern       A string to be found in the file name (if any).
     * @return      The name of the found file, or <code>NULL</code> if no file was found.
     * @throws IOException
     */
    public String findFirst(String pattern) throws IOException {
        String found = null;
        String[] entries = wrappedVirtualDir.list("");
        if (entries != null) {
            for (String entry : entries) {
                if (entry.toLowerCase().contains(pattern)) {
                    found = entry;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Tries to findFirst the first file that contains the given string.
     * @param pattern       A string to be found in the file name (if any).
     * @return      The name of the found file, or <code>NULL</code> if no file was found.
     * @throws IOException
     */
    public String[] findAll(String pattern) throws IOException {
        List<String> found = new ArrayList<String>();
        String[] entries = wrappedVirtualDir.list("");
        if (entries != null) {
            for (String entry : entries) {
                if (entry.toLowerCase().contains(pattern)) {
                    found.add(entry);
                }
            }
        }
        return found.toArray(new String[found.size()]);
    }

    /**
     * Closes the virtual directory.
     */
    public void close() {
        wrappedVirtualDir.close();
    }

    public void finalize() throws Throwable {
        close();
        super.finalize();
    }

    static boolean isZip(String filename) {
        final String extension = FileUtils.getExtension(filename);
        return (".zip".equals(extension) || ".ZIP".equals(extension));
    }

    public boolean isThisZipFile(){
        return this.isZipFile;
    }

    /**
     * Indicates if file name capitalisation should be corrected for this dir (i.e. if the file names are
     * in uppercase, they will be first converted to lowercase before any other operation).
     *
     * @return  <code>true</code> if upper-to-lowercase should be performed, <code>false</code> otherwise.
     * @throws IOException      This exception is not actually thrown in the base class, but it can be thrown
     *                          in derived classes.
     */
    protected boolean correctCapitalisation() throws IOException {
        return (shouldConvertCase = false);
    }
}
