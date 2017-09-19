package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.s2tbx.fcc.common.BandsExtractorOp;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.fcc.descriptor.FCCLandCoverModelDescriptor;
import org.esa.s2tbx.fcc.trimming.ColorFillerTilesComputing;
import org.esa.s2tbx.fcc.trimming.DifferenceRegionTilesComputing;
import org.esa.s2tbx.fcc.trimming.FinalMasksTilesComputing;
import org.esa.s2tbx.fcc.trimming.ObjectsSelectionTilesComputing;
import org.esa.s2tbx.fcc.trimming.PixelStatistic;
import org.esa.s2tbx.fcc.trimming.TrimmingRegionTilesComputing;
import org.esa.s2tbx.fcc.trimming.UnionMasksTilesComputing;
import org.esa.s2tbx.grm.DifferencePixelsRegionMergingOp;
import org.esa.s2tbx.grm.GenericRegionMergingOp;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.utils.matrix.IntMatrix;

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
        alias = "ForrestChangeOp",
        version="1.0",
        category = "",
        description = "Creates forrest change masks out of two source products",
        authors = "Jean Coravu, Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class ForestCoverChange extends Operator {
    static {
        String propertyName = "org.esa.s2tbx.fcc";
        String logLevel = System.getProperty(propertyName);
        if (logLevel != null) {
            Logger logger = Logger.getLogger(propertyName);
            logger.setLevel(Level.parse(logLevel));
        }
    }

    private static final Logger logger = Logger.getLogger(ForestCoverChange.class.getName());

    @SourceProduct(alias = "recentProduct", label = "Recent Date Product", description = "The source product to be modified.")
    private Product currentSourceProduct;
    @SourceProduct(alias = "previousProduct", label = "Previous Date Product", description = "The source product to be modified.")
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

    public ForestCoverChange() {
    }

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

    public void initialize() {
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

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        doExecute();
    }

    public void doExecute() throws OperatorException {
        long startTime = System.currentTimeMillis();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start Forest Cover Change: imageWidth: "+this.targetProduct.getSceneRasterWidth()+", imageHeight: "+this.targetProduct.getSceneRasterHeight() + ", start time: " + new Date(startTime));
        }

        Dimension tileSize = this.targetProduct.getPreferredTileSize();
        int[] trimmingSourceProductBandIndices = new int[] {0, 1, 2};
        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            ProductTrimmingResult currentResult = runTrimming(threadCount, threadPool, this.currentSourceProduct, this.currentProductBandsNames,
                                                              trimmingSourceProductBandIndices, tileSize);

            ProductTrimmingResult previousResult = runTrimming(threadCount, threadPool, this.previousSourceProduct, this.previousProductBandsNames,
                                                               trimmingSourceProductBandIndices, tileSize);

            Product currentProduct = currentResult.getProduct();
            IntSet currentSegmentationTrimmingRegionKeys = currentResult.getTrimmingRegionKeys();
            IntMatrix currentProductColorFill = currentResult.getSegmentationProductColorFill();

            Product previousProduct = previousResult.getProduct();
            IntSet previousSegmentationTrimmingRegionKeys = previousResult.getTrimmingRegionKeys();
            IntMatrix previousProductColorFill = previousResult.getSegmentationProductColorFill();

            // reset the references
            WeakReference<ProductTrimmingResult> referenceCurrentResult = new WeakReference<ProductTrimmingResult>(currentResult);
            referenceCurrentResult.clear();
            WeakReference<ProductTrimmingResult> referencePreviousResult = new WeakReference<ProductTrimmingResult>(previousResult);
            referencePreviousResult.clear();

            IntMatrix unionMaskProduct = runUnionMasksOp(threadCount, threadPool, currentSegmentationTrimmingRegionKeys, currentProductColorFill,
                                                         previousSegmentationTrimmingRegionKeys, previousProductColorFill, tileSize);

            // reset the references
            WeakReference<IntSet> referenceCurrentTrimmingRegionKeys = new WeakReference<IntSet>(currentSegmentationTrimmingRegionKeys);
            referenceCurrentTrimmingRegionKeys.clear();
            WeakReference<IntSet> referencePreviousTrimmingRegionKeys = new WeakReference<IntSet>(previousSegmentationTrimmingRegionKeys);
            referencePreviousTrimmingRegionKeys.clear();
            WeakReference<IntMatrix> referenceCurrentProductColorFill = new WeakReference<IntMatrix>(currentProductColorFill);
            referenceCurrentProductColorFill.clear();
            WeakReference<IntMatrix> referencePreviousProductColorFill = new WeakReference<IntMatrix>(previousProductColorFill);
            referencePreviousProductColorFill.clear();

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Start segmentation for difference bands.");
            }

            IntMatrix differenceSegmentationMatrix = DifferencePixelsRegionMergingOp.runSegmentation(threadCount, threadPool, currentProduct, this.currentProductBandsNames,
                                                                            previousProduct, this.previousProductBandsNames, this.mergingCostCriterion,
                                                                            this.regionMergingCriterion, this.totalIterationsForSecondSegmentation, this.threshold,
                                                                            this.spectralWeight, this.shapeWeight, tileSize);

            IntSet differenceTrimmingSet = computeDifferenceTrimmingSet(threadCount, threadPool, currentProduct, previousProduct, differenceSegmentationMatrix,
                                                                        unionMaskProduct, trimmingSourceProductBandIndices, tileSize);

            // reset the references
            WeakReference<Product> referenceCurrentProduct = new WeakReference<Product>(currentProduct);
            referenceCurrentProduct.clear();
            WeakReference<Product> referencePreviousProduct = new WeakReference<Product>(previousProduct);
            referencePreviousProduct.clear();

            ProductData productData = runFinalMaskOp(threadCount, threadPool, differenceSegmentationMatrix, unionMaskProduct, differenceTrimmingSet, tileSize);

            // reset the references
            WeakReference<IntMatrix> referenceDifferenceSegmentationProduct = new WeakReference<IntMatrix>(differenceSegmentationMatrix);
            referenceDifferenceSegmentationProduct.clear();
            WeakReference<IntMatrix> referenceUnionMaskProduct = new WeakReference<IntMatrix>(unionMaskProduct);
            referenceUnionMaskProduct.clear();

            Band targetBand = this.targetProduct.getBandAt(0);
            targetBand.setData(productData);

            FCCLandCoverModelDescriptor descriptor = new FCCLandCoverModelDescriptor();
            IndexCoding indexCoding = descriptor.getIndexCoding();
            targetBand.setSampleCoding(indexCoding);
            this.targetProduct.getIndexCodingGroup().add(indexCoding);

            ImageInfo imageInfo = descriptor.getImageInfo();
            imageInfo.getColorPaletteDef().setNumColors(256);
            targetBand.setImageInfo(imageInfo);

            // reset the source image of the target product
            targetBand.setSourceImage(null);
            targetBand.getSourceImage();

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

    private IntSet computeDifferenceTrimmingSet(int threadCount, Executor threadPool, Product currentSourceProduct, Product previousSourceProduct,
                                                IntMatrix differenceSegmentationMatrix, IntMatrix unionMaskMatrix, int[] sourceBandIndices, Dimension tileSize)
                                                throws Exception {

        DifferenceRegionTilesComputing helper = new DifferenceRegionTilesComputing(differenceSegmentationMatrix, currentSourceProduct, previousSourceProduct,
                                                                                     unionMaskMatrix, sourceBandIndices, tileSize);
        IntSet differenceTrimmingSet = helper.runTilesInParallel(threadCount, threadPool);

        helper = null;
        System.gc();

        return differenceTrimmingSet;
    }

    private ProductTrimmingResult runTrimming(int threadCount, Executor threadPool, Product sourceProduct,
                                              String[] sourceBandNames, int[] trimmingSourceProductBandIndices, Dimension tileSize)
                                              throws Exception {

        Product extractedBandsProduct = extractBands(sourceProduct, sourceBandNames);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start generate color fill for source product '" + sourceProduct.getName()+"'");
        }

        IntMatrix productColorFill = generateColorFill(threadCount, threadPool, extractedBandsProduct, sourceBandNames, tileSize);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start trimming for source product '" + sourceProduct.getName()+"'");
        }

        TrimmingRegionTilesComputing helper = new TrimmingRegionTilesComputing(productColorFill, extractedBandsProduct, trimmingSourceProductBandIndices, tileSize.width, tileSize.height);
        IntSet segmentationTrimmingRegionKeys = helper.runTilesInParallel(threadCount, threadPool);
        helper = null;
        System.gc();

        return new ProductTrimmingResult(extractedBandsProduct, segmentationTrimmingRegionKeys, productColorFill);
    }

    private IntMatrix generateColorFill(int threadCount, Executor threadPool, Product extractedBandsSourceProduct, String[] sourceBandNames, Dimension tileSize)
                                        throws Exception {

        IntMatrix segmentationMatrix = GenericRegionMergingOp.runSegmentation(threadCount, threadPool, extractedBandsSourceProduct, sourceBandNames,
                                                                               mergingCostCriterion, regionMergingCriterion, totalIterationsForSecondSegmentation,
                                                                               threshold, spectralWeight, shapeWeight, tileSize);

        return runColorFillerOp(threadCount, threadPool, extractedBandsSourceProduct, segmentationMatrix, forestCoverPercentage, tileSize);
    }

    private static IntMatrix runColorFillerOp(int threadCount, Executor threadPool, Product extractedBandsSourceProduct,
                                              IntMatrix segmentationMatrix, float percentagePixels, Dimension tileSize)
                                              throws Exception {

        IntSet validRegions = runObjectsSelectionOp(threadCount, threadPool, segmentationMatrix, extractedBandsSourceProduct, percentagePixels, tileSize);

        ColorFillerTilesComputing tilesComputing = new ColorFillerTilesComputing(segmentationMatrix, validRegions, tileSize.width, tileSize.height);
        return tilesComputing.runTilesInParallel(threadCount, threadPool);
    }

    private static IntSet runObjectsSelectionOp(int threadCount, Executor threadPool, IntMatrix segmentationMatrix,
                                                Product extractedBandsSourceProduct, float percentagePixels, Dimension tileSize)
                                                throws Exception {

        Product landCover = buildLandCoverProduct(extractedBandsSourceProduct);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("landCoverNames", ForestCoverChangeConstants.LAND_COVER_NAME);
        Product landCoverProduct = GPF.createProduct("AddLandCover", parameters, landCover);

        ObjectsSelectionTilesComputing tilesComputing = new ObjectsSelectionTilesComputing(segmentationMatrix, landCoverProduct, tileSize.width, tileSize.height);
        Int2ObjectMap<PixelStatistic> statistics = tilesComputing.runTilesInParallel(threadCount, threadPool);

        IntSet validRegions = new IntOpenHashSet();
        ObjectIterator<Int2ObjectMap.Entry<PixelStatistic>> it = statistics.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<PixelStatistic> entry = it.next();
            PixelStatistic value = entry.getValue();
            if (value.computePixelsPercentage() >= percentagePixels) {
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

    private static Product extractBands(Product sourceProduct, String[] sourceBandNames) {
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

    private static IntMatrix runUnionMasksOp(int threadCount, Executor threadPool, IntSet currentSegmentationTrimmingRegionKeys,
                                             IntMatrix currentSegmentationSourceProduct, IntSet previousSegmentationTrimmingRegionKeys,
                                             IntMatrix previousSegmentationSourceProduct, Dimension tileSize)
                                             throws Exception {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start running union mask");
        }

        UnionMasksTilesComputing tilesComputing = new UnionMasksTilesComputing(currentSegmentationSourceProduct, previousSegmentationSourceProduct,
                                                                               currentSegmentationTrimmingRegionKeys, previousSegmentationTrimmingRegionKeys,
                                                                               tileSize.width, tileSize.height);
        return tilesComputing.runTilesInParallel(threadCount, threadPool);
    }

    private static ProductData runFinalMaskOp(int threadCount, Executor threadPool, IntMatrix differenceSegmentationMatrix,
                                              IntMatrix unionMaskProduct, IntSet differenceTrimmingSet, Dimension tileSize)
                                              throws Exception {

        FinalMasksTilesComputing tilesComputing = new FinalMasksTilesComputing(differenceSegmentationMatrix, unionMaskProduct, differenceTrimmingSet, tileSize.width, tileSize.height);
        return tilesComputing.runTilesInParallel(threadCount, threadPool);
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

    public Product getOutputProduct() {
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

    private static class ProductTrimmingResult {
        private final Product product;
        private final IntSet trimmingRegionKeys;
        private final IntMatrix segmentationProductColorFill;

        ProductTrimmingResult(Product product, IntSet trimmingRegionKeys, IntMatrix segmentationProductColorFill) {
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

        public IntMatrix getSegmentationProductColorFill() {
            return segmentationProductColorFill;
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ForestCoverChange.class);
        }
    }
}
