package org.esa.s2tbx.dataio;

import com.bc.ceres.core.VirtualDir;
import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Iterator;

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
    private final VirtualDir dir; //if this is null, the path is an absolute path
    private final String separator; //separator used in the dir file system

    public VirtualPath(Path path, VirtualDir dir) {
        FileSystem fsys = FileSystems.getDefault();
        if(dir != null && dir.isCompressed()) {
            try {
                fsys = FileSystems.newFileSystem(Paths.get(dir.getBasePath()),null);
            } catch (IOException e) {
                fsys = null;
            }
        }

        if(fsys == null) {
            if (path != null) {
                this.path = path;
            } else {
                this.path = Paths.get("");
            }
            separator = File.separator;
        } else {
            if (path != null) {
                this.path = fsys.getPath(Paths.get(path.toString()).toString());
            } else {
                this.path = Paths.get("");
            }
            separator = fsys.getSeparator();
        }
        this.dir = dir;

    }

    public VirtualPath(String stringPath, VirtualDir dir) {
        this(Paths.get(stringPath),dir);
    }

    public VirtualDir getVirtualDir() {
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
        return path.getFileName();
    }

    @Override
    public VirtualPath getParent() {
        if(path.getFileName() == null || path.getFileName().toString().equals("") || path.getParent() == null)
        {
            //if parent is null get parent the virtual dir
            Path dirPath = Paths.get(this.dir.getBasePath());
            VirtualPath parent = new VirtualPath(dirPath.getFileName(),VirtualDir.create(dirPath.getParent().toFile()));
            return parent;
            //TODO update separator
        }

        VirtualPath parent = new VirtualPath(path.getParent(), this.dir);
        return parent;
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
        return new VirtualPath(path.resolve(other),this.dir);
    }

    @Override
    public VirtualPath resolve(String other) {
        return new VirtualPath(path.resolve(other),this.dir);
    }

    @Override
    public VirtualPath resolveSibling(Path other) {
        return new VirtualPath(path.resolveSibling(other),this.dir);
    }

    @Override
    public VirtualPath resolveSibling(String other) {
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
    public VirtualPath toAbsolutePath() {
        return new VirtualPath(Paths.get(dir.getBasePath(),path.toString()),null);
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
        return dir.getReader(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public InputStream getInputStream() throws IOException {
        return dir.getInputStream(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public File getFile() throws IOException {
        return dir.getFile(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public String[] list() throws IOException {
        return dir.list(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public String[] list(String subFolder) throws IOException {
        return dir.list(Paths.get(path.toString(),subFolder).toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public VirtualPath[] listPaths() throws IOException {
        String[] list = dir.list(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));

        ArrayList<VirtualPath> listPaths = new ArrayList<>();
        for(String item : list) {
            listPaths.add(this.resolve(item));
        }


        return listPaths.toArray(new VirtualPath[list.length]);
    }

    public boolean exists() {
        return dir.exists(path.toString().replace(FileSystems.getDefault().getSeparator(),separator));
    }

    public String getFullPathString() {
        return (dir.getBasePath() + File.separator + path.toString());
    }
}
