/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.spot;

import com.bc.ceres.core.NullProgressMonitor;
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
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class SpotDimapProductReaderTest {

    private SpotDimapProductReader reader;
    private String productsFolder = "_spot" + File.separator;

    @Before
    public void setup() {
        SpotDimapProductReaderPlugin plugin = new SpotDimapProductReaderPlugin();
        reader = new SpotDimapProductReader(plugin);
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(SpotDimapProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodesBySimpleProductReader(){
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile(productsFolder + "30382639609301123571X0_1A_NETWORK.ZIP");
        //System.setProperty("snap.dataio.reader.tileWidth", "100");
        //System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(finalProduct.getProductReader().getClass(), SpotDimapSimpleProductReader.class);
            assertEquals(4, finalProduct.getBands().length);
            assertEquals("WGS84(DD)", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals("SPOTSCENE_1A", finalProduct.getProductType());
            assertEquals(2, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(3000, finalProduct.getSceneRasterWidth());
            assertEquals(3000, finalProduct.getSceneRasterHeight());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadBandRasterDataBySimpleProductReader(){
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 200);
        File file = TestUtil.getTestFile(productsFolder + "30382639609301123571X0_1A_NETWORK.ZIP");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "200");
        try {

            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(finalProduct.getProductReader().getClass(), SpotDimapSimpleProductReader.class);
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
    public void testGetProductComponentsOnVolumeFileInputBySimpleProductReader(){
        assumeTrue(TestUtil.testdataAvailable());

        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile(productsFolder + "vol_list.dim");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(finalProduct.getProductReader().getClass(), SpotDimapSimpleProductReader.class);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(4, components.getChildren().length);
            String[] expectedIds = new String[]{"metadata.dim", "vol_list.dim", "mediumImage.tif", "icon.jpg"};
            int componentsAsExpected = 0;
            for(TreeNode<File> component: components.getChildren()){
                for(String expectedValue: expectedIds){
                    if(component.getId().toLowerCase().equals(expectedValue.toLowerCase())){
                        componentsAsExpected++;
                    }
                }
            }
            assertEquals(4, componentsAsExpected);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnArchiveInputBySimpleProductReader(){
        assumeTrue(TestUtil.testdataAvailable());

        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile(productsFolder + "30382639609301123571X0_1A_NETWORK.ZIP");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(finalProduct.getProductReader().getClass(), SpotDimapSimpleProductReader.class);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("30382639609301123571X0_1A_NETWORK.ZIP", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductNodesByVolumeProductReader(){
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile(productsFolder + "SPOT-5_2.5mc_3" + File.separator + "VOL_LIST.DIM");
        //System.setProperty("snap.dataio.reader.tileWidth", "100");
        //System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(finalProduct.getProductReader().getClass(), SpotDimapVolumeProductReader.class);
            assertEquals(3, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals("SPOTDimap", finalProduct.getProductType());
            assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(31770, finalProduct.getSceneRasterWidth());
            assertEquals(30620, finalProduct.getSceneRasterHeight());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadBandRasterDataByVolumeProductReader(){
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 200);
        File file = TestUtil.getTestFile(productsFolder + "SPOT-5_2.5mc_3" + File.separator + "VOL_LIST.DIM");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "200");
        try {

            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(finalProduct.getProductReader().getClass(), SpotDimapVolumeProductReader.class);
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
    public void testGetProductComponentsOnVolumeFileInputByVolumeProductReader(){
        assumeTrue(TestUtil.testdataAvailable());

        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile(productsFolder + "SPOT-5_2.5mc_3" + File.separator + "VOL_LIST.DIM");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(finalProduct.getProductReader().getClass(), SpotDimapVolumeProductReader.class);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(7, components.getChildren().length);
            String[] expectedIds = new String[]{"vol_list.dim", "SPVIEW01_0_0" + File.separator + "ICON_0_0.JPG", "SPVIEW01_0_0" + File.separator + "IMAGERY_0_0.TIF",
                    "SPVIEW01_0_0" + File.separator + "METADATA_0_0.dim", "SPVIEW01_0_1" + File.separator + "ICON_0_1.JPG", "SPVIEW01_0_1" + File.separator + "IMAGERY_0_1.TIF",
                    "SPVIEW01_0_1" + File.separator + "METADATA_0_1.dim"};
            int componentsAsExpected = 0;
            for(TreeNode<File> component: components.getChildren()){
                for(String expectedValue: expectedIds){
                    if(component.getId().toLowerCase().replace("/", "|").replace("\\", "|").equals(expectedValue.toLowerCase().replace("/", "|").replace("\\", "|"))){
                        componentsAsExpected++;
                        break;
                    }
                }
            }
            assertEquals(7, componentsAsExpected);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnArchiveInputByVolumeProductReader(){
        //no archive to input this method!
        /*
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile(productsFolder + "SPOT-5_2.5mc_3\\VOL_LIST.DIM");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(finalProduct.getProductReader().getClass(), SpotDimapSimpleProductReader.class);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("30382639609301123571X0_1A_NETWORK.ZIP", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
        */
    }
}
