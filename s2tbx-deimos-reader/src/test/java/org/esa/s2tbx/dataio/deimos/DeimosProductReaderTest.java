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

package org.esa.s2tbx.dataio.deimos;

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
 * @author Ramona MANDA
 */
public class DeimosProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_deimos" + File.separator;

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "small_deimos/DE01_SL6_22P_1T_20110228T092316_20110616T092427_DMI_0_2e9d.dim");

        DeimosProductReader reader = buildProductReader();

        Product product = reader.readProductNodes(productFile, null);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(3000, product.getSceneRasterWidth());
        assertEquals(3000, product.getSceneRasterHeight());
        assertEquals("DEIMOS", product.getProductType());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("WGS84(DD)", coordinateReferenceSystem.getName().getCode());

        assertEquals(1, product.getMaskGroup().getNodeCount());

        assertEquals(2, product.getTiePointGrids().length);

        assertEquals(4, product.getBands().length);

        Band band = product.getBandAt(0);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(9000000, band.getNumDataElems());
        assertEquals("NIR", band.getName());
        assertEquals(3000, band.getRasterWidth());
        assertEquals(3000, band.getRasterHeight());

        assertEquals(102, band.getSampleInt(0, 0));
        assertEquals(96, band.getSampleInt(123, 123));
        assertEquals(85, band.getSampleInt(23, 2000));
        assertEquals(90, band.getSampleInt(1453, 2871));
        assertEquals(94, band.getSampleInt(153, 800));
        assertEquals(87, band.getSampleInt(542, 2101));
        assertEquals(86, band.getSampleInt(654, 1670));
        assertEquals(94, band.getSampleInt(766, 983));
        assertEquals(88, band.getSampleInt(1986, 2354));
        assertEquals(79, band.getSampleInt(10, 1000));
        assertEquals(180, band.getSampleInt(500, 500));
        assertEquals(0, band.getSampleInt(3000, 3000));
    }

    @Test
    public void testReadProductSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "small_deimos/DE01_SL6_22P_1T_20110228T092316_20110616T092427_DMI_0_2e9d.dim");

        DeimosProductReader reader = buildProductReader();

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "NIR", "Red", "Green" } );
        subsetDef.setRegion(new Rectangle(1234, 543, 1678, 2134));
        subsetDef.setSubSampling(1, 1);

        Product product = reader.readProductNodes(productFile, subsetDef);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(1678, product.getSceneRasterWidth());
        assertEquals(2134, product.getSceneRasterHeight());
        assertEquals("DEIMOS", product.getProductType());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("WGS84(DD)", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(2, product.getTiePointGrids().length);

        assertEquals(3, product.getBands().length);

        Band band = product.getBandAt(2);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(3580852, band.getNumDataElems());
        assertEquals("Green", band.getName());
        assertEquals(1678, band.getRasterWidth());
        assertEquals(2134, band.getRasterHeight());

        assertEquals(70, band.getSampleInt(0, 0));
        assertEquals(123, band.getSampleInt(123, 123));
        assertEquals(59, band.getSampleInt(23, 2000));
        assertEquals(78, band.getSampleInt(1453, 1971));
        assertEquals(89, band.getSampleInt(153, 800));
        assertEquals(64, band.getSampleInt(542, 1701));
        assertEquals(136, band.getSampleInt(654, 1670));
        assertEquals(76, band.getSampleInt(766, 983));
        assertEquals(112, band.getSampleInt(1656, 1354));
        assertEquals(96, band.getSampleInt(10, 1230));
        assertEquals(76, band.getSampleInt(500, 500));
        assertEquals(0, band.getSampleInt(1678, 2134));
    }

    private static DeimosProductReader buildProductReader() {
        DeimosProductReaderPlugin plugin = new DeimosProductReaderPlugin();
        return new DeimosProductReader(plugin, plugin.getColorPaletteFilePath());
    }
}
