package jp2;

import jp2.segments.CodingStyleDefaultSegment;
import jp2.segments.ImageAndTileSizeSegment;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static jp2.BoxReaderTest.openBoxReader;
import static org.junit.Assert.*;

/**
 * @author Norman Fomferra
 */
public class CodestreamReaderS2Test {
    @Test
    public void testMarkerStructure() throws URISyntaxException, IOException {
        final BoxReader boxReader = openBoxReader("/org/esa/beam/dataio/s2/l1c/S2A_OPER_MSI_L1C_TL_CGS1_20130621T120000_A000065_T14SLD_B02.jp2");

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
        assertEquals(10980, imageAndTileSizeSegment.getXsiz());
        assertEquals(10980, imageAndTileSizeSegment.getYsiz());
        assertEquals(2048, imageAndTileSizeSegment.getXtsiz());
        assertEquals(2048, imageAndTileSizeSegment.getYtsiz());

        double xTiles = Math.ceil((imageAndTileSizeSegment.getXsiz() - imageAndTileSizeSegment.getXosiz()) / (float) imageAndTileSizeSegment.getXtsiz());
        double yTiles = Math.ceil((imageAndTileSizeSegment.getYsiz() - imageAndTileSizeSegment.getYosiz()) / (float) imageAndTileSizeSegment.getYtsiz());

        assertEquals(6, xTiles, 0.01);
        assertEquals(6, yTiles, 0.01);

        int numTiles = (int) (xTiles * yTiles);

        assertEquals(36, numTiles);

        final MarkerSegment seg3 = reader.readSegment();
        assertEquals(MarkerType.COD, seg3.getMarkerType());
        assertEquals(CodingStyleDefaultSegment.class, seg3.getClass());
        CodingStyleDefaultSegment roar = (CodingStyleDefaultSegment) seg3;
        assertEquals(12, roar.getLcod());
        assertEquals(1, roar.getLayers());
        assertEquals(0, roar.getOrder());
        assertEquals(5, roar.getLevels());


//        assertEquals(MarkerType.EOC, reader.readMarker());

    }

}
