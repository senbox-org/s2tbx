package org.esa.s2tbx.dataio.alos.pri.internal;

import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

/**
 * Unit test class for Alos PRISM Metadata
 *
 * @author Denisa Stefanescu
 */

public class AlosPRIMetadataTest {

    private AlosPRIMetadata metadata;
    private String productsFolder = "_alos" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        XmlMetadataParserFactory.registerParser(AlosPRIMetadata.class, new XmlMetadataParser<>(AlosPRIMetadata.class));
        metadata = GenericXmlMetadata.create(AlosPRIMetadata.class, TestUtil.getTestFile(productsFolder + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP" + File.separator + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.MD.XML"));
        metadata.addComponentMetadata(TestUtil.getTestFile(productsFolder + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP" + File.separator + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410" + File.separator + "ALPSMB038921910" + File.separator + "AL01_PRI_M1B_1C_20061017T212809_20061017T212815_ESR_003892_2B3E.DIMA"));
        metadata.addComponentMetadata(TestUtil.getTestFile(productsFolder + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP" + File.separator + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410" + File.separator + "ALPSMF038921800" + File.separator + "AL01_PRI_M1F_1C_20061017T212639_20061017T212645_ESR_003892_2B3E.DIMA"));
        metadata.addComponentMetadata(TestUtil.getTestFile(productsFolder + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP" + File.separator + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410" + File.separator + "ALPSMN038921855" + File.separator + "AL01_PRI_M1N_1C_20061017T212724_20061017T212730_ESR_003892_2B3E.DIMA"));
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
        assertEquals("AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410", metadata.getProductName());
    }

    @Test
    public void testGetProductDescription() throws Exception {
        assertEquals("Alos PRISM Data Products", metadata.getProductDescription());
    }

    @Test
    public void testGetFormatName() throws Exception {
        assertEquals(AlosPRIConstants.DIMAP, metadata.getFormatName());
    }

    @Test
    public void testGetMetadataProfile() throws Exception {
        assertEquals(AlosPRIConstants.ALOSPRI, metadata.getImageMetadataList().get(0).getMetadataProfile());
        assertEquals(AlosPRIConstants.ALOSPRI, metadata.getImageMetadataList().get(1).getMetadataProfile());
        assertEquals(AlosPRIConstants.ALOSPRI, metadata.getImageMetadataList().get(2).getMetadataProfile());
    }

    @Test
    public void testMaxInsertPointX() throws Exception {
        assertEquals(481647.5, metadata.getMaxInsertPointX(), 0.0);
    }

    @Test
    public void testMinInsertPointX() throws Exception {
        assertEquals(468615, metadata.getMinInsertPointX(), 0.0);
    }

    @Test
    public void testMaxInsertPointY() throws Exception {
        assertEquals(9051595, metadata.getMaxInsertPointY(), 0.0);
    }

    @Test
    public void testMinInsertPointY() throws Exception {
        assertEquals(9046182, metadata.getMinInsertPointY(), 0.0);
    }


    @Test
    public void testGetRasterHeight() throws Exception {
        assertEquals(22640, metadata.getRasterHeight());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(25629, metadata.getRasterWidth());
    }


    @Test
    public void testGetRasterFileNames() throws Exception {
        ArrayList<String> fileNames = new ArrayList<>();
        for (ImageMetadata imageMetadata : metadata.getImageMetadataList()) {
            for (String file : imageMetadata.getRasterFileNames()) {
                fileNames.add(file);
            }
        }
        assertNotNull(fileNames);
        assertEquals(fileNames.size(), 3);
        assertEquals("AL01_PRI_M1B_1C_20061017T212809_20061017T212815_ESR_003892_2B3E.GTIF", fileNames.get(0).toUpperCase());
        assertEquals("AL01_PRI_M1F_1C_20061017T212639_20061017T212645_ESR_003892_2B3E.GTIF", fileNames.get(1).toUpperCase());
        assertEquals("AL01_PRI_M1N_1C_20061017T212724_20061017T212730_ESR_003892_2B3E.GTIF", fileNames.get(2).toUpperCase());
    }

    @Test
    public void testGetNumBands() throws Exception {
        assertEquals(1, metadata.getImageMetadataList().get(0).getNumBands());
    }

    @Test
    public void testGetNoDataValue() throws Exception {
        assertEquals(0, metadata.getImageMetadataList().get(0).getNoDataValue());
    }

    @Test
    public void testGetSaturatedPixelValue() throws Exception {
        assertNotEquals(Integer.MAX_VALUE, metadata.getImageMetadataList().get(0).getSaturatedValue());
    }

    @Test
    public void testGetProductStartTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//the SimplaDateFormat knows only miliseconds, not microseconds!
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date expectedDate = sdf.parse("2006-10-17 21:27:24.567");
        assertEquals(expectedDate.getTime(), metadata.getProductStartTime().getAsDate().getTime());
        assertEquals(567000, metadata.getProductStartTime().getMicroSecondsFraction());//the SimplaDateFormat knows only miliseconds, not microseconds!
    }

    @Test
    public void testGetEndTime() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//the SimplaDateFormat knows only miliseconds, not microseconds
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = dateFormat.parse("2006-10-17 21:27:30.491");
        assertEquals(time.getTime(), metadata.getProductEndTime().getAsDate().getTime());
        assertEquals(491000, metadata.getProductEndTime().getMicroSecondsFraction());//the SimplaDateFormat knows only miliseconds, not microseconds!
    }

    @Test
    public void testGetScaleFactor() throws Exception {
        assertEquals(0.5010, metadata.getImageMetadataList().get(0).getGain(), 0.0);
        assertEquals(0.5010, metadata.getImageMetadataList().get(1).getGain(), 0.0);
        assertEquals(0.5010, metadata.getImageMetadataList().get(2).getGain(), 0.0);
    }

    @Test
    public void testGetPixelFormat() throws Exception {
        assertEquals(20, metadata.getImageMetadataList().get(0).getPixelDataType());
        assertEquals(20, metadata.getImageMetadataList().get(1).getPixelDataType());
        assertEquals(20, metadata.getImageMetadataList().get(2).getPixelDataType());
    }

    @Test
    public void testBandOffset() throws Exception {
        metadata.getProductOrigin();
        assertEquals(2749, metadata.bandOffset().get("ALPSMN038921855")[0].intValue());
        assertEquals(1221, metadata.bandOffset().get("ALPSMN038921855")[1].intValue());
        assertEquals(0, metadata.bandOffset().get("ALPSMF038921800")[0].intValue());
        assertEquals(2165, metadata.bandOffset().get("ALPSMF038921800")[1].intValue());
        assertEquals(5213, metadata.bandOffset().get("ALPSMB038921910")[0].intValue());
        assertEquals(0, metadata.bandOffset().get("ALPSMB038921910")[1].intValue());
    }

    @Test
    public void testGetCrsCode() throws Exception {
        assertEquals("epsg:32615", metadata.getCrsCode());
    }

    @Test
    public void testGetProductOrigin() throws Exception {
        assertEquals(2.5, metadata.getProductOrigin().stepX, 0.0);
        assertEquals(2.5, metadata.getProductOrigin().stepY, 0.0);
        assertEquals(468615, metadata.getProductOrigin().x, 0.0);
        assertEquals(9051595, metadata.getProductOrigin().y, 0.0);
    }

    @Test
    public void testGetMaxCorners() throws Exception {
        assertEquals(-91.78166198730469, metadata.getMaxCorners()[0][0], 0.0);
        assertEquals(-91.09302520751953, metadata.getMaxCorners()[0][1], 0.0);
        assertEquals(-94.87023162841797, metadata.getMaxCorners()[0][2], 0.0);
        assertEquals(-94.05230712890625, metadata.getMaxCorners()[0][3], 0.0);
        assertEquals(81.47442626953125, metadata.getMaxCorners()[1][0], 0.0);
        assertEquals(81.12564849853516, metadata.getMaxCorners()[1][1], 0.0);
        assertEquals(81.40962219238281, metadata.getMaxCorners()[1][2], 0.0);
        assertEquals(81.06774139404297, metadata.getMaxCorners()[1][3], 0.0);
    }

    @Test
    public void testGetBandUnit() throws Exception {
        assertEquals("W.M-2.SR-1.uM-1", metadata.getImageMetadataList().get(0).getBandUnit());
    }

    @Test
    public void testGetBandName() throws Exception {
        assertEquals("ALPSMB038921910", metadata.getImageMetadataList().get(0).getBandName());
        assertEquals("ALPSMF038921800", metadata.getImageMetadataList().get(1).getBandName());
        assertEquals("ALPSMN038921855", metadata.getImageMetadataList().get(2).getBandName());
    }
}
