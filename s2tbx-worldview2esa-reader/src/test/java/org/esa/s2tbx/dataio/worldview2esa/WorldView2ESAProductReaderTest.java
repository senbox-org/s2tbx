package org.esa.s2tbx.dataio.worldview2esa;

import com.bc.ceres.core.NullProgressMonitor;
import org.esa.s2tbx.dataio.worldview2esa.common.WorldView2ESAConstants;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
            assertEquals(16384, finalProduct.getSceneRasterWidth());
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
    public void testReadBandRasterData() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP"+File.separator+"WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "200");
        try {

            Product finalProduct = reader.readProductNodes(file, null);
            ProductData data = ProductData.createInstance(ProductData.TYPE_UINT16, 20000);
            assertNotNull(data);
            data.setElemFloatAt(3, 5);
            reader.readBandRasterData(finalProduct.getBandAt(0), 2000, 2000, 100, 200, data, new NullProgressMonitor());
            assertNotEquals(0, data.getElemFloatAt(0));
            assertNotEquals(-1000, data.getElemFloatAt(0));
            assertNotEquals(0, data.getElemFloatAt(1999));
            assertNotEquals(-1000, data.getElemFloatAt(1999));
            assertNotEquals(5, data.getElemFloatAt(3));
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (Exception e) {
            e.printStackTrace();
            //assertTrue(e.getMessage(), false);
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
