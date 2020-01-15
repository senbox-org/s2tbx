package org.esa.s2tbx.dataio.worldview2.metadata;

import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

/**
 * Unit test class for worldView2 Metadata tiles
 *
 * @author Razvan Dumitrascu
 */
public class WorldView2TileMetadataTest {
    private TileMetadata metadata;
    private String productsFolder = "_worldView" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        XmlMetadataParserFactory.registerParser(TileMetadata.class, new XmlMetadataParser<>(TileMetadata.class));
        metadata = GenericXmlMetadata.create(TileMetadata.class, TestUtil.getTestFile(productsFolder +
                "ZON24_I200862_FL01-P369685/ZON24_MUL/17MAY02091354-M2AS-ZON24.XML"));
    }

    @After
    public void teardown() {
        metadata = null;
        System.gc();
    }
    @Test
    public void testGetFileName() throws Exception {
        assertEquals("Metadata", metadata.getFileName());
    }

    @Test
    public void testGetProductDescription() throws Exception {
        assertEquals("Metadata", metadata.getProductDescription());
    }

    @Test
    public void testGetNumBands() throws Exception {
        assertEquals(0, metadata.getNumBands());
    }

    @Test
    public void testGetRasterHeight() throws Exception {
        assertEquals(0, metadata.getRasterHeight());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(0, metadata.getRasterWidth());
    }
    @Test
    public void testWorldView2TileComponent() throws Exception {
        metadata =  TileMetadata.create(TestUtil.getTestFile(productsFolder +
                "ZON24_I200862_FL01-P369685/ZON24_MUL/17MAY02091354-M2AS-ZON24.XML").toPath());
        assertNotNull(metadata.getTileComponent());
        assertEquals("MS1", metadata.getTileComponent().getBandID());
        assertEquals("N", metadata.getTileComponent().getMapHemisphere());
        assertEquals(16, metadata.getTileComponent().getBitsPerPixel());
        assertEquals(2461, metadata.getTileComponent().getLowerLeftColumnOffset()[0]);
        assertEquals(5019, metadata.getTileComponent().getLowerLeftRowOffset()[0]);
        assertEquals(0, metadata.getTileComponent().getLowerRightColumnOffset()[0]);
        assertEquals(5019, metadata.getTileComponent().getLowerRightRowOffset()[0]);
        assertEquals(35, metadata.getTileComponent().getMapZone());
        assertEquals(2462, metadata.getTileComponent().getNumColumns());
        assertEquals(1, metadata.getTileComponent().getNumOfTiles());
        assertEquals(5020, metadata.getTileComponent().getNumRows());
        assertEquals(397204.99999479, metadata.getTileComponent().getOriginX(),0.e-6 );
        assertEquals(6162998.99999922, metadata.getTileComponent().getOriginY(), 0.e-6);
        assertEquals("17MAY02091354-M2AS-ZON24.TIF", metadata.getTileComponent().getTileNames()[0]);
        assertEquals(2.0, metadata.getTileComponent().getStepSize(), 0.e-6);
        assertEquals(0, metadata.getTileComponent().getUpperLeftColumnOffset()[0]);
        assertEquals(0, metadata.getTileComponent().getUpperLeftRowOffset()[0]);
        assertEquals(2461, metadata.getTileComponent().getUpperRightColumnOffset()[0]);
        assertEquals(0, metadata.getTileComponent().getUpperRightRowOffset()[0]);


    }

}
