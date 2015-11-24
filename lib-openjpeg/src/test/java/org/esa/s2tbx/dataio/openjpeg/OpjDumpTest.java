package org.esa.s2tbx.dataio.openjpeg;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OpjDumpTest {

    Path opjDumpPath;

    @Before
    public void retreiveOpjDump() {
        opjDumpPath = Paths.get(OpenJpegExecRetriever.getSafeInfoExtractorAndUpdatePermissions());
        Assume.assumeTrue(Files.exists(opjDumpPath));
    }

    @Test
    public void testGetTileLayoutFromOpjDump() throws URISyntaxException, IOException {
        String jp2Path = "/org/esa/s2tbx/dataio/s2/l2a/S2A_USER_MSI_L2A_TL_MPS__20150210T180608_A000069_T14RMQ_B03_20m.jp2";

        final Path pathToJP2File = Paths.get(OpjDumpTest.class.getResource(jp2Path).toURI());
        try {
            OpenJpegUtils.getTileLayoutWithOpenJPEG(opjDumpPath.toAbsolutePath().toString(), pathToJP2File);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testLaunchOpjDump() throws URISyntaxException, IOException {
        String jp2Path = "/org/esa/s2tbx/dataio/s2/l2a/S2A_USER_MSI_L2A_TL_MPS__20150210T180608_A000069_T14RMQ_B03_20m.jp2";

        String pathToJp2File = OpjDumpTest.class.getResource(jp2Path).getPath();

        if(IS_OS_WINDOWS) {
            pathToJp2File = pathToJp2File.substring(1);
        }

        ProcessBuilder builder = new ProcessBuilder(opjDumpPath.toAbsolutePath().toString(), "-i", pathToJp2File);

        try {

            CommandOutput cout = OpenJpegUtils.runProcess(builder);
            assertNotNull(cout);
            assertTrue("Wrong output: " + cout.getTextOutput(), cout.getTextOutput().contains("correctly decoded"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
