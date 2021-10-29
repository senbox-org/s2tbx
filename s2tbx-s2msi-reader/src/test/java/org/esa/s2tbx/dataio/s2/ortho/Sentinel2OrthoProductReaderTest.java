package org.esa.s2tbx.dataio.s2.ortho;

import com.bc.ceres.binding.ConversionException;
import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn;
import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM34S_ReaderPlugIn;
import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM36N_ReaderPlugIn;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.subset.GeometrySubsetRegion;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.converters.JtsGeometryConverter;
import org.esa.snap.runtime.Engine;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * @author Denisa Stefanescu
 */
public class Sentinel2OrthoProductReaderTest {

    private Engine engine;

    private Path sentinel2TestProductsPath;

    private static final String SENTINEL2_DIR = "S2";

    private static final String L1C_PRODUCT_NAME = "L1C"+ File.separator+ "S2A_MSIL1C_20161206T080312_N0204_R035_T34HFH_20161206T081929.SAFE"+ File.separator+ "MTD_MSIL1C.xml";
    private static final String L2A_PRODUCT_NAME = "L2A"+ File.separator+ "S2B_MSIL2A_20190528T085609_N0212_R007_T36VWK_20190528T121447.zip";
    private static final String L3_PRODUCT_NAME = "L3"+ File.separator+ "Darmstadt"+ File.separator+ "10m"+ File.separator+ "S2A_USER_PRD_MSIL03_PDMC_20150812T193220_R108_V20161231T235959_20161231T235959.SAFE"+ File.separator+ "S2A_USER_MTD_SAFL03_PDMC_20150812T193220_R108_V20150730T103914_20150730T103914.xml";

    @BeforeClass
    public static void setupLogger() throws Exception {
        LogUtils4Tests.initLogger();
    }

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
    public void testReadProductL1CPixelSubset() throws IOException {
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L1C_PRODUCT_NAME);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM34S_ReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        Rectangle subsetRegion = new Rectangle(6142, 5402, 4663, 3035);
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B1", "B2", "B5", "view_zenith_B4", "view_azimuth_B5", "detector_footprint-B8A-01", "msi_lost_B5"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
        subsetDef.setSubSampling(1, 1);
        subsetDef.setIgnoreMetadata(true);

        Product finalProduct = reader.readProductNodes(productPath.toFile(), subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(-33.9139f, productOrigin.lat, 4);
        assertEquals(22.7461f, productOrigin.lon, 4);

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
        assertEquals(506, band_B1.getRasterHeight());

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
        assertEquals(10, band_view_zenith_B4.getRasterWidth());
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
        assertEquals(10, band_view_azimuth_B5.getRasterWidth());
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

        assertNotNull(finalProduct.getTiePointGrid("tco3"));
        TiePointGrid tiePointGrid_total_column_water_vapour = finalProduct.getTiePointGrid("tco3");
        assertEquals(1220.0, tiePointGrid_total_column_water_vapour.getSubSamplingX(),4);
        assertEquals(1220.0, tiePointGrid_total_column_water_vapour.getSubSamplingY(),4);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(700, 772);
        assertEquals(12.913931f, pixelValue, 0.1);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(3668, 6460);
        assertEquals(16.69695, pixelValue, 0.1);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(9300, 3492);
        assertEquals(12.447721f, pixelValue, 0.1);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(10276, 10540);
        assertEquals(15.901403f, pixelValue, 0.1);

        assertNotNull(finalProduct.getTiePointGrid("tcwv"));
        TiePointGrid tiePointGrid_total_column_ozone = finalProduct.getTiePointGrid("tcwv");
        assertEquals(1220.0, tiePointGrid_total_column_ozone.getSubSamplingX(),4);
        assertEquals(1220.0, tiePointGrid_total_column_ozone.getSubSamplingY(),4);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(700, 772);
        assertEquals(0.005749957f, pixelValue, 0.0001);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(3668, 6460);
        assertEquals(0.005775377f, pixelValue, 0.0001);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(9300, 3492);
        assertEquals(0.005737951f, pixelValue, 0.0001);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(10276, 10540);
        assertEquals(0.005794613f, pixelValue, 0.0001);

        assertNotNull(finalProduct.getTiePointGrid("msl"));
        TiePointGrid tiePointGrid_mean_sea_level_pressure = finalProduct.getTiePointGrid("msl");
        assertEquals(1220.0, tiePointGrid_mean_sea_level_pressure.getSubSamplingX(),4);
        assertEquals(1220.0, tiePointGrid_mean_sea_level_pressure.getSubSamplingY(),4);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(700, 772);
        assertEquals(100860.1f, pixelValue, 0.1);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(3668, 6460);
        assertEquals(100774.3f, pixelValue, 0.1);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(9300, 3492);
        assertEquals(100841.4f, pixelValue, 0.1);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(10276, 10540);
        assertEquals(100753.3f, pixelValue, 0.1);

    }

