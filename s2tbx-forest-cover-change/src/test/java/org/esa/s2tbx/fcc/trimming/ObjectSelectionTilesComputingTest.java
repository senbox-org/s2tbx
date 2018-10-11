package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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
public class ObjectSelectionTilesComputingTest extends AbstractOpTest {

    public ObjectSelectionTilesComputingTest() {
    }

    @Test
    public void testFinalMasksTilesComputing() throws Exception {
        Dimension tileSize = JAI.getDefaultTileSize();
        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();

        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        Path folder = this.forestCoverChangeTestsFolderPath.resolve("object-selection");

        File segmentationProductFile = folder.resolve("S2A_20160713T125925_A005524_T35UMP_grm.dim").toFile();
        Product segmentationProduct = productReaderPlugIn.createReaderInstance().readProductNodes(segmentationProductFile, null);

        File landCoverProductFile = folder.resolve("S2A_20160713T125925_A005524_T35UMP_grm_landCover.dim").toFile();
        Product landCoverProduct = productReaderPlugIn.createReaderInstance().readProductNodes(landCoverProductFile, null);

        checkLandCoverProduct(landCoverProduct);

        ProductBandToMatrixConverter converter = new ProductBandToMatrixConverter(segmentationProduct, tileSize.width, tileSize.height);
        IntMatrix segmentationMatrix = converter.runTilesInParallel(threadCount, threadPool);

        IntSet landCoverValidPixels = new IntOpenHashSet();
        landCoverValidPixels.add(40);
        landCoverValidPixels.add(50);
        landCoverValidPixels.add(60);
        landCoverValidPixels.add(61);
        landCoverValidPixels.add(62);
        landCoverValidPixels.add(70);
        landCoverValidPixels.add(71);
        landCoverValidPixels.add(80);
        landCoverValidPixels.add(81);
        landCoverValidPixels.add(82);
        landCoverValidPixels.add(90);
        landCoverValidPixels.add(100);
        landCoverValidPixels.add(110);
        landCoverValidPixels.add(160);
        landCoverValidPixels.add(170);

        ObjectsSelectionTilesComputing tilesComputing = new ObjectsSelectionTilesComputing(segmentationMatrix, landCoverProduct, landCoverValidPixels, tileSize.width, tileSize.height);
        Int2ObjectMap<PixelStatistic> statistics = tilesComputing.runTilesInParallel(threadCount, threadPool);

        checkStatistics(statistics);
    }

    private static void checkLandCoverProduct(Product landCoverProduct) {
        assertNotNull(landCoverProduct);
        assertEquals(landCoverProduct.getName(), "S2A_20160713T125925_A005524_T35UMP_grm_landCover");
        assertEquals(landCoverProduct.getSceneRasterSize(), new Dimension(549, 549));
        assertEquals(landCoverProduct.getNumBands(), 1);

        Band targetBand = landCoverProduct.getBandAt(0);
        assertNotNull(targetBand);
        assertEquals("land_cover_CCILandCover-2015", targetBand.getName());
        assertEquals(ProductData.TYPE_INT16, targetBand.getDataType());
        assertEquals(549 * 549, targetBand.getNumDataElems());
    }

    private static void checkStatistics(Int2ObjectMap<PixelStatistic> statistics) {
        assertNotNull(statistics);

        assertEquals(statistics.size(), 1512);

        PixelStatistic pixel = statistics.get(100);
        assertEquals(18, pixel.getPixelsInRange());
        assertEquals(122, pixel.getTotalNumberPixels());

        pixel = statistics.get(200);
        assertEquals(31, pixel.getPixelsInRange());
        assertEquals(163, pixel.getTotalNumberPixels());

        pixel = statistics.get(300);
        assertEquals(4, pixel.getPixelsInRange());
        assertEquals(150, pixel.getTotalNumberPixels());

        pixel = statistics.get(400);
        assertEquals(196, pixel.getPixelsInRange());
        assertEquals(197, pixel.getTotalNumberPixels());

        pixel = statistics.get(500);
        assertEquals(0, pixel.getPixelsInRange());
        assertEquals(43, pixel.getTotalNumberPixels());

        pixel = statistics.get(600);
        assertEquals(1, pixel.getPixelsInRange());
        assertEquals(392, pixel.getTotalNumberPixels());

        pixel = statistics.get(700);
        assertEquals(68, pixel.getPixelsInRange());
        assertEquals(84, pixel.getTotalNumberPixels());

        pixel = statistics.get(800);
        assertEquals(4, pixel.getPixelsInRange());
        assertEquals(119, pixel.getTotalNumberPixels());

        pixel = statistics.get(900);
        assertEquals(67, pixel.getPixelsInRange());
        assertEquals(106, pixel.getTotalNumberPixels());
    }
}
