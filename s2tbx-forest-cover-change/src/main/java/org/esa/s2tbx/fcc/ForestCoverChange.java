package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.s2tbx.fcc.common.BandsExtractorOp;
import org.esa.s2tbx.fcc.trimming.*;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.grm.DifferencePixelsRegionMergingOp;
import org.esa.s2tbx.grm.GenericRegionMergingOp;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.util.ProductUtils;

import javax.media.jai.JAI;
import java.awt.Dimension;
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
public class ForestCoverChange {
    private static final Logger logger = Logger.getLogger(ForestCoverChange.class.getName());

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

    public ForestCoverChange(Product currentSourceProduct, Product previousSourceProduct, Map<String, Object> parameters) {
        this.currentSourceProduct = currentSourceProduct;
        this.previousSourceProduct = previousSourceProduct;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if(entry.getKey().equals("forestCoverPercentage")) {
                this.forestCoverPercentage = (float) entry.getValue();
            } else if(entry.getKey().equals("totalIterationsForSecondSegmentation")) {
                this.totalIterationsForSecondSegmentation = (int) entry.getValue();
            } else if(entry.getKey().equals("regionMergingCriterion")) {
                this.regionMergingCriterion = (String) entry.getValue();
            } else if(entry.getKey().equals("shapeWeight")) {
                this.shapeWeight = (float) entry.getValue();
            } else if(entry.getKey().equals("spectralWeight")) {
                this.spectralWeight = (float) entry.getValue();
            } else if(entry.getKey().equals("threshold")) {
                this.threshold = (float) entry.getValue();
            } else if(entry.getKey().equals("mergingCostCriterion")) {
                this.mergingCostCriterion = (String) entry.getValue();
            }

        }
        initialize();
    }
    private void initialize() {
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

    }


    public void doExecute(ProgressMonitor pm) throws OperatorException {

        //TODO Jean remove
        Logger logger = Logger.getLogger("org.esa.s2tbx.fcc");
        logger.setLevel(Level.FINE);
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

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sourceBandIndices", sourceBandIndices);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("currentSourceProduct", currentSourceProduct);
        sourceProducts.put("previousSourceProduct", previousSourceProduct);
        sourceProducts.put("unionMask", unionMaskProduct);
        sourceProducts.put("differenceSegmentationProduct", differenceSegmentationProduct);
        DifferenceRegionComputingOp operator = (DifferenceRegionComputingOp) GPF.getDefaultInstance().createOperator("DifferenceRegionComputingOp", parameters, sourceProducts, null);
        operator.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        IntSet differenceTrimmingSet = operator.processResult(threadCount, threadPool);

//        DifferenceRegionTilesComputing helper = new DifferenceRegionTilesComputing(differenceSegmentationProduct, currentSourceProduct, previousSourceProduct,
//                                                                                     unionMaskProduct, sourceBandIndices, tileSize.width, tileSize.height);
//        IntSet differenceTrimmingSet = helper.runTilesInParallel(threadCount, threadPool);
//
//        helper = null;
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

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sourceBandIndices", trimmingSourceProductBandIndices);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("segmentationSourceProduct", productColorFill);
        sourceProducts.put("sourceProduct", product);
        TrimmingRegionComputingOp operator = (TrimmingRegionComputingOp) GPF.getDefaultInstance().createOperator("TrimmingRegionComputingOp", parameters, sourceProducts, null);
        operator.getTargetProduct();

        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        IntSet segmentationTrimmingRegionKeys = operator.processResult(threadCount, threadPool);

//        TrimmingRegionTilesComputing helper = new TrimmingRegionTilesComputing(productColorFill, product, trimmingSourceProductBandIndices,
//                                                                                 tileSize.width, tileSize.height);
//        IntSet segmentationTrimmingRegionKeys = helper.runTilesInParallel(threadCount, threadPool);
//        helper = null;

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

        Map<String, Object> selectionParameters = new HashMap<>();
        selectionParameters.put("validRegions", validRegions);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("segmentationSourceProduct", segmentationSourceProduct);
        ColorFillerOp operator = (ColorFillerOp) GPF.getDefaultInstance().createOperator("ColorFillerOp", selectionParameters, sourceProducts, null);
        Product targetProduct = operator.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        ProductData productData = operator.getProductData();
        Band targetBand = targetProduct.getBandAt(0);
        targetBand.setData(productData);
        targetBand.setSourceImage(null);
        targetBand.getSourceImage();
        return targetProduct;
//        ColorFillerTilesComputing helper = new ColorFillerTilesComputing(segmentationSourceProduct, validRegions, tileSize.width, tileSize.height);
//        return helper.runTilesInParallel(threadCount, threadPool);
    }

    private static IntSet runObjectsSelectionOp(int threadCount, Executor threadPool, Product sourceProduct, float percentagePixels, Dimension tileSize)
            throws Exception {

        Product landCover = buildLandCoverProduct(sourceProduct);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("landCoverNames", ForestCoverChangeConstants.LAND_COVER_NAME);
        Product landCoverProduct = GPF.createProduct("AddLandCover", parameters, landCover);

        Map<String, Object> selectionParameters = new HashMap<>();
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", sourceProduct);
        sourceProducts.put("landCoverProduct", landCoverProduct);
        ObjectsSelectionOp operator = (ObjectsSelectionOp) GPF.getDefaultInstance().createOperator("ObjectsSelectionOp", selectionParameters, sourceProducts, null);
        operator.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        Int2ObjectMap<PixelStatistic> statistics = operator.getStatistics();

//        ObjectsSelectionTilesComputing helper = new ObjectsSelectionTilesComputing(sourceProduct, landCoverProduct, tileSize.width, tileSize.height);
//        Int2ObjectMap<ObjectsSelectionOp.PixelStatistic> statistics = helper.runTilesInParallel(threadCount, threadPool);

        IntSet validRegions = new IntOpenHashSet();
        ObjectIterator<Int2ObjectMap.Entry<PixelStatistic>> it = statistics.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<PixelStatistic> entry = it.next();
            PixelStatistic value = entry.getValue();
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

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("currentSegmentationTrimmingRegionKeys", currentSegmentationTrimmingRegionKeys);
        parameters.put("previousSegmentationTrimmingRegionKeys", previousSegmentationTrimmingRegionKeys);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("currentSegmentationSourceProduct", currentSegmentationSourceProduct);
        sourceProducts.put("previousSegmentationSourceProduct", previousSegmentationSourceProduct);
        UnionMasksOp operator = (UnionMasksOp) GPF.getDefaultInstance().createOperator("UnionMasksOp", parameters, sourceProducts, null);
        Product targetProduct = operator.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        ProductData productData = operator.getProductData();
        Band targetBand = targetProduct.getBandAt(0);
        targetBand.setData(productData);
        targetBand.setSourceImage(null);
        targetBand.getSourceImage();
        return targetProduct;

//        UnionMasksTilesComputing helper = new UnionMasksTilesComputing(currentSegmentationSourceProduct, previousSegmentationSourceProduct, currentSegmentationTrimmingRegionKeys,
//                                                       previousSegmentationTrimmingRegionKeys, tileSize.width, tileSize.height);
//        ProductData productData = helper.runTilesInParallel(threadCount, threadPool);
//        int sceneRasterWidth = currentSegmentationSourceProduct.getSceneRasterWidth();
//        int sceneRasterHeight = currentSegmentationSourceProduct.getSceneRasterHeight();
//        Product targetProduct = new Product("forestCoverChange", currentSegmentationSourceProduct.getProductType(), sceneRasterWidth, sceneRasterHeight);
//        targetProduct.setPreferredTileSize(tileSize);
//        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneRasterWidth, sceneRasterHeight);
//        targetBand.setData(productData);
//        targetProduct.addBand(targetBand);
//        return targetProduct;
    }

    private Product runFinalMaskOp(int threadCount, Executor threadPool, Product differenceSegmentationProduct,
                                   Product unionMaskProduct, IntSet differenceTrimmingSet, Dimension tileSize)
            throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("differenceTrimmingSet", differenceTrimmingSet);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("differenceSegmentationProduct", differenceSegmentationProduct);
        sourceProducts.put("unionMaskProduct", unionMaskProduct);
        FinalMasksOp operator = (FinalMasksOp) GPF.getDefaultInstance().createOperator("FinalMasksOp", parameters, sourceProducts, null);
        operator.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        ProductData productData = operator.getProductData();
        Band targetBand = this.targetProduct.getBandAt(0);
        targetBand.setData(productData);
        targetBand.setSourceImage(null);
        targetBand.getSourceImage();
        return this.targetProduct;

