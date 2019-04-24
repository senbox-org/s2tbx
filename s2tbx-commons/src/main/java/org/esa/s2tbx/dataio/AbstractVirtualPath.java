package org.esa.s2tbx.dataio;

import com.bc.ceres.core.VirtualDir;
import org.esa.snap.utils.FileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by jcoravu on 9/4/2019.
 */
public abstract class AbstractVirtualPath extends VirtualDir {

    private final boolean copyFilesOnLocalDisk;

    private File localTempDir;

    protected AbstractVirtualPath(boolean copyFilesOnLocalDisk) {
        this.copyFilesOnLocalDisk = copyFilesOnLocalDisk;
    }

    public abstract Path buildPath(String first, String... more);

    public abstract String getFileSystemSeparator();

    public abstract <ResultType> ResultType loadData(String relativePath, ICallbackCommand<ResultType> command) throws IOException;

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
    public File getTempDir() throws IOException {
        return this.localTempDir;
    }

    protected final Path copyFileOnLocalDiskIfNeeded(Path entryPath, String childRelativePath) throws IOException {
        if (this.copyFilesOnLocalDisk && Files.isRegularFile(entryPath)) {
            // copy the file from the zip archive on the local disk
            if (this.localTempDir == null) {
                this.localTempDir = VirtualDir.createUniqueTempDir();
            }
            Path localFilePath = this.localTempDir.toPath().resolve(childRelativePath);
            copyFileOnLocalDiskIfMissing(entryPath, localFilePath);
            return localFilePath;
        } else {
            // do not copy the file from the zip archive on the local disk
            return entryPath;
        }
    }

    private static void copyFileOnLocalDiskIfMissing(Path sourceFile, Path destinationFile) throws IOException {
        boolean copyFile = true;
        if (Files.exists(destinationFile)) {
            // the destination file already exists
            if (Files.isRegularFile(destinationFile)) {
                long sourceFileSizeInBytes = Files.size(sourceFile);
                long destinationFileSizeInBytes = Files.size(destinationFile);
                copyFile = (destinationFileSizeInBytes != sourceFileSizeInBytes);
            } else {
                throw new NotRegularFileException(destinationFile.toString());
            }
        }
        if (copyFile) {
            Path parentFolder = destinationFile.getParent();
            if (!Files.exists(parentFolder)) {
                Files.createDirectories(parentFolder);
            }
            FileHelper.copyFileUsingInputStream(sourceFile, destinationFile.toString(), VirtualDirEx.BUFFER_SIZE);
        }
    }

    private void cleanup() {
        if (this.localTempDir != null) {
            deleteFileTree(this.localTempDir);
            this.localTempDir = null;
        }
    }
}
