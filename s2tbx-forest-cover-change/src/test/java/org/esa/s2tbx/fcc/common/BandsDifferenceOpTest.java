package org.esa.s2tbx.fcc.common;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.junit.Test;

import javax.media.jai.JAI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Jean Coravu
 */
public class BandsDifferenceOpTest {

    public BandsDifferenceOpTest() {
    }

    @Test
    public void testBandsDifferenceOp() {
        int sceneRasterWidth = 100;
        int sceneRasterHeight = 100;

        Product firstProduct = buildFirstProduct("FirstProduct", "BandsDifference", sceneRasterWidth, sceneRasterHeight);
        Product secondProduct = buildSecondProduct("SecondProduct", "BandsDifference", sceneRasterWidth, sceneRasterHeight);

        Map<String, Product> sourceProducts = new HashMap<String, Product>();

        Map<String, Object> parameters = new HashMap<>();

        Operator operator = GPF.getDefaultInstance().createOperator("BandsDifferenceOp", parameters, sourceProducts, null);
        operator.setSourceProducts(firstProduct, secondProduct);

        // execute the operator
        operator.execute(ProgressMonitor.NULL);

        // get the operator target product
        Product targetProduct = operator.getTargetProduct();

        assertNotNull(targetProduct);

        assertEquals(targetProduct.getName(), "BandsDifference");
        assertEquals(targetProduct.getProductType(), "difference");
        assertEquals(targetProduct.getSceneRasterWidth(), sceneRasterWidth);
        assertEquals(targetProduct.getSceneRasterHeight(), sceneRasterHeight);
        assertEquals(targetProduct.getNumBands(), 1);

        Band band = targetProduct.getBandAt(0);
        assertNotNull(band);

        assertEquals(ProductData.TYPE_FLOAT32, band.getDataType());

        long size = targetProduct.getSceneRasterWidth() * targetProduct.getSceneRasterHeight();
        assertEquals(size, band.getNumDataElems());

        float delta = 0.0f;

        assertEquals(2969.0f, band.getSampleFloat(34, 65), delta);
        assertEquals(3563.0f, band.getSampleFloat(14, 51), delta);
        assertEquals(-1.0f, band.getSampleFloat(1, 2), delta);
        assertEquals(98.0f, band.getSampleFloat(21, 23), delta);
        assertEquals(890.0f, band.getSampleFloat(89, 99), delta);
        assertEquals(-7822.0f, band.getSampleFloat(90, 12), delta);
        assertEquals(1187.0f, band.getSampleFloat(32, 45), delta);
        assertEquals(-100.0f, band.getSampleFloat(19, 19), delta);
        assertEquals(3959.0f, band.getSampleFloat(35, 76), delta);
        assertEquals(-8020.0f, band.getSampleFloat(89, 9), delta);
        assertEquals(-1189.0f, band.getSampleFloat(65, 54), delta);
        assertEquals(5147.0f, band.getSampleFloat(23, 76), delta);
        assertEquals(-5545.0f, band.getSampleFloat(93, 38), delta);
        assertEquals(-298.0f, band.getSampleFloat(47, 45), delta);
        assertEquals(890.0f, band.getSampleFloat(77, 87), delta);
    }

    private static Product buildFirstProduct(String productName, String productType, int sceneRasterWidth, int sceneRasterHeight) {
        Product product = new Product(productName, productType, sceneRasterWidth, sceneRasterHeight);

        product.setPreferredTileSize(JAI.getDefaultTileSize());
        Band firstBand = product.addBand("band_1", ProductData.TYPE_FLOAT32);

        ProductData data = firstBand.createCompatibleRasterData();
        int count = firstBand.getRasterWidth() * firstBand.getRasterHeight();
        for (int i = 0; i < count; i++) {
            float value = i + 1;
            data.setElemFloatAt(i, value);
        }
        firstBand.setData(data);

        return product;
    }

    private static Product buildSecondProduct(String productName, String productType, int sceneRasterWidth, int sceneRasterHeight) {
        Product product = new Product(productName, productType, sceneRasterWidth, sceneRasterHeight);

        product.setPreferredTileSize(JAI.getDefaultTileSize());
        Band firstBand = product.addBand("band_1", ProductData.TYPE_FLOAT32);

        ProductData data = firstBand.createCompatibleRasterData();
        firstBand.setData(data);
        int count = 100;
        for (int x=0; x<sceneRasterWidth; x++) {
            for (int y=0; y<sceneRasterHeight; y++) {
                firstBand.setPixelFloat(x, y, ++count);
            }
        }

        return product;
    }
}
