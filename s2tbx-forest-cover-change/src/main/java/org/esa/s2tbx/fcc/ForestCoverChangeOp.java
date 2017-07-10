package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.s2tbx.fcc.intern.AveragePixelsSourceBands;
import org.esa.s2tbx.fcc.intern.BandsExtractorOp;
import org.esa.s2tbx.fcc.intern.ColorFillerHelper;
import org.esa.s2tbx.fcc.intern.ColorFillerOp;
import org.esa.s2tbx.fcc.intern.ForestCoverChangeConstans;
import org.esa.s2tbx.fcc.intern.ObjectsSelectionHelper;
import org.esa.s2tbx.fcc.intern.ObjectsSelectionOp;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;
import org.esa.s2tbx.fcc.intern.TrimmingHelper;
import org.esa.s2tbx.fcc.intern.UnionMasksHelper;
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
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
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

    @SourceProduct(alias = "Source Product TM", description = "The source product to be modified.")
    private Product currentSourceProduct;
    @SourceProduct(alias = "Source Product ETM", description = "The source product to be modified.")
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

    public ForestCoverChangeOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int sceneWidth = this.currentSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.currentSourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product("forestCoverChange", this.currentSourceProduct.getProductType(), sceneWidth, sceneHeight);
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

        // reset the source inmage of the target product
        this.targetProduct.getBandAt(0).setSourceImage(null);

        String[] sourceBandNames = new String[] {"B4", "B8", "B11", "B12"}; // int[] indexes = new int[] {3, 4, 10, 11};

        int[] trimmingSourceProductBandIndices = new int[] {0, 1, 2};
        int threadCount = Runtime.getRuntime().availableProcessors();
        Executor threadPool = Executors.newCachedThreadPool();

        try {
            ProductTrimmingResult currentResult = runTrimming(threadCount, threadPool, this.currentSourceProduct, sourceBandNames, trimmingSourceProductBandIndices);
            IntSet currentSegmentationTrimmingRegionKeys = currentResult.getTrimmingRegionKeys();
            Product currentProductColorFill = currentResult.getSegmentationProductColorFill();

            ProductTrimmingResult previousResult = runTrimming(threadCount, threadPool, this.previousSourceProduct, sourceBandNames, trimmingSourceProductBandIndices);
            IntSet previousSegmentationTrimmingRegionKeys = previousResult.getTrimmingRegionKeys();
            Product previousProductColorFill = previousResult.getSegmentationProductColorFill();

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Start running union mask");
            }

            runUnionMasksOp(threadCount, threadPool, currentSegmentationTrimmingRegionKeys, currentProductColorFill,
                            previousSegmentationTrimmingRegionKeys, previousProductColorFill, this.targetProduct);

            if (logger.isLoggable(Level.FINE)) {
                long finishTime = System.currentTimeMillis();
                long totalSeconds = (finishTime - startTime) / 1000;
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Finish Forest Cover Change: imageWidth: "+this.targetProduct.getSceneRasterWidth()+", imageHeight: "+this.targetProduct.getSceneRasterHeight()+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
            }

        } catch (Exception ex) {
            throw new OperatorException(ex);
        }
    }

    private ProductTrimmingResult runTrimming(int threadCount, Executor threadPool, Product sourceProduct, String[] sourceBandNames, int[] trimmingSourceProductBandIndices) throws Exception {
        Product product = generateBandsExtractor(sourceProduct, sourceBandNames);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start generate color fill for source product '" + sourceProduct.getName()+"'");
        }

        Product productColorFill = generateColorFill(threadCount, threadPool, product);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start trimming for source product '" + sourceProduct.getName()+"'");
        }

        IntSet segmentationTrimmingRegionKeys = TrimmingHelper.doTrimming(threadCount, threadPool, productColorFill, product, trimmingSourceProductBandIndices);

        return new ProductTrimmingResult(segmentationTrimmingRegionKeys, productColorFill);
    }

    private Product generateColorFill(int threadCount, Executor threadPool, Product sourceProduct) throws Exception {
        String[] sourceBandNames = buildBandNamesArray(sourceProduct);
        Product segmentationProduct = GenericRegionMergingOp.runSegmentation(sourceProduct, sourceBandNames, mergingCostCriterion, regionMergingCriterion,
                                                                             totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

        return runColorFillerOp(threadCount, threadPool, segmentationProduct, forestCoverPercentage);
    }

    private static String[] buildBandNamesArray(Product sourceProduct) {
        ProductNodeGroup<Band> bandGroup = sourceProduct.getBandGroup();
        int bandCount = bandGroup.getNodeCount();
        String[] sourceBandNames = new String[bandCount];
        for (int i=0; i<bandCount; i++) {
            sourceBandNames[i] = bandGroup.get(i).getName();
        }
        return sourceBandNames;
    }

    private static Product runColorFillerOp(int threadCount, Executor threadPool, Product sourceProduct, float percentagePixels)
                                            throws IllegalAccessException, InterruptedException, IOException {

        IntSet validRegions = runObjectsSelectionOp(threadCount, threadPool, sourceProduct, percentagePixels);

//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("validRegions", validRegions);
//        Map<String, Product> sourceProducts = new HashMap<>();
//        sourceProducts.put("sourceProduct", sourceProduct);
//        ColorFillerOp colFillOp = (ColorFillerOp) GPF.getDefaultInstance().createOperator("ColorFillerOp", parameters, sourceProducts, null);
//        Product targetProductSelection = colFillOp.getTargetProduct();
//
//        OperatorExecutor executor = OperatorExecutor.create(colFillOp);
//        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
//
//        return targetProductSelection;

        Dimension tileSize = JAI.getDefaultTileSize();
        ColorFillerHelper helper = new ColorFillerHelper(sourceProduct, validRegions, tileSize.width, tileSize.height);
        return helper.computeRegionsInParallel(threadCount, threadPool);
    }

    private static IntSet runObjectsSelectionOp(int threadCount, Executor threadPool, Product sourceProduct, float percentagePixels)
                                                throws IllegalAccessException, InterruptedException, IOException {

//        Map<String, Object> parameters = new HashMap<>();
//        Map<String, Product> sourceProducts = new HashMap<>();
//        sourceProducts.put("sourceProduct", sourceProduct);
//        ObjectsSelectionOp objSelOp = (ObjectsSelectionOp) GPF.getDefaultInstance().createOperator("ObjectsSelectionOp", parameters, sourceProducts, null);
//        objSelOp.getTargetProduct();
//
//        OperatorExecutor executor = OperatorExecutor.create(objSelOp);
//        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
//        Int2ObjectMap<ObjectsSelectionOp.PixelStatistic> statistics = objSelOp.getStatistics();


        Product landCover = buildLandCoverProduct(sourceProduct);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("landCoverNames", ForestCoverChangeConstans.LAND_COVER_NAME);
        Product landCoverProduct = GPF.createProduct("AddLandCover", parameters, landCover);

        Dimension tileSize = JAI.getDefaultTileSize();
        ObjectsSelectionHelper helper = new ObjectsSelectionHelper(sourceProduct, landCoverProduct, tileSize.width, tileSize.height);
        Int2ObjectMap<ObjectsSelectionOp.PixelStatistic> statistics = helper.computeRegionsInParallel(threadCount, threadPool);

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

        ProductUtils.copyMetadata(sourceProduct,  landCoverProduct);
        ProductUtils.copyGeoCoding(sourceProduct,  landCoverProduct);
        ProductUtils.copyTiePointGrids(sourceProduct,  landCoverProduct);
        ProductUtils.copyVectorData(sourceProduct,  landCoverProduct);
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
                                           Product previousSegmentationSourceProduct, Product inputTargetProduct)
                                           throws IllegalAccessException, InterruptedException, IOException {

        Dimension tileSize = JAI.getDefaultTileSize();
        UnionMasksHelper helper = new UnionMasksHelper(currentSegmentationSourceProduct, previousSegmentationSourceProduct, currentSegmentationTrimmingRegionKeys,
                                                       previousSegmentationTrimmingRegionKeys, tileSize.width, tileSize.height);
        ProductData productData = helper.computeRegionsInParallel(threadCount, threadPool);
        inputTargetProduct.getBandAt(0).setData(productData);
        return inputTargetProduct;

//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("currentSegmentationTrimmingRegionKeys", currentSegmentationTrimmingRegionKeys);
//        parameters.put("previousSegmentationTrimmingRegionKeys", previousSegmentationTrimmingRegionKeys);
//        Map<String, Product> sourceProducts = new HashMap<>();
//        sourceProducts.put("currentSegmentationSourceProduct", currentSegmentationSourceProduct);
//        sourceProducts.put("previousSegmentationSourceProduct", previousSegmentationSourceProduct);
//        sourceProducts.put("inputTargetProduct", inputTargetProduct);
//        Operator unionMasksOp = GPF.getDefaultInstance().createOperator("UnionMasksOp", parameters, sourceProducts, null);
//        Product targetProductSelection = unionMasksOp.getTargetProduct();
//        OperatorExecutor executor = OperatorExecutor.create(unionMasksOp);
//        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
//
//        return targetProductSelection;
    }

    private static class ProductTrimmingResult {
        private final IntSet trimmingRegionKeys;
        private final Product segmentationProductColorFill;

        ProductTrimmingResult(IntSet trimmingRegionKeys, Product segmentationProductColorFill) {
            this.trimmingRegionKeys = trimmingRegionKeys;
            this.segmentationProductColorFill = segmentationProductColorFill;
        }

        public IntSet getTrimmingRegionKeys() {
            return trimmingRegionKeys;
        }

        public Product getSegmentationProductColorFill() {
            return segmentationProductColorFill;
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ForestCoverChangeOp.class);
        }
    }
}
