package org.esa.s2tbx.dataio;

import com.bc.ceres.core.VirtualDir;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Created by jcoravu on 3/4/2019.
 */
public class VirtualDirPath extends VirtualDir {

    private final Path dirPath;

    public VirtualDirPath(Path dirPath) {
        this.dirPath = dirPath;
    }

    @Override
    public String getBasePath() {
        return this.dirPath.toString();
    }

    @Override
    public File getBaseFile() {
        return this.dirPath.toFile();
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        Path child = getFile(path).toPath();
        InputStream inputStream = Files.newInputStream(child);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        if (path.endsWith(".gz")) {
            return new GZIPInputStream(bufferedInputStream);
        }
        return bufferedInputStream;
    }

    @Override
    public File getFile(String path) throws IOException {
        Path child = this.dirPath.resolve(path);
        if (!Files.exists(child)) {
            throw new FileNotFoundException(child.toString());
        }
        return child.toFile();
    }

    @Override
    public String[] list(String path) throws IOException {
        Path child = getFile(path).toPath();
        if (Files.isDirectory(child)) {
            List<String> files = new ArrayList<String>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(child)) {
                for (Path currentPath : stream) {
                    files.add(currentPath.toString());
                }
                return files.toArray(new String[files.size()]);
            }
        }
        return new String[0];
    }

    @Override
    public boolean exists(String path) {
        Path child = this.dirPath.resolve(path);
        return Files.exists(child);
    }

    @Override
    public String[] listAllFiles() throws IOException {
        try (Stream<Path> pathStream = Files.walk(this.dirPath)) {
            Stream<Path> filteredStream = pathStream.filter(new Predicate<Path>() {
                @Override
                public boolean test(Path path) {
                    return Files.isRegularFile(path);
                }
            });
            final int baseLength = this.dirPath.toUri().toString().length();
            Stream<String> fileStream = filteredStream.map(new Function<Path, String>() {
                @Override
                public String apply(Path path) {
                    return path.toUri().toString().substring(baseLength);
                }
            });
            return fileStream.toArray(new IntFunction<String[]>() {
                @Override
                public String[] apply(int value) {
                    return new String[value];
                }
            });
        }
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public boolean isCompressed() {
        return false;
    }

    @Override
    public boolean isArchive() {
        return false;
    }
}
