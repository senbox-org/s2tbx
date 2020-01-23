package org.esa.s2tbx.dataio.alos.pri;

import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Created by jcoravu on 21/1/2020.
 */
public class AlosPRIMetadataInspectorTest {

    private static final String PRODUCT_FOLDER = "_alos"+ File.separator;

    public AlosPRIMetadataInspectorTest() {
    }

    @Test
    public void testMetadataInspector() throws URISyntaxException, IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP" + File.separator + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.MD.XML");

        AlosPRIMetadataInspector metadataInspector = new AlosPRIMetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(productFile.toPath());
        assertNotNull(metadata);
        assertEquals(25629, metadata.getProductWidth());
        assertEquals(22640, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(3, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("ALPSMB038921910"));
        assertTrue(metadata.getBandList().contains("ALPSMF038921800"));
        assertTrue(metadata.getBandList().contains("ALPSMN038921855"));

        assertNotNull(metadata.getMaskList());
        assertEquals(2, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("no data"));
        assertTrue(metadata.getMaskList().contains("saturated"));
    }
}
