package org.esa.s2tbx.commons;

import com.sun.nio.zipfs.ZipFileSystem;
import com.sun.nio.zipfs.ZipFileSystemProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Map;

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

//    public static void main(String[] args) throws Exception {
//        System.out.println("ZipFileSystemBuilder");
//
////        String url = "vfs:/snap-products/files-to-test.zip";
////        String localFile = "D:/_test-extract-zip/files-to-test-downloaded.zip";
//
////        String url = "vfs:/snap-products/_rapideye/Somalia_Mod.zip";
////        String localFile = "D:/_test-extract-zip/Somalia_Mod-downloaded.zip";
//
//        String url = "vfs:/snap-products/JP2/IMG_PHR1A_PMS_201511151132244_ORT_2025121101-001_R1C1.JP2";
//        String localFile = "D:/_test-extract-zip/IMG_PHR1A_PMS_201511151132244_ORT_2025121101-001_R1C1.JP2";
//
//        HttpFileSystemProvider httpFileSystemProvider = new HttpFileSystemProvider();
//        Map<String, ?> connectionData = Collections.emptyMap();
//        httpFileSystemProvider.setConnectionData("http://localhost", connectionData);
//        URI uri = new URI("http", url, null);
//        Path zipPath = httpFileSystemProvider.getPath(uri);
//        //Path zipPath = Paths.get("C:\\Apache24\\htdocs\\snap-products\\files-to-test.zip");
//
////        try (FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(zipPath)) {
////            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
////            while (it.hasNext()) {
////                Path root = it.next();
////                Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
////                    @Override
////                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
////                        Path localPath = Paths.get("D:/_test-extract-zip/extract/"+file.hashCode());
////                        System.out.println("visitFile file="+file+"  size="+Files.size(file));
////
////                        FileHelper.copyFileUsingInputStream(file, localPath.toString());
////
////                        return FileVisitResult.CONTINUE;
////                    }
////                });
////            }
////        }
//
//        Path localPath = Paths.get(localFile);
//        long startTime1 = System.currentTimeMillis();
//        FileHelper.copyFileUsingFileChannel(zipPath, localPath.toString());
//        long endTime1 = System.currentTimeMillis();
//        double second1 = (endTime1 - startTime1) / 1000;
//        System.out.println("copyFileUsingFileChannel time="+second1+" seconds");
//
//        String localFile2 = "D:/_test-extract-zip/IMG_PHR1A_PMS_input-stream.JP2";
//        Path localPath2 = Paths.get(localFile2);
//        long startTime2 = System.currentTimeMillis();
//        FileHelper.copyFileUsingInputStream(zipPath, localPath2.toString());
//        long endTime2 = System.currentTimeMillis();
//        double second2 = (endTime2 - startTime2) / 1000;
//        System.out.println("copyFileUsingInputStream time="+second2+" seconds");
//    }
}
