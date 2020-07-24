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

import com.bc.ceres.binding.ConversionException;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.subset.GeometrySubsetRegion;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.converters.JtsGeometryConverter;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.Rectangle;
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

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Demo03_1B"+ File.separator+ "2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");

        ProductReader reader = buildProductReader();
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
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Demo03_1B"+ File.separator+ "2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "blue", "green", "red", "red_egde", "black_fill", "clouds", "missing_blue_data", "missing_red_data" } );
        subsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(1000, 2000, 3000, 4000), 0));
        subsetDef.setSubSampling(1, 1);

        ProductReader reader = buildProductReader();
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

    @Test
    public void testReadProductGeometrySubset() throws IOException, ConversionException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Demo03_1B" + File.separator + "2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((15.125024795532227 52.961814880371094, 15.160173416137695 52.95663833618164, 15.195322036743164 52.95146560668945, 15.23047161102295 52.9462890625, 15.265621185302734 52.94111633300781, 15.30077075958252 52.93593978881836, 15.335920333862305 52.930763244628906, 15.371070861816406 52.92559051513672, 15.406220436096191 52.920413970947266, 15.396490097045898 52.899322509765625, 15.386758804321289 52.87822723388672, 15.377028465270996 52.85713195800781, 15.367297172546387 52.83604049682617, 15.357566833496094 52.814945220947266, 15.347835540771484 52.79384994506836, 15.338105201721191 52.77275848388672, 15.328373908996582 52.75166320800781, 15.318643569946289 52.730567932128906, 15.30891227722168 52.709476470947266, 15.302425384521484 52.695411682128906, 15.267449378967285 52.7005615234375, 15.232474327087402 52.70570755004883, 15.197498321533203 52.71085739135742, 15.16252326965332 52.71600341796875, 15.127549171447754 52.721153259277344, 15.092574119567871 52.72629928588867, 15.057600021362305 52.731449127197266, 15.022625923156738 52.736595153808594, 15.02902603149414 52.75067138671875, 15.038625717163086 52.771785736083984, 15.048225402832031 52.79290008544922, 15.057826042175293 52.81401443481445, 15.067425727844238 52.83512878417969, 15.077025413513184 52.85624313354492, 15.086625099182129 52.877357482910156, 15.096224784851074 52.89847183227539, 15.105825424194336 52.919586181640625, 15.115425109863281 52.94070053100586, 15.125024795532227 52.961814880371094))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"blue", "green", "red", "red_edge", "black_fill", "clouds", "missing_blue_data", "missing_red_data"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);

        ProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(productFile, subsetDef);

        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals("L1B", product.getProductType());
        assertEquals(3017, product.getSceneRasterWidth());
        assertEquals(4002, product.getSceneRasterHeight());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("WGS84(DD)", coordinateReferenceSystem.getName().getCode());

        assertEquals(4, product.getMaskGroup().getNodeCount());

        assertEquals(4, product.getBands().length);

        Band band = product.getBandAt(2);
        assertNotNull(band);
        assertEquals(21, band.getDataType());
        assertEquals(12074034, band.getNumDataElems());
        assertEquals("red", band.getName());
        assertEquals(3017, band.getRasterWidth());
        assertEquals(4002, band.getRasterHeight());

        assertEquals(22.67f, band.getSampleFloat(0, 0), 2);
        assertEquals(24.88f, band.getSampleFloat(22, 20), 2);
        assertEquals(38.01f, band.getSampleFloat(123, 3221), 2);
        assertEquals(25.08f, band.getSampleFloat(246, 134), 2);
        assertEquals(19.53f, band.getSampleFloat(435, 3543), 2);
        assertEquals(47.51f, band.getSampleFloat(1000, 3245), 2);
    }

    private static ProductReader buildProductReader() {
        RapidEyeL1ReaderPlugin plugin = new RapidEyeL1ReaderPlugin();
        return plugin.createReaderInstance();
    }
}
