package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.Graph;
import org.esa.s2tbx.grm.segmentation.Node;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.rgb.ImageProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Jean Coravu.
 * Note: Nodes order is not guaranteed to be identical on different platforms, therefore the tests should be done by node id, not by node order
 */
public class GenericRegionMergingOpTest {
    private Path segmentationTestsFolderPath;
    private Product smallSourceProduct;
    private Product largeSourceProduct;

    public GenericRegionMergingOpTest() {
    }

    @Before
    public void setUp() throws Exception {
        // TODO Temporary disable tests so it won't block the builds on snap-build-server.tilaa.cloud
        // TODO This test seems to be platform dependent (fail on Linux) => To be fixed by CS-RO
        assumeTrue(false);
        // assumeTrue(TestUtil.testdataAvailable());

        checkTestDirectoryExists();

        ImageProductReaderPlugIn readerPlugIn = new ImageProductReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        File smallProductFile = this.segmentationTestsFolderPath.resolve("picture-334x400.png").toFile();
        this.smallSourceProduct = reader.readProductNodes(smallProductFile, null);

        File largeProductFile = this.segmentationTestsFolderPath.resolve("picture-750x898.png").toFile();
        this.largeSourceProduct = reader.readProductNodes(largeProductFile, null);
    }

    @Test
    public void testFullLambdaScheduleTileSmallSegmenter() throws IOException, IllegalAccessException {
        String mergingCostCriterion = GenericRegionMergingOp.FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION;
        String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
        int totalIterationsForSecondSegmentation = 250;
        float threshold = 8600.0f;

        GenericRegionMergingOp operator = executeOperator(this.smallSourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                          totalIterationsForSecondSegmentation, threshold, null, null);

        Product targetProduct = operator.getTargetProduct();
        AbstractSegmenter segmenter = operator.getSegmenter();

        assertNotNull(segmenter);

        Graph graph = segmenter.getGraph();
        assertNotNull(graph);

        assertEquals(12, graph.getNodeCount());

        //Node node = graph.getNodeAt(0);
        Node node = graph.getNodeById(0);
        assertNotNull(node);
        float[] nodeExpectedMeansValues = new float[] {43.48049f, 92.38074f, 71.088326f};
        checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 287, 39, 1596, 3);

        //node = graph.getNodeAt(3);
        node = graph.getNodeById(6856);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {28.577358f, 77.67291f, 106.54587f};
        checkGraphNode(node, nodeExpectedMeansValues, 35, 20, 265, 134, 1840, 6);

        //node = graph.getNodeAt(7);
        node = graph.getNodeById(52515);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {7.4516973f, 204.85187f, 82.08506f};
        checkGraphNode(node, nodeExpectedMeansValues, 36, 157, 260, 31, 1200, 4);

        //node = graph.getNodeAt(11);
        node = graph.getNodeById(85823);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {53.42437f, 96.581116f, 78.46628f};
        checkGraphNode(node, nodeExpectedMeansValues, 0, 256, 334, 144, 2464, 3);

