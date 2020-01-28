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

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class SpotViewProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_spot" + File.separator;

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");

        SpotViewProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(productFile, null);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("40972700506050900111I", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(2713, product.getSceneRasterWidth());
        assertEquals(2568, product.getSceneRasterHeight());
        assertEquals("SPOTView", product.getProductType());
        assertNull(product.getStartTime());
        assertNull(product.getEndTime());
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
        assertEquals(6966984, band.getNumDataElems());
        assertEquals("band_2", band.getName());
        assertEquals(2713, band.getRasterWidth());
        assertEquals(2568, band.getRasterHeight());

        assertEquals(0, band.getSampleInt(0, 0));
        assertEquals(141, band.getSampleInt(1000, 1000));
        assertEquals(96, band.getSampleInt(2000, 2000));
        assertEquals(107, band.getSampleInt(100, 323));
        assertEquals(139, band.getSampleInt(1234, 325));
        assertEquals(107, band.getSampleInt(543, 213));
        assertEquals(85, band.getSampleInt(34, 653));
        assertEquals(106, band.getSampleInt(900, 321));
        assertEquals(95, band.getSampleInt(1324, 2109));
        assertEquals(0, band.getSampleInt(442, 90));
        assertEquals(79, band.getSampleInt(321, 1693));
        assertEquals(89, band.getSampleInt(442, 896));
        assertEquals(0, band.getSampleInt(2700, 2500));
        assertEquals(0, band.getSampleInt(2713, 2568));
    }

    @Test
    public void testReadProductSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "band_0", "band_1", "band_3" } );
        subsetDef.setRegion(new Rectangle(123, 500, 1567, 1765));
        subsetDef.setSubSampling(1, 1);

        SpotViewProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(productFile, subsetDef);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("40972700506050900111I", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(1567, product.getSceneRasterWidth());
        assertEquals(1765, product.getSceneRasterHeight());
        assertEquals("SPOTView", product.getProductType());
        assertNull(product.getStartTime());
        assertNull(product.getEndTime());
        assertEquals("metadata", product.getMetadataRoot().getName());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("WGS84(DD)", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(3, product.getBands().length);

        Band band = product.getBandAt(1);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(2765755, band.getNumDataElems());
        assertEquals("band_1", band.getName());
        assertEquals(1567, band.getRasterWidth());
        assertEquals(1765, band.getRasterHeight());

        assertEquals(86, band.getSampleInt(0, 0));
        assertEquals(46, band.getSampleInt(1000, 1000));
        assertEquals(67, band.getSampleInt(200, 1200));
        assertEquals(66, band.getSampleInt(100, 323));
        assertEquals(48, band.getSampleInt(1234, 325));
        assertEquals(96, band.getSampleInt(543, 213));
        assertEquals(65, band.getSampleInt(34, 653));
        assertEquals(62, band.getSampleInt(900, 321));
        assertEquals(122, band.getSampleInt(1324, 1609));
        assertEquals(77, band.getSampleInt(442, 90));
        assertEquals(49, band.getSampleInt(321, 1693));
        assertEquals(65, band.getSampleInt(442, 896));
        assertEquals(50, band.getSampleInt(700, 500));
        assertEquals(0, band.getSampleInt(1567, 1765));
    }

    private static SpotViewProductReader buildProductReader() {
        SpotViewProductReaderPlugin plugin = new SpotViewProductReaderPlugin();
        return new SpotViewProductReader(plugin, plugin.getColorPaletteFilePath());
    }
}
