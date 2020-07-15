package org.esa.s2tbx.dataio.spot6;

import org.esa.s2tbx.dataio.spot6.internal.Spot6MetadataInspector;
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

public class Spot6MetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "_spot6_7" + File.separator;

    public Spot6MetadataInspectorTest() {
    }

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testSpot6MetadataInspector() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT6_1.5m_short" + File.separator + "SPOT_LIST.XML");
        assertNotNull(file);

        Spot6MetadataInspector metadataInspector = new Spot6MetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(2319, metadata.getProductWidth());
        assertEquals(1870, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(4, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("B2"));

        assertNotNull(metadata.getMaskList());
        assertEquals(3, metadata.getMaskList().size());
    }
}
