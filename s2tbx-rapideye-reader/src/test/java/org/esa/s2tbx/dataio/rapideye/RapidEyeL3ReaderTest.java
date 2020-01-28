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
public class RapidEyeL3ReaderTest {

    private static final String PRODUCTS_FOLDER = "_rapideye" + File.separator;

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Eritrea/13N041E-R1C2_2012_RE1_3a-3M_1234567890_metadata.xml");

        RapidEyeL3Reader reader = buildProductReader();

        Product product = reader.readProductNodes(productFile, null);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(10985, product.getSceneRasterWidth());
        assertEquals(11232, product.getSceneRasterHeight());
        assertEquals("L3M", product.getProductType());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(0, product.getTiePointGrids().length);

        assertEquals(3, product.getBands().length);

        Band band = product.getBandAt(0);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(123383520, band.getNumDataElems());
        assertEquals("blue", band.getName());
        assertEquals(10985, band.getRasterWidth());
        assertEquals(11232, band.getRasterHeight());

        assertEquals(0, band.getSampleInt(0, 0));
        assertEquals(0, band.getSampleInt(123, 123));
        assertEquals(89, band.getSampleInt(23, 2000));
        assertEquals(82, band.getSampleInt(1453, 2871));
        assertEquals(76, band.getSampleInt(153, 800));
        assertEquals(93, band.getSampleInt(1542, 2101));
        assertEquals(89, band.getSampleInt(1654, 1670));
        assertEquals(77, band.getSampleInt(766, 983));
        assertEquals(85, band.getSampleInt(1986, 2354));
        assertEquals(77, band.getSampleInt(10, 1000));
        assertEquals(79, band.getSampleInt(500, 500));
        assertEquals(0, band.getSampleInt(10985, 11232));
    }

    @Test
    public void testReadProductSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Eritrea/13N041E-R1C2_2012_RE1_3a-3M_1234567890_metadata.xml");

        RapidEyeL3Reader reader = buildProductReader();

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "green", "blue" } );
        subsetDef.setRegion(new Rectangle(1234, 543, 6789, 5134));
        subsetDef.setSubSampling(1, 1);

        Product product = reader.readProductNodes(productFile, subsetDef);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(6789, product.getSceneRasterWidth());
        assertEquals(5134, product.getSceneRasterHeight());
        assertEquals("L3M", product.getProductType());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(0, product.getTiePointGrids().length);

        assertEquals(2, product.getBands().length);

        Band band = product.getBandAt(1);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(34854726, band.getNumDataElems());
        assertEquals("green", band.getName());
        assertEquals(6789, band.getRasterWidth());
        assertEquals(5134, band.getRasterHeight());

        assertEquals(77, band.getSampleInt(0, 0));
        assertEquals(74, band.getSampleInt(123, 123));
        assertEquals(86, band.getSampleInt(23, 2000));
        assertEquals(81, band.getSampleInt(1453, 2871));
        assertEquals(77, band.getSampleInt(153, 800));
        assertEquals(83, band.getSampleInt(1542, 2101));
        assertEquals(86, band.getSampleInt(1654, 1670));
        assertEquals(80, band.getSampleInt(766, 983));
        assertEquals(85, band.getSampleInt(1986, 2354));
        assertEquals(83, band.getSampleInt(10, 1000));
        assertEquals(76, band.getSampleInt(500, 500));
        assertEquals(0, band.getSampleInt(6789, 5134));
    }

    private static RapidEyeL3Reader buildProductReader() {
        RapidEyeL3ReaderPlugin plugin = new RapidEyeL3ReaderPlugin();
        return new RapidEyeL3Reader(plugin, plugin.getColorPaletteFilePath());
    }
}
