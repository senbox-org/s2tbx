package org.esa.s2tbx.dataio;

import com.bc.ceres.core.VirtualDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.TreeSet;

/**
 * Created by jcoravu on 3/4/2019.
 */
public class VirtualZipPath extends VirtualDir {

    private final Path zipPath;

    private File tempZipFileDir;

    public VirtualZipPath(Path zipPath) {
        this.zipPath = zipPath;
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
        throw new UnsupportedOperationException("Not implemented yet!");
//        InputStream inputStream = Files.newInputStream(this.zipPath);
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//        ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);
//
//        boolean foundZipEntry = false;
//        ZipEntry zipEntry;
//        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
//            if (zipEntry.getName().equals(zipEntryPath)) {
//                foundZipEntry = true;
//            }
//        }
//        if (foundZipEntry) {
//            if (zipEntryPath.endsWith(".gz")) {
//                return new GZIPInputStream(zipInputStream);
//            }
//            return zipInputStream;
//        } else {
//            //TODO Jean close the streams
//            // the zip entry does not exist
//            throw new FileNotFoundException(this.zipPath.toString() + "!" + zipEntryPath);
//        }
    }

    @Override
    public File getFile(String zipEntryPath) throws IOException {
        if (this.tempZipFileDir == null) {
            this.tempZipFileDir = VirtualDir.createUniqueTempDir();
        }
        try {
            Path copiedFilePath = ZipFileSystemBuilder.copyFileFromZipArchive(this.zipPath, zipEntryPath, this.tempZipFileDir.toPath());
            return (copiedFilePath == null) ? null : copiedFilePath.toFile();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String[] list(String path) throws IOException {
        TreeSet<String> nameSet;
        try {
            nameSet = ZipFileSystemBuilder.listDirectoryEntriesFromZipArchive(this.zipPath, path);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        }
        if (nameSet == null) {
            throw new FileNotFoundException(getBasePath() + "!" + path);
        }
        return nameSet.toArray(new String[nameSet.size()]);
    }

    @Override
    public boolean exists(String zipEntryPath) {
        try {
            return ZipFileSystemBuilder.existFileInZipArchive(this.zipPath, zipEntryPath);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String[] listAllFiles() throws IOException {
        try {
            TreeSet<String> nameSet = ZipFileSystemBuilder.listAllFileEntriesFromZipArchive(this.zipPath);
            return nameSet.toArray(new String[0]);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
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
        return tempZipFileDir;
    }

    private void cleanup() {
        if (tempZipFileDir != null) {
            deleteFileTree(tempZipFileDir);
            tempZipFileDir = null;
        }
    }
}
