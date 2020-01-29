package org.esa.s2tbx.dataio.s2.ortho;

import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn;
import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM34S_ReaderPlugIn;
import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM36N_ReaderPlugIn;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.runtime.Engine;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Denisa Stefanescu
 */
public class Sentinel2OrthoProductReaderTest {

    private Engine engine;

    private Path sentinel2TestProductsPath;

    private static final String SENTINEL2_DIR = "S2";

    private static final String L1C_PRODUCT_NAME = "L1C/S2A_MSIL1C_20161206T080312_N0204_R035_T34HFH_20161206T081929.SAFE/MTD_MSIL1C.xml";
    private static final String L2A_PRODUCT_NAME = "L2A/S2B_MSIL2A_20190528T085609_N0212_R007_T36VWK_20190528T121447.zip";
    private static final String L3_PRODUCT_NAME = "L3/Darmstadt/10m/S2A_USER_PRD_MSIL03_PDMC_20150812T193220_R108_V20161231T235959_20161231T235959.SAFE/S2A_USER_MTD_SAFL03_PDMC_20150812T193220_R108_V20150730T103914_20150730T103914.xml";

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
        assumeTrue(Files.exists(sentinel2TestProductsPath));
    }

    @After
    public void teardown() {
        if (engine != null) {
            engine.stop();
        }
    }

    @Test
    public void testReadProductL1CSubset(){
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L1C_PRODUCT_NAME);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM34S_ReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();
        try {
            Rectangle subsetRegion = new Rectangle(6142, 5402, 4663, 3035);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"B1", "B2", "B5", "view_zenith_B4", "view_azimuth_B5", "detector_footprint-B8A-01", "msi_lost_B5"});
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);
            subsetDef.setIgnoreMetadata(true);

            Product finalProduct = reader.readProductNodes(productPath.toFile(),subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(-33.9139f, productOrigin.lat,4);
            assertEquals(22.7461f, productOrigin.lon,4);

            assertEquals(5, finalProduct.getBands().length);
            assertEquals("S2_MSI_Level-1C", finalProduct.getProductType());
            assertNull(finalProduct.getMetadataRoot().getElementGroup());
            assertEquals(2, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(4663, finalProduct.getSceneRasterWidth());
            assertEquals(3035, finalProduct.getSceneRasterHeight());
            assertEquals("06-DEC-2016 08:03:12.026000", finalProduct.getStartTime().toString());
            assertEquals("06-DEC-2016 08:03:12.026000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("S2A_MSIL1C_20161206T080312_N0204_R035_T34HFH_20161206T081929", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Mask mask_detector_footprint = finalProduct.getMaskGroup().get("detector_footprint-B8A-01");
            assertEquals(4663, mask_detector_footprint.getRasterWidth());
            assertEquals(3035, mask_detector_footprint.getRasterHeight());

            Mask mask_msi_lost = finalProduct.getMaskGroup().get("msi_lost_B5");
            assertEquals(2331, mask_msi_lost.getRasterWidth());
            assertEquals(1517, mask_msi_lost.getRasterHeight());

            Band band_B1 = finalProduct.getBand("B1");
            assertEquals(777, band_B1.getRasterWidth());
            assertEquals(505, band_B1.getRasterHeight());

            float pixelValue = band_B1.getSampleFloat(13, 93);
            assertEquals(0.1149f, pixelValue, 4);
            pixelValue = band_B1.getSampleFloat(135, 291);
            assertEquals(0.1261f, pixelValue, 4);
            pixelValue = band_B1.getSampleFloat(247, 176);
            assertEquals(0.1098f, pixelValue, 4);
            pixelValue = band_B1.getSampleFloat(526, 258);
            assertEquals(0.1488f, pixelValue, 4);
            pixelValue = band_B1.getSampleFloat(740, 361);
            assertEquals(0.1243f, pixelValue, 4);

            Band band_B2 = finalProduct.getBand("B2");
            assertEquals(4663, band_B2.getRasterWidth());
            assertEquals(3035, band_B2.getRasterHeight());

            pixelValue = band_B2.getSampleFloat(127, 406);
            assertEquals(0.1302f, pixelValue, 4);
            pixelValue = band_B2.getSampleFloat(1376, 1016);
            assertEquals(0.0784f, pixelValue, 4);
            pixelValue = band_B2.getSampleFloat(3756, 1277);
            assertEquals(0.1046f, pixelValue, 4);
            pixelValue = band_B2.getSampleFloat(2010, 1965);
            assertEquals(0.1636f, pixelValue, 4);
            pixelValue = band_B2.getSampleFloat(3721, 2345);
            assertEquals(0.0900f, pixelValue, 4);

            Band band_B5 = finalProduct.getBand("B5");
            assertEquals(2331, band_B5.getRasterWidth());
            assertEquals(1517, band_B5.getRasterHeight());

            pixelValue = band_B5.getSampleFloat(240, 679);
            assertEquals(0.1793f, pixelValue, 4);
            pixelValue = band_B5.getSampleFloat(1019, 296);
            assertEquals(0.1765f, pixelValue, 4);
            pixelValue = band_B5.getSampleFloat(1749, 344);
            assertEquals(0.0877f, pixelValue, 4);
            pixelValue = band_B5.getSampleFloat(807, 1052);
            assertEquals(0.0237f, pixelValue, 4);
            pixelValue = band_B5.getSampleFloat(1223, 780);
            assertEquals(0.0396f, pixelValue, 4);

            Band band_view_zenith_B4 = finalProduct.getBand("view_zenith_B4");
            assertEquals(9, band_view_zenith_B4.getRasterWidth());
            assertEquals(6, band_view_zenith_B4.getRasterHeight());

            pixelValue = band_view_zenith_B4.getSampleFloat(0, 1);
            assertEquals(11.1507f, pixelValue, 4);
            pixelValue = band_view_zenith_B4.getSampleFloat(1, 4);
            assertEquals(10.4637f, pixelValue, 4);
            pixelValue = band_view_zenith_B4.getSampleFloat(5, 4);
            assertEquals(9.0580f, pixelValue, 4);
            pixelValue = band_view_zenith_B4.getSampleFloat(7, 4);
            assertEquals(8.3039f, pixelValue, 4);
            pixelValue = band_view_zenith_B4.getSampleFloat(8, 3);
            assertEquals(7.4456f, pixelValue, 4);

            Band band_view_azimuth_B5 = finalProduct.getBand("view_azimuth_B5");
            assertEquals(9, band_view_azimuth_B5.getRasterWidth());
            assertEquals(6, band_view_azimuth_B5.getRasterHeight());

            pixelValue = band_view_azimuth_B5.getSampleFloat(0, 1);
            assertEquals(108.2130f, pixelValue, 4);
            pixelValue = band_view_azimuth_B5.getSampleFloat(1, 4);
            assertEquals(108.5850f, pixelValue, 4);
            pixelValue = band_view_azimuth_B5.getSampleFloat(5, 4);
            assertEquals(93.8128f, pixelValue, 4);
            pixelValue = band_view_azimuth_B5.getSampleFloat(7, 4);
            assertEquals(93.0096f, pixelValue, 4);
            pixelValue = band_view_azimuth_B5.getSampleFloat(8, 3);
            assertEquals(92.8102f, pixelValue, 4);
        }catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductL2ASubset(){
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L2A_PRODUCT_NAME);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM36N_ReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();
        try {
            Rectangle subsetRegion = new Rectangle(2516, 1924, 6883, 5773);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"B3", "B6", "B9", "view_zenith_mean", "view_azimuth_B2", "quality_aot", "quality_wvp", "ancillary_lost_B8A", "detector_footprint-B01-01"});
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);
            subsetDef.setIgnoreMetadata(false);

            Product finalProduct = reader.readProductNodes(productPath.toFile(), subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(58.4671f, productOrigin.lat,4);
            assertEquals(33.4309f, productOrigin.lon,4);

            assertEquals(7, finalProduct.getBands().length);
            assertEquals("S2_MSI_Level-2A", finalProduct.getProductType());
            assertNotNull(finalProduct.getMetadataRoot().getElementGroup());
            assertEquals(2, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(6883, finalProduct.getSceneRasterWidth());
            assertEquals(5773, finalProduct.getSceneRasterHeight());
            assertEquals("28-MAY-2019 08:56:09.024000", finalProduct.getStartTime().toString());
            assertEquals("28-MAY-2019 08:56:09.024000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("S2B_MSIL2A_20190528T085609_N0212_R007_T36VWK_20190528T121447", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Mask mask_detector_footprint = finalProduct.getMaskGroup().get("detector_footprint-B01-01");
            assertEquals(6883, mask_detector_footprint.getRasterWidth());
            assertEquals(5773, mask_detector_footprint.getRasterHeight());

            Mask mask_ancillary_lost = finalProduct.getMaskGroup().get("ancillary_lost_B8A");
            assertEquals(6883, mask_ancillary_lost.getRasterWidth());
            assertEquals(5773, mask_ancillary_lost.getRasterHeight());

            Band band_B3 = finalProduct.getBand("B3");
            assertEquals(6883, band_B3.getRasterWidth());
            assertEquals(5773, band_B3.getRasterHeight());

            float pixelValue = band_B3.getSampleFloat(366, 468);
            assertEquals(0.5508f, pixelValue, 4);
            pixelValue = band_B3.getSampleFloat(1406, 2104);
            assertEquals(0.0547f, pixelValue, 4);
            pixelValue = band_B3.getSampleFloat(3360, 5120);
            assertEquals(0.0560f, pixelValue, 4);
            pixelValue = band_B3.getSampleFloat(4635, 4501);
            assertEquals(0.0520f, pixelValue, 4);
            pixelValue = band_B3.getSampleFloat(6789, 5167);
            assertEquals(0.0711f, pixelValue, 4);

            Band band_B6 = finalProduct.getBand("B6");
            assertEquals(3441, band_B6.getRasterWidth());
            assertEquals(2886, band_B6.getRasterHeight());

            pixelValue = band_B6.getSampleFloat(323, 374);
            assertEquals(0.1545f, pixelValue, 4);
            pixelValue = band_B6.getSampleFloat(854, 2498);
            assertEquals(0.3611f, pixelValue, 4);
            pixelValue = band_B6.getSampleFloat(1363, 1498);
            assertEquals(0.2595f, pixelValue, 4);
            pixelValue = band_B6.getSampleFloat(2034, 816);
            assertEquals(0.6975f, pixelValue, 4);
            pixelValue = band_B6.getSampleFloat(2506, 1244);
            assertEquals(0.1791f, pixelValue, 4);

            Band band_B9 = finalProduct.getBand("B9");
            assertEquals(1147, band_B9.getRasterWidth());
            assertEquals(962, band_B9.getRasterHeight());

            pixelValue = band_B9.getSampleFloat(80, 165);
            assertEquals(0.4271f, pixelValue, 4);
            pixelValue = band_B9.getSampleFloat(433, 562);
            assertEquals(0.0146f, pixelValue, 4);
            pixelValue = band_B9.getSampleFloat(585, 211);
            assertEquals(1.1795f, pixelValue, 4);
            pixelValue = band_B9.getSampleFloat(687, 462);
            assertEquals(0.3591f, pixelValue, 4);
            pixelValue = band_B9.getSampleFloat(1053, 501);
            assertEquals(0.2303f, pixelValue, 4);

            Band band_view_zenith_mean = finalProduct.getBand("view_zenith_mean");
            assertEquals(13, band_view_zenith_mean.getRasterWidth());
            assertEquals(11, band_view_zenith_mean.getRasterHeight());

            pixelValue = band_view_zenith_mean.getSampleFloat(2, 2);
            assertEquals(9.0420f, pixelValue, 4);
            pixelValue = band_view_zenith_mean.getSampleFloat(3, 3);
            assertEquals(8.5536f, pixelValue, 4);
            pixelValue = band_view_zenith_mean.getSampleFloat(9, 9);
            assertEquals(5.6173f, pixelValue, 4);
            pixelValue = band_view_zenith_mean.getSampleFloat(12, 8);
            assertEquals(4.2239f, pixelValue, 4);

            Band band_view_azimuth_B2 = finalProduct.getBand("view_azimuth_B2");
            assertEquals(13, band_view_azimuth_B2.getRasterWidth());
            assertEquals(11, band_view_azimuth_B2.getRasterHeight());

            pixelValue = band_view_azimuth_B2.getSampleFloat(1, 1);
            assertEquals(103.4850f, pixelValue, 4);
            pixelValue = band_view_azimuth_B2.getSampleFloat(4, 7);
            assertEquals(111.7490f, pixelValue, 4);
            pixelValue = band_view_azimuth_B2.getSampleFloat(6, 4);
            assertEquals(112.2470f, pixelValue, 4);
            pixelValue = band_view_azimuth_B2.getSampleFloat(9, 5);
            assertEquals(113.9020f, pixelValue, 4);

            Band band_quality_aot = finalProduct.getBand("quality_aot");
            assertEquals(6883, band_quality_aot.getRasterWidth());
            assertEquals(5773, band_quality_aot.getRasterHeight());

            pixelValue = band_quality_aot.getSampleFloat(1480, 425);
            assertEquals(0.0830f, pixelValue, 4);
            pixelValue = band_quality_aot.getSampleFloat(1486, 4312);
            assertEquals(0.0810f, pixelValue, 4);
            pixelValue = band_quality_aot.getSampleFloat(3296, 3052);
            assertEquals(0.0760f, pixelValue, 4);
            pixelValue = band_quality_aot.getSampleFloat(4945, 3146);
            assertEquals(0.0990f, pixelValue, 4);

            Band band_quality_wvp = finalProduct.getBand("quality_wvp");
            assertEquals(6883, band_quality_wvp.getRasterWidth());
            assertEquals(5773, band_quality_wvp.getRasterHeight());

            pixelValue = band_quality_wvp.getSampleFloat(1480, 425);
            assertEquals(1.4700f, pixelValue, 4);
            pixelValue = band_quality_wvp.getSampleFloat(1486, 4312);
            assertEquals(1.6790f, pixelValue, 4);
            pixelValue = band_quality_wvp.getSampleFloat(3296, 3052);
            assertEquals(1.2730f, pixelValue, 4);
            pixelValue = band_quality_wvp.getSampleFloat(4945, 3146);
            assertEquals(1.8380f, pixelValue, 4);
        }catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductL3Subset(){
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L3_PRODUCT_NAME);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();
        try {
            Rectangle subsetRegion = new Rectangle(2516, 2886, 6291, 5329);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"B4", "B8A", "B9", "quality_mosaic_info_10m", "quality_mosaic_info_60m", "quality_scene_classification_20m", "scl_10m_nodata", "scl_60m_cloud_medium_proba"});
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);
            subsetDef.setIgnoreMetadata(false);

            Product finalProduct = reader.readProductNodes(productPath.toFile(), subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(50.2879f, productOrigin.lat,4);
            assertEquals(7.9489f, productOrigin.lon,4);

            assertEquals(6, finalProduct.getBands().length);
            assertEquals("S2_MSI_Level-3p", finalProduct.getProductType());
            assertNotNull(finalProduct.getMetadataRoot().getElementGroup());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(6291, finalProduct.getSceneRasterWidth());
            assertEquals(5329, finalProduct.getSceneRasterHeight());
            assertEquals("30-JUL-2015 10:39:14.021000", finalProduct.getStartTime().toString());
            assertEquals("30-JUL-2015 10:39:14.021000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("S2A_USER_MTD_SAFL03_PDMC_20150812T193220_R108_V20150730T103914_20150730T103914", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Band band_B4 = finalProduct.getBand("B4");
            assertEquals(6291, band_B4.getRasterWidth());
            assertEquals(5329, band_B4.getRasterHeight());

            float pixelValue = band_B4.getSampleFloat(535, 509);
            assertEquals(0.1870f, pixelValue, 4);
            pixelValue = band_B4.getSampleFloat(911, 1101);
            assertEquals(0.0070f, pixelValue, 4);
            pixelValue = band_B4.getSampleFloat(1301, 665);
            assertEquals(0.1970f, pixelValue, 4);
            pixelValue = band_B4.getSampleFloat(3384, 1403);
            assertEquals(0.1430f, pixelValue, 4);
            pixelValue = band_B4.getSampleFloat(6004, 2277);
            assertEquals(0.0170f, pixelValue, 4);

            Band band_B8A = finalProduct.getBand("B8A");
            assertEquals(3145, band_B8A.getRasterWidth());
            assertEquals(2664, band_B8A.getRasterHeight());

            pixelValue = band_B8A.getSampleFloat(290, 203);
            assertEquals(0.5300f, pixelValue, 4);
            pixelValue = band_B8A.getSampleFloat(1087, 327);
            assertEquals(0.2520f, pixelValue, 4);
            pixelValue = band_B8A.getSampleFloat(1451, 1096);
            assertEquals(0.2070f, pixelValue, 4);
            pixelValue = band_B8A.getSampleFloat(1908, 1368);
            assertEquals(0.0060f, pixelValue, 4);
            pixelValue = band_B8A.getSampleFloat(3055, 1626);
            assertEquals(0.1870f, pixelValue, 4);

            Band band_B9 = finalProduct.getBand("B9");
            assertEquals(1048, band_B9.getRasterWidth());
            assertEquals(888, band_B9.getRasterHeight());

            pixelValue = band_B9.getSampleFloat(58, 98);
            assertEquals(0.0270f, pixelValue, 4);
            pixelValue = band_B9.getSampleFloat(236, 78);
            assertEquals(0.0680f, pixelValue, 4);
            pixelValue = band_B9.getSampleFloat(126, 148);
            assertEquals(0.1880f, pixelValue, 4);
            pixelValue = band_B9.getSampleFloat(655, 468);
            assertEquals(0.1730f, pixelValue, 4);
            pixelValue = band_B9.getSampleFloat(977, 220);
            assertEquals(0.4580f, pixelValue, 4);

            Band band_quality_mosaic_info_10m = finalProduct.getBand("quality_mosaic_info_10m");
            assertEquals(6291, band_quality_mosaic_info_10m.getRasterWidth());
            assertEquals(5329, band_quality_mosaic_info_10m.getRasterHeight());

            pixelValue = band_quality_mosaic_info_10m.getSampleFloat(266, 336);
            assertEquals(7.0f, pixelValue, 0);
            pixelValue = band_quality_mosaic_info_10m.getSampleFloat(980, 215);
            assertEquals(8.0f, pixelValue, 0);
            pixelValue = band_quality_mosaic_info_10m.getSampleFloat(1013, 968);
            assertEquals(5.0f, pixelValue, 0);
            pixelValue = band_quality_mosaic_info_10m.getSampleFloat(1794, 218);
            assertEquals(3.0f, pixelValue, 0);
            pixelValue = band_quality_mosaic_info_10m.getSampleFloat(5003, 2083);
            assertEquals(0.0f, pixelValue, 0);

            Band band_quality_mosaic_info_60m = finalProduct.getBand("quality_mosaic_info_60m");
            assertEquals(1048, band_quality_mosaic_info_60m.getRasterWidth());
            assertEquals(888, band_quality_mosaic_info_60m.getRasterHeight());

            pixelValue = band_quality_mosaic_info_60m.getSampleFloat(48, 36);
            assertEquals(7.0f, pixelValue, 0);
            pixelValue = band_quality_mosaic_info_60m.getSampleFloat(158, 151);
            assertEquals(4.0f, pixelValue, 0);
            pixelValue = band_quality_mosaic_info_60m.getSampleFloat(344, 298);
            assertEquals(8.0f, pixelValue, 0);
            pixelValue = band_quality_mosaic_info_60m.getSampleFloat(338, 389);
            assertEquals(3.0f, pixelValue, 0);
            pixelValue = band_quality_mosaic_info_60m.getSampleFloat(913, 640);
            assertEquals(1.0f, pixelValue, 0);

            Band band_quality_scene_classification_20m = finalProduct.getBand("quality_scene_classification_20m");
            assertEquals(3145, band_quality_scene_classification_20m.getRasterWidth());
            assertEquals(2664, band_quality_scene_classification_20m.getRasterHeight());

            pixelValue = band_quality_scene_classification_20m.getSampleFloat(233, 150);
            assertEquals(5.0f, pixelValue, 0);
            pixelValue = band_quality_scene_classification_20m.getSampleFloat(516, 283);
            assertEquals(4.0f, pixelValue, 0);
            pixelValue = band_quality_scene_classification_20m.getSampleFloat(711, 859);
            assertEquals(7.0f, pixelValue, 0);
            pixelValue = band_quality_scene_classification_20m.getSampleFloat(677, 1462);
            assertEquals(6.0f, pixelValue, 0);
            pixelValue = band_quality_scene_classification_20m.getSampleFloat(1003, 1522);
            assertEquals(3.0f, pixelValue, 0);
        }catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }
}
