package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.esa.s2tbx.fcc.mahalanobis.MahalanobisDistance;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.internal.OperatorExecutor;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrimmingHelper {
    private static final Logger logger = Logger.getLogger(TrimmingHelper.class.getName());

    private TrimmingHelper() {
    }

    public static IntSet doTrimming(int threadCount, Executor threadPool, Product segmentationSourceProduct, Product sourceCompositionProduct, int[] sourceBandIndices)
                                    throws Exception {

        Int2ObjectMap<PixelSourceBands> validRegionStatistics = computeTrimmingStatistics(threadCount, threadPool, segmentationSourceProduct, sourceCompositionProduct, sourceBandIndices);

        int initialValidRegionCount = validRegionStatistics.size();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start applying trimming: valid region count: "+ initialValidRegionCount);
        }

        ChiSquaredDistribution chi  = new ChiSquaredDistribution(ForestCoverChangeConstans.DEGREES_OF_FREEDOM);

        float[] confidenceLevels = new float[]{ForestCoverChangeConstans.CONFIDENCE_LEVEL_99, ForestCoverChangeConstans.CONFIDENCE_LEVEL_95, ForestCoverChangeConstans.CONFIDENCE_LEVEL_90};
        for (int i=0; i<confidenceLevels.length; i++) {
            double cumulativeProbability = chi.inverseCumulativeProbability(confidenceLevels[i]);

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Start applying the trimming on the valid regions: iteration: "+(i+1)+", Chi distribution: " + cumulativeProbability+", valid region count: " + validRegionStatistics.size());
            }

            boolean continueRunning = true;
            while (continueRunning) {
                Int2ObjectMap<PixelSourceBands> validStatistics = MahalanobisDistance.filterValidRegionsUsingMahalanobisDistance(threadCount, threadPool, validRegionStatistics, cumulativeProbability);
                if (validStatistics == null) {
                    continueRunning = false;//break;
                } else {
                    if (validStatistics.size() == 0 || validRegionStatistics.size() == validStatistics.size()) {
                        continueRunning = false;//break;
                    } else {
                        validRegionStatistics = validStatistics;
                    }
                }
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            int removedValidRegionCount = initialValidRegionCount - validRegionStatistics.size();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Finish applying trimming: valid region count: "+ validRegionStatistics.size()+", removed region count: " + removedValidRegionCount);
        }

//        IntSet trimmingRegionKeys = new IntOpenHashSet(validRegionStatistics.keySet());
//        IntIterator it = validRegionStatistics.keySet().iterator();
//        while (it.hasNext()) {
//            trimmingRegionKeys.add(it.nextInt());
//        }
//
//        return validRegionStatistics;
        return new IntOpenHashSet(validRegionStatistics.keySet());
    }

    private static Int2ObjectMap<PixelSourceBands> computeTrimmingStatistics(int threadCount, Executor threadPool, Product segmentationSourceProduct, Product sourceProduct, int[] sourceBandIndices)
                                                                             throws Exception {

//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("sourceBandIndices", sourceBandIndices);
//        Map<String, Product> sourceProducts = new HashMap<>();
//        sourceProducts.put("segmentationSourceProduct", segmentationSourceProduct);
//        sourceProducts.put("sourceProduct", sourceProduct);
//        TrimmingRegionComputingOp trimRegOp = (TrimmingRegionComputingOp) GPF.getDefaultInstance().createOperator("TrimmingRegionComputingOp", parameters, sourceProducts, null);
//        trimRegOp.getTargetProduct();
//
//        OperatorExecutor executor = OperatorExecutor.create(trimRegOp);
//        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
//
//        return computeStatisticsPerRegion(trimRegOp.getValidRegionsMap());

        Dimension tileSize = JAI.getDefaultTileSize();
        TrimmingRegionComputingHelper helper = new TrimmingRegionComputingHelper(segmentationSourceProduct, sourceProduct, sourceBandIndices, tileSize.width, tileSize.height);
        Int2ObjectMap<AveragePixelsSourceBands> validRegionsMap = helper.computeRegionsInParallel(threadCount, threadPool);
        Int2ObjectMap<PixelSourceBands> computeStatisticsPerRegion = computeStatisticsPerRegion(validRegionsMap);
        helper.doClose();
        helper = null;
        System.gc();
        return computeStatisticsPerRegion;
    }

    private static Int2ObjectMap<PixelSourceBands> computeStatisticsPerRegion(Int2ObjectMap<AveragePixelsSourceBands> validRegionsMap) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute the average pixel values per region of "+validRegionsMap.size()+" valid regions before trimming");
        }

        Int2ObjectMap<PixelSourceBands> statistics = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();

        ObjectIterator<Int2ObjectMap.Entry<AveragePixelsSourceBands>> it = validRegionsMap.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<AveragePixelsSourceBands> entry = it.next();
            AveragePixelsSourceBands pixelsList = entry.getValue();

            float averageB4PixelValue  = pixelsList.getMeanValueB4Band();
            float averageB8PixelValue  = pixelsList.getMeanValueB8Band();
            float averageB11PixelValue = pixelsList.getMeanValueB11Band();
            float averageStandardDeviationB8PixelValue = pixelsList.getMeanStandardDeviationB8Band();

            PixelSourceBands averagePerBand = new PixelSourceBands(averageB4PixelValue, averageB8PixelValue, averageB11PixelValue, averageStandardDeviationB8PixelValue);
            statistics.put(entry.getIntKey(), averagePerBand);
        }

        return statistics;
    }
}
