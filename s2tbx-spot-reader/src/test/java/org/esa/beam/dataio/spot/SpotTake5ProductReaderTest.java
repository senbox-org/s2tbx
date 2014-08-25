package org.esa.beam.dataio.spot;

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
public class SpotTake5ProductReaderTest {

    private SpotTake5ProductReader reader;

    @Before
    public void setup() {
        SpotTake5ProductReaderPlugin plugin = new SpotTake5ProductReaderPlugin();
        reader = new SpotTake5ProductReader(plugin);
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(SpotTake5ProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("SPOT4_HRVIR1_XS_88888888_N1A.xml");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(4, finalProduct.getBands().length);
            assertEquals("WGS84(DD)", finalProduct.getGeoCoding().getGeoCRS().getName().toString());
            assertEquals("SPOTTake5", finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(4000, finalProduct.getSceneRasterWidth());
            assertEquals(3750, finalProduct.getSceneRasterHeight());
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
        File file = TestUtil.getTestFile("SPOT4_HRVIR1_XS_88888888_N1A.xml");
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
        File file = TestUtil.getTestFile("SPOT4_HRVIR1_XS_88888888_N1A.xml");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(2, components.getChildren().length);
            assertEquals("SPOT4_HRVIR1_XS_88888888_N1A.xml", components.getChildren()[0].getId());
            assertEquals("mediumImage.tif", components.getChildren()[1].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnArchiveInput() {
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("SPOT4_HRVIR1_XS_88888888_N1A.tgz");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("SPOT4_HRVIR1_XS_88888888_N1A.tgz", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testConfigurePreferredTileSize() {
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("SPOT4_HRVIR1_XS_88888888_N1A.tgz");
        System.setProperty("snap.reader.tileWidth", "200");
        System.setProperty("snap.reader.tileHeight", "200");
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