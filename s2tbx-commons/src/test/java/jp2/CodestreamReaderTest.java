package jp2;

import jp2.segments.CodingStyleDefaultSegment;
import jp2.segments.ImageAndTileSizeSegment;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static jp2.BoxReaderTest.openBoxReader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Norman Fomferra
 */
public class CodestreamReaderTest {
    @Test
    public void testMarkerStructure() throws URISyntaxException, IOException {
        final BoxReader boxReader = openBoxReader("/org/esa/beam/dataio/s2/l1c/IMG_GPPL1C_054_20091210235100_20091210235130_02_000000_15SUC.jp2");

        Box box;
        do {
            box =  boxReader.readBox();
            if (box == null) {
                fail();
            }
        } while (!box.getSymbol().equals("jp2c"));

        assertNotNull(box);

        boxReader.getStream().seek(box.getPosition() + box.getDataOffset());
        final CodestreamReader reader = new CodestreamReader(boxReader.getStream(),
                                                             box.getPosition() + box.getDataOffset(),
                                                             box.getLength() - box.getDataOffset());
        final MarkerSegment seg1 = reader.readSegment();
        assertEquals(MarkerType.SOC, seg1.getMarkerType());
        assertEquals(Marker.class, seg1.getClass());

        final MarkerSegment seg2 = reader.readSegment();
        assertEquals(MarkerType.SIZ, seg2.getMarkerType());
        assertEquals(ImageAndTileSizeSegment.class, seg2.getClass());
        final ImageAndTileSizeSegment imageAndTileSizeSegment = (ImageAndTileSizeSegment) seg2;
        assertEquals(41, imageAndTileSizeSegment.getLsiz());
        assertEquals(0, imageAndTileSizeSegment.getRsiz());
        assertEquals(10960, imageAndTileSizeSegment.getXsiz());
        assertEquals(10960, imageAndTileSizeSegment.getYsiz());
        assertEquals(4096, imageAndTileSizeSegment.getXtsiz());
        assertEquals(4096, imageAndTileSizeSegment.getYtsiz());

        final MarkerSegment seg3 = reader.readSegment();
        assertEquals(MarkerType.COD, seg3.getMarkerType());
        assertEquals(CodingStyleDefaultSegment.class, seg3.getClass());
        CodingStyleDefaultSegment roar = (CodingStyleDefaultSegment) seg3;
        assertEquals(18, roar.getLcod());
        assertEquals(1, roar.getOrder());
        assertEquals(12, roar.getLayers());
        assertEquals(5, roar.getLevels());



//        assertEquals(MarkerType.EOC, reader.readMarker());

    }

}
