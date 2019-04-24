package org.esa.s2tbx.dataio;

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
public class VirtualDirPath extends AbstractVirtualPath {

    private final Path dirPath;

    public VirtualDirPath(Path dirPath, boolean copyFilesOnLocalDisk) {
        super(copyFilesOnLocalDisk);

        this.dirPath = dirPath;
    }

    @Override
    public Path buildPath(String first, String... more) {
        return this.dirPath.getFileSystem().getPath(first, more);
    }

    @Override
    public String getFileSystemSeparator() {
        return this.dirPath.getFileSystem().getSeparator();
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
    public InputStream getInputStream(String childRelativePath) throws IOException {
        Path child = this.dirPath.resolve(childRelativePath);
        if (Files.exists(child)) {
            // the child exists
            if (Files.isRegularFile(child)) {
                // the chils is a file

                //TODO Jean remote system.out
                System.out.println("getInputStream dir child="+child.toString());

                InputStream inputStream = Files.newInputStream(child);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, VirtualDirEx.BUFFER_SIZE);
                if (childRelativePath.endsWith(".gz")) {
                    return new GZIPInputStream(bufferedInputStream);
                }
                return bufferedInputStream;
            } else {
                throw new NotRegularFileException(child.toString());
            }
        } else {
            throw new FileNotFoundException(child.toString());
        }
    }

    //TODO Jean remote attribute
    int fileCount = 0;

    @Override
    public File getFile(String childRelativePath) throws IOException {
        Path child = this.dirPath.resolve(childRelativePath);

        //TODO Jean remote system.out
        System.out.println((++this.fileCount) + " dir getFile '"+child.toString()+"'");

        if (Files.exists(child)) {
            Path fileToReturn = copyFileOnLocalDiskIfNeeded(child, childRelativePath);
            return fileToReturn.toFile();
        } else {
            throw new FileNotFoundException(child.toString());
        }
    }

    @Override
    public <ResultType> ResultType loadData(String path, ICallbackCommand<ResultType> command) throws IOException {
        Path child = this.dirPath.resolve(path);
        if (Files.exists(child)) {
            return command.execute(child);
        } else {
            throw new FileNotFoundException(child.toString());
        }
    }

    @Override
    public String[] list(String childRelativePath) throws IOException {
        Path child = this.dirPath.resolve(childRelativePath);
        if (Files.isDirectory(child)) {
            List<String> files = new ArrayList<String>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(child)) {
                for (Path currentPath : stream) {
                    files.add(currentPath.getFileName().toString());
                }
                return files.toArray(new String[files.size()]);
            }
        }
        return new String[0];
    }

    @Override
    public boolean exists(String childRelativePath) {
        Path child = this.dirPath.resolve(childRelativePath);
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
    public boolean isCompressed() {
        return false;
    }

    @Override
    public boolean isArchive() {
        return false;
    }
}
