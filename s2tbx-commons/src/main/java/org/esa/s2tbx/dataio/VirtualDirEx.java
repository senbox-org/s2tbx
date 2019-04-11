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
            try {
                return new TarVirtualDir(path.toFile());
            } catch (IOException ignored) {
                return null;
            }
        } else {
            AbstractVirtualPath virtualDir = null;
            if (Files.isDirectory(path)) {
                virtualDir = new VirtualDirPath(path);
            } else {
                try {
                    if (isZipFile(path)) {
                        virtualDir = new VirtualZipPath(path);
                    }
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to check if the file '"+path+"' is a zip archive.", e);
                }
            }
            return (virtualDir == null) ? null : new VirtualDirWrapper(virtualDir);
        }
    }

    public static VirtualDirEx build(Path path) throws IOException {
        if (Files.isRegularFile(path) && !VirtualDirEx.isPackedFile(path)) {
            Path parentPath = path.getParent();
            if (parentPath == null) {
                throw new IOException("Unable to retrieve parent to file '" + path.toString()+"'.");
            } else {
                return VirtualDirEx.create(parentPath);
            }
        }
        return VirtualDirEx.create(path);
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

    public abstract <ResultType> ResultType loadData(String path, ICallbackCommand<ResultType> command) throws IOException;

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
    private static class VirtualDirWrapper extends VirtualDirEx {

        private AbstractVirtualPath wrapped;
        private Map<String, String> files;

        public VirtualDirWrapper(AbstractVirtualPath dir) {
            this.wrapped = dir;
            this.files = new HashMap<>();
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
        public <ResultType> ResultType loadData(String relativePath, ICallbackCommand<ResultType> command) throws IOException {
            try {
                return this.wrapped.loadData(relativePath, command);
            } catch (FileNotFoundException e1) {
                try {
                    return this.wrapped.loadData(relativePath.toUpperCase(), command);
                } catch (FileNotFoundException e2) {
                    String key = FileUtils.getFileNameFromPath(relativePath).toLowerCase();
                    String path = findKeyFile(key);
                    if (path == null) {
                        throw new FileNotFoundException(String.format("File %s does not exist", relativePath));
                    } else {
                        return this.wrapped.loadData(path, command);
//                        try {
//                            // the "classic" way
//                            file = getFileInner(path);
//                        } catch (IOException e) {
//                            if (isArchive()) {
//                                file = getFileInner(path);
//                            } else {
//                                file = getFileFromTempDir(path);
//                            }
//                        }
                    }
                }
            }
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

    /**
     * Private implementation of a virtual directory representing the contents of a tar file.
     */
    private static class TarVirtualDir extends VirtualDirEx {

        public static final byte LF_SPEC_LINK = (byte) 'L';

        private final File archiveFile;
        private File extractDir;
        private FutureTask<Void> unpackTask;
        private ExecutorService executor;
        private boolean unpackStarted = false;

        private class UnpackProcess implements Callable<Void> {
            @Override
            public Void call() throws Exception {
                ensureUnpacked();
                return null;
            }
        }

        public TarVirtualDir(File tgz) throws IOException {
            if (tgz == null) {
                throw new IllegalArgumentException("Input file shall not be null");
            }
            archiveFile = tgz;
            extractDir = null;
            unpackTask = new FutureTask<>(new UnpackProcess());
            executor = Executors.newSingleThreadExecutor();
        }

        public static String getFilenameFromPath(String path) {
            int lastSepIndex = path.lastIndexOf("/");
            if (lastSepIndex == -1) {
                lastSepIndex = path.lastIndexOf("\\");
                if (lastSepIndex == -1) {
                    return path;
                }
            }

            return path.substring(lastSepIndex + 1, path.length());
        }

        public static boolean isTgz(String filename) {
            final String extension = FileUtils.getExtension(filename);
            return (".tgz".equals(extension) || ".gz".equals(extension));
        }

        public static boolean isTar(String filename) {
            return ".tar".equals(FileUtils.getExtension(filename));
        }

        @Override
        public <ResultType> ResultType loadData(String path, ICallbackCommand<ResultType> command) throws IOException {
            File file = getFile(path);
            return command.execute(file.toPath());
        }

        @Override
        public String getBasePath() {
            return archiveFile.getPath();
        }

        @Override
        public File getBaseFile() {
            return archiveFile;
        }

        @Override
        public InputStream getInputStream(String path) throws IOException {
            final File file = getFile(path);
            return new BufferedInputStream(new FileInputStream(file));
        }

        @Override
        public File getFile(String path) throws IOException {
            ensureUnpackedStarted();
            try {
                while (!unpackTask.isDone()) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // swallowed exception
            } finally {
                executor.shutdown();
            }
            final File file = new File(extractDir, path);
            if (!(file.isFile() || file.isDirectory())) {
                throw new FileNotFoundException("The path '"+path+"' does not exist in the folder '"+extractDir.getAbsolutePath()+"'.");
            }
            return file;
        }

        @Override
        public String[] list(String path) throws IOException {
            final File file = getFile(path);
            return file.list();
        }

        public boolean exists(String s) {
            return archiveFile.exists();
        }

        @Override
        public void close() {
            if (extractDir != null) {
                FileUtils.deleteTree(extractDir);
                extractDir = null;
            }
        }

        @Override
        public boolean isCompressed() {
            return isTgz(archiveFile.getName());
        }

        @Override
        public boolean isArchive() {
            return isTgz(archiveFile.getName());
        }

        @Override
        public void finalize() throws Throwable {
            super.finalize();
            close();
        }

        @Override
        public File getTempDir() throws IOException {
            ensureUnpackedStarted();
            return extractDir;
        }

        public void ensureUnpacked() throws IOException {
            ensureUnpacked(null);
        }

        public void ensureUnpacked(File unpackFolder) throws IOException {
            if (extractDir == null) {
                //SystemUtils.LOG.info("Unpacking archive contents");
                extractDir = unpackFolder != null ? unpackFolder : VirtualDir.createUniqueTempDir();
                TarInputStream tis = null;
                OutputStream outStream = null;
                try {
                    if (isTgz(archiveFile.getName())) {
                        tis = new TarInputStream(
                                new GZIPInputStream(new BufferedInputStream(new FileInputStream(archiveFile))));
                    } else {
                        tis = new TarInputStream(new BufferedInputStream(new FileInputStream(archiveFile)));
                    }
                    TarEntry entry;

                    String longLink = null;
                    while ((entry = tis.getNextEntry()) != null) {
                        String entryName = entry.getName();
                        boolean entryIsLink = entry.getHeader().linkFlag == TarHeader.LF_LINK || entry.getHeader().linkFlag == LF_SPEC_LINK;
                        if (longLink != null && longLink.startsWith(entryName)) {
                            entryName = longLink;
                            longLink = null;
                        }
                        if (entry.isDirectory()) {
                            final File directory = new File(extractDir, entryName);
                            ensureDirectory(directory);
                            continue;
                        }

                        final String fileNameFromPath = getFilenameFromPath(entryName);
                        final int pathIndex = entryName.indexOf(fileNameFromPath);
                        String tarPath = null;
                        if (pathIndex > 0) {
                            tarPath = entryName.substring(0, pathIndex - 1);
                        }

                        File targetDir;
                        if (tarPath != null) {
                            targetDir = new File(extractDir, tarPath);
                        } else {
                            targetDir = extractDir;
                        }

                        ensureDirectory(targetDir);
                        final File targetFile = new File(targetDir, fileNameFromPath);
                        if (!entryIsLink && targetFile.isFile()) {
                            continue;
                        }

                        if (!entryIsLink && !targetFile.createNewFile()) {
                            throw new IOException("Unable to create file: " + targetFile.getAbsolutePath());
                        }

                        outStream = new BufferedOutputStream(new FileOutputStream(targetFile));
                        final byte data[] = new byte[1024 * 1024];
                        int count;
                        while ((count = tis.read(data)) != -1) {
                            outStream.write(data, 0, count);
                            //if the entry is a link, must be saved, since the name of the next entry depends on this
                            if (entryIsLink) {
                                longLink = (longLink == null ? "" : longLink) + new String(data, 0, count);
                            } else {
                                longLink = null;
                            }
                        }
                        //the last character is \u0000, so it must be removed
                        if (longLink != null) {
                            longLink = longLink.substring(0, longLink.length() - 1);
                        }
                        outStream.flush();
                        outStream.close();

                    }
                } finally {
                    if (tis != null) {
                        tis.close();
                    }
                    if (outStream != null) {
                        outStream.flush();
                        outStream.close();
                    }
                }
            }
        }

        private void ensureDirectory(File targetDir) throws IOException {
            if (!targetDir.isDirectory()) {
                if (!targetDir.mkdirs()) {
                    throw new IOException("unable to create directory: " + targetDir.getAbsolutePath());
                }
            }
        }

        @Override
        public String[] listAll(Pattern...patterns) {
            List<String> fileNames = new ArrayList<>();
            TarInputStream tis;
            try (FileInputStream fileStream = new FileInputStream(this.archiveFile)) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileStream);
                if (isTgz(this.archiveFile.getName())) {
                    tis = new TarInputStream(new GZIPInputStream(bufferedInputStream));
                } else {
                    tis = new TarInputStream(bufferedInputStream);
                }
                TarEntry entry;
                String longLink = null;
                while ((entry = tis.getNextEntry()) != null) {
                    String entryName = entry.getName();
                    boolean entryIsLink = entry.getHeader().linkFlag == TarHeader.LF_LINK || entry.getHeader().linkFlag == LF_SPEC_LINK;
                    if (longLink != null && longLink.startsWith(entryName)) {
                        entryName = longLink;
                        longLink = null;
                    }
                    //if the entry is a link, must be saved, since the name of the next entry depends on this
                    if (entryIsLink) {
                        final byte data[] = new byte[1024 * 1024];
                        int count;
                        while ((count = tis.read(data)) != -1) {
                            longLink = (longLink == null ? "" : longLink) + new String(data, 0, count);
                        }
                    } else {
                        longLink = null;
                        fileNames.add(entryName);
                    }
                    //the last character is \u0000, so it must be removed
                    if (longLink != null) {
                        longLink = longLink.substring(0, longLink.length() - 1);
                    }
                }
            } catch (IOException e) {
                // cannot open/read tar, list will be empty
                fileNames = new ArrayList<>();
            }
            return fileNames.toArray(new String[fileNames.size()]);
        }

        public void ensureUnpackedStarted() {
            if (!unpackStarted) {
                unpackStarted = true;
                executor.execute(unpackTask);
            }
        }
    }
}
