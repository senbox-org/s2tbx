package org.esa.s2tbx.grm;

import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.Graph;
import org.esa.s2tbx.grm.segmentation.Node;
import org.esa.s2tbx.grm.segmentation.tiles.BaatzSchapeTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.FullLambdaScheduleTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.SpringTileSegmenter;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.rgb.ImageProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Jean Coravu.
 */
public class GenericRegionMergingOpTest {
    private Path segmentationTestsFolderPath;

    public GenericRegionMergingOpTest() {
    }

    @Before
    public void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());

        checkTestDirectoryExists();
    }

    @Test
    public void testBaatzSchapeTileSegmenter() throws IOException, IllegalAccessException {
        GenericRegionMergingOp operator = buildOperator(GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION,
                GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION,
                75, 2, 800, 0.5f, 0.5f);

        operator.initialize();

        Class<?> tileSegmenterClass = operator.getTileSegmenterClass();
        assertEquals(BaatzSchapeTileSegmenter.class, tileSegmenterClass);

        AbstractSegmenter segmenter = operator.runTileSegmentation();
        assertNotNull(segmenter);

        Graph graph = segmenter.getGraph();
        assertNotNull(graph);

        assertEquals(4, graph.getNodeCount());

        float[] firstNodeExpectedMeansValues = new float[] {56.123554f, 104.87113f, 84.69605f, 255.0f};

        Node firstNode = graph.getNodeAt(0);

        int length = firstNode.getNumberOfComponentsPerPixel();
        assertEquals(firstNodeExpectedMeansValues.length, length);
        for (int i=0; i<length; i++) {
            assertEquals(firstNodeExpectedMeansValues[i], firstNode.getMeansAt(i), 0.0f);
        }

        BoundingBox box = firstNode.getBox();
        assertEquals(0, box.getLeftX());
        assertEquals(0, box.getTopY());
        assertEquals(334, box.getWidth());
        assertEquals(400, box.getHeight());

        assertEquals(2936, firstNode.getContour().size());

        assertEquals(3, firstNode.getEdgeCount());
    }

    @Test
    public void testFullLambdaScheduleTileSegmenter() throws IOException, IllegalAccessException {
        GenericRegionMergingOp operator = buildOperator(GenericRegionMergingOp.FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION,
                GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION,
                75, 2, 50000, 0.0f, 0.0f);

        operator.initialize();

        Class<?> tileSegmenterClass = operator.getTileSegmenterClass();
        assertEquals(FullLambdaScheduleTileSegmenter.class, tileSegmenterClass);

        AbstractSegmenter segmenter = operator.runTileSegmentation();
        assertNotNull(segmenter);

        Graph graph = segmenter.getGraph();
        assertNotNull(graph);

        assertEquals(21, graph.getNodeCount());

        float[] firstNodeExpectedMeansValues = new float[] {43.48049f, 92.38074f, 71.088326f, 255.0f};

        Node firstNode = graph.getNodeAt(0);

        int length = firstNode.getNumberOfComponentsPerPixel();
        assertEquals(firstNodeExpectedMeansValues.length, length);
        for (int i=0; i<length; i++) {
            assertEquals(firstNodeExpectedMeansValues[i], firstNode.getMeansAt(i), 0.0f);
        }

        BoundingBox box = firstNode.getBox();
        assertEquals(0, box.getLeftX());
        assertEquals(0, box.getTopY());
        assertEquals(287, box.getWidth());
        assertEquals(39, box.getHeight());

        assertEquals(1596, firstNode.getContour().size());

        assertEquals(7, firstNode.getEdgeCount());
    }

    @Test
    public void testSpringTileSegmenter() throws IOException, IllegalAccessException {
        GenericRegionMergingOp operator = buildOperator(GenericRegionMergingOp.SPRING_MERGING_COST_CRITERION,
                GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION,
                75, 2, 1000, 0.0f, 0.0f);

        operator.initialize();

        Class<?> tileSegmenterClass = operator.getTileSegmenterClass();
        assertEquals(SpringTileSegmenter.class, tileSegmenterClass);

        AbstractSegmenter segmenter = operator.runTileSegmentation();
        assertNotNull(segmenter);

        Graph graph = segmenter.getGraph();
        assertNotNull(graph);

        assertEquals(8354, graph.getNodeCount());

        float[] firstNodeExpectedMeansValues = new float[] {45.5f, 83.5f, 63.5f, 255.0f};

        Node firstNode = graph.getNodeAt(0);

        int length = firstNode.getNumberOfComponentsPerPixel();
        assertEquals(firstNodeExpectedMeansValues.length, length);
        for (int i=0; i<length; i++) {
            assertEquals(firstNodeExpectedMeansValues[i], firstNode.getMeansAt(i), 0.0f);
        }

        BoundingBox box = firstNode.getBox();
        assertEquals(0, box.getLeftX());
        assertEquals(0, box.getTopY());
        assertEquals(1, box.getWidth());
        assertEquals(2, box.getHeight());

        assertEquals(12, firstNode.getContour().size());

        assertEquals(1, firstNode.getEdgeCount());
    }

    private GenericRegionMergingOp buildOperator(String mergingCostCriterion, String regionMergingCriterion, int totalIterationsForSecondSegmentation,
                                                 int iterationsForEachFirstSegmentation, int threshold, float spectralWeight, float shapeWeight)
                                                 throws IOException {

        File file = this.segmentationTestsFolderPath.resolve("picture-334x400.png").toFile();

        ImageProductReaderPlugIn readerPlugIn = new ImageProductReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        Product sourceProduct = reader.readProductNodes(file, null);

        Map<String, Object> annotatedFields = new HashMap<String, Object>();
        annotatedFields.put("mergingCostCriterion", mergingCostCriterion);
        annotatedFields.put("regionMergingCriterion", regionMergingCriterion);
        annotatedFields.put("totalIterationsForSecondSegmentation", totalIterationsForSecondSegmentation);
        annotatedFields.put("iterationsForEachFirstSegmentation", iterationsForEachFirstSegmentation);
        annotatedFields.put("threshold", threshold);
        annotatedFields.put("spectralWeight",spectralWeight);
        annotatedFields.put("shapeWeight", shapeWeight);
        annotatedFields.put("sourceBandNames", new String[]{"red", "blue", "green", "alpha"});

        return buildOperator(sourceProduct, annotatedFields);
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

    private static GenericRegionMergingOp buildOperator(Product sourceProduct, Map<String, Object> annotatedFields) {
        GenericRegionMergingOp operator = new GenericRegionMergingOp();
        try {
            for (Map.Entry<String, Object> entry : annotatedFields.entrySet()) {
                Field field = operator.getClass().getDeclaredField(entry.getKey());
                field.setAccessible(true);
                if (field.isAnnotationPresent(Parameter.class)) {
                    field.set(operator, entry.getValue());
                }
            }

            Field field = operator.getClass().getDeclaredField("sourceProduct");
            field.setAccessible(true);
            if (field.isAnnotationPresent(SourceProduct.class)) {
                field.set(operator, sourceProduct);
            }
        } catch (Exception e) {
            throw new OperatorException("Failed to set the operator parameter values.", e);
        }
        return operator;
    }
}
