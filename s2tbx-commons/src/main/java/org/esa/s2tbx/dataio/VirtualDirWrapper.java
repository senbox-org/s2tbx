package org.esa.s2tbx.dataio;

import org.esa.snap.core.util.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.esa.snap.utils.CollectionHelper.firstOrDefault;

/**
 * Private implementation of a wrapper over the "classic" VirtualDir class.
 * It is needed because the implementations of VirtualDir are not visible, and hence their
 * methods cannot be overridden.
 * With the exception of the getFile() method, all the methods are delegated to the wrapped instance.
 *
 * Another difference from the File/Dir/Zip implementations of VirtualDir is the ability to get files
 * on partial matches of the names (see findKeyFile() method)
 *
 * @author Cosmin Cara
 */
class VirtualDirWrapper extends VirtualDirEx {

    private AbstractVirtualPath wrapped;
    private Map<String, String> files;

    public VirtualDirWrapper(AbstractVirtualPath dir) {
        this.wrapped = dir;
        this.files = new HashMap<>();
    }

    @Override
    public Path buildPath(String first, String... more) {
        return this.wrapped.buildPath(first, more);
    }

    @Override
    public String getFileSystemSeparator() {
        return this.wrapped.getFileSystemSeparator();
    }

    @Override
    public String getBasePath() {
        return this.wrapped.getBasePath();
    }

    @Override
    public File getBaseFile() {
        return this.wrapped.getBaseFile();
    }

    @Override
    public InputStream getInputStream(String relativePath) throws IOException {
        InputStream inputStream;
        try {
            inputStream = this.wrapped.getInputStream(relativePath);
        } catch (IOException e) {
            try {
                inputStream = this.wrapped.getInputStream(relativePath.toUpperCase());
            } catch (IOException ex) {
                inputStream = getInputStreamFromTempDir(relativePath);
            }
        }
        if (inputStream == null) {
            String key = FileUtils.getFileNameFromPath(relativePath).toLowerCase();
            String path = findKeyFile(key);
            if (path == null) {
                throw new IOException(String.format("File %s does not exist", relativePath));
            } else {
                try {
                    // the "classic" way
                    inputStream = getInputStreamInner(path);
                } catch (IOException e) {
                    if (isArchive()) {
                        inputStream = getInputStreamInner(path);
                    } else {
                        inputStream = getInputStreamFromTempDir(path);
                    }
                }
            }
        }
        return inputStream;
    }

    @Override
    public File getFile(String relativePath) throws IOException {
        File file;
        try {
            file = this.wrapped.getFile(relativePath);
        } catch (IOException e) {
            try {
                file = this.wrapped.getFile(relativePath.toUpperCase());
            } catch (IOException ex) {
                file = getFileFromTempDir(relativePath);
            }
        }
        if (file == null || !Files.exists(file.toPath())) {
            String key = FileUtils.getFileNameFromPath(relativePath).toLowerCase();
            String path = findKeyFile(key);
            if (path == null) {
                throw new IOException(String.format("File %s does not exist", relativePath));
            } else {
                try {
                    // the "classic" way
                    file = getFileInner(path);
                } catch (IOException e) {
                    if (isArchive()) {
                        file = getFileInner(path);
                    } else {
                        file = getFileFromTempDir(path);
                    }
                }
            }
        }
        return file;
    }

    private File getFileFromTempDir(String relativePath) throws IOException {
        File tempDir = this.wrapped.getTempDir();
        if (tempDir != null) {
            Path tempPath = tempDir.toPath().resolve(relativePath);
            return tempPath.toFile();
        }
        return null;
    }

    private InputStream getInputStreamFromTempDir(String relativePath) throws IOException {
        File tempDir = this.wrapped.getTempDir();
        if (tempDir != null) {
            Path tempPath = tempDir.toPath().resolve(relativePath);
            if (Files.exists(tempPath)) {
                return Files.newInputStream(tempPath);
            }
        }
        return null;
    }

