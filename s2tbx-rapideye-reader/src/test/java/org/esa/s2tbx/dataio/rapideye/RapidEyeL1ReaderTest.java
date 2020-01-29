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
public class RapidEyeL1ReaderTest {

    private static final String PRODUCTS_FOLDER = "_rapideye" + File.separator;

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Demo03_1B/2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");

        RapidEyeL1Reader reader = buildProductReader();

        Product product = reader.readProductNodes(productFile, null);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals("L1B", product.getProductType());
        assertEquals(11829, product.getSceneRasterWidth());
        assertEquals(7422, product.getSceneRasterHeight());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("WGS84(DD)", coordinateReferenceSystem.getName().getCode());

        assertEquals(7, product.getMaskGroup().getNodeCount());

        assertEquals(6, product.getBands().length);

        Band band = product.getBandAt(1);
        assertNotNull(band);
        assertEquals(21, band.getDataType());
        assertEquals(87794838, band.getNumDataElems());
        assertEquals("green", band.getName());
        assertEquals(11829, band.getRasterWidth());
        assertEquals(7422, band.getRasterHeight());

        assertEquals(47.18f, band.getSampleFloat(0, 0), 0.0f);
        assertEquals(43.87f, band.getSampleFloat(22, 20), 0.0f);
        assertEquals(49.95f, band.getSampleFloat(123, 3221), 0.0f);
        assertEquals(52.12f, band.getSampleFloat(246, 134), 0.0f);
        assertEquals(57.67f, band.getSampleFloat(435, 6543), 0.0f);
        assertEquals(43.02f, band.getSampleFloat(10000, 3245), 0.0f);
        assertEquals(29.859999f, band.getSampleFloat(3214, 3242), 0.0f);
        assertEquals(46.52f, band.getSampleFloat(221, 1233), 0.0f);
        assertEquals(37.68f, band.getSampleFloat(9864, 532), 0.0f);
        assertEquals(44.489998f, band.getSampleFloat(8763, 2445), 0.0f);
        assertEquals(43.5f, band.getSampleFloat(7642, 3354), 0.0f);
        assertEquals(32.92f, band.getSampleFloat(5444, 5544), 0.0f);
        assertEquals(44.48f, band.getSampleFloat(5332, 4345), 0.0f);
        assertEquals(68.95f, band.getSampleFloat(8000, 7000), 0.0f);
        assertEquals(39.27f, band.getSampleFloat(543, 12), 0.0f);
        assertEquals(43.27f, band.getSampleFloat(32, 6547), 0.0f);
        assertEquals(0.0f, band.getSampleFloat(11829, 7422), 0.0f);
    }

    @Test
    public void testReadProductSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Demo03_1B/2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");

        RapidEyeL1Reader reader = buildProductReader();

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "blue", "green", "red", "red_egde", "black_fill", "clouds", "missing_blue_data", "missing_red_data" } );
        subsetDef.setRegion(new Rectangle(1000, 2000, 3000, 4000));
        subsetDef.setSubSampling(1, 1);

        Product product = reader.readProductNodes(productFile, subsetDef);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals("L1B", product.getProductType());
        assertEquals(3000, product.getSceneRasterWidth());
        assertEquals(4000, product.getSceneRasterHeight());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("WGS84(DD)", coordinateReferenceSystem.getName().getCode());

        assertEquals(4, product.getMaskGroup().getNodeCount());

        assertEquals(3, product.getBands().length);

        Band band = product.getBandAt(2);
        assertNotNull(band);
        assertEquals(21, band.getDataType());
        assertEquals(12000000, band.getNumDataElems());
        assertEquals("red", band.getName());
        assertEquals(3000, band.getRasterWidth());
        assertEquals(4000, band.getRasterHeight());

        assertEquals(25.699999f, band.getSampleFloat(0, 0), 0.0f);
        assertEquals(25.9f, band.getSampleFloat(22, 20), 0.0f);
        assertEquals(23.3f, band.getSampleFloat(123, 3221), 0.0f);
        assertEquals(21.64f, band.getSampleFloat(246, 134), 0.0f);
        assertEquals(34.86f, band.getSampleFloat(435, 3543), 0.0f);
        assertEquals(33.67f, band.getSampleFloat(1000, 3245), 0.0f);
        assertEquals(35.329998f, band.getSampleFloat(2214, 3242), 0.0f);
        assertEquals(16.619999f, band.getSampleFloat(221, 1233), 0.0f);
        assertEquals(30.42f, band.getSampleFloat(864, 532), 0.0f);
        assertEquals(24.1f, band.getSampleFloat(2763, 1445), 0.0f);
        assertEquals(29.0f, band.getSampleFloat(2642, 3354), 0.0f);
        assertEquals(27.98f, band.getSampleFloat(544, 544), 0.0f);
        assertEquals(23.33f, band.getSampleFloat(1332, 2345), 0.0f);
        assertEquals(24.609999f, band.getSampleFloat(2200, 700), 0.0f);
        assertEquals(24.57f, band.getSampleFloat(543, 12), 0.0f);
        assertEquals(52.11f, band.getSampleFloat(32, 547), 0.0f);
        assertEquals(0.0f, band.getSampleFloat(3000, 4000), 0.0f);
    }

    private static RapidEyeL1Reader buildProductReader() {
        RapidEyeL1ReaderPlugin plugin = new RapidEyeL1ReaderPlugin();
        return new RapidEyeL1Reader(plugin, plugin.getColorPaletteFilePath());
    }
}
