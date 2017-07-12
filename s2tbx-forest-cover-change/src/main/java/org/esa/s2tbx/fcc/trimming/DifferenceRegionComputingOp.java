package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.s2tbx.fcc.common.AveragePixelsSourceBands;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstans;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "TrimmingRegionComputingOp",
        version="1.0",
        category = "",
        description = "Creates a hash map containing the values from the source bands for a respective segmentation region",
        authors = "Razvan Dumitrascu, Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")

public class DifferenceRegionComputingOp extends AbstractRegionComputingOp {

    @SourceProduct(alias = "currentSourceProduct", description = "The current source product to be used for trimming.")
    private Product currentSourceProduct;

    @SourceProduct(alias = "previousSourceProductETM", description = "The previous source product to be used for trimming.")
    private Product previousSourceProductETM;

    @SourceProduct(alias = "unionMask", description = "The source products to be used for trimming.")
    private Product unionMask;


    private Int2ObjectMap<AveragePixelsSourceBands> validRegionsMap;

    @Override
    public void initialize() throws OperatorException {
        super.initialize();
        validateSourceProducts();

    }
    @Override
    protected float getFirstBandBandValue(int x, int y) {
        Band firstBandCurrentProduct = this.currentSourceProduct.getBandAt(sourceBandIndices[0]);
        Band firstBandPreviousProduct = this.previousSourceProductETM.getBandAt(sourceBandIndices[0]);
        return firstBandCurrentProduct.getSampleFloat(x, y) - firstBandPreviousProduct.getSampleFloat(x, y);
    }

    @Override
    protected float getSecondBandBandValue(int x, int y) {
        Band secondBandCurrentProduct = this.currentSourceProduct.getBandAt(sourceBandIndices[1]);
        Band secondBandPreviousProduct = this.previousSourceProductETM.getBandAt(sourceBandIndices[1]);
        return secondBandCurrentProduct.getSampleFloat(x, y) - secondBandPreviousProduct.getSampleFloat(x, y);
    }

    @Override
    protected float getThirdBandBandValue(int x, int y) {
        Band thirdBandCurrentProduct = this.currentSourceProduct.getBandAt(sourceBandIndices[2]);
        Band thirdBandPreviousProduct = this.previousSourceProductETM.getBandAt(sourceBandIndices[2]);
        return thirdBandCurrentProduct.getSampleFloat(x, y) - thirdBandPreviousProduct.getSampleFloat(x, y);
    }

    @Override
    protected boolean isSegmentationPixelValid(int x, int y, int segmentationPixelValue) {
        return unionMask.getBandAt(0).getSampleFloat(x, y) != ForestCoverChangeConstans.NO_DATA_VALUE;
    }

    private void validateSourceProducts() {
        if (this.currentSourceProduct.isMultiSize()) {
            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
                            "Please consider resampling it so that all rasters have the same size.",
                    this.currentSourceProduct.getName());
            throw new OperatorException(message);
        }
        if ((this.currentSourceProduct.getSceneRasterHeight() != this.segmentationSourceProduct.getSceneRasterHeight()) ||
                (this.currentSourceProduct.getSceneRasterWidth() != this.segmentationSourceProduct.getSceneRasterWidth())) {
            String message = String.format("Source product '%s' must have the same scene raster size as the source Composition Product '%s'.\n" +
                            "Please consider resampling it so that the 2 products have the same size.",
                    this.segmentationSourceProduct.getName(), this.currentSourceProduct.getName());
            throw new OperatorException(message);
        }
        if (this.previousSourceProductETM.isMultiSize()) {
            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
                            "Please consider resampling it so that all rasters have the same size.",
                    this.previousSourceProductETM.getName());
            throw new OperatorException(message);
        }
        if ((this.previousSourceProductETM.getSceneRasterHeight() != this.segmentationSourceProduct.getSceneRasterHeight()) ||
                (this.previousSourceProductETM.getSceneRasterWidth() != this.segmentationSourceProduct.getSceneRasterWidth())) {
            String message = String.format("Source product '%s' must have the same scene raster size as the source Composition Product '%s'.\n" +
                            "Please consider resampling it so that the 2 products have the same size.",
                    this.segmentationSourceProduct.getName(), this.previousSourceProductETM.getName());
            throw new OperatorException(message);
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi(){
            super(DifferenceRegionComputingOp.class);
        }
    }
}
