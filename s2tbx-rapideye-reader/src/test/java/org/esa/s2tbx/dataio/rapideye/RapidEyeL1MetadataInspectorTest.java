package org.esa.s2tbx.dataio.rapideye;

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
public class RapidEyeL1MetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "_rapideye" + File.separator;

    public RapidEyeL1MetadataInspectorTest() {
    }

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();

    }

    @Test
    public void testMetadataInspector() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "Demo03_1B/2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml");

        RapidEyeL1MetadataInspector metadataInspector = new RapidEyeL1MetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(productFile.toPath());
        assertNotNull(metadata);
        assertEquals(11829, metadata.getProductWidth());
        assertEquals(7422, metadata.getProductHeight());

        GeoCoding geoCoding = metadata.getGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("WGS84(DD)", coordinateReferenceSystem.getName().getCode());

        assertNotNull(metadata.getBandList());
        assertEquals(6, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("blue"));
        assertTrue(metadata.getBandList().contains("green"));
        assertTrue(metadata.getBandList().contains("near_infrared"));
        assertTrue(metadata.getBandList().contains("red"));
        assertTrue(metadata.getBandList().contains("red_edge"));
        assertTrue(metadata.getBandList().contains("unusable_data"));
        assertTrue(metadata.getBandList().contains("red_edge"));

        assertNotNull(metadata.getMaskList());
        assertEquals(7, metadata.getMaskList().size());
        assertTrue(metadata.getMaskList().contains("black_fill"));
        assertTrue(metadata.getMaskList().contains("clouds"));
        assertTrue(metadata.getMaskList().contains("missing_blue_data"));
        assertTrue(metadata.getMaskList().contains("missing_green_data"));
        assertTrue(metadata.getMaskList().contains("missing_nir_data"));
        assertTrue(metadata.getMaskList().contains("missing_red_data"));
        assertTrue(metadata.getMaskList().contains("missing_red_edge_data"));
    }
}
