package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.esa.s2tbx.fcc.common.AveragePixelsSourceBands;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.fcc.common.PixelSourceBands;
import org.esa.s2tbx.fcc.mahalanobis.MahalanobisDistance;
import org.esa.snap.core.datamodel.Product;

import java.awt.Dimension;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrimmingHelper {
    private static final Logger logger = Logger.getLogger(TrimmingHelper.class.getName());

    private TrimmingHelper() {
    }

    public static IntSet doTrimming(int threadCount, Executor threadPool, Int2ObjectMap<PixelSourceBands> validRegionStatistics) throws Exception {
        int initialValidRegionCount = validRegionStatistics.size();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start applying trimming: valid region count: "+ initialValidRegionCount);
        }

        ChiSquaredDistribution chi = new ChiSquaredDistribution(ForestCoverChangeConstants.DEGREES_OF_FREEDOM);

        float[] confidenceLevels = new float[]{ForestCoverChangeConstants.CONFIDENCE_LEVEL_99, ForestCoverChangeConstants.CONFIDENCE_LEVEL_95, ForestCoverChangeConstants.CONFIDENCE_LEVEL_90};
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

        return new IntOpenHashSet(validRegionStatistics.keySet());
    }

    public static IntSet computeTrimmingStatistics(int threadCount, Executor threadPool, Product segmentationSourceProduct,
                                                   Product sourceProduct, int[] sourceBandIndices, Dimension tileSize)
                                                   throws Exception {

        TrimmingRegionComputingHelper helper = new TrimmingRegionComputingHelper(segmentationSourceProduct, sourceProduct, sourceBandIndices, tileSize.width, tileSize.height);
        IntSet segmentationTrimmingRegionKeys = helper.computeRegionsInParallel(threadCount, threadPool);

        helper = null;
        System.gc();

        return segmentationTrimmingRegionKeys;
    }

    public static Int2ObjectMap<PixelSourceBands> computeStatisticsPerRegion(Int2ObjectMap<AveragePixelsSourceBands> validRegionsMap) {
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
