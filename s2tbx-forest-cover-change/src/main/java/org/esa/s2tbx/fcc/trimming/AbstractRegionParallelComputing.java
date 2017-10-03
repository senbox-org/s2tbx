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

    private final TrimmingValidSegments trimmingValidSegments;

    protected AbstractRegionParallelComputing(int imageWidth, int imageHeight, int tileWidth, int tileHeight, double degreesOfFreedom) {
        super(imageWidth, imageHeight, tileWidth, tileHeight);

        this.trimmingValidSegments = new TrimmingValidSegments(degreesOfFreedom);
    }

    protected final void addPixelValuesBands(int segmentationPixelValue, float valueB4Band, float valueB8Band, float valueB11Band) {
        synchronized (this.trimmingValidSegments) {
            this.trimmingValidSegments.addPixelValuesBands(segmentationPixelValue, valueB4Band, valueB8Band, valueB11Band);
        }
    }

    public final IntSet runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.trimmingValidSegments.processResult(threadCount, threadPool);
    }
}