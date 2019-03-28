package org.esa.s2tbx.dataio.ikonos;

import com.bc.ceres.core.NullProgressMonitor;
import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;


public class IkonosProductReaderTest {

    private IkonosProductReader reader;
    private String productFolder = "_ikonos" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());
        IkonosProductReaderPlugin plugin = new IkonosProductReaderPlugin();
        reader = new IkonosProductReader(plugin);
    }

    @After
    public void tearDown() throws Exception {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(IkonosProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productFolder + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP" + File.separator + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product product = reader.readProductNodes(file, null);
            assertEquals(5, product.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", product.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals(IkonosConstants.IKONOS_PRODUCT, product.getProductType());
            assertEquals(0, product.getMaskGroup().getNodeCount());
            assertEquals(4101, product.getSceneRasterWidth());
            assertEquals(3983, product.getSceneRasterHeight());
            assertEquals("20-AUG-2008 09:26:00.000000", product.getStartTime().toString());
            assertEquals("20-AUG-2008 09:26:00.000000", product.getEndTime().toString());
            assertEquals("metadata", product.getMetadataRoot().getName());
            assertEquals("IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001", product.getName());

            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadBandRasterData() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productFolder + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP" + File.separator + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product product = reader.readProductNodes(file, null);
            ProductData data = ProductData.createInstance(ProductData.TYPE_INT16, 20000);
            assertNotNull(data);
            data.setElemFloatAt(3, 5);
            reader.readBandRasterData(product.getBandAt(0), 2000, 2000, 100, 200, data, new NullProgressMonitor());
            assertNotEquals(0, data.getElemFloatAt(0));
            assertNotEquals(-1000, data.getElemFloatAt(0));
            assertNotEquals(0, data.getElemFloatAt(1999));
            assertNotEquals(-1000, data.getElemFloatAt(1999));
            assertNotEquals(5, data.getElemFloatAt(3));
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnFileInput() {
        File file = TestUtil.getTestFile(productFolder + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP" + File.separator + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnArchiveInput() {
        File file = TestUtil.getTestFile(productFolder + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP.ZIP");
        try {
            Product finalProduct = reader.readProductNodes(file, null);

            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP.ZIP", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }
}
