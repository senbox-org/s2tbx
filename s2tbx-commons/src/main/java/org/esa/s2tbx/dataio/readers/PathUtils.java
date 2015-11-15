package org.esa.s2tbx.dataio.readers;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by kraftek on 11/9/2015.
 */
public class PathUtils {

    public static Path get(File basePath, String...relatives) {
        if (relatives == null || relatives.length == 0) {
            return basePath.toPath();
        }
        return Paths.get(basePath.getAbsolutePath(), relatives);
    }

    public static Path get(Path basePath, String...relatives) {
        if (relatives == null || relatives.length == 0) {
            return basePath;
        }
        return Paths.get(basePath.toAbsolutePath().toString(), relatives);
    }

    /**
     * Returns the list of files in a folder using NIO API.
     *
     * @param basePath    The parent folder
     * @return The list of files
     * @throws IOException
     */
    public static List<Path> listFiles(Path basePath) throws IOException {
        return listFiles(basePath, 1);
    }

    /**
     * Returns the list of files in a folder, up to the given depth of the folder,
     * using NIO API.
     *
     * @param basePath    The parent folder
     * @param depth     The depth to look for files
     * @return The list of files
     * @throws IOException
     */
    public static List<Path> listFiles(Path basePath, int depth) throws IOException {
        if (basePath == null)
            return null;
        depth = depth <= 0 ? depth = 255 : depth;
        List<Path> files = new ArrayList<>();
        Files.walkFileTree(basePath,
                EnumSet.noneOf(FileVisitOption.class),
                depth,
                new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        files.add(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
        return files;
    }

    public static String[] list(Path basePath) throws IOException {
        if (basePath == null)
            return null;
        List<String> files = new ArrayList<>();
        Files.walkFileTree(basePath,
                EnumSet.noneOf(FileVisitOption.class),
                1,
                new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        files.add(file.toAbsolutePath().toString());
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
        return files.toArray(new String[files.size()]);
    }

    public static String getFileNameWithoutExtension(Path path) {
        if (path == null || Files.isDirectory(path)) {
            return null;
        }
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

}
