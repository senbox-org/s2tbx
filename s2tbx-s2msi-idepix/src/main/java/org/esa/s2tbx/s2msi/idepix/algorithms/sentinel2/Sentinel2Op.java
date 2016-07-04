package org.esa.s2tbx.s2msi.idepix.algorithms.sentinel2;

import org.esa.s2tbx.s2msi.idepix.operators.Sentinel2CloudBufferOp;
import org.esa.s2tbx.s2msi.idepix.util.AlgorithmSelector;
import org.esa.s2tbx.s2msi.idepix.util.IdepixConstants;
import org.esa.s2tbx.s2msi.idepix.util.IdepixUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import java.util.HashMap;
import java.util.Map;

/**
 * Idepix operator for pixel identification and classification for Sentinel-2 (MSI instrument)
 *
 * @author olafd
 */
@OperatorMetadata(alias = "Idepix.Sentinel2",
        category = "Optical/Pre-Processing",
        version = "2.2",
        authors = "Olaf Danne",
        copyright = "(c) 2016 by Brockmann Consult",
        description = "Pixel identification and classification for Sentinel-2.")
public class Sentinel2Op extends Operator {

    private static final int LAND_WATER_MASK_RESOLUTION = 50;
    private static final int OVERSAMPLING_FACTOR_X = 3;
    private static final int OVERSAMPLING_FACTOR_Y = 3;

    @Parameter(defaultValue = "false",
            label = " Write TOA Reflectances to the target product",
            description = " Write TOA Reflectances to the target product")
    private boolean copyToaReflectances;

    @Parameter(defaultValue = "false",
            label = " Write Feature Values to the target product",
            description = " Write all Feature Values to the target product")
    private boolean copyFeatureValues;

    // NN stuff is deactivated unless we have a better net

    //    @Parameter(defaultValue = "1.95",
//            label = " NN cloud ambiguous lower boundary",
//            description = " NN cloud ambiguous lower boundary")
//    private double nnCloudAmbiguousLowerBoundaryValue;
    private double nnCloudAmbiguousLowerBoundaryValue = 1.95;

    //    @Parameter(defaultValue = "3.45",
//            label = " NN cloud ambiguous/sure separation value",
//            description = " NN cloud ambiguous cloud ambiguous/sure separation value")
//    private double nnCloudAmbiguousSureSeparationValue;
    private double nnCloudAmbiguousSureSeparationValue = 3.45;

    //    @Parameter(defaultValue = "4.3",
//            label = " NN cloud sure/snow separation value",
//            description = " NN cloud ambiguous cloud sure/snow separation value")
//    private double nnCloudSureSnowSeparationValue;
    private double nnCloudSureSnowSeparationValue = 4.3;

    //    @Parameter(defaultValue = "false",
//            label = " Apply NN for pixel classification purely (not combined with feature value approach)",
//            description = " Apply NN for pixelclassification purely (not combined with feature value  approach)")
//    private boolean applyNNPure;
    private boolean applyNNPure = false;

    //    @Parameter(defaultValue = "false",
//            label = " Ignore NN and only use feature value approach for pixel classification (if set, overrides previous option)",
//            description = " Ignore NN and only use feature value approach for pixel classification (if set, overrides previous option)")
//    private boolean ignoreNN;
    boolean ignoreNN = true;       // currently bad results. Wait for better S2 NN.

    //    @Parameter(defaultValue = "true",
//            label = " Write NN output value to the target product",
//            description = " Write NN output value to the target product")
//    private boolean copyNNValue = true;
    private boolean copyNNValue = false;

    //    @Parameter(defaultValue = "true",
//            label = " Refine pixel classification near coastlines",
//            description = "Refine pixel classification near coastlines. ")
    private boolean refineClassificationNearCoastlines = false; // todo later


    //    @Parameter(defaultValue = "true", label = " Compute cloud shadow")
    private boolean computeCloudShadow = false; // todo later

    @Parameter(defaultValue = "true", label = " Compute a cloud buffer")
    private boolean computeCloudBuffer;

    @Parameter(defaultValue = "2", interval = "[0,100]",
            label = " Width of cloud buffer (# of pixels)",
            description = " The width of the 'safety buffer' around a pixel identified as cloudy.")
    private int cloudBufferWidth;

    @Parameter(defaultValue = "0.01",
            label = " Threshold CW_THRESH",
            description = " Threshold CW_THRESH")
    private double cwThresh;

    @Parameter(defaultValue = "-0.11",
            label = " Threshold GCL_THRESH",
            description = " Threshold GCL_THRESH")
    private double gclThresh;

    @Parameter(defaultValue = "0.01",
            label = " Threshold CL_THRESH",
            description = " Threshold CL_THRESH")
    private double clThresh;


    @SourceProduct(alias = "l1cProduct",
            label = "Sentinel-2 MSI L1C product",
            description = "The Sentinel-2 MSI L1C product.")
    private Product sourceProduct;

    @TargetProduct(description = "The target product.")
    private Product targetProduct;

    private Product waterMaskProduct;
    private Product postProcessingProduct;
    private Product s2ClassifProduct;

