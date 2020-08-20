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

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.l1b.metadata.L1bProductMetadataReader;
import org.esa.snap.lib.openjpeg.jp2.TileLayout;
import org.esa.snap.runtime.Engine;
import org.esa.snap.utils.TestUtil;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Nicolas Ducoin
 */
public class RetrieveTileLayoutTest {

    private Engine engine;

    private Path sentinel2TestProductsPath;

    private static final String SENTINEL2_DIR = "S2";

    private static final String L1B_PRODUCT_NAME = "L1B/Maricopa/S2A_OPER_PRD_MSIL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504.SAFE/S2A_OPER_MTD_SAFL1B_PDMC_20160404T102635_R084_V20160403T182456_20160403T182504.xml";

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
    public void testRetrieveLayoutForL1B10m() throws IOException {
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        VirtualPath input = new VirtualPath(productPath.getFileName().toString(), VirtualDirEx.build(productPath.getParent()));
        L1bProductMetadataReader productReader = new L1bProductMetadataReader(input);
        TileLayout retrievedTileLayout = productReader.retrieveTileLayoutFromProduct(input, S2SpatialResolution.R10M);
        TileLayout realTileLayout = new TileLayout(2552, 2304, 2592, 2304, 1, 1, 4,1);
        Assert.assertTrue(retrievedTileLayout!= null && retrievedTileLayout.equals(realTileLayout));
    }

    @Test
    public void testRetrieveLayoutForL1B20m() throws IOException {
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        VirtualPath input = new VirtualPath(productPath.getFileName().toString(), VirtualDirEx.build(productPath.getParent()));
        L1bProductMetadataReader productReader = new L1bProductMetadataReader(input);
        TileLayout retrievedTileLayout = productReader.retrieveTileLayoutFromProduct(input, S2SpatialResolution.R20M);
        TileLayout realTileLayout = new TileLayout(1276, 1152, 1296, 1152, 1, 1, 4,1);
        Assert.assertTrue(retrievedTileLayout!= null && retrievedTileLayout.equals(realTileLayout));
    }

    @Test
    public void testRetrieveLayoutForL1B60m() throws IOException {
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);
        VirtualPath input = new VirtualPath(productPath.getFileName().toString(), VirtualDirEx.build(productPath.getParent()));
        L1bProductMetadataReader productReader = new L1bProductMetadataReader(input);
        TileLayout retrievedTileLayout = productReader.retrieveTileLayoutFromProduct(input, S2SpatialResolution.R60M);
        TileLayout realTileLayout =  new TileLayout(1276, 384, 1296, 384, 1, 1, 4, 1);
        Assert.assertTrue(retrievedTileLayout!= null && retrievedTileLayout.equals(realTileLayout));
    }

}
