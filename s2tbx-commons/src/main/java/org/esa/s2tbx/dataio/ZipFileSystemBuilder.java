package org.esa.s2tbx.dataio;

import com.sun.nio.zipfs.ZipFileSystem;
import com.sun.nio.zipfs.ZipFileSystemProvider;
import org.esa.snap.vfs.remote.TransferFileContentUtil;
import org.esa.snap.vfs.remote.http.HttpFileSystemProvider;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by jcoravu on 4/4/2019.
 */
public class ZipFileSystemBuilder {

    private static final ZipFileSystemProvider ZIP_FILE_SYSTEM_PROVIDER = getZipFileSystemProvider();
    private static final Constructor<ZipFileSystem> ZIP_FILE_SYSTEM_CONSTRUCTOR;

    static {
        try {
            Constructor<ZipFileSystem> constructor = ZipFileSystem.class.getDeclaredConstructor(ZipFileSystemProvider.class, Path.class, Map.class);
            constructor.setAccessible(true);
            ZIP_FILE_SYSTEM_CONSTRUCTOR = constructor;
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    private ZipFileSystemBuilder() {
    }

    private static ZipFileSystemProvider getZipFileSystemProvider() {
        for (FileSystemProvider fsr : FileSystemProvider.installedProviders()) {
            if (fsr instanceof ZipFileSystemProvider)
                return (ZipFileSystemProvider) fsr;
        }
        throw new FileSystemNotFoundException("The zip file system provider is not installed!");
    }

    public static FileSystem newZipFileSystem(Path zipPath) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (zipPath.getFileSystem() instanceof ZipFileSystem) {
            throw new IllegalArgumentException("Can't create a ZIP file system nested in a ZIP file system. (" + zipPath + " is nested in " + zipPath.getFileSystem() + ")");
        }
        return ZIP_FILE_SYSTEM_CONSTRUCTOR.newInstance(ZIP_FILE_SYSTEM_PROVIDER, zipPath, Collections.emptyMap());
    }

    public static <ResultType> ResultType loadZipEntryDataFromZipArchive(Path zipPath, String zipEntryPath, ICallbackCommand<ResultType> command)
                                                        throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException {

        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                LoadZipEntryDataFileVisitor<ResultType> visitor = new LoadZipEntryDataFileVisitor<ResultType>(zipEntryPath, command);
                Files.walkFileTree(root, visitor);
                if (visitor.isFoundZipEntry()) {
                    return visitor.getZipEntryData();
                }
            }
            throw new FileNotFoundException("The zip entry path '"+zipEntryPath+"' does not exist in the zip archive '"+zipPath.toString()+"'.");
        }
    }

    public static TreeSet<String> listAllFileEntriesFromZipArchive(Path zipPath)
                                                throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException {

        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(zipPath)) {
            ListAllFileZipEntriesFileVisitor visitor = new ListAllFileZipEntriesFileVisitor();
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                Files.walkFileTree(root, visitor);
            }
            return visitor.getNameSet();
        }
    }

    public static TreeSet<String> listDirectoryEntriesFromZipArchive(Path zipPath, String directoryZipEntryPath)
                                                 throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException {

        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(zipPath)) {
            ListDirectoryZipEntriesFileVisitor visitor = new ListDirectoryZipEntriesFileVisitor(directoryZipEntryPath);
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                Files.walkFileTree(root, visitor);
            }
            return visitor.getNameSet();
        }
    }

    public static Path copyFileFromZipArchive(Path zipPath, String zipEntryPath, Path destinationFolder)
                                              throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException {

        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                CopyZipEntryFileVisitor visitor = new CopyZipEntryFileVisitor(zipEntryPath, destinationFolder);
                Files.walkFileTree(root, visitor);
                if (visitor.getLocalFile() != null) {
                    return visitor.getLocalFile();
                }
            }
            throw new FileNotFoundException("The zip entry path '"+zipEntryPath+"' does not exist in the zip archive '"+zipPath.toString()+"'.");
        }
    }

    public static boolean existFileInZipArchive(Path zipPath, String zipEntryPath)
                                                throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {

        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                ExistsZipEntryFileVisitor visitor = new ExistsZipEntryFileVisitor(zipEntryPath);
                Files.walkFileTree(root, visitor);
                if (visitor.existsZipEntry()) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class CopyZipEntryFileVisitor extends SimpleFileVisitor<Path> {

        private final String zipEntryPath;
        private final Path destinationFolder;

        private Path localFile;

        private CopyZipEntryFileVisitor(String zipEntryPath, Path destinationFolder) {
            if (zipEntryPath.startsWith("/")) {
                this.zipEntryPath = zipEntryPath.substring(1);
            } else {
                this.zipEntryPath = zipEntryPath;
            }
            this.destinationFolder = destinationFolder;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String currentZipEntryPath = file.toString();
            if (currentZipEntryPath.startsWith("/")) {
                currentZipEntryPath = currentZipEntryPath.substring(1);
            }
            if (this.zipEntryPath.equals(currentZipEntryPath)) {
                this.localFile = this.destinationFolder.resolve(currentZipEntryPath);
                Path parentFolder = this.localFile.getParent();
                if (!Files.exists(parentFolder)) {
                    Files.createDirectories(parentFolder);
                }
                boolean copyFile = true;
                if (Files.isRegularFile(this.localFile)) {
                    // the local file already exists
                    long localFileSizeInBytes = Files.size(this.localFile);
                    long zipEntryFileSizeInBytes = Files.size(file);
                    copyFile = (localFileSizeInBytes != zipEntryFileSizeInBytes);
                }
                if (copyFile) {
                    TransferFileContentUtil.copyFileUsingInputStream(file, this.localFile.toString());
                }
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

        Path getLocalFile() {
            return this.localFile;
        }
    }

    private static class LoadZipEntryDataFileVisitor<ResultType> extends SimpleFileVisitor<Path> {

        private final String zipEntryPath;
        private final ICallbackCommand<ResultType> command;

        private ResultType zipEntryData;
        private boolean foundZipEntry;

        private LoadZipEntryDataFileVisitor(String zipEntryPath, ICallbackCommand<ResultType> command) {
            if (zipEntryPath.startsWith("/")) {
                this.zipEntryPath = zipEntryPath.substring(1);
            } else {
                this.zipEntryPath = zipEntryPath;
            }
            this.command = command;
            this.foundZipEntry = false;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String currentZipEntryPath = file.toString();
            if (currentZipEntryPath.startsWith("/")) {
                currentZipEntryPath = currentZipEntryPath.substring(1);
            }
            if (this.zipEntryPath.equals(currentZipEntryPath)) {
                this.foundZipEntry = true;
                this.zipEntryData = this.command.execute(file);
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

        boolean isFoundZipEntry() {
            return foundZipEntry;
        }

        ResultType getZipEntryData() {
            return zipEntryData;
        }
    }

    private static class ExistsZipEntryFileVisitor extends SimpleFileVisitor<Path> {

        private final String zipEntryPath;

        private boolean zipEntryExists;

        private ExistsZipEntryFileVisitor(String zipEntryPath) {
            if (zipEntryPath.startsWith("/")) {
                this.zipEntryPath = zipEntryPath.substring(1);
            } else {
                this.zipEntryPath = zipEntryPath;

            }
            this.zipEntryExists = false;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            return checkZipEntry(file);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return checkZipEntry(dir);
        }

        boolean existsZipEntry() {
            return zipEntryExists;
        }

        private FileVisitResult checkZipEntry(Path path) {
            String currentZipEntryPath = path.toString();
            if (currentZipEntryPath.startsWith("/")) {
                currentZipEntryPath = currentZipEntryPath.substring(1);
            }
            if (this.zipEntryPath.equals(currentZipEntryPath)) {
                this.zipEntryExists = true;
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }
    }

    private static class ListDirectoryZipEntriesFileVisitor extends SimpleFileVisitor<Path> {

        private final String directoryZipEntryPath;

        private TreeSet<String> nameSet;

        private ListDirectoryZipEntriesFileVisitor(String directoryZipEntryPath) {
            String path = directoryZipEntryPath;
            if (".".equals(directoryZipEntryPath) || directoryZipEntryPath.isEmpty()) {
                path = "";
            } else if (!directoryZipEntryPath.endsWith("/")) {
                path += "/"; // add '/' at the end
            }
            if (path.startsWith("/")) {
                this.directoryZipEntryPath = path.substring(1);
            } else {
                this.directoryZipEntryPath = path;
            }
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            return checkZipEntry(file);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return checkZipEntry(dir);
        }

        TreeSet<String> getNameSet() {
            return this.nameSet;
        }

        private FileVisitResult checkZipEntry(Path path) {
            String currentZipEntryPath = path.toString();
            if (currentZipEntryPath.startsWith("/")) {
                currentZipEntryPath = currentZipEntryPath.substring(1);
            }
            if (currentZipEntryPath.startsWith(this.directoryZipEntryPath)) {
                // the directory exists
                if (this.nameSet == null) {
                    this.nameSet = new TreeSet<>();
                }
                int i1 = this.directoryZipEntryPath.length();
                int i2 = currentZipEntryPath.indexOf('/', i1);
                String entryName;
                if (i2 == -1) {
                    entryName = currentZipEntryPath.substring(i1);
                } else {
                    entryName = currentZipEntryPath.substring(i1, i2);
                }
                if (!entryName.isEmpty() && !this.nameSet.contains(entryName)) {
                    this.nameSet.add(entryName);
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }
//    @Override
//    public String[] list(String path) throws IOException {
//        try (InputStream inputStream = Files.newInputStream(this.zipPath);
//             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//             ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream)) {
//
//            if (".".equals(path) || path.isEmpty()) {
//                path = "";
//            } else if (!path.endsWith("/")) {
//                path += "/";
//            }
//
//            boolean dirSeen = false;
//            TreeSet<String> nameSet = new TreeSet<>();
//            ZipEntry zipEntry;
//            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
//                String zipEntryName = zipEntry.getName();
//                if (zipEntryName.startsWith(path)) {
//                    int i1 = path.length();
//                    int i2 = zipEntryName.indexOf('/', i1);
//                    String entryName;
//                    if (i2 == -1) {
//                        entryName = zipEntryName.substring(i1);
//                    } else {
//                        entryName = zipEntryName.substring(i1, i2);
//                    }
//                    if (!entryName.isEmpty() && !nameSet.contains(entryName)) {
//                        nameSet.add(entryName);
//                    }
//                    dirSeen = true;
//                }
//            }
//            if (!dirSeen) {
//                throw new FileNotFoundException(getBasePath() + "!" + path);
//            }
//            return nameSet.toArray(new String[nameSet.size()]);
//        }
//    }


    private static class ListAllFileZipEntriesFileVisitor extends SimpleFileVisitor<Path> {

        private final TreeSet<String> nameSet;

        private ListAllFileZipEntriesFileVisitor() {
            this.nameSet = new TreeSet<>();
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            this.nameSet.add(file.toString());
            return FileVisitResult.CONTINUE;
        }

        TreeSet<String> getNameSet() {
            return this.nameSet;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("ZipFileSystemBuilder");

//        String url = "vfs:/snap-products/files-to-test.zip";
//        String localFile = "D:/_test-extract-zip/files-to-test-downloaded.zip";

        String url = "vfs:/snap-products/_rapideye/Somalia_Mod.zip";
        String localFile = "D:/_test-extract-zip/Somalia_Mod-downloaded.zip";

        HttpFileSystemProvider httpFileSystemProvider = new HttpFileSystemProvider();
        Map<String, ?> connectionData = Collections.emptyMap();
        httpFileSystemProvider.setConnectionData("http://localhost", connectionData);
        URI uri = new URI("http", url, null);
        Path zipPath = httpFileSystemProvider.getPath(uri);
        //Path zipPath = Paths.get("C:\\Apache24\\htdocs\\snap-products\\files-to-test.zip");

        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(zipPath)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path localPath = Paths.get("D:/_test-extract-zip/extract/"+file.hashCode());
                        System.out.println("visitFile file="+file+"  size="+Files.size(file));

                        TransferFileContentUtil.copyFileUsingInputStream(file, localPath.toString());

                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }
//        Path localPath = Paths.get(localFile);
//        TransferFileContentUtil.copyFile(path, localPath);
    }
}
