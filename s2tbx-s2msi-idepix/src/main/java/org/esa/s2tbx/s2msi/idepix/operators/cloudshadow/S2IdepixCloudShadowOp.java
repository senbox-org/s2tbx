package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.util.SystemUtils;
import org.opengis.referencing.operation.MathTransform;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@OperatorMetadata(alias = "CCICloudShadow",
        category = "Optical",
        authors = "Grit Kirches, Michael Paperin, Olaf Danne, Tonio Fincke, Dagmar Mueller",
        copyright = "(c) Brockmann Consult GmbH",
        version = "1.0",
        description = "Algorithm detecting cloud shadow...")
public class S2IdepixCloudShadowOp extends Operator {

    private static Logger logger = SystemUtils.LOG;

    @SourceProduct(description = "The original input product")
    private Product l1cProduct;

    @SourceProduct(description = "The classification product.")
    private Product s2ClassifProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(description = "The mode by which clouds are detected. There are three options: Land/Water, Multiple Bands" +
            "or Single Band", valueSet = {"LandWater", "MultiBand", "SingleBand"}, defaultValue = "LandWater")
    private String mode;

    @Parameter(description = "Whether to also compute mountain shadow", defaultValue = "true")
    private boolean computeMountainShadow;

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

    @Parameter(description = "The digital elevation model.", defaultValue = "SRTM 3Sec", label = "Digital Elevation Model")
    private String demName = "SRTM 3Sec";

    public final static String BAND_NAME_CLOUD_SHADOW = "FlagBand";

    private Map<Integer, double[][]> meanReflPerTile = new HashMap<>();
    private Map<Integer, Integer> NCloudOverLand = new HashMap<>();
    private Map<Integer, Integer> NCloudOverWater = new HashMap<>();
    private Map<Integer, Integer> NValidPixelTile = new HashMap<>();

    @Override
    public void initialize() throws OperatorException {
        int sourceResolution = determineSourceResolution(l1cProduct);

        Product classificationProduct = getClassificationProduct(sourceResolution);

        HashMap<String, Product> preInput = new HashMap<>();
        preInput.put("s2ClassifProduct", classificationProduct);
        Map<String, Object> preParams = new HashMap<>();
        preParams.put("computeMountainShadow", computeMountainShadow);
        preParams.put("mode", mode);

        //Preprocessing:
        // No flags are created, only statistics generated to find the best offset along the illumination path.
        final String operatorAlias = OperatorSpi.getOperatorAlias(S2IdepixPreCloudShadowOp.class);
        final S2IdepixPreCloudShadowOp cloudShadowPreProcessingOperator =
                (S2IdepixPreCloudShadowOp) GPF.getDefaultInstance().createOperator(operatorAlias, preParams, preInput, null);

        //trigger computation of all tiles
        final OperatorExecutor operatorExecutor = OperatorExecutor.create(cloudShadowPreProcessingOperator);
        operatorExecutor.execute(ProgressMonitor.NULL);

        NCloudOverLand = cloudShadowPreProcessingOperator.getNCloudOverLandPerTile();
        NCloudOverWater = cloudShadowPreProcessingOperator.getNCloudOverWaterPerTile();
        meanReflPerTile = cloudShadowPreProcessingOperator.getMeanReflPerTile();
        NValidPixelTile = cloudShadowPreProcessingOperator.getNValidPixelTile();
        //writingMeanReflAlongPath(); // for development of minimum analysis.

        int[] bestOffsets = findOverallMinimumReflectance();

        int bestOffset = chooseBestOffset(bestOffsets);
        logger.fine("bestOffset all " + bestOffsets[0]);
        logger.fine("bestOffset land " + bestOffsets[1]);
        logger.fine("bestOffset water " + bestOffsets[2]);
        logger.fine("chosen Offset " + bestOffset);


        HashMap<String, Product> postInput = new HashMap<>();
        postInput.put("s2ClassifProduct", classificationProduct);
        //put in here the input products that are required by the post-processing operator
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("bestOffset", bestOffset);
        postParams.put("mode", mode);
        //put in here any parameters that might be requested by the post-processing operator

        //
        //Postprocessing
        //
        //Generation of all cloud shadow flags
        Product postProduct = GPF.createProduct("Idepix.Sentinel2.CloudShadow.Postprocess", postParams, postInput);

        setTargetProduct(prepareTargetProduct(sourceResolution, postProduct));
    }

    private int determineSourceResolution(Product product) throws OperatorException {
        final GeoCoding sceneGeoCoding = product.getSceneGeoCoding();
        if (sceneGeoCoding instanceof CrsGeoCoding) {
            final MathTransform imageToMapTransform = sceneGeoCoding.getImageToMapTransform();
            if (imageToMapTransform instanceof AffineTransform) {
                return (int)((AffineTransform) imageToMapTransform).getScaleX();
            }
        }
        throw new OperatorException("Invalid product");
    }

