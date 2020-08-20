package org.esa.s2tbx.dataio.alos.av2;

import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Created by jcoravu on 21/1/2020.
 */
public class AlosAV2MetadataInspectorTest {

    private static final String PRODUCT_FOLDER = "_alos"+ File.separator;

    public AlosAV2MetadataInspectorTest() {
    }

    @Test
    public void testMetadataInspector() throws URISyntaxException, IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410.SIP"+File.separator+"AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410"+File.separator+"AL01_AV2_OBS_1C_20080715T181736_20080715T181748_ESR_013182_3985.DIMA");

        AlosAV2MetadataInspector metadataInspector = new AlosAV2MetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(productFile.toPath());
        assertNotNull(metadata);
        assertEquals(200, metadata.getProductWidth());
        assertEquals(200, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(4, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("blue"));
        assertTrue(metadata.getBandList().contains("green"));
        assertTrue(metadata.getBandList().contains("red"));
        assertTrue(metadata.getBandList().contains("near_infrared"));

        assertNotNull(metadata.getMaskList());
        assertEquals(2, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("no data"));
        assertTrue(metadata.getMaskList().contains("saturated"));
    }
}
