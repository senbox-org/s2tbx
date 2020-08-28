package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.utils.TestUtil;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author Denisa Stefanescu
 */
public class Sentinel2L1BMetadataInspectorTest {
    private static final String SENTINEL2_DIR = "S2";

    private static final String L1B_PRODUCT_NAME = "L1B/Maricopa/S2A_OPER_PRD_MSIL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504.SAFE/S2A_OPER_MTD_SAFL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504.xml";

    @Before
    public void setup() {
        String productPath = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        Path sentinel2TestProductsPath = Paths.get(productPath, SENTINEL2_DIR);
        Assume.assumeTrue(Files.exists(sentinel2TestProductsPath));
    }

    @Test
    public void testSentinel2L1BMetadataInspector() throws IOException {
        File file = TestUtil.getTestFile(SENTINEL2_DIR + File.separator + L1B_PRODUCT_NAME);
        assertNotNull(file);

        Sentinel2L1BMetadataInspector metadataInspector = new Sentinel2L1BMetadataInspector(Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_MULTI);
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(2552, metadata.getProductWidth());
        assertEquals(4608, metadata.getProductHeight());

        assertNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(32, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("D11B2"));
        assertTrue(metadata.getBandList().contains("D11B11"));
        assertTrue(metadata.getBandList().contains("D11B10"));
        assertTrue(metadata.getBandList().contains("D12B8"));
        assertTrue(metadata.getBandList().contains("D12B5"));
        assertTrue(metadata.getBandList().contains("D12B1"));
        assertTrue(metadata.getBandList().contains("D11_tile_id_10m"));
        assertTrue(metadata.getBandList().contains("D12_tile_id_20m"));
        assertTrue(metadata.getBandList().contains("D11_tile_id_60m"));

        assertNotNull(metadata.getMaskList());
        assertEquals(12, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("tile_10m_d11_20160403t182500"));
        assertTrue(metadata.getMaskList().contains("tile_20m_d11_20160403t182504"));
        assertTrue(metadata.getMaskList().contains("tile_60m_d12_20160403t182456"));
    }

    @Test
    public void testSentinel2L1BMetadataInspector10m() throws IOException {
        File file = TestUtil.getTestFile(SENTINEL2_DIR + File.separator + L1B_PRODUCT_NAME);
        assertNotNull(file);

        Sentinel2L1BMetadataInspector metadataInspector = new Sentinel2L1BMetadataInspector(Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_10M);
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(2552, metadata.getProductWidth());
        assertEquals(4608, metadata.getProductHeight());

        assertNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(10, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("D11B2"));
        assertTrue(metadata.getBandList().contains("D12B3"));
        assertTrue(metadata.getBandList().contains("D11_tile_id_10m"));
        assertTrue(metadata.getBandList().contains("D12_tile_id_10m"));

        assertNotNull(metadata.getMaskList());
        assertEquals(4, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("tile_10m_d11_20160403t182500"));
        assertTrue(metadata.getMaskList().contains("tile_10m_d12_20160403t182456"));
    }

    @Test
    public void testSentinel2L1BMetadataInspector20m() throws IOException {
        File file = TestUtil.getTestFile(SENTINEL2_DIR + File.separator + L1B_PRODUCT_NAME);
        assertNotNull(file);

        Sentinel2L1BMetadataInspector metadataInspector = new Sentinel2L1BMetadataInspector(Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_20M);
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(1276, metadata.getProductWidth());
        assertEquals(2304, metadata.getProductHeight());

        assertNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(14, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("D11B11"));
        assertTrue(metadata.getBandList().contains("D12B8A"));
        assertTrue(metadata.getBandList().contains("D11_tile_id_20m"));
        assertTrue(metadata.getBandList().contains("D12_tile_id_20m"));

        assertNotNull(metadata.getMaskList());
        assertEquals(4, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("tile_20m_d11_20160403t182500"));
        assertTrue(metadata.getMaskList().contains("tile_20m_d12_20160403t182456"));
    }

    @Test
    public void testSentinel2L1BMetadataInspector60m() throws IOException {
        File file = TestUtil.getTestFile(SENTINEL2_DIR + File.separator + L1B_PRODUCT_NAME);
        assertNotNull(file);

        Sentinel2L1BMetadataInspector metadataInspector = new Sentinel2L1BMetadataInspector(Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_60M);
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(1276, metadata.getProductWidth());
        assertEquals(768, metadata.getProductHeight());

        assertNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(8, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("D11B10"));
        assertTrue(metadata.getBandList().contains("D12B9"));
        assertTrue(metadata.getBandList().contains("D11_tile_id_60m"));
        assertTrue(metadata.getBandList().contains("D12_tile_id_60m"));

        assertNotNull(metadata.getMaskList());
        assertEquals(4, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("tile_60m_d11_20160403t182500"));
        assertTrue(metadata.getMaskList().contains("tile_60m_d12_20160403t182456"));
    }
}
