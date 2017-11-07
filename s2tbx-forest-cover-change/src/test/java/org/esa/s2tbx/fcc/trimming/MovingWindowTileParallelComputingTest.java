package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.grm.segmentation.product.WriteProductBandsTilesComputing;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.utils.matrix.IntMatrix;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Jean Coravu
 */
public class MovingWindowTileParallelComputingTest extends AbstractOpTest {
    private static Path testsFolderPath;

    public MovingWindowTileParallelComputingTest() {
    }

    @BeforeClass
    public static void oneTimeSetUp() throws IOException {
        testsFolderPath = Files.createTempDirectory("_temp");
        if (!Files.exists(testsFolderPath)) {
            fail("The test directory path '"+testsFolderPath+"' is not valid.");
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        if (!FileUtils.deleteTree(testsFolderPath.toFile())) {
            fail("Unable to delete test directory.");
        }
    }

    @Test
    public void testMovingWindowTileParallelComputing() throws Exception {
        Dimension tileSize = JAI.getDefaultTileSize();
        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();

        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        File currentProductFile = this.forestCoverChangeTestsFolderPath.resolve("S2A_R093_T35UMP_20170628T092026.dim").toFile();
        Product currentSourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile, null);

        assertNotNull(currentSourceProduct);

        Path folder = this.forestCoverChangeTestsFolderPath.resolve("object-selection");
        File currentProductFile1 = folder.resolve("S2A_20160713T125925_A005524_T35UMP_grm.dim").toFile();
        Product segmentationProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile1, null);

        assertNotNull(segmentationProduct);

        IntSet validRegions = buildValidRegions();

        ProductBandToMatrixConverter converter = new ProductBandToMatrixConverter(segmentationProduct, tileSize.width, tileSize.height);
        IntMatrix segmentationMatrix = converter.runTilesInParallel(threadCount, threadPool);

        assertNotNull(segmentationMatrix);

        ColorFillerTilesComputing tilesComputing = new ColorFillerTilesComputing(segmentationMatrix, validRegions, null, null, tileSize.width, tileSize.height);
        IntMatrix colorFillerMatrix = tilesComputing.runTilesInParallel(threadCount, threadPool);

        assertNotNull(colorFillerMatrix);

        String[] sourceBandNames = {"B4", "B8", "B11", "B12"};
        int[] sourceBandIndices = {0, 1, 2};
        WriteProductBandsTilesComputing bandsTilesComputing = new WriteProductBandsTilesComputing(currentSourceProduct, sourceBandNames, tileSize.width, tileSize.height, testsFolderPath);
        Path temporaryOutputFolder = bandsTilesComputing.runTilesInParallel(threadCount, threadPool);
        Dimension movingWindowSize = tileSize;
        Dimension movingStepSize = new Dimension(tileSize.width/2, tileSize.height/2);
        double degreesOfFreedom = 2.8d;

        MovingWindowTileParallelComputing movingWindowTiles = new MovingWindowTileParallelComputing(colorFillerMatrix, movingWindowSize, movingStepSize, tileSize,
                                                                                temporaryOutputFolder, sourceBandIndices, degreesOfFreedom);
        IntSet majorityVotingSegmentIds = movingWindowTiles.runTilesInParallel(threadCount, threadPool);

        assertNotNull(majorityVotingSegmentIds);

        assertEquals(13, majorityVotingSegmentIds.size());

        assertTrue(majorityVotingSegmentIds.contains(293));
        assertTrue(majorityVotingSegmentIds.contains(1200));
        assertTrue(majorityVotingSegmentIds.contains(402));
        assertTrue(majorityVotingSegmentIds.contains(1));
        assertTrue(majorityVotingSegmentIds.contains(12));
        assertTrue(majorityVotingSegmentIds.contains(10));
        assertTrue(majorityVotingSegmentIds.contains(34));
        assertTrue(majorityVotingSegmentIds.contains(117));
        assertTrue(majorityVotingSegmentIds.contains(45));
        assertTrue(majorityVotingSegmentIds.contains(63));
        assertTrue(majorityVotingSegmentIds.contains(53));
        assertTrue(majorityVotingSegmentIds.contains(1005));
        assertTrue(majorityVotingSegmentIds.contains(20));
    }

    private static IntSet buildValidRegions() {
        IntSet validRegions = new IntOpenHashSet();
        validRegions.add(1);
        validRegions.add(10);
        validRegions.add(12);
        validRegions.add(20);
        validRegions.add(34);
        validRegions.add(45);
        validRegions.add(53);
        validRegions.add(63);
        validRegions.add(117);
        validRegions.add(293);
        validRegions.add(402);
        validRegions.add(800);
        validRegions.add(1005);
        validRegions.add(1200);
        return validRegions;
    }
}
