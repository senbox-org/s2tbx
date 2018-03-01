package org.esa.s2tbx.s2msi.idepix.operators.mountainshadow;

import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.pointop.PixelOperator;
import org.esa.snap.core.gpf.pointop.ProductConfigurer;
import org.esa.snap.core.gpf.pointop.Sample;
import org.esa.snap.core.gpf.pointop.SourceSampleConfigurer;
import org.esa.snap.core.gpf.pointop.TargetSampleConfigurer;
import org.esa.snap.core.gpf.pointop.WritableSample;

/**
 * @author Tonio Fincke
 */
@OperatorMetadata(alias = "Idepix.Sentinel2.MountainShadow",
        version = "1.0",
        internal = true,
        authors = "Tonio Fincke",
        copyright = "(c) 2018 by Brockmann Consult",
        description = "Computes Mountain Shadow for a Sentinel 2 product with elevation data, solar angles, " +
                "and a CRS geocoding.")
public class S2IdepixMountainShadowOp extends PixelOperator {

    @SourceProduct
    private Product sourceProduct;

    private final static int SZA_INDEX = 0;
    private final static int SAA_INDEX = 1;
    private final static int SLOPE_INDEX = 2;
    private final static int ASPECT_INDEX = 3;
    private final static int ORIENTATION_INDEX = 4;

    private final static int MOUNTAIN_SHADOW_FLAG_BAND_INDEX = 0;


    public final static String MOUNTAIN_SHADOW_FLAG_BAND_NAME = "mountainShadowFlag";

    private Product saoProduct;

    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();
        sourceProduct = getSourceProduct();
        saoProduct = sourceProduct;
        if (sourceProduct.getBand(SlopeAspectOrientationOp.SLOPE_BAND_NAME) == null ||
                sourceProduct.getBand(SlopeAspectOrientationOp.ASPECT_BAND_NAME) == null ||
                sourceProduct.getBand(SlopeAspectOrientationOp.ORIENTATION_BAND_NAME) == null) {
            saoProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(SlopeAspectOrientationOp.class),
                                           GPF.NO_PARAMS, sourceProduct);
        }
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);
        productConfigurer.addBand(MOUNTAIN_SHADOW_FLAG_BAND_NAME, ProductData.TYPE_INT8);
    }

    @Override
    protected void configureSourceSamples(SourceSampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(SZA_INDEX, S2IdepixConstants.SUN_ZENITH_BAND_NAME, sourceProduct);
        sampleConfigurer.defineSample(SAA_INDEX, S2IdepixConstants.SUN_AZIMUTH_BAND_NAME, sourceProduct);
        sampleConfigurer.defineSample(SLOPE_INDEX, SlopeAspectOrientationOp.SLOPE_BAND_NAME, saoProduct);
        sampleConfigurer.defineSample(ASPECT_INDEX, SlopeAspectOrientationOp.ASPECT_BAND_NAME, saoProduct);
        sampleConfigurer.defineSample(ORIENTATION_INDEX, SlopeAspectOrientationOp.ORIENTATION_BAND_NAME, saoProduct);
    }

    @Override
    protected void configureTargetSamples(TargetSampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(MOUNTAIN_SHADOW_FLAG_BAND_INDEX, MOUNTAIN_SHADOW_FLAG_BAND_NAME);
    }

    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        final double cosBeta = computeCosBeta(sourceSamples[SZA_INDEX].getFloat(), sourceSamples[SAA_INDEX].getFloat(),
                                              sourceSamples[SLOPE_INDEX].getFloat(), sourceSamples[ASPECT_INDEX].getFloat(),
                                              sourceSamples[ORIENTATION_INDEX].getFloat());
        targetSamples[MOUNTAIN_SHADOW_FLAG_BAND_INDEX].set(cosBeta < 0);
    }

    /* package local for testing */
    static double computeCosBeta(float sza, float saa, float slope, float aspect, float orientation) {
        return Math.cos(Math.toRadians(sza)) * Math.cos(slope) + Math.sin(Math.toRadians(sza)) * Math.sin(slope) *
                Math.cos(Math.toRadians(saa) - (aspect + orientation));
    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(S2IdepixMountainShadowOp.class);
        }
    }
}