    @Test
    public void testReadProductL2APixelSubset() throws IOException {
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L2A_PRODUCT_NAME);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM36N_ReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        Rectangle subsetRegion = new Rectangle(2516, 1924, 6883, 5773);
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B3", "B6", "B9", "view_zenith_mean", "view_azimuth_B2", "quality_aot", "quality_wvp", "ancillary_lost_B8A", "detector_footprint-B01-01"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
        subsetDef.setSubSampling(1, 1);
        subsetDef.setIgnoreMetadata(false);

        Product finalProduct = reader.readProductNodes(productPath.toFile(), subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(58.4671f, productOrigin.lat, 4);
        assertEquals(33.4309f, productOrigin.lon, 4);

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
        assertEquals(14, band_view_zenith_mean.getRasterWidth());
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
        assertEquals(14, band_view_azimuth_B2.getRasterWidth());
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
    }

    @Test
    public void testReadProductL3PixelSubset() throws IOException {
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L3_PRODUCT_NAME);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        Rectangle subsetRegion = new Rectangle(2516, 2886, 6291, 5329);
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B4", "B8A", "B9", "quality_mosaic_info_10m", "quality_mosaic_info_60m", "quality_scene_classification_20m", "scl_10m_nodata", "scl_60m_cloud_medium_proba"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
        subsetDef.setSubSampling(1, 1);
        subsetDef.setIgnoreMetadata(false);

        Product finalProduct = reader.readProductNodes(productPath.toFile(), subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(50.2879f, productOrigin.lat, 4);
        assertEquals(7.9489f, productOrigin.lon, 4);

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
        assertNotNull(band_B4);

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
        assertEquals(3146, band_B8A.getRasterWidth());
        assertEquals(2665, band_B8A.getRasterHeight());

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
        assertEquals(1049, band_B9.getRasterWidth());
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
        assertEquals(1049, band_quality_mosaic_info_60m.getRasterWidth());
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
        assertEquals(3146, band_quality_scene_classification_20m.getRasterWidth());
        assertEquals(2665, band_quality_scene_classification_20m.getRasterHeight());

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
    }

