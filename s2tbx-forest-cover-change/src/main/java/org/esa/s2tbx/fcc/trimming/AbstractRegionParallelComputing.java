package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.common.AveragePixelsSourceBands;
import org.esa.s2tbx.fcc.common.PixelSourceBands;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

/**
 * @author Jean Coravu
 */
public abstract class AbstractRegionParallelComputing extends AbstractImageTilesParallelComputing {
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

    private void doClose() {
        ObjectIterator<AveragePixelsSourceBands> it = this.validRegionsMap.values().iterator();
        while (it.hasNext()) {
            AveragePixelsSourceBands value = it.next();
            WeakReference<AveragePixelsSourceBands> reference = new WeakReference<>(value);
            reference.clear();
        }
        this.validRegionsMap.clear();
    }

    public final IntSet computeRegionsInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        Int2ObjectMap<PixelSourceBands> differenceRegionsTrimming = TrimmingHelper.computeStatisticsPerRegion(this.validRegionsMap);

        doClose();

        IntSet differenceTrimmingSet = TrimmingHelper.doTrimming(threadCount, threadPool, differenceRegionsTrimming);

        ObjectIterator<PixelSourceBands> it = differenceRegionsTrimming.values().iterator();
        while (it.hasNext()) {
            PixelSourceBands value = it.next();
            WeakReference<PixelSourceBands> reference = new WeakReference<PixelSourceBands>(value);
            reference.clear();
        }
        differenceRegionsTrimming.clear();

        return differenceTrimmingSet;
    }
}