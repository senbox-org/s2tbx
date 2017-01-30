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

package org.esa.s2tbx.dataio.rapideye;

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
public class RapidEyeL1ReaderTest {

    private RapidEyeL1Reader reader;
    private String productsFolder = "_rapideye" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        RapidEyeL1ReaderPlugin plugin = new RapidEyeL1ReaderPlugin();
        reader = new RapidEyeL1Reader(plugin, plugin.getColorPaletteFilePath());
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(RapidEyeL1ReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile(productsFolder + "Demo03_1B/2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(6, finalProduct.getBands().length);
            assertEquals("WGS84(DD)", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
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
        //Product product = new Product("name", "desc", 100, 200);
        File file = TestUtil.getTestFile(productsFolder + "Demo05_1B.zip");
        //File rasterFile = TestUtil.getTestFile("mediumImage.tif");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "200");
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
        File file = TestUtil.getTestFile(productsFolder + "Demo03_1B/2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");
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
        //Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile(productsFolder + "Demo05_1B.zip");
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
}
