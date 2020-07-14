package org.esa.s2tbx.dataio.muscate;

import com.bc.ceres.binding.ConversionException;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.subset.GeometrySubsetRegion;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.ProductUtils;
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

/**
 * @author Denisa Stefanescu
 */
public class MuscateProductReaderTest {

    private static final String PRODUCTS_FOLDER = "S2"+ File.separator+ "MUSCATE" + File.separator;

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testGetReaderPlugin() {
        ProductReader reader = buildProductReader();
        assertEquals(MuscateProductReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SENTINEL2A_20160205-103556-319_L2A_T31TFK_D_V1-0.zip");

        Rectangle subsetRegion = new Rectangle(1776, 1332, 6439, 5995);
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"AOT_R1", "Surface_Reflectance_B11", "Aux_IA_R2", "AOT_Interpolation_Mask_R2", "edge_mask_R1"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(subsetRegion, 0));
        subsetDef.setSubSampling(1, 1);

        ProductReader reader = buildProductReader();

        Product finalProduct = reader.readProductNodes(file, subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(45.0241f, productOrigin.lat, 4);
        assertEquals(5.4948f, productOrigin.lon, 4);

        assertEquals(3, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals("MUSCATE", finalProduct.getProductType());
        //the band associated with edge_mask_R1 mask is not selected, the mask will not be added to the final product
        assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(6439, finalProduct.getSceneRasterWidth());
        assertEquals(5995, finalProduct.getSceneRasterHeight());
        assertEquals("SENTINEL2A_20160205-103556-319_L2A_T31TFK_D_V1-0", finalProduct.getName());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

        Mask maskWater = finalProduct.getMaskGroup().get("AOT_Interpolation_Mask_R2");
        assertEquals(3220, maskWater.getRasterWidth());
        assertEquals(2998, maskWater.getRasterHeight());

        Band band_R1 = finalProduct.getBand("AOT_R1");
        assertEquals(6439, band_R1.getRasterWidth());
        assertEquals(5995, band_R1.getRasterHeight());

        float pixelValue = band_R1.getSampleFloat(1489, 1307);
        assertEquals(0.1050f, pixelValue, 4);
        pixelValue = band_R1.getSampleFloat(2450, 774);
        assertEquals(0.0750f, pixelValue, 4);
        pixelValue = band_R1.getSampleFloat(3835, 5602);
        assertEquals(0.1150f, pixelValue, 4);
        pixelValue = band_R1.getSampleFloat(6381, 5992);
        assertEquals(0.1000f, pixelValue, 4);

        Band band_B11 = finalProduct.getBand("Surface_Reflectance_B11");
        assertEquals(3220, band_B11.getRasterWidth());
        assertEquals(2998, band_B11.getRasterHeight());

        pixelValue = band_B11.getSampleFloat(1489, 1307);
        assertEquals(0.2750f, pixelValue, 4);
        pixelValue = band_B11.getSampleFloat(2450, 774);
        assertEquals(0.1610f, pixelValue, 4);
        pixelValue = band_B11.getSampleFloat(2352, 2532);
        assertEquals(0.4850f, pixelValue, 4);
        pixelValue = band_B11.getSampleFloat(3137, 2892);
        assertEquals(0.0038f, pixelValue, 4);

        Band band_R2 = finalProduct.getBand("Aux_IA_R2");
        assertEquals(3220, band_R2.getRasterWidth());
        assertEquals(2998, band_R2.getRasterHeight());

        pixelValue = band_R2.getSampleFloat(1489, 1307);
        assertEquals(0.0f, pixelValue, 4);
        pixelValue = band_R2.getSampleFloat(2450, 774);
        assertEquals(1.0f, pixelValue, 4);
        pixelValue = band_R2.getSampleFloat(2352, 2532);
        assertEquals(0.0f, pixelValue, 4);
        pixelValue = band_R2.getSampleFloat(3137, 2892);
        assertEquals(0.0f, pixelValue, 4);
    }

    @Test
    public void testReadProductGeometrySubset() throws IOException, ConversionException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(PRODUCTS_FOLDER + "SENTINEL2A_20160205-103556-319_L2A_T31TFK_D_V1-0.zip");

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((4.494754314422607 45.02415084838867, 4.5897908210754395 45.022865295410156, 4.684821128845215 45.02150344848633," +
                " 4.779844284057617 45.02006149291992, 4.874859809875488 45.0185432434082," +
                " 4.969868183135986 45.01694107055664, 5.064867973327637 45.015262603759766," +
                " 5.1598591804504395 45.01350402832031, 5.2548418045043945 45.01166915893555," +
                " 5.311522483825684 45.010536193847656, 5.308815956115723 44.94316482543945," +
                " 5.306118965148926 44.87579345703125, 5.303431510925293 44.80842208862305," +
                " 5.300753593444824 44.741050720214844, 5.298084735870361 44.673675537109375," +
                " 5.2954254150390625 44.606300354003906, 5.2927751541137695 44.53892517089844," +
                " 5.290134429931641 44.47154998779297, 5.29012393951416 44.47127914428711," +
                " 5.233966827392578 44.47239303588867, 5.139861583709717 44.47419738769531," +
                " 5.045748233795166 44.475921630859375, 4.951626777648926 44.477569580078125," +
                " 4.857497215270996 44.4791374206543, 4.763360023498535 44.48063278198242," +
                " 4.669216156005859 44.48204803466797, 4.5750651359558105 44.48338317871094," +
                " 4.480907917022705 44.48464584350586, 4.48091459274292 44.48491287231445," +
                " 4.48262357711792 44.55232238769531, 4.484338283538818 44.619728088378906," +
                " 4.486059188842773 44.6871337890625, 4.487785816192627 44.754539489746094," +
                " 4.489518642425537 44.82194519042969, 4.491257667541504 44.889347076416016," +
                " 4.493002891540527 44.956748962402344, 4.494754314422607 45.02415084838867))");
        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"AOT_R1", "Surface_Reflectance_B11", "Aux_IA_R2", "AOT_Interpolation_Mask_R2", "edge_mask_R1"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);

        ProductReader reader = buildProductReader();

        Product finalProduct = reader.readProductNodes(file, subsetDef);

        assertNotNull(finalProduct.getSceneGeoCoding());
        GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
        assertEquals(45.0241f, productOrigin.lat, 4);
        assertEquals(5.4948f, productOrigin.lon, 4);

        assertEquals(3, finalProduct.getBands().length);
        assertEquals("EPSG:World Geodetic System 1984", finalProduct.getSceneGeoCoding().getGeoCRS().getName().toString());
        assertEquals("MUSCATE", finalProduct.getProductType());
        //the band associated with edge_mask_R1 mask is not selected, the mask will not be added to the final product
        assertEquals(1, finalProduct.getMaskGroup().getNodeCount());
        assertEquals(6439, finalProduct.getSceneRasterWidth());
        assertEquals(5995, finalProduct.getSceneRasterHeight());
        assertEquals("SENTINEL2A_20160205-103556-319_L2A_T31TFK_D_V1-0", finalProduct.getName());
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);

        Mask maskWater = finalProduct.getMaskGroup().get("AOT_Interpolation_Mask_R2");
        assertEquals(3220, maskWater.getRasterWidth());
        assertEquals(2998, maskWater.getRasterHeight());

        Band band_R1 = finalProduct.getBand("AOT_R1");
        assertEquals(6439, band_R1.getRasterWidth());
        assertEquals(5995, band_R1.getRasterHeight());

        float pixelValue = band_R1.getSampleFloat(1489, 1307);
        assertEquals(0.1050f, pixelValue, 4);
        pixelValue = band_R1.getSampleFloat(2450, 774);
        assertEquals(0.0750f, pixelValue, 4);
        pixelValue = band_R1.getSampleFloat(3835, 5602);
        assertEquals(0.1150f, pixelValue, 4);
        pixelValue = band_R1.getSampleFloat(6381, 5992);
        assertEquals(0.1000f, pixelValue, 4);

        Band band_B11 = finalProduct.getBand("Surface_Reflectance_B11");
        assertEquals(3220, band_B11.getRasterWidth());
        assertEquals(2998, band_B11.getRasterHeight());

        pixelValue = band_B11.getSampleFloat(1489, 1307);
        assertEquals(0.2750f, pixelValue, 4);
        pixelValue = band_B11.getSampleFloat(2450, 774);
        assertEquals(0.1610f, pixelValue, 4);
        pixelValue = band_B11.getSampleFloat(2352, 2532);
        assertEquals(0.4850f, pixelValue, 4);
        pixelValue = band_B11.getSampleFloat(3137, 2892);
        assertEquals(0.0038f, pixelValue, 4);

        Band band_R2 = finalProduct.getBand("Aux_IA_R2");
        assertEquals(3220, band_R2.getRasterWidth());
        assertEquals(2998, band_R2.getRasterHeight());

        pixelValue = band_R2.getSampleFloat(1489, 1307);
        assertEquals(0.0f, pixelValue, 4);
        pixelValue = band_R2.getSampleFloat(2450, 774);
        assertEquals(1.0f, pixelValue, 4);
        pixelValue = band_R2.getSampleFloat(2352, 2532);
        assertEquals(0.0f, pixelValue, 4);
        pixelValue = band_R2.getSampleFloat(3137, 2892);
        assertEquals(0.0f, pixelValue, 4);
    }

    private static ProductReader buildProductReader() {
        MuscateProductReaderPlugin plugin = new MuscateProductReaderPlugin();
        return plugin.createReaderInstance();
    }
}