        checkTargetBandForFullLamdaScheduleSegmenter(targetProduct);
    }

    @Test
    public void testSpringTileSmallSegmenter() throws IOException, IllegalAccessException {
        String mergingCostCriterion = GenericRegionMergingOp.SPRING_MERGING_COST_CRITERION;
        String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
        int totalIterationsForSecondSegmentation = 1750;
        float threshold = 8650.0f;

        GenericRegionMergingOp operator = executeOperator(this.smallSourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                          totalIterationsForSecondSegmentation, threshold, null, null);

        Product targetProduct = operator.getTargetProduct();
        AbstractSegmenter segmenter = operator.getSegmenter();

        assertNotNull(segmenter);

        Graph graph = segmenter.getGraph();
        assertNotNull(graph);

        assertEquals(81, graph.getNodeCount());

        //Node node = graph.getNodeAt(0);
        Node node = graph.getNodeById(0);
        assertNotNull(node);
        float[] nodeExpectedMeansValues = new float[] {56.079975f, 105.31003f, 84.8043f};
        checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 334, 400, 2936, 28);

        //node = graph.getNodeAt(10);
        node = graph.getNodeById(46622);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {102.666664f, 147.33333f, 177.0f};
        checkGraphNode(node, nodeExpectedMeansValues, 196, 139, 2, 2, 16, 2);

        //node = graph.getNodeAt(20);
        node = graph.getNodeById(64872);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {29.915312f, 76.24685f, 105.828926f};
        checkGraphNode(node, nodeExpectedMeansValues, 38, 194, 254, 192, 2800, 53);

        //node = graph.getNodeAt(30);
        node = graph.getNodeById(66735);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {24.333334f, 101.0f, 131.66667f};
        checkGraphNode(node, nodeExpectedMeansValues, 269, 199, 3, 1, 16, 1);

        checkTargetBandForSpringSegmenter(targetProduct);
    }

    @Test
    public void testBaatzSchapeSmallTileSegmenter() throws IOException, IllegalAccessException {
        String mergingCostCriterion = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION;
        String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
        int totalIterationsForSecondSegmentation = 50;
        float threshold = 750.0f;
        float spectralWeight = 0.5f;
        float shapeWeight = 0.3f;

        GenericRegionMergingOp operator = executeOperator(this.smallSourceProduct, mergingCostCriterion, regionMergingCriterion, totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

        Product targetProduct = operator.getTargetProduct();
        AbstractSegmenter segmenter = operator.getSegmenter();

        assertNotNull(segmenter);

        Graph graph = segmenter.getGraph();
        assertNotNull(graph);

        assertEquals(4, graph.getNodeCount());

        // test the first node
        //Node node = graph.getNodeAt(0);
        Node node = graph.getNodeById(0);
        assertNotNull(node);
        float[] nodeExpectedMeansValues = new float[] {56.252293f, 103.91999f, 84.66389f};
        checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 334, 400, 2936, 3);

        // test the second node
        //node = graph.getNodeAt(1);
        node = graph.getNodeById(6956);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {28.649923f, 78.105064f, 106.62353f};
        checkGraphNode(node, nodeExpectedMeansValues, 33, 20, 268, 135, 1900, 1);

        // test the third node
        //node = graph.getNodeAt(2);
        node = graph.getNodeById(51723);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {10.381125f, 199.06993f, 82.21632f};
        checkGraphNode(node, nodeExpectedMeansValues, 36, 154, 261, 37, 1240, 1);

        // test the forth node
        //node = graph.getNodeAt(3);
        node = graph.getNodeById(64266);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {29.810776f, 78.85059f, 107.02629f};
        checkGraphNode(node, nodeExpectedMeansValues, 36, 192, 268, 189, 2292, 1);

        checkTargetBandForBaatzSchapeSmallSegmenter(targetProduct);
    }

    @Test
    public void testBaatzSchapeLargeTileSegmenter() throws IOException, IllegalAccessException {
        String mergingCostCriterion = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION;
        String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
        int totalIterationsForSecondSegmentation = 60;
        float threshold = 1000.0f;
        float spectralWeight = 0.5f;
        float shapeWeight = 0.3f;

        GenericRegionMergingOp operator = executeOperator(this.largeSourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                          totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

        Product targetProduct = operator.getTargetProduct();
        AbstractSegmenter segmenter = operator.getSegmenter();

        assertNotNull(segmenter);

        Graph graph = segmenter.getGraph();
        assertNotNull(graph);

        assertEquals(4, graph.getNodeCount());

        // test the first node
        //Node node = graph.getNodeAt(0);
        Node node = graph.getNodeById(0);
        assertNotNull(node);
        float[] nodeExpectedMeansValues = new float[] {51.66685f, 104.76079f, 90.67668f};
        checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 750, 898, 6592, 3);

        // test the second node
        //node = graph.getNodeAt(1);
        node = graph.getNodeById(268136);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {5.248829f, 209.01393f, 82.01373f};
        checkGraphNode(node, nodeExpectedMeansValues, 82, 357, 579, 66, 2932, 1);

        // test the third node
        //node = graph.getNodeAt(2);
        node = graph.getNodeById(326727);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {29.568516f, 74.19531f, 105.85714f};
        checkGraphNode(node, nodeExpectedMeansValues, 102, 435, 558, 419, 7556, 1);

        // test the forth node
        //node = graph.getNodeAt(3);
        node = graph.getNodeById(34389);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {27.908916f, 75.81499f, 105.56643f};
        checkGraphNode(node, nodeExpectedMeansValues, 72, 45, 570, 303, 5428, 1);

        checkTargetBandForBaatzSchapeSegmenter(targetProduct);
    }

    @Test
    public void testFastBaatzSchapeLargeTileSegmenter() throws IOException, IllegalAccessException {
        String mergingCostCriterion = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION;
        String regionMergingCriterion = GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION; // => fast segmentation
        int totalIterationsForSecondSegmentation = 60;
        float threshold = 1000.0f;
        float spectralWeight = 0.5f;
        float shapeWeight = 0.3f;

        GenericRegionMergingOp operator = executeOperator(this.largeSourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                          totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

        Product targetProduct = operator.getTargetProduct();
        AbstractSegmenter segmenter = operator.getSegmenter();

        assertNotNull(segmenter);

        Graph graph = segmenter.getGraph();
        assertNotNull(graph);

        assertEquals(4, graph.getNodeCount());

        // test the first node
        //Node node = graph.getNodeAt(0);
        Node node = graph.getNodeById(0);
        assertNotNull(node);
        float[] nodeExpectedMeansValues = new float[] {55.079082f, 104.811226f, 85.818596f};
        checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 750, 898, 6592, 3);

        // test the second node
        //node = graph.getNodeAt(1);
        node = graph.getNodeById(36172);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {28.557613f, 78.30157f, 106.95757f};
        checkGraphNode(node, nodeExpectedMeansValues, 74, 48, 601, 301, 5412, 1);

        // test the third node
        //node = graph.getNodeAt(2);
        node = graph.getNodeById(327464);// 265402
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {29.72f, 77.462265f, 107.269226f};
        checkGraphNode(node, nodeExpectedMeansValues, 101, 436, 581, 426, 6080, 1);

        // test the forth node
        //node = graph.getNodeAt(3);
        node = graph.getNodeById(265402);
        assertNotNull(node);
        nodeExpectedMeansValues = new float[] {5.579265f, 209.00505f, 82.32177f};
        checkGraphNode(node, nodeExpectedMeansValues, 81, 353, 586, 70, 2860, 1);

        checkTargetBandForFastBaatzSchapeSegmenter(targetProduct);
    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        Path testFolderPath = Paths.get(testDirectoryPathProperty);
        if (!Files.exists(testFolderPath)) {
            fail("The test directory path '"+testDirectoryPathProperty+"' is not valid.");
        }

        this.segmentationTestsFolderPath = testFolderPath.resolve("_segmentation");
        if (!Files.exists(segmentationTestsFolderPath)) {
            fail("The GDAL test directory path '"+segmentationTestsFolderPath.toString()+"' is not valid.");
        }
    }

    private static GenericRegionMergingOp executeOperator(Product sourceProduct, String mergingCostCriterion, String regionMergingCriterion,
                                                          int totalIterationsForSecondSegmentation, float threshold, Float spectralWeight, Float shapeWeight )
                                                          throws IOException {

        String[] sourceBandNames = new String[]{"red", "blue", "green"};

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mergingCostCriterion", mergingCostCriterion);
        parameters.put("regionMergingCriterion", regionMergingCriterion);
        parameters.put("totalIterationsForSecondSegmentation", totalIterationsForSecondSegmentation);
        parameters.put("threshold", threshold);
        parameters.put("sourceBandNames", sourceBandNames);
        if (spectralWeight != null) {
            parameters.put("spectralWeight", spectralWeight.floatValue());
        }
        if (shapeWeight != null) {
            parameters.put("shapeWeight", shapeWeight.floatValue());
        }

        Map<String, Product> sourceProducts = new HashMap<String, Product>();
        sourceProducts.put("source", sourceProduct);

        // create the operator
        GenericRegionMergingOp operator = (GenericRegionMergingOp)GPF.getDefaultInstance().createOperator("GenericRegionMergingOp", parameters, sourceProducts, null);

        Product targetProduct = operator.getTargetProduct(); // initialize the operator

        assertNotNull(targetProduct.getPreferredTileSize());

        assertEquals(mergingCostCriterion, operator.getMergingCostCriterion());
        assertEquals(regionMergingCriterion, operator.getRegionMergingCriterion());
        assertEquals(totalIterationsForSecondSegmentation, operator.getTotalIterationsForSecondSegmentation());
        assertEquals(threshold, operator.getThreshold(), 0.0f);
        if (spectralWeight != null) {
            assertEquals(spectralWeight.floatValue(), operator.getSpectralWeight(), 0.0f);
        }
        if (shapeWeight != null) {
            assertEquals(shapeWeight.floatValue(), operator.getShapeWeight(), 0.0f);
        }
        assertEquals(sourceBandNames.length, operator.getSourceBandNames().length);

        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(ProgressMonitor.NULL); // execute the operator

        return operator;
    }

    public static void checkGraphNode(Node node, float[] nodeExpectedMeansValues, int regionLeftX, int regionLeftY,
                                       int regionWidth, int regionHeight, int nodeContourSize, int nodeEdgeCount) {

        int length = node.getNumberOfComponentsPerPixel();
        assertEquals(nodeExpectedMeansValues.length, length);
        for (int i=0; i<length; i++) {
            assertEquals(nodeExpectedMeansValues[i], node.getMeansAt(i), 0.0f);
        }

        BoundingBox box = node.getBox();
        assertEquals(regionLeftX, box.getLeftX());
        assertEquals(regionLeftY, box.getTopY());
        assertEquals(regionWidth, box.getWidth());
        assertEquals(regionHeight, box.getHeight());

        assertEquals(nodeContourSize, node.getContour().size());

        assertEquals(nodeEdgeCount, node.getEdgeCount());
    }

    private static void checkTargetBandForSpringSegmenter(Product targetProduct) {
        Band band = checkTargetBand(targetProduct);

        int bandValue = band.getSampleInt(64, 84);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(164, 184);
        assertEquals(17, bandValue);

        bandValue = band.getSampleInt(264, 114);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(14, 18);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(123, 321);
        assertEquals(21, bandValue);

        bandValue = band.getSampleInt(200, 100);
        assertEquals(2, bandValue);
    }

    private static void checkTargetBandForFullLamdaScheduleSegmenter(Product targetProduct) {
        Band band = checkTargetBand(targetProduct);

        int bandValue = band.getSampleInt(64, 84);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(164, 184);
        assertEquals(8, bandValue);

        bandValue = band.getSampleInt(264, 114);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(14, 18);
        assertEquals(3, bandValue);

        bandValue = band.getSampleInt(123, 321);
        assertEquals(10, bandValue);

        bandValue = band.getSampleInt(200, 100);
        assertEquals(4, bandValue);
    }

    private static void checkTargetBandForBaatzSchapeSmallSegmenter(Product targetProduct) {
        Band band = checkTargetBand(targetProduct);

        int bandValue = band.getSampleInt(64, 84);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(164, 184);
        assertEquals(3, bandValue);

        bandValue = band.getSampleInt(264, 114);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(14, 18);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(123, 321);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(200, 100);
        assertEquals(2, bandValue);
    }

    private static void checkTargetBandForBaatzSchapeSegmenter(Product targetProduct) {
        Band band = checkTargetBand(targetProduct);

        int bandValue = band.getSampleInt(64, 84);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(164, 184);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(264, 114);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(14, 18);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(123, 321);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(200, 100);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(332, 178);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(614, 400);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(240, 510);
        assertEquals(3, bandValue);

        bandValue = band.getSampleInt(340, 410);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(164, 121);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(700, 868);
        assertEquals(1, bandValue);
    }

    private static void checkTargetBandForFastBaatzSchapeSegmenter(Product targetProduct) {
        Band band = checkTargetBand(targetProduct);

        int bandValue = band.getSampleInt(64, 84);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(164, 184);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(264, 114);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(14, 18);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(123, 321);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(200, 100);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(332, 178);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(614, 400);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(240, 510);
        assertEquals(3, bandValue);

        bandValue = band.getSampleInt(340, 410);
        assertEquals(4, bandValue);

        bandValue = band.getSampleInt(164, 121);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(700, 868);
        assertEquals(1, bandValue);
    }

    private static Band checkTargetBand(Product targetProduct) {
        assertNotNull(targetProduct);

        Band targetBand = targetProduct.getBandAt(0);
        assertNotNull(targetBand);

        assertEquals(ProductData.TYPE_INT32, targetBand.getDataType());

        long size = targetProduct.getSceneRasterWidth() * targetProduct.getSceneRasterHeight();
        assertEquals(size, targetBand.getNumDataElems());

        return targetBand;
    }
}
