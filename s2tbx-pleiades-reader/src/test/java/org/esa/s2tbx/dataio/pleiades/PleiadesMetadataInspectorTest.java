package org.esa.s2tbx.dataio.pleiades;

import org.esa.s2tbx.dataio.pleiades.internal.PleiadesMetadataInspector;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Denisa Stefanescu
 */
public class PleiadesMetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "_pleiades" + File.separator;

    public PleiadesMetadataInspectorTest() {
    }

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testPleiadesMetadataInspector() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP"+File.separator+"TPP1600462598"+File.separator+"VOL_PHR.XML");
        assertNotNull(file);

        PleiadesMetadataInspector metadataInspector = new PleiadesMetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(33300, metadata.getProductWidth());
        assertEquals(4397, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(4, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("B2"));

        assertNotNull(metadata.getMaskList());
        assertEquals(3, metadata.getMaskList().size());
    }
}
