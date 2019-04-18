/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio;

import com.bc.ceres.core.VirtualDir;
import org.apache.commons.io.IOUtils;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.io.FileUtils;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarHeader;
import org.xeustechnologies.jtar.TarInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.esa.snap.utils.CollectionHelper.firstOrDefault;

/**
 * This class extends or alters the features of com.bc.ceres.core.VirtualDir class with a Tar/Tgz implementation
 * and proper methods of retrieving the contents of the virtual directory.
 *
 * @author Cosmin Cara
 */
public abstract class VirtualDirEx extends VirtualDir {

    private static final Logger logger = Logger.getLogger(VirtualDirEx.class.getName());

    private final static HashSet<String> COMPRESSED_EXTENSIONS = new HashSet<String>() {{
        add(".zip");
        add(".tgz");
        add(".gz");
        add(".z");
        add(".tar");
        add(".bz");
        add(".lzh");
        add(".tbz");
    }};

    private int depth;

    protected VirtualDirEx() {
        super();

        this.depth = 1;
    }

    /**
     * Factory method to create an instance of either a VirtualDir object (File, Dir)
     * or of a VirtualDirEx object (VirtualDirWrapper for zip files, TarVirtualDir).
     * @param file  The file object to be wrapped.
     * @return  See the description
     */
    @Deprecated
    public static VirtualDirEx create(File file) {
        return create(file.toPath());
    }

    private static VirtualDirEx create(Path path) {
        String fileName = path.getFileName().toString();
        if (Files.isRegularFile(path) && (TarVirtualDir.isTgz(fileName) || TarVirtualDir.isTar(fileName))) {
            return new TarVirtualDir(path.toFile());
        } else {
            AbstractVirtualPath virtualDir = null;
            if (Files.isDirectory(path)) {
                virtualDir = new VirtualDirPath(path, false);
            } else {
                try {
                    if (isZipFile(path)) {
                        virtualDir = new VirtualZipPath(path, true);
                    }
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException | IOException e) {
                    throw new IllegalStateException(e);
                }
            }
            return (virtualDir == null) ? null : new VirtualDirWrapper(virtualDir);
        }
    }

    public static VirtualDirEx build(Path path) throws IOException {
        return build(path, false, true);
    }

    public static VirtualDirEx build(Path path, boolean copyFilesFromDirectoryOnLocalDisk, boolean copyFilesFromArchiveOnLocalDisk) throws IOException {
        AbstractVirtualPath virtualDir = null;
        if (Files.isRegularFile(path)) {
            // the path represents a file
            if (VirtualDirEx.isPackedFile(path)) {
                // the path represents an archive
                String fileName = path.getFileName().toString();
                if (TarVirtualDir.isTgz(fileName) || TarVirtualDir.isTar(fileName)) {
                    return new TarVirtualDir(path.toFile());
                } else {
                    // check if the file represents a zip archive
                    boolean zipFile;
                    try {
                        zipFile = isZipFile(path);
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    }
                    if (zipFile) {
                        virtualDir = new VirtualZipPath(path, copyFilesFromArchiveOnLocalDisk);
                    } else {
                        throw new IllegalArgumentException("The path '"+path.toString()+"' does not represent a zip archive.");
                    }
                }
            } else {
                Path parentPath = path.getParent();
                if (parentPath == null) {
                    throw new IllegalArgumentException("Unable to retrieve the parent of the file '" + path.toString()+"'.");
                } else if (Files.isDirectory(parentPath)){
                    virtualDir = new VirtualDirPath(parentPath, copyFilesFromDirectoryOnLocalDisk);
                } else {
                    throw new IllegalArgumentException("Unable to check if the parent of the file '" + path.toString()+"' represents a directory.");
                }
            }
        } else if (Files.isDirectory(path)) {
            // the path represents a directory
            virtualDir = new VirtualDirPath(path, copyFilesFromDirectoryOnLocalDisk);
        } else {
            throw new IllegalArgumentException("Unable to check if the path '"+path.toString()+"' represents a file or a directory.");
        }
        return (virtualDir == null) ? null : new VirtualDirWrapper(virtualDir);
    }

