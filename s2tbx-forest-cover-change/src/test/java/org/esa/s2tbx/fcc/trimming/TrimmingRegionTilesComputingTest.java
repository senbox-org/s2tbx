package org.esa.s2tbx.fcc.trimming;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by jcoravu on 24/7/2017.
 */
public class TrimmingRegionTilesComputingTest extends AbstractOpTest {

    public TrimmingRegionTilesComputingTest() {
    }

    @Test
    public void testTrimmingRegionTilesComputing() throws Exception {
        Dimension tileSize = JAI.getDefaultTileSize();
        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();

        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        Path folder = this.forestCoverChangeTestsFolderPath.resolve("trimming");

        File segmentationSourceProductFile = folder.resolve("productColorFill.dim").toFile();
        Product segmentationSourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(segmentationSourceProductFile, null);

        File sourceProductFile = folder.resolve("sourceProduct.dim").toFile();
        Product sourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(sourceProductFile, null);

        int[] sourceBandIndices = new int[] {0, 1, 2};

        ProductBandToMatrixConverter converter = new ProductBandToMatrixConverter(segmentationSourceProduct, tileSize.width, tileSize.height);
        IntMatrix segmentationMatrix = converter.runTilesInParallel(threadCount, threadPool);
        double degreesOfFreedom  = 2.8;
        TrimmingRegionTilesComputing trimming = new TrimmingRegionTilesComputing(segmentationMatrix, sourceProduct, sourceBandIndices, tileSize.width, tileSize.height, degreesOfFreedom);
        IntSet trimmingSet = trimming.runTilesInParallel(threadCount, threadPool);

        assertNotNull(trimmingSet);

        assertTrue(trimmingSet.contains(410));
        assertTrue(trimmingSet.contains(899));
        assertTrue(trimmingSet.contains(820));
        assertTrue(trimmingSet.contains(402));
        assertTrue(trimmingSet.contains(387));
        assertTrue(trimmingSet.contains(962));
        assertTrue(trimmingSet.contains(176));
        assertTrue(trimmingSet.contains(1419));
        assertTrue(trimmingSet.contains(890));
        assertTrue(trimmingSet.contains(1098));
        assertTrue(trimmingSet.contains(429));
        assertTrue(trimmingSet.contains(985));
        assertTrue(trimmingSet.contains(886));
        assertTrue(trimmingSet.contains(861));
        assertTrue(trimmingSet.contains(412));
        assertTrue(trimmingSet.contains(1289));
        assertTrue(trimmingSet.contains(397));
        assertTrue(trimmingSet.contains(881));
        assertTrue(trimmingSet.contains(1436));
        assertTrue(trimmingSet.contains(1092));
        assertTrue(trimmingSet.contains(373));
        assertTrue(trimmingSet.contains(542));
        assertTrue(trimmingSet.contains(1214));
        assertTrue(trimmingSet.contains(625));
        assertTrue(trimmingSet.contains(600));
        assertTrue(trimmingSet.contains(411));
        assertTrue(trimmingSet.contains(1111));
        assertTrue(trimmingSet.contains(1464));
        assertTrue(trimmingSet.contains(845));
        assertTrue(trimmingSet.contains(1413));
        assertTrue(trimmingSet.contains(391));
        assertTrue(trimmingSet.contains(1075));
        assertTrue(trimmingSet.contains(382));
        assertTrue(trimmingSet.contains(1285));
        assertTrue(trimmingSet.contains(1277));
        assertTrue(trimmingSet.contains(862));
    }
}
