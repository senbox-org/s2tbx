package org.esa.s2tbx.s2msi.resampler;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

/**
 * Created by obarrile on 05/07/2017.
 */
@OperatorMetadata(alias = "S2Resampling",
        category = "Optical/Geometric",
        authors = "Omar Barrilero",
        version = "1.0",
        internal = false,
        description = "Specific S2 resample algorithm")
public class S2ResamplingOp extends Operator {

    private static final String NAME_EXTENSION = "S2resampled";

    @SourceProduct(description = "The source product which is to be resampled.", label = "Name")
    Product sourceProduct;

    @TargetProduct(description = "The resampled target product.")
    Product targetProduct;


    @Parameter(alias = "resolution",
            label = "Output resolution",
            description = "The output resolution.",
            valueSet = {"10", "20", "60"},
            defaultValue = "60"
    )
    private String targetResolution;

    @Parameter(alias = "upsampling",
            label = "Upsampling method",
            description = "The method used for interpolation (upsampling to a finer resolution).",
            valueSet = {"Nearest", "Bilinear", "Bicubic"},
            defaultValue = "Bilinear"
    )
    private String upsamplingMethod;

    @Parameter(alias = "downsampling",
            label = "Downsampling method",
            description = "The method used for aggregation (downsampling to a coarser resolution).",
            valueSet = {"First", "Min", "Max", "Mean", "Median"},
            defaultValue = "Mean")
    private String downsamplingMethod;

    @Parameter(alias = "flagDownsampling",
            label = "Flag downsampling method",
            description = "The method used for aggregation (downsampling to a coarser resolution) of flags.",
            valueSet = {"First", "FlagAnd", "FlagOr", "FlagMedianAnd", "FlagMedianOr"},
            defaultValue = "First")
    private String flagDownsamplingMethod;

    @Parameter(label = "Resample on pyramid levels (for faster imaging)", defaultValue = "true",
            description = "This setting will increase performance when viewing the image, but accurate resamplings " +
                    "are only retrieved when zooming in on a pixel.")
    private boolean resampleOnPyramidLevels;

    private S2Resampler s2Resampler;

    @Override
    public void initialize() throws OperatorException {
        s2Resampler = new S2Resampler(Integer.parseInt(targetResolution));
        s2Resampler.setDownsamplingMethod(downsamplingMethod);
        s2Resampler.setFlagDownsamplingMethod(flagDownsamplingMethod);
        s2Resampler.setResampleOnPyramidLevels(resampleOnPyramidLevels);
        s2Resampler.setUpsamplingMethod(upsamplingMethod);

        if (!s2Resampler.canResample(sourceProduct)) {
            throw new OperatorException("Invalid S2 source product.");
        }

        targetProduct = s2Resampler.initialize(sourceProduct);
    }


    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException  {
        targetProduct = s2Resampler.resample(sourceProduct);       
    }

    /**
     * The SPI is used to register this operator in the graph processing framework
     * via the SPI configuration file
     * {@code META-INF/services/org.esa.snap.core.gpf.OperatorSpi}.
     * This class may also serve as a factory for new operator instances.
     *
     * @see OperatorSpi#createOperator()
     * @see OperatorSpi#createOperator(java.util.Map, java.util.Map)
     */
    public static class Spi extends OperatorSpi {
        public Spi() {
            super(S2ResamplingOp.class);
        }
    }
}
