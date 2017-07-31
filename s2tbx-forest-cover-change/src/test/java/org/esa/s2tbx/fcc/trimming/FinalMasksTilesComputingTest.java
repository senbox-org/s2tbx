package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
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

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public class FinalMasksTilesComputingTest extends AbstractOpTest {

    public FinalMasksTilesComputingTest() {
    }

    @Test
    public void testFinalMasksTilesComputing() throws Exception {
        Dimension tileSize = JAI.getDefaultTileSize();
        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();

        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        Path finalFolder = this.forestCoverChangeTestsFolderPath.resolve("final-mask");

        File differenceSegmentationProductFile = finalFolder.resolve("S2A_R093_T35UMP_20170628T092026_grm.dim").toFile();
        Product differenceSegmentationProduct = productReaderPlugIn.createReaderInstance().readProductNodes(differenceSegmentationProductFile, null);

        ProductBandToMatrixConverter converter1 = new ProductBandToMatrixConverter(differenceSegmentationProduct, tileSize.width, tileSize.height);
        IntMatrix differecenSegmentationMatrix = converter1.runTilesInParallel(threadCount, threadPool);

        File unionMaskProductFile = finalFolder.resolve("S2A_R093_T35UMP_20170628T092026_grm_fill_union.dim").toFile();
        Product unionMaskProduct = productReaderPlugIn.createReaderInstance().readProductNodes(unionMaskProductFile, null);

        ProductBandToMatrixConverter converter2 = new ProductBandToMatrixConverter(unionMaskProduct, tileSize.width, tileSize.height);
        IntMatrix unionMaskMatrix = converter2.runTilesInParallel(threadCount, threadPool);

        IntSet differenceTrimmingSet = buildDifferenceTrimmingSet();

        FinalMasksTilesComputing tilesComputing = new FinalMasksTilesComputing(differecenSegmentationMatrix, unionMaskMatrix, differenceTrimmingSet, tileSize.width, tileSize.height);
        ProductData productData = tilesComputing.runTilesInParallel(threadCount, threadPool);

        assertNotNull(productData);

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, differenceSegmentationProduct.getSceneRasterWidth(), differenceSegmentationProduct.getSceneRasterHeight());
        targetBand.setData(productData);
        // reset the source image of the target product
        targetBand.setSourceImage(null);
        targetBand.getSourceImage();

        assertEquals(0, targetBand.getSampleInt(64, 84));
        assertEquals(0, targetBand.getSampleInt(143, 160));
        assertEquals(1, targetBand.getSampleInt(48, 241));
        assertEquals(1, targetBand.getSampleInt(235, 152));
        assertEquals(0, targetBand.getSampleInt(214, 274));
        assertEquals(1, targetBand.getSampleInt(253, 474));
        assertEquals(1, targetBand.getSampleInt(21, 505));
        assertEquals(1, targetBand.getSampleInt(36, 437));
        assertEquals(0, targetBand.getSampleInt(510, 53));
        assertEquals(1, targetBand.getSampleInt(210, 380));
        assertEquals(0, targetBand.getSampleInt(146, 277));
        assertEquals(1, targetBand.getSampleInt(467, 286));
    }

    private static IntSet buildDifferenceTrimmingSet() {
        IntSet differenceTrimmingSet = new IntOpenHashSet();
        differenceTrimmingSet.add(1063);
        differenceTrimmingSet.add(1034);
        differenceTrimmingSet.add(967);
        differenceTrimmingSet.add(790);
        differenceTrimmingSet.add(1058);
        differenceTrimmingSet.add(500);
        differenceTrimmingSet.add(568);
        differenceTrimmingSet.add(471);
        differenceTrimmingSet.add(1432);
        differenceTrimmingSet.add(415);
        differenceTrimmingSet.add(399);
        differenceTrimmingSet.add(662);
        differenceTrimmingSet.add(1124);
        differenceTrimmingSet.add(1254);
        differenceTrimmingSet.add(985);
        differenceTrimmingSet.add(1067);
        differenceTrimmingSet.add(1392);
        differenceTrimmingSet.add(1164);
        differenceTrimmingSet.add(1135);
        differenceTrimmingSet.add(1051);
        differenceTrimmingSet.add(1356);
        differenceTrimmingSet.add(1209);
        differenceTrimmingSet.add(421);
        differenceTrimmingSet.add(1087);
        differenceTrimmingSet.add(1354);
        differenceTrimmingSet.add(1100);
        differenceTrimmingSet.add(1147);
        differenceTrimmingSet.add(704);
        differenceTrimmingSet.add(452);
        differenceTrimmingSet.add(428);
        differenceTrimmingSet.add(463);
        differenceTrimmingSet.add(1434);
        differenceTrimmingSet.add(337);
        differenceTrimmingSet.add(936);
        differenceTrimmingSet.add(368);
        differenceTrimmingSet.add(1322);
        differenceTrimmingSet.add(1201);
        differenceTrimmingSet.add(869);
        differenceTrimmingSet.add(186);
        differenceTrimmingSet.add(639);
        differenceTrimmingSet.add(1181);
        return differenceTrimmingSet;
    }
}
