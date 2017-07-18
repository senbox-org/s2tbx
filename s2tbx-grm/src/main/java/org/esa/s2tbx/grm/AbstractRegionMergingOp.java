package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.s2tbx.grm.segmentation.tiles.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.math.MathUtils;

import javax.media.jai.JAI;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Jean Coravu
 */
public abstract class AbstractRegionMergingOp extends Operator {
    private static final Logger logger = Logger.getLogger(AbstractRegionMergingOp.class.getName());

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
    protected String mergingCostCriterion;

    @Parameter(label = "Region merging criterion",
            defaultValue = LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION,
            description = "The method to check the region merging.",
            valueSet = {BEST_FITTING_REGION_MERGING_CRITERION, LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION})
    protected String regionMergingCriterion;

    @Parameter(defaultValue = DEFAULT_TOTAL_NUMBER_OF_ITERATIONS, label = "Total iterations", description = "The total number of iterations.")
    protected int totalIterationsForSecondSegmentation;

    @Parameter(defaultValue = DEFAULT_THRESHOLD, label = "Threshold", description = "The threshold.")
    protected float threshold;

    @Parameter(defaultValue = DEFAULT_SPECTRAL_WEIGHT, label = "Spectral weight", description = "The spectral weight.")
    protected float spectralWeight;

    @Parameter(defaultValue = DEFAULT_SHAPE_WEIGHT, label = "Shape weight", description = "The shape weight.")
    protected float shapeWeight;

    @TargetProduct
    protected Product targetProduct;

    protected AbstractTileSegmenter tileSegmenter;
    protected long startTime;
    protected AtomicInteger processingTiles;
    protected AtomicInteger processedTiles;
    protected int totalTileCount;
    protected AbstractSegmenter segmenter;

    protected AbstractRegionMergingOp() {
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

        //TODO Jean remove
        Logger logger = Logger.getLogger("org.esa.s2tbx.grm");
        logger.setLevel(Level.FINE);
    }

    protected abstract TileDataSource[] getSourceTiles(BoundingBox tileRegion);

    @Override
    public final void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
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
                this.segmenter = this.tileSegmenter.runSecondSegmentationsAndMergeGraphs();
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

    protected final void initTargetProduct(int sceneWidth, int sceneHeight, String productName, String productType) {
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(productName, productType, sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
    }

    protected final void executeFirstTileSegmentation(Rectangle targetRectangle, int totalTileCount) throws OperatorException {
        try {
            ProcessingTile currentTile = this.tileSegmenter.buildTile(targetRectangle.x, targetRectangle.y, targetRectangle.width, targetRectangle.height);
            TileDataSource[] sourceTiles = getSourceTiles(currentTile.getRegion());
            try {
                this.tileSegmenter.runTileFirstSegmentation(sourceTiles, currentTile);
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

    protected final void initTiles() {
        this.processingTiles = new AtomicInteger(0);
        this.processedTiles = new AtomicInteger(0);

        int sceneWidth = this.targetProduct.getSceneRasterWidth();
        int sceneHeight = this.targetProduct.getSceneRasterHeight();
        Dimension imageSize = new Dimension(this.targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());
        Dimension tileSize = this.targetProduct.getPreferredTileSize();

        int tileCountX = MathUtils.ceilInt(sceneWidth / (double) tileSize.width);
        int tileCountY = MathUtils.ceilInt(sceneHeight / (double) tileSize.height);
        this.totalTileCount = tileCountX * tileCountY;

        try {
            int threadCount = Runtime.getRuntime().availableProcessors();
            Executor threadPool = Executors.newCachedThreadPool();
            this.tileSegmenter = buildTileSegmenter(threadCount, threadPool, mergingCostCriterion, regionMergingCriterion, totalIterationsForSecondSegmentation,
                    threshold, spectralWeight, shapeWeight, imageSize, tileSize);
        } catch (IOException e) {
            throw new OperatorException(e);
        }
    }

    protected static AbstractTileSegmenter buildTileSegmenter(int threadCount, Executor threadPool, String mergingCostCriterion, String regionMergingCriterion,
                                                              int totalIterationsForSecondSegmentation, float threshold, float spectralWeight,
                                                              float shapeWeight, Dimension imageSize, Dimension tileSize)
            throws IOException {

        AbstractTileSegmenter tileSegmenter = null;
        boolean fastSegmentation = false;
        if (GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(regionMergingCriterion)) {
            fastSegmentation = true;
        } else if (GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(regionMergingCriterion)) {
            fastSegmentation = false;
        }
        if (GenericRegionMergingOp.SPRING_MERGING_COST_CRITERION.equalsIgnoreCase(mergingCostCriterion)) {
            tileSegmenter = new SpringTileSegmenter(threadCount, threadPool, imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation);
        } else if (GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(mergingCostCriterion)) {
            tileSegmenter = new BaatzSchapeTileSegmenter(threadCount, threadPool, imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation, spectralWeight, shapeWeight);
        } else if (GenericRegionMergingOp.FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION.equalsIgnoreCase(mergingCostCriterion)) {
            tileSegmenter = new FullLambdaScheduleTileSegmenter(threadCount, threadPool, imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation);
        } else {
            throw new IllegalArgumentException("Unknown merging cost criterion '" + mergingCostCriterion + "'.");
        }
        return tileSegmenter;
    }
}
