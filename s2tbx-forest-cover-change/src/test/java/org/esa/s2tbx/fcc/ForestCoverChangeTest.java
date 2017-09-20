package org.esa.s2tbx.fcc;

import org.esa.s2tbx.fcc.trimming.AbstractOpTest;
import org.esa.s2tbx.grm.GenericRegionMergingOp;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public class ForestCoverChangeTest extends AbstractOpTest {

    public ForestCoverChangeTest() {
    }

    @Test
    public void testForestCoverChange() throws Exception {
        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        File currentProductFile = this.forestCoverChangeTestsFolderPath.resolve("S2A_R093_T35UMP_20170628T092026.dim").toFile();
        Product currentSourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile, null);

        File previousProductFile = this.forestCoverChangeTestsFolderPath.resolve("S2A_20160713T125925_A005524_T35UMP.dim").toFile();
        Product previousSourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(previousProductFile, null);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("forestCoverPercentage",95.0f );
        parameters.put("totalIterationsForSecondSegmentation", 10 );
        parameters.put("regionMergingCriterion", GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION );
        parameters.put("shapeWeight",0.5f );
        parameters.put("spectralWeight",0.5f );
        parameters.put("threshold",5.0f );
        parameters.put("mergingCostCriterion",GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION );

        ForestCoverChange forestCoverChange = new ForestCoverChange(currentSourceProduct, previousSourceProduct, parameters);
        forestCoverChange.doExecute();
        Product targetProduct = forestCoverChange.getTargetProduct();

        assertNotNull(targetProduct);

        assertEquals(1, targetProduct.getNumBands());
        assertEquals(549, targetProduct.getSceneRasterWidth());
        assertEquals(549, targetProduct.getSceneRasterHeight());
        chechTargetProduct(targetProduct);
    }

    private static void chechTargetProduct(Product targetProduct) {
        Band band = checkTargetBand(targetProduct);

        assertEquals(0, band.getSampleInt(134, 165));
        assertEquals(1, band.getSampleInt(504, 220));
        assertEquals(3, band.getSampleInt(223, 384));
        assertEquals(1, band.getSampleInt(29, 434));
        assertEquals(3, band.getSampleInt(16, 507));
        assertEquals(3, band.getSampleInt(219, 384));
        assertEquals(1, band.getSampleInt(138, 163));
        assertEquals(3, band.getSampleInt(244, 156));
        assertEquals(1, band.getSampleInt(93, 358));
        assertEquals(3, band.getSampleInt(241, 155));
        assertEquals(3, band.getSampleInt(198, 161));
        assertEquals(1, band.getSampleInt(39, 509));

        assertEquals(0, band.getSampleInt(46, 366));
        assertEquals(0, band.getSampleInt(61, 47));
        assertEquals(0, band.getSampleInt(214, 240));
        assertEquals(2, band.getSampleInt(207, 386));
        assertEquals(2, band.getSampleInt(175, 166));
        assertEquals(0, band.getSampleInt(202, 137));
        assertEquals(0, band.getSampleInt(57, 367));
        assertEquals(0, band.getSampleInt(31, 480));
        assertEquals(0, band.getSampleInt(414, 445));
        assertEquals(0, band.getSampleInt(277, 245));
        assertEquals(0, band.getSampleInt(63, 368));
        assertEquals(0, band.getSampleInt(475, 63));
        assertEquals(0, band.getSampleInt(438, 213));
    }

    private static Band checkTargetBand(Product targetProduct) {
        Band targetBand = targetProduct.getBandAt(0);
        assertNotNull(targetBand);

        assertEquals(ProductData.TYPE_INT32, targetBand.getDataType());

        long size = targetProduct.getSceneRasterWidth() * targetProduct.getSceneRasterHeight();
        assertEquals(size, targetBand.getNumDataElems());

        return targetBand;
    }
}
