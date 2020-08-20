package org.esa.s2tbx.dataio.s2.ortho;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.ortho.metadata.Sentinel2OrthoMetadataInspector;
import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn;
import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM34S_ReaderPlugIn;
import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM36N_ReaderPlugIn;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Denisa Stefanescu
 */
public class Sentinel2OrthoMetadataInspectorTest {

    private static final String SENTINEL2_DIR = "S2";

    private static final String L1C_PRODUCT_NAME = "L1C/S2A_MSIL1C_20161206T080312_N0204_R035_T34HFH_20161206T081929.SAFE/MTD_MSIL1C.xml";
    private static final String L2A_PRODUCT_NAME = "L2A/S2B_MSIL2A_20190528T085609_N0212_R007_T36VWK_20190528T121447.zip";
    private static final String L3_PRODUCT_NAME = "L3/Darmstadt/10m/S2A_USER_PRD_MSIL03_PDMC_20150812T193220_R108_V20161231T235959_20161231T235959.SAFE/S2A_USER_MTD_SAFL03_PDMC_20150812T193220_R108_V20150730T103914_20150730T103914.xml";

    @BeforeClass
    public static void setupLogger() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Before
    public void setup() {
        String productPath = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        Path sentinel2TestProductsPath = Paths.get(productPath, SENTINEL2_DIR);
        Assume.assumeTrue(Files.exists(sentinel2TestProductsPath));
    }

