package org.esa.beam.dataio.rapideye.metadata;

import junit.framework.TestCase;
import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.metadata.XmlMetadataParser;
import org.esa.beam.dataio.metadata.XmlMetadataParserFactory;
import org.esa.beam.dataio.rapideye.TestUtil;
import org.esa.beam.dataio.rapideye.nitf.NITFReaderWrapper;
import org.esa.beam.framework.datamodel.ProductData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ramona Manda
 */
public class RapidEyeMetadataTest extends TestCase {
    private RapidEyeMetadata metadata;

    @After
    public void tearDown(){
        metadata = null;
        System.gc();
    }

    @Before
    public void setUp() throws Exception {
        XmlMetadataParserFactory.registerParser(RapidEyeMetadata.class, new XmlMetadataParser<RapidEyeMetadata>(RapidEyeMetadata.class));
        metadata = XmlMetadata.create(RapidEyeMetadata.class, TestUtil.getTestFile("2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml"));
    }

    @Test
    public void testGetBrowseFileName() throws Exception {
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_browse.tif", metadata.getBrowseFileName());
    }

    @Test
    public void testGetMaskFileName() throws Exception {
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_udm.tif", metadata.getMaskFileName());
    }

    @Test
    public void testGetRasterFileNames() throws Exception {
        assertEquals(5, metadata.getRasterFileNames().length);
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band1.ntf", metadata.getRasterFileNames()[0]);
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band2.ntf", metadata.getRasterFileNames()[1]);
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band3.ntf", metadata.getRasterFileNames()[2]);
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band4.ntf", metadata.getRasterFileNames()[3]);
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band5.ntf", metadata.getRasterFileNames()[4]);
    }

    @Test
    public void testGetReferenceSystem() throws Exception {
        assertNotNull(metadata.getReferenceSystem());
        assertEquals("4326", metadata.getReferenceSystem().epsgCode);
        assertEquals("N/A", metadata.getReferenceSystem().geodeticDatum);
        assertEquals("N/A", metadata.getReferenceSystem().projectionCode);
        assertEquals("N/A", metadata.getReferenceSystem().projectionZone);
    }

    @Test
    public void testGetProductStartTime() throws Exception {
        ProductData.UTC expected = ProductData.UTC.parse("2009-04-16 07:54:05.290", "yyyy-MM-dd HH:mm:ss");
        assertEquals(expected.getAsDate().getTime(), metadata.getProductStartTime().getAsDate().getTime());
    }

    @Test
    public void testGetProductEndTime() throws Exception {
        ProductData.UTC expected = ProductData.UTC.parse("2009-04-16 07:53:26.244", "yyyy-MM-dd HH:mm:ss");
        assertEquals(expected.getAsDate().getTime(), metadata.getProductEndTime().getAsDate().getTime());
    }

    @Test
    public void testGetScaleFactor() throws Exception {
        assertEquals(0.01, metadata.getScaleFactor(0), 0.001);
        assertEquals(0.01, metadata.getScaleFactor(1), 0.001);
        assertEquals(0.01, metadata.getScaleFactor(2), 0.001);
        assertEquals(0.01, metadata.getScaleFactor(3), 0.001);
    }

    @Test
    public void testGetPixelFormat() throws Exception {
        assertEquals(21, metadata.getPixelFormat());
    }

    @Test
    public void testGetCornersLatitudes() throws Exception {
        assertEquals(53.088257, metadata.getCornersLatitudes()[0], 0.001);
        assertEquals(52.924603, metadata.getCornersLatitudes()[1], 0.001);
        assertEquals(52.670235, metadata.getCornersLatitudes()[2], 0.001);
        assertEquals(52.50815, metadata.getCornersLatitudes()[3], 0.001);
    }

    @Test
    public void testGetCornersLongitudes() throws Exception {
        assertEquals(15.082264, metadata.getCornersLongitudes()[0], 0.001);
        assertEquals(16.193775, metadata.getCornersLongitudes()[1], 0.001);
        assertEquals(14.893127, metadata.getCornersLongitudes()[2], 0.001);
        assertEquals(15.994719, metadata.getCornersLongitudes()[3], 0.001);
    }

    @Test
    public void testGetRationalPolinomialCoefficients() throws Exception {
        assertNull(metadata.getRationalPolinomialCoefficients());
    }

    @Test
    public void testGetRootElement() throws Exception {
        assertNotNull(metadata.getRootElement());
    }

    @Test
    public void testGetFileName() throws Exception {
        assertEquals("L1B", metadata.getFileName());
    }

    @Test
    public void testSetFileName() throws Exception {
        metadata.setFileName("testname");
        assertEquals("testname", metadata.getFileName());
    }

    @Test
    public void testGetNumBands() throws Exception {
        assertEquals(5, metadata.getNumBands());
    }

    @Test
    public void testGetProductName() throws Exception {
        assertEquals("2009-04-16T104920_RE4_1B-NAC_3436599_84303", metadata.getProductName());
    }

    @Test
    public void testGetFormatName() throws Exception {
        assertEquals("NITF", metadata.getFormatName());
    }

    @Test
    public void testGetMetadataProfile() throws Exception {
        assertEquals("L1B", metadata.getMetadataProfile());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(11829, metadata.getRasterWidth());
    }

    @Test
    public void testGetRasterHeight() throws Exception {
        assertEquals(7422, metadata.getRasterHeight());
    }

    @Test
    public void testGetPath() throws Exception {
        assertEquals(TestUtil.ABSOLUTE_PATH + "\\2009-04-16T104920_RE4_1B-NAC_3436599_84303_metadata.xml", metadata.getPath());
    }

    @Test
    public void testSetPath() throws Exception {
        metadata.setPath("testpath");
        assertEquals("testpath", metadata.getPath());
    }

    @Test
    public void testSetName() throws Exception {
        metadata.setName("testname");
        assertEquals("testname", metadata.getFileName());
    }
}