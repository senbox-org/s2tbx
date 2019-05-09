package org.esa.s2tbx.commons;

import org.esa.s2tbx.dataio.VirtualDirEx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
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
    public FilePathInputStream getInputStream(String childRelativePath) throws IOException {
        Path child = this.dirPath.resolve(childRelativePath);
        if (Files.exists(child)) {
            // the child exists
            return getInputStream(child);
        } else {
            throw new FileNotFoundException(child.toString());
        }
    }

    @Override
    public FilePathInputStream getInputStreamIgnoreCaseIfExists(String childRelativePath) throws IOException {
        Path fileToReturn = findFileIgnoreCase(this.dirPath, childRelativePath);
        if (fileToReturn != null) {
            // the child exists
            return getInputStream(fileToReturn);
        }
        return null;
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
    public String[] list(String childRelativePath) throws IOException {
        Path child = this.dirPath;
        if (childRelativePath != null) {
            child = this.dirPath.resolve(childRelativePath);
        }
        if (Files.exists(child)) {
            if (Files.isDirectory(child)) {
                List<String> files = new ArrayList<String>();
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(child)) {
                    for (Path currentPath : directoryStream) {
                        files.add(currentPath.getFileName().toString());
                    }
                    return files.toArray(new String[files.size()]);
                }
            } else {
                throw new NotDirectoryException(child.toString());
            }
        } else {
            throw new FileNotFoundException(child.toString());
        }
    }

    @Override
    public boolean exists(String childRelativePath) {
        Path child = this.dirPath;
        if (childRelativePath != null) {
            child = this.dirPath.resolve(childRelativePath);
        }
        return Files.exists(child);
    }

    @Override
    public Path getFileIgnoreCaseIfExists(String childRelativePath) throws IOException {
        Path fileToReturn = findFileIgnoreCase(this.dirPath, childRelativePath);
        if (fileToReturn != null) {
            // the child exists
            return copyFileOnLocalDiskIfNeeded(fileToReturn, childRelativePath);
        }
        return null;
    }

    @Override
    public String[] listAllFiles() throws IOException {
        ListAllFilesVisitor filesVisitor = new ListAllFilesVisitor();
        Files.walkFileTree(this.dirPath, filesVisitor);
        TreeSet<String> nameSet = filesVisitor.getNameSet();
        return nameSet.toArray(new String [nameSet.size()]);
    }

    @Override
    public boolean isCompressed() {
        return false;
    }

    @Override
    public boolean isArchive() {
        return false;
    }

    private static Path buildChildPath(Path parentDirPath, String childRelativePath) {
        String fileSystemSeparator = parentDirPath.getFileSystem().getSeparator();
        String relativePath = replaceFileSeparator(childRelativePath, fileSystemSeparator);
        if (relativePath.startsWith(fileSystemSeparator)) {
            relativePath = relativePath.substring(fileSystemSeparator.length());
        }
        return parentDirPath.resolve(relativePath);
    }

    public static Path findFileIgnoreCase(Path parentDirPath, String childRelativePath) throws IOException {
        Path childPathToFind = buildChildPath(parentDirPath, childRelativePath);
        FindChildFileVisitor findChildFileVisitor = new FindChildFileVisitor(childPathToFind);
        Files.walkFileTree(parentDirPath, findChildFileVisitor);
        if (findChildFileVisitor.getExistingChildPath() != null) {
            return findChildFileVisitor.getExistingChildPath();
        }
        return null;
    }

    private static FilePathInputStream getInputStream(Path child) throws IOException {
        if (Files.isRegularFile(child)) {
            // the child is a file

            //TODO Jean remote system.out
            System.out.println("getInputStream dir child="+child.toString());

            InputStream inputStream = Files.newInputStream(child);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, VirtualDirEx.BUFFER_SIZE);
            InputStream inputStreamToReturn;
            if (child.toString().endsWith(".gz")) {
                inputStreamToReturn = new GZIPInputStream(bufferedInputStream);
            } else {
                inputStreamToReturn = bufferedInputStream;
            }
            return new FilePathInputStream(child, inputStreamToReturn, null);
        } else {
            throw new NotRegularFileException(child.toString());
        }
    }
}
