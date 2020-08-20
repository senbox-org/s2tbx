package org.esa.s2tbx.dataio.rapideye;

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
 * Created by jcoravu on 21/1/2020.
 */
public class RapidEyeL3MetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "_rapideye" + File.separator;

    public RapidEyeL3MetadataInspectorTest() {
    }

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testMetadataInspector() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Eritrea/13N041E-R1C2_2012_RE1_3a-3M_1234567890_metadata.xml");

        RapidEyeL3MetadataInspector metadataInspector = new RapidEyeL3MetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(productFile.toPath());
        assertNotNull(metadata);
        assertEquals(10985, metadata.getProductWidth());
        assertEquals(11232, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(5, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("red"));
        assertTrue(metadata.getBandList().contains("green"));
        assertTrue(metadata.getBandList().contains("near_infrared"));
        assertTrue(metadata.getBandList().contains("blue"));
        assertTrue(metadata.getBandList().contains("red_edge"));

        assertNotNull(metadata.getMaskList());
        assertEquals(0, metadata.getMaskList().size());
    }
}
