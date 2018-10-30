package org.esa.s2tbx.fcc;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.s2tbx.fcc.trimming.PixelSourceBands;
import org.esa.s2tbx.fcc.mahalanobis.MahalanobisDistance;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Jean Coravu.
 */
public class MahalanobisDistanceTest {

    public MahalanobisDistanceTest() {

    }

    @Test
    public void testComputeValidRegionsInParallel() throws Exception {
        Int2ObjectMap<PixelSourceBands> validRegionStatistics = new Int2ObjectLinkedOpenHashMap<>();
        validRegionStatistics.put(1, new PixelSourceBands(0.03658f, 0.3489f, 0.1672f, 0.040451f));
        validRegionStatistics.put(2, new PixelSourceBands(0.05486f, 0.3466f, 0.16980f, 0.024763f));
        validRegionStatistics.put(3, new PixelSourceBands(0.037149f, 0.34544f, 0.154756f, 0.025699f));
        validRegionStatistics.put(4, new PixelSourceBands(0.03771f, 0.35033f, 0.1710159f, 0.04961942f));
        validRegionStatistics.put(5, new PixelSourceBands(0.039724f, 0.36974552f, 0.175908f, 0.060355f));
        validRegionStatistics.put(6, new PixelSourceBands(0.035685f, 0.3384305f, 0.16784738f, 0.04111339f));
        validRegionStatistics.put(7, new PixelSourceBands(0.0439721f, 0.3385603f, 0.149252f, 0.025938f));
        validRegionStatistics.put(8, new PixelSourceBands(0.037748f, 0.339788f, 0.171245f, 0.042440f));
        validRegionStatistics.put(9, new PixelSourceBands(0.03720115f, 0.31524312f, 0.14938368f, 0.03888046f));
        validRegionStatistics.put(10, new PixelSourceBands(0.0511194f, 0.28710383f, 0.139258f, 0.0364000f));
        validRegionStatistics.put(11, new PixelSourceBands(0.0554377f, 0.31724885f, 0.14809814f, 0.037076473f));

        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        double cumulativeProbability = 2.0d;

        Int2ObjectMap<PixelSourceBands> firstResult = MahalanobisDistance.computeValidRegionsInParallel(threadCount, threadPool, validRegionStatistics, cumulativeProbability);
        assertEquals(11, validRegionStatistics.size());
        assertEquals(7, firstResult.size());
        assertNotNull(firstResult.get(1));
        assertNotNull(firstResult.get(4));
        assertNotNull(firstResult.get(6));
        assertNotNull(firstResult.get(7));
        assertNotNull(firstResult.get(8));
        assertNotNull(firstResult.get(9));
        assertNotNull(firstResult.get(11));

        Int2ObjectMap<PixelSourceBands> secondResult = MahalanobisDistance.computeValidRegionsInParallel(threadCount, threadPool, firstResult, cumulativeProbability);
        assertEquals(7, firstResult.size());
        assertEquals(3, secondResult.size());
        assertNotNull(secondResult.get(1));
        assertNotNull(secondResult.get(6));
        assertNotNull(secondResult.get(8));
    }
}