    @Override
    public void initialize() throws OperatorException {
        final boolean inputProductIsValid = IdepixUtils.validateInputProduct(sourceProduct, AlgorithmSelector.MSI);
        if (!inputProductIsValid) {
            throw new OperatorException(IdepixConstants.INPUT_INCONSISTENCY_ERROR_MESSAGE);
        }

        if (IdepixUtils.isValidSentinel2(sourceProduct)) {
            processSentinel2();
        }
    }

    private void processSentinel2() {
        processLandWaterMask();

        Map<String, Product> inputProducts = new HashMap<>(4);
        inputProducts.put("l1c", sourceProduct);
        inputProducts.put("waterMask", waterMaskProduct);

        final Map<String, Object> pixelClassificationParameters = createPixelClassificationParameters();

        s2ClassifProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(Sentinel2ClassificationOp.class),
                                             pixelClassificationParameters, inputProducts);

//        AddElevationOp elevationOp = new AddElevationOp();
//        elevationOp.setParameterDefaultValues();
//        elevationOp.setSourceProduct(prelimClassifProduct);
//        s2ClassifProduct = elevationOp.getTargetProduct();

        if (refineClassificationNearCoastlines || computeCloudShadow || computeCloudBuffer) {
            // Post Cloud Classification: coastline refinement, cloud shadow, cloud buffer
            computePostProcessProduct();

            targetProduct = IdepixUtils.cloneProduct(s2ClassifProduct, true);

            Band cloudFlagBand = targetProduct.getBand(IdepixUtils.IDEPIX_CLASSIF_FLAGS);
            cloudFlagBand.setSourceImage(postProcessingProduct.getBand(IdepixUtils.IDEPIX_CLASSIF_FLAGS).getSourceImage());
        } else {
            targetProduct = s2ClassifProduct;
        }

        // new bit masks:
        IdepixUtils.setupIdepixCloudscreeningBitmasks(targetProduct);

        setTargetProduct(targetProduct);
    }

    private void processLandWaterMask() {
        HashMap<String, Object> waterMaskParameters = new HashMap<>();
        waterMaskParameters.put("resolution", LAND_WATER_MASK_RESOLUTION);
        waterMaskParameters.put("subSamplingFactorX", OVERSAMPLING_FACTOR_X);
        waterMaskParameters.put("subSamplingFactorY", OVERSAMPLING_FACTOR_Y);
        waterMaskProduct = GPF.createProduct("LandWaterMask", waterMaskParameters, sourceProduct);
    }

    private void computePostProcessProduct() {
        HashMap<String, Product> input = new HashMap<>();
        input.put("l1c", sourceProduct);
        input.put("s2Cloud", s2ClassifProduct);

        Map<String, Object> params = new HashMap<>();
        params.put("cloudBufferWidth", cloudBufferWidth);
        params.put("gaComputeCloudBuffer", computeCloudBuffer);
        params.put("gaComputeCloudShadow", computeCloudShadow);
        params.put("gaRefineClassificationNearCoastlines", refineClassificationNearCoastlines);
        final Product classifiedProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(Sentinel2PostProcessOp.class),
                                                            params, input);

        if (computeCloudBuffer) {
            input = new HashMap<>();
            input.put("classifiedProduct", classifiedProduct);
            params = new HashMap<>();
            params.put("cloudBufferWidth", cloudBufferWidth);
            postProcessingProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(Sentinel2CloudBufferOp.class),
                                                      params, input);
        } else {
            postProcessingProduct = classifiedProduct;
        }
    }

    private Map<String, Object> createPixelClassificationParameters() {
        Map<String, Object> gaCloudClassificationParameters = new HashMap<>(1);
        gaCloudClassificationParameters.put("copyToaReflectances", copyToaReflectances);
        gaCloudClassificationParameters.put("copyFeatureValues", copyFeatureValues);
        gaCloudClassificationParameters.put("applyNNPure", applyNNPure);
        gaCloudClassificationParameters.put("ignoreNN", ignoreNN);
        gaCloudClassificationParameters.put("nnCloudAmbiguousLowerBoundaryValue", nnCloudAmbiguousLowerBoundaryValue);
        gaCloudClassificationParameters.put("nnCloudAmbiguousSureSeparationValue", nnCloudAmbiguousSureSeparationValue);
        gaCloudClassificationParameters.put("nnCloudSureSnowSeparationValue", nnCloudSureSnowSeparationValue);
        gaCloudClassificationParameters.put("cloudBufferWidth", cloudBufferWidth);
        gaCloudClassificationParameters.put("cwThresh", cwThresh);
        gaCloudClassificationParameters.put("gclThresh", gclThresh);
        gaCloudClassificationParameters.put("clThresh", clThresh);

        return gaCloudClassificationParameters;
    }


    /**
     * The Service Provider Interface (SPI) for the operator.
     * It provides operator meta-data and is a factory for new operator instances.
     */
    public static class Spi extends OperatorSpi {

        public Spi() {
            super(Sentinel2Op.class);
        }
    }
}
