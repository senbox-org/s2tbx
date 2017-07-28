//package org.esa.s2tbx.fcc.trimming;
//
//import com.bc.ceres.core.ProgressMonitor;
//import com.bc.ceres.core.SubProgressMonitor;
//import it.unimi.dsi.fastutil.ints.IntSet;
//import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
//import org.esa.snap.core.datamodel.Band;
//import org.esa.snap.core.datamodel.Product;
//import org.esa.snap.core.datamodel.ProductData;
//
//import org.esa.snap.core.gpf.*;
//import org.esa.snap.core.gpf.annotations.OperatorMetadata;
//import org.esa.snap.core.gpf.annotations.Parameter;
//import org.esa.snap.core.gpf.annotations.SourceProduct;
//import org.esa.snap.core.gpf.annotations.TargetProduct;
//import org.esa.snap.core.gpf.internal.OperatorExecutor;
//
//import javax.media.jai.JAI;
//import java.awt.*;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * @author Razvan Dumitrascu
// * @author Jean Coravu
// * @since 5.0.6
// */
//@OperatorMetadata(
//        alias = "TrimmingOp",
//        version="1.0",
//        category = "",
//        description = "",
//        authors = "Razvan Dumitrascu, Jean Coravu",
//        copyright = "Copyright (C) 2017 by CS ROMANIA")
//public class TrimmingOp extends Operator {
//
//    @SourceProduct(alias = "Source product", description = "The source products to be used for trimming.")
//    private Product sourceProduct;
//
//    @SourceProduct(alias = "Segmentation source product", description = "The source products to be used for trimming.")
//    private Product segmentationSourceProduct;
//
//    @Parameter(label = "Source band indices", description = "The source band indices.")
//    private int[] sourceBandIndices;
//
//    @TargetProduct
//    private Product targetProduct;
//
//    private IntSet processedValidTiles;
//    private Set<String> processedTiles;
//
//    @Override
//    public void initialize() throws OperatorException {
//        try {
//            this.processedValidTiles = computeValidTiles();
//        } catch (Exception e) {
//            throw new OperatorException(e);
//        }
//        int sceneWidth = this.segmentationSourceProduct.getSceneRasterWidth();
//        int sceneHeight = this.segmentationSourceProduct.getSceneRasterHeight();
//        Dimension tileSize = JAI.getDefaultTileSize();
//
//        this.targetProduct = new Product(this.segmentationSourceProduct.getName() + "_union", this.segmentationSourceProduct.getProductType(), sceneWidth, sceneHeight);
//        this.targetProduct.setPreferredTileSize(tileSize);
//        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
//        this.targetProduct.addBand(targetBand);
//        this.processedTiles = new HashSet<String>();
//    }
//
//    private IntSet computeValidTiles() throws Exception {
//        int[] trimmingSourceProductBandIndices = new int[]{0, 1, 2};
//        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
//        ExecutorService threadPool = Executors.newCachedThreadPool();
//        IntSet processedTiels = null;
//
////        Map<String, Object> parameters = new HashMap<>();
////        parameters.put("sourceBandIndices", trimmingSourceProductBandIndices);
////        Map<String, Product> sourceProducts = new HashMap<>();
////        sourceProducts.put("sourceProduct", sourceProduct);
////        sourceProducts.put("segmentationSourceProduct", segmentationSourceProduct);
////        TrimmingRegionComputingOp operator = (TrimmingRegionComputingOp) GPF.getDefaultInstance().createOperator("TrimmingRegionComputingOp", parameters, sourceProducts, null);
////        Product targetProduct = operator.getTargetProduct();
////        OperatorExecutor executor = OperatorExecutor.create(operator);
////        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
////        processedTiels = operator.processResult(threadCount, threadPool);
//        return processedTiels;
//    }
//
//    @Override
//    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
//        Rectangle tileRegion = targetTile.getRectangle();
//        String key = tileRegion.x + "|" + tileRegion.y + "|" + tileRegion.width + "|" + tileRegion.height;
//        boolean canProcessTile = false;
//        Band segmentationBand = this.segmentationSourceProduct.getBandAt(0);
//        Band targetProductBand = this.targetProduct.getBandAt(0);
//        synchronized (this.processedTiles) {
//            canProcessTile = this.processedTiles.add(key);
//        }
//        if (canProcessTile) {
//            for (int y = tileRegion.y; y < tileRegion.y + tileRegion.height; y++) {
//                for (int x = tileRegion.x; x < tileRegion.x + tileRegion.width; x++) {
//                    int segmentationPixelValue = segmentationBand.getSampleInt(x, y);
//                    if (!this.processedValidTiles.contains(segmentationPixelValue)) {
//                        segmentationPixelValue = ForestCoverChangeConstants.NO_DATA_VALUE;
//                    }
//                    targetProductBand.setPixelFloat(x, y, segmentationPixelValue);
//                }
//            }
//        }
//    }
//
//    public static class Spi extends OperatorSpi {
//
//        public Spi(){
//            super(TrimmingOp.class);
//        }
//    }
//}
