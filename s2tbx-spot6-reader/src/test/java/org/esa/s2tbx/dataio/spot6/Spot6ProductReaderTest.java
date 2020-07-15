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

import com.bc.ceres.binding.ConversionException;
import com.bc.ceres.core.NullProgressMonitor;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.subset.GeometrySubsetRegion;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.core.util.converters.JtsGeometryConverter;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;

import java.awt.Rectangle;
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
public class Spot6ProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_spot6_7" + File.separator;

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testGetReaderPlugin() {
        Spot6ProductReader reader = buildProductReader();
        assertEquals(Spot6ProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT6_1.5m_short" + File.separator + "SPOT_LIST.XML");

        Spot6ProductReader reader = buildProductReader();

        Product finalProduct = reader.readProductNodes(file, null);
        assertNotNull(finalProduct);
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
    }

    @Test
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT6_1.5m_short" + File.separator + "SPOT_LIST.XML");

        Rectangle subsetRegion = new Rectangle(540, 90, 1366, 631);
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B1", "B3", "NODATA"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
        subsetDef.setSubSampling(1, 1);

        Spot6ProductReader reader = buildProductReader();

        Product finalProduct = reader.readProductNodes(file, subsetDef);
        assertNotNull(finalProduct);
        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(39.8534f, productOrigin.lat, 4);
        assertEquals(-83.7753f, productOrigin.lon, 4);

        assertEquals(2, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals("SPOT 6/7 Product", finalProduct.getProductType());
        assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(1366, finalProduct.getSceneRasterWidth());
        assertEquals(631, finalProduct.getSceneRasterHeight());
        //name should be changed
        assertEquals("SPOT_20140129050233095_816009101_2", finalProduct.getName());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

        Mask mask = finalProduct.getMaskGroup().get("NODATA");
        assertEquals(1366, mask.getRasterWidth());
        assertEquals(631, mask.getRasterHeight());

        Band band_B1 = finalProduct.getBand("B1");
        assertEquals(1366, band_B1.getRasterWidth());
        assertEquals(631, band_B1.getRasterHeight());

        float pixelValue = band_B1.getSampleFloat(105, 143);
        assertEquals(38.8288f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(208, 246);
        assertEquals(85.3185f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(467, 221);
        assertEquals(154.5807f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(714, 389);
        assertEquals(30.2235f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(824, 344);
        assertEquals(39.1436f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(1035, 336);
        assertEquals(55.9345f, pixelValue, 4);

        Band band_B3 = finalProduct.getBand("B3");
        assertEquals(1366, band_B3.getRasterWidth());
        assertEquals(631, band_B3.getRasterHeight());

        pixelValue = band_B3.getSampleFloat(105, 143);
        assertEquals(36.5549f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(208, 246);
        assertEquals(81.8917f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(467, 221);
        assertEquals(121.2311f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(714, 389);
        assertEquals(46.2649f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(824, 344);
        assertEquals(84.9617f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(1035, 336);
        assertEquals(66.3986f, pixelValue, 4);
    }

    @Test
    public void testReadProductGeometrySubset() throws IOException, ConversionException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT6_1.5m_short" + File.separator + "SPOT_LIST.XML");
        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((-83.78767395019531 42.67092514038086, -83.78624725341797 42.67088317871094, -83.78482055664062 42.67084503173828, -83.78339385986328 42.67080307006836, -83.78196716308594 42.6707649230957, -83.7805404663086 42.67072296142578, -83.77911376953125 42.67068099975586, -83.77769470214844 42.6706428527832, -83.7762680053711 42.67060089111328, -83.77484130859375 42.670562744140625, -83.7734146118164 42.6705207824707, -83.77198791503906 42.67048263549805, -83.77056121826172 42.670440673828125, -83.7691421508789 42.67040252685547, -83.76771545410156 42.67036056518555, -83.76628875732422 42.67032241821289, -83.76486206054688 42.67028045654297, -83.76343536376953 42.67024230957031, -83.76270294189453 42.67021942138672, -83.76275634765625 42.669166564941406, -83.7628173828125 42.66811752319336, -83.76287078857422 42.66706466674805, -83.76292419433594 42.666011810302734, -83.76297760009766 42.66495895385742, -83.76303100585938 42.66390609741211, -83.7630844116211 42.66285705566406, -83.76314544677734 42.66180419921875, -83.76314544677734 42.66170883178711, -83.76387786865234 42.6617317199707, -83.76530456542969 42.66176986694336, -83.76673126220703 42.66181182861328, -83.76815795898438 42.66184997558594, -83.76957702636719 42.66189193725586, -83.77100372314453 42.661930084228516, -83.77243041992188 42.66197204589844, -83.77385711669922 42.662010192871094, -83.77528381347656 42.662052154541016, -83.77670288085938 42.66209411621094, -83.77812957763672 42.662132263183594, -83.77955627441406 42.662174224853516, -83.7809829711914 42.66221237182617, -83.78240966796875 42.662254333496094, -83.7838363647461 42.66229248046875, -83.7852554321289 42.66233444213867, -83.78668212890625 42.66237258911133, -83.7881088256836 42.66241455078125, -83.78810119628906 42.662506103515625, -83.78804779052734 42.66355895996094, -83.78799438476562 42.66461181640625, -83.7879409790039 42.66566467285156, -83.78788757324219 42.66671371459961, -83.78783416748047 42.66776657104492, -83.78778076171875 42.668819427490234, -83.78772735595703 42.66987228393555, -83.78767395019531 42.67092514038086))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B1", "B3", "NODATA"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);

        Spot6ProductReader reader = buildProductReader();

        Product finalProduct = reader.readProductNodes(file, subsetDef);
        assertNotNull(finalProduct);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(39.8534f, productOrigin.lat, 4);
        assertEquals(-83.7753f, productOrigin.lon, 4);

        assertEquals(2, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals("SPOT 6/7 Product", finalProduct.getProductType());
        assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(1368, finalProduct.getSceneRasterWidth());
        assertEquals(633, finalProduct.getSceneRasterHeight());
        //name should be changed
        assertEquals("SPOT_20140129050233095_816009101_2", finalProduct.getName());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

        Mask mask = finalProduct.getMaskGroup().get("NODATA");
        assertEquals(1368, mask.getRasterWidth());
        assertEquals(633, mask.getRasterHeight());

        Band band_B1 = finalProduct.getBand("B1");
        assertEquals(1368, band_B1.getRasterWidth());
        assertEquals(633, band_B1.getRasterHeight());

        float pixelValue = band_B1.getSampleFloat(105, 143);
        assertEquals(38.8288f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(208, 246);
        assertEquals(59.7124f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(467, 221);
        assertEquals(170.9518f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(714, 389);
        assertEquals(30.2235f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(824, 344);
        assertEquals(39.1436f, pixelValue, 4);
        pixelValue = band_B1.getSampleFloat(1035, 336);
        assertEquals(55.9345f, pixelValue, 4);

        Band band_B3 = finalProduct.getBand("B3");
        assertEquals(1368, band_B3.getRasterWidth());
        assertEquals(633, band_B3.getRasterHeight());

        pixelValue = band_B3.getSampleFloat(105, 143);
        assertEquals(36.5549f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(208, 246);
        assertEquals(60.3299f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(467, 221);
        assertEquals(135.1534f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(714, 389);
        assertEquals(46.2649f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(824, 344);
        assertEquals(76.6797f, pixelValue, 4);
        pixelValue = band_B3.getSampleFloat(1035, 336);
        assertEquals(66.3986f, pixelValue, 4);
    }

    @Test
    public void testReadBandRasterData() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT6_1.5m_short" + File.separator + "SPOT_LIST.XML");

        Spot6ProductReader reader = buildProductReader();

        Product finalProduct = reader.readProductNodes(file, null);
        assertNotNull(finalProduct);

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
    }

    @Test
    public void testGetProductComponentsOnFileInput() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT6_1.5m_short" + File.separator + "SPOT_LIST.XML");

        Spot6ProductReader reader = buildProductReader();

        Product finalProduct = reader.readProductNodes(file, null);
        assertNotNull(finalProduct);
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
    }

    private static Spot6ProductReader buildProductReader() {
        Spot6ProductReaderPlugin plugin = new Spot6ProductReaderPlugin();
        return new Spot6ProductReader(plugin);
    }
}
