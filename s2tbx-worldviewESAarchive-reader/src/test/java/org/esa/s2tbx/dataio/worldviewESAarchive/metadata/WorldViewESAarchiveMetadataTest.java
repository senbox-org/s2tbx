package org.esa.s2tbx.dataio.worldviewESAarchive.metadata;

import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.worldviewESAarchive.common.WorldViewESAarchiveConstants;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public class WorldViewESAarchiveMetadataTest {
    private WorldViewESAarchiveMetadata metadata;
    private String productsFolder = "_worldView" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        XmlMetadataParserFactory.registerParser(WorldViewESAarchiveMetadata.class, new XmlMetadataParser<>(WorldViewESAarchiveMetadata.class));
        metadata = WorldViewESAarchiveMetadata.create(WorldViewESAarchiveMetadata.class, TestUtil.getTestFile(productsFolder +
                "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP"+File.separator+"WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML"));
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
    public void testGetProductDescription() throws Exception {
        assertEquals("Metadata", metadata.getProductDescription());
    }

    @Test
    public void testGetNumBands() throws Exception {
        assertEquals(0, metadata.getNumBands());
    }

    @Test
    public void testGetFormatName() throws Exception {
        assertEquals(WorldViewESAarchiveConstants.PRODUCT_GENERIC_NAME, metadata.getFormatName());
    }

    @Test
    public void testGetRasterHeight() throws Exception {
        assertEquals(0, metadata.getRasterHeight());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(0, metadata.getRasterWidth());
    }

    @Test
    public void testGetProductStartTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(WorldViewESAarchiveConstants.WORLDVIEW2_UTC_DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = sdf.parse("2011-05-25T09:53:46");
        assertEquals(time.getTime(), metadata.getProductStartTime().getAsDate().getTime());
    }

    @Test
    public void testGetProductEndTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(WorldViewESAarchiveConstants.WORLDVIEW2_UTC_DATE_FORMAT);//the SimplaDateFormat knows only miliseconds, not microseconds!
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date expectedDate = sdf.parse("2011-05-25T09:53:51");
       assertEquals(expectedDate.getTime(), metadata.getProductEndTime().getAsDate().getTime());

    }
}
