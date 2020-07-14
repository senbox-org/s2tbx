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
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class SpotDimapProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_spot" + File.separator;

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testGetReaderPlugin() {
        ProductReader reader = buildProductReader();
        assertEquals(SpotDimapProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodesBySimpleProductReader() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "30382639609301123571X0_1A_NETWORK.ZIP");

        ProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, null);

        assertEquals(finalProduct.getProductReader().getClass(), SpotDimapProductReader.class);
        assertEquals(4, finalProduct.getBands().length);
        assertEquals("WGS84(DD)", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals("SPOTSCENE_1A", finalProduct.getProductType());
        assertEquals(2, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(3000, finalProduct.getSceneRasterWidth());
        assertEquals(3000, finalProduct.getSceneRasterHeight());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
    }

    @Test
    public void testGetProductComponentsOnVolumeFileInputBySimpleProductReader() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "vol_list.dim");

        SpotDimapProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, null);

        assertEquals(finalProduct.getProductReader().getClass(), SpotDimapProductReader.class);
        TreeNode<File> components = reader.getProductComponents();
        assertEquals(4, components.getChildren().length);
        String[] expectedIds = new String[]{"metadata.dim", "vol_list.dim", "mediumImage.tif", "icon.jpg"};
        int componentsAsExpected = 0;
        for (TreeNode<File> component : components.getChildren()) {
            for (String expectedValue : expectedIds) {
                if (component.getId().toLowerCase().equals(expectedValue.toLowerCase())) {
                    componentsAsExpected++;
                }
            }
        }
        assertEquals(4, componentsAsExpected);
    }

    @Test
    public void testGetProductComponentsOnArchiveInputBySimpleProductReader() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "30382639609301123571X0_1A_NETWORK.ZIP");

        SpotDimapProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, null);

        assertEquals(finalProduct.getProductReader().getClass(), SpotDimapProductReader.class);
        TreeNode<File> components = reader.getProductComponents();
        assertEquals(1, components.getChildren().length);
        assertEquals("30382639609301123571X0_1A_NETWORK.ZIP", components.getChildren()[0].getId());
    }

    @Test
    public void testReadProductNodesByVolumeProductReader() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT-5_2.5mc_3" + File.separator + "VOL_LIST.DIM");

        ProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, null);

        assertEquals(finalProduct.getProductReader().getClass(), SpotDimapProductReader.class);
        assertEquals(3, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals("SPOTDimap", finalProduct.getProductType());
        assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(31770, finalProduct.getSceneRasterWidth());
        assertEquals(30620, finalProduct.getSceneRasterHeight());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
    }

    @Test
    public void testGetProductComponentsOnVolumeFileInputByVolumeProductReader() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT-5_2.5mc_3" + File.separator + "VOL_LIST.DIM");

        SpotDimapProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, null);

        assertEquals(finalProduct.getProductReader().getClass(), SpotDimapProductReader.class);
        TreeNode<File> components = reader.getProductComponents();
        assertEquals(7, components.getChildren().length);
        String[] expectedIds = new String[]{"vol_list.dim", "SPVIEW01_0_0" + File.separator + "ICON_0_0.JPG", "SPVIEW01_0_0" + File.separator + "IMAGERY_0_0.TIF",
                "SPVIEW01_0_0" + File.separator + "METADATA_0_0.dim", "SPVIEW01_0_1" + File.separator + "ICON_0_1.JPG", "SPVIEW01_0_1" + File.separator + "IMAGERY_0_1.TIF",
                "SPVIEW01_0_1" + File.separator + "METADATA_0_1.dim"};
        int componentsAsExpected = 0;
        for (TreeNode<File> component : components.getChildren()) {
            for (String expectedValue : expectedIds) {
                if (component.getId().toLowerCase().replace("/", "|").replace("\\", "|").equals(expectedValue.toLowerCase().replace("/", "|").replace("\\", "|"))) {
                    componentsAsExpected++;
                    break;
                }
            }
        }
        assertEquals(7, componentsAsExpected);
    }

    @Test
    public void testReaderProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "30382639609301123571X0_1A_NETWORK.ZIP");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"XS1", "SWIR", "SATURATED"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(800, 540, 1721, 1801), 0));
        subsetDef.setSubSampling(1, 1);

        SpotDimapProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, subsetDef);

        assertEquals(finalProduct.getProductReader().getClass(), SpotDimapProductReader.class);
        TreeNode<File> components = reader.getProductComponents();
        assertEquals(1, components.getChildren().length);
        assertEquals("30382639609301123571X0_1A_NETWORK.ZIP", components.getChildren()[0].getId());
        assertEquals(2, finalProduct.getBands().length);
        assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(1721, finalProduct.getSceneRasterWidth());
        assertEquals(1801, finalProduct.getSceneRasterHeight());

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(33.6105f, productOrigin.lat, 4);
        assertEquals(6.5712f, productOrigin.lon, 4);

        Mask mask = finalProduct.getMaskGroup().get("SATURATED");
        assertEquals(1721, mask.getRasterWidth());
        assertEquals(1801, mask.getRasterHeight());

        Band band_XS1 = finalProduct.getBand("XS1");
        assertEquals(1721, band_XS1.getRasterWidth());
        assertEquals(1801, band_XS1.getRasterHeight());

        float pixelValue = band_XS1.getSampleFloat(232, 332);
        assertEquals(134.0f, pixelValue, 0);
        pixelValue = band_XS1.getSampleFloat(855, 1298);
        assertEquals(136.0f, pixelValue, 0);
        pixelValue = band_XS1.getSampleFloat(1481, 1075);
        assertEquals(109.0f, pixelValue, 0);
        pixelValue = band_XS1.getSampleFloat(1444, 333);
        assertEquals(140.0f, pixelValue, 0);
        pixelValue = band_XS1.getSampleFloat(1548, 1037);
        assertEquals(101.0f, pixelValue, 0);

        Band band_SWIR = finalProduct.getBand("SWIR");
        assertEquals(1721, band_SWIR.getRasterWidth());
        assertEquals(1801, band_SWIR.getRasterHeight());

        pixelValue = band_SWIR.getSampleFloat(232, 332);
        assertEquals(153.0f, pixelValue, 0);
        pixelValue = band_SWIR.getSampleFloat(855, 1298);
        assertEquals(155.0f, pixelValue, 0);
        pixelValue = band_SWIR.getSampleFloat(1481, 1075);
        assertEquals(118.0f, pixelValue, 0);
        pixelValue = band_SWIR.getSampleFloat(1444, 333);
        assertEquals(147.0f, pixelValue, 0);
        pixelValue = band_SWIR.getSampleFloat(1548, 1037);
        assertEquals(115.0f, pixelValue, 0);
    }

    @Test
    public void testReaderProductGeometrySubset() throws ConversionException, IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "30382639609301123571X0_1A_NETWORK.ZIP");

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((6.563314914703369 33.60757827758789, 6.611313343048096 33.600948333740234, 6.6593122482299805 33.59431838989258, 6.707311153411865 33.58768844604492, 6.755309581756592 33.581058502197266, 6.803308486938477 33.57442855834961, 6.851306915283203 33.56779861450195, 6.899305820465088 33.5611686706543, 6.9473042488098145 33.55453872680664, 6.937067985534668 33.51668167114258, 6.926831245422363 33.478824615478516, 6.916594982147217 33.44096755981445, 6.906358242034912 33.40311050415039, 6.896121501922607 33.36525344848633, 6.885885238647461 33.327392578125, 6.875648498535156 33.28953552246094, 6.86541223526001 33.251678466796875, 6.861555576324463 33.2374153137207, 6.813730716705322 33.2440299987793, 6.765905857086182 33.250640869140625, 6.718080997467041 33.25725173950195, 6.670256614685059 33.26386642456055, 6.622431755065918 33.270477294921875, 6.574606895446777 33.2770881652832, 6.526782035827637 33.28369903564453, 6.478957176208496 33.290313720703125, 6.482751369476318 33.30458068847656, 6.49282169342041 33.34245681762695, 6.502892017364502 33.38032913208008, 6.512962818145752 33.41820526123047, 6.523033142089844 33.45608139038086, 6.5331034660339355 33.493953704833984, 6.543173789978027 33.531829833984375, 6.553244590759277 33.569705963134766, 6.563314914703369 33.60757827758789))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setNodeNames(new String[]{"XS1", "SWIR", "SATURATED"});
        subsetDef.setSubSampling(1, 1);

        SpotDimapProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, subsetDef);

        assertEquals(finalProduct.getProductReader().getClass(), SpotDimapProductReader.class);
        TreeNode<File> components = reader.getProductComponents();
        assertEquals(1, components.getChildren().length);
        assertEquals("30382639609301123571X0_1A_NETWORK.ZIP", components.getChildren()[0].getId());
        assertEquals(2, finalProduct.getBands().length);
        assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(1724, finalProduct.getSceneRasterWidth());
        assertEquals(1803, finalProduct.getSceneRasterHeight());

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(33.6105f, productOrigin.lat, 4);
        assertEquals(6.5712f, productOrigin.lon, 4);

        Mask mask = finalProduct.getMaskGroup().get("SATURATED");
        assertEquals(1724, mask.getRasterWidth());
        assertEquals(1803, mask.getRasterHeight());

        Band band_XS1 = finalProduct.getBand("XS1");
        assertEquals(1724, band_XS1.getRasterWidth());
        assertEquals(1803, band_XS1.getRasterHeight());

        float pixelValue = band_XS1.getSampleFloat(232, 332);
        assertEquals(135.0f, pixelValue, 0);
        pixelValue = band_XS1.getSampleFloat(855, 1298);
        assertEquals(134.0f, pixelValue, 0);
        pixelValue = band_XS1.getSampleFloat(1481, 1075);
        assertEquals(100.0f, pixelValue, 0);
        pixelValue = band_XS1.getSampleFloat(1444, 333);
        assertEquals(144.0f, pixelValue, 0);
        pixelValue = band_XS1.getSampleFloat(1548, 1037);
        assertEquals(102.0f, pixelValue, 0);

        Band band_SWIR = finalProduct.getBand("SWIR");
        assertEquals(1724, band_SWIR.getRasterWidth());
        assertEquals(1803, band_SWIR.getRasterHeight());

        pixelValue = band_SWIR.getSampleFloat(232, 332);
        assertEquals(152.0f, pixelValue, 0);
        pixelValue = band_SWIR.getSampleFloat(855, 1298);
        assertEquals(155.0f, pixelValue, 0);
        pixelValue = band_SWIR.getSampleFloat(1481, 1075);
        assertEquals(105.0f, pixelValue, 0);
        pixelValue = band_SWIR.getSampleFloat(1444, 333);
        assertEquals(144.0f, pixelValue, 0);
        pixelValue = band_SWIR.getSampleFloat(1548, 1037);
        assertEquals(103.0f, pixelValue, 0);
    }

    private static SpotDimapProductReader buildProductReader() {
        SpotDimapProductReaderPlugin plugin = new SpotDimapProductReaderPlugin();
        return (SpotDimapProductReader)plugin.createReaderInstance();
    }
}
