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
import org.esa.snap.core.gpf.internal.OperatorContext;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.util.math.MathUtils;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
        Logger.getLogger("org.esa.s2tbx.grm").setLevel(Level.FINER);
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle targetRectangle = targetTile.getRectangle();

        ProcessingTile currentTile = this.tileSegmenter.buildTile(targetRectangle.x, targetRectangle.y, targetRectangle.width, targetRectangle.height);

        int tileColumnIndex = this.tileSegmenter.computeTileColumnIndex(currentTile);
        int tileRowIndex = this.tileSegmenter.computeTileRowIndex(currentTile);
        if (logger.isLoggable(Level.FINE)) {
            int tileMargin = this.tileSegmenter.computeTileMargin();
            int firstNumberOfIterations = this.tileSegmenter.getIterationsForEachFirstSegmentation();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute tile: row index: "+tileRowIndex+", column index: "+tileColumnIndex+", margin: "+tileMargin+", bounds: [x=" +targetRectangle.x+", y="+targetRectangle.y+", width="+targetRectangle.width+", height="+targetRectangle.height+"], first number of iterations: "+firstNumberOfIterations);
        }

        ProcessingTile oldTile = this.tileSegmenter.addTile(tileRowIndex, tileColumnIndex, currentTile);
        if (oldTile == null) {
            Tile[] sourceTiles = getSourceTiles(currentTile.getRegion());

            try {
                this.tileSegmenter.runOneTileFirstSegmentation(sourceTiles, currentTile);
            } catch (Exception ex) {
                throw new OperatorException(ex);
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                int tileMargin = this.tileSegmenter.computeTileMargin();
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Tile already computed: row index: "+tileRowIndex+", column index: "+tileColumnIndex+", margin: "+tileMargin+", bounds: [x=" +targetRectangle.x+", y="+targetRectangle.y+", width="+targetRectangle.width+", height="+targetRectangle.height+"]");
            }
        }
    }

    private Tile[] getSourceTiles(BoundingBox tileRegion) {
        Tile[] sourceTiles = new Tile[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            sourceTiles[i] = getSourceTile(band, rectangleToRead);
        }
        return sourceTiles;
    }

    public Class<?> getTileSegmenterClass() {
        return this.tileSegmenter.getClass();
    }

    public AbstractSegmenter runSegmentation() throws IOException, IllegalAccessException {
        int sceneWidth = this.tileSegmenter.getImageWidth();
        int sceneHeight = this.tileSegmenter.getImageHeight();

        long startTime = System.currentTimeMillis();
        if (logger.isLoggable(Level.FINE)) {
            int tileWidth = this.tileSegmenter.getTileWidth();
            int tileHeight = this.tileSegmenter.getTileHeight();
            int tileMargin = this.tileSegmenter.computeTileMargin();
            int firstNumberOfIterations = this.tileSegmenter.getIterationsForEachFirstSegmentation();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start Segmentation: image width: " +sceneWidth+", image height: "+sceneHeight+", tile width: "+tileWidth+", tile height: "+tileHeight+", margin: "+tileMargin+", first number of iterations: "+firstNumberOfIterations+", start time: "+new Date(startTime));
        }

        AbstractSegmenter segmenter = runTilesSegmentationWithJAI();
//        AbstractSegmenter segmenter = runAllTilesSegmentation();

        Band targetBand = this.targetProduct.getBandAt(0);
        targetBand.setSourceImage(null); // reset the source image
        segmenter.fillBandData(targetBand);

        if (logger.isLoggable(Level.FINE)) {
            long finishTime = System.currentTimeMillis();
            long totalSeconds = (finishTime - startTime) / 1000;
            int graphNodeCount = segmenter.getGraph().getNodeCount();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Finish Segmentation: image width: " +sceneWidth+", image height: "+sceneHeight+", graph node count: "+graphNodeCount+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
        }

        return segmenter;
    }

    private AbstractSegmenter runAllTilesSegmentation() throws IOException, IllegalAccessException {
        int sceneWidth = this.tileSegmenter.getImageWidth();
        int sceneHeight = this.tileSegmenter.getImageHeight();
        Tile[] sourceTiles = new Tile[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(0, 0, sceneWidth, sceneHeight);
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            sourceTiles[i] = getSourceTile(band, rectangleToRead);
        }
        return this.tileSegmenter.runAllTilesSegmentation(sourceTiles);
    }

    private AbstractSegmenter runTilesSegmentationWithJAI() throws IOException, IllegalAccessException {
        int sceneWidth = this.tileSegmenter.getImageWidth();
        int sceneHeight = this.tileSegmenter.getImageHeight();
        int tileWidth = this.tileSegmenter.getTileWidth();
        int tileHeight = this.tileSegmenter.getTileHeight();
        int tileCountX = MathUtils.ceilInt(sceneWidth / (double) tileWidth);
        int tileCountY = MathUtils.ceilInt(sceneHeight / (double) tileHeight);
        if (tileCountX > 1 || tileCountY > 1) {
            OperatorExecutor operatorExecutor = OperatorExecutor.create(this);
            operatorExecutor.execute(ProgressMonitor.NULL);

            //int computedTileCountX = this.tileSegmenter.getComputedTileCountX();
            //int computedTileCountY = this.tileSegmenter.getComputedTileCountY();
            for (int row = 0; row <tileCountY; row++) {
                for (int col = 0; col < tileCountX; col++) {
                    if (this.tileSegmenter.canAddTile(row, col)) {
                        int startX = col * tileWidth;
                        int startY = row * tileHeight;
                        int sizeX = tileWidth;
                        int sizeY = tileHeight;
                        // current tile size might be different for right and bottom borders
                        if (col == tileCountX - 1) {
                            sizeX = sceneWidth % tileWidth;
                        }
                        if (row == tileCountY - 1) {
                            sizeY = sceneHeight % tileHeight;
                        }
                        ProcessingTile currentTile = this.tileSegmenter.buildTile(startX, startY, sizeX, sizeY);
                        int tileColumnIndex = col;//computeTileColumnIndex(currentTile);
                        int tileRowIndex = row;//computeTileRowIndex(currentTile);
                        ProcessingTile oldTile = this.tileSegmenter.addTile(tileRowIndex, tileColumnIndex, currentTile);
                        if (oldTile == null) {
                            Tile[] sourceTiles = getSourceTiles(currentTile.getRegion());
                            this.tileSegmenter.runOneTileFirstSegmentation(sourceTiles, currentTile);
                        }
                    }
                }
            }

            return this.tileSegmenter.runAllTilesSecondSegmentation();
        }

        Tile[] sourceTiles = new Tile[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(0, 0, sceneWidth, sceneHeight);
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            sourceTiles[i] = getSourceTile(band, rectangleToRead);
        }
        BoundingBox imageRegion = new BoundingBox(0, 0, sceneWidth, sceneHeight);
        int numberOfIterations = this.tileSegmenter.getIterationsForEachFirstSegmentation() + this.tileSegmenter.getTotalIterationsForSecondSegmentation();
        AbstractSegmenter segmenter = this.tileSegmenter.buildSegmenter(this.tileSegmenter.getThreshold());
        segmenter.update(sourceTiles, imageRegion, numberOfIterations, this.tileSegmenter.isFastSegmentation(), this.tileSegmenter.isAddFourNeighbors());
        return segmenter;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(GenericRegionMergingOp.class);
        }
    }
}
