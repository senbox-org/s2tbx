package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Created by jcoravu on 21/1/2020.
 */
public class SpotViewMetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "_spot" + File.separator;

    public SpotViewMetadataInspectorTest() {
    }

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testMetadataInspector() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP");

        SpotViewMetadataInspector metadataInspector = new SpotViewMetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(productFile.toPath());
        assertNotNull(metadata);
        assertEquals(2713, metadata.getProductWidth());
        assertEquals(2568, metadata.getProductHeight());

        GeoCoding geoCoding = metadata.getGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("WGS84(DD)", coordinateReferenceSystem.getName().getCode());

        assertNotNull(metadata.getBandList());
        assertEquals(4, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("band_0"));
        assertTrue(metadata.getBandList().contains("band_1"));
        assertTrue(metadata.getBandList().contains("band_2"));
        assertTrue(metadata.getBandList().contains("band_3"));

        assertNotNull(metadata.getMaskList());
        assertEquals(0, metadata.getMaskList().size());
    }
}
