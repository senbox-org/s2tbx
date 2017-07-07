package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.tiles.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.math.MathUtils;

import javax.media.jai.JAI;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
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
public class GenericRegionMergingOp extends Operator {
    private static final Logger logger = Logger.getLogger(GenericRegionMergingOp.class.getName());

    public static final String SPRING_MERGING_COST_CRITERION = "Spring";
    public static final String BAATZ_SCHAPE_MERGING_COST_CRITERION = "Baatz & Schape";
    public static final String FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION = "Full Lamda Schedule";
    public static final String BEST_FITTING_REGION_MERGING_CRITERION = "Best Fitting";
    public static final String LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION = "Local Mutual Best Fitting";

    public static final String DEFAULT_TOTAL_NUMBER_OF_ITERATIONS = "50";
    public static final String DEFAULT_THRESHOLD = "100.0";
    public static final String DEFAULT_SPECTRAL_WEIGHT = "0.5";
    public static final String DEFAULT_SHAPE_WEIGHT = "0.5";

    @Parameter(label = "Merging cost criterion",
            defaultValue = BAATZ_SCHAPE_MERGING_COST_CRITERION,
            description = "The method to compute the region merging.",
            valueSet = {SPRING_MERGING_COST_CRITERION, BAATZ_SCHAPE_MERGING_COST_CRITERION, FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION})
    private String mergingCostCriterion;

    @Parameter(label = "Region merging criterion",
            defaultValue = LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION,
            description = "The method to check the region merging.",
            valueSet = {BEST_FITTING_REGION_MERGING_CRITERION, LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION})
    private String regionMergingCriterion;

    @Parameter(defaultValue = DEFAULT_TOTAL_NUMBER_OF_ITERATIONS, label = "Total iterations", description = "The total number of iterations.")
    private int totalIterationsForSecondSegmentation;

    @Parameter(defaultValue = DEFAULT_THRESHOLD, label = "Threshold", description = "The threshold.")
    private float threshold;

    @Parameter(defaultValue = DEFAULT_SPECTRAL_WEIGHT, label = "Spectral weight", description = "The spectral weight.")
    private float spectralWeight;

    @Parameter(defaultValue = DEFAULT_SHAPE_WEIGHT, label = "Shape weight", description = "The shape weight.")
    private float shapeWeight;

    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    private AbstractTileSegmenter tileSegmenter;
    private long startTime;
    private AtomicInteger processingTiles;
    private AtomicInteger processedTiles;
    private int totalTileCount;
    private AbstractSegmenter segmenter;

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

        Band firstSelectedSourceBand = this.sourceProduct.getBand(this.sourceBandNames[0]);
        for (int i=1; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            if (firstSelectedSourceBand.getRasterWidth() != band.getRasterWidth() || firstSelectedSourceBand.getRasterHeight() != band.getRasterHeight()) {
                throw new OperatorException("Please select the bands with the same resolution.");
            }
        }

        createTargetProduct();

        this.processingTiles = new AtomicInteger(0);
        this.processedTiles = new AtomicInteger(0);

        int sceneWidth = this.targetProduct.getSceneRasterWidth();
        int sceneHeight = this.targetProduct.getSceneRasterHeight();
        Dimension tileSize = this.targetProduct.getPreferredTileSize();

        int tileCountX = MathUtils.ceilInt(sceneWidth / (double) tileSize.width);
        int tileCountY = MathUtils.ceilInt(sceneHeight / (double) tileSize.height);
        this.totalTileCount = tileCountX * tileCountY;

        try {
            this.tileSegmenter = buildTileSegmenter(mergingCostCriterion, regionMergingCriterion, totalIterationsForSecondSegmentation,
                                                    threshold, spectralWeight, shapeWeight, targetProduct);
        } catch (IOException e) {
            throw new OperatorException(e);
        }

        //TODO Jean remove
        Logger logger = Logger.getLogger("org.esa.s2tbx.grm");
        logger.setLevel(Level.FINE);
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        int startProcessingTileCount = this.processingTiles.incrementAndGet();
        if (startProcessingTileCount == 1) {
            this.startTime = System.currentTimeMillis();
            if (logger.isLoggable(Level.FINE)) {
                int imageWidth = this.tileSegmenter.getImageWidth();
                int imageHeight = this.tileSegmenter.getImageHeight();
                int tileWidth = this.tileSegmenter.getTileWidth();
                int tileHeight = this.tileSegmenter.getTileHeight();
                int tileMargin = this.tileSegmenter.computeTileMargin();
                int firstNumberOfIterations = this.tileSegmenter.getIterationsForEachFirstSegmentation();
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Start Segmentation: image width: " + imageWidth + ", image height: " + imageHeight + ", tile width: " + tileWidth + ", tile height: " + tileHeight + ", margin: " + tileMargin + ", first number of iterations: " + firstNumberOfIterations + ", start time: " + new Date(startTime));
            }
        }

        executeFirstTileSegmentation(targetTile.getRectangle(), this.totalTileCount);

