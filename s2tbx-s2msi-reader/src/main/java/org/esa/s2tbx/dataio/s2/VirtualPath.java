package org.esa.s2tbx.dataio.s2;

import org.esa.s2tbx.dataio.VirtualDirEx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

public class VirtualPath {

    private final Path relativePath; // relative relativePath starting from dir
    private final VirtualDirEx dir;

    public VirtualPath(Path relativePath, VirtualDirEx dir) {
        this.dir = dir;
        try {
            this.relativePath = this.dir.buildPath(relativePath.toString());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public VirtualDirEx getVirtualDir() {
        return dir;
    }

    public String getSeparator() {
        return this.relativePath.getFileSystem().getSeparator();
    }

    public Path getFileName() {
        if (this.relativePath.toString().equals(".")) {
            return this.dir.getBaseFile().toPath().getFileName();
        }
        return this.relativePath.getFileName();
    }

    public VirtualPath getParent() {
        Path normalizedPath = this.relativePath.normalize();
        Path parentPath = normalizedPath.getParent();
        if (parentPath == null) {
            // the relative path has sno parent
            if (normalizedPath.toString().equals(".")) {
                return null;
            }
            Path path = this.relativePath.resolve(".").getFileName();
            return new VirtualPath(path, this.dir);
        }
        return new VirtualPath(parentPath, this.dir);
    }

    public VirtualPath resolve(String other) {
        if (this.relativePath.getFileName().toString().equals(".")) {
            return new VirtualPath(this.relativePath.resolveSibling(other), this.dir);
        }
        return new VirtualPath(this.relativePath.resolve(other), this.dir);
    }

    public VirtualPath resolveSibling(String other) {
        if (this.relativePath.getFileName().toString().equals(".")) {
            return new VirtualPath(this.relativePath.normalize().resolveSibling(other), this.dir);
        }
        return new VirtualPath(this.relativePath.resolveSibling(other), this.dir);
    }

    public boolean isDirectory() {
        if (exists()) {
            try {
                String[] children = list();
                if (children != null && children.length > 0) {
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public InputStream getInputStream() throws IOException {
        return this.dir.getInputStream(this.relativePath.toString());
    }

    public Path getFile() throws IOException {
        File file = this.dir.getFile(this.relativePath.toString());
        return file.toPath();
    }

    public String[] list() throws IOException {
        return this.dir.list(this.relativePath.toString());
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
        String[] list = dir.list(relativePath.toString());
        if (list != null && list.length > 0) {
            VirtualPath[] result = new VirtualPath[list.length];
            for (int i=0; i<list.length; i++) {
                result[i]  = resolve(list[i]);
            }
            return result;
        }
        return null;
    }

    public VirtualPath[] listPaths(String pattern) throws IOException {
        String[] list = dir.list(relativePath.toString());
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

    public boolean exists() {
        return this.dir.exists(this.relativePath.toString());
    }

    public String getFullPathString() {
        Path dirPath = this.dir.getBaseFile().toPath();
        if (this.relativePath.toString().equals(".")) {
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
}
