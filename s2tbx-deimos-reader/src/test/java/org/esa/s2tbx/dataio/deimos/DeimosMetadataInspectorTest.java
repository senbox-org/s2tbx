package org.esa.s2tbx.dataio.deimos;

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
public class DeimosMetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "_deimos" + File.separator;

    public DeimosMetadataInspectorTest() {
    }

    @Test
    public void testMetadataInspector() throws URISyntaxException, IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "small_deimos/DE01_SL6_22P_1T_20110228T092316_20110616T092427_DMI_0_2e9d.dim");

        DeimosMetadataInspector metadataInspector = new DeimosMetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(productFile.toPath());
        assertNotNull(metadata);
        assertEquals(3000, metadata.getProductWidth());
        assertEquals(3000, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(4, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("NIR"));
        assertTrue(metadata.getBandList().contains("Red"));
        assertTrue(metadata.getBandList().contains("Green"));
        assertTrue(metadata.getBandList().contains("band_3"));

        assertNotNull(metadata.getMaskList());
        assertEquals(2, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("nodata"));
        assertTrue(metadata.getMaskList().contains("SATURATED"));
    }
}
