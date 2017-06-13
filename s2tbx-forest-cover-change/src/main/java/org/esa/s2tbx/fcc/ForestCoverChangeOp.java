package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.s2tbx.fcc.intern.BandsExtractor;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;
import org.esa.s2tbx.fcc.intern.TrimmingHelper;
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
public class ForestCoverChangeOp extends Operator{

    @SuppressWarnings({"PackageVisibleField"})

    public static final String SPRING_MERGING_COST_CRITERION = "Spring";
    public static final String BAATZ_SCHAPE_MERGING_COST_CRITERION = "Baatz & Schape";
    public static final String FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION = "Full Lamda Schedule";
    public static final String BEST_FITTING_REGION_MERGING_CRITERION = "Best Fitting";
    public static final String LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION = "Local Mutual Best Fitting";

    @SourceProduct(alias = "Source Product TM", description = "The source product to be modified.")
    private Product sourceProductTM;
    @SourceProduct(alias = "Source Product ETM", description = "The source product to be modified.")
    private Product sourceProductETM;

    @TargetProduct
    private Product targetProduct;

    @Parameter(defaultValue = "95.0", itemAlias = "percentage", description = "Specifies the percentage of forest cover per segment")
    private float percentage;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Merging cost criterion",
            defaultValue = BAATZ_SCHAPE_MERGING_COST_CRITERION,
            description = "The method to compute the region merging.",
            valueSet = {SPRING_MERGING_COST_CRITERION, BAATZ_SCHAPE_MERGING_COST_CRITERION, FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION})
    private String mergingCostCriterion;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Region merging criterion",
            defaultValue = LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION,
            description = "The method to check the region merging.",
            valueSet = {BEST_FITTING_REGION_MERGING_CRITERION, LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION})
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


    @Override
    public void initialize() throws OperatorException {
        int sceneWidth = this.sourceProductTM.getSceneRasterWidth();
        int sceneHeight = this.sourceProductTM.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.sourceProductTM.getName() + "_union", this.sourceProductTM.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        Logger rootLogger = Logger.getLogger("org.esa.s2tbx.grm");
        rootLogger.setLevel(Level.FINER);

    }

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        File parentFolder = new File("D:\\Forest_cover_changes");
        int[] indexes = new int[] {3, 4, 10, 11};

        Product firstProduct = BandsExtractor.generateBandsExtractor(this.sourceProductETM, indexes);
        firstProduct = BandsExtractor.resampleAllBands(firstProduct);

        Product secondProduct = BandsExtractor.generateBandsExtractor(this.sourceProductTM, indexes);
        secondProduct = BandsExtractor.resampleAllBands(secondProduct);

        Product bandsDifferenceProduct = BandsExtractor.generateBandsDifference(firstProduct, secondProduct);

        Product segmentationAllBandsProduct = BandsExtractor.runSegmentation(firstProduct, secondProduct, bandsDifferenceProduct,
                mergingCostCriterion, regionMergingCriterion,
                totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
        BandsExtractor.writeProduct(segmentationAllBandsProduct, parentFolder, "severalSourcesGenericRegionMergingOp");

        Product firstSegmentationProduct = BandsExtractor.runSegmentation(firstProduct, mergingCostCriterion, regionMergingCriterion,
                totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
        BandsExtractor.writeProduct(firstSegmentationProduct, parentFolder, "firstSegmentation");

        Product secondSegmentationProduct = BandsExtractor.runSegmentation(secondProduct, mergingCostCriterion, regionMergingCriterion,
                totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
        BandsExtractor.writeProduct(secondSegmentationProduct, parentFolder, "secondSegmentation");

        float treeCoverPercentagePixels = 95.0f;
        Product firstProductColorFill = BandsExtractor.runColorFillerOp(firstSegmentationProduct, treeCoverPercentagePixels);
        BandsExtractor.writeProduct(firstProductColorFill, parentFolder, "firstProductColorFill");

        Product secondProductColorFill = BandsExtractor.runColorFillerOp(secondSegmentationProduct, treeCoverPercentagePixels);
        BandsExtractor.writeProduct(secondProductColorFill, parentFolder, "secondProductColorFill");

        int[] trimmingSourceProductBandIndices = new int[] {0, 1, 2};

        Int2ObjectMap<PixelSourceBands> firstTrimmingStatistics = TrimmingHelper.doTrimming(segmentationAllBandsProduct, firstProduct, trimmingSourceProductBandIndices);
        IntSet firstSegmentationTrimmingRegionKeys = firstTrimmingStatistics.keySet();

        Int2ObjectMap<PixelSourceBands> secondTrimmingStatistics = TrimmingHelper.doTrimming(segmentationAllBandsProduct, secondProduct, trimmingSourceProductBandIndices);
        IntSet secondSegmentationTrimmingRegionKeys = secondTrimmingStatistics.keySet();

        Product unionMasksProduct = BandsExtractor.runUnionMasksOp(firstSegmentationTrimmingRegionKeys, firstProductColorFill,
                secondSegmentationTrimmingRegionKeys, secondProductColorFill);
        BandsExtractor.writeProduct(unionMasksProduct, parentFolder, "unionMasksProduct");
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ForestCoverChangeOp.class);
        }
    }
}
