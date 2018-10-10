package org.esa.s2tbx.grm;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.grm.segmentation.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.internal.TileImpl;
import org.junit.Test;

import javax.media.jai.JAI;

import java.awt.*;
import java.lang.ref.WeakReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Jean Coravu.
 */
public class SegmenterTest {

    public SegmenterTest() {
    }

    @Test
    public void testGenerateFourNeighborhood() {
        int[] neighborhood = new int[4];
        AbstractSegmenter.generateFourNeighborhood(neighborhood, 6050, 100, 100);
        assertEquals(5950, neighborhood[0]);
        assertEquals(6051, neighborhood[1]);
        assertEquals(6150, neighborhood[2]);
        assertEquals(6049, neighborhood[3]);
    }

    @Test
    public void testGenerateEightNeighborhood() {
        int[] neighborhood = new int[8];
        AbstractSegmenter.generateEightNeighborhood(neighborhood, 5050, 100, 100);
        assertEquals(4950, neighborhood[0]);
        assertEquals(4951, neighborhood[1]);
        assertEquals(5051, neighborhood[2]);
        assertEquals(5151, neighborhood[3]);
        assertEquals(5150, neighborhood[4]);
        assertEquals(5149, neighborhood[5]);
        assertEquals(5049, neighborhood[6]);
        assertEquals(4949, neighborhood[7]);
    }

    @Test
    public void testMergeBoundingBoxes() {
        BoundingBox box1 = new BoundingBox(0, 0, 100, 100);
        BoundingBox box2 = new BoundingBox(50, 50, 150, 120);
        BoundingBox mergedBox = AbstractSegmenter.mergeBoundingBoxes(box1, box2);
        assertEquals(0, mergedBox.getLeftX());
        assertEquals(0, mergedBox.getTopY());
        assertEquals(200, mergedBox.getWidth());
        assertEquals(170, mergedBox.getHeight());
    }

    @Test
    public void testFullLambdaScheduleSegmenter() {
        Product sourceProduct = buildSourceProduct();
        boolean fastSegmentation = false;
        boolean addFourNeighbors = true;
        AbstractSegmenter segmenter = runFullLambdaScheduleSegmenter(sourceProduct, fastSegmentation, addFourNeighbors);
        Graph graph = segmenter.getGraph();

        assertNotNull(graph);

        assertEquals(6, graph.getNodeCount());

        Node node = graph.getNodeAt(0);
        float[] nodeExpectedMeansValues = new float[] {50f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 20, 30, 200, 5);

        node = graph.getNodeAt(1);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 5, 18, 1, 76, 1);

