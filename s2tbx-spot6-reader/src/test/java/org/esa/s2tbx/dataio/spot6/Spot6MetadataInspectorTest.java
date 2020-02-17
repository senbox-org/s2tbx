package org.esa.s2tbx.dataio.spot6;

import org.esa.s2tbx.dataio.spot6.internal.Spot6MetadataInspector;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.utils.TestUtil;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class Spot6MetadataInspectorTest {

    private String productsFolder = "_spot6_7" + File.separator;

    public Spot6MetadataInspectorTest() {
    }

    @Before
    public void setup() {
        String productPath = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        Path spot6TestProductsPath = Paths.get(productPath, productsFolder);
        Assume.assumeTrue(Files.exists(spot6TestProductsPath));
    }

    @Test
    public void testSpot6MetadataInspector() throws IOException {
        File file = TestUtil.getTestFile(productsFolder + "SPOT6_1.5m_short" + File.separator + "SPOT_LIST.XML");
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
