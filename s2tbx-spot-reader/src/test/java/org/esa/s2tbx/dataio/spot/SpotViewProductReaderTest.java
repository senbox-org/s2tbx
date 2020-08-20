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
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class SpotViewProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_spot" + File.separator;

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");

        ProductReader reader = buildProductReader();
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
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"band_0", "band_1", "band_3"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(123, 500, 1567, 1765), 0));
        subsetDef.setSubSampling(1, 1);

        ProductReader reader = buildProductReader();
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

    @Test
    public void testReadProductGeometrySubset() throws IOException, ConversionException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((26.650020599365234 39.99710464477539, 26.70611572265625 39.98999786376953, 26.762208938598633 39.98289489746094, 26.81830406188965 39.97578811645508, 26.874399185180664 39.96868133544922, 26.930496215820312 39.96157455444336, 26.986591339111328 39.9544677734375, 27.042686462402344 39.94736099243164, 27.09878158569336 39.94025421142578, 27.087310791015625 39.90000534057617, 27.075838088989258 39.85975646972656, 27.06436538696289 39.81950378417969, 27.052892684936523 39.77925491333008, 27.04142189025879 39.7390022277832, 27.029949188232422 39.698753356933594, 27.018476486206055 39.658504486083984, 27.007003784179688 39.61825180053711, 26.99553108215332 39.5780029296875, 26.939729690551758 39.5850830078125, 26.883926391601562 39.5921630859375, 26.828125 39.5992431640625, 26.772323608398438 39.606327056884766, 26.716520309448242 39.613407135009766, 26.66071891784668 39.620487213134766, 26.604917526245117 39.627567291259766, 26.549118041992188 39.634647369384766, 26.56032943725586 39.67491912841797, 26.57154083251953 39.71519470214844, 26.582752227783203 39.75546646118164, 26.593963623046875 39.795738220214844, 26.605175018310547 39.83601379394531, 26.61638641357422 39.876285552978516, 26.62759780883789 39.916561126708984, 26.638809204101562 39.95683288574219, 26.650020599365234 39.99710464477539))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"band_0", "band_1", "band_3"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);

        ProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(productFile, subsetDef);

        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("40972700506050900111I", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(1572, product.getSceneRasterWidth());
        assertEquals(1766, product.getSceneRasterHeight());
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
        assertEquals(2776152, band.getNumDataElems());
        assertEquals("band_1", band.getName());
        assertEquals(1572, band.getRasterWidth());
        assertEquals(1766, band.getRasterHeight());

        assertEquals(58, band.getSampleInt(0, 0));
        assertEquals(54, band.getSampleInt(1000, 1000));
        assertEquals(63, band.getSampleInt(200, 1200));
        assertEquals(64, band.getSampleInt(100, 323));
        assertEquals(125, band.getSampleInt(149, 311));
        assertEquals(35, band.getSampleInt(328, 944));
        assertEquals(48, band.getSampleInt(1301, 427));
        assertEquals(87, band.getSampleInt(1439, 1532));
    }

    private static ProductReader buildProductReader() {
        SpotViewProductReaderPlugin plugin = new SpotViewProductReaderPlugin();
        return plugin.createReaderInstance();
    }
}
