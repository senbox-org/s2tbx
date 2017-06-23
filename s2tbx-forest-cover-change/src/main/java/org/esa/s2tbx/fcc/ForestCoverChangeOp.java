package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.s2tbx.fcc.intern.BandsExtractor;
import org.esa.s2tbx.fcc.intern.ColorFillerOp;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;
import org.esa.s2tbx.fcc.intern.TrimmingHelper;
import org.esa.s2tbx.grm.AbstractGenericRegionMergingOp;
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
import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
import org.esa.snap.core.gpf.descriptor.SourceProductDescriptor;
import org.esa.snap.core.gpf.descriptor.SourceProductsDescriptor;
import org.esa.snap.core.gpf.internal.OperatorExecutor;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
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
        this.targetProduct.setSceneGeoCoding(this.currentSourceProduct.getSceneGeoCoding());
        this.targetProduct.setPreferredTileSize(tileSize);
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

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start bands extractor for current product");
        }

        Product currentProduct = generateBandsExtractor(this.currentSourceProduct, sourceBandNames);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start generate color fill for current product");
        }

        Product currentProductColorFill = generateColorFill(currentProduct);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start trimming for current product");
        }

        int[] trimmingSourceProductBandIndices = new int[] {0, 1, 2};

        IntSet currentSegmentationTrimmingRegionKeys = null;
        try {
            currentSegmentationTrimmingRegionKeys = computeTrimming(currentProductColorFill, currentProduct, trimmingSourceProductBandIndices);
        } catch (InterruptedException e) {
            throw new OperatorException(e);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start bands extractor for previous product");
        }

        Product previousProduct = generateBandsExtractor(this.previousSourceProduct, sourceBandNames);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start generate color fill for previous product");
        }

        Product previousProductColorFill = generateColorFill(previousProduct);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start trimming for previous product");
        }

        IntSet previousSegmentationTrimmingRegionKeys = null;
        try {
            previousSegmentationTrimmingRegionKeys = computeTrimming(previousProductColorFill, previousProduct, trimmingSourceProductBandIndices);
        } catch (InterruptedException e) {
            throw new OperatorException(e);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start running union mask");
        }
        runUnionMasksOp(currentSegmentationTrimmingRegionKeys, currentProductColorFill, previousSegmentationTrimmingRegionKeys, previousProductColorFill, this.targetProduct);

        if (logger.isLoggable(Level.FINE)) {
            long finishTime = System.currentTimeMillis();
            long totalSeconds = (finishTime - startTime) / 1000;
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Finish Forest Cover Change: imageWidth: "+this.targetProduct.getSceneRasterWidth()+", imageHeight: "+this.targetProduct.getSceneRasterHeight()+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
        }
    }

    private static IntSet computeTrimming(Product segmentationSourceProduct, Product currentProduct, int[] trimmingSourceProductBandIndices) throws InterruptedException {
        Int2ObjectMap<PixelSourceBands> currentTrimmingStatistics = TrimmingHelper.doTrimming(segmentationSourceProduct, currentProduct, trimmingSourceProductBandIndices);
        return currentTrimmingStatistics.keySet();
    }

    private Product generateColorFill(Product sourceProduct) {
        Product segmentationProduct = runSegmentation(sourceProduct, mergingCostCriterion, regionMergingCriterion,
                                                                        totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
        return runColorFillerOp(segmentationProduct, forestCoverPercentage);
    }

    private static Product runSegmentation(Product sourceProduct, String mergingCostCriterion, String regionMergingCriterion,
                                          int totalIterationsForSecondSegmentation, float threshold, float spectralWeight,
                                          float shapeWeight) {

        String[] sourceBandNames = buildBandNamesArray(sourceProduct);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mergingCostCriterion", mergingCostCriterion);
        parameters.put("regionMergingCriterion", regionMergingCriterion);
        parameters.put("totalIterationsForSecondSegmentation", totalIterationsForSecondSegmentation);
        parameters.put("threshold", threshold);
        parameters.put("spectralWeight", spectralWeight);
        parameters.put("shapeWeight", shapeWeight);
        parameters.put("sourceBandNames", sourceBandNames);

        Map<String, Product> sourceProducts = new HashMap<String, Product>(1);
        sourceProducts.put("sourceProduct", sourceProduct);

        Operator operator = GPF.getDefaultInstance().createOperator("GenericRegionMergingOp", parameters, sourceProducts, null);
        Product targetProduct = operator.getTargetProduct();
        targetProduct.setSceneGeoCoding(sourceProduct.getSceneGeoCoding());
        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        return targetProduct;
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

    private static Product runColorFillerOp(Product firstProduct, float percentagePixels) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("percentagePixels", percentagePixels);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", firstProduct);
        ColorFillerOp colFillOp = (ColorFillerOp) GPF.getDefaultInstance().createOperator("ColorFillerOp", parameters, sourceProducts, null);
        Product targetProductSelection = colFillOp.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(colFillOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        return targetProductSelection;
    }

    private static Product generateBandsDifference(Product currentProduct, Product previousProduct) {
        Product[] products = new Product[] {currentProduct, previousProduct};
        Map<String, Object> parameters = new HashMap<>();
        return GPF.createProduct("BandsDifferenceOp", parameters, products, null);
    }

    private static Product generateBandsExtractor(Product sourceProduct, String[] sourceBandNames) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Generate bands extractor for source product '" + sourceProduct.getName()+"'");
        }

        Map<String, Product> sourceProducts = new HashMap<>(1);
        sourceProducts.put("sourceProduct", sourceProduct);
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("sourceBandNames", sourceBandNames);
        Product targetProduct = GPF.createProduct("BandsExtractorOp", parameters, sourceProducts, null);
        return resampleAllBands(targetProduct);
    }

    private static Product resampleAllBands(Product sourceProduct) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Resample bands for source product '" + sourceProduct.getName()+"'");
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("targetWidth", sourceProduct.getSceneRasterWidth());
        parameters.put("targetHeight", sourceProduct.getSceneRasterHeight());
        Product targetProduct = GPF.createProduct("Resample", parameters, sourceProduct);
        targetProduct.setName(sourceProduct.getName());
        return targetProduct;
    }

    private static Product runUnionMasksOp(IntSet currentSegmentationTrimmingRegionKeys, Product currentSegmentationSourceProduct,
                                          IntSet previousSegmentationTrimmingRegionKeys, Product previousSegmentationSourceProduct,
                                          Product inputTargetProduct) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("currentSegmentationTrimmingRegionKeys", currentSegmentationTrimmingRegionKeys);
        parameters.put("previousSegmentationTrimmingRegionKeys", previousSegmentationTrimmingRegionKeys);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("currentSegmentationSourceProduct", currentSegmentationSourceProduct);
        sourceProducts.put("previousSegmentationSourceProduct", previousSegmentationSourceProduct);
        sourceProducts.put("inputTargetProduct", inputTargetProduct);
        Operator unionMasksOp = GPF.getDefaultInstance().createOperator("UnionMasksOp", parameters, sourceProducts, null);
        Product targetProductSelection = unionMasksOp.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(unionMasksOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        return targetProductSelection;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ForestCoverChangeOp.class);
        }
    }
}