    @Test
    public void testSentinel2OrthoMetadataInspectorL1C() throws IOException {
        File file = TestUtil.getTestFile(SENTINEL2_DIR + File.separator + L1C_PRODUCT_NAME);
        assertNotNull(file);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM34S_ReaderPlugIn();
        Sentinel2OrthoMetadataInspector metadataInspector = new Sentinel2OrthoMetadataInspector(S2Config.Sentinel2ProductLevel.L1C, readerPlugIn.getEPSG());
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(10980, metadata.getProductWidth());
        assertEquals(10980, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(43, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("sun_zenith"));
        assertTrue(metadata.getBandList().contains("sun_azimuth"));
        assertTrue(metadata.getBandList().contains("view_zenith_mean"));
        assertTrue(metadata.getBandList().contains("view_azimuth_mean"));
        assertTrue(metadata.getBandList().contains("view_zenith_B1"));
        assertTrue(metadata.getBandList().contains("view_azimuth_B5"));
        assertTrue(metadata.getBandList().contains("B4"));
        assertTrue(metadata.getBandList().contains("B8A"));
        assertTrue(metadata.getBandList().contains("B12"));

        assertNotNull(metadata.getMaskList());
        assertEquals(162, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("nodata_B1"));
        assertTrue(metadata.getMaskList().contains("partially_corrected_crosstalk_B2"));
        assertTrue(metadata.getMaskList().contains("saturated_l1a_B3"));
        assertTrue(metadata.getMaskList().contains("saturated_l1b_B4"));
        assertTrue(metadata.getMaskList().contains("defective_B5"));
        assertTrue(metadata.getMaskList().contains("ancillary_lost_B6"));
        assertTrue(metadata.getMaskList().contains("ancillary_degraded_B7"));
        assertTrue(metadata.getMaskList().contains("msi_lost_B8"));
        assertTrue(metadata.getMaskList().contains("msi_degraded_B8A"));
        assertTrue(metadata.getMaskList().contains("opaque_clouds_10m"));
        assertTrue(metadata.getMaskList().contains("cirrus_clouds_10m"));
        assertTrue(metadata.getMaskList().contains("detector_footprint-B10-03"));
    }

    @Test
    public void testSentinel2OrthoMetadataInspectorL2A() throws IOException {
        File file = TestUtil.getTestFile(SENTINEL2_DIR + File.separator + L2A_PRODUCT_NAME);
        assertNotNull(file);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM36N_ReaderPlugIn();
        Sentinel2OrthoMetadataInspector metadataInspector = new Sentinel2OrthoMetadataInspector(S2Config.Sentinel2ProductLevel.L2A, readerPlugIn.getEPSG());
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(10980, metadata.getProductWidth());
        assertEquals(10980, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(47, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("sun_zenith"));
        assertTrue(metadata.getBandList().contains("sun_azimuth"));
        assertTrue(metadata.getBandList().contains("view_zenith_mean"));
        assertTrue(metadata.getBandList().contains("view_azimuth_mean"));
        assertTrue(metadata.getBandList().contains("quality_aot"));
        assertTrue(metadata.getBandList().contains("quality_wvp"));
        assertTrue(metadata.getBandList().contains("quality_cloud_confidence"));
        assertTrue(metadata.getBandList().contains("quality_snow_confidence"));
        assertTrue(metadata.getBandList().contains("quality_scene_classification"));
        assertTrue(metadata.getBandList().contains("B2"));
        assertTrue(metadata.getBandList().contains("B6"));
        assertTrue(metadata.getBandList().contains("B11"));

        assertNotNull(metadata.getMaskList());
        assertEquals(198, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("nodata_B1"));
        assertTrue(metadata.getMaskList().contains("scl_nodata"));
        assertTrue(metadata.getMaskList().contains("partially_corrected_crosstalk_B2"));
        assertTrue(metadata.getMaskList().contains("saturated_l1a_B3"));
        assertTrue(metadata.getMaskList().contains("saturated_l1b_B4"));
        assertTrue(metadata.getMaskList().contains("defective_B5"));
        assertTrue(metadata.getMaskList().contains("scl_saturated_defective"));
        assertTrue(metadata.getMaskList().contains("ancillary_lost_B6"));
        assertTrue(metadata.getMaskList().contains("ancillary_degraded_B7"));
        assertTrue(metadata.getMaskList().contains("msi_lost_B8"));
        assertTrue(metadata.getMaskList().contains("msi_degraded_B8A"));
        assertTrue(metadata.getMaskList().contains("opaque_clouds_10m"));
        assertTrue(metadata.getMaskList().contains("cirrus_clouds_10m"));
        assertTrue(metadata.getMaskList().contains("detector_footprint-B09-03"));
    }

    @Test
    public void testSentinel2OrthoMetadataInspectorL3() throws IOException {
        File file = TestUtil.getTestFile(SENTINEL2_DIR + File.separator + L3_PRODUCT_NAME);
        assertNotNull(file);
        S2OrthoProductReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn();
        Sentinel2OrthoMetadataInspector metadataInspector = new Sentinel2OrthoMetadataInspector(S2Config.Sentinel2ProductLevel.L3, readerPlugIn.getEPSG());
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(10980, metadata.getProductWidth());
        assertEquals(10980, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(48, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("sun_zenith"));
        assertTrue(metadata.getBandList().contains("sun_azimuth"));
        assertTrue(metadata.getBandList().contains("view_zenith_mean"));
        assertTrue(metadata.getBandList().contains("view_azimuth_mean"));
        assertTrue(metadata.getBandList().contains("quality_mosaic_info_10m"));
        assertTrue(metadata.getBandList().contains("quality_mosaic_info_60m"));
        assertTrue(metadata.getBandList().contains("quality_scene_classification_20m"));
        assertTrue(metadata.getBandList().contains("B2"));
        assertTrue(metadata.getBandList().contains("B6"));
        assertTrue(metadata.getBandList().contains("B11"));

        assertNotNull(metadata.getMaskList());
        assertEquals(66, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("scl_10m_nodata"));
        assertTrue(metadata.getMaskList().contains("scl_20m_saturated_defective"));
        assertTrue(metadata.getMaskList().contains("scl_10m_water"));
        assertTrue(metadata.getMaskList().contains("scl_20m_vegetation"));
        assertTrue(metadata.getMaskList().contains("scl_20m_urban_areas"));
        assertTrue(metadata.getMaskList().contains("scl_60m_unclassified"));
        assertTrue(metadata.getMaskList().contains("msc_10m_1"));
        assertTrue(metadata.getMaskList().contains("msc_20m_2"));
        assertTrue(metadata.getMaskList().contains("msc_60m_6"));
    }
}
