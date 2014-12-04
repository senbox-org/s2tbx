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
public class BoxReaderTest {
    @Test
    public void testSentinel2L1cTile() throws URISyntaxException, IOException {
        final BoxReader boxReader = openBoxReader("/org/esa/beam/dataio/s2/l1c/IMG_GPPL1C_054_20091210235100_20091210235130_02_000000_15SUC.jp2");
        final Box box1 = boxReader.readBox();
        assertEquals("jP  ", box1.getSymbol());
        assertEquals(12, box1.getLength());
        assertEquals(218793738, ((Jpeg2000SignatureBox)box1).getSignature());

        final Box box2 = boxReader.readBox();
        assertEquals("ftyp", box2.getSymbol());
        assertEquals(20, box2.getLength());
        final FileTypeBox fileTypeBox = (FileTypeBox) box2;
        assertEquals(decode4b("jp2 "), fileTypeBox.getBr());
        assertEquals(0, fileTypeBox.getMinV());
        assertEquals("jp2 ", encode4b(fileTypeBox.getCl0()));

        final Box box3 = boxReader.readBox();
        assertEquals(decode4b("jp2h"), box3.getCode());
        assertEquals(45, box3.getLength());

        final Box box4 = boxReader.readBox();
        assertEquals("ihdr", box4.getSymbol());
        assertEquals(22, box4.getLength());
        final ImageHeaderBox imageHeaderBox = (ImageHeaderBox) box4;
        assertEquals(10960, imageHeaderBox.getHeight());
        assertEquals(10960, imageHeaderBox.getWidth());
        assertEquals(15, imageHeaderBox.getBpc());  // 16-bits
        assertEquals(1, imageHeaderBox.getNc());  // one component only
        assertEquals(7, imageHeaderBox.getC());
        assertEquals(1, imageHeaderBox.getUnkC());
        assertEquals(0, imageHeaderBox.getIpr());

        final Box box5 = boxReader.readBox();
        assertEquals("colr", box5.getSymbol());
        assertEquals(15, box5.getLength());
        final ColorSpecificationBox colorSpecificationBox = (ColorSpecificationBox) box5;
        assertEquals(1, colorSpecificationBox.getMeth ());
        assertEquals(0, colorSpecificationBox.getPrec());
        assertEquals(0, colorSpecificationBox.getApprox());
        assertEquals(17, colorSpecificationBox.getEnumCS());
        assertArrayEquals(new byte[0], colorSpecificationBox.getProfile());

        final Box box6 = boxReader.readBox();
        assertEquals("asoc", box6.getSymbol());
        assertEquals(1789, box6.getLength());
        assertEquals(IgnoredBox.class, box6.getClass());

        final Box box7 = boxReader.readBox();
        assertEquals("uuid", box7.getSymbol());
        assertEquals(374, box7.getLength());
        final UuidBox uuidBox = (UuidBox) box7;
        assertEquals("b14bf8bd-083d-4b43-a5ae-8cd7d5a6ce03", uuidBox.getUiid().toString());
        assertEquals(350, uuidBox.getData().length);

        final Box box8 = boxReader.readBox();
        assertEquals("jp2c", box8.getSymbol());
        assertEquals(14634208, box8.getLength());

        final Box box9 = boxReader.readBox();
        assertNull(box9);
    }


    @Test
    public void testIsoSpecPart1ConformanceFiles() throws IOException, URISyntaxException {
        test("images/conformance/Otoe_OrthoImage8.jp2", 968, 920, 635565);
        test("images/conformance/sekscir25.jp2", 10726, 9147, 11803226L);
        test("images/conformance/CB_TM_QQ432.jp2", 3164, 2982, 18759332L);
        test("images/conformance/CB_TM432.jp2", 361, 488,348981L);
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
        assertEquals(45, box3.getLength());

        final Box box4 = boxReader.readBox();
        assertEquals(decode4b("ihdr"), box4.getCode());
        assertEquals(22, box4.getLength());
        final ImageHeaderBox imageHeaderBox = (ImageHeaderBox) box4;
        assertEquals(height, imageHeaderBox.getHeight());
        assertEquals(width, imageHeaderBox.getWidth());
        assertEquals(7, imageHeaderBox.getBpc());
        assertEquals(3, imageHeaderBox.getNc());
        assertEquals(7, imageHeaderBox.getC());
        assertEquals(1, imageHeaderBox.getUnkC());
        assertEquals(0, imageHeaderBox.getIpr());

        final Box box5 = boxReader.readBox();
        assertEquals(decode4b("colr"), box5.getCode());
        assertEquals(15, box5.getLength());
        final ColorSpecificationBox colorSpecificationBox = (ColorSpecificationBox) box5;
        assertEquals(1, colorSpecificationBox.getMeth ());
        assertEquals(0, colorSpecificationBox.getPrec());
        assertEquals(0, colorSpecificationBox.getApprox());
        assertEquals(16, colorSpecificationBox.getEnumCS());
        assertArrayEquals(new byte[0], colorSpecificationBox.getProfile());

        final Box box6 = boxReader.readBox();
        assertEquals(decode4b("jp2c"), box6.getCode());
        assertEquals(codestreamBoxLength, box6.getLength());

        final Box box7 = boxReader.readBox();
        assertNull(box7);

//        assertEquals("jp2c", encode4b(1785737827));
//        assertEquals("asoc", encode4b(0x61736f63));
    }

     static BoxReader openBoxReader(String jp2Path) throws URISyntaxException, IOException {
        final File file = new File(BoxReaderTest.class.getResource(jp2Path).toURI());
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
