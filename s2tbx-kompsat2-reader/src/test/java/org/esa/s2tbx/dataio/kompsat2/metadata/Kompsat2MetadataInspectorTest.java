package org.esa.s2tbx.dataio.kompsat2.metadata;

import org.esa.s2tbx.dataio.kompsat2.Kompsat2MetadataInspector;
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
public class Kompsat2MetadataInspectorTest {

    private String productsFolder = "_kompsat" + File.separator;

    public Kompsat2MetadataInspectorTest() {
    }

    @Test
    public void testKompsatMetadataInspector() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());
        File file = TestUtil.getTestFile(productsFolder + "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP" + File.separator +
                                                 "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML");
        assertNotNull(file);

        Kompsat2MetadataInspector metadataInspector = new Kompsat2MetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(file.toPath());
        assertNotNull(metadata);
        assertEquals(18172, metadata.getProductWidth());
        assertEquals(18808, metadata.getProductHeight());

        assertNotNull(metadata.getGeoCoding());

        assertNotNull(metadata.getBandList());
        assertEquals(5, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("MS1"));
        assertTrue(metadata.getBandList().contains("PAN"));

        assertNotNull(metadata.getMaskList());
        assertEquals(0, metadata.getMaskList().size());
    }
}