    @Test
    public void testReadProductL1CGeometrySubset() throws IOException, ConversionException {
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L1C_PRODUCT_NAME);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM34S_ReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((22.74609375 -33.913883209228516, 22.787073135375977 -33.913291931152344, 22.82805061340332 -33.91269302368164, 22.869028091430664 -33.91207504272461, 22.910003662109375 -33.91144943237305, 22.950979232788086 -33.910804748535156, 22.99195098876953 -33.91014862060547, 23.032922744750977 -33.909481048583984, 23.073894500732422 -33.90879821777344, 23.1148624420166 -33.90810012817383, 23.15583038330078 -33.90739059448242, 23.196796417236328 -33.90666580200195, 23.237762451171875 -33.90592575073242, 23.25019073486328 -33.90570068359375, 23.25109100341797 -33.939857482910156, 23.251989364624023 -33.9740104675293, 23.252891540527344 -34.0081672668457, 23.253795623779297 -34.04232406616211, 23.25469970703125 -34.07647705078125, 23.255605697631836 -34.11063003540039, 23.256513595581055 -34.1447868347168, 23.257423400878906 -34.17893981933594, 23.257431030273438 -34.1792106628418, 23.24496078491211 -34.179439544677734, 23.20386505126953 -34.18018341064453, 23.16276741027832 -34.18091583251953, 23.121667861938477 -34.18163299560547, 23.08056640625 -34.18233871459961, 23.039464950561523 -34.18302917480469, 22.998361587524414 -34.1837043762207, 22.957256317138672 -34.18436813354492, 22.91615104675293 -34.18501663208008, 22.875041961669922 -34.18565368652344, 22.833934783935547 -34.186275482177734, 22.792823791503906 -34.18688201904297, 22.751712799072266 -34.187477111816406, 22.751707077026367 -34.18720626831055, 22.751001358032227 -34.15304183959961, 22.75029754638672 -34.118873596191406, 22.74959373474121 -34.08470916748047, 22.748891830444336 -34.05054473876953, 22.74818992614746 -34.016380310058594, 22.74748992919922 -33.98221206665039, 22.74679183959961 -33.94804763793945, 22.74609375 -33.913883209228516))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B1", "B2", "B5", "view_zenith_B4", "view_azimuth_B5", "detector_footprint-B8A-01", "msi_lost_B5"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);
        subsetDef.setIgnoreMetadata(true);

        Product finalProduct = reader.readProductNodes(productPath.toFile(), subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(-33.9139f, productOrigin.lat, 4);
        assertEquals(22.7461f, productOrigin.lon, 4);

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
        assertEquals(2332, mask_msi_lost.getRasterWidth());
        assertEquals(1518, mask_msi_lost.getRasterHeight());

        Band band_B1 = finalProduct.getBand("B1");
        assertEquals(777, band_B1.getRasterWidth());
        assertEquals(506, band_B1.getRasterHeight());

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
        assertEquals(2332, band_B5.getRasterWidth());
        assertEquals(1518, band_B5.getRasterHeight());

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
        assertEquals(10, band_view_zenith_B4.getRasterWidth());
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
        assertEquals(10, band_view_azimuth_B5.getRasterWidth());
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
    }

    @Test
    public void testReadProductL2AGeometrySubset() throws IOException, ConversionException {
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L2A_PRODUCT_NAME);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM36N_ReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((33.43094253540039 58.467132568359375, 33.554527282714844 58.46665954589844, 33.67810821533203 58.466064453125, 33.80168533325195 58.46535110473633, 33.925254821777344 58.46451950073242, 34.0488166809082 58.46356964111328, 34.17237091064453 58.46249771118164, 34.29591751098633 58.46131134033203, 34.41944885253906 58.46000289916992, 34.542972564697266 58.45857620239258, 34.61046600341797 58.45774841308594, 34.607513427734375 58.39301681518555, 34.60457229614258 58.328285217285156, 34.601646423339844 58.263553619384766, 34.598731994628906 58.19881820678711, 34.595829010009766 58.13408660888672, 34.59294128417969 58.06935119628906, 34.590065002441406 58.004615783691406, 34.58720016479492 57.93988037109375, 34.58717727661133 57.939430236816406, 34.52065658569336 57.940242767333984, 34.398921966552734 57.94164276123047, 34.27717208862305 57.94292449951172, 34.15541458129883 57.944087982177734, 34.03364562988281 57.94513702392578, 33.91187286376953 57.946067810058594, 33.79008865356445 57.94688415527344, 33.66830062866211 57.94758224487305, 33.546504974365234 57.94816589355469, 33.42470932006836 57.948631286621094, 33.424713134765625 57.94907760620117, 33.425479888916016 58.01383972167969, 33.42625045776367 58.07859802246094, 33.427024841308594 58.14335250854492, 33.427799224853516 58.20811080932617, 33.42858123779297 58.27286911010742, 33.42936325073242 58.337623596191406, 33.430152893066406 58.40237808227539, 33.43094253540039 58.467132568359375))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B3", "B6", "B9", "view_zenith_mean", "view_azimuth_B2", "quality_aot", "quality_wvp", "ancillary_lost_B8A", "detector_footprint-B01-01"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);
        subsetDef.setIgnoreMetadata(false);

