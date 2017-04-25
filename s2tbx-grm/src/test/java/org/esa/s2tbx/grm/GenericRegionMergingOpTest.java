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
        FirstTileSegmentationGRMOp firstOperator = executeFirstOperatorForBaatzSchapeTileSegmenter();

        String operatorName = "SecondTileSegmentationGRMOp";

        Map<String, Object> parameters = buildOperatorParameters(firstOperator.getMergingCostCriterion(), firstOperator.getRegionMergingCriterion(),
                                                                 firstOperator.getTotalIterationsForSecondSegmentation(), firstOperator.getThreshold(),
                                                                 firstOperator.getSpectralWeight(), firstOperator.getShapeWeight());
        parameters.put("temporaryFolder", firstOperator.getTemporaryFolder());
        parameters.put("startTime", firstOperator.getStartTime());

        Map<String, Product> sourceProducts = new HashMap<String, Product>(1);
        sourceProducts.put("source", firstOperator.getTargetProduct());

        SecondTileSegmentationGRMOp secondOperator = (SecondTileSegmentationGRMOp)GPF.getDefaultInstance().createOperator(operatorName, parameters, sourceProducts, null);
        Product targetProduct = secondOperator.getTargetProduct();
        AbstractSegmenter segmenter = secondOperator.getSegmenter();

        assertNotNull(segmenter);

        Graph graph = segmenter.getGraph();
        assertNotNull(graph);

        assertEquals(2, graph.getNodeCount());

        float[] firstNodeExpectedMeansValues = new float[] {39.001804f, 88.01934f, 98.79258f};

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

        assertEquals(1, firstNode.getEdgeCount());

        Band band = checkTargetBand(targetProduct);

        int bandValue = band.getSampleInt(64, 84);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(164, 184);
        assertEquals(2, bandValue);

        bandValue = band.getSampleInt(264, 114);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(14, 18);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(123, 321);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(200, 100);
        assertEquals(1, bandValue);
    }

    private FirstTileSegmentationGRMOp executeFirstOperatorForBaatzSchapeTileSegmenter() throws IOException {
        File file = this.segmentationTestsFolderPath.resolve("picture-334x400.png").toFile();

        ImageProductReaderPlugIn readerPlugIn = new ImageProductReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        Product sourceProduct = reader.readProductNodes(file, null);

        String mergingCostCriterion = FirstTileSegmentationGRMOp.BAATZ_SCHAPE_MERGING_COST_CRITERION;
        String regionMergingCriterion = FirstTileSegmentationGRMOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
        int totalIterationsForSecondSegmentation = 75;
        float threshold = 800.0f;
        float spectralWeight = 0.5f;
        float shapeWeight = 0.5f;
        String[] sourceBandNames = new String[]{"red", "blue", "green"};

        String operatorName = "FirstTileSegmentationGRMOp";

        Map<String, Object> firstOperatorParameters = buildOperatorParameters(mergingCostCriterion, regionMergingCriterion, totalIterationsForSecondSegmentation,
                                                                              threshold, spectralWeight, shapeWeight);
        firstOperatorParameters.put("sourceBandNames", sourceBandNames);

        Map<String, Product> sourceProducts = new HashMap<String, Product>(1);
        sourceProducts.put("source", sourceProduct);

        FirstTileSegmentationGRMOp execOp = (FirstTileSegmentationGRMOp)GPF.getDefaultInstance().createOperator(operatorName, firstOperatorParameters, sourceProducts, null);
        execOp.getTargetProduct();

        assertEquals(mergingCostCriterion, execOp.getMergingCostCriterion());
        assertEquals(regionMergingCriterion, execOp.getRegionMergingCriterion());
        assertEquals(totalIterationsForSecondSegmentation, execOp.getTotalIterationsForSecondSegmentation());
        assertEquals(threshold, execOp.getThreshold(), 0.0f);
        assertEquals(spectralWeight, execOp.getSpectralWeight(), 0.0f);
        assertEquals(shapeWeight, execOp.getShapeWeight(), 0.0f);
        assertEquals(sourceBandNames.length, execOp.getSourceBandNames().length);

        OperatorExecutor executor = OperatorExecutor.create(execOp);
        executor.execute(ProgressMonitor.NULL);

        return execOp;
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

    private Band checkTargetBand(Product targetProduct) {
        assertNotNull(targetProduct);

        Band targetBand = targetProduct.getBandAt(0);
        assertNotNull(targetBand);

        assertEquals(ProductData.TYPE_INT32, targetBand.getDataType());

        long size = targetProduct.getSceneRasterWidth() * targetProduct.getSceneRasterHeight();
        assertEquals(size, targetBand.getNumDataElems());

        return targetBand;
    }

    private static Map<String, Object> buildOperatorParameters(String mergingCostCriterion, String regionMergingCriterion, int totalIterationsForSecondSegmentation,
                                                               float threshold, float spectralWeight, float shapeWeight) {

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("mergingCostCriterion", mergingCostCriterion);
        parameters.put("regionMergingCriterion", regionMergingCriterion);
        parameters.put("totalIterationsForSecondSegmentation", totalIterationsForSecondSegmentation);
        parameters.put("threshold", threshold);
        parameters.put("spectralWeight", spectralWeight);
        parameters.put("shapeWeight", shapeWeight);
        return parameters;
    }
}
