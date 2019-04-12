package org.esa.s2tbx.dataio;

//import com.bc.ceres.core.VirtualDir;
import com.bc.ceres.core.VirtualDir;
import org.esa.s2tbx.dataio.readers.PathUtils;
import org.esa.snap.core.util.SystemUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

public class VirtualPath implements Path {

    private final Path path; //relative path starting from dir
    private final VirtualDirEx dir; //if this is null, the path is an absolute path
    private final String separator; //separator used in the dir file system

    public VirtualPath(Path path, VirtualDirEx dir) {
        if (path.getFileSystem() == FileSystems.getDefault()) {
            FileSystem fsys = FileSystems.getDefault();
            if (dir != null && dir.isCompressed()) {
                try {
                    fsys = FileSystems.newFileSystem(Paths.get(dir.getBasePath()),null);
                } catch (Exception e) {
                    fsys = null;
                }
            }

            if (fsys == null) {
                if (path != null && !path.toString().equals("")) {
                    this.path = path;
                } else {
                    this.path = Paths.get(".");
                }
                this.separator = File.separator;
            } else {
                if (path != null && !path.toString().equals("")) {
                    this.path = fsys.getPath(Paths.get(path.toString()).toString());
                } else {
                    this.path = Paths.get(".");
                }
                this.separator = fsys.getSeparator();
            }
            this.dir = dir;
        } else {
            this.dir = dir;
            this.path = path;
            this.separator = this.path.getFileSystem().getSeparator();
        }

    }

    public VirtualPath(String stringPath, VirtualDirEx dir) {
        this(Paths.get(stringPath), dir);
    }

    public VirtualPath(Path path) {
        this(path, null);
    }

    public VirtualPath(String stringPath) {
        this(stringPath, null);
    }

    public VirtualDirEx getVirtualDir() {
        return dir;
    }


    @Override
    public int compareTo(Path other) {
        if(!(other instanceof VirtualPath)) {
            return -1;
        }
        VirtualPath virtualOther = (VirtualPath) other;
        if(virtualOther.getVirtualDir()!= null && this.dir != null) {
            if (virtualOther.getVirtualDir().getBasePath().compareTo(dir.getBasePath()) != 0) {
                return -1;
            }
            return path.compareTo(other);
        } else if(virtualOther.getVirtualDir()== null && this.dir == null) {
            return path.compareTo(other);
        }

        return -1;
    }

    @Override
    public FileSystem getFileSystem() {
        SystemUtils.LOG.warning("Method getFileSystem not implemented");
        return null;
    }

    @Override
    public boolean isAbsolute() {
        if(this.dir == null) {
            return true;
        }
        return false;
    }

    @Override
    public Path getRoot() {
        SystemUtils.LOG.warning("Method getRoot not implemented");
        return null;
    }

    @Override
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

    @Override
    public VirtualPath getParent() {
        Path normalizedPath = path.normalize();
        if(normalizedPath.getParent() != null) {
            return new VirtualPath(normalizedPath.getParent(), this.dir);
        }

        if(normalizedPath.getNameCount() == 1 && !normalizedPath.toString().equals("")) {
            return new VirtualPath(".", this.dir);
        }

        if(normalizedPath.getNameCount() == 0 || normalizedPath.toString().equals("")) {
            if(VirtualDirEx.isPackedFile(new File(dir.getBasePath())) || dir.isCompressed()) {
                return null;
            } else {
                //It is possible in this case to change the VirtualDir
                Path dirPath = Paths.get(this.dir.getBasePath());
                VirtualPath parent = new VirtualPath(dirPath.getFileName(),VirtualDirEx.create(dirPath.getParent().toFile()));
                return parent;
            }
        }
        return null;
    }

    @Override
    public int getNameCount() {
        SystemUtils.LOG.warning("Method getNameCount not implemented");
        return 0;
    }

    @Override
    public Path getName(int index) {
        SystemUtils.LOG.warning("Method getName not implemented");
        return null;
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        SystemUtils.LOG.warning("Method subpath not implemented");
        return null;
    }

    @Override
    public boolean startsWith(Path other) {
        return path.startsWith(other);
    }

    @Override
    public boolean startsWith(String other) {
        return path.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
        return path.endsWith(other);
    }

    @Override
    public boolean endsWith(String other) {
        return path.endsWith(other);
    }

    @Override
    public VirtualPath normalize() {
        return new VirtualPath(path.normalize(),this.dir);
    }

    @Override
    public VirtualPath resolve(Path other) {
        if(path.getFileName().toString().equals(".")) {
            return new VirtualPath(path.resolveSibling(other),this.dir);
        }
        return new VirtualPath(path.resolve(other),this.dir);
    }

    @Override
    public VirtualPath resolve(String other) {
        if(path.getFileName().toString().equals(".")) {
            return new VirtualPath(path.resolveSibling(other),this.dir);
        }
        return new VirtualPath(path.resolve(other),this.dir);
    }

    @Override
    public VirtualPath resolveSibling(Path other) {
        if(path.getFileName().toString().equals(".")) {
            return new VirtualPath(path.normalize().resolveSibling(other),this.dir);
        }
        return new VirtualPath(path.resolveSibling(other),this.dir);
    }

