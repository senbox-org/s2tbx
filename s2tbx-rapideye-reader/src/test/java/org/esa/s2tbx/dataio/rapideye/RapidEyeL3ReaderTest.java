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
public class RapidEyeL3ReaderTest {

    private static final String PRODUCTS_FOLDER = "_rapideye" + File.separator;

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Eritrea"+ File.separator+ "13N041E-R1C2_2012_RE1_3a-3M_1234567890_metadata.xml");

        ProductReader reader = buildProductReader();
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
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Eritrea"+ File.separator+ "13N041E-R1C2_2012_RE1_3a-3M_1234567890_metadata.xml");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "green", "blue" } );
        subsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(1234, 543, 6789, 5134), 0));
        subsetDef.setSubSampling(1, 1);

        ProductReader reader = buildProductReader();
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

    @Test
    public void testReadProductGeometrySubset() throws IOException, ConversionException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Eritrea" + File.separator + "13N041E-R1C2_2012_RE1_3a-3M_1234567890_metadata.xml");

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((41.5560188293457 13.981417655944824, 41.585662841796875 13.98110294342041, 41.61531066894531 13.980785369873047, 41.644954681396484 13.980463981628418, 41.674598693847656 13.98013973236084, 41.70423889160156 13.97981071472168, 41.733882904052734 13.97947883605957, 41.76352310180664 13.979143142700195, 41.79316329956055 13.978803634643555, 41.82280349731445 13.978461265563965, 41.85244369506836 13.978114128112793, 41.86996841430664 13.97790813446045, 41.86960983276367 13.948965072631836, 41.8692512512207 13.920021057128906, 41.868896484375 13.891077995300293, 41.86853790283203 13.86213493347168, 41.86818313598633 13.83319091796875, 41.867828369140625 13.804247856140137, 41.86747741699219 13.775303840637207, 41.867122650146484 13.746360778808594, 41.86711883544922 13.746089935302734, 41.849613189697266 13.746293067932129, 41.820003509521484 13.746633529663086, 41.7903938293457 13.746970176696777, 41.760780334472656 13.74730396270752, 41.731170654296875 13.747633934020996, 41.70155715942383 13.747960090637207, 41.67194366455078 13.748283386230469, 41.64232635498047 13.748601913452148, 41.61271286010742 13.748917579650879, 41.58309555053711 13.74923038482666, 41.55348205566406 13.74953842163086, 41.55348205566406 13.749810218811035, 41.55379867553711 13.77876091003418, 41.55411148071289 13.80771255493164, 41.55442810058594 13.836663246154785, 41.554744720458984 13.86561393737793, 41.55506134033203 13.89456558227539, 41.555381774902344 13.923516273498535, 41.55569839477539 13.95246696472168, 41.5560188293457 13.981417655944824))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"green", "blue"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);

        ProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(productFile, subsetDef);

        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(6791, product.getSceneRasterWidth());
        assertEquals(5136, product.getSceneRasterHeight());
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
        assertEquals(34878576, band.getNumDataElems());
        assertEquals("green", band.getName());
        assertEquals(6791, band.getRasterWidth());
        assertEquals(5136, band.getRasterHeight());

        assertEquals(77, band.getSampleInt(0, 0));
        assertEquals(75, band.getSampleInt(123, 123));
        assertEquals(87, band.getSampleInt(23, 2000));
        assertEquals(80, band.getSampleInt(1453, 2871));
        assertEquals(79, band.getSampleInt(153, 800));
        assertEquals(84, band.getSampleInt(1542, 2101));
        assertEquals(38, band.getSampleInt(3744, 686));
        assertEquals(62, band.getSampleInt(5769, 1016));
        assertEquals(75, band.getSampleInt(500, 500));
        assertEquals(49, band.getSampleInt(6789, 5134));
        assertEquals(0, band.getSampleInt(6659, 154));
    }

    private static ProductReader buildProductReader() {
        RapidEyeL3ReaderPlugin plugin = new RapidEyeL3ReaderPlugin();
        return plugin.createReaderInstance();
    }
}
