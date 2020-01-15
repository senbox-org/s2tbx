package org.esa.s2tbx.dataio.kompsat2.metadata;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.kompsat2.Kompsat2ProductReader;
import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

/**
 * Unit test class for Kompsat2 Metadata
 *
 * @author Razvan Dumitrascu
 */
public class Kompsat2MetadataTest {

    private Kompsat2Metadata metadata;
    private String productsFolder = "_kompsat" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        XmlMetadataParserFactory.registerParser(Kompsat2Metadata.class, new XmlMetadataParser<>(Kompsat2Metadata.class));
        metadata = GenericXmlMetadata.create(Kompsat2Metadata.class, TestUtil.getTestFile(productsFolder +
          "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP" + File.separator +
          "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML"));
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
        assertEquals("KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001", metadata.getProductName());
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
        assertEquals(Kompsat2Constants.PRODUCT_GENERIC_NAME, metadata.getFormatName());
    }

    @Test
    public void testGetRasterHeight() throws Exception {
        assertEquals(0, metadata.getRasterHeight());
    }

    @Test
    public void testGetProductType() throws Exception {
        assertEquals("MSC_MUL_1G", metadata.getProductType());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(0, metadata.getRasterWidth());
    }

    @Test
    public void testGetProductStartTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = sdf.parse("2011-09-20T01:32:01.394Z");
        assertEquals(time.getTime(), metadata.getProductStartTime().getAsDate().getTime());
    }

    @Test
    public void testGetProductEndTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = sdf.parse("2011-09-20T01:32:03.730Z");
        assertEquals(time.getTime(), metadata.getProductEndTime().getAsDate().getTime());
    }


    @Test
    public void testKompsat2MetadataComponent() throws Exception {
        String metadataFileName = "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML";
        Path metadataPath = TestUtil.getTestFile(productsFolder +
                "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP" + File.separator + metadataFileName).toPath();
        try (VirtualDirEx productDirectory = VirtualDirEx.build(metadataPath, false, false)) {
            metadata = Kompsat2ProductReader.readProductMetadata(productDirectory, metadataFileName);
            assertNotNull(metadata.getMetadataComponent());
            float[][] tiePointGridPoints = {{-14.40976521f, -14.40929376f, -14.57977522f, -14.57929798f },{129.57652647f, 129.74505447f, 129.57696612f, 129.74562262f}};
            for (int index = 0; index<4;index++) {
                assertEquals(tiePointGridPoints[0][index], metadata.getMetadataComponent().getTiePointGridPoints()[0][index], 0.e-6);
                assertEquals(tiePointGridPoints[1][index], metadata.getMetadataComponent().getTiePointGridPoints()[1][index], 0.e-6);
            }
            assertEquals("UTM:South,52 WGS 84", metadata.getMetadataComponent().getCrsCode());
            assertEquals("MSC_110920012631_27459_10080892_1G.zip", metadata.getMetadataComponent().getImageDirectoryName() );
            assertEquals("-14.494548069 129.661042414", metadata.getMetadataComponent().getOriginPos());
        }
    }
}