        node = graph.getNodeAt(2);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 10, 18, 1, 76, 1);

        node = graph.getNodeAt(3);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 15, 18, 1, 76, 1);

        node = graph.getNodeAt(4);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 20, 18, 1, 76, 1);

        node = graph.getNodeAt(5);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 25, 18, 1, 76, 1);
    }

    @Test
    public void testFastFullLambdaScheduleSegmenter() {
        Product sourceProduct = buildSourceProduct();
        boolean fastSegmentation = true;
        boolean addFourNeighbors = false;
        AbstractSegmenter segmenter = runFullLambdaScheduleSegmenter(sourceProduct, fastSegmentation, addFourNeighbors);
        Graph graph = segmenter.getGraph();

        assertNotNull(graph);

        assertEquals(6, graph.getNodeCount());

        Node node = graph.getNodeAt(0);
        float[] nodeExpectedMeansValues = new float[] {50f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 20, 30, 200, 5);

        node = graph.getNodeAt(1);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 5, 18, 1, 76, 1);

        node = graph.getNodeAt(2);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 10, 18, 1, 76, 1);

        node = graph.getNodeAt(3);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 15, 18, 1, 76, 1);

        node = graph.getNodeAt(4);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 20, 18, 1, 76, 1);

        node = graph.getNodeAt(5);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 25, 18, 1, 76, 1);
    }

    @Test
    public void testSpringSegmenter() {
        Product sourceProduct = buildSourceProduct();
        int numberOfIterations = 508;
        float threshold = 1000.0f;
        boolean fastSegmentation = false;
        boolean addFourNeighbors = true;
        AbstractSegmenter segmenter = runSpringSegmenter(sourceProduct, fastSegmentation, addFourNeighbors, numberOfIterations, threshold);
        Graph graph = segmenter.getGraph();

        assertNotNull(graph);

        assertEquals(7, graph.getNodeCount());

        Node node = graph.getNodeAt(0);
        float[] nodeExpectedMeansValues = new float[] {50f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 20, 30, 200, 6);

        node = graph.getNodeAt(1);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 5, 18, 1, 76, 1);

        node = graph.getNodeAt(2);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 10, 18, 1, 76, 1);

        node = graph.getNodeAt(3);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 15, 18, 1, 76, 1);

        node = graph.getNodeAt(4);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 20, 18, 1, 76, 1);

        node = graph.getNodeAt(5);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 25, 18, 1, 76, 1);

        node = graph.getNodeAt(6);
        nodeExpectedMeansValues = new float[] {50f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 19, 29, 1, 1, 8, 1);
    }

    @Test
    public void testFastSpringSegmenter() {
        Product sourceProduct = buildSourceProduct();
        int numberOfIterations = 408;
        float threshold = 100.0f;
        boolean fastSegmentation = true;
        boolean addFourNeighbors = false;
        AbstractSegmenter segmenter = runSpringSegmenter(sourceProduct, fastSegmentation, addFourNeighbors, numberOfIterations, threshold);
        Graph graph = segmenter.getGraph();

        assertNotNull(graph);

        assertEquals(6, graph.getNodeCount());

        Node node = graph.getNodeAt(0);
        float[] nodeExpectedMeansValues = new float[] {50f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 20, 30, 200, 5);

        node = graph.getNodeAt(1);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 5, 18, 1, 76, 1);

        node = graph.getNodeAt(2);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 10, 18, 1, 76, 1);

        node = graph.getNodeAt(3);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 15, 18, 1, 76, 1);

        node = graph.getNodeAt(4);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 20, 18, 1, 76, 1);

        node = graph.getNodeAt(5);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 25, 18, 1, 76, 1);
    }

    @Test
    public void testBaatzSchapeSegmenter() {
        Product sourceProduct = buildSourceProduct();
        boolean fastSegmentation = false;
        boolean addFourNeighbors = true;
        AbstractSegmenter segmenter = runBaatzSchapeSegmenter(sourceProduct, fastSegmentation, addFourNeighbors);
        Graph graph = segmenter.getGraph();

        assertNotNull(graph);

        assertEquals(6, graph.getNodeCount());

        Node node = graph.getNodeAt(0);
        float[] nodeExpectedMeansValues = new float[] {50f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 20, 30, 200, 5);

        node = graph.getNodeAt(1);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 5, 18, 1, 76, 1);

        node = graph.getNodeAt(2);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 10, 18, 1, 76, 1);

        node = graph.getNodeAt(3);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 15, 18, 1, 76, 1);

        node = graph.getNodeAt(4);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 20, 18, 1, 76, 1);

        node = graph.getNodeAt(5);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 25, 18, 1, 76, 1);

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        OutputMaskMatrixHelper outputMaskMatrixHelper = segmenter.buildOutputMaskMatrixHelper();
        OutputMarkerMatrixHelper outputMarkerMatrix = outputMaskMatrixHelper.buildMaskMatrix();
        ProductData data = outputMarkerMatrix.buildOutputProductData();
        targetBand.setData(data);

        int bandValue = targetBand.getSampleInt(12, 14);
        assertEquals(1, bandValue);

        bandValue = targetBand.getSampleInt(16, 18);
        assertEquals(1, bandValue);

        bandValue = targetBand.getSampleInt(19, 29);
        assertEquals(1, bandValue);

        bandValue = targetBand.getSampleInt(13, 5);
        assertEquals(2, bandValue);

        bandValue = targetBand.getSampleInt(5, 10);
        assertEquals(3, bandValue);

        bandValue = targetBand.getSampleInt(2, 15);
        assertEquals(4, bandValue);

        bandValue = targetBand.getSampleInt(17, 20);
        assertEquals(5, bandValue);

        bandValue = targetBand.getSampleInt(3, 25);
        assertEquals(6, bandValue);

        bandValue = targetBand.getSampleInt(19, 29);
        assertEquals(1, bandValue);

        bandValue = targetBand.getSampleInt(19, 29);
        assertEquals(1, bandValue);
    }

    @Test
    public void testFastBaatzSchapeSegmenter() {
        Product sourceProduct = buildSourceProduct();
        boolean fastSegmentation = true;
        boolean addFourNeighbors = false;
        AbstractSegmenter segmenter = runBaatzSchapeSegmenter(sourceProduct, fastSegmentation, addFourNeighbors);
        Graph graph = segmenter.getGraph();

        assertNotNull(graph);

        assertEquals(6, graph.getNodeCount());

        Node node = graph.getNodeAt(0);
        float[] nodeExpectedMeansValues = new float[] {50f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 0, 0, 20, 30, 200, 5);

        node = graph.getNodeAt(1);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 5, 18, 1, 76, 1);

        node = graph.getNodeAt(2);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 10, 18, 1, 76, 1);

        node = graph.getNodeAt(3);
        nodeExpectedMeansValues = new float[] {122f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 15, 18, 4, 88, 1);

        node = graph.getNodeAt(4);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 20, 18, 1, 76, 1);

        node = graph.getNodeAt(5);
        nodeExpectedMeansValues = new float[] {150f};
        GenericRegionMergingOpTest.checkGraphNode(node, nodeExpectedMeansValues, 1, 25, 18, 1, 76, 1);

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        OutputMaskMatrixHelper outputMaskMatrixHelper = segmenter.buildOutputMaskMatrixHelper();
        OutputMarkerMatrixHelper outputMarkerMatrix = outputMaskMatrixHelper.buildMaskMatrix();
        ProductData data = outputMarkerMatrix.buildOutputProductData();
        targetBand.setData(data);

        int bandValue = targetBand.getSampleInt(12, 14);
        assertEquals(1, bandValue);

        bandValue = targetBand.getSampleInt(16, 18);
        assertEquals(1, bandValue);

        bandValue = targetBand.getSampleInt(19, 29);
        assertEquals(1, bandValue);

        bandValue = targetBand.getSampleInt(13, 5);
        assertEquals(2, bandValue);

        bandValue = targetBand.getSampleInt(5, 10);
        assertEquals(3, bandValue);

        bandValue = targetBand.getSampleInt(2, 15);
        assertEquals(4, bandValue);

        bandValue = targetBand.getSampleInt(17, 20);
        assertEquals(5, bandValue);

        bandValue = targetBand.getSampleInt(3, 25);
        assertEquals(6, bandValue);

        bandValue = targetBand.getSampleInt(19, 29);
        assertEquals(1, bandValue);

        bandValue = targetBand.getSampleInt(19, 29);
        assertEquals(1, bandValue);
    }

    private static Product buildSourceProduct() {
        int sceneRasterWidth = 20;
        int sceneRasterHeight = 30;
        Product product = new Product("tempProduct", "Test", sceneRasterWidth, sceneRasterHeight);

        product.setPreferredTileSize(JAI.getDefaultTileSize());
        Band band = product.addBand("band_1", ProductData.TYPE_INT16);

        ProductData data = band.createCompatibleRasterData();
        int index = 0;
        for (int y=0; y<sceneRasterHeight;y++) {
            for (int x=0; x<sceneRasterWidth;x++) {
                int value = 50;
                if ((x > 0 && x < sceneRasterWidth-1) && (y > 0 && y < sceneRasterHeight-1 && y % 5 == 0)) {
                    value = 150;
                }
                data.setElemIntAt(index++, value);
            }
        }
        band.setData(data);

        return product;
    }

    private static AbstractSegmenter runBaatzSchapeSegmenter(Product sourceProduct, boolean fastSegmentation, boolean addFourNeighbors) {
        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();

        int numberOfIterations = 50;
        float spectralWeight = 0.5f;
        float shapeWeight = 0.5f;
        float threshold = 50.0f;
        BoundingBox rectange = new BoundingBox(0, 0, sceneWidth, sceneHeight);
        Band sourceBand = sourceProduct.getBandAt(0);
        Rectangle targetRectangle = new Rectangle(0, 0, sceneWidth, sceneHeight);
        Tile tile = new TileImpl(sourceBand, sourceBand.getSourceImage().getData(), targetRectangle, true);
        TileDataSource[] sourceTiles = new TileDataSource[] {new TileDataSourceImpl(tile)};

        BaatzSchapeSegmenter segmenter = new BaatzSchapeSegmenter(spectralWeight, shapeWeight, threshold);
        segmenter.update(sourceTiles, rectange, numberOfIterations, fastSegmentation, addFourNeighbors);

        return segmenter;
    }

    private static AbstractSegmenter runSpringSegmenter(Product sourceProduct, boolean fastSegmentation, boolean addFourNeighbors, int numberOfIterations, float threshold) {
        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();

        BoundingBox rectange = new BoundingBox(0, 0, sceneWidth, sceneHeight);
        Band sourceBand = sourceProduct.getBandAt(0);
        Rectangle targetRectangle = new Rectangle(0, 0, sceneWidth, sceneHeight);
        Tile tile = new TileImpl(sourceBand, sourceBand.getSourceImage().getData(), targetRectangle, true);
        TileDataSource[] sourceTiles = new TileDataSource[] {new TileDataSourceImpl(tile)};

        SpringSegmenter segmenter = new SpringSegmenter(threshold);
        segmenter.update(sourceTiles, rectange, numberOfIterations, fastSegmentation, addFourNeighbors);

        return segmenter;
    }

    private static AbstractSegmenter runFullLambdaScheduleSegmenter(Product sourceProduct, boolean fastSegmentation, boolean addFourNeighbors) {
        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();
        int numberOfIterations = 608;
        float threshold = 1100.0f;
        BoundingBox rectange = new BoundingBox(0, 0, sceneWidth, sceneHeight);
        Band sourceBand = sourceProduct.getBandAt(0);
        Rectangle targetRectangle = new Rectangle(0, 0, sceneWidth, sceneHeight);
        Tile tile = new TileImpl(sourceBand, sourceBand.getSourceImage().getData(), targetRectangle, true);
        TileDataSource[] sourceTiles = new TileDataSource[] {new TileDataSourceImpl(tile)};
        FullLambdaScheduleSegmenter segmenter = new FullLambdaScheduleSegmenter(threshold);
        segmenter.update(sourceTiles, rectange, numberOfIterations, fastSegmentation, addFourNeighbors);
        return segmenter;
    }
}
