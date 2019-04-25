package org.esa.s2tbx.dataio;

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
    public InputStream getInputStream(String zipEntryPath) throws IOException {
        boolean success = true;
        FileSystem fileSystem = null;
        try {
            fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath);
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                Path entryPath = buildZipEntryPath(root, zipEntryPath);
                if (Files.exists(entryPath)) {
                    // the entry exists into the zip archive
                    if (Files.isRegularFile(entryPath)) {
                        // the entry is a file

                        //TODO Jean remote system.out
                        System.out.println("getInputStream zip entryPath="+entryPath.toString());

                        InputStream inputStream = Files.newInputStream(entryPath);
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, VirtualDirEx.BUFFER_SIZE);
                        return new AutoCloseInputStream(bufferedInputStream, fileSystem);
                    } else {
                        throw new NotRegularFileException(entryPath.toString());
                    }
                }
            }
            success = false;
            throw new FileNotFoundException(getMissingZipEntryExceptionMessage(zipEntryPath));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        } finally {
            if (fileSystem != null && !success) {
                fileSystem.close();
            }
        }
    }

    //TODO Jean remote attribute
    int fileCount = 0;

    @Override
    public File getFile(String zipEntryPath) throws IOException {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                Path entryPath = buildZipEntryPath(root, zipEntryPath);

                //TODO Jean remote system.out
                System.out.println((++this.fileCount) + " zip getFile '"+entryPath.toString()+"'");

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
    public <ResultType> ResultType loadData(String zipEntryPath, ICallbackCommand<ResultType> command) throws IOException {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                Path entryPath = buildZipEntryPath(root, zipEntryPath);
                if (Files.exists(entryPath)) {
                    // the entry exists into the zip archive
                    Path file = copyFileOnLocalDiskIfNeeded(entryPath, zipEntryPath);
                    return command.execute(file);
                }
            } // end 'while (it.hasNext())'
            throw new FileNotFoundException(getMissingZipEntryExceptionMessage(zipEntryPath));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String[] list(String zipEntryPath) throws IOException {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                Path entryPath = root;
                if (zipEntryPath != null) {
                    entryPath = buildZipEntryPath(root, zipEntryPath);
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
                Path root = it.next();
                Path entryPath = root;
                if (zipEntryPath != null) {
                    entryPath = buildZipEntryPath(root, zipEntryPath);
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
    public String[] listAllFiles() throws IOException {
        try {
            TreeSet<String> nameSet = ZipFileSystemBuilder.listAllFileEntriesFromZipArchive(this.zipPath);
            return nameSet.toArray(new String[0]);
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

    private static Path buildZipEntryPath(Path root, String zipEntryPath) {
        String rootAsString = root.toString();
        if (zipEntryPath.startsWith(rootAsString)) {
            return root.getFileSystem().getPath(zipEntryPath);
        }
        return root.resolve(zipEntryPath);
    }
}
