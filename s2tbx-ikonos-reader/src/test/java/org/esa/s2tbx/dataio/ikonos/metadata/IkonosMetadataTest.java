package org.esa.s2tbx.dataio.ikonos.metadata;

import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

/**
 * Unit test class for Ikonos Metadata
 *
 * @author Denisa Stefanescu
 */
public class IkonosMetadataTest {

    private IkonosMetadata metadata;
    private String productsFolder = "_ikonos" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        XmlMetadataParserFactory.registerParser(IkonosMetadata.class, new XmlMetadataParser<>(IkonosMetadata.class));
        metadata = GenericXmlMetadata.create(IkonosMetadata.class, TestUtil.getTestFile(productsFolder + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP"+ File.separator+ "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML"));
    }

    @After
    public void teardown() {
        metadata = null;
        System.gc();
    }

    @Test
    public void testGetFileName() throws Exception {
        assertEquals("Metadata", metadata.getFileName());
    }

    @Test
    public void testGetProductName() throws Exception {
        assertEquals("IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001", metadata.getProductName());
    }

    @Test
    public void testGetProductDescription() throws Exception {
        assertEquals("Metadata", metadata.getProductDescription());
    }

    @Test
    public void testGetNumBands() throws Exception {
        assertEquals(0, metadata.getNumBands());
    }

    @Test
    public void testGetFormatName() throws Exception {
        assertEquals(IkonosConstants.PRODUCT_GENERIC_NAME, metadata.getFormatName());
    }

    @Test
    public void testGetRasterHeight() throws Exception {
        assertEquals(0, metadata.getRasterHeight());
    }

    @Test
    public void testGetProductType() throws Exception {
        assertEquals("OSA_GEO_1P", metadata.getProductType());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(0, metadata.getRasterWidth());
    }

    @Test
    public void testGetProductStartTime() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = dateFormat.parse("2008-08-20T09:26:00Z");
        assertEquals(time.getTime(), metadata.getProductStartTime().getAsDate().getTime());
    }

    @Test
    public void testGetProductEndTime() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = dateFormat.parse("2008-08-20T09:26:00Z");
        assertEquals(time.getTime(), metadata.getProductEndTime().getAsDate().getTime());
    }

    @Test
    public void testIkonosMetadataComponent() throws Exception {
        Path path = TestUtil.getTestFile(productsFolder + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP\\IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML").toPath();
        try (InputStream inputStream = Files.newInputStream(path)) {
            FilePathInputStream filePathInputStream = new FilePathInputStream(path, inputStream, null);
            metadata = IkonosMetadata.create(filePathInputStream);
        }
        assertNotNull(metadata.getMetadataComponent());
        float[][] tiePointGridPoints = {{38.1166697339f, 38.1123862139f, 37.9972505388f, 37.9929852559f}, {23.9048421125f, 24.0730688435f, 23.9001216511f, 24.0680760263f}};
        for (int index = 0; index < 4; index++) {
            assertEquals(tiePointGridPoints[0][index], metadata.getMetadataComponent().getTiePointGridPoints()[0][index], 0.e-6);
            assertEquals(tiePointGridPoints[1][index], metadata.getMetadataComponent().getTiePointGridPoints()[1][index], 0.e-6);
        }
        assertEquals("EPSG:32634", metadata.getMetadataComponent().getCrsCode());
        assertEquals(38.0548528801, metadata.getMetadataComponent().getOriginPositionX(), 10);
        assertEquals(23.9865276316, metadata.getMetadataComponent().getOriginPositionY(), 10);
    }
}
