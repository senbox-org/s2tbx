package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.s2tbx.fcc.intern.BandsExtractor;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;
import org.esa.s2tbx.fcc.intern.TrimmingHelper;
import org.esa.s2tbx.grm.GenericRegionMergingOp;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.io.File;
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
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        //TODO Jean remove
        Logger rootLogger = Logger.getLogger("org.esa.s2tbx.grm");
        rootLogger.setLevel(Level.FINER);
    }

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        this.targetProduct.getBandAt(0).setSourceImage(null);

        File parentFolder = new File("D:\\Forest_cover_changes");

        String[] sourceBandNames = new String[] {"B4", "B5", "B10", "B11"}; // int[] indexes = new int[] {3, 4, 10, 11};

        Product previousProduct = BandsExtractor.generateBandsExtractor(this.previousSourceProduct, sourceBandNames);
        previousProduct = BandsExtractor.resampleAllBands(previousProduct);

        Product currentProduct = BandsExtractor.generateBandsExtractor(this.currentSourceProduct, sourceBandNames);
        currentProduct = BandsExtractor.resampleAllBands(currentProduct);

        Product bandsDifferenceProduct = BandsExtractor.generateBandsDifference(currentProduct, previousProduct);

        Product segmentationAllBandsProduct = BandsExtractor.runSegmentation(currentProduct, previousProduct, bandsDifferenceProduct,
                                                                             mergingCostCriterion, regionMergingCriterion,
                                                                             totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
        BandsExtractor.writeProduct(segmentationAllBandsProduct, parentFolder, "severalSourcesGenericRegionMergingOp");

        Product currentSegmentationProduct = BandsExtractor.runSegmentation(currentProduct, mergingCostCriterion, regionMergingCriterion,
                                                                            totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
        BandsExtractor.writeProduct(currentSegmentationProduct, parentFolder, "firstSegmentation");

        Product previousSegmentationProduct = BandsExtractor.runSegmentation(previousProduct, mergingCostCriterion, regionMergingCriterion,
                                                                             totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
        BandsExtractor.writeProduct(previousSegmentationProduct, parentFolder, "secondSegmentation");

        Product currentProductColorFill = BandsExtractor.runColorFillerOp(currentSegmentationProduct, forestCoverPercentage);
        BandsExtractor.writeProduct(currentProductColorFill, parentFolder, "firstProductColorFill");

        Product previousProductColorFill = BandsExtractor.runColorFillerOp(previousSegmentationProduct, forestCoverPercentage);
        BandsExtractor.writeProduct(previousProductColorFill, parentFolder, "secondProductColorFill");

        int[] trimmingSourceProductBandIndices = new int[] {0, 1, 2};

        Int2ObjectMap<PixelSourceBands> currentTrimmingStatistics = TrimmingHelper.doTrimming(segmentationAllBandsProduct, currentProduct, trimmingSourceProductBandIndices);
        IntSet currentSegmentationTrimmingRegionKeys = currentTrimmingStatistics.keySet();

        Int2ObjectMap<PixelSourceBands> previousTrimmingStatistics = TrimmingHelper.doTrimming(segmentationAllBandsProduct, previousProduct, trimmingSourceProductBandIndices);
        IntSet previousSegmentationTrimmingRegionKeys = previousTrimmingStatistics.keySet();

        BandsExtractor.runUnionMasksOp(currentSegmentationTrimmingRegionKeys, currentProductColorFill, previousSegmentationTrimmingRegionKeys, previousProductColorFill, this.targetProduct);
//        Product unionMasksProduct = BandsExtractor.runUnionMasksOp(currentSegmentationTrimmingRegionKeys, currentProductColorFill,
//                                                                   previousSegmentationTrimmingRegionKeys, previousProductColorFill, this.targetProduct);
//        BandsExtractor.writeProduct(unionMasksProduct, parentFolder, "unionMasksProduct");
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ForestCoverChangeOp.class);
        }
    }
}
