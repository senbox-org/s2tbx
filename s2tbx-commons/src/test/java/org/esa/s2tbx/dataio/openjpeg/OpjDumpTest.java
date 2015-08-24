package org.esa.s2tbx.dataio.openjpeg;

import org.esa.s2tbx.dataio.jp2.CodeStreamUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class OpjDumpTest {

    Path opjDumpPath;

    @Before
    public void retreiveOpjDump() {
        Path openjpegDirs = Paths.get("").resolve("lib-openjpeg").resolve("release").resolve("dependency");
        String endOfPath = OpenJpegExecRetriever.getInfoExtractor();
        opjDumpPath = openjpegDirs.resolve(endOfPath);
        Assume.assumeTrue(Files.exists(opjDumpPath));
    }

    @Test
    public void testRun1() throws URISyntaxException, IOException {
        String jp2Path = "/org/esa/s2tbx/dataio/s2/l2a/S2A_USER_MSI_L2A_TL_MPS__20150210T180608_A000069_T14RMQ_B03_20m.jp2";

        final File file = new File(OpjDumpTest.class.getResource(jp2Path).toURI());
        try {
            CodeStreamUtils.getTileLayoutWithOpenJPEG(opjDumpPath.toAbsolutePath().toString(), file.toURI());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRun2() throws URISyntaxException, IOException {
        String jp2Path = "/org/esa/s2tbx/dataio/s2/l2a/S2A_USER_MSI_L2A_TL_MPS__20150210T180608_A000069_T14RMQ_B03_20m.jp2";

        final File file = new File(OpjDumpTest.class.getResource(jp2Path).toURI());

        ProcessBuilder builder = new ProcessBuilder(opjDumpPath.toAbsolutePath().toString(), "-i", file.toURI().getPath().substring(1));

        try {
            CommandOutput cout = OpenJpegUtils.runProcess(builder);
            assertTrue(cout.getTextOutput().contains("correctly decoded"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
