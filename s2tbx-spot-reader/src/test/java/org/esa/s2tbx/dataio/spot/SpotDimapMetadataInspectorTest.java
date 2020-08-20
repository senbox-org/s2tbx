package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class SpotDimapMetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "_spot" + File.separator;

    public SpotDimapMetadataInspectorTest() {
    }

    @Test
    public void testMetadataInspector() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "30382639609301123571X0_1A_NETWORK.ZIP");

        SpotDimapMetadataInspector metadataInspector = new SpotDimapMetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(productFile.toPath());
        assertNotNull(metadata);
        assertEquals(3000, metadata.getProductWidth());
        assertEquals(3000, metadata.getProductHeight());

        GeoCoding geoCoding = metadata.getGeoCoding();
        assertNotNull(geoCoding);

        assertNotNull(metadata.getBandList());
        assertEquals(4, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("XS1"));
        assertTrue(metadata.getBandList().contains("XS2"));
        assertTrue(metadata.getBandList().contains("XS3"));
        assertTrue(metadata.getBandList().contains("SWIR"));

        assertNotNull(metadata.getMaskList());
        assertEquals(2, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("NODATA"));
        assertTrue(metadata.getMaskList().contains("SATURATED"));
    }
}
