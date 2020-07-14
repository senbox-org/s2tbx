package org.esa.s2tbx.dataio.muscate;

import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * @author Denisa Stefanescu
 */
public class MuscateMetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "S2"+ File.separator+ "MUSCATE" + File.separator;

    public MuscateMetadataInspectorTest() {
    }

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testMuscateMetadataInspector() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SENTINEL2A_20160205-103556-319_L2A_T31TFK_D_V1-0.zip");
        assertNotNull(file);

        MuscateMetadataInspector metadataInspector = new MuscateMetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(10980, metadata.getProductWidth());
        assertEquals(10980, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(58, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("Aux_Mask_Cloud_R1"));
        assertTrue(metadata.getBandList().contains("AOT_R1"));
        assertTrue(metadata.getBandList().contains("Surface_Reflectance_B11"));
        assertTrue(metadata.getBandList().contains("Flat_Reflectance_B11"));
        assertTrue(metadata.getBandList().contains("WVC_R1"));
        assertTrue(metadata.getBandList().contains("sun_azimuth"));
        assertTrue(metadata.getBandList().contains("view_azimuth_B4"));
        assertTrue(metadata.getBandList().contains("Aux_IA_R2"));

        assertNotNull(metadata.getMaskList());
        assertEquals(46, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("AOT_Interpolation_Mask_R1"));
        assertTrue(metadata.getMaskList().contains("cloud_mask_shadow_R1"));
        assertTrue(metadata.getMaskList().contains("MG2_Water_Mask_R1"));
        assertTrue(metadata.getMaskList().contains("edge_mask_R1"));
        assertTrue(metadata.getMaskList().contains("saturation_B5"));
    }
}
