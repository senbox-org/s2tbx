package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BaatzSchapeNode;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.Graph;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.s2tbx.grm.segmentation.TileDataSourceImpl;
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
import org.esa.snap.utils.ObjectMemory;
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
        alias = "GenericRegionMergingOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "The 'Generic Region Merging' operator computes the distinct regions from a product",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class GenericRegionMergingOp extends AbstractRegionMergingOp {
    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    public GenericRegionMergingOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        super.initialize();

        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }
        Band firstSelectedSourceBand = this.sourceProduct.getBand(this.sourceBandNames[0]);
        for (int i=1; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            if (firstSelectedSourceBand.getRasterWidth() != band.getRasterWidth() || firstSelectedSourceBand.getRasterHeight() != band.getRasterHeight()) {
                throw new OperatorException("Please select the bands with the same resolution.");
            }
        }

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.sourceProduct.getName() + "_grm", this.sourceProduct.getProductType(), "band_1", ProductData.TYPE_INT32);
        ProductUtils.copyGeoCoding(this.sourceProduct, this.targetProduct);
    }

    public String getMergingCostCriterion() {
        return mergingCostCriterion;
    }

    public String getRegionMergingCriterion() {
        return regionMergingCriterion;
    }

    public int getTotalIterationsForSecondSegmentation() {
        return totalIterationsForSecondSegmentation;
    }

    public float getThreshold() {
        return threshold;
    }

    public float getShapeWeight() {
        return shapeWeight;
    }

    public float getSpectralWeight() {
        return spectralWeight;
    }

    public String[] getSourceBandNames() {
        return sourceBandNames;
    }

    @Override
    protected TileDataSource[] getSourceTiles(BoundingBox tileRegion) {
        TileDataSource[] sourceTiles = new TileDataSource[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            sourceTiles[i] = new TileDataSourceImpl(getSourceTile(band, rectangleToRead));
        }
        return sourceTiles;
    }

    public static IntMatrix runSegmentation(int threadCount, Executor threadPool, Product sourceProduct, String[] sourceBandNames,
                                            String mergingCostCriterion, String regionMergingCriterion, int totalIterationsForSecondSegmentation,
                                            float threshold, float spectralWeight, float shapeWeight, Dimension tileSize)
                                            throws Exception {

        Dimension imageSize = new Dimension(sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());

        AbstractTileSegmenter tileSegmenter = buildTileSegmenter(threadCount, threadPool, mergingCostCriterion, regionMergingCriterion, totalIterationsForSecondSegmentation,
                                                                 threshold, spectralWeight, shapeWeight, imageSize, tileSize);

        long startTime = System.currentTimeMillis();
        tileSegmenter.logStartSegmentation(startTime);

        tileSegmenter.runFirstSegmentationsInParallel(sourceProduct, sourceBandNames);
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
            super(GenericRegionMergingOp.class);
        }
    }
}
