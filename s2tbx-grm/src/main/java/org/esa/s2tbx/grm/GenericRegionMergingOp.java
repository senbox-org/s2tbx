package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.grm.tiles.BaatzSchapeTileSegmenter;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.*;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import java.awt.*;
import java.util.Date;

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
    private static final String SPRING_MERGING_COST_CRITERION = "Spring";
    public static final String BAATZ_SCHAPE_MERGING_COST_CRITERION = "Baatz & Schape";
    private static final String FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION = "Full Lamda Schedule";
    private static final String BEST_FITTING_REGION_MERGING_CRITERION = "Best Fitting";
    private static final String LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION = "Local Mutual Best Fitting";

    @Parameter(label = "Merging cost criterion",
            description = "The method to compute the region merging.",
            valueSet = {SPRING_MERGING_COST_CRITERION, BAATZ_SCHAPE_MERGING_COST_CRITERION, FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION})
    private String mergingCostCriterion;

    @Parameter(label = "Region merging criterion",
            description = "The method to check the region merging.",
            valueSet = {BEST_FITTING_REGION_MERGING_CRITERION, LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION})
    private String regionMergingCriterion;

    @Parameter(label = "Number of iterations", description = "The number of iterations.")
    private int numberOfIterations;

    @Parameter(label = "Threshold", description = "The threshold.")
    private int threshold;

    @Parameter(label = "Spectral weight", description = "The spectral weight.")
    private float spectralWeight;

    @Parameter(label = "Shape weight", description = "The shape weight.")
    private float shapeWeight;

    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

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

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();

        this.targetProduct = new Product(this.sourceProduct.getName() + "_grm", this.sourceProduct.getProductType(), sceneWidth, sceneHeight);
    }

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        long t0 = System.currentTimeMillis();
        System.out.println("startTime doExecute="+new Date(t0).toString());

        int sourceBandIndices[] = new int[this.sourceBandNames.length];
        for (int i=0; i<this.sourceBandNames.length; i++) {
            sourceBandIndices[i] = this.sourceProduct.getBandIndex(this.sourceBandNames[i]);
        }
        AbstractSegmenter segmenter = null;
        if (SPRING_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
            segmenter = new SpringSegmenter(this.threshold);
        } else if (BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
            segmenter = new BaatzSchapeSegmenter(this.spectralWeight, this.shapeWeight, this.threshold);
        } else if (FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
            segmenter = new FullLambdaScheduleSegmenter(this.threshold);
        }

        boolean fastSegmentation = false;
        if (BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(this.regionMergingCriterion)) {
            fastSegmentation = true;
        } else if (BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(this.regionMergingCriterion)) {
            fastSegmentation = false;
        }

//        boolean addFourNeighbors = true;
//        BoundingBox rectange = new BoundingBox(0, 0, this.sourceProduct.getSceneRasterWidth(), this.sourceProduct.getSceneRasterHeight());
//        segmenter.update(this.sourceProduct, sourceBandIndices, rectange, this.numberOfIterations, fastSegmentation, addFourNeighbors);

        try {
            int numberOfFirstIterations = 2;
            BaatzSchapeTileSegmenter tileSegmenter = new BaatzSchapeTileSegmenter(spectralWeight, shapeWeight, threshold);
            segmenter = tileSegmenter.runSegmentation(this.sourceProduct, sourceBandIndices, numberOfIterations, numberOfFirstIterations);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        System.out.println("final graph.nodecount="+segmenter.getGraph().getNodeCount());

        Band targetBand = segmenter.buildBand();
        this.targetProduct.addBand(targetBand);


        long endTime = System.currentTimeMillis();
        System.out.println("endTime doExecute="+new Date(endTime).toString());
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(GenericRegionMergingOp.class);
        }
    }
}
