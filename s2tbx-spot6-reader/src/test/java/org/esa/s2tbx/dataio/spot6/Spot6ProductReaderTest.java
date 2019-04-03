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

package org.esa.s2tbx.dataio.spot6;

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
public class Spot6ProductReaderTest {

    private Spot6ProductReader reader;
    private String productsFolder = "_spot6_7" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        Spot6ProductReaderPlugin plugin = new Spot6ProductReaderPlugin();
        reader = new Spot6ProductReader(plugin);
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(Spot6ProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "SPOT6_1.5m_short/SPOT_LIST.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(4, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals("SPOT 6/7 Product", finalProduct.getProductType());
            assertEquals(3, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(2319, finalProduct.getSceneRasterWidth());
            assertEquals(1870, finalProduct.getSceneRasterHeight());
            //name should be changed
            assertEquals("SPOT_20140129050233095_816009101_2", finalProduct.getName());
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
        File file = TestUtil.getTestFile(productsFolder + "SPOT6_1.5m_short\\SPOT_LIST.XML");
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
        File file = TestUtil.getTestFile(productsFolder + "SPOT6_1.5m_short\\SPOT_LIST.XML");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(8, components.getChildren().length);
            assertEquals("SPOT_LIST.XML", components.getChildren()[0].getId());
            assertEquals("SPOT_VOL.XML", components.getChildren()[1].getId());
            assertEquals("DIM_SPOT6_PMS_201305251604372_ORT_816009101.XML", components.getChildren()[2].getId());
            assertEquals("IMG_SPOT6_PMS_201305251604372_ORT_816009101_R2C2.JP2", components.getChildren()[3].getId());
            assertEquals("Area_Of_Interest Mask", components.getChildren()[4].getId());
            assertEquals("Detector_Quality Mask", components.getChildren()[5].getId());
            assertEquals("Cloud_Cotation Mask", components.getChildren()[6].getId());
            assertEquals("SPOT_PROD.XML", components.getChildren()[7].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnArchiveInput() {
        //TODO this will work someday!
        /*File file = TestUtil.getTestFile(productsFolder + "SPOT6_archived.zip");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("SPOT6_archived.zip", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }*/
    }
}
