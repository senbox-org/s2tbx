package org.esa.s2tbx.dataio.deimos.dimap;

import junit.framework.TestCase;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.utils.DateHelper;
import org.esa.s2tbx.utils.TestUtil;
import org.esa.snap.framework.datamodel.ProductData;
import org.junit.After;
import org.junit.Before;

import java.awt.Color;

/**
 * Unit test class for Deimos Metadata
 * @author Cosmin Cara
 *
 */
public class DeimosMetadataTest extends TestCase {
    private DeimosMetadata metadata;

    @Before
    public void setUp() throws Exception {
        XmlMetadataParserFactory.registerParser(DeimosMetadata.class, new XmlMetadataParser<>(DeimosMetadata.class));
        metadata = XmlMetadata.create(DeimosMetadata.class, TestUtil.getTestFile("DE01_SL6_22P_1T_20120905T170604_20120905T170613_DMI_0_4502.tar"));
    }

    @After
    public void tearDown() {
        metadata = null;
        System.gc();
    }

    public void testGetFileName() throws Exception {
        assertEquals("DE01_SL6_22P_1T_20120905T170604_20120905T170613_DMI_0_4502.tif", metadata.getFileName());
    }

    public void testGetProductName() throws Exception {
        assertEquals("DE004502p_023150_046799_042858_045602", metadata.getProductName());
    }

    public void testGetProductDescription() throws Exception {
        assertEquals("DE004502p_023150_046799_042858_045602", metadata.getProductDescription());
    }

    public void testGetFormatName() throws Exception {
        assertEquals(DeimosConstants.DIMAP, metadata.getFormatName());
    }

    public void testGetMetadataProfile() throws Exception {
        assertEquals(DeimosConstants.DEIMOS, metadata.getMetadataProfile());
    }

    public void testGetRasterWidth() throws Exception {
        assertEquals(4338, metadata.getRasterWidth());
    }

    public void testGetRasterHeight() throws Exception {
        assertEquals(1889, metadata.getRasterHeight());
    }

    public void testGetRasterFileNames() throws Exception {
        String[] fileNames = metadata.getRasterFileNames();
        assertNotNull(fileNames);
        assertEquals(fileNames.length, 1);
        assertEquals("DE01_SL6_22P_1T_20120905T170604_20120905T170613_DMI_0_4502.tif", fileNames[0]);
    }

    public void testGetBandNames() throws Exception {
        String[] bandNames = metadata.getBandNames();
        assertNotNull(bandNames);
        assertEquals(bandNames.length, 3);
        assertEquals(DeimosConstants.DEFAULT_BAND_NAMES[0], bandNames[0]);
        assertEquals(DeimosConstants.DEFAULT_BAND_NAMES[1], bandNames[1]);
        assertEquals(DeimosConstants.DEFAULT_BAND_NAMES[2], bandNames[2]);
    }

    public void testGetNumBands() throws Exception {
        assertEquals(3, metadata.getNumBands());
    }

    public void testGetNoDataValue() throws Exception {
        assertEquals(0, metadata.getNoDataValue());
    }

    public void testGetNoDataColor() throws Exception {
        assertEquals(Color.BLACK, metadata.getNoDataColor());
    }

    public void testGetSaturatedPixelValue() throws Exception {
        assertEquals(Integer.MAX_VALUE, metadata.getSaturatedPixelValue());
    }

    public void testGetSaturatedColor() throws Exception {
        assertEquals(Color.WHITE, metadata.getSaturatedColor());
    }

    public void testGetCenterTime() throws Exception {
        ProductData.UTC time = DateHelper.parseDate("2012-09-05 17:06:09", DeimosConstants.DEIMOS_DATE_FORMAT);
        assertEquals(time, metadata.getCenterTime());
    }

    public void testGetProcessingLevel() throws Exception {
        assertEquals(DeimosConstants.PROCESSING_2T, metadata.getProcessingLevel());
    }
}
