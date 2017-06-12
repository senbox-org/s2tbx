package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.chi.distribution.ChiSquareDistribution;
import org.esa.s2tbx.fcc.mahalanobis.MahalanobisDistance;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.internal.OperatorExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrimmingHelper {

    private TrimmingHelper() {
    }

    private static double computeChiDistribution(int numberOfComponentsPerPixel) {
        double result = ChiSquareDistribution.computeChiSquare(numberOfComponentsPerPixel, 12.5916d);
        System.out.println("\ncomputeChiDistribution result="+result);

        double value = ChiSquareDistribution.computeChiSquare(numberOfComponentsPerPixel, 16.8119d);
        System.out.println("computeChiDistribution value1="+value);

        if (result < value) {
            result = value;
        }
        value = ChiSquareDistribution.computeChiSquare(numberOfComponentsPerPixel, 22.4577d);
        if (result < value) {
            result = value;
        }
        System.out.println("computeChiDistribution value1="+value+" value2="+value+"  => result="+result);
        return result;
    }

    public static Int2ObjectMap<PixelSourceBands> doMahalanobis(Product initialSegmentationProduct, Product sourceCompositionProduct,
                                                                int[] bandsUsed, Int2ObjectMap<PixelSourceBands> trimmingStatistics) {

        Int2ObjectMap<List<PixelSourceBands>> trimmingStatistics11 = computeTrimmingStatistics(initialSegmentationProduct, sourceCompositionProduct, bandsUsed);
        Int2ObjectMap<PixelSourceBands> statistics = computeStatistics(trimmingStatistics11);

        return null;
    }

    public static Int2ObjectMap<PixelSourceBands> doTrimming(Product segmentationSourceProduct, Product sourceCompositionProduct, int[] bandsUsed) {
        Int2ObjectMap<List<PixelSourceBands>> trimmingStatistics = computeTrimmingStatistics(segmentationSourceProduct, sourceCompositionProduct, bandsUsed);
        Int2ObjectMap<PixelSourceBands> statistics = computeStatistics(trimmingStatistics);

        double chi = computeChiDistribution(4);

        while (true) {
            Object2DoubleOpenHashMap<PixelSourceBands> result = MahalanobisDistance.computeMahalanobisSquareMatrix(statistics.values());
            if (result == null) {
                break;
            } else {
                Int2ObjectMap<PixelSourceBands> validStatistics = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();

                ObjectIterator<Int2ObjectMap.Entry<PixelSourceBands>> it = statistics.int2ObjectEntrySet().iterator();
                while (it.hasNext()) {
                    Int2ObjectMap.Entry<PixelSourceBands> entry = it.next();
                    PixelSourceBands point = entry.getValue();
                    double distance = result.getDouble(point);
                    if (distance <= chi) {
                        validStatistics.put(entry.getIntKey(), point);
                    }
                }

                if (validStatistics.size() == 0 || statistics.size() == validStatistics.size()) {
                    break;
                } else {
                    statistics = validStatistics;
                    validStatistics = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();
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

            double sumB4PixelValue = 0.0d;
            double sumB8PixelValue = 0.0d;
            double sumB11PixelValue = 0.0d;
            double sumStandardDeviationB8PixelValue = 0.0d;
            for (PixelSourceBands pixelValues: pixelsList) {
                sumB4PixelValue += pixelValues.getMeanValueB4Band();
                sumB8PixelValue += pixelValues.getMeanValueB8Band();
                sumB11PixelValue += pixelValues.getMeanValueB11Band();
                sumStandardDeviationB8PixelValue += pixelValues.getStandardDeviationValueB8Band();
            }

            double averageB4PixelValue = sumB4PixelValue / pixelsList.size();
            double averageB8PixelValue = sumB8PixelValue / pixelsList.size();
            double averageB11PixelValue = sumB11PixelValue / pixelsList.size();
            // compute the standard deviation
            double averageStandardDeviationB8PixelValue = sumStandardDeviationB8PixelValue / pixelsList.size();
            double sum = 0.0d;
            for (PixelSourceBands pixelValues: pixelsList) {
                double value = pixelValues.getStandardDeviationValueB8Band() - averageStandardDeviationB8PixelValue;
                sum += Math.pow((value), 2);
            }
            double average = sum / (double)pixelsList.size();
            averageStandardDeviationB8PixelValue = Math.sqrt(average);

            PixelSourceBands averagePerBand = new PixelSourceBands(averageB4PixelValue, averageB8PixelValue, averageB11PixelValue, averageStandardDeviationB8PixelValue);
            statistics.put(entry.getIntKey(), averagePerBand);
        }

        return statistics;
    }

    private static Int2ObjectMap<List<PixelSourceBands>> computeTrimmingStatistics(Product segmentationSourceProduct, Product sourceCompositionProduct, int[] bandsUsed) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("bandsUsed", bandsUsed);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("segmentationSourceProduct", segmentationSourceProduct);
        sourceProducts.put("sourceCompositionProduct", sourceCompositionProduct);
        TrimmingRegionComputingOp trimRegOp = (TrimmingRegionComputingOp) GPF.getDefaultInstance().createOperator("TrimmingRegionComputingOp", parameters, sourceProducts, null);
        trimRegOp.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(trimRegOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        return trimRegOp.getPixelsStatistics();
    }
}
