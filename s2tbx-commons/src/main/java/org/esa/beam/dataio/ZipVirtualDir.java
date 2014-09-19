package org.esa.beam.dataio;

import com.bc.ceres.core.VirtualDir;
import org.esa.beam.util.StringUtils;
import org.esa.beam.util.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is an extension of com.bc.ceres.core.VirtualDir class for
 * accessing files and folders inside a ZIP archive.
 * Also, this represents an abstraction of the underlying source, so that
 * the source can be either file system (i.e. uncompressed product structure) or a zip file.
 *
 * @author Cosmin Cara
 * @see com.bc.ceres.core.VirtualDir
 */
public class ZipVirtualDir {
    protected boolean isZipFile;
    protected boolean isTarFile;
    protected boolean shouldConvertCase;
    protected String unnecessaryPath;
    protected VirtualDir wrappedVirtualDir;

    /**
     * Constructor that wraps a virtual directory over a file or folder source.
     *
     * @param source The source file or folder.
     * @throws IOException
     */
    public ZipVirtualDir(File source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("Input file shall not be null");
        }
        wrappedVirtualDir = VirtualDir.create(source);
        if (wrappedVirtualDir == null) {
            //try to open as a tar!
            wrappedVirtualDir = new TarVirtualDir(source);
            if (wrappedVirtualDir == null) {
                throw new IOException("Failed to open " + source.getName());
            }
        }
        isZipFile = isZip(source.getName());
        isTarFile = isTar(source.getName());
        unnecessaryPath = "";
        if (isZipFile) {
            correctCapitalisation();
        }
    }

    /**
     * Returns a file by its name from this virtual directory.
     *
     * @param relativePath The path of the file, relative to this virtual directory.
     * @return A <code>File</code> object if the file was found.
     * @throws IOException Exception thrown if the path cannot be properly accessed.
     */
    public File getFile(String relativePath) throws IOException {
        String pathSeparator;
        if (!isZipFile && !isTarFile) {
            pathSeparator = "\\\\";
            relativePath = relativePath.replaceAll("/", "\\\\");
        } else {
            pathSeparator = "/";
        }
        try {
            //if the path letter case is correct, there is no need to read all the path tree
            File result = wrappedVirtualDir.getFile(shouldConvertCase ? relativePath.toUpperCase() : relativePath);
            if (result != null) {
                return result;
            }
        } catch (IOException ex) {
        }
        String[] relativePathArray = relativePath.split(pathSeparator);
        String newRelativePath = "";
        String[] files = wrappedVirtualDir.list("");
        int index = 0;
        while (files != null && files.length > 0 && index < relativePathArray.length) {
            boolean found = false;
            for (String file : files) {
                if (relativePathArray[index].equalsIgnoreCase(file)) {
                    newRelativePath += file + pathSeparator;
                    index++;
                    found = true;
                    if (index < relativePathArray.length) {//there are still subfolders/subfiles to be searched
                        files = wrappedVirtualDir.list(newRelativePath);
                    }
                    break;
                }
            }
            if (!found) {//if no subfolder/subfile did not matched the search, it makes no sense to continue searching
                break;
            }
        }
        if (index > 0) {//if the file was found (meaning the index is not 0), then the last path separator should be removed!
            newRelativePath = newRelativePath.substring(0, newRelativePath.length() - pathSeparator.length());
        }
        if (index == 0) {
            throw new IOException();
        }
        return wrappedVirtualDir.getFile(newRelativePath);
    }

    /**
     * Lists the entries of this virtual directory (only the first level).
     *
     * @param path The path to look into. Set it to "" to return all files from the first level.
     * @return A list of file names.
     * @throws IOException
     */
    public String[] list(String path) throws IOException {
        return wrappedVirtualDir.list(path);
    }

    public String[] listAll() {
        List<String> fileNames = new ArrayList<String>();
        if (wrappedVirtualDir != null) {
            if (isTarFile) {
                return ((TarVirtualDir) wrappedVirtualDir).listAll();
            }
            String path = wrappedVirtualDir.getBasePath();
            if (isZipFile) {
                try {
                    ZipFile zipFile = new ZipFile(path);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        fileNames.add(entries.nextElement().getName().toLowerCase());
                    }
                    zipFile.close();
                } catch (IOException e) {
                    // cannot open zip, list will be empty
                }
            } else {
                listFiles(new File(path), fileNames);
            }
        }
        return fileNames.toArray(new String[fileNames.size()]);
    }

    /**
     * Tries to findFirst the first file that contains the given string.
     *
     * @param pattern A string to be found in the file name (if any).
     * @return The name of the found file, or <code>NULL</code> if no file was found.
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
     *
     * @param pattern A string to be found in the file name (if any).
     * @return The name of the found file, or <code>NULL</code> if no file was found.
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

    static boolean isTar(String filename) {
        final String extension = FileUtils.getExtension(filename);
        return (".tgz".equals(extension) || ".TGZ".equals(extension));
    }

    public boolean isThisZipFile() {
        return this.isZipFile;
    }

    public boolean isThisTarFile() {
        return this.isTarFile;
    }

    /**
     * Indicates if file name capitalisation should be corrected for this dir (i.e. if the file names are
     * in uppercase, they will be first converted to lowercase before any other operation).
     *
     * @return <code>true</code> if upper-to-lowercase should be performed, <code>false</code> otherwise.
     * @throws IOException This exception is not actually thrown in the base class, but it can be thrown
     *                     in derived classes.
     */
    protected boolean correctCapitalisation() throws IOException {
        return (shouldConvertCase = false);
    }

    private void listFiles(File parent, List<String> outList) {
        if (parent.isFile())
            return;
        File[] files = parent.listFiles();
        for (File file : files) {
            if (file.isFile())
                outList.add(new File(wrappedVirtualDir.getBasePath()).toURI().relativize(file.toURI()).getPath().toLowerCase());
            else {
                listFiles(file, outList);
            }
        }
    }

    public String getBasePath() {
        return wrappedVirtualDir.getBasePath();
    }

    public File getTempDir() throws IOException {
        return wrappedVirtualDir.getTempDir();
    }

    public static boolean isCompressedFile(File file) {
        String extension = FileUtils.getExtension(file);
        if (StringUtils.isNullOrEmpty(extension)) {
            return false;
        }

        extension = extension.toLowerCase();

        return extension.contains("zip")
                || extension.contains("tar")
                || extension.contains("tgz")
                || extension.contains("gz")
                || extension.contains("rar")
                || extension.contains("arj")
                || extension.contains("arc")
                || extension.contains("as")
                || extension.contains("b64")
                || extension.contains("btoa")
                || extension.contains("bz")
                || extension.contains("cab")
                || extension.contains("cpt")
                || extension.contains("hqx")
                || extension.contains("iso")
                || extension.contains("lha")
                || extension.contains("lzh")
                || extension.contains("mim")
                || extension.contains("mme")
                || extension.contains("pak")
                || extension.contains("pf")
                || extension.contains("sea")
                || extension.contains("sit")
                || extension.contains("sitx")
                || extension.contains("tbz")
                || extension.contains("tbz2")
                || extension.contains("uu")
                || extension.contains("uue")
                || extension.contains("z")
                || extension.contains("zoo");
    }
}
