package org.esa.beam.dataio.rapideye;

import com.bc.ceres.core.NullProgressMonitor;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.TreeNode;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Ramona Manda
 */
public class RapidEyeL1ReaderTest {

    private RapidEyeL1Reader reader;

    @Before
    public void setup() {
        RapidEyeL1ReaderPlugin plugin = new RapidEyeL1ReaderPlugin();
        reader = new RapidEyeL1Reader(plugin);
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(RapidEyeL1ReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("Demo03_1B/2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(6, finalProduct.getBands().length);
            assertEquals("WGS84(DD)", finalProduct.getGeoCoding().getGeoCRS().getName().toString());
            assertEquals("L1B", finalProduct.getProductType());
            assertEquals(7, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(11829, finalProduct.getSceneRasterWidth());
            assertEquals(7422, finalProduct.getSceneRasterHeight());
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
        Product product = new Product("name", "desc", 100, 200);
        File file = TestUtil.getTestFile("Demo05_1B.zip");
        File rasterFile = TestUtil.getTestFile("mediumImage.tif");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "200");
        try {

            Product finalProduct = reader.readProductNodes(file, null);
            ProductData data = ProductData.createInstance(ProductData.TYPE_UINT16, 20000);
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
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("Demo03_1B/2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(7, components.getChildren().length);
            assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml", components.getChildren()[0].getId());
            assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band1.ntf", components.getChildren()[1].getId());
            assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band2.ntf", components.getChildren()[2].getId());
            assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band3.ntf", components.getChildren()[3].getId());
            assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band4.ntf", components.getChildren()[4].getId());
            assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band5.ntf", components.getChildren()[5].getId());
            assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_udm.tif", components.getChildren()[6].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnArchiveInput() {
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("Demo05_1B.zip");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("Demo05_1B.zip", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testConfigurePreferredTileSize() {
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("Demo05_1B.zip");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            System.setProperty("snap.reader.tileWidth", "300");
            System.setProperty("snap.reader.tileHeight", "100");
            reader.configurePreferredTileSize(finalProduct);
            Dimension size = finalProduct.getPreferredTileSize();
            assertEquals(100, size.height);
            assertEquals(300, size.width);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }
}