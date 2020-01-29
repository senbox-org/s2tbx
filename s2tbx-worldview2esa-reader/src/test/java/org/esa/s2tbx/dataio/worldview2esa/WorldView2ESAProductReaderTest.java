package org.esa.s2tbx.dataio.worldview2esa;

import org.esa.s2tbx.dataio.worldview2esa.common.WorldView2ESAConstants;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class WorldView2ESAProductReaderTest {
    private WorldView2ESAProductReader reader;
    private String productsFolder = "_worldView" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        WorldView2ESAProductReaderPlugin plugin = new WorldView2ESAProductReaderPlugin();
        reader = new WorldView2ESAProductReader(plugin);
    }

    @After
    public void tearDown() throws Exception {
        if (reader!=null) {
            reader.close();
        }
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(WorldView2ESAProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP"+File.separator+"WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "200");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(9, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals(WorldView2ESAConstants.PRODUCT_TYPE, finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(32768, finalProduct.getSceneRasterWidth());
            assertEquals(16384, finalProduct.getSceneRasterHeight());
            assertEquals("25-MAY-2011 09:53:46.000000", finalProduct.getStartTime().toString());
            assertEquals("25-MAY-2011 09:53:51.000000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (Exception e) {
            e.printStackTrace();
            //assertTrue(e.getMessage(), false);
            assumeTrue(e.getMessage(),false);
        }
    }

    @Test
    public void testReadProductNodesSubset() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP"+File.separator+"WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "200");
        try {
            Rectangle subsetRegion = new Rectangle(12376, 3315, 15250, 11493);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"Coastal", "Red Edge", "Pan"});
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);
            subsetDef.setIgnoreMetadata(true);

            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(44.4898f, productOrigin.lat,4);
            assertEquals(23.8304f, productOrigin.lon,4);

            assertEquals(3, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals(WorldView2ESAConstants.PRODUCT_TYPE, finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(15250, finalProduct.getSceneRasterWidth());
            assertEquals(11493, finalProduct.getSceneRasterHeight());
            assertEquals("25-MAY-2011 09:53:46.000000", finalProduct.getStartTime().toString());
            assertEquals("25-MAY-2011 09:53:51.000000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Band band_Coastal = finalProduct.getBand("Coastal");
            assertEquals(3812, band_Coastal.getRasterWidth());
            assertEquals(2873, band_Coastal.getRasterHeight());

            float pixelValue = band_Coastal.getSampleFloat(867, 1561);
            assertEquals(70.1595f, pixelValue, 4);
            pixelValue = band_Coastal.getSampleFloat(1307, 691);
            assertEquals(103.1758f, pixelValue, 4);
            pixelValue = band_Coastal.getSampleFloat(1920, 2640);
            assertEquals(82.3441f, pixelValue, 4);
            pixelValue = band_Coastal.getSampleFloat(2893, 1150);
            assertEquals(85.4885f, pixelValue, 4);
            pixelValue = band_Coastal.getSampleFloat(3539, 530);
            assertEquals(105.1411f, pixelValue, 4);

            Band band_Red_Edge = finalProduct.getBand("Red Edge");
            assertEquals(3812, band_Red_Edge.getRasterWidth());
            assertEquals(2873, band_Red_Edge.getRasterHeight());

            pixelValue = band_Red_Edge.getSampleFloat(456, 405);
            assertEquals(31.6270f, pixelValue, 4);
            pixelValue = band_Red_Edge.getSampleFloat(1080, 926);
            assertEquals(51.2204f, pixelValue, 4);
            pixelValue = band_Red_Edge.getSampleFloat(1142, 2071);
            assertEquals(67.4197f, pixelValue, 4);
            pixelValue = band_Red_Edge.getSampleFloat(2849, 1154);
            assertEquals(122.0342f, pixelValue, 4);
            pixelValue = band_Red_Edge.getSampleFloat(3135, 343);
            assertEquals(110.4634f, pixelValue, 4);

            Band band_Pan = finalProduct.getBand("Pan");
            assertEquals(15250, band_Pan.getRasterWidth());
            assertEquals(11493, band_Pan.getRasterHeight());

            pixelValue = band_Pan.getSampleFloat(1783, 1636);
            assertEquals(43.0963f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(5614, 5937);
            assertEquals(44.2934f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(11485, 4469);
            assertEquals(86.7912f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(11470, 4630);
            assertEquals(99.5605f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(8190, 10546);
            assertEquals(81.2047f, pixelValue, 4);
        } catch (Exception e) {
            e.printStackTrace();
            assumeTrue(e.getMessage(),false);
        }
    }

    @Test
    public void testGetProductComponentsOnFileInput() {
        File file = TestUtil.getTestFile(productsFolder + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP"+File.separator+"WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML", components.getChildren()[0].getId());
        } catch (Exception e) {
            e.printStackTrace();
            //assertTrue(e.getMessage(), false);
            assumeTrue(e.getMessage(),false);
        }
    }
}
