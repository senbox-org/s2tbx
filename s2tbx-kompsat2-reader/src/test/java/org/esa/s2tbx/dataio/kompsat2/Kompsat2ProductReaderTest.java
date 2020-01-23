package org.esa.s2tbx.dataio.kompsat2;

import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author Razvan Dumitrascu
 */
public class Kompsat2ProductReaderTest {

    private Kompsat2ProductReader reader;
    private String productsFolder = "_kompsat" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        Kompsat2ProductReaderPlugin plugin = new Kompsat2ProductReaderPlugin();
        reader = new Kompsat2ProductReader(plugin);
    }

    @After
    public void tearDown() throws Exception {
        if (reader!=null) {
            reader.close();
        }
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(Kompsat2ProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP" + File.separator +
          "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(5, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals(Kompsat2Constants.KOMPSAT2_PRODUCT, finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(18172, finalProduct.getSceneRasterWidth());
            assertEquals(18808, finalProduct.getSceneRasterHeight());
            assertEquals("20-SEP-2011 01:32:01.394000", finalProduct.getStartTime().toString());
            assertEquals("20-SEP-2011 01:32:03.730000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductSubset() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP" + File.separator +
                                                 "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Rectangle subsetRegion = new Rectangle(3294, 4148, 10000, 8660);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[] { "MS1", "MS4", "PAN"} );
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);

            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(-14.4472f, productOrigin.lat,4);
            assertEquals(129.6072f, productOrigin.lon,4);

            assertEquals(3, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals(Kompsat2Constants.KOMPSAT2_PRODUCT, finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(10000, finalProduct.getSceneRasterWidth());
            assertEquals(8660, finalProduct.getSceneRasterHeight());
            assertEquals("20-SEP-2011 01:32:01.394000", finalProduct.getStartTime().toString());
            assertEquals("20-SEP-2011 01:32:03.730000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Band band_MS1 = finalProduct.getBand("MS1");
            assertEquals(2500, band_MS1.getRasterWidth());
            assertEquals(2165, band_MS1.getRasterHeight());

            float pixelValue = band_MS1.getSampleFloat(199, 488);
            assertEquals(0.8481f, pixelValue, 4);
            pixelValue = band_MS1.getSampleFloat(713, 415);
            assertEquals(0.6248f, pixelValue, 4);
            pixelValue = band_MS1.getSampleFloat(1727, 409);
            assertEquals(0.6157f, pixelValue, 4);
            pixelValue = band_MS1.getSampleFloat(458, 970);
            assertEquals(0.9885f, pixelValue, 4);
            pixelValue = band_MS1.getSampleFloat(1227, 1353);
            assertEquals(0.8451f, pixelValue, 4);
            pixelValue = band_MS1.getSampleFloat(1784, 1742);
            assertEquals(0.7349f, pixelValue, 4);

            Band band_MS4 = finalProduct.getBand("MS4");
            assertEquals(2500, band_MS4.getRasterWidth());
            assertEquals(2165, band_MS4.getRasterHeight());

            pixelValue = band_MS4.getSampleFloat(199, 488);
            assertEquals(0.5273f, pixelValue, 4);
            pixelValue = band_MS4.getSampleFloat(713, 415);
            assertEquals(0.3363f, pixelValue, 4);
            pixelValue = band_MS4.getSampleFloat(1727, 409);
            assertEquals(0.3305f, pixelValue, 4);
            pixelValue = band_MS4.getSampleFloat(458, 970);
            assertEquals(0.7094f, pixelValue, 4);
            pixelValue = band_MS4.getSampleFloat(1227, 1353);
            assertEquals(0.4597f, pixelValue, 4);
            pixelValue = band_MS4.getSampleFloat(1784, 1742);
            assertEquals(0.4186f, pixelValue, 4);

            Band band_PAN = finalProduct.getBand("PAN");
            assertEquals(10000, band_PAN.getRasterWidth());
            assertEquals(8660, band_PAN.getRasterHeight());

            pixelValue = band_PAN.getSampleFloat(996, 1111);
            assertEquals(0.3670f, pixelValue, 4);
            pixelValue = band_PAN.getSampleFloat(6835, 1808);
            assertEquals(0.3394f, pixelValue, 4);
            pixelValue = band_PAN.getSampleFloat(3794, 3920);
            assertEquals(0.6633f, pixelValue, 4);
            pixelValue = band_PAN.getSampleFloat(9788, 8289);
            assertEquals(0.5824f, pixelValue, 4);
            pixelValue = band_PAN.getSampleFloat(1272, 7504);
            assertEquals(0.6823f, pixelValue, 4);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnFileInput() {
        File file = TestUtil.getTestFile(productsFolder + "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP" + File.separator +
          "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

}
