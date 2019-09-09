package org.esa.s2tbx.lib.openjpeg;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by jcoravu on 7/6/2019.
 */
public class JP2FileReaderTest {

    @Test
    public void testReadFileHeader() throws Exception {
        File testJP2File = JP2FileReaderTest.getTestDataDir("space.jp2");
        assertNotNull(testJP2File);

        Path filePath = testJP2File.toPath();
        assertNotNull(filePath);

        assertTrue("The input test file '"+filePath.toString()+"' does not exist.", Files.exists(filePath));

        JP2FileReader jp2FileReader = new JP2FileReader();
        jp2FileReader.readFileFormat(filePath, 1024, true);

        List<String> xmlMetadata = jp2FileReader.getXmlMetadata();

        assertNotNull(xmlMetadata);
        assertEquals(2, xmlMetadata.size());

        String firstXML = xmlMetadata.get(0);
        assertNotNull(firstXML);
        assertEquals(377, firstXML.length());

        String secondXML = xmlMetadata.get(1);
        assertNotNull(secondXML);
        assertEquals(902, secondXML.length());

        ContiguousCodestreamBox contiguousCodestreamBox = jp2FileReader.getHeaderDecoder();
        assertNotNull(contiguousCodestreamBox);

        SIZMarkerSegment siz = contiguousCodestreamBox.getSiz();
        assertNotNull(siz);
        assertEquals(1, siz.computeNumTilesX());
        assertEquals(1, siz.computeNumTilesY());
        assertEquals(1, siz.computeNumTiles());
        assertEquals(400, siz.getImageHeight());
        assertEquals(700, siz.getImageWidth());
        assertEquals(0, siz.getImageLeftX());
        assertEquals(0, siz.getImageTopY());
        assertEquals(0, siz.getTileLeftX());
        assertEquals(0, siz.getTileTopY());
        assertEquals(1, siz.getNumComps());
        assertEquals(8, siz.getComponentOriginBitDepthAt(0));
        assertEquals(400, siz.getCompImgHeight(0));
        assertEquals(700, siz.getCompImgWidth(0));
        assertEquals(1, siz.getComponentDxAt(0));
        assertEquals(1, siz.getComponentDyAt(0));
        assertEquals(false, siz.isComponentOriginSignedAt(0));

        CODMarkerSegment cod = contiguousCodestreamBox.getCod();
        assertNotNull(cod);
        assertEquals(0, cod.getMultipleComponenTransform());
        assertEquals(6, cod.getCodeBlockCount());
        assertEquals(64, cod.getCodeBlockHeight());
        assertEquals(64, cod.getCodeBlockWidth());
        assertEquals(0, cod.getCodeBlockStyle());
        assertEquals(0, cod.getCodingStyle());
        assertEquals(1, cod.getNumberOfLayers());
        assertEquals(0, cod.getProgressiveOrder());
        assertEquals(1, cod.getQmfbid());
        assertEquals(15, cod.getCodeBlockHeightExponentOffset(0));
        assertEquals(15, cod.getCodeBlockWidthExponentOffset(0));

        QCDMarkerSegment qcd = contiguousCodestreamBox.getQcd();
        assertNotNull(qcd);
        assertEquals(2, qcd.getNumGuardBits());
        assertEquals(0, qcd.getQuantizationType());
        assertEquals(6, qcd.getResolutionLevels());
        assertEquals(4, qcd.getSubbandsAtResolutionLevel(0));
        assertEquals(0, qcd.computeExponent(0, 0));
        assertEquals(64, qcd.computeMantissa(0, 0));
        assertEquals(8, qcd.computeNoQuantizationExponent(0, 0));

        RGNMarkerSegment rgn = contiguousCodestreamBox.getRgn();
        assertNull(rgn);
    }

    private static File getTestDataDir() {
        File dir = new File("./src/test/data/");
        if (!dir.exists()) {
            dir = new File("./lib-openjpeg/src/test/data/");
            if (!dir.exists()) {
                fail("Can't find my test data. Where is '" + dir + "'?");
            }
        }
        return dir;
    }

    public static File getTestDataDir(String path) {
        return new File(getTestDataDir(), path);
    }
}
