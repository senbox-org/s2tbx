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
public class SpotViewProductReaderTest {

    private SpotViewProductReader reader;

    @Before
    public void setup() {
        SpotViewProductReaderPlugin plugin = new SpotViewProductReaderPlugin();
        reader = new SpotViewProductReader(plugin);
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(SpotViewProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(4, finalProduct.getBands().length);
            assertEquals("WGS84(DD)", finalProduct.getGeoCoding().getGeoCRS().getName().toString());
            assertEquals("SPOTView", finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(2713, finalProduct.getSceneRasterWidth());
            assertEquals(2568, finalProduct.getSceneRasterHeight());
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
        File file = TestUtil.getTestFile("SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");
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
        File file = TestUtil.getTestFile("SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL/metadata.xml");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(3, components.getChildren().length);
            String[] expectedIds = new String[]{"metadata.dim", "metadata.xml", "geolayer.bil"};
            int componentsAsExpected = 0;
            for(TreeNode<File> component: components.getChildren()){
                for(String expectedValue: expectedIds){
                    if(component.getId().toLowerCase().equals(expectedValue.toLowerCase())){
                        componentsAsExpected++;
                    }
                }
            }
            assertEquals(3, componentsAsExpected);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnArchiveInput() {
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testConfigurePreferredTileSize() {
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");
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