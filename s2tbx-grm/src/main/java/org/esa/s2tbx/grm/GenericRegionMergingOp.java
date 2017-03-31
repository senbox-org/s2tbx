package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
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

import javax.media.jai.JAI;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
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
public class GenericRegionMergingOp extends Operator {
    private static final Logger logger = Logger.getLogger(GenericRegionMergingOp.class.getName());

    public static final String SPRING_MERGING_COST_CRITERION = "Spring";
    public static final String BAATZ_SCHAPE_MERGING_COST_CRITERION = "Baatz & Schape";
    public static final String FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION = "Full Lamda Schedule";
    private static final String BEST_FITTING_REGION_MERGING_CRITERION = "Best Fitting";
    public static final String LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION = "Local Mutual Best Fitting";

    @Parameter(label = "Merging cost criterion",
            description = "The method to compute the region merging.",
            valueSet = {SPRING_MERGING_COST_CRITERION, BAATZ_SCHAPE_MERGING_COST_CRITERION, FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION})
    private String mergingCostCriterion;

    @Parameter(label = "Region merging criterion",
            description = "The method to check the region merging.",
            valueSet = {BEST_FITTING_REGION_MERGING_CRITERION, LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION})
    private String regionMergingCriterion;

    @Parameter(label = "Total iterations for second segmentation", description = "The total number of iterations.")
    private int totalIterationsForSecondSegmentation;

    @Parameter(label = "Iterations for each first segmentation", description = "The number of iterations for each first segmentation.")
    private int iterationsForEachFirstSegmentation;

    @Parameter(label = "Threshold", description = "The threshold.")
    private int threshold;

    @Parameter(label = "Spectral weight", description = "The spectral weight.")
    private float spectralWeight;

    @Parameter(label = "Shape weight", description = "The shape weight.")
    private float shapeWeight;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    private AbstractTileSegmenter tileSegmenter;

    public GenericRegionMergingOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        if (this.mergingCostCriterion == null) {
            throw new OperatorException("Please specify the merging cost criterion.");
        }
        if (this.regionMergingCriterion == null) {
            throw new OperatorException("Please specify the region merging criterion.");
        }
        if (this.totalIterationsForSecondSegmentation == 0.0f) {
            throw new OperatorException("Please specify the total iterations for second segmentation.");
        }
        if (this.iterationsForEachFirstSegmentation == 0.0f) {
            throw new OperatorException("Please specify the iterations for each first segmentation.");
        }
        if (this.threshold == 0.0f) {
            throw new OperatorException("Please specify the threshold.");
        }
        if (BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
            if (this.spectralWeight == 0.0f) {
                throw new OperatorException("Please specify the spectral weight.");
            }
            if (this.shapeWeight == 0.0f) {
                throw new OperatorException("Please specify the shape weight.");
            }
        }
        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.sourceProduct.getName() + "_grm", this.sourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        boolean fastSegmentation = false;
        if (BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(this.regionMergingCriterion)) {
            fastSegmentation = true;
        } else if (BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(this.regionMergingCriterion)) {
            fastSegmentation = false;
        }

        Dimension imageSize = new Dimension(this.targetProduct.getSceneRasterWidth(), this.targetProduct.getSceneRasterHeight());

        try {
            if (SPRING_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
                this.tileSegmenter = new SpringTileSegmenter(imageSize, tileSize, totalIterationsForSecondSegmentation, iterationsForEachFirstSegmentation, threshold, fastSegmentation);
            } else if (BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
                this.tileSegmenter = new BaatzSchapeTileSegmenter(imageSize, tileSize, totalIterationsForSecondSegmentation, iterationsForEachFirstSegmentation, threshold, fastSegmentation, spectralWeight, shapeWeight);
            } else if (FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
                this.tileSegmenter = new FullLambdaScheduleTileSegmenter(imageSize, tileSize, totalIterationsForSecondSegmentation, iterationsForEachFirstSegmentation, threshold, fastSegmentation);
            }
        } catch (Exception ex) {
            throw new OperatorException(ex);
        }

        //TODO Jean remove
        Logger.getLogger("org.esa.s2tbx.grm").setLevel(Level.FINE);
    }

    public Class<?> getTileSegmenterClass() {
        return this.tileSegmenter.getClass();
    }

    public AbstractSegmenter runTileSegmentation() throws IOException, IllegalAccessException {
        OperatorExecutor operatorExecutor = OperatorExecutor.create(this);
        operatorExecutor.execute(ProgressMonitor.NULL);

        AbstractSegmenter segmenter = this.tileSegmenter.runAllTilesSecondSegmentation();

//        Tile[] sourceTiles = new Tile[this.sourceBandNames.length];
//        Rectangle rectangleToRead = new Rectangle(0, 0, sceneWidth, sceneHeight);
//        for (int i=0; i<this.sourceBandNames.length; i++) {
//            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
//            sourceTiles[i] = getSourceTile(band, rectangleToRead);
//        }
//        AbstractSegmenter segmenter = this.tileSegmenter.runAllTilesSegmentation(sourceTiles);

        return segmenter;
    }

    public void addBand(AbstractSegmenter segmenter) {
        Band oldTargetBand = this.targetProduct.getBandAt(0);
        Band targetBand = segmenter.buildBand();
        this.targetProduct.removeBand(oldTargetBand);
        this.targetProduct.addBand(targetBand);
    }

    public void runSegmentation() throws IOException, IllegalAccessException {
        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();

        long startTime = System.currentTimeMillis();
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start Segmentation: image width: " +sceneWidth+", image height: "+sceneHeight+", start time: "+new Date(startTime));
        }

        AbstractSegmenter segmenter = runTileSegmentation();

        addBand(segmenter);

        if (logger.isLoggable(Level.FINE)) {
            long finishTime = System.currentTimeMillis();
            long totalSeconds = (finishTime - startTime) / 1000;
            int graphNodeCount = segmenter.getGraph().getNodeCount();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Finish Segmentation: image width: " +sceneWidth+", image height: "+sceneHeight+", graph node count: "+graphNodeCount+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
        }
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle targetRectangle = targetTile.getRectangle();

        ProcessingTile currentTile = this.tileSegmenter.buildTile(targetRectangle.x, targetRectangle.y, targetRectangle.width, targetRectangle.height);
        BoundingBox tileRegion = currentTile.getRegion();

        Tile[] sourceTiles = new Tile[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());

        if (logger.isLoggable(Level.FINE)) {
            int tileColumnIndex = this.tileSegmenter.computeTileColumnIndex(currentTile);
            int tileRowIndex = this.tileSegmenter.computeTileRowIndex(currentTile);
            int tileMargin = this.tileSegmenter.computeTileMargin();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute tile: tile bounds: [x=" +targetRectangle.x+", y="+targetRectangle.y+", width="+targetRectangle.width+", height="+targetRectangle.height+"], margin: "+tileMargin+", tile row index: "+tileRowIndex+", tile column index: "+tileColumnIndex);
        }

        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            sourceTiles[i] = getSourceTile(band, rectangleToRead);
        }

        try {
            this.tileSegmenter.runOneTileFirstSegmentation(sourceTiles, currentTile);
        } catch (Exception ex) {
            throw new OperatorException(ex);
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(GenericRegionMergingOp.class);
        }
    }
}
