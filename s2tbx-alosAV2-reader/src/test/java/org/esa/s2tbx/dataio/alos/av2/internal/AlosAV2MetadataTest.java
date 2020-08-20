package org.esa.s2tbx.dataio.alos.av2.internal;

import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

/**
 * Unit test class for Alos AVNIR-2 Metadata
 *
 * @author Denisa Stefanescu
 */

public class AlosAV2MetadataTest {

    private AlosAV2Metadata metadata;
    private String productsFolder = "_alos" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        XmlMetadataParserFactory.registerParser(AlosAV2Metadata.class, new XmlMetadataParser<>(AlosAV2Metadata.class));
        metadata = GenericXmlMetadata.create(AlosAV2Metadata.class, TestUtil.getTestFile(productsFolder + "AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410.SIP"+File.separator+"AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410"+File.separator+"AL01_AV2_OBS_1C_20080715T181736_20080715T181748_ESR_013182_3985.DIMA"));
    }

    @After
    public void teardown() {
        metadata = null;
        System.gc();
    }

    @Test
    public void testGetFileName() throws Exception {
        assertEquals("ALOS", metadata.getFileName());
    }

    @Test
    public void testGetProductName() throws Exception {
        assertEquals("ALAV2A131821810", metadata.getProductName());
    }

    @Test
    public void testGetProductDescription() throws Exception {
        assertEquals("Alos AVNIR-2 Data Products", metadata.getProductDescription());
    }

    @Test
    public void testGetFormatName() throws Exception {
        assertEquals(AlosAV2Constants.DIMAP, metadata.getFormatName());
    }

    @Test
    public void testGetMetadataProfile() throws Exception {
        assertEquals(AlosAV2Constants.ALOSAV2, metadata.getMetadataProfile());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(200, metadata.getRasterWidth());
    }

    @Test
    public void testGetRasterHeight() throws Exception {
        assertEquals(200, metadata.getRasterHeight());
    }

    @Test
    public void testGetRasterFileNames() throws Exception {
        String[] fileNames = metadata.getRasterFileNames();
        assertNotNull(fileNames);
        assertEquals(fileNames.length, 1);
        assertEquals("al01_av2_obs_1c_20080715t181736_20080715t181748_esr_013182_3985.gtif", fileNames[0]);
    }

    @Test
    public void testGetBandNames() throws Exception {
        String[] bandNames = metadata.getBandNames();
        assertNotNull(bandNames);
        assertEquals(bandNames.length, 4);
        assertEquals(AlosAV2Constants.DEFAULT_BAND_NAMES[0], bandNames[0]);
        assertEquals(AlosAV2Constants.DEFAULT_BAND_NAMES[1], bandNames[1]);
        assertEquals(AlosAV2Constants.DEFAULT_BAND_NAMES[2], bandNames[2]);
        assertEquals(AlosAV2Constants.DEFAULT_BAND_NAMES[3], bandNames[3]);
    }

    @Test
    public void testGetNumBands() throws Exception {
        assertEquals(4, metadata.getNumBands());
    }

    @Test
    public void testGetNoDataValue() throws Exception {
        assertEquals(0, metadata.getNoDataValue());
    }

    @Test
    public void testGetNoDataColor() throws Exception {
        assertEquals(Color.BLACK, metadata.getNoDataColor());
    }

    @Test
    public void testGetSaturatedPixelValue() throws Exception {
        assertNotEquals(Integer.MAX_VALUE, metadata.getSaturatedPixelValue());
    }

    @Test
    public void testGetSaturatedColor() throws Exception {
        assertEquals(Color.WHITE, metadata.getSaturatedColor());
    }

    @Test
    public void testGetProductStartTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//the SimplaDateFormat knows only miliseconds, not microseconds!
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date expectedDate = sdf.parse("2008-07-15 18:17:36.286");
        assertEquals(expectedDate.getTime(), metadata.getProductStartTime().getAsDate().getTime());
        assertEquals(285521, metadata.getProductStartTime().getMicroSecondsFraction());//the SimplaDateFormat knows only miliseconds, not microseconds!
    }

    @Test
    public void testGetEndTime() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//the SimplaDateFormat knows only miliseconds, not microseconds
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = dateFormat.parse("2008-07-15 18:17:48.124");
        assertEquals(time.getTime(), metadata.getProductEndTime().getAsDate().getTime());
        assertEquals(123521, metadata.getProductEndTime().getMicroSecondsFraction());//the SimplaDateFormat knows only miliseconds, not microseconds!
    }

    @Test
    public void testGetScaleFactor() throws Exception {
        assertEquals(0.01, metadata.getGain(AlosAV2Constants.DEFAULT_BAND_NAMES[0]), 5880);
        assertEquals(0.01, metadata.getGain(AlosAV2Constants.DEFAULT_BAND_NAMES[1]), 0.5730);
        assertEquals(0.01, metadata.getGain(AlosAV2Constants.DEFAULT_BAND_NAMES[2]), 0.5020);
        assertEquals(0.01, metadata.getGain(AlosAV2Constants.DEFAULT_BAND_NAMES[3]), 0.8350);
    }

    @Test
    public void testGetPixelFormat() throws Exception {
        assertEquals(20, metadata.getPixelDataType());
    }

    @Test
    public void testGetProcessingLevel() throws Exception {
        assertEquals(AlosAV2Constants.PROCESSING_1B, metadata.getProcessingLevel());
    }
}
