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
import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractRegionParallelComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(AbstractRegionParallelComputing.class.getName());

    private final Int2ObjectMap<AveragePixelsSourceBands> validRegionsMap;

    protected AbstractRegionParallelComputing(int imageWidth, int imageHeight, int tileWidth, int tileHeight) {
        super(imageWidth, imageHeight, tileWidth, tileHeight);

        this.validRegionsMap = new Int2ObjectLinkedOpenHashMap<>();
    }

    protected final void addPixelValuesBands(int segmentationPixelValue, float valueB4Band, float valueB8Band, float valueB11Band) {
        synchronized (this.validRegionsMap) {
            AveragePixelsSourceBands value = this.validRegionsMap.get(segmentationPixelValue);
            if (value == null) {
                value = new AveragePixelsSourceBands();
                this.validRegionsMap.put(segmentationPixelValue, value);
            }
            value.addPixelValuesBands(valueB4Band, valueB8Band, valueB11Band);
        }
    }

    public final IntSet runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return processResult(threadCount, threadPool);
    }

    public final IntSet processResult(int threadCount, Executor threadPool) throws Exception {
        Int2ObjectMap<PixelSourceBands> differenceRegionsTrimming = computeStatisticsPerRegion(this.validRegionsMap);

        doClose();

        IntSet differenceTrimmingSet = doTrimming(threadCount, threadPool, differenceRegionsTrimming);

        ObjectIterator<PixelSourceBands> it = differenceRegionsTrimming.values().iterator();
        while (it.hasNext()) {
            PixelSourceBands value = it.next();
            WeakReference<PixelSourceBands> reference = new WeakReference<PixelSourceBands>(value);
            reference.clear();
        }
        differenceRegionsTrimming.clear();

        return differenceTrimmingSet;
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

    private static IntSet doTrimming(int threadCount, Executor threadPool, Int2ObjectMap<PixelSourceBands> validRegionStatistics) throws Exception {
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