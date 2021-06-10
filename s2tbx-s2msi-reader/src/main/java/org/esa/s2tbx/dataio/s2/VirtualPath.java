package org.esa.s2tbx.dataio.s2;

import org.esa.s2tbx.commons.FilePath;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.jp2.reader.JP2LocalFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by obarrile on 01/08/2016.
 */

/**
 * This class represents a Path to any resource, but it is useful to represent a relativePath to a resource inside a compressed
 * file. It consists of the VirtualDir representing the folder or compressed file and a relative relativePath starting there.
 * If the VirtualDir is null, the relativePath is an absolute relativePath.
 */

public class VirtualPath implements JP2LocalFile {

    private final Path relativePath; // relative relativePath starting from dir
    private final VirtualDirEx dir;

    public VirtualPath(String relativePath, VirtualDirEx dir) {
        this.dir = dir;
        String path = replaceFileSeparator(relativePath, this.dir.getFileSystemSeparator());
        this.relativePath = this.dir.buildPath(path);
    }

    public VirtualDirEx getVirtualDir() {
        return dir;
    }

    public String getSeparator() {
        return this.relativePath.getFileSystem().getSeparator();
    }

    public Path getFileName() {
        if (isCurrentDirectory()) {
            return this.dir.getBaseFile().toPath().getFileName();
        }
        return this.relativePath.getFileName();
    }

    public VirtualPath getParent() {
        Path normalizedPath = this.relativePath.normalize();
        Path parentPath = normalizedPath.getParent();
        if (parentPath == null) {
            // the relative path has no parent
            if (normalizedPath.toString().equals(".") || normalizedPath.toString().isEmpty()) {
                return null;
            }
            Path path = this.relativePath.resolve(".").getFileName();
            return new VirtualPath(path.toString(), this.dir);
        }
        return new VirtualPath(parentPath.toString(), this.dir);
    }

    public VirtualPath resolve(String other) {
        String path = replaceFileSeparator(other, this.dir.getFileSystemSeparator());
        if (isCurrentDirectory()) {
            return new VirtualPath(this.relativePath.resolveSibling(path).toString(), this.dir);
        }
        return new VirtualPath(this.relativePath.resolve(path).toString(), this.dir);
    }

    public VirtualPath resolveSibling(String other) {
        String path = replaceFileSeparator(other, this.dir.getFileSystemSeparator());
        if (isCurrentDirectory()) {
            return new VirtualPath(this.relativePath.normalize().resolveSibling(path).toString(), this.dir);
        }
        return new VirtualPath(this.relativePath.resolveSibling(path).toString(), this.dir);
    }

    public boolean existsAndHasChildren() {
        if (exists()) {
            try {
                String[] children = list();
                if (children != null && children.length > 0) {
                    return true;
                }
            } catch (IOException e) {
                // ignore exception
            }
        }
        return false;
    }

    public InputStream getInputStream() throws IOException {
        if (isCurrentDirectory()) {
            throw new IllegalStateException("Unable to get the input stream for path '"+this.relativePath.toString()+"'.");
        }
        return this.dir.getInputStream(this.relativePath.toString());
    }

    public FilePath getFilePath() throws IOException {
        return this.dir.getFilePath(this.relativePath.toString());
    }

    @Override
    public Path getLocalFile() throws IOException {
        if (isCurrentDirectory()) {
            throw new IllegalStateException("Unable to get the file for path '"+this.relativePath.toString()+"'.");
        }
        File file = this.dir.getFile(this.relativePath.toString());
        Path filePath = file.toPath();
        if (filePath.getFileSystem() == FileSystems.getDefault()) {
            return filePath;
        } else {
            throw new IllegalStateException("The file '"+filePath.toString()+"' is not a local file.");
        }
    }

    public String[] list() throws IOException {
        String childRelativePath = isCurrentDirectory() ? null : this.relativePath.toString();
        return this.dir.list(childRelativePath);
    }

    public String[] listEndingBy(String suffix) throws IOException {
        List<String> found = null;
        String[] entries = list();
        if (entries != null) {
            found = Arrays.stream(entries).filter(e -> e.toLowerCase().endsWith(suffix)).collect(Collectors.toList());
        }
        return found != null ? found.toArray(new String[found.size()]) : null;
    }

    public VirtualPath[] listPaths() throws IOException {
        String childRelativePath = isCurrentDirectory() ? null : this.relativePath.toString();
        if (this.dir.exists(childRelativePath)) {
            String[] list = this.dir.list(childRelativePath);
            if (list != null && list.length > 0) {
                VirtualPath[] result = new VirtualPath[list.length];
                for (int i = 0; i < list.length; i++) {
                    result[i] = resolve(list[i]);
                }
                return result;
            }
        }
        return null;
    }

    public VirtualPath[] listPaths(String pattern) throws IOException {
        String childRelativePath = isCurrentDirectory() ? null : this.relativePath.toString();
        String[] list = this.dir.list(childRelativePath);
        if (list != null && list.length > 0) {
            List<VirtualPath> listPaths = new ArrayList<>(list.length);
            for (int i=0; i<list.length; i++) {
                if (list[i].contains(pattern)) {
                    listPaths.add(resolve(list[i]));
                }
            }
            if (listPaths.size() > 0) {
                return listPaths.toArray(new VirtualPath[listPaths.size()]);
            }
        }
        return null;
    }

    public VirtualPath[] listPaths(String pattern, String[] suffixList) throws IOException {
        String childRelativePath = isCurrentDirectory() ? null : this.relativePath.toString();
        String[] list = this.dir.list(childRelativePath);
        if (list != null && list.length > 0) {
            List<VirtualPath> listPaths = new ArrayList<>(list.length);
            for (int i=0; i<list.length; i++) {
                for (String suffix:suffixList){
                    if (list[i].contains(pattern) && list[i].toLowerCase().endsWith(suffix)) {
                        listPaths.add(resolve(list[i]));
                    }
                }
            }
            if (listPaths.size() > 0) {
                return listPaths.toArray(new VirtualPath[listPaths.size()]);
            }
        }
        return null;
    }

    public boolean exists() {
        String childRelativePath = isCurrentDirectory() ? null : this.relativePath.toString();
        return this.dir.exists(childRelativePath);
    }

    public String getFullPathString() {
//        Path dirPath = this.dir.getBaseFile().toPath();
//        if (isCurrentDirectory()) {
//            return dirPath.toString();
//        } else {
//            Path path = dirPath.resolve(this.relativePath.toString());
//            return path.toString();
//        }

        //TODO Jean old code
        Path dirPath = this.dir.getBaseFile().toPath();
        if (isCurrentDirectory()) {
            String result = dirPath.toString();
            if (this.dir.isArchive()) {
                result += "!" + this.relativePath.toString();
            }
            return result;
        } else {
            Path path = dirPath.resolve(this.relativePath.toString());
            return path.toString();
        }
    }
    
    public void close() {
        this.dir.close();
    }

    private boolean isCurrentDirectory() {
        return this.relativePath.toString().equals(".");
    }

    private static String replaceFileSeparator(String path, String fileSystemSeparator) {
        return path.replace("\\", fileSystemSeparator).replace("/", fileSystemSeparator);
    }
}
