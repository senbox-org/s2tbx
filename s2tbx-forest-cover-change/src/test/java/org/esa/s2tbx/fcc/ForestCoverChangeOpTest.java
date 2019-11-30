package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.fcc.trimming.AbstractOpTest;
import org.esa.s2tbx.grm.GenericRegionMergingOp;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.test.LongTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@RunWith(LongTestRunner.class)
public class ForestCoverChangeOpTest extends AbstractOpTest {

    public ForestCoverChangeOpTest() {
    }

    @Test
    public void testForestCoverChangeOp() throws Exception {
        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        File currentProductFile = this.forestCoverChangeTestsFolderPath.resolve("S2A_R093_T35UMP_20170628T092026.dim").toFile();
        Product currentSourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile, null);

        File previousProductFile = this.forestCoverChangeTestsFolderPath.resolve("S2A_20160713T125925_A005524_T35UMP.dim").toFile();
        Product previousSourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(previousProductFile, null);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("forestCoverPercentage", 95.0f);
        parameters.put("totalIterationsForSecondSegmentation", 10);
        parameters.put("regionMergingCriterion", GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION);
        parameters.put("shapeWeight", 0.5f);
        parameters.put("spectralWeight", 0.5f);
        parameters.put("threshold", 5.0f);
        parameters.put("mergingCostCriterion", GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION);
        parameters.put("degreesOfFreedom", 4.0d);

        Map<String, Product> sourceProducts = new HashMap<String, Product>();
        sourceProducts.put("recentProduct", currentSourceProduct);
        sourceProducts.put("previousProduct", previousSourceProduct);

        // create the operator
        Operator operator = GPF.getDefaultInstance().createOperator("ForestCoverChangeOp", parameters, sourceProducts, null);

        // execute the operator
        operator.execute(ProgressMonitor.NULL);

        // get the operator target product
        Product targetProduct = operator.getTargetProduct();

        assertNotNull(targetProduct);

        assertEquals(549, targetProduct.getSceneRasterWidth());
        assertEquals(549, targetProduct.getSceneRasterHeight());

        assertEquals(1, targetProduct.getNumBands());

        Band band = targetProduct.getBandAt(0);
        assertNotNull(band);

        assertEquals(ProductData.TYPE_INT32, band.getDataType());

        long size = targetProduct.getSceneRasterWidth() * targetProduct.getSceneRasterHeight();
        assertEquals(size, band.getNumDataElems());

        checkBand(band);
    }

    private static void checkBand(Band band) {
        assertEquals(3, band.getSampleInt(134, 165));
        assertEquals(0, band.getSampleInt(504, 220));
        assertEquals(3, band.getSampleInt(223, 384));
        assertEquals(0, band.getSampleInt(29, 434));
        assertEquals(3, band.getSampleInt(16, 507));

        assertEquals(3, band.getSampleInt(219, 384));
        assertEquals(3, band.getSampleInt(138, 163));
        assertEquals(0, band.getSampleInt(244, 156));
        assertEquals(0, band.getSampleInt(93, 358));
        assertEquals(0, band.getSampleInt(241, 155));

        assertEquals(0, band.getSampleInt(198, 161));
        assertEquals(0, band.getSampleInt(39, 509));
        assertEquals(0, band.getSampleInt(46, 366));
        assertEquals(0, band.getSampleInt(61, 47));
        assertEquals(0, band.getSampleInt(214, 240));

        assertEquals(0, band.getSampleInt(207, 386));
        assertEquals(3, band.getSampleInt(175, 166));
        assertEquals(0, band.getSampleInt(202, 137));
        assertEquals(0, band.getSampleInt(57, 367));
        assertEquals(0, band.getSampleInt(31, 480));

        assertEquals(0, band.getSampleInt(414, 445));
        assertEquals(0, band.getSampleInt(277, 245));
        assertEquals(0, band.getSampleInt(63, 368));
        assertEquals(0, band.getSampleInt(475, 63));
        assertEquals(0, band.getSampleInt(438, 213));
    }
}
