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
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "DE01_SL6_22P_1T_20120905T170604_20120905T170613_DMI_0_4502/DE01_SL6_22P_1T_20120905T170604_20120905T170613_DMI_0_4502.dim");

        DeimosProductReader reader = buildProductReader();

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "NIR", "Red", "Green" } );
        subsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(1234, 543, 1678, 1134), 0));
        subsetDef.setSubSampling(1, 1);

        Product product = reader.readProductNodes(productFile, subsetDef);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(1678, product.getSceneRasterWidth());
        assertEquals(1134, product.getSceneRasterHeight());
        assertEquals("DEIMOS", product.getProductType());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(3, product.getBands().length);

        Band band = product.getBandAt(2);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(1902852, band.getNumDataElems());
        assertEquals("Green", band.getName());
        assertEquals(1678, band.getRasterWidth());
        assertEquals(1134, band.getRasterHeight());

        assertEquals(55, band.getSampleInt(0, 0));
        assertEquals(43, band.getSampleInt(123, 123));
        assertEquals(63, band.getSampleInt(23, 1000));
        assertEquals(100, band.getSampleInt(1428, 1116));
        assertEquals(41, band.getSampleInt(1441, 986));
        assertEquals(51, band.getSampleInt(297, 308));
        assertEquals(48, band.getSampleInt(94, 437));
        assertEquals(64, band.getSampleInt(488, 269));
        assertEquals(45, band.getSampleInt(599, 240));
        assertEquals(54, band.getSampleInt(1547, 1042));
        assertEquals(30, band.getSampleInt(1664, 1109));
        assertEquals(93, band.getSampleInt(851, 19));
    }

    @Test
    public void testReadProductGeometrySubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "DE01_SL6_22P_1T_20120905T170604_20120905T170613_DMI_0_4502"+ File.separator+ "DE01_SL6_22P_1T_20120905T170604_20120905T170613_DMI_0_4502.dim");
        try {
            DeimosProductReader reader = buildProductReader();
            JtsGeometryConverter converter = new JtsGeometryConverter();
            Geometry geometry = converter.parse("POLYGON ((-99.59724426269531 40.64783477783203, -99.56056213378906 40.64801788330078, -99.52387237548828 40.648189544677734," +
                                                        " -99.4871826171875 40.64834976196289, -99.45049285888672 40.64849853515625," +
                                                        " -99.41381072998047 40.64863586425781, -99.37712097167969 40.64876174926758," +
                                                        " -99.3404312133789 40.64887619018555, -99.30374145507812 40.64897918701172," +
                                                        " -99.26705169677734 40.649070739746094, -99.23036193847656 40.64915084838867," +
                                                        " -99.19367218017578 40.64921569824219, -99.1606216430664 40.649269104003906," +
                                                        " -99.16055297851562 40.62132263183594, -99.16049194335938 40.59337615966797," +
                                                        " -99.1604232788086 40.5654296875, -99.16035461425781 40.5374870300293," +
                                                        " -99.16028594970703 40.50954055786133, -99.16022491455078 40.48159408569336," +
                                                        " -99.16015625 40.45364761352539, -99.16008758544922 40.42570114135742," +
                                                        " -99.16008758544922 40.42451095581055, -99.19302368164062 40.424461364746094," +
                                                        " -99.2295913696289 40.42439651489258, -99.26615905761719 40.42431640625," +
                                                        " -99.30272674560547 40.42422866821289, -99.33929443359375 40.42412567138672," +
                                                        " -99.37586212158203 40.42401123046875, -99.41242980957031 40.42388916015625," +
                                                        " -99.44898986816406 40.42375183105469, -99.48555755615234 40.42360305786133," +
                                                        " -99.52212524414062 40.42344284057617, -99.5586929321289 40.423274993896484," +
                                                        " -99.59525299072266 40.423091888427734, -99.59526824951172 40.42428207397461," +
                                                        " -99.59551239013672 40.45222473144531, -99.59575653076172 40.48017120361328," +
                                                        " -99.59600830078125 40.508113861083984, -99.59625244140625 40.53606033325195," +
                                                        " -99.59650421142578 40.564002990722656, -99.59674835205078 40.59194564819336," +
                                                        " -99.59700012207031 40.61989212036133, -99.59724426269531 40.64783477783203))");
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"NIR", "Red", "Green"});
            subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
            subsetDef.setSubSampling(1, 1);

            Product product = reader.readProductNodes(productFile, subsetDef);
            assertNotNull(product.getFileLocation());
            assertNotNull(product.getName());
            assertNotNull(product.getPreferredTileSize());
            assertNotNull(product.getProductReader());
            assertEquals(product.getProductReader(), reader);
            assertEquals(1680, product.getSceneRasterWidth());
            assertEquals(1136, product.getSceneRasterHeight());
            assertEquals("DEIMOS", product.getProductType());

            GeoCoding geoCoding = product.getSceneGeoCoding();
            assertNotNull(geoCoding);
            CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
            assertNotNull(coordinateReferenceSystem);
            assertNotNull(coordinateReferenceSystem.getName());
            assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

            assertEquals(0, product.getMaskGroup().getNodeCount());

            assertEquals(3, product.getBands().length);

            Band band = product.getBandAt(2);
            assertNotNull(band);
            assertEquals(20, band.getDataType());
            assertEquals(1908480, band.getNumDataElems());
            assertEquals("Green", band.getName());
            assertEquals(1680, band.getRasterWidth());
            assertEquals(1136, band.getRasterHeight());

            assertEquals(55, band.getSampleInt(0, 0));
            assertEquals(44, band.getSampleInt(123, 123));
            assertEquals(59, band.getSampleInt(23, 1000));
            assertEquals(89, band.getSampleInt(1376, 1000));
            assertEquals(40, band.getSampleInt(1441, 986));
            assertEquals(50, band.getSampleInt(297, 308));
            assertEquals(49, band.getSampleInt(94, 437));
            assertEquals(65, band.getSampleInt(488, 269));
            assertEquals(45, band.getSampleInt(599, 240));
            assertEquals(54, band.getSampleInt(1547, 1042));
            assertEquals(31, band.getSampleInt(1664, 1109));
            assertEquals(95, band.getSampleInt(851, 19));
        } catch (ConversionException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    private static DeimosProductReader buildProductReader() {
        DeimosProductReaderPlugin plugin = new DeimosProductReaderPlugin();
        return new DeimosProductReader(plugin, plugin.getColorPaletteFilePath());
    }
}
