package org.esa.s2tbx.dataio.worldview2.metadata;

import org.esa.s2tbx.dataio.worldview2.WorldView2MetadataInspector;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class WorldView2MetadataInspectorTest {
    private String productsFolder = "_worldView" + File.separator;

    @Test
    public void testWorldView2ESAMetadataInspector() {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(productsFolder + "ZON24_I200862_FL01-P369685/ZON24_README.XML");

        assertNotNull(file);
        try {
            WorldView2MetadataInspector metadataInspector = new WorldView2MetadataInspector();
            MetadataInspector.Metadata metadata =  metadataInspector.getMetadata(file.toPath());

            assertNotNull(metadata);

            assertEquals(9846, metadata.getProductWidth());
            assertEquals(20079, metadata.getProductHeight());
            assertNotNull(metadata.getGeoCoding());

            assertNotNull(metadata.getBandList());
            assertEquals(5, metadata.getBandList().size());
            assertTrue(metadata.getBandList().contains("Green"));
            assertTrue(metadata.getBandList().contains("NIR1"));
            assertTrue(metadata.getBandList().contains("Red"));
            assertTrue(metadata.getBandList().contains("Pan"));

            assertEquals(0, metadata.getMaskList().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
