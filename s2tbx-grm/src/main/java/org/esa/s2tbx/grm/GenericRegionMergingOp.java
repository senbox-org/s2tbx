package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.OutputMarkerMatrixHelper;
import org.esa.s2tbx.grm.segmentation.OutputMaskMatrixHelper;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.s2tbx.grm.segmentation.TileDataSourceImpl;
import org.esa.s2tbx.grm.segmentation.tiles.AbstractTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.BaatzSchapeTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.FullLambdaScheduleTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;
import org.esa.s2tbx.grm.segmentation.tiles.SegmentationSourceProductPair;
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
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.math.MathUtils;
import org.esa.snap.utils.matrix.IntMatrix;

import javax.media.jai.JAI;
import java.awt.*;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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

    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    @TargetProduct
    private Product targetProduct;

    private final AtomicInteger processingTileCount = new AtomicInteger(0);
    private final AtomicInteger processedTileCount = new AtomicInteger(0);
    private int totalTileCount;
    private final Set<String> processedTiles = new HashSet<>();

    private AbstractTileSegmenter tileSegmenter;
    private long startTime;
    private AbstractSegmenter segmenter;
    private ProductData grmData = null;

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
        int targetWidth = firstSelectedSourceBand.getRasterWidth();
        int targetHeight = firstSelectedSourceBand.getRasterHeight();
        for (int i=1; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            if (targetWidth != band.getRasterWidth() || targetHeight != band.getRasterHeight()) {
                throw new OperatorException("Please select the bands with the same resolution.");
            }
        }
        String productName = this.sourceProduct.getName() + "_grm";
        String productType = this.sourceProduct.getProductType();

        // At this stage all the bands have same resolutions (may be different from sourceProduct in case of multisize)
        this.targetProduct = new Product(productName, productType, targetWidth, targetHeight);
        this.targetProduct.setPreferredTileSize(JAI.getDefaultTileSize());

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, targetWidth, targetHeight);
        this.targetProduct.addBand(targetBand);

        ProductUtils.copyGeoCoding(firstSelectedSourceBand, this.targetProduct);

        initTiles();
    }

    private void computeGRMTiles() {
        int sceneWidth = this.targetProduct.getSceneRasterWidth();
        int sceneHeight = this.targetProduct.getSceneRasterHeight();
        Dimension tileSize = this.targetProduct.getPreferredTileSize();
        for (int y = 0; y < sceneHeight; y += tileSize.height) {
            int h = y + tileSize.height < sceneHeight ? tileSize.height : sceneHeight - y;
            for (int x = 0; x < sceneWidth; x += tileSize.width){
                int w = x + tileSize.width < sceneWidth ? tileSize.width : sceneWidth - x;
                Rectangle targetRectangle = new Rectangle(x, y, w, h);
                computeGRMTile(targetRectangle);
            }
        }
    }

    private void computeGRMTile(Rectangle targetRectangle){
        Dimension tileSize = this.targetProduct.getPreferredTileSize();
        int tileRowIndex = targetRectangle.y / tileSize.height;
        int tileColumnIndex = targetRectangle.x / tileSize.width;

        String key = tileRowIndex+"|"+tileColumnIndex;
        boolean canProcessTile;
        synchronized (this.processedTiles) {
            canProcessTile = this.processedTiles.add(key);
        }
        if (canProcessTile) {
            int startProcessingTileCount = this.processingTileCount.incrementAndGet();
            if (startProcessingTileCount == 1) {
                beforeProcessingFirstTile();
            }

            try {
                processTile(targetRectangle, tileRowIndex, tileColumnIndex);
            } catch (Exception ex) {
                throw new OperatorException(ex);
            } finally {
                synchronized (this.processedTileCount) {
                    int finishProcessingTileCount = this.processedTileCount.incrementAndGet();
                    if (finishProcessingTileCount == this.totalTileCount) {
                        try {
                            afterProcessedLastTile();
                            this.processedTileCount.notifyAll();
                        } catch (Exception e) {
                            throw new OperatorException(e);
                        }
                    }
                }
            }
        } else {
            // tile already computed
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Tile already computed: row index: "+tileRowIndex+", column index: "+tileColumnIndex+", bounds [x=" + targetRectangle.x+", y="+targetRectangle.y+", width="+targetRectangle.width+", height="+targetRectangle.height+"]");
            }
        }
    }

    /**
     * [SIITBX-434] Use computeTile() for writing the GRM output on targetTile.
     * Reason: If JAI execution threads < nr of processing tiles then deadlock occurs.
     *
     * @param targetBand The target band.
     * @param targetTile The current tile associated with the target band to be computed.
     * @param pm         A progress monitor which should be used to determine computation cancellation requests.
     * @throws OperatorException When error occurs
     */
    @Override
    public final void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        synchronized (this.processedTileCount) {
            if (this.processedTileCount.get() < 1) {
                computeGRMTiles();
            }
            if (this.processedTileCount.get() < this.totalTileCount) {
                try {
                    this.processedTileCount.wait();
                } catch (Exception e) {
                    throw new OperatorException(e);
                }
            }
        }
        Rectangle targetRectangle = targetTile.getRectangle();
        if(grmData != null) {
            Object grmDataElems = grmData.getElems();
            ProductData tileData = targetTile.getDataBuffer();
            Object tileDataElems = tileData.getElems();
            final int ws = targetBand.getRasterWidth();
            final int xs0 = targetRectangle.x;
            final int ys0 = targetRectangle.y;
            final int w = targetRectangle.width;
            final int h = targetRectangle.height;
            final int maxY = ys0 + h;
            final int tileStride = targetTile.getScanlineStride();
            for (int ys = ys0, yd = 0; ys < maxY; ++ys, ++yd) {
                int srcPos = ys * ws + xs0;
                int destPos = yd * tileStride;
                System.arraycopy(grmDataElems, srcPos, tileDataElems, destPos, w);
            }
        } else {
            throw new OperatorException("GRM compute failed.");
        }
    }

    public AbstractSegmenter getSegmenter() {
        return this.segmenter;
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

    private void beforeProcessingFirstTile() {
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

    private void afterProcessedLastTile() throws Exception {
        this.segmenter = this.tileSegmenter.runSecondSegmentationsAndMergeGraphs();

        OutputMaskMatrixHelper outputMaskMatrixHelper = this.segmenter.buildOutputMaskMatrixHelper();

        OutputMarkerMatrixHelper outputMarkerMatrix = outputMaskMatrixHelper.buildMaskMatrix();

        outputMaskMatrixHelper.doClose();
        WeakReference<OutputMaskMatrixHelper> referenceMaskMatrix = new WeakReference<OutputMaskMatrixHelper>(outputMaskMatrixHelper);
        referenceMaskMatrix.clear();

        grmData = outputMarkerMatrix.buildOutputProductData();
        int graphNodeCount = outputMarkerMatrix.getGraphNodeCount();

        outputMarkerMatrix.doClose();
        WeakReference<OutputMarkerMatrixHelper> referenceMarkerMatrix = new WeakReference<OutputMarkerMatrixHelper>(outputMarkerMatrix);
        referenceMarkerMatrix.clear();

        if (logger.isLoggable(Level.FINE)) {
            int imageWidth = tileSegmenter.getImageWidth();
            int imageHeight = tileSegmenter.getImageHeight();
            int tileWidth = tileSegmenter.getTileWidth();
            int tileHeight = tileSegmenter.getTileHeight();
            int tileMargin = tileSegmenter.computeTileMargin();

            long finishTime = System.currentTimeMillis();
            long totalSeconds = (finishTime - this.startTime) / 1000;
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Finish Segmentation: image width: " + imageWidth + ", image height: " + imageHeight + ", tile width: " + tileWidth + ", tile height: " + tileHeight + ", margin: " + tileMargin + ", graph node count: " + graphNodeCount + ", total seconds: " + totalSeconds + ", finish time: " + new Date(finishTime));
        }
    }

    private void processTile(Rectangle targetRectangle, int tileRowIndex, int tileColumnIndex) throws Exception {
        ProcessingTile currentTile = this.tileSegmenter.buildTile(targetRectangle.x, targetRectangle.y, targetRectangle.width, targetRectangle.height);
        TileDataSource[] sourceTiles = getSourceTiles(currentTile.getRegion());
        this.tileSegmenter.runTileFirstSegmentation(sourceTiles, currentTile, tileRowIndex, tileColumnIndex);
    }

    private final void initTiles() {

        int sceneWidth = this.targetProduct.getSceneRasterWidth();
        int sceneHeight = this.targetProduct.getSceneRasterHeight();
        Dimension tileSize = this.targetProduct.getPreferredTileSize();

        int tileCountX = MathUtils.ceilInt(sceneWidth / (double) tileSize.width);
        int tileCountY = MathUtils.ceilInt(sceneHeight / (double) tileSize.height);
        this.totalTileCount = tileCountX * tileCountY;

        int threadCount = 1;
        Executor threadPool = Executors.newCachedThreadPool();

        String folderPath = System.getProperty("grm.temp.folder.path");
        if (folderPath == null) {
            folderPath = System.getProperty("java.io.tmpdir");
        }
        Path temporaryParentFolder = Paths.get(folderPath);

        RegionMergingProcessingParameters processingParameters = new RegionMergingProcessingParameters(threadCount, threadPool, sceneWidth, sceneHeight,
                tileSize.width, tileSize.height);

        RegionMergingInputParameters inputParameters = new RegionMergingInputParameters(mergingCostCriterion, regionMergingCriterion, totalIterationsForSecondSegmentation,
                threshold, spectralWeight, shapeWeight);

        try {
            this.tileSegmenter = buildTileSegmenter(processingParameters, inputParameters, temporaryParentFolder);
        } catch (IOException e) {
            throw new OperatorException(e);
        }
    }

    private TileDataSource[] getSourceTiles(BoundingBox tileRegion) {
        TileDataSource[] sourceTiles = new TileDataSource[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            sourceTiles[i] = new TileDataSourceImpl(getSourceTile(band, rectangleToRead));
        }
        return sourceTiles;
    }

    private static AbstractTileSegmenter buildTileSegmenter(RegionMergingProcessingParameters processingParameters, RegionMergingInputParameters inputParameters,
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

    private static void logStartSegmentation(long startTime, int imageWidth, int imageHeight, int tileWidth, int tileHeight, int threadCount) {
        if (logger.isLoggable(Level.FINE)) {
            int tileMargin = AbstractTileSegmenter.computeTileMargin(tileWidth, tileHeight);
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start Segmentation: image width: " + imageWidth + ", image height: " + imageHeight + ", tile width: " + tileWidth + ", tile height: " + tileHeight + ", margin: " + tileMargin + ", thread count: " + threadCount + ", start time: " + new Date(startTime));
        }
    }

    private static void logFinishSegmentation(long startTime, int imageWidth, int imageHeight, int tileWidth, int tileHeight, int graphNodeCount) {
        if (logger.isLoggable(Level.FINE)) {
            long finishTime = System.currentTimeMillis();
            long totalSeconds = (finishTime - startTime) / 1000;
            int tileMargin = AbstractTileSegmenter.computeTileMargin(tileWidth, tileHeight);
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Finish Segmentation: image width: " +imageWidth+", image height: "+imageHeight+", tile width: "+tileWidth+", tile height: "+tileHeight+", margin: "+tileMargin+", graph node count: "+graphNodeCount+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
        }
    }

    private static OutputMaskMatrixHelper computeOutputMaskMatrix(RegionMergingProcessingParameters processingParameters, RegionMergingInputParameters inputParameters,
                                                                  SegmentationSourceProductPair segmentationSourceProducts, Path temporaryParentFolder)
                                                                  throws Exception {

        AbstractTileSegmenter tileSegmenter = buildTileSegmenter(processingParameters, inputParameters, temporaryParentFolder);
        tileSegmenter.runFirstSegmentationsInParallel(segmentationSourceProducts);
        AbstractSegmenter segmenter = tileSegmenter.runSecondSegmentationsAndMergeGraphs();

        tileSegmenter.doClose();
        WeakReference<AbstractTileSegmenter> referenceTileSegmenter = new WeakReference<AbstractTileSegmenter>(tileSegmenter);
        referenceTileSegmenter.clear();

        OutputMaskMatrixHelper outputMaskMatrixHelper = segmenter.buildOutputMaskMatrixHelper();

        segmenter.doClose();
        WeakReference<AbstractSegmenter> referenceSegmenter = new WeakReference<AbstractSegmenter>(segmenter);
        referenceSegmenter.clear();

        return outputMaskMatrixHelper;
    }

    private static OutputMarkerMatrixHelper computeOutputMarkerMatrix(RegionMergingProcessingParameters processingParameters, RegionMergingInputParameters inputParameters,
                                                                      SegmentationSourceProductPair segmentationSourceProducts, Path temporaryParentFolder)
                                                                      throws Exception {

        OutputMaskMatrixHelper outputMaskMatrixHelper = computeOutputMaskMatrix(processingParameters, inputParameters, segmentationSourceProducts, temporaryParentFolder);

        OutputMarkerMatrixHelper outputMarkerMatrix = outputMaskMatrixHelper.buildMaskMatrix();

        outputMaskMatrixHelper.doClose();
        WeakReference<OutputMaskMatrixHelper> referenceMaskMatrix = new WeakReference<OutputMaskMatrixHelper>(outputMaskMatrixHelper);
        referenceMaskMatrix.clear();

        return outputMarkerMatrix;
    }

    public static IntMatrix computeSegmentation(RegionMergingProcessingParameters processingParameters, RegionMergingInputParameters inputParameters,
                                                SegmentationSourceProductPair segmentationSourceProducts, Path temporaryParentFolder)
                                                throws Exception {

        long startTime = System.currentTimeMillis();

        // log the start message
        logStartSegmentation(startTime, processingParameters.getImageWidth(), processingParameters.getImageHeight(),
                             processingParameters.getTileWidth(), processingParameters.getTileHeight(), processingParameters.getThreadCount());

        OutputMarkerMatrixHelper outputMarkerMatrix = computeOutputMarkerMatrix(processingParameters, inputParameters, segmentationSourceProducts, temporaryParentFolder);

        IntMatrix result = outputMarkerMatrix.buildOutputMatrix();
        int graphNodeCount = outputMarkerMatrix.getGraphNodeCount();

        outputMarkerMatrix.doClose();
        WeakReference<OutputMarkerMatrixHelper> referenceMarkerMatrix = new WeakReference<OutputMarkerMatrixHelper>(outputMarkerMatrix);
        referenceMarkerMatrix.clear();

        // log the final message
        logFinishSegmentation(startTime, result.getColumnCount(), result.getRowCount(), processingParameters.getTileWidth(), processingParameters.getTileHeight(), graphNodeCount);

        return result;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(GenericRegionMergingOp.class);
        }
    }
}
