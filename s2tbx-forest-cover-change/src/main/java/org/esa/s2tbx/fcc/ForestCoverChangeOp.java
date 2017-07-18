package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.s2tbx.fcc.common.AveragePixelsSourceBands;
import org.esa.s2tbx.fcc.common.BandsExtractorOp;
import org.esa.s2tbx.fcc.common.PixelSourceBands;
import org.esa.s2tbx.fcc.trimming.*;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.grm.DifferencePixelsRegionMergingOp;
import org.esa.s2tbx.grm.GenericRegionMergingOp;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.util.ProductUtils;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "ForestCoverChangeOp",
        version="1.0",
        category = "Raster",
        description = "Generates Forest Cover Change product from L2a Sentinel 2 products ",
        authors = "Razvan Dumitrascu, Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class ForestCoverChangeOp extends Operator {
    private static final Logger logger = Logger.getLogger(ForestCoverChangeOp.class.getName());

    @SourceProduct(alias = "Current Source Product", description = "The source product to be modified.")
    private Product currentSourceProduct;
    @SourceProduct(alias = "Previous Source Product", description = "The source product to be modified.")
    private Product previousSourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(defaultValue = "95.0", label = "Forest cover percentage", itemAlias = "percentage", description = "Specifies the percentage of forest cover per segment")
    private float forestCoverPercentage;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Merging cost criterion",
            defaultValue = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION,
            description = "The method to compute the region merging.",
            valueSet = {GenericRegionMergingOp.SPRING_MERGING_COST_CRITERION, GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION, GenericRegionMergingOp.FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION})
    private String mergingCostCriterion;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Region merging criterion",
            defaultValue = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION,
            description = "The method to check the region merging.",
            valueSet = {GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION, GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION})
    private String regionMergingCriterion;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Total iterations",
            defaultValue = "10",
            description = "The total number of iterations.")
    private int totalIterationsForSecondSegmentation;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Threshold", defaultValue = "5.0", description = "The threshold.")
    private float threshold;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Spectral weight", defaultValue = "0.5", description = "The spectral weight.")
    private float spectralWeight;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Shape weight", defaultValue = "0.5" , description = "The shape weight.")
    private float shapeWeight;

    private String[] currentProductBandsNames;
    private String[] previousProductBandsNames;

    public ForestCoverChangeOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        validateSourceProducts();

        this.currentProductBandsNames = findBandNames(this.currentSourceProduct);
        this.previousProductBandsNames = findBandNames(this.previousSourceProduct);

        int sceneWidth = this.currentSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.currentSourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product("ForestCoverChange", this.currentSourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        ProductUtils.copyGeoCoding(this.currentSourceProduct, this.targetProduct);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        //TODO Jean remove
        Logger logger = Logger.getLogger("org.esa.s2tbx.fcc");
        logger.setLevel(Level.FINE);
    }

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        long startTime = System.currentTimeMillis();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start Forest Cover Change: imageWidth: "+this.targetProduct.getSceneRasterWidth()+", imageHeight: "+this.targetProduct.getSceneRasterHeight() + ", start time: " + new Date(startTime));
        }

        // reset the source image of the target product
        this.targetProduct.getBandAt(0).setSourceImage(null);

        Dimension tileSize = JAI.getDefaultTileSize();
        int[] trimmingSourceProductBandIndices = new int[] {0, 1, 2};
        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            ProductTrimmingResult currentResult = runTrimming(threadCount, threadPool, this.currentSourceProduct, this.currentProductBandsNames,
                                                              trimmingSourceProductBandIndices, tileSize);
            Product currentProduct = currentResult.getProduct();
            IntSet currentSegmentationTrimmingRegionKeys = currentResult.getTrimmingRegionKeys();
            Product currentProductColorFill = currentResult.getSegmentationProductColorFill();

            // reset the reference
            currentResult = null;

            ProductTrimmingResult previousResult = runTrimming(threadCount, threadPool, this.previousSourceProduct, this.previousProductBandsNames,
                                                               trimmingSourceProductBandIndices, tileSize);
            Product previousProduct = previousResult.getProduct();
            IntSet previousSegmentationTrimmingRegionKeys = previousResult.getTrimmingRegionKeys();
            Product previousProductColorFill = previousResult.getSegmentationProductColorFill();

            // reset the reference
            previousResult = null;

            Product unionMaskProduct = runUnionMasksOp(threadCount, threadPool, currentSegmentationTrimmingRegionKeys, currentProductColorFill,
                                                       previousSegmentationTrimmingRegionKeys, previousProductColorFill, tileSize);

            // reset the references
            currentSegmentationTrimmingRegionKeys = null;
            currentProductColorFill = null;
            previousSegmentationTrimmingRegionKeys = null;
            previousProductColorFill = null;

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Start segmentation for difference bands.");
            }

            Product differenceSegmentationProduct = DifferencePixelsRegionMergingOp.runSegmentation(threadCount, threadPool, currentProduct, this.currentProductBandsNames,
                                                                            previousProduct, this.previousProductBandsNames, mergingCostCriterion,
                                                                            regionMergingCriterion, totalIterationsForSecondSegmentation, threshold,
                                                                            spectralWeight, shapeWeight);

            // reset the references
            currentProduct = null;
            previousProduct = null;

            IntSet differenceTrimmingSet = computeDifferenceTrimmingSet(threadCount, threadPool, differenceSegmentationProduct,
                    unionMaskProduct, trimmingSourceProductBandIndices, tileSize);

            runFinalMaskOp(threadCount, threadPool, differenceSegmentationProduct, unionMaskProduct, differenceTrimmingSet, tileSize);

            if (logger.isLoggable(Level.FINE)) {
                long finishTime = System.currentTimeMillis();
                long totalSeconds = (finishTime - startTime) / 1000;
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Finish Forest Cover Change: imageWidth: "+this.targetProduct.getSceneRasterWidth()+", imageHeight: "+this.targetProduct.getSceneRasterHeight()+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
            }
        } catch (Exception ex) {
            throw new OperatorException(ex);
        } finally {
            threadPool.shutdown();
        }
    }

    private IntSet computeDifferenceTrimmingSet(int threadCount, Executor threadPool, Product differenceSegmentationProduct,
                                                Product unionMaskProduct, int[] sourceBandIndices, Dimension tileSize)
                                                throws Exception {

        DifferenceRegionComputingHelper helper = new DifferenceRegionComputingHelper(differenceSegmentationProduct, currentSourceProduct, previousSourceProduct,
                                                                                     unionMaskProduct, sourceBandIndices, tileSize.width, tileSize.height);
        IntSet differenceTrimmingSet = helper.runTilesInParallel(threadCount, threadPool);

        helper = null;
        System.gc();

        return differenceTrimmingSet;
    }

    private ProductTrimmingResult runTrimming(int threadCount, Executor threadPool, Product sourceProduct,
                                              String[] sourceBandNames, int[] trimmingSourceProductBandIndices, Dimension tileSize)
                                              throws Exception {

        Product product = generateBandsExtractor(sourceProduct, sourceBandNames);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start generate color fill for source product '" + sourceProduct.getName()+"'");
        }

        Product productColorFill = generateColorFill(threadCount, threadPool, product, sourceBandNames, tileSize);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start trimming for source product '" + sourceProduct.getName()+"'");
        }

        TrimmingRegionComputingHelper helper = new TrimmingRegionComputingHelper(productColorFill, product, trimmingSourceProductBandIndices,
                                                                                 tileSize.width, tileSize.height);
        IntSet segmentationTrimmingRegionKeys = helper.runTilesInParallel(threadCount, threadPool);

        helper = null;
        System.gc();

        return new ProductTrimmingResult(product, segmentationTrimmingRegionKeys, productColorFill);
    }

    private Product generateColorFill(int threadCount, Executor threadPool, Product sourceProduct, String[] sourceBandNames, Dimension tileSize)
                                      throws Exception {

        Product segmentationProduct = GenericRegionMergingOp.runSegmentation(threadCount, threadPool, sourceProduct, sourceBandNames, mergingCostCriterion,
                                                    regionMergingCriterion, totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

        return runColorFillerOp(threadCount, threadPool, segmentationProduct, forestCoverPercentage, tileSize);
    }

    private static Product runColorFillerOp(int threadCount, Executor threadPool, Product segmentationSourceProduct,
                                            float percentagePixels, Dimension tileSize)
                                            throws Exception {

        IntSet validRegions = runObjectsSelectionOp(threadCount, threadPool, segmentationSourceProduct, percentagePixels, tileSize);

        ColorFillerHelper helper = new ColorFillerHelper(segmentationSourceProduct, validRegions, tileSize.width, tileSize.height);
        return helper.runTilesInParallel(threadCount, threadPool);
    }

    private static IntSet runObjectsSelectionOp(int threadCount, Executor threadPool, Product sourceProduct, float percentagePixels, Dimension tileSize)
                                                throws Exception {

        Product landCover = buildLandCoverProduct(sourceProduct);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("landCoverNames", ForestCoverChangeConstants.LAND_COVER_NAME);
        Product landCoverProduct = GPF.createProduct("AddLandCover", parameters, landCover);

        ObjectsSelectionHelper helper = new ObjectsSelectionHelper(sourceProduct, landCoverProduct, tileSize.width, tileSize.height);
        Int2ObjectMap<ObjectsSelectionOp.PixelStatistic> statistics = helper.runTilesInParallel(threadCount, threadPool);

        IntSet validRegions = new IntOpenHashSet();
        ObjectIterator<Int2ObjectMap.Entry<ObjectsSelectionOp.PixelStatistic>> it = statistics.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<ObjectsSelectionOp.PixelStatistic> entry = it.next();
            ObjectsSelectionOp.PixelStatistic value = entry.getValue();
            float percent = ((float)value.getPixelsInRange()/(float)value.getTotalNumberPixels()) * 100;
            if (percent >= percentagePixels) {
                validRegions.add(entry.getIntKey());
            }
        }
        return validRegions;
    }

    private static Product buildLandCoverProduct(Product sourceProduct) {
        Product landCoverProduct = new Product(sourceProduct.getName(), sourceProduct.getProductType(),
                sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        landCoverProduct.setStartTime(sourceProduct.getStartTime());
        landCoverProduct.setEndTime(sourceProduct.getEndTime());
        landCoverProduct.setNumResolutionsMax(sourceProduct.getNumResolutionsMax());

        ProductUtils.copyMetadata(sourceProduct, landCoverProduct);
        ProductUtils.copyGeoCoding(sourceProduct, landCoverProduct);
        ProductUtils.copyTiePointGrids(sourceProduct, landCoverProduct);
        ProductUtils.copyVectorData(sourceProduct, landCoverProduct);
        return landCoverProduct;
    }

    private static Product generateBandsExtractor(Product sourceProduct, String[] sourceBandNames) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Extract "+sourceBandNames.length+" bands for source product '" + sourceProduct.getName()+"'");
        }

        Product targetProduct = BandsExtractorOp.extractBands(sourceProduct, sourceBandNames);
        return resampleAllBands(targetProduct);
    }

    private static Product resampleAllBands(Product sourceProduct) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Resample the bands for source product '" + sourceProduct.getName()+"'");
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("targetWidth", sourceProduct.getSceneRasterWidth());
        parameters.put("targetHeight", sourceProduct.getSceneRasterHeight());
        Product targetProduct = GPF.createProduct("Resample", parameters, sourceProduct);
        targetProduct.setName(sourceProduct.getName());
        return targetProduct;
    }

    private static Product runUnionMasksOp(int threadCount, Executor threadPool, IntSet currentSegmentationTrimmingRegionKeys,
                                           Product currentSegmentationSourceProduct, IntSet previousSegmentationTrimmingRegionKeys,
                                           Product previousSegmentationSourceProduct, Dimension tileSize)
                                           throws Exception {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start running union mask");
        }

        UnionMasksHelper helper = new UnionMasksHelper(currentSegmentationSourceProduct, previousSegmentationSourceProduct, currentSegmentationTrimmingRegionKeys,
                                                       previousSegmentationTrimmingRegionKeys, tileSize.width, tileSize.height);
        ProductData productData = helper.runTilesInParallel(threadCount, threadPool);
        int sceneRasterWidth = currentSegmentationSourceProduct.getSceneRasterWidth();
        int sceneRasterHeight = currentSegmentationSourceProduct.getSceneRasterHeight();
        Product targetProduct = new Product("forestCoverChange", currentSegmentationSourceProduct.getProductType(), sceneRasterWidth, sceneRasterHeight);
        targetProduct.setPreferredTileSize(tileSize);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneRasterWidth, sceneRasterHeight);
        targetBand.setData(productData);
        targetProduct.addBand(targetBand);
        return targetProduct;
    }

    private Product runFinalMaskOp(int threadCount, Executor threadPool, Product differenceSegmentationProduct,
                                   Product unionMaskProduct, IntSet differenceTrimmingSet, Dimension tileSize)
                                   throws Exception {

        FinalMasksHelper helper = new FinalMasksHelper(differenceSegmentationProduct, unionMaskProduct, differenceTrimmingSet, tileSize.width, tileSize.height);
        ProductData productData = helper.runTilesInParallel(threadCount, threadPool);
        this.targetProduct.getBandAt(0).setData(productData);
        return this.targetProduct;
    }

    private static class ProductTrimmingResult {
        private final Product product;
        private final IntSet trimmingRegionKeys;
        private final Product segmentationProductColorFill;

        ProductTrimmingResult(Product product, IntSet trimmingRegionKeys, Product segmentationProductColorFill) {
            this.product = product;
            this.trimmingRegionKeys = trimmingRegionKeys;
            this.segmentationProductColorFill = segmentationProductColorFill;
        }

        public Product getProduct() {
            return product;
        }

        public IntSet getTrimmingRegionKeys() {
            return trimmingRegionKeys;
        }

        public Product getSegmentationProductColorFill() {
            return segmentationProductColorFill;
        }
    }

    private void validateSourceProducts() {
        if (this.currentSourceProduct.getNumBands() < 4) {
            String message = String.format("The current source product '%s' does not contain minimum number of source bands needed.",
                    this.currentSourceProduct.getName());
            throw new OperatorException(message);
        }
        if (this.previousSourceProduct.getNumBands() < 4) {
            String message = String.format("The previous source product '%s' does not contain minimum number of source bands needed.",
                    this.previousSourceProduct.getName());
            throw new OperatorException(message);
        }
        if (this.currentSourceProduct.getSceneRasterWidth() != this.previousSourceProduct.getSceneRasterWidth()
                || this.currentSourceProduct.getSceneRasterHeight() != this.previousSourceProduct.getSceneRasterHeight()) {

            String message = String.format("Source products '%s' and '%s' do not have the same raster sizes.",
                    this.currentSourceProduct.getName(),
                    this.previousSourceProduct.getName());
            throw new OperatorException(message);
        }
    }

    private static String[] findBandNames(Product product) {
        String red = findBand(ForestCoverChangeConstants.MINIMUM_SPECTRAL_WAVE_LENGTH_RED_BAND,
                              ForestCoverChangeConstants.MAXIMUM_SPECTRAL_WAVE_LENGTH_RED_BAND,
                              product);
        String nir = findBand(ForestCoverChangeConstants.MINIMUM_SPECTRAL_WAVE_LENGTH_NIR_BAND,
                              ForestCoverChangeConstants.MAXIMUM_SPECTRAL_WAVE_LENGTH_NIR_BAND,
                              product);
        String swir = findBand(ForestCoverChangeConstants.MINIMUM_SPECTRAL_WAVE_LENGTH_SWIR_BAND,
                               ForestCoverChangeConstants.MAXIMUM_SPECTRAL_WAVE_LENGTH_SWIR_BAND,
                               product);
        String swir2 = findBand(ForestCoverChangeConstants.MINIMUM_SPECTRAL_WAVE_LENGTH_SWIR2_BAND,
                                ForestCoverChangeConstants.MAXIMUM_SPECTRAL_WAVE_LENGTH_SWIR2_BAND,
                                product);
        return new String[] {red, nir, swir, swir2};
    }

    private static String findBand(float minWavelength, float maxWavelength, Product product) {
        String bestBand = null;
        float minDelta = Float.MAX_VALUE;
        float mean = (minWavelength + maxWavelength) / 2;
        for (Band band : product.getBands()) {
            float bandWavelength = band.getSpectralWavelength();
            if (bandWavelength != 0.0F) {
                float delta = Math.abs(bandWavelength - mean);
                if (delta < minDelta) {
                    bestBand = band.getName();
                    minDelta = delta;
                }
            }
        }
        if (bestBand == null) {
            String message = String.format("Source product '%s' does not contain a band that has a wave length between '%s' and '%s'.",
                    product.getName(),
                    minWavelength,
                    maxWavelength);
            throw new OperatorException(message);
        }
        return bestBand;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ForestCoverChangeOp.class);
        }
    }
}
