/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.snap.runtime.Engine;
import org.esa.snap.utils.TestUtil;
import org.junit.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Nicolas Ducoin
 */
public class RetrieveTileLayoutTest {

    private Engine engine;

    private Path sentinel2TestProductsPath;

    private static final String SENTINEL2_DIR = "Sentinel2";

    private static final String L1B_PRODUCT_NAME = "L1B/S2A_OPER_PRD_MSIL1B_PDMC_20150704T101016_R062_V20150627T103414_20150627T103417.SAFE/S2A_OPER_MTD_SAFL1B_PDMC_20150704T101016_R062_V20150627T103414_20150627T103417.xml";

    @Before
    public void setup() {
        /*
         * We need a proper Engine start so that the openjpeg activator is started
         */
        engine = Engine.start(false);

        /**
         * Run these tests only if Sentinel 2 products test directory exists and is set
         */
        String productPath = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        sentinel2TestProductsPath = Paths.get(productPath, SENTINEL2_DIR);
        Assume.assumeTrue(Files.exists(sentinel2TestProductsPath));

    }

    @After
    public void teardown() {
        if (engine != null)
            engine.stop();
    }

    @Test
    public void testRetrieveLayoutForL1B10m() {
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, Sentinel2ProductReader.ProductInterpretation.RESOLUTION_10M);
        TileLayout retrievedTileLayout = productReader.retrieveTileLayoutFromProduct(productPath, S2SpatialResolution.R10M);
        TileLayout realTileLayout = new TileLayout(2552, 18432, 2592, 2304, 1, 8, 5);
        Assert.assertTrue(retrievedTileLayout!= null && retrievedTileLayout.equals(realTileLayout));
    }

    @Test
    public void testRetrieveLayoutForL1B20m() {
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, Sentinel2ProductReader.ProductInterpretation.RESOLUTION_20M);
        TileLayout retrievedTileLayout = productReader.retrieveTileLayoutFromProduct(productPath, S2SpatialResolution.R20M);
        TileLayout realTileLayout = new TileLayout(1276, 9216, 1296, 1152, 1, 8, 5);
        Assert.assertTrue(retrievedTileLayout!= null && retrievedTileLayout.equals(realTileLayout));
    }

    @Test
    public void testRetrieveLayoutForL1B60m() {
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, Sentinel2ProductReader.ProductInterpretation.RESOLUTION_60M);
        TileLayout retrievedTileLayout = productReader.retrieveTileLayoutFromProduct(productPath, S2SpatialResolution.R60M);
        TileLayout realTileLayout =  new TileLayout(1276, 3072, 1296, 384, 1, 8, 5);
        Assert.assertTrue(retrievedTileLayout!= null && retrievedTileLayout.equals(realTileLayout));
    }

}