    @Override
    public VirtualPath resolveSibling(String other) {
        if(path.getFileName().toString().equals(".")) {
            return new VirtualPath(path.normalize().resolveSibling(other),this.dir);
        }
        return new VirtualPath(path.resolveSibling(other),this.dir);
    }

    @Override
    public Path relativize(Path other) {
        SystemUtils.LOG.warning("Method relativize not implemented");
        return null;
    }

    @Override
    public URI toUri() {
        SystemUtils.LOG.warning("Method toUri not implemented");
        return null;
    }

    @Override
    public Path toAbsolutePath() {
        if(dir != null) {
            return Paths.get(dir.getBasePath(), path.toString());
        }
        else {
            return Paths.get(path.toString());
        }
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        SystemUtils.LOG.warning("Method toRealPath not implemented");
        return null;
    }

    @Override
    public File toFile() {
        try {
            return this.getFile();
        } catch (IOException e) {
            SystemUtils.LOG.warning("Not possible to get file from dir");
            return null;
        }
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        SystemUtils.LOG.warning("Method register not implemented");
        return null;
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        SystemUtils.LOG.warning("Method register not implemented");
        return null;
    }

    @Override
    public Iterator<Path> iterator() {
        return path.iterator();
    }


    public boolean isDirectory() {
        if(!this.exists()) {
            return false;
        }
        try {
            if(this.list()!= null && this.list().length>0) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }


    // VirtualDir methods

    public Reader getReader() throws IOException {
        if(dir == null) {
            return new InputStreamReader(getInputStream());
        }
        return dir.getReader(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public InputStream getInputStream() throws IOException {
        if(dir == null) {
            return new BufferedInputStream(new FileInputStream(path.toFile()));
        }
        return dir.getInputStream(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public File getFile() throws IOException {
        if(dir == null) {
            return path.toFile();
        }
        return dir.getFile(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public String[] list() throws IOException {
        if(dir == null) {
            if(!Files.isDirectory(path)) {
                return null;
            }
            return path.toFile().list();
        }
        return dir.list(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public String[] list(String subFolder) throws IOException {
        if(dir == null) {
            Path subFolderPath = path.resolve(subFolder);
            if(subFolder == null || !Files.isDirectory(subFolderPath)) {
                return null;
            }
            return subFolderPath.toFile().list();
        }
        return dir.list(Paths.get(path.toString(),subFolder).toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public String[] listFilter(String pattern) throws IOException {
        List<String> found = null; // = new ArrayList<>();
        String[] entries = list();
        if (entries != null) {
            found = Arrays.stream(entries).filter(e -> e.toLowerCase().contains(pattern)).collect(Collectors.toList());
        }
        return found != null ? found.toArray(new String[found.size()]) : null;
    }

    public String[] listEndingBy(String suffix) throws IOException {
        List<String> found = null; // = new ArrayList<>();
        String[] entries = list();
        if (entries != null) {
            found = Arrays.stream(entries).filter(e -> e.toLowerCase().endsWith(suffix)).collect(Collectors.toList());
        }
        return found != null ? found.toArray(new String[found.size()]) : null;
    }

    public VirtualPath[] listPaths() throws IOException {
        String[] list;
        if (dir == null) {
            if(!Files.exists(path) || !Files.isDirectory(path)) {
                return null;
            }
            list = path.toFile().list();
        } else {
            list = dir.list(path.toString().replace(FileSystems.getDefault().getSeparator(), separator));
        }
        if(list == null) {
            return null;
        }

        ArrayList<VirtualPath> listPaths = new ArrayList<>();
        for(String item : list) {
            listPaths.add(this.resolve(item));
        }


        return listPaths.toArray(new VirtualPath[list.length]);
    }

    public VirtualPath[] listPaths(String pattern) throws IOException {
        String[] list;
        if (dir == null) {
            if(!Files.exists(path) || !Files.isDirectory(path)) {
                return null;
            }
            list = path.toFile().list();
        } else {
            list = dir.list(path.toString().replace(FileSystems.getDefault().getSeparator(), separator));
        }
        if(list == null) {
            return null;
        }

        ArrayList<VirtualPath> listPaths = new ArrayList<>();
        for(String item : list) {
            if(item.contains(pattern)) {
                listPaths.add(this.resolve(item));
            }
        }
        if(listPaths.size() == 0) {
            return null;
        }

        return listPaths.toArray(new VirtualPath[listPaths.size()]);
    }

    public boolean exists() {
        if(dir == null) {
            return Files.exists(path);
        }
        if(path == null || path.getNameCount() == 0 || path.getNameCount() == 1 && path.toString().equals(".")) {
            return Files.exists(Paths.get(dir.getBasePath().toString()));
        }
        return dir.exists(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public String getFullPathString() {
        if(dir != null) {
            return (dir.getBasePath() + File.separator + path.toString());
        } else {
            return path.toString();
        }
    }
    
    public void close() {
        if(dir != null) {
            dir.close();
        }
    }
}
