package org.esa.s2tbx.fcc.common;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public class BandsExtractorOpTest {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    private static Product product;

    @BeforeClass
    public static void setup() throws FactoryException, TransformException, IOException {
        product = createProduct("P1");
    }

    @AfterClass
    public static void teardown() {
        product.dispose();
    }

    @Test
    public void testBandsExtractorOp() throws IOException {
        final BandsExtractorOp op = new BandsExtractorOp();
        op.setParameterDefaultValues();
        op.setSourceProduct(product);
        String[] bandNames = { "b2", "b3" };
        op.setParameter("sourceBandNames", bandNames);
        final Product productExtractedBands = op.getTargetProduct();

        assertEquals(productExtractedBands.getNumBands(), 2);
        assertEquals(productExtractedBands.getBandAt(0).getName(), "b2");
        assertEquals(productExtractedBands.getBandAt(1).getName(), "b3");
        assertEquals(productExtractedBands.getStartTime(), product.getStartTime());
        assertEquals(productExtractedBands.getEndTime(), product.getEndTime());
        assertEquals(productExtractedBands.getNumResolutionsMax(), product.getNumResolutionsMax());
        assertEquals(productExtractedBands.getSceneGeoCoding(), product.getSceneGeoCoding());
    }

    private static Product createProduct(final String name) {
        final Product product = new Product(name, "Mosaic", WIDTH, HEIGHT);
        final Band band1 = new Band("b1", ProductData.TYPE_FLOAT32, WIDTH, HEIGHT);
        product.addBand(band1);
        final Band band2 = new Band("b2", ProductData.TYPE_FLOAT32, WIDTH, HEIGHT);
        product.addBand(band2);
        final Band band3 = new Band("b3", ProductData.TYPE_FLOAT32, WIDTH, HEIGHT);
        product.addBand(band3);
        return product;
    }

}