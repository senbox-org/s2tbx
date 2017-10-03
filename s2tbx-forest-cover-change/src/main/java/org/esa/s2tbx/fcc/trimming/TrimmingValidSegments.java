package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.fcc.mahalanobis.MahalanobisDistance;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class TrimmingValidSegments {
    private static final Logger logger = Logger.getLogger(TrimmingValidSegments.class.getName());

    private final Int2ObjectMap<AveragePixelsSourceBands> validRegionsMap;
    private final double degreesOfFreedom;

    public TrimmingValidSegments(double degreesOfFreedom) {
        this.degreesOfFreedom = degreesOfFreedom;
        this.validRegionsMap = new Int2ObjectLinkedOpenHashMap<>();
    }

    public void addPixelValuesBands(int segmentationPixelValue, float valueB4Band, float valueB8Band, float valueB11Band) {
        synchronized (this.validRegionsMap) {
            AveragePixelsSourceBands value = this.validRegionsMap.get(segmentationPixelValue);
            if (value == null) {
                value = new AveragePixelsSourceBands();
                this.validRegionsMap.put(segmentationPixelValue, value);
            }
            value.addPixelValuesBands(valueB4Band, valueB8Band, valueB11Band);
        }
    }

    public final IntSet processResult(int threadCount, Executor threadPool) throws Exception {
        Int2ObjectMap<PixelSourceBands> averagePixelsPerSegment = computeStatisticsPerRegion(this.validRegionsMap);

        doClose();

        IntSet validRegionIds = doTrimming(threadCount, threadPool, averagePixelsPerSegment, this.degreesOfFreedom);

        // reset the references
        ObjectIterator<PixelSourceBands> it = averagePixelsPerSegment.values().iterator();
        while (it.hasNext()) {
            PixelSourceBands value = it.next();
            WeakReference<PixelSourceBands> reference = new WeakReference<PixelSourceBands>(value);
            reference.clear();
        }
        averagePixelsPerSegment.clear();
        WeakReference<Int2ObjectMap<PixelSourceBands>> reference = new WeakReference<Int2ObjectMap<PixelSourceBands>>(averagePixelsPerSegment);
        reference.clear();

        return validRegionIds;
    }

    private void doClose() {
        ObjectIterator<AveragePixelsSourceBands> it = this.validRegionsMap.values().iterator();
        while (it.hasNext()) {
            AveragePixelsSourceBands value = it.next();
            WeakReference<AveragePixelsSourceBands> reference = new WeakReference<>(value);
            reference.clear();
        }
        this.validRegionsMap.clear();
    }

    private static IntSet doTrimming(int threadCount, Executor threadPool, Int2ObjectMap<PixelSourceBands> validRegionStatistics, double degreesOfFreedom) throws Exception {
        int initialValidRegionCount = validRegionStatistics.size();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start applying trimming: valid region count: "+ initialValidRegionCount);
        }

        ChiSquaredDistribution chi = new ChiSquaredDistribution(degreesOfFreedom);

        float[] confidenceLevels = new float[]{ForestCoverChangeConstants.CONFIDENCE_LEVEL_99, ForestCoverChangeConstants.CONFIDENCE_LEVEL_95, ForestCoverChangeConstants.CONFIDENCE_LEVEL_90};
        for (int i=0; i<confidenceLevels.length; i++) {
            double cumulativeProbability = chi.inverseCumulativeProbability(confidenceLevels[i]);

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Start applying the trimming on the valid regions: iteration: "+(i+1)+", Chi distribution: " + cumulativeProbability+", valid region count: " + validRegionStatistics.size());
            }

            boolean continueRunning = true;
            while (continueRunning) {
                Int2ObjectMap<PixelSourceBands> validStatistics = MahalanobisDistance.computeValidRegionsInParallel(threadCount, threadPool, validRegionStatistics, cumulativeProbability);
                if (validStatistics == null) {
                    continueRunning = false;
                } else {
                    if (validStatistics.size() == 0 || validRegionStatistics.size() == validStatistics.size()) {
                        continueRunning = false;
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

    private static Int2ObjectMap<PixelSourceBands> computeStatisticsPerRegion(Int2ObjectMap<AveragePixelsSourceBands> validRegionsMap) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute the average pixel values per region of "+validRegionsMap.size()+" valid regions before trimming");
        }

        Int2ObjectMap<PixelSourceBands> statistics = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();

        ObjectIterator<Int2ObjectMap.Entry<AveragePixelsSourceBands>> it = validRegionsMap.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<AveragePixelsSourceBands> entry = it.next();
            AveragePixelsSourceBands averagePixelValues = entry.getValue();

            if (averagePixelValues == null) {
                System.out.println("               ============ averagePixelValues==null");
            }
            float averageB4PixelValue = averagePixelValues.getMeanValueB4Band();
            float averageB8PixelValue = averagePixelValues.getMeanValueB8Band();
            float averageB11PixelValue = averagePixelValues.getMeanValueB11Band();
            float averageStandardDeviationB8PixelValue = averagePixelValues.getMeanStandardDeviationB8Band();

            PixelSourceBands averagePerBand = new PixelSourceBands(averageB4PixelValue, averageB8PixelValue, averageB11PixelValue, averageStandardDeviationB8PixelValue);
            statistics.put(entry.getIntKey(), averagePerBand);
        }

        return statistics;
    }
}

