/*
 *
 *  * Copyright (C) 2016 CS ROMANIA
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.pleiades;

import com.bc.ceres.core.NullProgressMonitor;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class PleiadesProductReaderTest {

    private PleiadesProductReader reader;
    private String productsFolder = "_pleiades" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        PleiadesProductReaderPlugin plugin = new PleiadesProductReaderPlugin();
        reader = new PleiadesProductReader(plugin);
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(PleiadesProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP"+File.separator+"TPP1600462598"+File.separator+"VOL_PHR.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(4, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals("Pleiades 1A/B Product", finalProduct.getProductType());
            assertEquals(3, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(33300, finalProduct.getSceneRasterWidth());
            assertEquals(4397, finalProduct.getSceneRasterHeight());
            assertEquals("ORTHO PMS DS_PHR1A_201511151131518_FR1_PX_W008N40_0103_01317", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductSubset() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP"+File.separator+"TPP1600462598"+File.separator+"VOL_PHR.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Rectangle subsetRegion = new Rectangle(16650, 675, 12601, 3722);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[] { "B0", "B2", "SATURATED"} );
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);

            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(39.8534f, productOrigin.lat,4);
            assertEquals(-7.9933f, productOrigin.lon,4);

            assertEquals(2, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals("Pleiades 1A/B Product", finalProduct.getProductType());
            assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(12601, finalProduct.getSceneRasterWidth());
            assertEquals(3722, finalProduct.getSceneRasterHeight());
            assertEquals("ORTHO PMS DS_PHR1A_201511151131518_FR1_PX_W008N40_0103_01317", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Mask mask = finalProduct.getMaskGroup().get("SATURATED");
            assertEquals(12601, mask.getRasterWidth());
            assertEquals(3722, mask.getRasterHeight());

            Band band_B0 = finalProduct.getBand("B0");
            assertEquals(12601, band_B0.getRasterWidth());
            assertEquals(3722, band_B0.getRasterHeight());

            float pixelValue = band_B0.getSampleFloat(2995, 1116);
            assertEquals(26.6739f, pixelValue, 4);
            pixelValue = band_B0.getSampleFloat(3247, 1335);
            assertEquals(13.1723f, pixelValue, 4);
            pixelValue = band_B0.getSampleFloat(3247, 1372);
            assertEquals(40.1756, pixelValue, 4);
            pixelValue = band_B0.getSampleFloat(4162, 1402);
            assertEquals(9.1108f, pixelValue, 4);
            pixelValue = band_B0.getSampleFloat(7046, 1470);
            assertEquals(13.6114f, pixelValue, 4);
            pixelValue = band_B0.getSampleFloat(7396, 1461);
            assertEquals(22.7222f, pixelValue, 4);

            Band band_B2 = finalProduct.getBand("B2");
            assertEquals(12601, band_B2.getRasterWidth());
            assertEquals(3722, band_B2.getRasterHeight());

            pixelValue = band_B2.getSampleFloat(2995, 1116);
            assertEquals(32.9756f, pixelValue, 4);
            pixelValue = band_B2.getSampleFloat(3247, 1335);
            assertEquals(25.2682f, pixelValue, 4);
            pixelValue = band_B2.getSampleFloat(3247, 1372);
            assertEquals(39.7073f, pixelValue, 4);
            pixelValue = band_B2.getSampleFloat(4162, 1402);
            assertEquals(22.2439f, pixelValue, 4);
            pixelValue = band_B2.getSampleFloat(7046, 1470);
            assertEquals(22.6341f, pixelValue, 4);
            pixelValue = band_B2.getSampleFloat(7396, 1461);
            assertEquals(35.70732f, pixelValue, 4);

        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadBandRasterData() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP"+File.separator+"TPP1600462598"+File.separator+"VOL_PHR.XML");
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
        File file = TestUtil.getTestFile(productsFolder + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP"+File.separator+"TPP1600462598"+File.separator+"VOL_PHR.XML");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("VOL_PHR.XML", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnArchiveInput() {
        //TODO this will work someday!
        /*File file = TestUtil.getTestFile(productsFolder + "Pleiades_archived.zip");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("Pleiades_archived.zip", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }*/
    }
}
