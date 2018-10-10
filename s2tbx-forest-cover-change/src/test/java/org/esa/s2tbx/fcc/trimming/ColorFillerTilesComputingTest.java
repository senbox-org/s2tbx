package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.matrix.IntMatrix;
import org.junit.Test;

import javax.media.jai.JAI;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public class ColorFillerTilesComputingTest extends AbstractOpTest {

    public ColorFillerTilesComputingTest() {
    }

    @Test
    public void testColorFillerTileComputing() throws Exception {
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

        assertEquals(1, colorFillerMatrix.getValueAt(3, 5));
        assertEquals(10, colorFillerMatrix.getValueAt(6, 133));
        assertEquals(12, colorFillerMatrix.getValueAt(6, 167));

        assertEquals(20, colorFillerMatrix.getValueAt(3, 305));
        assertEquals(34, colorFillerMatrix.getValueAt(12, 239));
        assertEquals(45, colorFillerMatrix.getValueAt(19, 415));
        assertEquals(53, colorFillerMatrix.getValueAt(14, 406));
        assertEquals(63, colorFillerMatrix.getValueAt(31, 441));
        assertEquals(117, colorFillerMatrix.getValueAt(41, 440));
        assertEquals(293, colorFillerMatrix.getValueAt(123, 88));
        assertEquals(402, colorFillerMatrix.getValueAt(161, 414));
        assertEquals(800, colorFillerMatrix.getValueAt(305, 231));
        assertEquals(1005, colorFillerMatrix.getValueAt(384, 399));
        assertEquals(1200, colorFillerMatrix.getValueAt(454, 93));
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
