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
import org.esa.snap.utils.AbstractTilesComputingOp;

import javax.media.jai.JAI;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Jean Coravu
 */
public abstract class AbstractRegionMergingOp extends AbstractTilesComputingOp {
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

    protected AbstractTileSegmenter tileSegmenter;
    protected long startTime;

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
    }

    protected abstract TileDataSource[] getSourceTiles(BoundingBox tileRegion);

    @Override
    protected void beforeProcessingFirstTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) {
        this.startTime = System.currentTimeMillis();
        if (logger.isLoggable(Level.FINE)) {
            int imageWidth = this.tileSegmenter.getImageWidth();
            int imageHeight = this.tileSegmenter.getImageHeight();
            int tileWidth = this.tileSegmenter.getTileWidth();
            int tileHeight = this.tileSegmenter.getTileHeight();
            int tileMargin = this.tileSegmenter.computeTileMargin();
            int firstNumberOfIterations = this.tileSegmenter.computeIterationsForEachFirstSegmentation();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start Segmentation: image width: " + imageWidth + ", image height: " + imageHeight + ", tile width: " + tileWidth + ", tile height: " + tileHeight + ", margin: " + tileMargin + ", first number of iterations: " + firstNumberOfIterations + ", start time: " + new Date(startTime));
        }
    }

    @Override
    protected void afterProcessedLastTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
        AbstractSegmenter segmenter = this.tileSegmenter.runSecondSegmentationsAndMergeGraphs();

        Band productTargetBand = this.targetProduct.getBandAt(0);
        productTargetBand.setSourceImage(null); // reset the source image
        segmenter.fillBandData(productTargetBand);
        productTargetBand.getSourceImage();

        if (logger.isLoggable(Level.FINE)) {
            int imageWidth = tileSegmenter.getImageWidth();
            int imageHeight = tileSegmenter.getImageHeight();
            int tileWidth = tileSegmenter.getTileWidth();
            int tileHeight = tileSegmenter.getTileHeight();
            int tileMargin = tileSegmenter.computeTileMargin();

            long finishTime = System.currentTimeMillis();
            long totalSeconds = (finishTime - this.startTime) / 1000;
            int graphNodeCount = segmenter.getGraph().getNodeCount();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Finish Segmentation: image width: " + imageWidth + ", image height: " + imageHeight + ", tile width: " + tileWidth + ", tile height: " + tileHeight + ", margin: " + tileMargin + ", graph node count: " + graphNodeCount + ", total seconds: " + totalSeconds + ", finish time: " + new Date(finishTime));
        }

        finishSegmentation(segmenter);
    }

    @Override
    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
        Rectangle targetRectangle = targetTile.getRectangle();
        ProcessingTile currentTile = this.tileSegmenter.buildTile(targetRectangle.x, targetRectangle.y, targetRectangle.width, targetRectangle.height);
        TileDataSource[] sourceTiles = getSourceTiles(currentTile.getRegion());
        this.tileSegmenter.runTileFirstSegmentation(sourceTiles, currentTile, tileRowIndex, tileColumnIndex);
    }

    @Override
    protected void initTargetProduct(int sceneWidth, int sceneHeight, String productName, String productType, String bandName, int bandDataType) {
        super.initTargetProduct(sceneWidth, sceneHeight, productName, productType, bandName, bandDataType);

        //TODO Jean uncomment
//        Dimension imageSize = new Dimension(this.targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());
//        Dimension tileSize = this.targetProduct.getPreferredTileSize();
//        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
//        Executor threadPool = Executors.newCachedThreadPool();
//        try {
//            this.tileSegmenter = buildTileSegmenter(threadCount, threadPool, mergingCostCriterion, regionMergingCriterion, totalIterationsForSecondSegmentation,
//                                                    this.threshold, spectralWeight, shapeWeight, imageSize, tileSize);
//        } catch (IOException e) {
//            throw new OperatorException(e);
//        }
    }

    protected void finishSegmentation(AbstractSegmenter segmenter) {
    }

    protected static AbstractTileSegmenter buildTileSegmenter(RegionMergingProcessingParameters processingParameters, RegionMergingInputParameters inputParameters,
                                                              Path temporaryParentFolder)
                                                              throws IOException {

        AbstractTileSegmenter tileSegmenter = null;
        boolean fastSegmentation = false;
        if (GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(inputParameters.getRegionMergingCriterion())) {
            fastSegmentation = true;
        } else if (GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(inputParameters.getRegionMergingCriterion())) {
            fastSegmentation = false;
        }
        if (GenericRegionMergingOp.SPRING_MERGING_COST_CRITERION.equalsIgnoreCase(inputParameters.getMergingCostCriterion())) {
            tileSegmenter = new SpringTileSegmenter(processingParameters, inputParameters.getTotalIterationsForSecondSegmentation(),
                                                    inputParameters.getThreshold(), fastSegmentation, temporaryParentFolder);

        } else if (GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(inputParameters.getMergingCostCriterion())) {
            tileSegmenter = new BaatzSchapeTileSegmenter(processingParameters, inputParameters.getTotalIterationsForSecondSegmentation(),
                                                         inputParameters.getThreshold(), fastSegmentation, inputParameters.getSpectralWeight(),
                                                         inputParameters.getShapeWeight(), temporaryParentFolder);

        } else if (GenericRegionMergingOp.FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION.equalsIgnoreCase(inputParameters.getMergingCostCriterion())) {
            tileSegmenter = new FullLambdaScheduleTileSegmenter(processingParameters, inputParameters.getTotalIterationsForSecondSegmentation(),
                                                                inputParameters.getThreshold(), fastSegmentation, temporaryParentFolder);

        } else {
            throw new IllegalArgumentException("Unknown merging cost criterion '" + inputParameters.getMergingCostCriterion() + "'.");
        }
        return tileSegmenter;
    }

    protected static void logStartSegmentation(long startTime, int imageWidth, int imageHeight, int tileWidth, int tileHeight, int threadCount) {
        if (logger.isLoggable(Level.FINE)) {
            int tileMargin = AbstractTileSegmenter.computeTileMargin(tileWidth, tileHeight);
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start Segmentation: image width: " + imageWidth + ", image height: " + imageHeight + ", tile width: " + tileWidth + ", tile height: " + tileHeight + ", margin: " + tileMargin + ", thread count: " + threadCount + ", start time: " + new Date(startTime));
        }
    }

    protected static void logFinishSegmentation(long startTime, int imageWidth, int imageHeight, int tileWidth, int tileHeight, int graphNodeCount) {
        if (logger.isLoggable(Level.FINE)) {
            long finishTime = System.currentTimeMillis();
            long totalSeconds = (finishTime - startTime) / 1000;
            int tileMargin = AbstractTileSegmenter.computeTileMargin(tileWidth, tileHeight);
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Finish Segmentation: image width: " +imageWidth+", image height: "+imageHeight+", tile width: "+tileWidth+", tile height: "+tileHeight+", margin: "+tileMargin+", graph node count: "+graphNodeCount+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
        }
    }
}
