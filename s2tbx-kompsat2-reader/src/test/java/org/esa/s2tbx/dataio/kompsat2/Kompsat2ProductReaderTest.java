package org.esa.s2tbx.dataio.kompsat2;

import com.bc.ceres.binding.ConversionException;
import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.subset.GeometrySubsetRegion;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.core.util.converters.JtsGeometryConverter;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
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
        if (reader != null) {
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
    public void testReadProductPixelSubset() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP" + File.separator +
                                                 "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Rectangle subsetRegion = new Rectangle(3294, 4148, 10000, 8660);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"MS1", "MS4", "PAN"});
            subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
            subsetDef.setSubSampling(1, 1);

            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(-14.4472f, productOrigin.lat, 4);
            assertEquals(129.6072f, productOrigin.lon, 4);

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
    public void testReadProductGeometrySubset() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.SIP" + File.separator +
                                                 "KO2_OPER_MSC_MUL_1G_20110920T013201_20110920T013203_027459_1008_0892_0001.MD.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            JtsGeometryConverter converter = new JtsGeometryConverter();
            Geometry geometry = converter.parse("POLYGON ((129.607177734375 -14.447186470031738, 129.61721801757812 -14.447160720825195," +
                                                        " 129.62725830078125 -14.447134017944336, 129.63729858398438 -14.447107315063477," +
                                                        " 129.6473388671875 -14.4470796585083, 129.65737915039062 -14.447052001953125," +
                                                        " 129.6674041748047 -14.44702434539795, 129.6774444580078 -14.446995735168457," +
                                                        " 129.68748474121094 -14.446966171264648, 129.69752502441406 -14.44693660736084," +
                                                        " 129.699951171875 -14.446929931640625, 129.69998168945312 -14.456711769104004," +
                                                        " 129.70001220703125 -14.4664945602417, 129.70004272460938 -14.476276397705078," +
                                                        " 129.7000732421875 -14.486059188842773, 129.70010375976562 -14.495841026306152," +
                                                        " 129.70013427734375 -14.505623817443848, 129.70016479492188 -14.515405654907227," +
                                                        " 129.7001953125 -14.525188446044922, 129.7001953125 -14.525224685668945," +
                                                        " 129.69776916503906 -14.52523136138916, 129.68772888183594 -14.525260925292969," +
                                                        " 129.6776885986328 -14.525290489196777, 129.6676483154297 -14.52531909942627," +
                                                        " 129.65760803222656 -14.525347709655762, 129.64756774902344 -14.525375366210938," +
                                                        " 129.63751220703125 -14.525403022766113, 129.62747192382812 -14.525429725646973," +
                                                        " 129.617431640625 -14.525456428527832, 129.60739135742188 -14.525483131408691," +
                                                        " 129.60739135742188 -14.525446891784668, 129.60736083984375 -14.515664100646973," +
                                                        " 129.6073455810547 -14.505882263183594, 129.60731506347656 -14.496099472045898," +
                                                        " 129.60728454589844 -14.486316680908203, 129.6072540283203 -14.476534843444824," +
                                                        " 129.60723876953125 -14.466752052307129, 129.60720825195312 -14.456969261169434," +
                                                        " 129.607177734375 -14.447186470031738))");
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"MS1", "MS4", "PAN"});
            subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
            subsetDef.setSubSampling(1, 1);

            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(-14.4472f, productOrigin.lat, 4);
            assertEquals(129.6072f, productOrigin.lon, 4);

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
        } catch (ConversionException e) {
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
