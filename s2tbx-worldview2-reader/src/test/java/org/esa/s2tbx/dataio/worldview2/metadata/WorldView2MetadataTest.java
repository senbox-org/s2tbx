package org.esa.s2tbx.dataio.worldview2.metadata;

import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

/**
 * Unit test class for worldView2 Metadata
 *
 * @author Razvan Dumitrascu
 */
public class WorldView2MetadataTest {
    private WorldView2Metadata metadata;
    private String productsFolder = "_worldView" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        XmlMetadataParserFactory.registerParser(WorldView2Metadata.class, new XmlMetadataParser<>(WorldView2Metadata.class));
        metadata = GenericXmlMetadata.create(WorldView2Metadata.class, TestUtil.getTestFile(productsFolder +
                "ZON24_I200862_FL01-P369685/ZON24_README.XML"));
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
        assertEquals(WorldView2Constants.PRODUCT_GENERIC_NAME, metadata.getFormatName());
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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        LocalDateTime newDate = LocalDateTime.parse("2017-05-02T09:13:54.600000Z", dateTimeFormatter);
        assertEquals(newDate.toInstant(ZoneOffset.UTC), metadata.getProductStartTime().getAsDate().toInstant());
    }

    @Test
    public void testGetProductEndTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//the SimplaDateFormat knows only miliseconds, not microseconds!
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date expectedDate = sdf.parse("2017-05-02 09:14:08.999");
        assertEquals(999000, metadata.getProductEndTime().getMicroSecondsFraction());//the SimplaDateFormat knows only miliseconds, not microseconds!
        assertEquals(expectedDate.getTime(), metadata.getProductEndTime().getAsDate().getTime());

    }
}

