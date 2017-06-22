package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.esa.s2tbx.fcc.ForestCoverChangeOp;
import org.esa.s2tbx.fcc.chi.distribution.ChiSquareDistribution;
import org.esa.s2tbx.fcc.mahalanobis.MahalanobisDistance;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.internal.OperatorExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrimmingHelper {
    private static final Logger logger = Logger.getLogger(TrimmingHelper.class.getName());

    private TrimmingHelper() {
    }

    private static double computeChiDistribution(int numberOfComponentsPerPixel) {
        double result = ChiSquareDistribution.computeChiSquare(numberOfComponentsPerPixel, 0.99d);
        double value = ChiSquareDistribution.computeChiSquare(numberOfComponentsPerPixel, 0.95d);
        if (result < value) {
            result = value;
        }
        value = ChiSquareDistribution.computeChiSquare(numberOfComponentsPerPixel, 0.9d);
        if (result < value) {
            result = value;
        }
        return result;
    }

    public static Int2ObjectMap<PixelSourceBands> doTrimming(Product segmentationSourceProduct, Product sourceCompositionProduct, int[] sourceBandIndices) {
        Int2ObjectMap<PixelSourceBands> statistics = computeTrimmingStatistics(segmentationSourceProduct, sourceCompositionProduct, sourceBandIndices);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Timming statistics per region size " + statistics.size());
        }

        ChiSquaredDistribution chi  = new ChiSquaredDistribution(ForestCoverChangeConstans.DEGREES_OF_FREEDOM);


        float[] confidenceLevels = new float[]{ForestCoverChangeConstans.CONFIDENCE_LEVEL_99, ForestCoverChangeConstans.CONFIDENCE_LEVEL_95, ForestCoverChangeConstans.CONFIDENCE_LEVEL_90};
        for(float confidence: confidenceLevels) {
            double  cumulativeProbability = chi.inverseCumulativeProbability(confidence);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "The chi distribution is " + cumulativeProbability);
            }
            while (true) {
                Object2FloatOpenHashMap<PixelSourceBands> result = MahalanobisDistance.computeMahalanobisSquareMatrix(statistics.values());
                if (result == null) {
                    break;
                } else {
                    Int2ObjectMap<PixelSourceBands> validStatistics = new Int2ObjectLinkedOpenHashMap<>();

                    ObjectIterator<Int2ObjectMap.Entry<PixelSourceBands>> it = statistics.int2ObjectEntrySet().iterator();
                    while (it.hasNext()) {
                        Int2ObjectMap.Entry<PixelSourceBands> entry = it.next();
                        PixelSourceBands point = entry.getValue();
                        float distance = result.getFloat(point);
                        if (distance <= cumulativeProbability) {
                            validStatistics.put(entry.getIntKey(), point);
                        }else{
                            if (logger.isLoggable(Level.FINE)) {
                                logger.log(Level.FINE, ""); // add an empty line
                                logger.log(Level.FINE, "deleted value is " + distance);
                            }
                        }

                    }
                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, ""); // add an empty line
                        logger.log(Level.FINE, "number of validated statistics are  " + validStatistics.size());
                    }

                    if (validStatistics.size() == 0 || statistics.size() == validStatistics.size()) {
                        break;
                    } else {
                        statistics = validStatistics;
                    }
                }
            }
        }
        return statistics;

    }

    private static Int2ObjectMap<PixelSourceBands> computeStatistics(Int2ObjectMap<List<PixelSourceBands>> trimmingStatistics) {
        Int2ObjectMap<PixelSourceBands> statistics = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();
        ObjectIterator<Int2ObjectMap.Entry<List<PixelSourceBands>>> it = trimmingStatistics.int2ObjectEntrySet().iterator();

        while (it.hasNext()) {
            Int2ObjectMap.Entry<List<PixelSourceBands>> entry = it.next();
            List<PixelSourceBands> pixelsList = entry.getValue();

            float sumB4PixelValue = 0.0f;
            float sumB8PixelValue = 0.0f;
            float sumB11PixelValue = 0.0f;
            float sumStandardDeviationB8PixelValue = 0.0f;
            for (PixelSourceBands pixelValues: pixelsList) {
                sumB4PixelValue += pixelValues.getMeanValueB4Band();
                sumB8PixelValue += pixelValues.getMeanValueB8Band();
                sumB11PixelValue += pixelValues.getMeanValueB11Band();
                sumStandardDeviationB8PixelValue += pixelValues.getStandardDeviationValueB8Band();
            }

            float averageB4PixelValue = sumB4PixelValue / pixelsList.size();
            float averageB8PixelValue = sumB8PixelValue / pixelsList.size();
            float averageB11PixelValue = sumB11PixelValue / pixelsList.size();
            // compute the standard deviation
            float averageStandardDeviationB8PixelValue = sumStandardDeviationB8PixelValue / pixelsList.size();
            float sum = 0.0f;
            for (PixelSourceBands pixelValues: pixelsList) {
                double value = pixelValues.getStandardDeviationValueB8Band() - averageStandardDeviationB8PixelValue;
                sum += Math.pow((value), 2);
            }
            float average = sum / (float)pixelsList.size();
            averageStandardDeviationB8PixelValue = (float)Math.sqrt(average);

            PixelSourceBands averagePerBand = new PixelSourceBands(averageB4PixelValue, averageB8PixelValue, averageB11PixelValue, averageStandardDeviationB8PixelValue);
            statistics.put(entry.getIntKey(), averagePerBand);
        }

        return statistics;
    }

    private static Int2ObjectMap<PixelSourceBands> computeTrimmingStatistics(Product segmentationSourceProduct, Product sourceProduct, int[] sourceBandIndices) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sourceBandIndices", sourceBandIndices);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("segmentationSourceProduct", segmentationSourceProduct);
        sourceProducts.put("sourceProduct", sourceProduct);
        TrimmingRegionComputingOp trimRegOp = (TrimmingRegionComputingOp) GPF.getDefaultInstance().createOperator("TrimmingRegionComputingOp", parameters, sourceProducts, null);
        trimRegOp.getTargetProduct();

        OperatorExecutor executor = OperatorExecutor.create(trimRegOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        Int2ObjectMap<List<PixelSourceBands>> trimmingStatistics = trimRegOp.getPixelsStatistics();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Timming statistics size " + trimmingStatistics.size());
        }

        return computeStatistics(trimmingStatistics);
    }
}
