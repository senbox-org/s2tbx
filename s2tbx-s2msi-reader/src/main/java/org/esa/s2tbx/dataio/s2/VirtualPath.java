package org.esa.s2tbx.dataio.s2;

import com.bc.ceres.core.VirtualDir;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.util.SystemUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by obarrile on 01/08/2016.
 */

/**
 * This class represents a Path to any resource, but it is useful to represent a path to a resource inside a compressed
 * file. It consists of the VirtualDir representing the folder or compressed file and a relative path starting there.
 * If the VirtualDir is null, the path is an absolute path.
 */

public class VirtualPath {

    private final Path path; // relative path starting from dir
    private final VirtualDirEx dir; // if this is null, the path is an absolute path

    private File tempZipFileDir;

    public VirtualPath(Path path, VirtualDirEx dir) {
        this.dir = dir;
        if (this.dir == null) {
            this.path = path;
        } else {
            try (FileSystem fileSystem = this.dir.newFileSystem()) {
                this.path = fileSystem.getPath(path.toString());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public VirtualDirEx getVirtualDir() {
        return dir;
    }

    public Path getFileName() {
        Path name = path.getFileName();
        if(name != null && !name.toString().equals(".")) {
            return name;
        }
        if(name != null && name.equals(".")) {
            return this.getParent().getFileName();
        }

        return Paths.get(getVirtualDir().getBasePath()).getFileName();
    }

    public VirtualPath getParent() {
        Path normalizedPath = path.normalize();
        if (normalizedPath.getParent() != null) {
            return new VirtualPath(normalizedPath.getParent(), this.dir);
        }

        if (normalizedPath.getNameCount() == 1 && !normalizedPath.toString().equals("")) {
            Path p = path.resolve(".").getFileName();
            return new VirtualPath(p, this.dir);
        }
        return null;
    }

    public VirtualPath resolve(String other) {
        if (this.path.getFileName().toString().equals(".")) {
            return new VirtualPath(this.path.resolveSibling(other), this.dir);
        }
        return new VirtualPath(this.path.resolve(other), this.dir);
    }

    public VirtualPath resolveSibling(String other) {
        if (path.getFileName().toString().equals(".")) {
            return new VirtualPath(path.normalize().resolveSibling(other), this.dir);
        }
        return new VirtualPath(path.resolveSibling(other), this.dir);
    }

    public Path toAbsolutePath() {
        if (dir != null) {
            return Paths.get(dir.getBasePath(), path.toString());
        } else {
            return Paths.get(path.toString());
        }
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
        if (dir == null) {
            return new BufferedInputStream(Files.newInputStream(path));
        }
        return this.dir.getInputStream(this.path.toString());
    }

    public Path getRelativePath() {
        return this.path;
    }

    public File getFile() throws IOException {
        if (this.dir == null) {
            if (this.path.getFileSystem() == FileSystems.getDefault()) {
                return this.path.toFile();
            } else {
//                if (this.tempZipFileDir == null) {
//                    this.tempZipFileDir = VirtualDir.createUniqueTempDir();
//                }

//                if (jp2FilePath.getFileSystem() != FileSystems.getDefault()) {
//                    Path imageRelativePath = imageFilePath.getRelativePath();
//                    if (!imageRelativePath.startsWith(inputRelativeFolderPath)) {
//                        throw new IllegalStateException("The relative path '"+imageRelativePath.toString()+"' does not start with product input path '"+inputRelativeFolderPath.toString()+"'.");
//                    } else {
//                        int inputFolderNameCount = inputRelativeFolderPath.getNameCount();
//                        int imageNameCount = imageRelativePath.getNameCount();
//                        Path p = imageRelativePath.subpath(inputFolderNameCount, imageNameCount);
//                        jp2FilePath = this.cacheDir.toPath().resolve(p.toString());
//                        if (!Files.exists(jp2FilePath)) {
//                            Files.createDirectories(jp2FilePath.getParent());
//                            FileHelper.copyFileUsingInputStream(imageFilePath.getFile().toPath(), jp2FilePath.toString());
//                        }
//                    }
//                }

            }

            throw new UnsupportedOperationException("Not implemented yet.");
        }
        return this.dir.getFile(this.path.toString());
    }

    public String[] list() throws IOException {
        if (dir == null) {
            if (!Files.isDirectory(path)) {
                return null;
            }
            return path.toFile().list();
        }
        return dir.list(path.toString());
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
        String[] list;
        if (dir == null) {
            if (!Files.exists(path) || !Files.isDirectory(path)) {
                return null;
            }
            list = path.toFile().list();
        } else {
            list = dir.list(path.toString());
        }
        if (list == null) {
            return null;
        }

        ArrayList<VirtualPath> listPaths = new ArrayList<>();
        for (String item : list) {
            listPaths.add(this.resolve(item));
        }
        return listPaths.toArray(new VirtualPath[list.length]);
    }

    public VirtualPath[] listPaths(String pattern) throws IOException {
        String[] list;
        if (dir == null) {
            if (!Files.exists(path) || !Files.isDirectory(path)) {
                return null;
            }
            list = path.toFile().list();
        } else {
            list = dir.list(path.toString());
        }
        if (list == null) {
            return null;
        }

        ArrayList<VirtualPath> listPaths = new ArrayList<>();
        for (String item : list) {
            if (item.contains(pattern)) {
                listPaths.add(this.resolve(item));
            }
        }
        if (listPaths.size() == 0) {
            return null;
        }

        return listPaths.toArray(new VirtualPath[listPaths.size()]);
    }

    public boolean exists() {
        if (this.dir == null) {
            return Files.exists(this.path);
        }
        return this.dir.exists(this.path.toString());
    }

    public String getFullPathString() {
        if(dir != null) {
            return (dir.getBasePath() + File.separator + path.toString());
        } else {
            return path.toString();
        }
    }
    
    public void close() {
        if (dir != null) {
            dir.close();
        }
    }
}
