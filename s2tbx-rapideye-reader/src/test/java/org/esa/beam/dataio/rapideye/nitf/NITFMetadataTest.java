package org.esa.beam.dataio.rapideye.nitf;

import junit.framework.TestCase;
import org.esa.beam.dataio.rapideye.TestUtil;
import org.esa.beam.framework.datamodel.ProductData;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ramona Manda
 */
public class NITFMetadataTest extends TestCase {
    private NITFMetadata metadata;

    @Before
    public void setUp() throws Exception {
        NITFReaderWrapper reader = new NITFReaderWrapper(TestUtil.getTestFile("2009-04-16T104920_RE4_1B-NAC_3436599_84303_band2.ntf"));
        metadata = reader.getMetadata();
    }

    @Test
    public void testGetMetadataRoot() throws Exception {
        assertNotNull(metadata.getMetadataRoot());
    }

    @Test
    public void testGetFileDate() throws Exception {
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2010-06-09 11:57:19");
        assertEquals(expectedDate.getTime(), metadata.getFileDate().getAsDate().getTime());
    }

    @Test
    public void testGetFileTitle() throws Exception {
        assertEquals("RE Image Data", metadata.getFileTitle());
    }

    @Test
    public void testGetNumImages() throws Exception {
        assertEquals(1, metadata.getNumImages());
    }

    @Test
    public void testGetNumBands() throws Exception {
        assertEquals(1, metadata.getNumBands());
    }

    @Test
    public void testGetWidth() throws Exception {
        assertEquals(11829, metadata.getWidth());
    }

    @Test
    public void testGetHeight() throws Exception {
        assertEquals(7422, metadata.getHeight());
    }

    @Test
    public void testGetDataType() throws Exception {
        assertEquals(ProductData.TYPE_UINT16, metadata.getDataType());
    }

    @Test
    public void testGetUnit() throws Exception {
        assertEquals("nm", metadata.getUnit());
    }

    @Test
    public void testGetWavelength() throws Exception {
        assertEquals(-1.0, metadata.getWavelength(), 0.001);
    }
    
}