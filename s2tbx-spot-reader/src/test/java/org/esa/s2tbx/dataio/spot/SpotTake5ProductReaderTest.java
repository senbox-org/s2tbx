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
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.subset.GeometrySubsetRegion;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.converters.JtsGeometryConverter;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class SpotTake5ProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_spot" + File.separator;

    @Test
    public void testReadProductNodes() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT4_HRVIR1_XS_88888888_N1A.xml");

        SpotTake5ProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(productFile, null);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("SPOT4_HRVIR1_XS_20130616_N2A_JTanzanieD0000B0000", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(4000, product.getSceneRasterWidth());
        assertEquals(3750, product.getSceneRasterHeight());
        assertEquals("SPOT4Take5", product.getProductType());
        assertNotNull(product.getStartTime());
        assertNotNull(product.getEndTime());
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
        assertEquals(9000000, band.getNumDataElems());
        assertEquals("XS3", band.getName());
        assertEquals(3000, band.getRasterWidth());
        assertEquals(3000, band.getRasterHeight());

        assertEquals(66, band.getSampleInt(0, 0));
        assertEquals(52, band.getSampleInt(231, 231));
        assertEquals(81, band.getSampleInt(32, 1235));
        assertEquals(97, band.getSampleInt(400, 2134));
        assertEquals(54, band.getSampleInt(35, 983));
        assertEquals(53, band.getSampleInt(763, 2658));
        assertEquals(74, band.getSampleInt(33, 900));
        assertEquals(64, band.getSampleInt(323, 896));
        assertEquals(82, band.getSampleInt(65, 654));
        assertEquals(111, band.getSampleInt(1500, 2000));
        assertEquals(90, band.getSampleInt(345, 2234));
        assertEquals(58, band.getSampleInt(542, 2434));
        assertEquals(0, band.getSampleInt(3000, 3000));
    }

    @Test
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT4_HRVIR1_XS_88888888_N1A.tgz");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "XS1", "XS2", "SWIR" } );
        subsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(1200, 1000, 1567, 1000), 0));
        subsetDef.setSubSampling(1, 1);

        SpotTake5ProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(productFile, subsetDef);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("SPOT4_HRVIR1_XS_20130616_N2A_JTanzanieD0000B0000", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(1567, product.getSceneRasterWidth());
        assertEquals(1000, product.getSceneRasterHeight());
        assertEquals("SPOT4Take5", product.getProductType());
        assertNotNull(product.getStartTime());
        assertNotNull(product.getEndTime());
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
        assertEquals(1567000, band.getNumDataElems());
        assertEquals("XS2", band.getName());
        assertEquals(1567, band.getRasterWidth());
        assertEquals(1000, band.getRasterHeight());

        assertEquals(70, band.getSampleInt(0, 0));
        assertEquals(73, band.getSampleInt(231, 231));
        assertEquals(105, band.getSampleInt(35, 983));
        assertEquals(69, band.getSampleInt(763, 508));
        assertEquals(56, band.getSampleInt(1433, 900));
        assertEquals(92, band.getSampleInt(323, 896));
        assertEquals(48, band.getSampleInt(65, 654));
    }

    @Test
    public void testReadProductGeometrySubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT4_HRVIR1_XS_88888888_N1A.tgz");
        try {
            JtsGeometryConverter converter = new JtsGeometryConverter();
            Geometry geometry = converter.parse("POLYGON ((-1.5016114711761475 47.83796691894531, -1.465959072113037 47.831275939941406, -1.4303066730499268 47.8245849609375, -1.3946542739868164 47.81789016723633, -1.359001874923706 47.81119918823242, -1.3233494758605957 47.804508209228516, -1.287697196006775 47.797813415527344, -1.2520447969436646 47.79112243652344, -1.2163923978805542 47.78443145751953, -1.1807399988174438 47.77773666381836, -1.1450875997543335 47.77104568481445, -1.1094352006912231 47.76435470581055, -1.0737828016281128 47.757659912109375, -1.0516438484191895 47.75350570678711, -1.0621176958084106 47.73228454589844, -1.0725915431976318 47.71106719970703, -1.083065390586853 47.68984603881836, -1.0935392379760742 47.66862487792969, -1.1040129661560059 47.64740753173828, -1.114486813545227 47.62618637084961, -1.1249606609344482 47.60496520996094, -1.1354345083236694 47.58374786376953, -1.1360257863998413 47.582550048828125, -1.158097743988037 47.58668899536133, -1.1936421394348145 47.59335708618164, -1.2291865348815918 47.60002517700195, -1.2647309303283691 47.606689453125, -1.300275444984436 47.61335754394531, -1.3358198404312134 47.620025634765625, -1.3713642358779907 47.62669372558594, -1.406908631324768 47.633358001708984, -1.442453145980835 47.6400260925293, -1.4779975414276123 47.64669418334961, -1.5135419368743896 47.65336227416992, -1.549086332321167 47.66002655029297, -1.5846307277679443 47.66669464111328, -1.584049105644226 47.66789627075195, -1.5737444162368774 47.68915557861328, -1.5634396076202393 47.71041488647461, -1.5531349182128906 47.73167419433594, -1.542830228805542 47.7529296875, -1.5325255393981934 47.77418899536133, -1.5222208499908447 47.795448303222656, -1.511916160583496 47.816707611083984, -1.5016114711761475 47.83796691894531))");
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"XS1", "XS2", "SWIR"});
            subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
            subsetDef.setSubSampling(1, 1);

            SpotTake5ProductReader reader = buildProductReader();
            Product product = reader.readProductNodes(productFile, subsetDef);
            assertNotNull(product.getFileLocation());
            assertNotNull(product.getName());
            assertEquals("SPOT4_HRVIR1_XS_20130616_N2A_JTanzanieD0000B0000", product.getName());
            assertNotNull(product.getPreferredTileSize());
            assertNotNull(product.getProductReader());
            assertEquals(product.getProductReader(), reader);
            assertEquals(1568, product.getSceneRasterWidth());
            assertEquals(1001, product.getSceneRasterHeight());
            assertEquals("SPOT4Take5", product.getProductType());
            assertNotNull(product.getStartTime());
            assertNotNull(product.getEndTime());
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
            assertEquals(1569568, band.getNumDataElems());
            assertEquals("XS2", band.getName());
            assertEquals(1568, band.getRasterWidth());
            assertEquals(1001, band.getRasterHeight());

            assertEquals(53, band.getSampleInt(0, 0));
            assertEquals(92, band.getSampleInt(231, 231));
            assertEquals(90, band.getSampleInt(35, 983));
            assertEquals(88, band.getSampleInt(1533, 900));
            assertEquals(86, band.getSampleInt(323, 896));
            assertEquals(72, band.getSampleInt(65, 654));
        } catch (ConversionException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    private static SpotTake5ProductReader buildProductReader() {
        SpotTake5ProductReaderPlugin plugin = new SpotTake5ProductReaderPlugin();
        return new SpotTake5ProductReader(plugin, plugin.getColorPaletteFilePath());
    }
}
