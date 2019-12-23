package org.esa.s2tbx.commons;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.engine_utilities.util.NotRegularFileException;
import org.esa.snap.utils.FileHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jcoravu on 15/5/2019.
 */
public abstract class AbstractFile {

    private static final Logger logger = Logger.getLogger(AbstractFile.class.getName());

    private final Path file;

    protected AbstractFile(Path file) {
        this.file = file;
    }

    protected abstract Path getLocalTempFolder() throws IOException;

    public String getFileName() {
        return this.file.getFileName().toString();
    }

    public Path getLocalFile() throws IOException {
        if (Files.exists(this.file)) {
            if (Files.isRegularFile(this.file)) {
                Path localFile;
                if (isLocalPath(this.file)) {
                    localFile = this.file;
                } else {
                    Path localTempFolder = getLocalTempFolder();
                    String fileName = this.file.getFileName().toString();
                    localFile = localTempFolder.resolve(fileName);
                    if (FileHelper.canCopyOrReplaceFile(this.file, localFile)) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.log(Level.FINE, "Copy file '" + this.file.toString() + "' to local folder '" + localTempFolder.toString() + "'.");
                        }
                        FileHelper.copyFileUsingInputStream(this.file, localFile.toString(), VirtualDirEx.BUFFER_SIZE);
                    }
                }
                return localFile;
            } else {
                throw new NotRegularFileException(this.file.toString());
            }
        } else {
            throw new FileNotFoundException(this.file.toString());
        }
    }

    public static boolean isLocalPath(Path path) {
        return (path.getFileSystem().provider() == FileSystems.getDefault().provider());
    }
}
