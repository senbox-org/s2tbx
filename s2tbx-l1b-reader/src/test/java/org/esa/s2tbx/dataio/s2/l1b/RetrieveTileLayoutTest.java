package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.utils.TestUtil;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Nicolas Ducoin
 */
public class RetrieveTileLayoutTest {

    private Path sentinel2TestProductsPath;

    private static final String SENTINEL2_DIR = "Sentinel2";

    private static final String L1B_PRODUCT_NAME = "L1B/S2A_OPER_PRD_MSIL1B_PDMC_20150704T101016_R062_V20150627T103414_20150627T103417.SAFE/S2A_OPER_MTD_SAFL1B_PDMC_20150704T101016_R062_V20150627T103414_20150627T103417.xml";

    /**
     * Run these tests only if Sentinel 2 products test directory exists and is set
     */
    @Before
    public void updateDataPath() {
        String productPath = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        sentinel2TestProductsPath = Paths.get(productPath, SENTINEL2_DIR);

        Assume.assumeTrue(Files.exists(sentinel2TestProductsPath));
    }

    @Test
    public void testRetrieveLayoutForL1B10m() {
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);

        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, S2SpatialResolution.R10M);

        TileLayout retrievedTileLayout = productReader.retrieveTileLayoutFromProduct(productPath, S2SpatialResolution.R10M);

        TileLayout realTileLayout = new TileLayout(2552, 18432, 2592, 2304, 1, 8, 5);

        Assert.assertTrue(retrievedTileLayout!= null && retrievedTileLayout.equals(realTileLayout));
    }



    @Test
    public void testRetrieveLayoutForL1B20m() {
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);

        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, S2SpatialResolution.R20M);
        TileLayout retrievedTileLayout = productReader.retrieveTileLayoutFromProduct(productPath, S2SpatialResolution.R20M);

        TileLayout realTileLayout = new TileLayout(1276, 9216, 1296, 1152, 1, 8, 5);

        Assert.assertTrue(retrievedTileLayout!= null && retrievedTileLayout.equals(realTileLayout));
    }



    @Test
    public void testRetrieveLayoutForL1B60m() {
        Path productPath = sentinel2TestProductsPath.resolve(L1B_PRODUCT_NAME);

        Sentinel2L1BProductReader productReader = new Sentinel2L1BProductReader(null, S2SpatialResolution.R60M);
        TileLayout retrievedTileLayout = productReader.retrieveTileLayoutFromProduct(productPath, S2SpatialResolution.R60M);

        TileLayout realTileLayout =  new TileLayout(1276, 3072, 1296, 384, 1, 8, 5);

        Assert.assertTrue(retrievedTileLayout!= null && retrievedTileLayout.equals(realTileLayout));
    }

}
