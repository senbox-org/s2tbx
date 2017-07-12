package org.esa.s2tbx.fcc.trimming;

import java.util.logging.Logger;

import org.esa.s2tbx.fcc.common.ForestCoverChangeConstans;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "TrimmingRegionComputingOp",
        version="1.0",
        category = "",
        description = "Creates a hash map containing the values from the source bands for a respective segmentation region",
        authors = "Razvan Dumitrascu, Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class TrimmingRegionComputingOp extends AbstractRegionComputingOp {
    private static final Logger logger = Logger.getLogger(TrimmingRegionComputingOp.class.getName());

    @SourceProduct(alias = "sourceCompositionProduct", description = "The source products to be used for trimming.")
    private Product sourceProduct;

    public TrimmingRegionComputingOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        super.initialize();
        validateSourceProducts();

    }

    @Override
    protected float getFirstBandBandValue(int x, int y) {
        Band firstBand = this.sourceProduct.getBandAt(sourceBandIndices[0]);
        return firstBand.getSampleFloat(x, y);
    }

    @Override
    protected float getSecondBandBandValue(int x, int y) {
        Band secondBand = this.sourceProduct.getBandAt(sourceBandIndices[1]);
        return secondBand.getSampleFloat(x, y);
    }

    @Override
    protected float getThirdBandBandValue(int x, int y) {
        Band thirdBand = this.sourceProduct.getBandAt(sourceBandIndices[2]);
        return thirdBand.getSampleFloat(x, y);
    }

    @Override
    protected boolean isSegmentationPixelValid(int x, int y, int segmentationPixelValue) {
        return segmentationPixelValue != ForestCoverChangeConstans.NO_DATA_VALUE;

    }

    private void validateSourceProducts() {
        if (this.sourceProduct.isMultiSize()) {
            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
                            "Please consider resampling it so that all rasters have the same size.",
                    this.sourceProduct.getName());
            throw new OperatorException(message);
        }
        if ((this.sourceProduct.getSceneRasterHeight() != this.segmentationSourceProduct.getSceneRasterHeight()) ||
                (this.sourceProduct.getSceneRasterWidth() != this.segmentationSourceProduct.getSceneRasterWidth())) {
            String message = String.format("Source product '%s' must have the same scene raster size as the source Composition Product '%s'.\n" +
                            "Please consider resampling it so that the 2 products have the same size.",
                    this.segmentationSourceProduct.getName(), this.sourceProduct.getName());
            throw new OperatorException(message);
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi(){
            super(TrimmingRegionComputingOp.class);
        }
    }
}
