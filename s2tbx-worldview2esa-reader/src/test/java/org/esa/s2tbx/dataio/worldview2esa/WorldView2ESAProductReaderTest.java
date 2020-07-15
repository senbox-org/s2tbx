package org.esa.s2tbx.dataio.worldview2esa;

import com.bc.ceres.binding.ConversionException;
import org.esa.s2tbx.dataio.worldview2esa.common.WorldView2ESAConstants;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.subset.GeometrySubsetRegion;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.core.util.converters.JtsGeometryConverter;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class WorldView2ESAProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_worldView" + File.separator;

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testGetReaderPlugin() {
        ProductReader reader = buildProductReader();
        assertEquals(WorldView2ESAProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP" + File.separator + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML");

        ProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, null);

        assertEquals(9, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals(WorldView2ESAConstants.PRODUCT_TYPE, finalProduct.getProductType());
        assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(32768, finalProduct.getSceneRasterWidth());
        assertEquals(16384, finalProduct.getSceneRasterHeight());
        assertEquals("25-MAY-2011 09:53:46.000000", finalProduct.getStartTime().toString());
        assertEquals("25-MAY-2011 09:53:51.000000", finalProduct.getEndTime().toString());
        assertEquals("metadata", finalProduct.getMetadataRoot().getName());
        assertEquals("WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061", finalProduct.getName());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
    }

    @Test
    public void testReadProductNodesPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP" + File.separator + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML");

        Rectangle subsetRegion = new Rectangle(12376, 3315, 15250, 11493);
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"Coastal", "Red Edge", "Pan"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
        subsetDef.setSubSampling(1, 1);
        subsetDef.setIgnoreMetadata(true);

        ProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(44.4898f, productOrigin.lat, 4);
        assertEquals(23.8304f, productOrigin.lon, 4);

        assertEquals(3, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals(WorldView2ESAConstants.PRODUCT_TYPE, finalProduct.getProductType());
        assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(15250, finalProduct.getSceneRasterWidth());
        assertEquals(11493, finalProduct.getSceneRasterHeight());
        assertEquals("25-MAY-2011 09:53:46.000000", finalProduct.getStartTime().toString());
        assertEquals("25-MAY-2011 09:53:51.000000", finalProduct.getEndTime().toString());
        assertEquals("metadata", finalProduct.getMetadataRoot().getName());
        assertEquals("WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061", finalProduct.getName());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

        Band band_Coastal = finalProduct.getBand("Coastal");
        assertEquals(3812, band_Coastal.getRasterWidth());
        assertEquals(2874, band_Coastal.getRasterHeight());

        float pixelValue = band_Coastal.getSampleFloat(867, 1561);
        assertEquals(70.1595f, pixelValue, 4);
        pixelValue = band_Coastal.getSampleFloat(1307, 691);
        assertEquals(103.1758f, pixelValue, 4);
        pixelValue = band_Coastal.getSampleFloat(1920, 2640);
        assertEquals(82.3441f, pixelValue, 4);
        pixelValue = band_Coastal.getSampleFloat(2893, 1150);
        assertEquals(85.4885f, pixelValue, 4);
        pixelValue = band_Coastal.getSampleFloat(3539, 530);
        assertEquals(105.1411f, pixelValue, 4);

        Band band_Red_Edge = finalProduct.getBand("Red Edge");
        assertEquals(3812, band_Red_Edge.getRasterWidth());
        assertEquals(2874, band_Red_Edge.getRasterHeight());

        pixelValue = band_Red_Edge.getSampleFloat(456, 405);
        assertEquals(31.6270f, pixelValue, 4);
        pixelValue = band_Red_Edge.getSampleFloat(1080, 926);
        assertEquals(51.2204f, pixelValue, 4);
        pixelValue = band_Red_Edge.getSampleFloat(1142, 2071);
        assertEquals(67.4197f, pixelValue, 4);
        pixelValue = band_Red_Edge.getSampleFloat(2849, 1154);
        assertEquals(122.0342f, pixelValue, 4);
        pixelValue = band_Red_Edge.getSampleFloat(3135, 343);
        assertEquals(110.4634f, pixelValue, 4);

        Band band_Pan = finalProduct.getBand("Pan");
        assertEquals(15250, band_Pan.getRasterWidth());
        assertEquals(11493, band_Pan.getRasterHeight());

        pixelValue = band_Pan.getSampleFloat(1783, 1636);
        assertEquals(43.0963f, pixelValue, 4);
        pixelValue = band_Pan.getSampleFloat(5614, 5937);
        assertEquals(44.2934f, pixelValue, 4);
        pixelValue = band_Pan.getSampleFloat(11485, 4469);
        assertEquals(86.7912f, pixelValue, 4);
        pixelValue = band_Pan.getSampleFloat(11470, 4630);
        assertEquals(99.5605f, pixelValue, 4);
        pixelValue = band_Pan.getSampleFloat(8190, 10546);
        assertEquals(81.2047f, pixelValue, 4);
    }

    @Test
    public void testReadProductNodesGeometrySubset() throws ConversionException, IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP" + File.separator + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML");

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((23.830440521240234 44.48979568481445, 23.837656021118164 44.48961639404297, 23.84486961364746 44.489437103271484, 23.85208511352539 44.4892578125, 23.85930061340332 44.489078521728516, 23.86651611328125 44.488895416259766, 23.873729705810547 44.48871612548828, 23.880945205688477 44.48853302001953, 23.888160705566406 44.48834991455078, 23.895374298095703 44.48816680908203, 23.902589797973633 44.48798370361328, 23.907060623168945 44.48786926269531, 23.906803131103516 44.482704162597656, 23.90654754638672 44.477542877197266, 23.90629005432129 44.47237777709961, 23.906034469604492 44.46721267700195, 23.905776977539062 44.4620475769043, 23.905521392822266 44.45688247680664, 23.90526580810547 44.451717376708984, 23.90500831604004 44.446556091308594, 23.90500831604004 44.446537017822266, 23.90053939819336 44.446651458740234, 23.893329620361328 44.446834564208984, 23.886119842529297 44.44701385498047, 23.878910064697266 44.44719696044922, 23.871700286865234 44.44738006591797, 23.864490509033203 44.44755935668945, 23.857280731201172 44.4477424621582, 23.85007095336914 44.44792175292969, 23.84286117553711 44.44810104370117, 23.835651397705078 44.448280334472656, 23.828441619873047 44.44845962524414, 23.828441619873047 44.44847869873047, 23.828691482543945 44.453643798828125, 23.828941345214844 44.45880889892578, 23.829191207885742 44.46397399902344, 23.82944107055664 44.46913528442383, 23.82969093322754 44.474300384521484, 23.829940795898438 44.47946548461914, 23.830190658569336 44.4846305847168, 23.830440521240234 44.48979568481445))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"Coastal", "Red Edge", "Pan"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);
        subsetDef.setIgnoreMetadata(true);

        ProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(44.4898f, productOrigin.lat, 4);
        assertEquals(23.8304f, productOrigin.lon, 4);

        assertEquals(3, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals(WorldView2ESAConstants.PRODUCT_TYPE, finalProduct.getProductType());
        assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(15250, finalProduct.getSceneRasterWidth());
        assertEquals(11493, finalProduct.getSceneRasterHeight());
        assertEquals("25-MAY-2011 09:53:46.000000", finalProduct.getStartTime().toString());
        assertEquals("25-MAY-2011 09:53:51.000000", finalProduct.getEndTime().toString());
        assertEquals("metadata", finalProduct.getMetadataRoot().getName());
        assertEquals("WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061", finalProduct.getName());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

        Band band_Coastal = finalProduct.getBand("Coastal");
        assertEquals(3812, band_Coastal.getRasterWidth());
        assertEquals(2874, band_Coastal.getRasterHeight());

        float pixelValue = band_Coastal.getSampleFloat(867, 1561);
        assertEquals(70.1595f, pixelValue, 4);
        pixelValue = band_Coastal.getSampleFloat(1307, 691);
        assertEquals(103.1758f, pixelValue, 4);
        pixelValue = band_Coastal.getSampleFloat(1920, 2640);
        assertEquals(82.3441f, pixelValue, 4);
        pixelValue = band_Coastal.getSampleFloat(2893, 1150);
        assertEquals(85.4885f, pixelValue, 4);
        pixelValue = band_Coastal.getSampleFloat(3539, 530);
        assertEquals(105.1411f, pixelValue, 4);

        Band band_Red_Edge = finalProduct.getBand("Red Edge");
        assertEquals(3812, band_Red_Edge.getRasterWidth());
        assertEquals(2874, band_Red_Edge.getRasterHeight());

        pixelValue = band_Red_Edge.getSampleFloat(456, 405);
        assertEquals(31.6270f, pixelValue, 4);
        pixelValue = band_Red_Edge.getSampleFloat(1080, 926);
        assertEquals(51.2204f, pixelValue, 4);
        pixelValue = band_Red_Edge.getSampleFloat(1142, 2071);
        assertEquals(67.4197f, pixelValue, 4);
        pixelValue = band_Red_Edge.getSampleFloat(2849, 1154);
        assertEquals(122.0342f, pixelValue, 4);
        pixelValue = band_Red_Edge.getSampleFloat(3135, 343);
        assertEquals(110.4634f, pixelValue, 4);

        Band band_Pan = finalProduct.getBand("Pan");
        assertEquals(15250, band_Pan.getRasterWidth());
        assertEquals(11493, band_Pan.getRasterHeight());

        pixelValue = band_Pan.getSampleFloat(1783, 1636);
        assertEquals(43.0963f, pixelValue, 4);
        pixelValue = band_Pan.getSampleFloat(5614, 5937);
        assertEquals(44.2934f, pixelValue, 4);
        pixelValue = band_Pan.getSampleFloat(11485, 4469);
        assertEquals(86.7912f, pixelValue, 4);
        pixelValue = band_Pan.getSampleFloat(11470, 4630);
        assertEquals(99.5605f, pixelValue, 4);
        pixelValue = band_Pan.getSampleFloat(8190, 10546);
        assertEquals(81.2047f, pixelValue, 4);
    }

    @Test
    public void testGetProductComponentsOnFileInput() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.SIP" + File.separator + "WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML");
        WorldView2ESAProductReader reader = buildProductReader();
        Product finalProduct = reader.readProductNodes(file, null);
        TreeNode<File> components = reader.getProductComponents();
        assertEquals(1, components.getChildren().length);
        assertEquals("WV2_OPER_WV-110__2A_20110525T095346_N44-248_E023-873_4061.MD.XML", components.getChildren()[0].getId());
    }

    private static WorldView2ESAProductReader buildProductReader() {
        WorldView2ESAProductReaderPlugin plugin = new WorldView2ESAProductReaderPlugin();
        return (WorldView2ESAProductReader)plugin.createReaderInstance();
    }
}
