package org.esa.s2tbx.dataio.worldview2esa.metadata;

import org.esa.s2tbx.dataio.worldview2esa.WorldView2ESAMetadataInspector;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Denisa Stefanescu
 */
public class WorldView2ESAMetadataInspectorTest {
    private String productsFolder = "_worldView" + File.separator;

    @Test
    public void testWorldView2ESAMetadataInspector() {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(productsFolder + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP" + File.separator + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML");

        assertNotNull(file);
        try {
            WorldView2ESAMetadataInspector metadataInspector = new WorldView2ESAMetadataInspector();
            MetadataInspector.Metadata metadata =  metadataInspector.getMetadata(file.toPath());

            assertNotNull(metadata);

            assertEquals(32768, metadata.getProductWidth());
            assertEquals(16384, metadata.getProductHeight());
            assertNotNull(metadata.getGeoCoding());

            assertNotNull(metadata.getBandList());
            assertEquals(9, metadata.getBandList().size());
            assertTrue(metadata.getBandList().contains("Coastal"));
            assertTrue(metadata.getBandList().contains("Yellow"));
            assertTrue(metadata.getBandList().contains("Red Edge"));
            assertTrue(metadata.getBandList().contains("Pan"));

            assertEquals(0, metadata.getMaskList().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
