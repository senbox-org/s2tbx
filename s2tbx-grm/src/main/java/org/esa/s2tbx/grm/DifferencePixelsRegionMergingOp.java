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
import org.esa.snap.utils.matrix.IntMatrix;

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

    public static IntMatrix runSegmentation(int threadCount, Executor threadPool, Product currentSourceProduct, String[] currentSourceBandNames,
                                            Product previousSourceProduct, String[] previousSourceBandNames, String mergingCostCriterion,
                                            String regionMergingCriterion, int totalIterationsForSecondSegmentation, float threshold,
                                            float spectralWeight, float shapeWeight, Dimension tileSize)
                                            throws Exception {

        Dimension imageSize = new Dimension(currentSourceProduct.getSceneRasterWidth(), currentSourceProduct.getSceneRasterHeight());

        AbstractTileSegmenter tileSegmenter = buildTileSegmenter(threadCount, threadPool, mergingCostCriterion, regionMergingCriterion,
                                                    totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight, imageSize, tileSize);

        long startTime = System.currentTimeMillis();
        tileSegmenter.logStartSegmentation(startTime);

        tileSegmenter.runDifferenceFirstSegmentationsInParallel(currentSourceProduct, currentSourceBandNames, previousSourceProduct, previousSourceBandNames);
        AbstractSegmenter segmenter = tileSegmenter.runSecondSegmentationsAndMergeGraphs();
        IntMatrix result = segmenter.buildOutputMatrix();

        tileSegmenter.logFinishSegmentation(startTime, segmenter);

        segmenter.getGraph().doClose();
        segmenter = null;
        tileSegmenter = null;
        System.gc();

        return result;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(DifferencePixelsRegionMergingOp.class);
        }
    }
}
