//package org.esa.s2tbx.fcc.trimming;
//
//import com.bc.ceres.core.ProgressMonitor;
//import it.unimi.dsi.fastutil.ints.IntSet;
//import org.esa.snap.core.datamodel.Band;
//import org.esa.snap.core.datamodel.Product;
//import org.esa.snap.core.datamodel.ProductData;
//import org.esa.snap.core.gpf.Operator;
//import org.esa.snap.core.gpf.OperatorException;
//import org.esa.snap.core.gpf.OperatorSpi;
//import org.esa.snap.core.gpf.Tile;
//import org.esa.snap.core.gpf.annotations.OperatorMetadata;
//import org.esa.snap.core.gpf.annotations.Parameter;
//import org.esa.snap.core.gpf.annotations.SourceProduct;
//import org.esa.snap.core.gpf.annotations.TargetProduct;
//
//import javax.media.jai.JAI;
//import java.awt.Dimension;
//import java.awt.Rectangle;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.Executor;
//
///**
// * @author Razvan Dumitrascu
// * @since 5.0.6
// */
//@OperatorMetadata(
//        alias = "DifferenceRegionComputingOp",
//        version="1.0",
//        category = "",
//        description = "Creates a hash map containing the values from the source bands for a respective segmentation region",
//        authors = "Razvan Dumitrascu, Jean Coravu",
//        copyright = "Copyright (C) 2017 by CS ROMANIA")
//
//public class DifferenceRegionComputingOp extends Operator {
//    @SourceProduct(alias = "currentSourceProduct", description = "The current source product to be used for trimming.")
//    private Product currentSourceProduct;
//
//    @SourceProduct(alias = "previousSourceProduct", description = "The previous source product to be used for trimming.")
//    private Product previousSourceProduct;
//
//    @SourceProduct(alias = "unionMask", description = "The source products to be used for trimming.")
//    private Product unionMask;
//
//    @SourceProduct(alias = "Source", description = "The segmentation source product with segments that have more than 95% forest cover")
//    private Product differenceSegmentationProduct;
//
//    @TargetProduct
//    private Product targetProduct;
//
//    @Parameter(itemAlias = "sourceBandIndices", description = "The index from the source product to be used.")
//    private int[] sourceBandIndices;
//
//    private DifferenceRegionTilesComputing differenceRegionComputingHelper;
//    private Set<String> processedTiles;
//
//    @Override
//    public void initialize() throws OperatorException {
//        validateSourceProducts();
//
//        int sceneWidth = this.differenceSegmentationProduct.getSceneRasterWidth();
//        int sceneHeight = this.differenceSegmentationProduct.getSceneRasterHeight();
//        Dimension tileSize = JAI.getDefaultTileSize();
//
//        this.targetProduct = new Product(this.differenceSegmentationProduct.getName() + "_trim", this.differenceSegmentationProduct.getProductType(), sceneWidth, sceneHeight);
//        this.targetProduct.setPreferredTileSize(tileSize);
//        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
//        this.targetProduct.addBand(targetBand);
//
//        this.processedTiles = new HashSet<String>();
//        this.differenceRegionComputingHelper = new DifferenceRegionTilesComputing(differenceSegmentationProduct, currentSourceProduct, previousSourceProduct, unionMask, sourceBandIndices, 0, 0);
//    }
//
//    @Override
//    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
//        Rectangle tileRegion = targetTile.getRectangle();
//        String key = tileRegion.x+"|"+tileRegion.y+"|"+tileRegion.width+"|"+tileRegion.height;
//        boolean canProcessTile = false;
//        synchronized (this.processedTiles) {
//            canProcessTile = this.processedTiles.add(key);
//        }
//        if (canProcessTile) {
//            try {
//                this.differenceRegionComputingHelper.runTile(tileRegion.x, tileRegion.y, tileRegion.width, tileRegion.height, 0, 0);
//            } catch (Exception ex) {
//                throw new OperatorException(ex);
//            }
//        }
//    }
//
//    public final IntSet processResult(int threadCount, Executor threadPool) throws Exception {
//        return this.differenceRegionComputingHelper.processResult(threadCount, threadPool);
//    }
//
//    private void validateSourceProducts() {
//        if (this.currentSourceProduct.isMultiSize()) {
//            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
//                            "Please consider resampling it so that all rasters have the same size.",
//                    this.currentSourceProduct.getName());
//            throw new OperatorException(message);
//        }
//        if ((this.currentSourceProduct.getSceneRasterHeight() != this.differenceSegmentationProduct.getSceneRasterHeight()) ||
//                (this.currentSourceProduct.getSceneRasterWidth() != this.differenceSegmentationProduct.getSceneRasterWidth())) {
//            String message = String.format("Source product '%s' must have the same scene raster size as the source Composition Product '%s'.\n" +
//                            "Please consider resampling it so that the 2 products have the same size.",
//                    this.differenceSegmentationProduct.getName(), this.currentSourceProduct.getName());
//            throw new OperatorException(message);
//        }
//        if (this.previousSourceProduct.isMultiSize()) {
//            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
//                            "Please consider resampling it so that all rasters have the same size.",
//                    this.previousSourceProduct.getName());
//            throw new OperatorException(message);
//        }
//        if ((this.previousSourceProduct.getSceneRasterHeight() != this.differenceSegmentationProduct.getSceneRasterHeight()) ||
//                (this.previousSourceProduct.getSceneRasterWidth() != this.differenceSegmentationProduct.getSceneRasterWidth())) {
//            String message = String.format("Source product '%s' must have the same scene raster size as the source Composition Product '%s'.\n" +
//                            "Please consider resampling it so that the 2 products have the same size.",
//                    this.differenceSegmentationProduct.getName(), this.previousSourceProduct.getName());
//            throw new OperatorException(message);
//        }
//    }
//
//    public static class Spi extends OperatorSpi {
//
//        public Spi(){
//            super(DifferenceRegionComputingOp.class);
//        }
//    }
//}
