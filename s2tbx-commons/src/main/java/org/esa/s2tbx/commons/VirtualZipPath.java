package org.esa.s2tbx.commons;

import org.esa.snap.engine_utilities.util.AllFilesVisitor;
import org.esa.snap.engine_utilities.util.FileSystemUtils;
import org.esa.snap.engine_utilities.util.ZipFileSystemBuilder;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by jcoravu on 3/4/2019.
 */
public class VirtualZipPath extends AbstractVirtualPath {

    private final Path zipPath;

    public VirtualZipPath(Path zipPath, boolean copyFilesOnLocalDisk) {
        super(copyFilesOnLocalDisk);

        this.zipPath = zipPath;
    }

    @Override
    public Path buildPath(String first, String... more) {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            return fileSystem.getPath(first, more);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getFileSystemSeparator() {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            return fileSystem.getSeparator();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getBasePath() {
        return this.zipPath.toString();
    }

    @Override
    public File getBaseFile() {
        return this.zipPath.toFile();
    }

    @Override
    public FilePathInputStream getInputStream(String zipEntryPath) throws IOException {
        boolean success = false;
        FileSystem fileSystem = null;
        try {
            fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath);
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path zipArchiveRoot = it.next();
                Path entryPath = ZipFileSystemBuilder.buildZipEntryPath(zipArchiveRoot, zipEntryPath);
                if (Files.exists(entryPath)) {
                    // the entry exists into the zip archive
                    FilePathInputStream filePathInputStream = getBufferedInputStream(entryPath, fileSystem);
                    success = true;
                    return filePathInputStream;
                }
            }
            throw new FileNotFoundException(getMissingZipEntryExceptionMessage(zipEntryPath));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        } finally {
            if (fileSystem != null && !success) {
                fileSystem.close();
            }
        }
    }

    @Override
    public FilePathInputStream getInputStreamIgnoreCaseIfExists(String zipEntryPath) throws IOException {
        boolean success = true;
        FileSystem fileSystem = null;
        try {
            fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath);
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path zipArchiveRoot = it.next();
                Path entryPathToFind = ZipFileSystemBuilder.buildZipEntryPath(zipArchiveRoot, zipEntryPath);
                FindChildItemVisitor findChildFileVisitor = new FindChildItemVisitor(entryPathToFind);
                Files.walkFileTree(zipArchiveRoot, findChildFileVisitor);
                if (findChildFileVisitor.getExistingChildPath() != null) {
                    // the entry exists into the zip archive
                    return getBufferedInputStream(findChildFileVisitor.getExistingChildPath(), fileSystem);
                }
            }
            success = false;
            return null;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        } finally {
            if (fileSystem != null && !success) {
                fileSystem.close();
            }
        }
    }

    @Override
    public File getFile(String zipEntryPath) throws IOException {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path zipArchiveRoot = it.next();
                Path entryPath = ZipFileSystemBuilder.buildZipEntryPath(zipArchiveRoot, zipEntryPath);
                if (Files.exists(entryPath)) {
                    // the entry exists into the zip archive
                    Path fileToReturn = copyFileOnLocalDiskIfNeeded(entryPath, zipEntryPath);
                    return fileToReturn.toFile();
                }
            } // end 'while (it.hasNext())'
            throw new FileNotFoundException(getMissingZipEntryExceptionMessage(zipEntryPath));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public FilePath getFilePath(String zipEntryPath) throws IOException {
        boolean success = false;
        FileSystem fileSystem = null;
        try {
            fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath);
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path zipArchiveRoot = it.next();
                Path entryPath = ZipFileSystemBuilder.buildZipEntryPath(zipArchiveRoot, zipEntryPath);
                if (Files.exists(entryPath)) {
                    // the entry exists into the zip archive
                    success = true;
                    return new FilePath(entryPath, fileSystem);
                }
            }
            throw new FileNotFoundException(getMissingZipEntryExceptionMessage(zipEntryPath));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        } finally {
            if (fileSystem != null && !success) {
                fileSystem.close();
            }
        }
    }

    @Override
    public String[] list(String zipEntryPath) throws IOException {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path zipArchiveRoot = it.next();
                Path entryPath = zipArchiveRoot;
                if (zipEntryPath != null) {
                    entryPath = ZipFileSystemBuilder.buildZipEntryPath(zipArchiveRoot, zipEntryPath);
                }
                if (Files.exists(entryPath)) {
                    // the zip entry exists
                    if (Files.isDirectory(entryPath)) {
                        List<String> files = new ArrayList<String>();
                        String zipFileSystemSeparator = fileSystem.getSeparator();
                        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(entryPath)) {
                            for (Path currentPath : directoryStream) {
                                String fileName = currentPath.getFileName().toString();
                                if (fileName.endsWith(zipFileSystemSeparator)) {
                                    int index = fileName.length() - zipFileSystemSeparator.length();
                                    fileName = fileName.substring(0, index);
                                }
                                files.add(fileName);
                            }
                            return files.toArray(new String[files.size()]);
                        }
                    } else {
                        throw new NotDirectoryException(entryPath.toString());
                    }
                }
            } // end 'while (it.hasNext())'
            throw new FileNotFoundException(getMissingZipEntryExceptionMessage(zipEntryPath));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean exists(String zipEntryPath) {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path zipArchiveRoot = it.next();
                Path entryPath = zipArchiveRoot;
                if (zipEntryPath != null) {
                    entryPath = ZipFileSystemBuilder.buildZipEntryPath(zipArchiveRoot, zipEntryPath);
                }
                if (Files.exists(entryPath)) {
                    return true;
                }
            } // end 'while (it.hasNext())'
            return false;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Path getFileIgnoreCaseIfExists(String zipEntryPath) throws IOException {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path zipArchiveRoot = it.next();
                Path entryPathToFind = ZipFileSystemBuilder.buildZipEntryPath(zipArchiveRoot, zipEntryPath);
                FindChildItemVisitor findChildFileVisitor = new FindChildItemVisitor(entryPathToFind);
                Files.walkFileTree(zipArchiveRoot, findChildFileVisitor);
                if (findChildFileVisitor.getExistingChildPath() != null) {
                    // the entry exists into the zip archive
                    return copyFileOnLocalDiskIfNeeded(findChildFileVisitor.getExistingChildPath(), zipEntryPath);
                }
            } // end 'while (it.hasNext())'
            return null;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String[] listAllFiles() throws IOException {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            AllFilesVisitor filesVisitor = new AllFilesVisitor();
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path zipArchiveRoot = it.next();
                Files.walkFileTree(zipArchiveRoot, filesVisitor);
            } // end 'while (it.hasNext())'
            TreeSet<String> filePaths = filesVisitor.getFilePaths();
            return filePaths.toArray(new String [filePaths.size()]);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isCompressed() {
        return true;
    }

    @Override
    public boolean isArchive() {
        return true;
    }

    private String getMissingZipEntryExceptionMessage(String zipEntryPath) {
        return "The zip entry path '"+zipEntryPath+"' does not exist in the zip archive '"+this.zipPath.toString()+"'.";
    }
}
