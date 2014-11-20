package jp2;

import jp2.boxes.*;
import org.junit.Test;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static jp2.BoxType.decode4b;
import static jp2.BoxType.encode4b;
import static org.junit.Assert.*;

/**
 * @author Norman Fomferra
 */
public class BoxReaderS2Test {

    @Test
    public void testIsoSpecPart1ConformanceFiles() throws IOException, URISyntaxException {
        test("/org/esa/beam/dataio/s2/l1c/S2A_OPER_MSI_L1C_TL_CGS1_20130621T120000_A000065_T14SLD_B02.jp2", 10980, 10980, 635565);
    }

    private void test(String jp2Path, int width, int height, long codestreamBoxLength) throws URISyntaxException, IOException {
        final BoxReader boxReader = openBoxReader(jp2Path);

        final Box box1 = boxReader.readBox();
        assertEquals(decode4b("jP  "), box1.getCode());
        assertEquals(12, box1.getLength());
        assertEquals(0x0D0A870A, ((Jpeg2000SignatureBox)box1).getSignature());

        final Box box2 = boxReader.readBox();
        assertEquals(decode4b("ftyp"), box2.getCode());
        assertEquals(20, box2.getLength());
        final FileTypeBox fileTypeBox = (FileTypeBox) box2;
        assertEquals(decode4b("jp2 "), fileTypeBox.getBr());
        assertEquals(0, fileTypeBox.getMinV());
        assertEquals("jp2 ", encode4b(fileTypeBox.getCl0()));

        final Box box3 = boxReader.readBox();
        assertEquals(decode4b("jp2h"), box3.getCode());

        final Box box4 = boxReader.readBox();
        assertEquals(decode4b("ihdr"), box4.getCode());
        assertEquals(22, box4.getLength());
        final ImageHeaderBox imageHeaderBox = (ImageHeaderBox) box4;
        assertEquals(height, imageHeaderBox.getHeight());
        assertEquals(width, imageHeaderBox.getWidth());

    }

     static BoxReader openBoxReader(String jp2Path) throws URISyntaxException, IOException {
        final File file = new File(BoxReaderS2Test.class.getResource(jp2Path).toURI());
        final FileImageInputStream stream = new FileImageInputStream(file);
        return new BoxReader(stream, file.length(), new MyListener());
    }


    private static class MyListener implements BoxReader.Listener {
        @Override
        public void knownBoxSeen(Box box) {
            System.out.println("known box: " + encode4b(box.getCode()));
        }

        @Override
        public void unknownBoxSeen(Box box) {
            System.out.println("unknown box: " + encode4b(box.getCode()));
        }
    }
}
