package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by jcoravu on 4/10/2017.
 */
public class TrimmingValidSegmentsTest {

    public TrimmingValidSegmentsTest() {
    }

    @Test
    public void testValidSegments() throws Exception {
        TrimmingValidSegments trimmingValidSegments = new TrimmingValidSegments(2.5d);

        trimmingValidSegments.addPixelValuesBands(48234, 0.0705f, 0.3236f, 0.1866f);
        trimmingValidSegments.addPixelValuesBands(24459, 0.0705f, 0.2973f, 0.1859f);
        trimmingValidSegments.addPixelValuesBands(16, 0.0569f, 0.2572f, 0.1685f);
        trimmingValidSegments.addPixelValuesBands(48234, 0.0705f, 0.3236f, 0.1866f);
        trimmingValidSegments.addPixelValuesBands(19, 0.0571f, 0.2952f, 0.1527f);
        trimmingValidSegments.addPixelValuesBands(48235, 0.0564f, 0.3401f, 0.1935f);
        trimmingValidSegments.addPixelValuesBands(24459, 0.0706f, 0.2795f, 0.1859f);
        trimmingValidSegments.addPixelValuesBands(48235, 0.0564f, 0.3401f, 0.1935f);
        trimmingValidSegments.addPixelValuesBands(24460, 0.0676f, 0.2934f, 0.2056f);
        trimmingValidSegments.addPixelValuesBands(48235, 0.0633f, 0.3264f, 0.1935f);
        trimmingValidSegments.addPixelValuesBands(24460, 0.0664f, 0.299f, 0.2056f);
        trimmingValidSegments.addPixelValuesBands(48236, 0.0585f, 0.3079f, 0.1925f);
        trimmingValidSegments.addPixelValuesBands(24460, 0.0655f, 0.3083f, 0.202f);
        trimmingValidSegments.addPixelValuesBands(48235, 0.0633f, 0.3264f, 0.1935f);
        trimmingValidSegments.addPixelValuesBands(19, 0.0532f, 0.2491f, 0.1527f);
        trimmingValidSegments.addPixelValuesBands(48236, 0.0585f, 0.3079f, 0.1925f);
        trimmingValidSegments.addPixelValuesBands(24460, 0.0653f, 0.3197f, 0.202f);
        trimmingValidSegments.addPixelValuesBands(48237, 0.0554f, 0.3108f, 0.1863f);
        trimmingValidSegments.addPixelValuesBands(24460, 0.0623f, 0.3333f, 0.1958f);
        trimmingValidSegments.addPixelValuesBands(48238, 0.0546f, 0.2918f, 0.1649f);
        trimmingValidSegments.addPixelValuesBands(48237, 0.0554f, 0.3108f, 0.1863f);
        trimmingValidSegments.addPixelValuesBands(48238, 0.0549f, 0.2957f, 0.1649f);
        trimmingValidSegments.addPixelValuesBands(48238, 0.0546f, 0.2918f, 0.1649f);
        trimmingValidSegments.addPixelValuesBands(48239, 0.0537f, 0.2631f, 0.1623f);
        trimmingValidSegments.addPixelValuesBands(48238, 0.0549f, 0.2957f, 0.1649f);
        trimmingValidSegments.addPixelValuesBands(24461, 0.0646f, 0.3396f, 0.1958f);
        trimmingValidSegments.addPixelValuesBands(20, 0.0545f, 0.2874f, 0.1301f);
        trimmingValidSegments.addPixelValuesBands(24461, 0.0655f, 0.3348f, 0.1788f);
        trimmingValidSegments.addPixelValuesBands(20, 0.0549f, 0.2265f, 0.1301f);
        trimmingValidSegments.addPixelValuesBands(24461, 0.0658f, 0.3141f, 0.1788f);
        trimmingValidSegments.addPixelValuesBands(20, 0.0546f, 0.2096f, 0.1463f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.0643f, 0.2931f, 0.1694f);
        trimmingValidSegments.addPixelValuesBands(21, 0.0587f, 0.2711f, 0.1463f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.0659f, 0.282f, 0.1694f);
        trimmingValidSegments.addPixelValuesBands(22, 0.0695f, 0.3267f, 0.1901f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.0593f, 0.2893f, 0.1643f);
        trimmingValidSegments.addPixelValuesBands(48239, 0.0537f, 0.2631f, 0.1623f);
        trimmingValidSegments.addPixelValuesBands(48240, 0.0539f, 0.3033f, 0.1719f);
        trimmingValidSegments.addPixelValuesBands(48240, 0.0539f, 0.3033f, 0.1719f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.0583f, 0.3048f, 0.1643f);
        trimmingValidSegments.addPixelValuesBands(22, 0.074f, 0.3159f, 0.1901f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.0591f, 0.2939f, 0.1638f);
        trimmingValidSegments.addPixelValuesBands(48240, 0.0538f, 0.3194f, 0.1755f);
        trimmingValidSegments.addPixelValuesBands(48240, 0.0538f, 0.3194f, 0.1755f);
        trimmingValidSegments.addPixelValuesBands(48241, 0.0548f, 0.3384f, 0.1766f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.0603f, 0.2846f, 0.1638f);
        trimmingValidSegments.addPixelValuesBands(48242, 0.054f, 0.324f, 0.1777f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.0589f, 0.283f, 0.1577f);
        trimmingValidSegments.addPixelValuesBands(48243, 0.0568f, 0.3546f, 0.1815f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.0565f, 0.2855f, 0.1577f);
        trimmingValidSegments.addPixelValuesBands(48244, 0.0553f, 0.338f, 0.1782f);
        trimmingValidSegments.addPixelValuesBands(25, 0.0559f, 0.3853f, 0.1657f);
        trimmingValidSegments.addPixelValuesBands(48245, 0.0569f, 0.3027f, 0.1783f);
        trimmingValidSegments.addPixelValuesBands(25, 0.0577f, 0.3667f, 0.1657f);
        trimmingValidSegments.addPixelValuesBands(48245, 0.0587f, 0.2941f, 0.1775f);
        trimmingValidSegments.addPixelValuesBands(32, 0.0554f, 0.3174f, 0.1636f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.055f, 0.2827f, 0.1477f);
        trimmingValidSegments.addPixelValuesBands(48241, 0.0548f, 0.3384f, 0.1766f);
        trimmingValidSegments.addPixelValuesBands(24462, 0.0555f, 0.28f, 0.1477f);
        trimmingValidSegments.addPixelValuesBands(48242, 0.054f, 0.324f, 0.1777f);
        trimmingValidSegments.addPixelValuesBands(32, 0.0553f, 0.3048f, 0.1636f);
        trimmingValidSegments.addPixelValuesBands(48245, 0.0572f, 0.3035f, 0.1775f);
        trimmingValidSegments.addPixelValuesBands(32, 0.0552f, 0.3478f, 0.1867f);

        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService threadPool = Executors.newCachedThreadPool();

        IntSet validRegionIds = trimmingValidSegments.processResult(threadCount, threadPool);

        assertNotNull(validRegionIds);

        assertEquals(23, validRegionIds.size());

        assertTrue(validRegionIds.contains(32));
        assertTrue(validRegionIds.contains(24462));
        assertTrue(validRegionIds.contains(16));
        assertTrue(validRegionIds.contains(25));
        assertTrue(validRegionIds.contains(24459));
        assertTrue(validRegionIds.contains(48242));
        assertTrue(validRegionIds.contains(24460));
        assertTrue(validRegionIds.contains(22));
        assertTrue(validRegionIds.contains(48236));
        assertTrue(validRegionIds.contains(19));
        assertTrue(validRegionIds.contains(48245));
        assertTrue(validRegionIds.contains(24461));
        assertTrue(validRegionIds.contains(48244));
        assertTrue(validRegionIds.contains(48234));
        assertTrue(validRegionIds.contains(48238));
        assertTrue(validRegionIds.contains(48241));
        assertTrue(validRegionIds.contains(21));
        assertTrue(validRegionIds.contains(48239));
        assertTrue(validRegionIds.contains(48237));
        assertTrue(validRegionIds.contains(48243));
        assertTrue(validRegionIds.contains(48240));
        assertTrue(validRegionIds.contains(20));
        assertTrue(validRegionIds.contains(48235));
    }
}
