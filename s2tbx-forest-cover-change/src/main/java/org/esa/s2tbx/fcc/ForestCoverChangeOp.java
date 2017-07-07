package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.s2tbx.fcc.intern.BandsExtractorOp;
import org.esa.s2tbx.fcc.intern.ColorFillerOp;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;
import org.esa.s2tbx.fcc.intern.TrimmingHelper;
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

        try {
            ProductTrimmingResult currentResult = runTrimming(this.currentSourceProduct, sourceBandNames, trimmingSourceProductBandIndices);
            IntSet currentSegmentationTrimmingRegionKeys = currentResult.getTrimmingRegionKeys();
            Product currentProductColorFill = currentResult.getSegmentationProductColorFill();

            ProductTrimmingResult previousResult = runTrimming(this.previousSourceProduct, sourceBandNames, trimmingSourceProductBandIndices);
            IntSet previousSegmentationTrimmingRegionKeys = previousResult.getTrimmingRegionKeys();
            Product previousProductColorFill = previousResult.getSegmentationProductColorFill();

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

        } catch (Exception ex) {
            throw new OperatorException(ex);
        }
    }

    private ProductTrimmingResult runTrimming(Product sourceProduct, String[] sourceBandNames, int[] trimmingSourceProductBandIndices) throws Exception {
        Product product = generateBandsExtractor(sourceProduct, sourceBandNames);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start generate color fill for source product '" + sourceProduct.getName()+"'");
        }

        Product productColorFill = generateColorFill(product);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start trimming for source product '" + sourceProduct.getName()+"'");
        }

        IntSet segmentationTrimmingRegionKeys = TrimmingHelper.doTrimming(productColorFill, product, trimmingSourceProductBandIndices);

        return new ProductTrimmingResult(segmentationTrimmingRegionKeys, productColorFill);
    }

    private Product generateColorFill(Product sourceProduct) throws Exception {
        String[] sourceBandNames = buildBandNamesArray(sourceProduct);
        Product segmentationProduct = GenericRegionMergingOp.runSegmentation(sourceProduct, sourceBandNames, mergingCostCriterion, regionMergingCriterion,
                                                                             totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

        return runColorFillerOp(segmentationProduct, forestCoverPercentage);
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

    private static Product runColorFillerOp(Product sourceProduct, float percentagePixels) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("percentagePixels", percentagePixels);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", sourceProduct);
        ColorFillerOp colFillOp = (ColorFillerOp) GPF.getDefaultInstance().createOperator("ColorFillerOp", parameters, sourceProducts, null);
        Product targetProductSelection = colFillOp.getTargetProduct();

        OperatorExecutor executor = OperatorExecutor.create(colFillOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        return targetProductSelection;
    }

    private static Product generateBandsExtractor(Product sourceProduct, String[] sourceBandNames) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Generate bands extractor for source product '" + sourceProduct.getName()+"'");
        }

        Product targetProduct = BandsExtractorOp.extractBands(sourceProduct, sourceBandNames);
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
