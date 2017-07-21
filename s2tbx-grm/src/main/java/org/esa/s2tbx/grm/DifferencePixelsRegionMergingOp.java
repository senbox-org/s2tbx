package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import org.esa.s2tbx.grm.segmentation.*;
import org.esa.s2tbx.grm.segmentation.tiles.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.*;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.math.MathUtils;

import javax.media.jai.JAI;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Jean Coravu
 */
@OperatorMetadata(
        alias = "DifferencePixelsRegionMergingOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "The 'Generic Region Merging' operator computes the distinct regions from a product",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class DifferencePixelsRegionMergingOp extends AbstractRegionMergingOp {
    @SourceProduct(alias = "Current source", description = "The source product.")
    private Product currentSourceProduct;

    @SourceProduct(alias = "Previous source", description = "The source product.")
    private Product previousSourceProduct;

    @Parameter(label = "Current source bands", description = "The current source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] currentSourceBandNames;

    @Parameter(label = "Previous source bands", description = "The previous source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] previousSourceBandNames;

    public DifferencePixelsRegionMergingOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        super.initialize();

        validateSourceBandNames(this.currentSourceBandNames, this.currentSourceProduct, "Please select at least one current source band.");
        validateSourceBandNames(this.previousSourceBandNames, this.previousSourceProduct, "Please select at least one previous source band.");

        int sceneWidth = this.currentSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.currentSourceProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.currentSourceProduct.getName() + "_grm", this.currentSourceProduct.getProductType(), "band_1", ProductData.TYPE_INT32);
        ProductUtils.copyGeoCoding(this.currentSourceProduct, this.targetProduct);
    }

    @Override
    protected TileDataSource[] getSourceTiles(BoundingBox tileRegion) {
        TileDataSource[] sourceTiles = new TileDataSource[this.currentSourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());

        for (int i = 0; i<sourceTiles.length; i++) {
            Band currentBand = this.currentSourceProduct.getBand(this.currentSourceBandNames[i]);
            Band previousBand = this.previousSourceProduct.getBand(this.previousSourceBandNames[i]);

            Tile currentTile = getSourceTile(currentBand, rectangleToRead);
            Tile previousTile = getSourceTile(previousBand, rectangleToRead);

            sourceTiles[i] = new DifferenceTileDataSourceImpl(currentTile, previousTile);
        }

        return sourceTiles;
    }

    private void validateSourceBandNames(String[] sourceBandNames, Product sourceProduct, String errorMessage) {
        if (sourceBandNames == null || sourceBandNames.length == 0) {
            throw new OperatorException(errorMessage);
        }
        Band firstSourceBand = sourceProduct.getBand(sourceBandNames[0]);
        for (int i = 1; i<sourceBandNames.length; i++) {
            Band band = sourceProduct.getBand(sourceBandNames[i]);
            if (firstSourceBand.getRasterWidth() != band.getRasterWidth() || firstSourceBand.getRasterHeight() != band.getRasterHeight()) {
                throw new OperatorException("Please select the bands with the same resolution.");
            }
        }
    }

    public static Product runSegmentation(int threadCount, Executor threadPool, Product currentSourceProduct, String[] currentSourceBandNames,
                                          Product previousSourceProduct, String[] previousSourceBandNames, String mergingCostCriterion,
                                          String regionMergingCriterion, int totalIterationsForSecondSegmentation, float threshold,
                                          float spectralWeight, float shapeWeight)
                                          throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mergingCostCriterion", mergingCostCriterion);
        parameters.put("regionMergingCriterion", regionMergingCriterion);
        parameters.put("totalIterationsForSecondSegmentation", totalIterationsForSecondSegmentation);
        parameters.put("threshold", threshold);
        parameters.put("spectralWeight", spectralWeight);
        parameters.put("shapeWeight", shapeWeight);
        parameters.put("currentSourceBandNames", currentSourceBandNames);
        parameters.put("previousSourceBandNames", previousSourceBandNames);

        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("currentSourceProduct", currentSourceProduct);
        sourceProducts.put("previousSourceProduct", previousSourceProduct);
        DifferencePixelsRegionMergingOp regionComputingOp = (DifferencePixelsRegionMergingOp)GPF.getDefaultInstance().createOperator("DifferencePixelsRegionMergingOp", parameters, sourceProducts, null);
        Product targetProduct = regionComputingOp.getTargetProduct();

        OperatorExecutor executor = OperatorExecutor.create(regionComputingOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        return targetProduct;

//        //TODO Jean remove
//        Logger logger = Logger.getLogger("org.esa.s2tbx.grm");
//        logger.setLevel(Level.FINE);
//
//        Dimension imageSize = new Dimension(currentSourceProduct.getSceneRasterWidth(), currentSourceProduct.getSceneRasterHeight());
//        Dimension tileSize = JAI.getDefaultTileSize();
//
//        AbstractTileSegmenter tileSegmenter = buildTileSegmenter(threadCount, threadPool, mergingCostCriterion, regionMergingCriterion,
//                                                    totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight, imageSize, tileSize);
//
//        long startTime = System.currentTimeMillis();
//        tileSegmenter.logStartSegmentation(startTime);
//
//        tileSegmenter.runDifferenceFirstSegmentationsInParallel(currentSourceProduct, currentSourceBandNames, previousSourceProduct, previousSourceBandNames);
//        AbstractSegmenter segmenter = tileSegmenter.runSecondSegmentationsAndMergeGraphs();
//        Band productTargetBand = segmenter.buildBandData("band_1");
//
//        tileSegmenter.logFinishSegmentation(startTime, segmenter);
//
//        segmenter.getGraph().doClose();
//        segmenter = null;
//        tileSegmenter = null;
//        System.gc();
//
//        Product targetProduct = new Product(currentSourceProduct.getName() + "_grm", currentSourceProduct.getProductType(), productTargetBand.getRasterWidth(), productTargetBand.getRasterHeight());
//        targetProduct.setPreferredTileSize(tileSize);
//        ProductUtils.copyGeoCoding(currentSourceProduct, targetProduct);
//        productTargetBand.getSourceImage();
//        targetProduct.addBand(productTargetBand);
//
//        return targetProduct;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(DifferencePixelsRegionMergingOp.class);
        }
    }
}