    private Product getClassificationProduct(int resolution) {
        if (resolution == 60) {
            return s2ClassifProduct;
        }

        HashMap<String, Product> resamplingInput = new HashMap<>();
        resamplingInput.put("sourceProduct", l1cProduct);
        Map<String, Object> resamplingParams = new HashMap<>();
        resamplingParams.put("upsampling", "Nearest");
        resamplingParams.put("downsampling", "First");
        resamplingParams.put("targetResolution", 60);
        Product resampledProduct = GPF.createProduct("Resample", resamplingParams, resamplingInput);

        HashMap<String, Product> classificationInput = new HashMap<>();
        classificationInput.put("sourceProduct", resampledProduct);
        Map<String, Object> classificationParams = new HashMap<>();
        classificationParams.put("computeMountainShadow", false);
        classificationParams.put("computeCloudShadow", false);
        classificationParams.put("computeCloudBuffer", false);
        classificationParams.put("cwThresh", cwThresh);
        classificationParams.put("gclThresh", gclThresh);
        classificationParams.put("clThresh", clThresh);
        classificationParams.put("demName", demName);

        return GPF.createProduct("Idepix.Sentinel2", classificationParams, classificationInput);
    }

    private Product prepareTargetProduct(int resolution, Product postProcessedProduct) {
        if (resolution == 60) {
            return postProcessedProduct;
        }

        HashMap<String, Product> resamplingInput = new HashMap<>();
        resamplingInput.put("sourceProduct", postProcessedProduct);
        Map<String, Object> resamplingParams = new HashMap<>();
        resamplingParams.put("upsampling", "Nearest");
        resamplingParams.put("downsampling", "First");
        resamplingParams.put("targetResolution", resolution);
        return GPF.createProduct("Resample", resamplingParams, resamplingInput);
    }

    private int chooseBestOffset(int[] bestOffset) {
        int NCloudWater = 0;
        int NCloudLand = 0;
        int out;
        if (NCloudOverWater.size() > 0) {
            for (int index : NCloudOverWater.keySet()) {
                NCloudWater += NCloudOverWater.get(index);
            }
        }
        if (NCloudOverLand.size() > 0) {
            for (int index : NCloudOverLand.keySet()) {
                NCloudLand += NCloudOverLand.get(index);
            }
        }
        int Nall = NCloudLand + NCloudWater;
        float relCloudLand = (float) NCloudLand / Nall;
        float relCloudWater = (float) NCloudWater / Nall;
        if (relCloudLand > 2 * relCloudWater) {
            out = bestOffset[1];
        } else if (relCloudWater > 2 * relCloudLand) {
            out = bestOffset[2];
        } else out = bestOffset[0];
        return out;
    }

    private int[] findOverallMinimumReflectance() {
        double[][] scaledTotalReflectance = new double[3][meanReflPerTile.get(0)[0].length];
        for (int j = 0; j < 3; j++) {
            /*Checking the meanReflPerTile:
                - if it has no relative minimum other than the first or the last value, it is excluded.
                - if it contains NaNs, it is excluded.
                Exclusion works by setting values to NaN.
            */
            for (int key : meanReflPerTile.keySet()) {
                double[][] meanValues = meanReflPerTile.get(key);
                boolean exclude = false;
                List<Integer> relativeMinimum = indecesRelativMaxInArray(meanValues[j]);
                if (relativeMinimum.contains(0)) relativeMinimum.remove(relativeMinimum.indexOf(0));
                if (relativeMinimum.contains(meanValues[j].length-1))
                    relativeMinimum.remove(relativeMinimum.indexOf(meanValues[j].length-1));

                //smallest relative minimum is in second part of the path -> exclude
                if (relativeMinimum.indexOf(0) > meanValues[j].length / 2.) exclude = true;
                if (relativeMinimum.size() == 0) exclude = true;
                if (exclude) {
                    for (int i = 0; i < meanValues[j].length; i++) {
                        meanValues[j][i] = Double.NaN;
                    }
                }
            }
            //Finding the minimum in brightness in the scaled mean function.
            for (int key : meanReflPerTile.keySet()) {
                double[][] meanValues = meanReflPerTile.get(key);
                double[] maxValue = new double[3];
                for (int i = 0; i < meanValues[j].length; i++) {
                    if (!Double.isNaN(meanValues[j][i])) {
                        if (meanValues[j][i] > maxValue[j]) {
                            maxValue[j] = meanValues[j][i];
                        }
                    }
                }
                for (int i = 0; i < meanValues[j].length; i++) {
                    if (!Double.isNaN(meanValues[j][i]) && maxValue[j] > 0) {
                        scaledTotalReflectance[j][i] += meanValues[j][i] / maxValue[j];
                    }
                }
            }
        }
        int[] offset = new int[3];
        for (int j = 0; j < 3; j++) {
            List<Integer> test = indecesRelativMaxInArray(scaledTotalReflectance[j]);
            if (test.contains(0)) test.remove(test.indexOf(0));
            if (test.contains(scaledTotalReflectance[j].length-1))
                test.remove(test.indexOf(scaledTotalReflectance[j].length-1));

            if (test.size() > 0) {
                offset[j] = test.get(0);
            }
        }
        return offset;
    }

    private List<Integer> indecesRelativMaxInArray(double[] x) {
        int lx = x.length;
        List<Integer> ID = new ArrayList<>();
        boolean valid = true;
        int i = 0;
        while (i < lx && valid) {
            if (Double.isNaN(x[i])) valid = false;
            i++;
        }
        if (valid) {
            double fac = -1.;

            if (fac * x[0] > fac * x[1]) ID.add(0);
            if (fac * x[lx - 1] > fac * x[lx - 2]) ID.add(lx - 1);

            for (i = 1; i < lx - 1; i++) {
                if (fac * x[i] > fac * x[i - 1] && fac * x[i] > fac * x[i + 1]) ID.add(i);
            }
        } else {
            ID.add(0);
            ID.add(lx - 1);
        }

        return ID;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixCloudShadowOp.class);
        }
    }

}
