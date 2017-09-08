package org.esa.s2tbx.dataio.kompsat2;

import com.bc.ceres.core.NullProgressMonitor;
import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author Razvan Dumitrascu
 */
public class Kompsat2ProductReaderTest {

    private Kompsat2ProductReader reader;
    private String productsFolder = "_kompsat" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        Kompsat2ProductReaderPlugin plugin = new Kompsat2ProductReaderPlugin();
        reader = new Kompsat2ProductReader(plugin);
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(Kompsat2ProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP/KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(5, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals(Kompsat2Constants.KOMPSAT2_PRODUCT, finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(18172, finalProduct.getSceneRasterWidth());
            assertEquals(18808, finalProduct.getSceneRasterHeight());
            assertEquals("20-SEP-2011 01:32:01.394000", finalProduct.getStartTime().toString());
            assertEquals("20-SEP-2011 01:32:03.730000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001", finalProduct.getName());
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
        File file = TestUtil.getTestFile(productsFolder + "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP/KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML");
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
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnFileInput() {
        File file = TestUtil.getTestFile(productsFolder + "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP/KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

}
