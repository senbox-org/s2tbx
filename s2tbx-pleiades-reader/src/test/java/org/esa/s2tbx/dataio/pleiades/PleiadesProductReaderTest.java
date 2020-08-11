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

import com.bc.ceres.binding.ConversionException;
import com.bc.ceres.core.NullProgressMonitor;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class PleiadesProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_pleiades" + File.separator;

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testGetReaderPlugin() {
        AbstractProductReader reader = buildProductReader();
        assertEquals(PleiadesProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP" + File.separator + "TPP1600462598" + File.separator + "VOL_PHR.XML");
        assertNotNull(file);

        AbstractProductReader reader = buildProductReader();

        Product finalProduct = reader.readProductNodes(file, null);
        assertNotNull(finalProduct);

        assertEquals(4, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals("Pleiades 1A/B Product", finalProduct.getProductType());
        assertEquals(3, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(33300, finalProduct.getSceneRasterWidth());
        assertEquals(4397, finalProduct.getSceneRasterHeight());
        assertEquals("ORTHO PMS DS_PHR1A_201511151131518_FR1_PX_W008N40_0103_01317", finalProduct.getName());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
    }

    @Test
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP" + File.separator + "TPP1600462598" + File.separator + "VOL_PHR.XML");
        assertNotNull(file);

        Rectangle subsetRegion = new Rectangle(16650, 675, 12601, 3722);
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B0", "B2", "SATURATED"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
        subsetDef.setSubSampling(1, 1);

        AbstractProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, subsetDef);
        assertNotNull(finalProduct);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(39.8534f, productOrigin.lat, 4);
        assertEquals(-7.9933f, productOrigin.lon, 4);

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
    }

    @Test
    public void testReadProductGeometrySubset() throws IOException, ConversionException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP" + File.separator + "TPP1600462598" + File.separator + "VOL_PHR.XML");
        assertNotNull(file);

        AbstractProductReader reader = buildProductReader();

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((-7.993321895599365 39.835411071777344, -7.990604877471924 39.83538818359375, -7.987888336181641 39.835365295410156," +
                " -7.985171318054199 39.8353385925293, -7.982454776763916 39.8353157043457, -7.979737758636475 39.83529281616211," +
                " -7.977020740509033 39.835269927978516, -7.97430419921875 39.835243225097656, -7.971587181091309 39.83522033691406," +
                " -7.968870639801025 39.83519744873047, -7.966153621673584 39.83517074584961, -7.963437080383301 39.835147857666016," +
                " -7.960720062255859 39.83512496948242, -7.958003520965576 39.83509826660156, -7.955286502838135 39.83507537841797," +
                " -7.952569961547852 39.83504867553711, -7.94985294342041 39.835025787353516, -7.947135925292969 39.83500289916992," +
                " -7.9444193840026855 39.83497619628906, -7.941702842712402 39.83495330810547, -7.938985824584961 39.83492660522461," +
                " -7.936269283294678 39.834903717041016, -7.933552265167236 39.834877014160156, -7.930835723876953 39.8348503112793," +
                " -7.928118705749512 39.8348274230957, -7.9254021644592285 39.834800720214844, -7.922685146331787 39.83477783203125," +
                " -7.919968605041504 39.83475112915039, -7.919699668884277 39.834747314453125, -7.919732570648193 39.8326530456543," +
                " -7.919765472412109 39.83055877685547, -7.919798374176025 39.82846450805664, -7.919830799102783 39.82637023925781," +
                " -7.919863700866699 39.824275970458984, -7.919896602630615 39.822181701660156, -7.919929504394531 39.82008743286133," +
                " -7.919962406158447 39.8179931640625, -7.919962406158447 39.81798553466797, -7.920230865478516 39.81798553466797," +
                " -7.922946929931641 39.81801223754883, -7.925662994384766 39.81803894042969, -7.928379535675049 39.81806182861328," +
                " -7.931095600128174 39.81808853149414, -7.933811664581299 39.818111419677734, -7.936527729034424 39.818138122558594," +
                " -7.939243793487549 39.81816101074219, -7.941959857940674 39.81818771362305, -7.944675922393799 39.81821060180664," +
                " -7.947391986846924 39.8182373046875, -7.950108051300049 39.818260192871094, -7.952824115753174 39.81828689575195," +
                " -7.955540657043457 39.81830978393555, -7.958256721496582 39.81833267211914, -7.960972785949707 39.818359375," +
                " -7.963688850402832 39.818382263183594, -7.966404914855957 39.81840896606445, -7.969120979309082 39.81843185424805," +
                " -7.971837043762207 39.81845474243164, -7.97455358505249 39.8184814453125, -7.977269649505615 39.818504333496094," +
                " -7.97998571395874 39.81852722167969, -7.982701778411865 39.81855010986328, -7.98541784286499 39.81857681274414," +
                " -7.988134384155273 39.818599700927734, -7.990850448608398 39.81862258911133, -7.993566513061523 39.81864547729492," +
                " -7.993566513061523 39.81865692138672, -7.993535995483398 39.82075119018555, -7.993505477905273 39.822845458984375," +
                " -7.993474960327148 39.8249397277832, -7.993443965911865 39.82703399658203, -7.99341344833374 39.82912826538086," +
                " -7.993382930755615 39.83122253417969, -7.99335241317749 39.833316802978516, -7.993321895599365 39.835411071777344))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B0", "B2", "SATURATED"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);

        Product finalProduct = reader.readProductNodes(file, subsetDef);
        assertNotNull(finalProduct);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(39.8534f, productOrigin.lat, 4);
        assertEquals(-7.9933f, productOrigin.lon, 4);

        assertEquals(2, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals("Pleiades 1A/B Product", finalProduct.getProductType());
        assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(12603, finalProduct.getSceneRasterWidth());
        assertEquals(3723, finalProduct.getSceneRasterHeight());
        assertEquals("ORTHO PMS DS_PHR1A_201511151131518_FR1_PX_W008N40_0103_01317", finalProduct.getName());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

        Mask mask = finalProduct.getMaskGroup().get("SATURATED");
        assertEquals(12603, mask.getRasterWidth());
        assertEquals(3723, mask.getRasterHeight());

        Band band_B0 = finalProduct.getBand("B0");
        assertEquals(12603, band_B0.getRasterWidth());
        assertEquals(3723, band_B0.getRasterHeight());

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
        assertEquals(12603, band_B2.getRasterWidth());
        assertEquals(3723, band_B2.getRasterHeight());

        pixelValue = band_B2.getSampleFloat(2995, 1116);
        assertEquals(32.9756f, pixelValue, 4);
        pixelValue = band_B2.getSampleFloat(3247, 1335);
        assertEquals(31.8048f, pixelValue, 4);
        pixelValue = band_B2.getSampleFloat(3247, 1372);
        assertEquals(39.4146f, pixelValue, 4);
        pixelValue = band_B2.getSampleFloat(4162, 1402);
        assertEquals(22.3414f, pixelValue, 4);
        pixelValue = band_B2.getSampleFloat(7046, 1470);
        assertEquals(25.3658f, pixelValue, 4);
        pixelValue = band_B2.getSampleFloat(7396, 1461);
        assertEquals(31.0243f, pixelValue, 4);
    }

    @Test
    public void testReadBandRasterData() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP" + File.separator + "TPP1600462598" + File.separator + "VOL_PHR.XML");
        assertNotNull(file);

        AbstractProductReader reader = buildProductReader();
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

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP" + File.separator + "TPP1600462598" + File.separator + "VOL_PHR.XML");
        assertNotNull(file);

        AbstractProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, null);
        assertNotNull(finalProduct);

        TreeNode<File> components = reader.getProductComponents();
        assertEquals(1, components.getChildren().length);
        assertEquals("VOL_PHR.XML", components.getChildren()[0].getId());
    }

    private static AbstractProductReader buildProductReader() {
        PleiadesProductReaderPlugin plugin = new PleiadesProductReaderPlugin();
        return new PleiadesProductReader(plugin);
    }
}
