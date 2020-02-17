package org.esa.s2tbx.dataio.muscate;

import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

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
 * @author Denisa Stefanescu
 */
public class MuscateProductReaderTest {

    private ProductReader reader;
    private String productsFolder = "S2"+ File.separator+ "MUSCATE" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());
        MuscateProductReaderPlugin productReader = new MuscateProductReaderPlugin();
        reader = productReader.createReaderInstance();
    }

    @Test
    public void testReadProductSubset() {
        Date startDate = Calendar.getInstance().getTime();
        File file = TestUtil.getTestFile(productsFolder + "SENTINEL2A_20160205-103556-319_L2A_T31TFK_D_V1-0.zip");
        System.setProperty("snap.dataio.reader.tileWidth", "100");
        System.setProperty("snap.dataio.reader.tileHeight", "100");
        try {
            Rectangle subsetRegion = new Rectangle(1776, 1332, 6439, 5995);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[] { "AOT_R1", "Surface_Reflectance_B11", "Aux_IA_R2", "AOT_Interpolation_Mask_R2", "edge_mask_R1"} );
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);

            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(45.0241f, productOrigin.lat,4);
            assertEquals(5.4948f, productOrigin.lon,4);

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
            assertEquals(3221, maskWater.getRasterWidth());
            assertEquals(2999, maskWater.getRasterHeight());

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
            assertEquals(3221, band_B11.getRasterWidth());
            assertEquals(2999, band_B11.getRasterHeight());

            pixelValue = band_B11.getSampleFloat(1489, 1307);
            assertEquals(0.2750f, pixelValue, 4);
            pixelValue = band_B11.getSampleFloat(2450, 774);
            assertEquals(0.1610f, pixelValue, 4);
            pixelValue = band_B11.getSampleFloat(2352, 2532);
            assertEquals(0.4850f, pixelValue, 4);
            pixelValue = band_B11.getSampleFloat(3137, 2892);
            assertEquals(0.0038f, pixelValue, 4);

            Band band_R2 = finalProduct.getBand("Aux_IA_R2");
            assertEquals(3221, band_R2.getRasterWidth());
            assertEquals(2999, band_R2.getRasterHeight());

            pixelValue = band_R2.getSampleFloat(1489, 1307);
            assertEquals(0.0f, pixelValue, 4);
            pixelValue = band_R2.getSampleFloat(2450, 774);
            assertEquals(1.0f, pixelValue, 4);
            pixelValue = band_R2.getSampleFloat(2352, 2532);
            assertEquals(0.0f, pixelValue, 4);
            pixelValue = band_R2.getSampleFloat(3137, 2892);
            assertEquals(0.0f, pixelValue, 4);

        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }
}
