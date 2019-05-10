package org.esa.s2tbx.dataio.jp2;

import org.esa.s2tbx.commons.NotRegularFileException;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.PathUtils;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.utils.FileHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.esa.s2tbx.dataio.Utils.getMD5sum;

/**
 * Created by jcoravu on 8/5/2019.
 */
public class VirtualFile {

    private static final Logger logger = Logger.getLogger(VirtualFile.class.getName());

    private final Path file;
    private Path localCacheFolder;

    public VirtualFile(Path file, Class clazz) throws IOException {
        this.file = file;

        this.localCacheFolder = buildLocalCacheFolder(file, clazz);
    }

    public Path getLocalCacheFolder() {
        return localCacheFolder;
    }

    public String getFileName() {
        return this.file.getFileName().toString();
    }

    public void deleteLocalFilesOnExit() throws IOException {
        List<Path> files = PathUtils.listFiles(this.localCacheFolder);
        this.localCacheFolder.toFile().deleteOnExit();
        if (files != null) {
            for (Path file : files) {
                file.toFile().deleteOnExit();
            }
        }
    }

    public Path getLocalFile() throws IOException {
        if (Files.exists(this.file)) {
            if (Files.isRegularFile(this.file)) {
                Path localFile;
                FileSystemProvider fileSystemProvider = this.file.getFileSystem().provider();
                if (fileSystemProvider == FileSystems.getDefault().provider()) {
                    localFile = this.file;
                } else {
                    String fileName = this.file.getFileName().toString();
                    localFile = this.localCacheFolder.resolve(fileName);
                    if (FileHelper.canCopyOrReplaceFile(this.file, localFile)) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.log(Level.FINE, "Copy file '" + this.file.toString() + "' to local folder '" + this.localCacheFolder.toString() + "'.");
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

    private static Path buildLocalCacheFolder(Path inputFile, Class clazz) throws IOException {
        Path versionFile = ResourceInstaller.findModuleCodeBasePath(clazz).resolve("version/version.properties");
        Properties versionProp = new Properties();

        try (InputStream inputStream = Files.newInputStream(versionFile)) {
            versionProp.load(inputStream);
        }

        String version = versionProp.getProperty("project.version");
        if (version == null) {
            throw new IOException("Unable to get project.version property from " + versionFile);
        }

        String md5sum = getMD5sum(inputFile.toString());
        if (md5sum == null) {
            throw new IOException("Unable to get md5sum of path " + inputFile.toString());
        }

        Path localCacheFolder = PathUtils.get(SystemUtils.getCacheDir(), "s2tbx", "jp2-reader", version, md5sum, PathUtils.getFileNameWithoutExtension(inputFile).toLowerCase() + "_cached");
        if (!Files.exists(localCacheFolder)) {
            Files.createDirectories(localCacheFolder);
        }
        return localCacheFolder;
    }
}