//        FinalMasksTilesComputing helper = new FinalMasksTilesComputing(differenceSegmentationProduct, unionMaskProduct, differenceTrimmingSet, tileSize.width, tileSize.height);
//        ProductData productData = helper.runTilesInParallel(threadCount, threadPool);
//        this.targetProduct.getBandAt(0).setData(productData);
//        return this.targetProduct;
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
        final GeoCoding geoCodingCurrentProduct = currentSourceProduct.getSceneGeoCoding();
        GeoPos currentProductMinPoint = geoCodingCurrentProduct.getGeoPos(new PixelPos(0, 0), null);
        GeoPos currentProductMaxPoint = geoCodingCurrentProduct.getGeoPos(new PixelPos(currentSourceProduct.getSceneRasterWidth(),
                currentSourceProduct.getSceneRasterHeight()), null);
        final GeoCoding geoCodingPreviousProduct = previousSourceProduct.getSceneGeoCoding();
        GeoPos previousProductMinPoint = geoCodingPreviousProduct.getGeoPos(new PixelPos(0, 0), null);
        GeoPos previousProductMaxPoint = geoCodingPreviousProduct.getGeoPos(new PixelPos(previousSourceProduct.getSceneRasterWidth(),
                previousSourceProduct.getSceneRasterHeight()), null);
        if ((currentProductMinPoint.getLat() != previousProductMinPoint.getLat())||(currentProductMinPoint.getLon() != previousProductMinPoint.getLon())){
            String message = String.format("Source products '%s' and '%s' do not have the same geoCoding.",
                    this.currentSourceProduct.getName(),
                    this.previousSourceProduct.getName());
            throw new OperatorException(message);
        }
        if ((currentProductMaxPoint.getLat() != previousProductMaxPoint.getLat())||(currentProductMaxPoint.getLon() != previousProductMaxPoint.getLon())){
            String message = String.format("Source products '%s' and '%s' do not have the same geoCoding.",
                    this.currentSourceProduct.getName(),
                    this.previousSourceProduct.getName());
            throw new OperatorException(message);
        }
    }

    public Product getTargetProduct() {
        return this.targetProduct;
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
}