    private static boolean isZipFile(Path zipPath) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(zipPath)) {
            return (fileSystem != null);
        }
    }

    /**
     * Helper method to check if a file is either packed (i.e. tar file) or compressed.
     * The test is performed agains a set of pre-defined file extensions.
     * @param file  The file to be tested
     * @return  <code>true</code> if the file is packed or compressed, <code>false</code> otherwise
     */
    @Deprecated
    public static boolean isPackedFile(File file) {
        return isPackedFile(file.toPath());
    }

    public static boolean isPackedFile(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int pointIndex = fileName.lastIndexOf(".");
        if (pointIndex <= 0) {
            return false;
        }
        String extension = fileName.substring(pointIndex);
        return !StringUtils.isNullOrEmpty(extension) && COMPRESSED_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * Checks if the file name belongs to a tar file.
     * @param filename  The name of the file to be tested.
     * @return  <code>true</code> if the file is a tar file, <code>false</code> otherwise
     */
    public static boolean isTar(String filename) {
        return TarVirtualDir.isTar(filename);
    }

    public Path buildPath(String first, String... more) throws IOException {
        return null;
    }

    public void setFolderDepth(int value) {
        this.depth = value;
    }

    /**
     * Finds the first occurrence of the pattern in the list of files of this instance.
     * @param pattern   The pattern to be found.
     * @return  The first found entry matching the pattern, or <code>null</code> if none found.
     * @throws IOException
     */
    public String findFirst(String pattern) throws IOException {
        String found = null;
        String[] entries = list("");
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
     * Finds all the files that contain the given string.
     *
     * @param pattern A string to be found in the file name (if any).
     * @return The array of file names that matched the pattern, or <code>NULL</code> if no file was found.
     * @throws IOException
     */
    public String[] findAll(String pattern) throws IOException {
        List<String> found = null;
        String[] entries = listAll();
        if (entries != null) {
            found = Arrays.stream(entries).filter(e -> e.toLowerCase().contains(pattern)).collect(Collectors.toList());
        }
        return found != null ? found.toArray(new String[found.size()]) : null;
    }

    @Override
    public String[] listAllFiles() throws IOException {
        return listAll();
    }

    /**
     * List all the files contained in this virtual directory instance.
     * @return  An array of file names
     */
    public String[] listAll(Pattern...patterns) {
        String path = getBasePath();
        if (TarVirtualDir.isTar(path) || TarVirtualDir.isTgz(path)) {
            return listAll();
        } else {
            List<String> fileNames = new ArrayList<>();
            if (isArchive()) {
                try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(getBaseFile().toPath())) {
                    Iterator<Path> it = fileSystem.getRootDirectories().iterator();
                    while (it.hasNext()) {
                        Path root = it.next();
                        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                String zipEntryPath = file.toString();
                                if (zipEntryPath.startsWith("/")) {
                                    zipEntryPath = zipEntryPath.substring(1);
                                }
                                if (isTar(zipEntryPath)) {
                                    File file1 = getFile(zipEntryPath);
                                    TarVirtualDir innerTar = new TarVirtualDir(file1) {
                                        @Override
                                        public void close() {
                                        }
                                    };
                                    innerTar.ensureUnpacked(getTempDir());
                                    fileNames.addAll(Arrays.asList(innerTar.listAll()));
                                    file1.delete();
                                } else {
                                    fileNames.add(zipEntryPath);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // cannot open zip, list will be empty
                    Logger.getLogger(VirtualDirEx.class.getName()).severe(e.getMessage());
                }
            } else {
                fileNames.addAll(listFiles(getBaseFile(), patterns));
            }
            return fileNames.toArray(new String[fileNames.size()]);
        }
    }

    private List<String> listFiles(File parent, Pattern...filters) {
        List<String> files = new ArrayList<>();
        Path parentPath = parent.toPath();
        try {
            FileVisitor<? super Path> visitor = new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (parentPath.equals(dir) || (filters.length == 0)) {
                        return FileVisitResult.CONTINUE;
                    } else {
                        String relativePath = parentPath.relativize(dir).toString();
                        if (Arrays.stream(filters).anyMatch(p -> p.matcher(relativePath).matches())) {
                            files.add(relativePath);
                            return FileVisitResult.CONTINUE;
                        }
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String relativePath = parentPath.relativize(file).toString();
                    if (filters.length == 0 || Arrays.stream(filters).anyMatch(p -> p.matcher(relativePath).matches())) {
                        files.add(relativePath);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            };
            Files.walkFileTree(parentPath, EnumSet.noneOf(FileVisitOption.class), this.depth, visitor);
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        return files;
    }
}
