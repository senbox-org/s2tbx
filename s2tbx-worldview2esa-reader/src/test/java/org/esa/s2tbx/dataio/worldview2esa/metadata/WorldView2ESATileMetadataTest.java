package org.esa.s2tbx.dataio.worldview2esa.metadata;

import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

public class WorldView2ESATileMetadataTest {
    private TileMetadata metadata;
    private String productsFolder = "_worldView" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        XmlMetadataParserFactory.registerParser(TileMetadata.class, new XmlMetadataParser<>(TileMetadata.class));
        metadata = GenericXmlMetadata.create(TileMetadata.class, TestUtil.getTestFile(productsFolder +
                "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP"+ File.separator+ "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061"+File.separator+"053963700060_01_P001_MUL"+File.separator+"11MAY25095346-M2AS-053963700060_01_P001.XML"));
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
        assertEquals(4096, metadata.getRasterHeight());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(8192, metadata.getRasterWidth());
    }
    @Test
    public void testWorldView2TileComponent() throws Exception {
        Path path = TestUtil.getTestFile(productsFolder +
                "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP"+ File.separator+ "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061"+File.separator+"053963700060_01_P001_MUL"+File.separator+"11MAY25095346-M2AS-053963700060_01_P001.XML").toPath();
        try (InputStream inputStream = Files.newInputStream(path)) {
            metadata =  TileMetadata.create(new FilePathInputStream(path, inputStream, null));
        }
        assertNotNull(metadata.getTileComponent());
        assertEquals("Multi", metadata.getTileComponent().getBandID());
        assertEquals("N", metadata.getTileComponent().getMapHemisphere());
        assertEquals(16, metadata.getTileComponent().getBitsPerPixel());
        assertEquals(34, metadata.getTileComponent().getMapZone());
        assertEquals(4096, metadata.getTileComponent().getNumColumns());
        assertEquals(18, metadata.getTileComponent().getNumOfTiles());
        assertEquals(4096, metadata.getTileComponent().getNumRows());
        assertEquals(720104.80020902, metadata.getTileComponent().getOriginX(),0.e-5 );
        assertEquals(4931498.3999986, metadata.getTileComponent().getOriginY(), 0.e-6);
        assertEquals("11MAY25095346-M2AS_R1C1-053963700060_01_P001.TIF", metadata.getTileComponent().getTileNames()[0]);
        assertEquals(1.600000000000000, metadata.getTileComponent().getStepSize(), 0.e-0);
    }
}
