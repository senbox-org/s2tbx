package org.esa.s2tbx.dataio.pleiades;

import org.esa.s2tbx.dataio.pleiades.internal.PleiadesMetadataInspector;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Denisa Stefanescu
 */
public class PleiadesMetadataInspectorTest {

    private String productsFolder = "_pleiades" + File.separator;

    public PleiadesMetadataInspectorTest() {
    }

    @Test
    public void testPleiadesMetadataInspector() throws IOException {
        File file = TestUtil.getTestFile(productsFolder + "PL1_OPER_HIR_PMS_3__20151115T113200_N39-883_W007-992_5011.SIP"+File.separator+"TPP1600462598"+File.separator+"VOL_PHR.XML");
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
