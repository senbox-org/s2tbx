package org.esa.s2tbx.dataio;

import com.bc.ceres.core.VirtualDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by jcoravu on 3/4/2019.
 */
public class VirtualZipPath extends AbstractVirtualPath {

    private final Path zipPath;

    private File tempZipFileDir;

    public VirtualZipPath(Path zipPath) {
        this.zipPath = zipPath;
    }

    @Override
    public Path buildPath(String first, String... more) throws IOException {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            return fileSystem.getPath(first, more);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
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
                    InputStream inputStream = Files.newInputStream(entryPath);
                    return new AutoCloseInputStream(inputStream, fileSystem);
                }
            }
            success = false;
            throw new FileNotFoundException("The zip entry path '"+zipEntryPath+"' does not exist in the zip archive '"+this.zipPath.toString()+"'.");
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        } finally {
            if (fileSystem != null && !success) {
                fileSystem.close();
            }
        }
    }

    @Override
    public <ResultType> ResultType loadData(String zipEntryPath, ICallbackCommand<ResultType> command) throws IOException {
        try {
            return ZipFileSystemBuilder.loadZipEntryDataFromZipArchive(this.zipPath, zipEntryPath, command);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public File getFile(String zipEntryPath) throws IOException {
        if (this.tempZipFileDir == null) {
            this.tempZipFileDir = VirtualDir.createUniqueTempDir();
        }
        try {
            Path copiedFilePath = ZipFileSystemBuilder.copyFileFromZipArchive(this.zipPath, zipEntryPath, this.tempZipFileDir.toPath());
            return (copiedFilePath == null) ? null : copiedFilePath.toFile();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String[] list(String path) throws IOException {
        TreeSet<String> nameSet;
        try {
            nameSet = ZipFileSystemBuilder.listDirectoryEntriesFromZipArchive(this.zipPath, path);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
        if (nameSet == null) {
            throw new FileNotFoundException(getBasePath() + "!" + path);
        }
        return nameSet.toArray(new String[nameSet.size()]);
    }

    @Override
    public boolean exists(String zipEntryPath) {
        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(this.zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                Path entryPath = buildZipEntryPath(root, zipEntryPath);
                if (Files.exists(entryPath)) {
                    return true;
                }
            }
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
    public void close() {
        cleanup();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        cleanup();
    }

    @Override
    public boolean isCompressed() {
        return true;
    }

    @Override
    public boolean isArchive() {
        return true;
    }

    @Override
    public File getTempDir() throws IOException {
        return this.tempZipFileDir;
    }

    private void cleanup() {
        if (this.tempZipFileDir != null) {
            deleteFileTree(this.tempZipFileDir);
            this.tempZipFileDir = null;
        }
    }

    private static Path buildZipEntryPath(Path root, String zipEntryPath) {
        String rootAsString = root.toString();
        if (zipEntryPath.startsWith(rootAsString)) {
            return root.getFileSystem().getPath(zipEntryPath);
        }
        return root.resolve(zipEntryPath);
    }
}
