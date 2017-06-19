package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.GraphDataSource;
import org.esa.s2tbx.grm.segmentation.tiles.AbstractTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.BaatzSchapeTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.FullLambdaScheduleTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;
import org.esa.s2tbx.grm.segmentation.tiles.SpringTileSegmenter;
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
import org.esa.snap.core.util.math.MathUtils;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Jean Coravu
 */
public abstract class AbstractGenericRegionMergingOp extends Operator {
    private static final Logger logger = Logger.getLogger(AbstractGenericRegionMergingOp.class.getName());

    public static final String SPRING_MERGING_COST_CRITERION = "Spring";
    public static final String BAATZ_SCHAPE_MERGING_COST_CRITERION = "Baatz & Schape";
    public static final String FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION = "Full Lamda Schedule";
    public static final String BEST_FITTING_REGION_MERGING_CRITERION = "Best Fitting";
    public static final String LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION = "Local Mutual Best Fitting";

    @Parameter(label = "Merging cost criterion",
            description = "The method to compute the region merging.",
            valueSet = {SPRING_MERGING_COST_CRITERION, BAATZ_SCHAPE_MERGING_COST_CRITERION, FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION})
    private String mergingCostCriterion;

    @Parameter(label = "Region merging criterion",
            description = "The method to check the region merging.",
            valueSet = {BEST_FITTING_REGION_MERGING_CRITERION, LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION})
    private String regionMergingCriterion;

    @Parameter(label = "Total iterations", description = "The total number of iterations.")
    private int totalIterationsForSecondSegmentation;

    @Parameter(label = "Threshold", description = "The threshold.")
    private float threshold;

    @Parameter(label = "Spectral weight", description = "The spectral weight.")
    private float spectralWeight;

    @Parameter(label = "Shape weight", description = "The shape weight.")
    private float shapeWeight;

    @TargetProduct
    private Product targetProduct;

    private AbstractTileSegmenter tileSegmenter;
    private long startTime;
    private AtomicInteger processingTiles;
    private AtomicInteger processedTiles;
    private int totalTileCount;
    private AbstractSegmenter segmenter;

    protected AbstractGenericRegionMergingOp() {
    }

    protected abstract void validateSourceBandNames();

    protected abstract int getTargetSceneRasterWidth();

    protected abstract int getTargetSceneRasterHeight();

    protected abstract String getTargetProductName();

    protected abstract String getTargetProductType();

    protected abstract GraphDataSource[] getSourceTiles(BoundingBox tileRegion);

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

        validateSourceBandNames();

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
            createTileSegmenter();
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

    private void createTargetProduct() {
        int sceneWidth = getTargetSceneRasterWidth();
        int sceneHeight = getTargetSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(getTargetProductName() + "_grm", getTargetProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
    }

    private void createTileSegmenter() throws IOException {
        boolean fastSegmentation = false;
        if (BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(this.regionMergingCriterion)) {
            fastSegmentation = true;
        } else if (BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(this.regionMergingCriterion)) {
            fastSegmentation = false;
        }
        Dimension imageSize = new Dimension(this.targetProduct.getSceneRasterWidth(), this.targetProduct.getSceneRasterHeight());
        Dimension tileSize = this.targetProduct.getPreferredTileSize();
        if (SPRING_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
            this.tileSegmenter = new SpringTileSegmenter(imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation);
        } else if (BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
            this.tileSegmenter = new BaatzSchapeTileSegmenter(imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation, spectralWeight, shapeWeight);
        } else if (FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
            this.tileSegmenter = new FullLambdaScheduleTileSegmenter(imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation);
        }
    }

    private void executeFirstTileSegmentation(Rectangle targetRectangle, int totalTileCount) throws OperatorException {
        try {
            ProcessingTile currentTile = this.tileSegmenter.buildTile(targetRectangle.x, targetRectangle.y, targetRectangle.width, targetRectangle.height);
            GraphDataSource[] sourceTiles = getSourceTiles(currentTile.getRegion());
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
}