    private File getFileInner(String path) throws IOException {
        String pathSeparator;
        if (!this.wrapped.isArchive() && !this.wrapped.getBasePath().toLowerCase().endsWith("tar")) {
            pathSeparator = "\\\\";
            path = path.replaceAll("/", "\\\\");
        } else {
            pathSeparator = "/";
        }
        try {
            //if the path letter case is correct, there is no need to read all the path tree
            File result = this.wrapped.getFile(path);
            if (result != null) {
                return result;
            }
        } catch (IOException ignored) {
            // do nothing
        }
        String newRelativePath = computeNewRelativePath(path, pathSeparator);
        return this.wrapped.getFile(newRelativePath);
    }

    private String computeNewRelativePath(String path, String pathSeparator) throws IOException {
        String[] relativePathArray = path.split(pathSeparator);
        String newRelativePath = "";
        String[] files = this.wrapped.list("");
        int index = 0;
        while (files != null && files.length > 0 && index < relativePathArray.length) {
            boolean found = false;
            for (String file : files) {
                if (relativePathArray[index].equalsIgnoreCase(file)) {
                    newRelativePath += file + pathSeparator;
                    index++;
                    found = true;
                    if (index < relativePathArray.length) {//there are still subfolders/subfiles to be searched
                        files = this.wrapped.list(newRelativePath);
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
        return newRelativePath;
    }

    private InputStream getInputStreamInner(String path) throws IOException {
        String pathSeparator;
        if (!this.wrapped.isArchive() && !this.wrapped.getBasePath().toLowerCase().endsWith("tar")) {
            pathSeparator = "\\\\";
            path = path.replaceAll("/", "\\\\");
        } else {
            pathSeparator = "/";
        }
        try {
            //if the path letter case is correct, there is no need to read all the path tree
            InputStream result = this.wrapped.getInputStream(path);
            if (result != null) {
                return result;
            }
        } catch (IOException ignored) {
            // do nothing
        }
        String newRelativePath = computeNewRelativePath(path, pathSeparator);
        return this.wrapped.getInputStream(newRelativePath);
    }

    @Override
    public String[] list(String s) throws IOException {
        return this.wrapped.list(s);
    }

    @Override
    public boolean exists(String s) {
        return this.wrapped.exists(s);
    }

    @Override
    public void close() {
        this.wrapped.close();
    }

    @Override
    public boolean isCompressed() {
        return this.wrapped.isCompressed();
    }

    @Override
    public boolean isArchive() {
        return this.wrapped.isArchive();
    }

    @Override
    public File getTempDir() throws IOException {
        return this.wrapped.getTempDir();
    }

    @Override
    protected void finalize() throws Throwable {
        this.wrapped = null;
        super.finalize();
    }

    @Override
    public String[] listAll(Pattern...patterns) {
        String[] list = super.listAll(patterns);
        Arrays.stream(list).forEach(item -> this.files.put(FileUtils.getFileNameFromPath(item).toLowerCase(), item));
        return list;
    }

    private String findKeyFile(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        String ret = this.files.get(key);
        if (ret == null) {
            String namePart = FileUtils.getFilenameWithoutExtension(FileUtils.getFileNameFromPath(key));
            String extPart = FileUtils.getExtension(key);
            ret = firstOrDefault(this.files.keySet(),
                    k -> {
                        String name = FileUtils.getFilenameWithoutExtension(FileUtils.getFileNameFromPath(k));
                        name = name.substring(name.lastIndexOf("/") + 1);
                        return (extPart != null && extPart.equalsIgnoreCase(FileUtils.getExtension(k))) && namePart.equals(name);
                    });
            //If no identical name found, look for a name that could be a truncated name of key (needed for some Deimos products)
            if(ret == null) {
                ret = firstOrDefault(this.files.keySet(),
                        k -> {
                            String name = FileUtils.getFilenameWithoutExtension(FileUtils.getFileNameFromPath(k));
                            name = name.substring(name.lastIndexOf("/") + 1);
                            return (extPart != null && extPart.equalsIgnoreCase(FileUtils.getExtension(k))) && namePart.startsWith(name);
                        });
            }
        }
        return ret;
    }
}
