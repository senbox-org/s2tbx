package org.esa.s2tbx.fcc.common;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;

import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public class BandsExtractorOpTest {

    public BandsExtractorOpTest() {
    }

    @Test
    public void testBandsExtractorOp() {
        int sceneRasterWidth = 100;
        int sceneRasterHeight = 100;

        Product product = new Product("P1", "bandsExtractor", sceneRasterWidth, sceneRasterHeight);
        product.addBand("b1", ProductData.TYPE_FLOAT32);
        product.addBand("b2", ProductData.TYPE_FLOAT64);
        product.addBand("b3", ProductData.TYPE_INT16);

        String[] bandNames = { "b2", "b3" };
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sourceBandNames", bandNames);

        Map<String, Product> sourceProducts = new HashMap<String, Product>();
        sourceProducts.put("sourceProduct", product);

        Operator operator = GPF.getDefaultInstance().createOperator("BandsExtractorOp", parameters, sourceProducts, null);

        // execute the operator
        operator.execute(ProgressMonitor.NULL);

        // get the operator target product
        Product targetProduct = operator.getTargetProduct();

        assertNotNull(targetProduct);

        assertEquals(targetProduct.getName(), "P1");
        assertEquals(targetProduct.getProductType(), "bandsExtractor");
        assertEquals(targetProduct.getSceneRasterWidth(), sceneRasterWidth);
        assertEquals(targetProduct.getSceneRasterHeight(), sceneRasterHeight);
        assertEquals(targetProduct.getNumBands(), 2);
        assertEquals(targetProduct.getStartTime(), product.getStartTime());
        assertEquals(targetProduct.getEndTime(), product.getEndTime());
        assertEquals(targetProduct.getNumResolutionsMax(), product.getNumResolutionsMax());
        assertEquals(targetProduct.getSceneGeoCoding(), product.getSceneGeoCoding());

        Band b0 = targetProduct.getBandAt(0);
        testBand(b0,ProductData.TYPE_FLOAT64, "b2", sceneRasterWidth, sceneRasterHeight);

        Band b1 = targetProduct.getBandAt(1);
        testBand(b1, ProductData.TYPE_INT16, "b3", sceneRasterWidth, sceneRasterHeight);
    }

    private void testBand(Band band, int dataType, String bandName, int sceneRasterWidth, int sceneRasterHeight) {
        assertEquals(band.getName(), bandName);
        assertEquals(band.getDataType(), dataType);
        assertEquals(band.getRasterWidth(), sceneRasterWidth);
        assertEquals(band.getRasterHeight(), sceneRasterHeight);
    }
}