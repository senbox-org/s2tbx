package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import org.esa.s2tbx.grm.tiles.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.*;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.util.math.MathUtils;

import javax.media.jai.JAI;
import java.awt.*;
import java.io.IOException;
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

    AbstractTileSegmenter tileSegmenter;

    public GenericRegionMergingOp() {
    }

    @Override
    public void initialize() throws OperatorException {
//        if (this.mergingCostCriterion == null) {
//            throw new OperatorException("Please specify the merging cost criterion.");
//        }
//        if (this.regionMergingCriterion == null) {
//            throw new OperatorException("Please specify the region merging criterion.");
//        }
//        if (this.threshold == 0.0f) {
//            throw new OperatorException("Please specify the threshold.");
//        }
//        if (BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
//            if (this.spectralWeight == 0.0f) {
//                throw new OperatorException("Please specify the spectral weight.");
//            }
//            if (this.shapeWeight == 0.0f) {
//                throw new OperatorException("Please specify the shape weight.");
//            }
//        }
        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.sourceProduct.getName() + "_grm", this.sourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        Dimension imageSize = new Dimension(this.targetProduct.getSceneRasterWidth(), this.targetProduct.getSceneRasterHeight());

        this.threshold = 2000;
        this.numberOfIterations = 75;
        this.shapeWeight = 0.5f;
        this.spectralWeight = 0.5f;
        int numberOfFirstIterations = 2;

        boolean fastSegmentation = false;
        if (BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(this.regionMergingCriterion)) {
            fastSegmentation = true;
        } else if (BEST_FITTING_REGION_MERGING_CRITERION.equalsIgnoreCase(this.regionMergingCriterion)) {
            fastSegmentation = false;
        }

        try {
            this.tileSegmenter = new BaatzSchapeTileSegmenter(imageSize, tileSize, numberOfIterations, numberOfFirstIterations, threshold, fastSegmentation, spectralWeight, shapeWeight);
//        if (SPRING_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
//            this.tileSegmenter = new SpringTileSegmenter(imageSize, tileSize, numberOfIterations, numberOfFirstIterations, threshold, fastSegmentation);
//        } else if (BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
//            this.tileSegmenter = new BaatzSchapeTileSegmenter(imageSize, tileSize, numberOfIterations, numberOfFirstIterations, threshold, fastSegmentation, spectralWeight, shapeWeight);
//        } else if (FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION.equalsIgnoreCase(this.mergingCostCriterion)) {
//            this.tileSegmenter = new FullLambdaScheduleTileSegmenter(imageSize, tileSize, numberOfIterations, numberOfFirstIterations, threshold, fastSegmentation);
//        }
        } catch (Exception ex) {
            throw new OperatorException(ex);
        }
    }

    public void runSegmentation() throws IOException, IllegalAccessException {
        System.out.println("  >>>>>>>>>>> start runSegmentation time="+ new Date(System.currentTimeMillis()));

        OperatorExecutor operatorExecutor = OperatorExecutor.create(this);
        operatorExecutor.execute(ProgressMonitor.NULL);

        AbstractSegmenter segmenter = this.tileSegmenter.runAllTilesSecondSegmentation();
        Band oldTargetBand = this.targetProduct.getBandAt(0);
        Band targetBand = segmenter.buildBand();
        this.targetProduct.removeBand(oldTargetBand);
        this.targetProduct.addBand(targetBand);

        System.out.println("  >>>>>>>>>>> finish runSegmentation time="+ new Date(System.currentTimeMillis()));
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle targetRectangle = targetTile.getRectangle();

        ProcessingTile currentTile = this.tileSegmenter.buildTile(targetRectangle.x, targetRectangle.y, targetRectangle.width, targetRectangle.height);
        BoundingBox region = currentTile.getRegion();

        Tile[] sourceTiles = new Tile[this.sourceBandNames.length];
        Rectangle imageRectangle = new Rectangle(region.getLeftX(), region.getTopY(), region.getWidth(), region.getHeight());
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            sourceTiles[i] = getSourceTile(band, imageRectangle);
        }

        try {
            this.tileSegmenter.runOneTileFirstSegmentation(sourceTiles, currentTile);
        } catch (Exception ex) {
            throw new OperatorException(ex);
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(GenericRegionMergingOp.class);
        }
    }
}
