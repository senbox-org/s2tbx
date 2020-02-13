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

import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class SpotTake5ProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_spot" + File.separator;

    @Test
    public void testReadProductNodes() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT4_HRVIR1_XS_88888888_N1A.xml");

        SpotTake5ProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(productFile, null);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("SPOT4_HRVIR1_XS_20130616_N2A_JTanzanieD0000B0000", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(4000, product.getSceneRasterWidth());
        assertEquals(3750, product.getSceneRasterHeight());
        assertEquals("SPOT4Take5", product.getProductType());
        assertNotNull(product.getStartTime());
        assertNotNull(product.getEndTime());
        assertEquals("metadata", product.getMetadataRoot().getName());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("WGS84(DD)", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(4, product.getBands().length);

        Band band = product.getBandAt(2);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(9000000, band.getNumDataElems());
        assertEquals("XS3", band.getName());
        assertEquals(3000, band.getRasterWidth());
        assertEquals(3000, band.getRasterHeight());

        assertEquals(66, band.getSampleInt(0, 0));
        assertEquals(52, band.getSampleInt(231, 231));
        assertEquals(81, band.getSampleInt(32, 1235));
        assertEquals(97, band.getSampleInt(400, 2134));
        assertEquals(54, band.getSampleInt(35, 983));
        assertEquals(53, band.getSampleInt(763, 2658));
        assertEquals(74, band.getSampleInt(33, 900));
        assertEquals(64, band.getSampleInt(323, 896));
        assertEquals(82, band.getSampleInt(65, 654));
        assertEquals(111, band.getSampleInt(1500, 2000));
        assertEquals(90, band.getSampleInt(345, 2234));
        assertEquals(58, band.getSampleInt(542, 2434));
        assertEquals(0, band.getSampleInt(3000, 3000));
    }

    @Test
    public void testReadProductSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT4_HRVIR1_XS_20130608_N1_TUILE_EArgentinaD0000B0000.xml");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "XS1", "XS2", "SWIR" } );
        subsetDef.setRegion(new Rectangle(1200, 1000, 2567, 2000));
        subsetDef.setSubSampling(1, 1);

        SpotTake5ProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(productFile, subsetDef);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("SPOT4_HRVIR1_XS_20130608_N1_TUILE_EArgentinaD0000B0000", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(2567, product.getSceneRasterWidth());
        assertEquals(2000, product.getSceneRasterHeight());
        assertEquals("SPOT4Take5", product.getProductType());
        assertNotNull(product.getStartTime());
        assertNotNull(product.getEndTime());
        assertEquals("metadata", product.getMetadataRoot().getName());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(3, product.getBands().length);

        Band band = product.getBandAt(1);
        assertNotNull(band);
        assertEquals(11, band.getDataType());
        assertEquals(5134000, band.getNumDataElems());
        assertEquals("XS2", band.getName());
        assertEquals(2567, band.getRasterWidth());
        assertEquals(2000, band.getRasterHeight());

        assertEquals(98, band.getSampleInt(0, 0));
        assertEquals(90, band.getSampleInt(231, 231));
        assertEquals(118, band.getSampleInt(32, 1235));
        assertEquals(93, band.getSampleInt(400, 1134));
        assertEquals(126, band.getSampleInt(35, 983));
        assertEquals(103, band.getSampleInt(763, 1508));
        assertEquals(92, band.getSampleInt(1733, 900));
        assertEquals(80, band.getSampleInt(323, 896));
        assertEquals(105, band.getSampleInt(65, 654));
        assertEquals(108, band.getSampleInt(1500, 1500));
        assertEquals(75, band.getSampleInt(345, 1234));
        assertEquals(78, band.getSampleInt(542, 1434));
        assertEquals(123, band.getSampleInt(1925, 1600));
    }

    private static SpotTake5ProductReader buildProductReader() {
        SpotTake5ProductReaderPlugin plugin = new SpotTake5ProductReaderPlugin();
        return new SpotTake5ProductReader(plugin, plugin.getColorPaletteFilePath());
    }
}
