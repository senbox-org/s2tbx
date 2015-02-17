package jp2;

import org.junit.Test;
import org.openjpeg.CommandOutput;
import org.openjpeg.JpegUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public class OpjDumpTest {
    @Test
    public void testRun1() throws URISyntaxException, IOException {
        String jp2Path = "/org/esa/beam/dataio/s2/l2a/S2A_USER_MSI_L2A_TL_MPS__20150210T180608_A000069_T14RMQ_B03_20m.jp2";

        final File file = new File(OpjDumpTest.class.getResource(jp2Path).toURI());
        try {
            CodeStreamUtils.getTileLayoutWithOpenJPEG("opj_dump.exe", file.toURI(), new AEmptyListener());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRun2() throws URISyntaxException, IOException {
        String jp2Path = "/org/esa/beam/dataio/s2/l2a/S2A_USER_MSI_L2A_TL_MPS__20150210T180608_A000069_T14RMQ_B03_20m.jp2";

        final File file = new File(OpjDumpTest.class.getResource(jp2Path).toURI());

        ProcessBuilder builder = new ProcessBuilder("opj_dump.exe", "-i", file.toURI().getPath().substring(1));

        try {
            CommandOutput cout = JpegUtils.runProcess(builder);
            assertTrue(cout.getTextOutput().contains("correctly decoded"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