        Product finalProduct = reader.readProductNodes(productPath.toFile(), subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(58.4671f, productOrigin.lat, 4);
        assertEquals(33.4309f, productOrigin.lon, 4);

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
        assertEquals(3442, band_B6.getRasterWidth());
        assertEquals(2887, band_B6.getRasterHeight());

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
        assertEquals(1148, band_B9.getRasterWidth());
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
        assertEquals(14, band_view_zenith_mean.getRasterWidth());
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
        assertEquals(14, band_view_azimuth_B2.getRasterWidth());
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
    }

    @Test
    public void testReadProductL3GeometrySubset() throws IOException, ConversionException {
        Date startDate = Calendar.getInstance().getTime();
        Path productPath = sentinel2TestProductsPath.resolve(L3_PRODUCT_NAME);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((7.94886589050293 50.28799057006836, 8.042341232299805 50.28879928588867, 8.135821342468262 50.28953170776367, 8.229304313659668 50.29018783569336, 8.32279109954834 50.290771484375, 8.416280746459961 50.29127883911133, 8.509773254394531 50.291709899902344, 8.603267669677734 50.29206466674805, 8.696763038635254 50.2923469543457, 8.790260314941406 50.29255294799805, 8.831955909729004 50.29262161254883, 8.832165718078613 50.232723236083984, 8.832375526428223 50.17282485961914, 8.832585334777832 50.1129264831543, 8.832793235778809 50.05302810668945, 8.833001136779785 49.99312973022461, 8.833208084106445 49.9332275390625, 8.833415031433105 49.87332534790039, 8.83362102508545 49.81342315673828, 8.83362102508545 49.81333541870117, 8.792339324951172 49.81326675415039, 8.699769020080566 49.81306457519531, 8.607199668884277 49.81278991699219, 8.514631271362305 49.81243896484375, 8.422065734863281 49.81201171875, 8.329503059387207 49.81151580810547, 8.236942291259766 49.810943603515625, 8.14438533782959 49.81029510498047, 8.051831245422363 49.809574127197266, 7.959280967712402 49.808780670166016, 7.9592790603637695 49.80887222290039, 7.957993030548096 49.8687629699707, 7.95670223236084 49.928653717041016, 7.95540714263916 49.98854446411133, 7.954107284545898 50.04843521118164, 7.952803611755371 50.10832595825195, 7.951495170593262 50.168212890625, 7.950182914733887 50.22810363769531, 7.94886589050293 50.28799057006836))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"B4", "B8A", "B9", "quality_mosaic_info_10m", "quality_mosaic_info_60m", "quality_scene_classification_20m", "scl_10m_nodata", "scl_60m_cloud_medium_proba"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);
        subsetDef.setIgnoreMetadata(false);

        Product finalProduct = reader.readProductNodes(productPath.toFile(), subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(50.2879f, productOrigin.lat, 4);
        assertEquals(7.9489f, productOrigin.lon, 4);

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
        assertEquals(3146, band_B8A.getRasterWidth());
        assertEquals(2665, band_B8A.getRasterHeight());

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
        assertEquals(1049, band_B9.getRasterWidth());
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
        assertEquals(1049, band_quality_mosaic_info_60m.getRasterWidth());
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
        assertEquals(3146, band_quality_scene_classification_20m.getRasterWidth());
        assertEquals(2665, band_quality_scene_classification_20m.getRasterHeight());

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
    }
}
