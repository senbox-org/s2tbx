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
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Jean Coravu.
 *
 * Note: Nodes order is not guaranteed to be identical on different platforms,
 * therefore the tests should be done by node area and nod perimeter.
 */
public class GenericRegionMergingOpTest {

    public GenericRegionMergingOpTest() {
    }

    @Test
    public void testFullLambdaScheduleTileSmallSegmenter() throws IOException, IllegalAccessException, URISyntaxException {
        Product sourceProduct = readSourceProduct("picture-334x400.png");
        try {
            String mergingCostCriterion = GenericRegionMergingOp.FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION;
            String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
            int totalIterationsForSecondSegmentation = 250;
            float threshold = 8600.0f;
            Float spectralWeight = null;
            Float shapeWeight = null;

            GenericRegionMergingOp operator = executeOperator(sourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                              totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

            AbstractSegmenter segmenter = operator.getSegmenter();
            assertNotNull(segmenter);

            Graph graph = segmenter.getGraph();
            assertNotNull(graph);
            assertEquals(12, graph.getNodeCount());

            List<Node> nodes = findNodesByAreaAndPerimeter(graph, 6125, 798);
            assertEquals(1, nodes.size());
            float[] nodeExpectedMeansValues = new float[]{43.48049f, 92.38074f, 71.088326f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 0, 0, 287, 39, 1596, 3);

            nodes = findNodesByAreaAndPerimeter(graph, 33468, 924);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{28.577358f, 77.67291f, 106.54587f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 35, 20, 265, 134, 1840, 6);

            nodes = findNodesByAreaAndPerimeter(graph, 7277, 600);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{7.4516973f, 204.85187f, 82.08506f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 36, 157, 260, 31, 1200, 4);

            nodes = findNodesByAreaAndPerimeter(graph, 13672, 1232);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{53.42437f, 96.581116f, 78.46628f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 0, 256, 334, 144, 2464, 3);

            nodes = findNodesByAreaAndPerimeter(graph, 46173, 1060);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{29.666191f, 78.3342f, 107.13783f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 38, 194, 264, 184, 2120, 5);

            nodes = findNodesByAreaAndPerimeter(graph, 1638, 578);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{49.614162f, 104.15507f, 80.54029f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 58, 145, 243, 29, 1156, 4);

            Product targetProduct = operator.getTargetProduct();
            checkTargetBandForFullLamdaScheduleSegmenter(targetProduct, 1, 12);
        } finally {
            sourceProduct.dispose();
        }
    }

    @Test
    public void testSpringTileSmallSegmenter() throws IOException, IllegalAccessException, URISyntaxException {
        Product sourceProduct = readSourceProduct("picture-334x400.png");
        try {
            String mergingCostCriterion = GenericRegionMergingOp.SPRING_MERGING_COST_CRITERION;
            String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
            int totalIterationsForSecondSegmentation = 1750;
            float threshold = 8650.0f;
            Float spectralWeight = null;
            Float shapeWeight = null;

            GenericRegionMergingOp operator = executeOperator(sourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                              totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

            AbstractSegmenter segmenter = operator.getSegmenter();
            assertNotNull(segmenter);

            Graph graph = segmenter.getGraph();
            assertNotNull(graph);
            assertEquals(81, graph.getNodeCount());

            List<Node> nodes = findNodesByAreaAndPerimeter(graph, 3, 8);
            assertEquals(3, nodes.size());

            nodes = findNodesByAreaAndPerimeter(graph, 1, 4);
            assertEquals(56, nodes.size());

            nodes = findNodesByAreaAndPerimeter(graph, 45477, 4302);
            assertEquals(1, nodes.size());
            float[] nodeExpectedMeansValues = new float[]{56.079975f, 105.31003f, 84.8043f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 0, 0, 334, 400, 2936, 28);

            nodes = findNodesByAreaAndPerimeter(graph, 22, 26);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{15.727273f, 132.0f, 110.954544f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 40, 203, 4, 9, 52, 2);

            nodes = findNodesByAreaAndPerimeter(graph, 3, 8);
            assertEquals(3, nodes.size());
            nodeExpectedMeansValues = new float[]{102.666664f, 147.33333f, 177.0f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 196, 139, 2, 2, 16, 2);

            nodes = findNodesByAreaAndPerimeter(graph, 44011, 1622);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{29.915312f, 76.24685f, 105.828926f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 38, 194, 254, 192, 2800, 53);

            nodes = findNodesByAreaAndPerimeter(graph, 22, 26);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{15.727273f, 132.0f, 110.954544f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 40, 203, 4, 9, 52, 2);

            Product targetProduct = operator.getTargetProduct();
            checkTargetBandForSpringSegmenter(targetProduct, 1, 81);
        } finally {
            sourceProduct.dispose();
        }
    }

    @Test
    public void testBaatzSchapeSmallTileSegmenter() throws IOException, IllegalAccessException, URISyntaxException {
        Product sourceProduct = readSourceProduct("picture-334x400.png");
        try {
            String mergingCostCriterion = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION;
            String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
            int totalIterationsForSecondSegmentation = 50;
            float threshold = 750.0f;
            float spectralWeight = 0.5f;
            float shapeWeight = 0.3f;

            GenericRegionMergingOp operator = executeOperator(sourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                              totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

            AbstractSegmenter segmenter = operator.getSegmenter();
            assertNotNull(segmenter);

            Graph graph = segmenter.getGraph();
            assertNotNull(graph);
            assertEquals(4, graph.getNodeCount());

            List<Node> nodes = findNodesByAreaAndPerimeter(graph, 45146, 4184);
            assertEquals(1, nodes.size());
            float[] nodeExpectedMeansValues = new float[]{56.252293f, 103.91999f, 84.66389f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 0, 0, 334, 400, 2936, 3);

            nodes = findNodesByAreaAndPerimeter(graph, 33684, 950);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{28.649923f, 78.105064f, 106.62353f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 33, 20, 268, 135, 1900, 1);

            nodes = findNodesByAreaAndPerimeter(graph, 7979, 620);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{10.381125f, 199.06993f, 82.21632f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 36, 154, 261, 37, 1240, 1);

            nodes = findNodesByAreaAndPerimeter(graph, 46791, 1146);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{29.810776f, 78.85059f, 107.02629f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 36, 192, 268, 189, 2292, 1);

            Product targetProduct = operator.getTargetProduct();
            checkTargetBandForBaatzSchapeSmallSegmenter(targetProduct, 1, 4);
        } finally {
            sourceProduct.dispose();
        }
    }

    @Test
    public void testBaatzSchapeLargeTileSegmenter() throws IOException, IllegalAccessException, URISyntaxException {
        Product sourceProduct = readSourceProduct("picture-750x898.png");
        try {
            String mergingCostCriterion = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION;
            String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
            int totalIterationsForSecondSegmentation = 60;
            float threshold = 1000.0f;
            float spectralWeight = 0.5f;
            float shapeWeight = 0.3f;

            GenericRegionMergingOp operator = executeOperator(sourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                              totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

            AbstractSegmenter segmenter = operator.getSegmenter();
            assertNotNull(segmenter);

            Graph graph = segmenter.getGraph();
            assertNotNull(graph);
            assertEquals(4, graph.getNodeCount());

            List<Node> nodes = findNodesByAreaAndPerimeter(graph, 287418, 11254);
            assertEquals(1, nodes.size());
            float[] nodeExpectedMeansValues = new float[]{51.66685f, 104.76079f, 90.67668f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 0, 0, 750, 898, 6592, 3);

            nodes = findNodesByAreaAndPerimeter(graph, 34369, 1466);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{5.248829f, 209.01393f, 82.01373f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 82, 357, 579, 66, 2932, 1);

            nodes = findNodesByAreaAndPerimeter(graph, 196209, 3778);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{29.568516f, 74.19531f, 105.85714f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 102, 435, 558, 419, 7556, 1);

            nodes = findNodesByAreaAndPerimeter(graph, 155504, 2714);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{27.908916f, 75.81499f, 105.56643f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 72, 45, 570, 303, 5428, 1);

            Product targetProduct = operator.getTargetProduct();
            checkTargetBandForBaatzSchapeSegmenter(targetProduct, 1, 4);
        } finally {
            sourceProduct.dispose();
        }
    }

    @Test
    public void testFastBaatzSchapeLargeTileSegmenter() throws IOException, IllegalAccessException, URISyntaxException {
        Product sourceProduct = readSourceProduct("picture-750x898.png");
        try {
            String mergingCostCriterion = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION;
            String regionMergingCriterion = GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION; // => fast segmentation
            int totalIterationsForSecondSegmentation = 60;
            float threshold = 1000.0f;
            float spectralWeight = 0.5f;
            float shapeWeight = 0.3f;

            GenericRegionMergingOp operator = executeOperator(sourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                              totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

            AbstractSegmenter segmenter = operator.getSegmenter();
            assertNotNull(segmenter);

            Graph graph = segmenter.getGraph();
            assertNotNull(graph);
            assertEquals(4, graph.getNodeCount());

            List<Node> nodes = findNodesByAreaAndPerimeter(graph, 244584, 10472);
            assertEquals(1, nodes.size());
            float[] nodeExpectedMeansValues = new float[]{55.079082f, 104.811226f, 85.818596f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 0, 0, 750, 898, 6592, 3);

            nodes = findNodesByAreaAndPerimeter(graph, 168075, 2706);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{28.557613f, 78.30157f, 106.95757f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 74, 48, 601, 301, 5412, 1);

            nodes = findNodesByAreaAndPerimeter(graph, 225567, 3040);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{29.72f, 77.462265f, 107.269226f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 101, 436, 581, 426, 6080, 1);

            nodes = findNodesByAreaAndPerimeter(graph, 35274, 1430);
            assertEquals(1, nodes.size());
            nodeExpectedMeansValues = new float[]{5.579265f, 209.00505f, 82.32177f};
            checkGraphNode(nodes.get(0), nodeExpectedMeansValues, 81, 353, 586, 70, 2860, 1);

            Product targetProduct = operator.getTargetProduct();
            checkTargetBandForFastBaatzSchapeSegmenter(targetProduct, 1, 4);
        } finally {
            sourceProduct.dispose();
        }
    }

    private Product readSourceProduct(String imageFileName) throws IOException, URISyntaxException {
        URL resource = getClass().getResource(imageFileName);
        assertNotNull(resource);
        File productFile = new File(resource.toURI());
        if (!productFile.exists()) {
            fail("The test file '"+productFile.getAbsolutePath()+"' representing the input product does not exist.");
        }
        ImageProductReaderPlugIn readerPlugIn = new ImageProductReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();
        return reader.readProductNodes(productFile, null);
    }

    private static List<Node> findNodesByAreaAndPerimeter(Graph graph, int nodeArea, int nodePerimeter) {
        List<Node> nodes = new ArrayList<>();
        for (int i=0; i<graph.getNodeCount(); i++) {
            Node node = graph.getNodeAt(i);
            if (node.getArea() == nodeArea && node.getPerimeter() == nodePerimeter) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    private static GenericRegionMergingOp executeOperator(Product sourceProduct, String mergingCostCriterion, String regionMergingCriterion,
                                                          int totalIterationsForSecondSegmentation, float threshold, Float spectralWeight, Float shapeWeight)
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
        assertNotNull(targetProduct);
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

    private static void checkTargetBandForSpringSegmenter(Product targetProduct, int minimumExpectedValue, int maximumExpectedValue) {
        Band band = checkTargetBand(targetProduct);
        assertValueInsideInterval(band.getSampleInt(64, 84), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(164, 184), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(264, 114), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(14, 18), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(123, 321), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(200, 100), minimumExpectedValue, maximumExpectedValue);
    }

    private static void checkTargetBandForFullLamdaScheduleSegmenter(Product targetProduct, int minimumExpectedValue, int maximumExpectedValue) {
        Band band = checkTargetBand(targetProduct);
        assertValueInsideInterval(band.getSampleInt(64, 84), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(164, 184), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(264, 114), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(14, 18), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(123, 321), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(200, 100), minimumExpectedValue, maximumExpectedValue);
    }

    private static void checkTargetBandForBaatzSchapeSmallSegmenter(Product targetProduct, int minimumExpectedValue, int maximumExpectedValue) {
        Band band = checkTargetBand(targetProduct);
        assertValueInsideInterval(band.getSampleInt(64, 84), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(164, 184), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(264, 114), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(14, 18), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(123, 321), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(200, 100), minimumExpectedValue, maximumExpectedValue);
    }

    private static void checkTargetBandForBaatzSchapeSegmenter(Product targetProduct, int minimumExpectedValue, int maximumExpectedValue) {
        Band band = checkTargetBand(targetProduct);
        assertValueInsideInterval(band.getSampleInt(64, 84), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(164, 184), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(264, 114), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(14, 18), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(123, 321), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(200, 100), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(332, 178), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(614, 400), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(240, 510), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(340, 410), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(164, 121), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(700, 868), minimumExpectedValue, maximumExpectedValue);
    }

    private static void checkTargetBandForFastBaatzSchapeSegmenter(Product targetProduct, int minimumExpectedValue, int maximumExpectedValue) {
        Band band = checkTargetBand(targetProduct);
        assertValueInsideInterval(band.getSampleInt(64, 84), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(164, 184), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(264, 114), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(14, 18), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(123, 321), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(200, 100), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(332, 178), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(614, 400), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(240, 510), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(340, 410), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(164, 121), minimumExpectedValue, maximumExpectedValue);
        assertValueInsideInterval(band.getSampleInt(700, 868), minimumExpectedValue, maximumExpectedValue);
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

    private static void assertValueInsideInterval(int actualValue, int minimumExpectedValue, int maximumExpectedValue) {
        if (actualValue < minimumExpectedValue || actualValue > maximumExpectedValue) {
            fail("The actual value " + actualValue + " is outside interval " + minimumExpectedValue+" - " + maximumExpectedValue + ".");
        }
    }
}