        if (startProcessingTileCount == this.totalTileCount) {
            synchronized (this.processedTiles) {
                if (this.processedTiles.get() < this.totalTileCount) {
                    try {
                        this.processedTiles.wait();
                    } catch (InterruptedException e) {
                        throw new OperatorException(e);
                    }
                }
            }

            try {
                this.segmenter = this.tileSegmenter.runAllTilesSecondSegmentation();
            } catch (Exception e) {
                throw new OperatorException(e);
            }

            Band productTargetBand = this.targetProduct.getBandAt(0);
            productTargetBand.setSourceImage(null); // reset the source image
            this.segmenter.fillBandData(productTargetBand);
            productTargetBand.getSourceImage();

            if (logger.isLoggable(Level.FINE)) {
                int imageWidth = tileSegmenter.getImageWidth();
                int imageHeight = tileSegmenter.getImageHeight();
                int tileWidth = tileSegmenter.getTileWidth();
                int tileHeight = tileSegmenter.getTileHeight();
                int tileMargin = tileSegmenter.computeTileMargin();

                long finishTime = System.currentTimeMillis();
                long totalSeconds = (finishTime - this.startTime) / 1000;
                int graphNodeCount = this.segmenter.getGraph().getNodeCount();
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Finish Segmentation: image width: " +imageWidth+", image height: "+imageHeight+", tile width: "+tileWidth+", tile height: "+tileHeight+", margin: "+tileMargin+", graph node count: "+graphNodeCount+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
            }
        }
    }

    public AbstractSegmenter getSegmenter() {
        return segmenter;
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

    private void createTargetProduct() {
        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.sourceProduct.getName() + "_grm", this.sourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        this.targetProduct.setSceneGeoCoding(this.sourceProduct.getSceneGeoCoding());

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
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

    private void executeFirstTileSegmentation(Rectangle targetRectangle, int totalTileCount) throws OperatorException {
        try {
            ProcessingTile currentTile = this.tileSegmenter.buildTile(targetRectangle.x, targetRectangle.y, targetRectangle.width, targetRectangle.height);
            Tile[] sourceTiles = getSourceTiles(currentTile.getRegion());
            try {
                this.tileSegmenter.runOneTileFirstSegmentation(sourceTiles, currentTile);
            } catch (Exception ex) {
                throw new OperatorException(ex);
            }
        } finally {
            synchronized (this.processedTiles) {
                int finishProcessingTileCount = this.processedTiles.incrementAndGet();
                if (finishProcessingTileCount == totalTileCount) {
                    this.processedTiles.notifyAll();
                }
            }
        }
    }

    private static AbstractTileSegmenter buildTileSegmenter(String mergingCostCriterion, String regionMergingCriterion,
                                                            int totalIterationsForSecondSegmentation, float threshold, float spectralWeight,
                                                            float shapeWeight, Product targetProduct)
                                                            throws IOException {

        AbstractTileSegmenter tileSegmenter = null;
        boolean fastSegmentation = false;
        if (GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(regionMergingCriterion)) {
            fastSegmentation = true;
        } else if (GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(regionMergingCriterion)) {
            fastSegmentation = false;
        }
        Dimension imageSize = new Dimension(targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());
        Dimension tileSize = targetProduct.getPreferredTileSize();
        if (GenericRegionMergingOp.SPRING_MERGING_COST_CRITERION.equalsIgnoreCase(mergingCostCriterion)) {
            tileSegmenter = new SpringTileSegmenter(imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation);
        } else if (GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(mergingCostCriterion)) {
            tileSegmenter = new BaatzSchapeTileSegmenter(imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation, spectralWeight, shapeWeight);
        } else if (GenericRegionMergingOp.FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION.equalsIgnoreCase(mergingCostCriterion)) {
            tileSegmenter = new FullLambdaScheduleTileSegmenter(imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation);
        } else {
            throw new IllegalArgumentException("Unknown merging cost criterion '" + mergingCostCriterion + "'.");
        }
        return tileSegmenter;
    }

    public static Product runSegmentation(Product sourceProduct, String[] sourceBandNames, String mergingCostCriterion, String regionMergingCriterion,
                                          int totalIterationsForSecondSegmentation, float threshold, float spectralWeight, float shapeWeight)
                                          throws Exception {

        //TODO Jean remove
        Logger logger = Logger.getLogger("org.esa.s2tbx.grm");
        logger.setLevel(Level.FINE);

        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        Product targetProduct = new Product(sourceProduct.getName() + "_grm", sourceProduct.getProductType(), sceneWidth, sceneHeight);
        targetProduct.setPreferredTileSize(tileSize);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);

        AbstractTileSegmenter tileSegmenter = buildTileSegmenter(mergingCostCriterion, regionMergingCriterion, totalIterationsForSecondSegmentation,
                                                                 threshold, spectralWeight, shapeWeight, targetProduct);

        long startTime = System.currentTimeMillis();
        AbstractTileSegmenter.logStartSegmentation(startTime, tileSegmenter);

        AbstractSegmenter segmenter = tileSegmenter.runSegmentationUsingThreads(sourceProduct, sourceBandNames);

        Band productTargetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        segmenter.fillBandData(productTargetBand);
        segmenter.getGraph().doClose();
        productTargetBand.getSourceImage();
        targetProduct.addBand(productTargetBand);

        AbstractTileSegmenter.logFinishSegmentation(startTime, tileSegmenter, segmenter);

        return targetProduct;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(GenericRegionMergingOp.class);
        }
    }
}
