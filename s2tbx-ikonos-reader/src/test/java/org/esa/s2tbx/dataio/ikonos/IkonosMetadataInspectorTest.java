package org.esa.s2tbx.dataio.ikonos;

import org.esa.s2tbx.dataio.ikonos.metadata.IkonosMetadataInspector;
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
public class IkonosMetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "_ikonos" + File.separator;

    public IkonosMetadataInspectorTest() {
    }

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testMetadataInspector() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP" + File.separator + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML");

        IkonosMetadataInspector metadataInspector = new IkonosMetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(productFile.toPath());
        assertNotNull(metadata);
        assertEquals(200, metadata.getProductWidth());
        assertEquals(200, metadata.getProductHeight());

        GeoCoding geoCoding = metadata.getGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertNotNull(metadata.getBandList());
        assertEquals(5, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("Blue"));
        assertTrue(metadata.getBandList().contains("Red"));
        assertTrue(metadata.getBandList().contains("Green"));
        assertTrue(metadata.getBandList().contains("Pan"));
        assertTrue(metadata.getBandList().contains("Near"));

        assertNotNull(metadata.getMaskList());
        assertEquals(0, metadata.getMaskList().size());
    }
}
