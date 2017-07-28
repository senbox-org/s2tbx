//package org.esa.s2tbx.fcc.trimming;
//
//import it.unimi.dsi.fastutil.ints.IntSet;
//import org.esa.snap.core.dataio.ProductReaderPlugIn;
//import org.esa.snap.core.datamodel.Product;
//import org.junit.Test;
//
//import javax.media.jai.JAI;
//import java.awt.Dimension;
//import java.io.File;
//import java.nio.file.Path;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
///**
// * Created by jcoravu on 24/7/2017.
// */
//public class DifferenceRegionTilesComputingTest extends AbstractOpTest {
//
//    public DifferenceRegionTilesComputingTest() {
//    }
//
//    @Test
//    public void testDifferenceTrimming() throws Exception {
//        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();
//
//        Path folder = this.forestCoverChangeTestsFolderPath.resolve("difference-trimming");
//
//        File differenceSegmentationProductFile = folder.resolve("differenceSegmentationProduct.dim").toFile();
//        Product differenceSegmentationProduct = productReaderPlugIn.createReaderInstance().readProductNodes(differenceSegmentationProductFile, null);
//
//        File unionMaskProductFile = folder.resolve("unionMaskProduct.dim").toFile();
//        Product unionMaskProduct = productReaderPlugIn.createReaderInstance().readProductNodes(unionMaskProductFile, null);
//
//        File currentProductFile = this.forestCoverChangeTestsFolderPath.resolve("S2A_R093_T35UMP_20170628T092026.dim").toFile();
//        Product currentSourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile, null);
//
//        File previousProductFile = this.forestCoverChangeTestsFolderPath.resolve("S2A_20160713T125925_A005524_T35UMP.dim").toFile();
//        Product previousSourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(previousProductFile, null);
//
//        Dimension tileSize = JAI.getDefaultTileSize();
//        int[] sourceBandIndices = new int[] {0, 1, 2};
//        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
//        ExecutorService threadPool = Executors.newCachedThreadPool();
//
//        DifferenceRegionTilesComputing difference = new DifferenceRegionTilesComputing(differenceSegmentationProduct, currentSourceProduct, previousSourceProduct,
//                                                                                       unionMaskProduct, sourceBandIndices, tileSize);
//
//        IntSet differenceTrimmingSet = difference.runTilesInParallel(threadCount, threadPool);
//
//        assertNotNull(differenceTrimmingSet);
//
//        assertTrue(differenceTrimmingSet.contains(1063));
//        assertTrue(differenceTrimmingSet.contains(1034));
//        assertTrue(differenceTrimmingSet.contains(967));
//        assertTrue(differenceTrimmingSet.contains(790));
//        assertTrue(differenceTrimmingSet.contains(1058));
//        assertTrue(differenceTrimmingSet.contains(500));
//        assertTrue(differenceTrimmingSet.contains(568));
//        assertTrue(differenceTrimmingSet.contains(471));
//        assertTrue(differenceTrimmingSet.contains(1432));
//        assertTrue(differenceTrimmingSet.contains(415));
//        assertTrue(differenceTrimmingSet.contains(399));
//        assertTrue(differenceTrimmingSet.contains(662));
//        assertTrue(differenceTrimmingSet.contains(1124));
//        assertTrue(differenceTrimmingSet.contains(1254));
//        assertTrue(differenceTrimmingSet.contains(985));
//        assertTrue(differenceTrimmingSet.contains(1067));
//        assertTrue(differenceTrimmingSet.contains(1392));
//        assertTrue(differenceTrimmingSet.contains(1164));
//        assertTrue(differenceTrimmingSet.contains(1135));
//        assertTrue(differenceTrimmingSet.contains(1051));
//        assertTrue(differenceTrimmingSet.contains(1356));
//        assertTrue(differenceTrimmingSet.contains(1209));
//        assertTrue(differenceTrimmingSet.contains(421));
//        assertTrue(differenceTrimmingSet.contains(1087));
//        assertTrue(differenceTrimmingSet.contains(1354));
//        assertTrue(differenceTrimmingSet.contains(1100));
//        assertTrue(differenceTrimmingSet.contains(1147));
//        assertTrue(differenceTrimmingSet.contains(704));
//        assertTrue(differenceTrimmingSet.contains(452));
//        assertTrue(differenceTrimmingSet.contains(428));
//        assertTrue(differenceTrimmingSet.contains(463));
//        assertTrue(differenceTrimmingSet.contains(337));
//        assertTrue(differenceTrimmingSet.contains(1434));
//        assertTrue(differenceTrimmingSet.contains(936));
//        assertTrue(differenceTrimmingSet.contains(368));
//        assertTrue(differenceTrimmingSet.contains(1322));
//        assertTrue(differenceTrimmingSet.contains(1201));
//        assertTrue(differenceTrimmingSet.contains(869));
//        assertTrue(differenceTrimmingSet.contains(186));
//        assertTrue(differenceTrimmingSet.contains(639));
//        assertTrue(differenceTrimmingSet.contains(1181));
//    }
//}
