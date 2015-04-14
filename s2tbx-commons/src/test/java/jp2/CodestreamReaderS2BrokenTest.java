package jp2;

import jp2.segments.CodingStyleDefaultSegment;
import jp2.segments.ImageAndTileSizeSegment;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static jp2.BoxReaderTest.*;
import static org.junit.Assert.*;

/**
 * @author Norman Fomferra
 */
public class CodestreamReaderS2BrokenTest {
    @Test
    public void testMarkerStructure() throws URISyntaxException, IOException {
        final BoxReader boxReader = openBoxReader("/org/esa/s2tbx/dataio/s2/l2a/S2A_USER_MSI_L2A_TL_MPS__20150210T180608_A000069_T14RMQ_B03_20m.jp2");

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
        assertEquals(5490, imageAndTileSizeSegment.getXsiz());
        assertEquals(5490, imageAndTileSizeSegment.getYsiz());
        assertEquals(5490, imageAndTileSizeSegment.getXtsiz());
        assertEquals(5490, imageAndTileSizeSegment.getYtsiz());

        double xTiles = Math.ceil((imageAndTileSizeSegment.getXsiz() - imageAndTileSizeSegment.getXosiz()) / (float) imageAndTileSizeSegment.getXtsiz());
        double yTiles = Math.ceil((imageAndTileSizeSegment.getYsiz() - imageAndTileSizeSegment.getYosiz()) / (float) imageAndTileSizeSegment.getYtsiz());

        assertEquals(1, xTiles, 0.01);
        assertEquals(1, yTiles, 0.01);

        int numTiles = (int) (xTiles * yTiles);

        assertEquals(1, numTiles);

        MarkerSegment seg3 = reader.readSegment();

        while(!(seg3 instanceof CodingStyleDefaultSegment))
        {
            MarkerType marker = seg3.getMarkerType();
            if(!marker.toString().contains("_"))
            {
                System.err.println(seg3.getMarkerType());
            }
            seg3 = reader.readSegment();
        }

        CodingStyleDefaultSegment roar = (CodingStyleDefaultSegment) seg3;
        assertNotEquals(12, roar.getLcod());
        assertNotEquals(1, roar.getLayers());
        assertNotEquals(0, roar.getOrder());
        assertNotEquals(6, roar.getLevels());


//        assertEquals(MarkerType.EOC, reader.readMarker());

    }

}
