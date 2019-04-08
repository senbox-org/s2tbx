package org.esa.s2tbx.dataio;

import com.sun.nio.zipfs.ZipFileSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by jcoravu on 4/4/2019.
 *
 * http://www.matjazcerkvenik.si/developer/java-download-file-via-http.php
 */
public class ExtractZipFileContentMain {

    public static void main(String[] args) throws Exception {
        System.out.println("ExtractZipFileContentMain");

//        String url = "http://localhost/snap-products/_rapideye/Somalia_Mod.zip";
//        String localFile = "D:/_test-extract-zip/Somalia_Mod-downloaded.zip";
        String url = "http://localhost/snap-products/files-to-test.zip";
        String localFile = "D:/_test-extract-zip/files-to-test-downloaded.zip";
        download(url, localFile);


////        URI zipURI = new URI("file", "D:/_test-extract-zip/Somalia_Mod.zip", null);
//////        Path zipPath = Paths.get("D:/_test-extract-zip/Somalia_Mod.zip");
//
//        Path zipPath = Paths.get("D:/_test-extract-zip/Somalia_Mod.zip");
////        Path zipPath = Paths.get("D:/_test-extract-zip/pleiades.ZiP");
//
//        String zipEntryPath = "02N045E-R2C1_2012_RE1_3B-3M_1234567890.tif";
//        Path outputFile = Paths.get("D:/_test-extract-zip").resolve(zipEntryPath);
//
//        Map<String,?> env = Collections.emptyMap();
//
//        // check installed providers
//        FileSystem fileSystem = ZipFileSystemBuilder.newZipFileSystem(zipPath);
//
//        for (FileSystemProvider provider: FileSystemProvider.installedProviders()) {
//            try {
//                fileSystem = provider.newFileSystem(zipPath, env);
//                break;
//            } catch (UnsupportedOperationException uoe) {
//                uoe.printStackTrace();
//            }
//        }
//
//        extractFile(zipPath, zipEntryPath, outputFile);
//
//        Path toDirectory = Paths.get("D:/_test-extract-zip/extract");
//        URI fromZip = zipPath.toUri();
//        extractAll(fromZip, toDirectory);
    }

//    private void extractAll(URI fromZip, Path toDirectory) throws IOException {
//        FileSystem fs = FileSystems.newFileSystem(fromZip, Collections.emptyMap());
//        fs.getRootDirectories()
//                .forEach(root -> {
//                    // in a full implementation, you'd have to handle directories
//                    Files.walk(root).forEach(path -> Files.copy(path, toDirectory));
//                });
//    }

    public static void extractFile(Path zipFile, String fileName, Path outputFile) throws IOException {
        // Wrap the file system in a try-with-resources statement
        // to auto-close it when finished and prevent a memory leak
        try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null)) {
            Iterator<Path> it = fileSystem.getRootDirectories().iterator();
            while (it.hasNext()) {
                Path root = it.next();
                System.out.println("root='"+root+"'");
                Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        System.out.println("visitFile='"+file+"'");
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        // In a full implementation, you'd need to create each
                        // sub-directory of the destination directory before
                        // copying files into it
                        return super.preVisitDirectory(dir, attrs);
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        System.out.println("  postVisitDirectory='"+dir+"'");
                        return super.postVisitDirectory(dir, exc);
                    }
                });
            }
//            Path fileToExtract = fileSystem.getPath(fileName);
//            Files.copy(fileToExtract, outputFile);
        }
    }

    private static void extractAll(URI fromZip, Path toDirectory) throws IOException {
        List<FileSystemProvider> installedProviders = FileSystemProvider.installedProviders();
        FileSystem zipFs = FileSystems.newFileSystem(fromZip, Collections.emptyMap());

        for(Path root : zipFs.getRootDirectories()) {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // You can do anything you want with the path here
                    Files.copy(file, toDirectory);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // In a full implementation, you'd need to create each
                    // sub-directory of the destination directory before
                    // copying files into it
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        }
    }

    // url=http://www.example.com/testFile.zip
// localFile=/path/to/testFile.zip
    public static void download(String url, String localFile) throws Exception {

        System.out.println("Downloading " + localFile);

        boolean downloadComplete = false;

        while (!downloadComplete) {
            downloadComplete = transferData(url, localFile);
        }

    }



    public static boolean transferData(String url, String localFile) throws Exception {

        long transferedSize = getFileSize(localFile);
        System.out.println("transferedSize: " + transferedSize);

        URL website = new URL(url);

        URLConnection connection1 = website.openConnection();
        long contentLength = connection1.getContentLengthLong();
        System.out.println("contentLength: " + contentLength);


        URLConnection connection = website.openConnection();
//        connection.setRequestProperty("Range", "bytes="+transferedSize+"-" + (contentLength-1));
        connection.setRequestProperty("Range", "bytes="+transferedSize+"-");
        ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
        long remainingSize = connection.getContentLengthLong();
        long buffer = remainingSize;
        if (remainingSize > 65536) {
            buffer = 1 << 16;
        }
        System.out.println("Remaining size: " + remainingSize);

        if (transferedSize == remainingSize) {
            System.out.println("File is complete");
            rbc.close();
            return true;
        }

        FileOutputStream fos = new FileOutputStream(localFile, true);

        System.out.println("Continue downloading at " + transferedSize);
        while (remainingSize > 0) {
            long delta = fos.getChannel().transferFrom(rbc, transferedSize, buffer);
            transferedSize += delta;
            System.out.println(transferedSize + " bytes received");
            if (delta == 0) {
                break;
            }
        }
        fos.close();
        System.out.println("Download incomplete, retrying");

        return false;

    }



    public static long getFileSize(String localFile) {
        File f = new File(localFile);
        return f.length();
    }
}
