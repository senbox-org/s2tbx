package org.esa.s2tbx.dataio.worldview2;

import com.bc.ceres.binding.ConversionException;
import com.bc.ceres.core.NullProgressMonitor;
import com.vividsolutions.jts.geom.Geometry;
import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.core.util.converters.JtsGeometryConverter;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author Razvan Dumitrascu
 */
public class WorldView2ProductReaderTest {
    private WorldView2ProductReader reader;
    private String productsFolder = "_worldView" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());

        WorldView2ProductReaderPlugin plugin = new WorldView2ProductReaderPlugin();
        reader = new WorldView2ProductReader(plugin);
    }

    @After
    public void tearDown() throws Exception {
        if (reader!=null) {
            reader.close();
        }
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(WorldView2ProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "ZON24_I200862_FL01-P369685"+ File.separator+ "ZON24_README.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "200");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(5, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals(WorldView2Constants.PRODUCT_TYPE, finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(9846, finalProduct.getSceneRasterWidth());
            assertEquals(20079, finalProduct.getSceneRasterHeight());
            assertEquals("02-MAY-2017 09:13:54.600000", finalProduct.getStartTime().toString());
            assertEquals("02-MAY-2017 09:14:08.999000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("ZON24_I200862_FL01-P369685", finalProduct.getName());
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
        File file = TestUtil.getTestFile(productsFolder + "ZON24_I200862_FL01-P369685"+ File.separator+ "ZON24_README.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "200");
        try {
            Rectangle subsetRegion = new Rectangle(2046, 2508, 5677, 4885);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"NIR1", "Red", "Pan"});
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);
            subsetDef.setIgnoreMetadata(true);

            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(55.5803f, productOrigin.lat,4);
            assertEquals(23.8304f, productOrigin.lon,4);

            assertEquals(3, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals(WorldView2Constants.PRODUCT_TYPE, finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(5677, finalProduct.getSceneRasterWidth());
            assertEquals(4885, finalProduct.getSceneRasterHeight());
            assertEquals("02-MAY-2017 09:13:54.600000", finalProduct.getStartTime().toString());
            assertEquals("02-MAY-2017 09:14:08.999000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("ZON24_I200862_FL01-P369685", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Band band_NIR1 = finalProduct.getBand("NIR1");
            assertEquals(1419, band_NIR1.getRasterWidth());
            assertEquals(1221, band_NIR1.getRasterHeight());

            float pixelValue = band_NIR1.getSampleFloat(124, 130);
            assertEquals(6.2642f, pixelValue, 4);
            pixelValue = band_NIR1.getSampleFloat(513, 404);
            assertEquals(13.9893f, pixelValue, 4);
            pixelValue = band_NIR1.getSampleFloat(1110, 1145);
            assertEquals(6.8708f, pixelValue, 4);
            pixelValue = band_NIR1.getSampleFloat(1010, 548);
            assertEquals(9.2602f, pixelValue, 4);
            pixelValue = band_NIR1.getSampleFloat(1362, 88);
            assertEquals(8.8640f, pixelValue, 4);

            Band band_Red = finalProduct.getBand("Red");
            assertEquals(1419, band_Red.getRasterWidth());
            assertEquals(1221, band_Red.getRasterHeight());

            pixelValue = band_Red.getSampleFloat(124, 130);
            assertEquals(3.8646f, pixelValue, 4);
            pixelValue = band_Red.getSampleFloat(513, 404);
            assertEquals(18.2270f, pixelValue, 4);
            pixelValue = band_Red.getSampleFloat(1110, 1145);
            assertEquals(3.9799f, pixelValue, 4);
            pixelValue = band_Red.getSampleFloat(1010, 548);
            assertEquals(3.7684f, pixelValue, 4);
            pixelValue = band_Red.getSampleFloat(1362, 88);
            assertEquals(3.8453f, pixelValue, 4);

            Band band_Pan = finalProduct.getBand("Pan");
            assertEquals(5677, band_Pan.getRasterWidth());
            assertEquals(4885, band_Pan.getRasterHeight());

            pixelValue = band_Pan.getSampleFloat(773, 2217);
            assertEquals(3.9406f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(2046, 1731);
            assertEquals(18.9441f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(5134, 782);
            assertEquals(5.5514f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(4329, 1300);
            assertEquals(3.8074f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(4984, 4520);
            assertEquals(7.9743f, pixelValue, 4);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductGeometrySubset() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "ZON24_I200862_FL01-P369685"+ File.separator+ "ZON24_README.XML");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "200");
        try {
            JtsGeometryConverter converter = new JtsGeometryConverter();
            Geometry geometry = converter.parse("POLYGON ((25.38511848449707 55.59101486206055, 25.389955520629883 55.59107971191406, 25.394794464111328 55.59114456176758, 25.39963150024414 55.59120559692383, 25.404468536376953 55.591270446777344, 25.409305572509766 55.591331481933594, 25.41414451599121 55.59139633178711, 25.418981552124023 55.59145736694336, 25.423818588256836 55.59151840209961, 25.42865753173828 55.591583251953125, 25.430139541625977 55.59160232543945, 25.430248260498047 55.588863372802734, 25.43035888671875 55.58612060546875, 25.43046760559082 55.58338165283203, 25.43057632446289 55.58064270019531, 25.430686950683594 55.577903747558594, 25.430795669555664 55.575164794921875, 25.430904388427734 55.572425842285156, 25.431015014648438 55.56968307495117, 25.431015014648438 55.569664001464844, 25.429533004760742 55.569644927978516, 25.424697875976562 55.569580078125, 25.419862747192383 55.56951904296875, 25.415027618408203 55.5694580078125, 25.410192489624023 55.569393157958984, 25.405359268188477 55.569332122802734, 25.400524139404297 55.56926727294922, 25.395689010620117 55.56920623779297, 25.390853881835938 55.56914138793945, 25.386018753051758 55.56907653808594, 25.386018753051758 55.56909942626953, 25.385906219482422 55.57183837890625, 25.385793685913086 55.574581146240234, 25.38568115234375 55.57732009887695, 25.385568618774414 55.58005905151367, 25.385456085205078 55.58279800415039, 25.385343551635742 55.58553695678711, 25.385231018066406 55.58827590942383, 25.38511848449707 55.59101486206055))");
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"NIR1", "Red", "Pan"});
            subsetDef.setGeoRegion(geometry);
            subsetDef.setSubSampling(1, 1);
            subsetDef.setIgnoreMetadata(true);

            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(55.5803f, productOrigin.lat,4);
            assertEquals(23.8304f, productOrigin.lon,4);

            assertEquals(3, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
            assertEquals(WorldView2Constants.PRODUCT_TYPE, finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(5677, finalProduct.getSceneRasterWidth());
            assertEquals(4885, finalProduct.getSceneRasterHeight());
            assertEquals("02-MAY-2017 09:13:54.600000", finalProduct.getStartTime().toString());
            assertEquals("02-MAY-2017 09:14:08.999000", finalProduct.getEndTime().toString());
            assertEquals("metadata", finalProduct.getMetadataRoot().getName());
            assertEquals("ZON24_I200862_FL01-P369685", finalProduct.getName());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

            Band band_NIR1 = finalProduct.getBand("NIR1");
            assertEquals(1419, band_NIR1.getRasterWidth());
            assertEquals(1221, band_NIR1.getRasterHeight());

            float pixelValue = band_NIR1.getSampleFloat(124, 130);
            assertEquals(6.2642f, pixelValue, 4);
            pixelValue = band_NIR1.getSampleFloat(513, 404);
            assertEquals(13.9893f, pixelValue, 4);
            pixelValue = band_NIR1.getSampleFloat(1110, 1145);
            assertEquals(6.8708f, pixelValue, 4);
            pixelValue = band_NIR1.getSampleFloat(1010, 548);
            assertEquals(9.2602f, pixelValue, 4);
            pixelValue = band_NIR1.getSampleFloat(1362, 88);
            assertEquals(8.8640f, pixelValue, 4);

            Band band_Red = finalProduct.getBand("Red");
            assertEquals(1419, band_Red.getRasterWidth());
            assertEquals(1221, band_Red.getRasterHeight());

            pixelValue = band_Red.getSampleFloat(124, 130);
            assertEquals(3.8646f, pixelValue, 4);
            pixelValue = band_Red.getSampleFloat(513, 404);
            assertEquals(18.2270f, pixelValue, 4);
            pixelValue = band_Red.getSampleFloat(1110, 1145);
            assertEquals(3.9799f, pixelValue, 4);
            pixelValue = band_Red.getSampleFloat(1010, 548);
            assertEquals(3.7684f, pixelValue, 4);
            pixelValue = band_Red.getSampleFloat(1362, 88);
            assertEquals(3.8453f, pixelValue, 4);

            Band band_Pan = finalProduct.getBand("Pan");
            assertEquals(5677, band_Pan.getRasterWidth());
            assertEquals(4885, band_Pan.getRasterHeight());

            pixelValue = band_Pan.getSampleFloat(773, 2217);
            assertEquals(3.9406f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(2046, 1731);
            assertEquals(18.9441f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(5134, 782);
            assertEquals(5.5514f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(4329, 1300);
            assertEquals(3.8074f, pixelValue, 4);
            pixelValue = band_Pan.getSampleFloat(4984, 4520);
            assertEquals(7.9743f, pixelValue, 4);
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
        File file = TestUtil.getTestFile(productsFolder + "ZON24_I200862_FL01-P369685/ZON24_README.XML");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("ZON24_README.XML", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

}
