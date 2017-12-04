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
import static org.junit.Assert.assertTrue;

/**
 * Created by jcoravu on 31/7/2017.
 */
public class UnionMasksTilesComputingTest extends AbstractOpTest {

    public UnionMasksTilesComputingTest() {
    }

    @Test
    public void testTrimmingRegionTilesComputing() throws Exception {
        Dimension tileSize = JAI.getDefaultTileSize();
        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();

        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        Path unionFolder = this.forestCoverChangeTestsFolderPath.resolve("union-mask");

        File currentSegmentationSourceProductFile = unionFolder.resolve("S2A_R093_T35UMP_20170628T092026_grm_fill.dim").toFile();
        Product currentSegmentationSourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentSegmentationSourceProductFile, null);

        IntSet currentSegmentationTrimmingRegionKeys = buildCurrentSegmentationTrimmingRegionKeys();
        IntSet previousSegmentationTrimmingRegionKeys = buildPreviousSegmentationTrimmingRegionKeys();

        ProductBandToMatrixConverter converter1 = new ProductBandToMatrixConverter(currentSegmentationSourceProduct, tileSize.width, tileSize.height);
        IntMatrix currentSegmentationSourceMatrix = converter1.runTilesInParallel(threadCount, threadPool);

        UnionMasksTilesComputing tilesComputing = new UnionMasksTilesComputing(currentSegmentationSourceMatrix, currentSegmentationTrimmingRegionKeys,
                                                                               previousSegmentationTrimmingRegionKeys, tileSize.width, tileSize.height);
        ProductData productData = tilesComputing.runTilesInParallel(threadCount, threadPool);

        assertNotNull(productData);

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, currentSegmentationSourceProduct.getSceneRasterWidth(), currentSegmentationSourceProduct.getSceneRasterHeight());
        targetBand.setData(productData);
        // reset the source image of the target product
        targetBand.setSourceImage(null);
        targetBand.getSourceImage();

        assertEquals(2, targetBand.getSampleInt(147, 163));

        assertEquals(2, targetBand.getSampleInt(57, 237));
        assertEquals(0, targetBand.getSampleInt(33, 434));
        assertEquals(0, targetBand.getSampleInt(478, 51));
        assertEquals(2, targetBand.getSampleInt(509, 212));
        assertEquals(2, targetBand.getSampleInt(209, 533));
        assertEquals(2, targetBand.getSampleInt(10, 508));
        assertEquals(2, targetBand.getSampleInt(254, 476));
        assertEquals(2, targetBand.getSampleInt(84, 343));
        assertEquals(0, targetBand.getSampleInt(14, 468));
        assertEquals(0, targetBand.getSampleInt(205, 139));
        assertEquals(0, targetBand.getSampleInt(22, 325));
        assertEquals(0, targetBand.getSampleInt(196, 397));
        assertEquals(2, targetBand.getSampleInt(3, 433));
        assertEquals(2, targetBand.getSampleInt(506, 214));
    }

    private static IntSet buildCurrentSegmentationTrimmingRegionKeys() {
        IntSet regionKeys = new IntOpenHashSet();
        regionKeys.add(410);
        regionKeys.add(899);
        regionKeys.add(820);
        regionKeys.add(1278);
        regionKeys.add(402);
        regionKeys.add(963);
        regionKeys.add(387);
        regionKeys.add(176);
        regionKeys.add(1419);
        regionKeys.add(1112);
        regionKeys.add(890);
        regionKeys.add(429);
        regionKeys.add(1286);
        regionKeys.add(1093);
        regionKeys.add(886);
        regionKeys.add(861);
        regionKeys.add(412);
        regionKeys.add(397);
        regionKeys.add(881);
        regionKeys.add(1436);
        regionKeys.add(373);
        regionKeys.add(542);
        regionKeys.add(625);
        regionKeys.add(600);
        regionKeys.add(411);
        regionKeys.add(1290);
        regionKeys.add(845);
        regionKeys.add(1464);
        regionKeys.add(1413);
        regionKeys.add(1215);
        regionKeys.add(986);
        regionKeys.add(391);
        regionKeys.add(382);
        regionKeys.add(1099);
        regionKeys.add(862);
        regionKeys.add(1076);
        return regionKeys;
    }

    private static IntSet buildPreviousSegmentationTrimmingRegionKeys() {
        IntSet regionKeys = new IntOpenHashSet();
        regionKeys.add(903);
        regionKeys.add(410);
        regionKeys.add(627);
        regionKeys.add(386);
        regionKeys.add(399);
        regionKeys.add(1153);
        regionKeys.add(1127);
        regionKeys.add(620);
        regionKeys.add(1315);
        regionKeys.add(1438);
        regionKeys.add(697);
        regionKeys.add(839);
        regionKeys.add(1442);
        regionKeys.add(1283);
        regionKeys.add(1008);
        regionKeys.add(319);
        regionKeys.add(426);
        regionKeys.add(371);
        regionKeys.add(1129);
        regionKeys.add(1074);
        regionKeys.add(389);
        regionKeys.add(1052);
        regionKeys.add(1328);
        regionKeys.add(1099);
        regionKeys.add(418);
        regionKeys.add(394);
        regionKeys.add(1336);
        regionKeys.add(1085);
        regionKeys.add(400);
        regionKeys.add(882);
        regionKeys.add(1020);
        regionKeys.add(417);
        regionKeys.add(350);
        regionKeys.add(744);
        regionKeys.add(412);
        regionKeys.add(404);
        regionKeys.add(1498);
        regionKeys.add(576);
        regionKeys.add(1314);
        regionKeys.add(679);
        regionKeys.add(1047);
        return regionKeys;
    }
}
