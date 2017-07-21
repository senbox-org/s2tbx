package org.esa.s2tbx.fcc.common;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public class BandsExtractorOpTest {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;


    @Test
    public void testBandsExtractorOp() throws IOException {
        Product product = createProduct("P1");
        final BandsExtractorOp op = new BandsExtractorOp();
        op.setParameterDefaultValues();
        op.setSourceProduct(product);
        String[] bandNames = { "b2", "b3" };
        op.setParameter("sourceBandNames", bandNames);

        final Product productExtractedBands = op.getTargetProduct();
        assertEquals(productExtractedBands.getName(), "P1");
        assertEquals(productExtractedBands.getProductType(), "bandsExtractor");
        assertEquals(productExtractedBands.getSceneRasterSize(), new Dimension(WIDTH, HEIGHT));
        assertEquals(productExtractedBands.getNumBands(), 2);
        assertEquals(productExtractedBands.getStartTime(), product.getStartTime());
        assertEquals(productExtractedBands.getEndTime(), product.getEndTime());
        assertEquals(productExtractedBands.getNumResolutionsMax(), product.getNumResolutionsMax());
        assertEquals(productExtractedBands.getSceneGeoCoding(), product.getSceneGeoCoding());

        Band b0 = productExtractedBands.getBandAt(0);
        Band b1 = productExtractedBands.getBandAt(1);
        testBand(b0,ProductData.TYPE_FLOAT64, "b2");
        testBand(b1, ProductData.TYPE_INT16, "b3");
    }

    private void testBand(Band band,int dataType, String bandName) {
        assertEquals(band.getName(), bandName);
        assertEquals(band.getDataType(), dataType);
        assertEquals(band.getRasterHeight(), HEIGHT);
        assertEquals(band.getRasterWidth(), WIDTH);
    }

    private static Product createProduct(final String name) {
        final Product product = new Product(name, "bandsExtractor", WIDTH, HEIGHT);
        final Band band1 = new Band("b1", ProductData.TYPE_FLOAT32, WIDTH, HEIGHT);
        product.addBand(band1);
        final Band band2 = new Band("b2", ProductData.TYPE_FLOAT64, WIDTH, HEIGHT);
        product.addBand(band2);
        final Band band3 = new Band("b3", ProductData.TYPE_INT16, WIDTH, HEIGHT);
        product.addBand(band3);
        return product;
    }

}