package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.runtime.Engine;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * @author Denisa Stefanescu
 */
public class Sentinel2L1BProductReaderTest {

    private Engine engine;

    private Path sentinel2TestProductsPath;

    private static final String SENTINEL2_DIR = "S2";

    private static final String L1B_PRODUCT_NAME = "L1B/Maricopa/S2A_OPER_PRD_MSIL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504.SAFE/S2A_OPER_MTD_SAFL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504.xml";

    @BeforeClass
    public static void setupLogger() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Before
    public void setup() {
        /*
         * We need a proper Engine start so that the openjpeg activator is started
         */
        engine = Engine.start(false);

        /**
         * Run these tests only if Sentinel 2 products test directory exists and is set
         */
        String productPath = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        sentinel2TestProductsPath = Paths.get(productPath, SENTINEL2_DIR);
        assumeTrue(Files.exists(sentinel2TestProductsPath));
    }

    @After
    public void teardown() {
        if (engine != null) {
            engine.stop();
        }
    }

    @Test
    public void testReadProductSubset10m(){
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_10M);
        try {
            Rectangle subsetRegion = new Rectangle(238, 867, 1956, 1854);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"D11B2", "D12B8", "D11_tile_id_10m", "tile_10m_d11_20160403t182500", "tile_10m_d12_20160403t182500"});
            subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
            subsetDef.setSubSampling(1, 1);
            subsetDef.setIgnoreMetadata(true);

            Product finalProduct = productReader.readProductNodes(productPath, subsetDef);

            assertNull(finalProduct.getSceneGeoCoding());

            assertEquals(3, finalProduct.getBands().length);
            assertEquals("S2_MSI_Level-1B", finalProduct.getProductType());
            assertNull(finalProduct.getMetadataRoot().getElementGroup());
            assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(1956, finalProduct.getSceneRasterWidth());
            assertEquals(1854, finalProduct.getSceneRasterHeight());
            assertEquals("03-APR-2016 18:24:56.765302", finalProduct.getStartTime().toString());
            assertEquals("03-APR-2016 18:25:04.449044", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("S2A_OPER_PRD_MSIL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Mask mask = finalProduct.getMaskGroup().get("tile_10m_d11_20160403t182500");
            assertEquals(1956, mask.getRasterWidth());
            assertEquals(1854, mask.getRasterHeight());

            Band band_D11B2 = finalProduct.getBand("D11B2");
            assertEquals(1956, band_D11B2.getRasterWidth());
            assertEquals(1854, band_D11B2.getRasterHeight());

            float pixelValue = band_D11B2.getSampleFloat(298, 616);
            assertEquals(350.0f, pixelValue, 0);
            pixelValue = band_D11B2.getSampleFloat(757, 256);
            assertEquals(185.0f, pixelValue, 0);
            pixelValue = band_D11B2.getSampleFloat(1529, 934);
            assertEquals(327.0f, pixelValue, 0);
            pixelValue = band_D11B2.getSampleFloat(1365, 983);
            assertEquals(263.0f, pixelValue, 0);
            pixelValue = band_D11B2.getSampleFloat(1832, 784);
            assertEquals(324.0f, pixelValue, 0);

            Band band_D12B8 = finalProduct.getBand("D12B8");
            assertEquals(1956, band_D12B8.getRasterWidth());
            assertEquals(1854, band_D12B8.getRasterHeight());

            pixelValue = band_D12B8.getSampleFloat(298, 616);
            assertEquals(786.0f, pixelValue, 0);
            pixelValue = band_D12B8.getSampleFloat(757, 256);
            assertEquals(527.0f, pixelValue, 0);
            pixelValue = band_D12B8.getSampleFloat(1529, 934);
            assertEquals(555.0f, pixelValue, 0);
            pixelValue = band_D12B8.getSampleFloat(1365, 983);
            assertEquals(399.0f, pixelValue, 0);
            pixelValue = band_D12B8.getSampleFloat(1832, 784);
            assertEquals(632.0f, pixelValue, 0);

            Band band_D11_tile = finalProduct.getBand("D11_tile_id_10m");
            assertEquals(1956, band_D11_tile.getRasterWidth());
            assertEquals(1854, band_D11_tile.getRasterHeight());

            pixelValue = band_D11_tile.getSampleFloat(1195, 628);
            assertEquals(1.0f, pixelValue, 0);
            pixelValue = band_D11_tile.getSampleFloat(423, 1701);
            assertEquals(2.0f, pixelValue, 0);
        }catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductSubset20m(){
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_20M);
        try {
            Rectangle subsetRegion = new Rectangle(232, 1168, 921, 833);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"D11B11", "D12B5", "tile_20m_d11_20160403t182500", "tile_20m_d12_20160403t182500"});
            subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
            subsetDef.setSubSampling(1, 1);
            subsetDef.setIgnoreMetadata(true);

            Product finalProduct = productReader.readProductNodes(productPath, subsetDef);

            assertNull(finalProduct.getSceneGeoCoding());

            assertEquals(2, finalProduct.getBands().length);
            assertEquals("S2_MSI_Level-1B", finalProduct.getProductType());
            assertNull(finalProduct.getMetadataRoot().getElementGroup());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(921, finalProduct.getSceneRasterWidth());
            assertEquals(833, finalProduct.getSceneRasterHeight());
            assertEquals("03-APR-2016 18:24:56.765302", finalProduct.getStartTime().toString());
            assertEquals("03-APR-2016 18:25:04.449044", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("S2A_OPER_PRD_MSIL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Band band_D11B11 = finalProduct.getBand("D11B11");
            assertEquals(921, band_D11B11.getRasterWidth());
            assertEquals(833, band_D11B11.getRasterHeight());

            float pixelValue = band_D11B11.getSampleFloat(78, 63);
            assertEquals(785.0f, pixelValue, 0);
            pixelValue = band_D11B11.getSampleFloat(151, 731);
            assertEquals(723.0f, pixelValue, 0);
            pixelValue = band_D11B11.getSampleFloat(112, 785);
            assertEquals(801.0f, pixelValue, 0);
            pixelValue = band_D11B11.getSampleFloat(648, 433);
            assertEquals(499.0f, pixelValue, 0);
            pixelValue = band_D11B11.getSampleFloat(887, 138);
            assertEquals(1020.0f, pixelValue, 0);

            Band band_D12B5 = finalProduct.getBand("D12B5");
            assertEquals(921, band_D12B5.getRasterWidth());
            assertEquals(833, band_D12B5.getRasterHeight());

            pixelValue = band_D12B5.getSampleFloat(78, 63);
            assertEquals(535.0f, pixelValue, 0);
            pixelValue = band_D12B5.getSampleFloat(151, 731);
            assertEquals(493.0f, pixelValue, 0);
            pixelValue = band_D12B5.getSampleFloat(112, 785);
            assertEquals(480.0f, pixelValue, 0);
            pixelValue = band_D12B5.getSampleFloat(648, 433);
            assertEquals(461.0f, pixelValue, 0);
            pixelValue = band_D12B5.getSampleFloat(887, 138);
            assertEquals(715.0f, pixelValue, 0);
        }catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductSubset60m(){
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_60M);
        try {
            Rectangle subsetRegion = new Rectangle(264, 176, 673, 672);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"D11B10", "D12B1", "D12_tile_id_60m", "tile_60m_d11_20160403t182500", "tile_60m_d12_20160403t182500"});
            subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
            subsetDef.setSubSampling(1, 1);

            Product finalProduct = productReader.readProductNodes(productPath, subsetDef);

            assertNull(finalProduct.getSceneGeoCoding());

            assertEquals(3, finalProduct.getBands().length);
            assertEquals("S2_MSI_Level-1B", finalProduct.getProductType());
            assertNotNull(finalProduct.getMetadataRoot().getElementGroup());
            assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(673, finalProduct.getSceneRasterWidth());
            assertEquals(672, finalProduct.getSceneRasterHeight());
            assertEquals("03-APR-2016 18:24:56.765302", finalProduct.getStartTime().toString());
            assertEquals("03-APR-2016 18:25:04.449044", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("S2A_OPER_PRD_MSIL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Mask mask = finalProduct.getMaskGroup().get("tile_60m_d12_20160403t182500");
            assertEquals(673, mask.getRasterWidth());
            assertEquals(672, mask.getRasterHeight());

            Band band_D11B10 = finalProduct.getBand("D11B10");
            assertEquals(673, band_D11B10.getRasterWidth());
            assertEquals(672, band_D11B10.getRasterHeight());

            float pixelValue = band_D11B10.getSampleFloat(117, 204);
            assertEquals(12.0f, pixelValue, 0);
            pixelValue = band_D11B10.getSampleFloat(345, 48);
            assertEquals(14.0f, pixelValue, 0);
            pixelValue = band_D11B10.getSampleFloat(281, 335);
            assertEquals(13.0f, pixelValue, 0);
            pixelValue = band_D11B10.getSampleFloat(575, 190);
            assertEquals(10.0f, pixelValue, 0);
            pixelValue = band_D11B10.getSampleFloat(464, 294);
            assertEquals(15.0f, pixelValue, 0);

            Band band_D12B1 = finalProduct.getBand("D12B1");
            assertEquals(673, band_D12B1.getRasterWidth());
            assertEquals(672, band_D12B1.getRasterHeight());

            pixelValue = band_D12B1.getSampleFloat(117, 204);
            assertEquals(391.0f, pixelValue, 0);
            pixelValue = band_D12B1.getSampleFloat(345, 48);
            assertEquals(356.0f, pixelValue, 0);
            pixelValue = band_D12B1.getSampleFloat(281, 335);
            assertEquals(267.0f, pixelValue, 0);
            pixelValue = band_D12B1.getSampleFloat(575, 190);
            assertEquals(303.0f, pixelValue, 0);
            pixelValue = band_D12B1.getSampleFloat(464, 294);
            assertEquals(401.0f, pixelValue, 0);

            Band band_D12_tile = finalProduct.getBand("D12_tile_id_60m");
            assertEquals(673, band_D12_tile.getRasterWidth());
            assertEquals(672, band_D12_tile.getRasterHeight());

            pixelValue = band_D12_tile.getSampleFloat(345, 48);
            assertEquals(1.0f, pixelValue, 0);
            pixelValue = band_D12_tile.getSampleFloat(281, 335);
            assertEquals(2.0f, pixelValue, 0);
        }catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductSubset(){
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_MULTI);
        try {
            Rectangle subsetRegion = new Rectangle(264, 176, 673, 672);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"D11B2", "D11B11", "D11B10", "D12B8", "D12B5", "D12B1","tile_10m_d11_20160403t182500", "tile_60m_d12_20160403t182500"});
            subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
            subsetDef.setSubSampling(1, 1);

            Product finalProduct = productReader.readProductNodes(productPath, subsetDef);

            assertNull(finalProduct.getSceneGeoCoding());

            assertEquals(6, finalProduct.getBands().length);
            assertEquals("S2_MSI_Level-1B", finalProduct.getProductType());
            assertNotNull(finalProduct.getMetadataRoot().getElementGroup());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(673, finalProduct.getSceneRasterWidth());
            assertEquals(672, finalProduct.getSceneRasterHeight());
            assertEquals("03-APR-2016 18:24:56.765302", finalProduct.getStartTime().toString());
            assertEquals("03-APR-2016 18:25:04.449044", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("S2A_OPER_PRD_MSIL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Band band_D11B2 = finalProduct.getBand("D11B2");
            assertEquals(673, band_D11B2.getRasterWidth());
            assertEquals(672, band_D11B2.getRasterHeight());

            float pixelValue = band_D11B2.getSampleFloat(117, 204);
            assertEquals(295.0f, pixelValue, 0);
            pixelValue = band_D11B2.getSampleFloat(345, 48);
            assertEquals(307.0f, pixelValue, 0);

            Band band_D11B11 = finalProduct.getBand("D11B11");
            assertEquals(336, band_D11B11.getRasterWidth());
            assertEquals(336, band_D11B11.getRasterHeight());

            pixelValue = band_D11B11.getSampleFloat(117, 204);
            assertEquals(675.0f, pixelValue, 0);
            pixelValue = band_D11B11.getSampleFloat(281, 335);
            assertEquals(836.0f, pixelValue, 0);

            Band band_D11B10 = finalProduct.getBand("D11B10");
            assertEquals(336, band_D11B10.getRasterWidth());
            assertEquals(112, band_D11B10.getRasterHeight());

            pixelValue = band_D11B10.getSampleFloat(117, 48);
            assertEquals(12.0f, pixelValue, 0);
            pixelValue = band_D11B10.getSampleFloat(281, 100);
            assertEquals(15.0f, pixelValue, 0);

            Band band_D12B8 = finalProduct.getBand("D12B8");
            assertEquals(673, band_D12B8.getRasterWidth());
            assertEquals(672, band_D12B8.getRasterHeight());

            pixelValue = band_D12B8.getSampleFloat(345, 48);
            assertEquals(634.0f, pixelValue, 0);
            pixelValue = band_D12B8.getSampleFloat(281, 335);
            assertEquals(419.0f, pixelValue, 0);

            Band band_D12B5 = finalProduct.getBand("D12B5");
            assertEquals(336, band_D12B5.getRasterWidth());
            assertEquals(336, band_D12B5.getRasterHeight());

            pixelValue = band_D12B5.getSampleFloat(320, 48);
            assertEquals(641.0f, pixelValue, 0);
            pixelValue = band_D12B5.getSampleFloat(281, 335);
            assertEquals(560.0f, pixelValue, 0);

            Band band_D12B1 = finalProduct.getBand("D12B1");
            assertEquals(336, band_D12B1.getRasterWidth());
            assertEquals(112, band_D12B1.getRasterHeight());

            pixelValue = band_D12B1.getSampleFloat(320, 48);
            assertEquals(435.0f, pixelValue, 0);
            pixelValue = band_D12B1.getSampleFloat(281, 99);
            assertEquals(381.0f, pixelValue, 0);
        }catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }
}
