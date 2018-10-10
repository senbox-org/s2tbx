package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.matrix.IntMatrix;
import org.junit.Test;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Jean Coravu
 */
public class MovingWindowTest extends AbstractOpTest {

    public MovingWindowTest() {
    }

    @Test
    public void testMovingWindow() throws Exception {
        Dimension tileSize = JAI.getDefaultTileSize();
        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();

        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        Path folder = this.forestCoverChangeTestsFolderPath.resolve("object-selection");

        File currentProductFile = folder.resolve("S2A_20160713T125925_A005524_T35UMP_grm.dim").toFile();
        Product segmentationProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile, null);

        assertNotNull(segmentationProduct);

        IntSet validRegions = buildValidRegions();

        ProductBandToMatrixConverter converter = new ProductBandToMatrixConverter(segmentationProduct, tileSize.width, tileSize.height);
        IntMatrix segmentationMatrix = converter.runTilesInParallel(threadCount, threadPool);

        assertNotNull(segmentationMatrix);

        ColorFillerTilesComputing tilesComputing = new ColorFillerTilesComputing(segmentationMatrix, validRegions, null, null, tileSize.width, tileSize.height);
        IntMatrix colorFillerMatrix = tilesComputing.runTilesInParallel(threadCount, threadPool);

        assertNotNull(colorFillerMatrix);

        assertEquals(549, colorFillerMatrix.getColumnCount());
        assertEquals(549, colorFillerMatrix.getRowCount());

        MovingWindow movingWindow = new MovingWindow(colorFillerMatrix);
        IntSet validSegmentIds = movingWindow.runTile(0, 0, 512, 512);

        assertNotNull(validSegmentIds);

        assertEquals(14, validSegmentIds.size());

        assertTrue(validSegmentIds.contains(293));
        assertTrue(validSegmentIds.contains(1200));
        assertTrue(validSegmentIds.contains(800));
        assertTrue(validSegmentIds.contains(402));
        assertTrue(validSegmentIds.contains(1));
        assertTrue(validSegmentIds.contains(12));
        assertTrue(validSegmentIds.contains(10));
        assertTrue(validSegmentIds.contains(34));
        assertTrue(validSegmentIds.contains(117));
        assertTrue(validSegmentIds.contains(45));
        assertTrue(validSegmentIds.contains(63));
        assertTrue(validSegmentIds.contains(1005));
        assertTrue(validSegmentIds.contains(53));
        assertTrue(validSegmentIds.contains(20));
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
