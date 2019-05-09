package org.esa.s2tbx.dataio.s2;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by obarrile on 22/12/2016.
 */
public class VirtualPathTest {

    @Test
    public void testZip() throws Exception {
        VirtualPath virtualPath = new VirtualPath(".", VirtualDirEx.build(getTestDataDir("VirtualDirTest.zip").toPath()));
        VirtualPath dir1 = virtualPath.resolve("dir1");
        VirtualPath virtualPathFile4 = new VirtualPath("dir1/File4",VirtualDirEx.build(getTestDataDir("VirtualDirTest.zip").toPath()));
        assertNotNull(virtualPath);
        try {
            String[] filesArray = virtualPath.list();
            assertNotNull(filesArray);
            List<String> allFiles = Arrays.asList(filesArray);

            assertEquals(5, allFiles.size());
            assertTrue(allFiles.contains("dir1"));
            assertTrue(allFiles.contains("dir2"));
            assertTrue(allFiles.contains("File1"));
            assertTrue(allFiles.contains("File2"));
            assertTrue(allFiles.contains("File5.gz"));


            String[] filesDir1Array = dir1.list();
            assertNotNull(filesDir1Array);
            List<String> allDir1Files = Arrays.asList(filesDir1Array);

            assertEquals(2, allDir1Files.size());
            assertTrue(dir1.existsAndHasChildren());
            assertTrue(allDir1Files.contains("File3"));
            assertTrue(allDir1Files.contains("File4"));

            assertNull(virtualPath.getParent());

            assertTrue(virtualPathFile4.exists());
            assertNotNull(virtualPathFile4.getParent().getParent());
            assertNotNull(virtualPathFile4.resolveSibling("File3"));
            assertTrue(virtualPathFile4.resolveSibling("File3").exists());
            assertFalse(virtualPathFile4.resolveSibling("File5").exists());

            assertFalse(virtualPathFile4.existsAndHasChildren());
            assertTrue(virtualPathFile4.getVirtualDir().isCompressed());

        } finally {
            virtualPath.close();
            virtualPathFile4.close();
        }
    }


    @Test
    public void testDir() throws Exception {
        VirtualPath virtualPath = new VirtualPath(".",VirtualDirEx.build(getTestDataDir("VirtualDirTest.dir").toPath()));
        VirtualPath dir1 = virtualPath.resolve("dir1");
        VirtualPath virtualPathFile4 = new VirtualPath("dir1/File4",VirtualDirEx.build(getTestDataDir("VirtualDirTest.dir").toPath()));
        assertNotNull(virtualPath);
        try {
            String[] filesArray = virtualPath.list();
            assertNotNull(filesArray);
            List<String> allFiles = Arrays.asList(filesArray);

            assertEquals(4, allFiles.size());
            assertTrue(allFiles.contains("dir1"));
            assertTrue(allFiles.contains("File1"));
            assertTrue(allFiles.contains("File2"));
            assertTrue(allFiles.contains("File5.gz"));


            String[] filesDir1Array = dir1.list();
            assertNotNull(filesDir1Array);
            List<String> allDir1Files = Arrays.asList(filesDir1Array);

            assertEquals(2, allDir1Files.size());
            assertTrue(dir1.existsAndHasChildren());
            assertTrue(allDir1Files.contains("File3"));
            assertTrue(allDir1Files.contains("File4"));

            assertTrue(virtualPathFile4.exists());
            assertNotNull(virtualPathFile4.getParent().getParent());
            assertNotNull(virtualPathFile4.resolveSibling("File3"));
            assertTrue(virtualPathFile4.resolveSibling("File3").exists());
            assertFalse(virtualPathFile4.resolveSibling("File5").exists());

            assertFalse(virtualPathFile4.existsAndHasChildren());
            assertFalse(virtualPathFile4.getVirtualDir().isCompressed());

        } finally {
            virtualPath.close();
            dir1.close();
            virtualPathFile4.close();
        }
    }

    private static File getTestDataDir() {
        File dir = new File("./src/test/data/");
        if (!dir.exists()) {
            dir = new File("./s2tbx-s2msi-reader/src/test/data/");
            if (!dir.exists()) {
                fail("Can't find my test data. Where is '" + dir + "'?");
            }
        }
        return dir;
    }

    private static File getTestDataDir(String path) {
        return new File(getTestDataDir(), path);
    }
}
