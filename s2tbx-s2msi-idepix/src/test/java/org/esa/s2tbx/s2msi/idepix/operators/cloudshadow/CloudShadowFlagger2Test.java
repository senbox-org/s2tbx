package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertArrayEquals;

/**
 * @author Tonio Fincke
 */
public class CloudShadowFlagger2Test {

    private ArrayList<Integer> startValues;
    private ArrayList<Integer> endValues;

    @Before
    public void setUp() {
        startValues = new ArrayList<>();
        endValues = new ArrayList<>();
    }

    @Test
    public void testDetectValleys_noValleys() {
        final int[][] valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);
        assertNotNull(valleys);
        assertEquals(0, valleys.length);
    }

    @Test
    public void testDetectValleys_onlyValleyStarts() {
        startValues.add(20);
        int[][] valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);
        assertNotNull(valleys);
        assertEquals(1, valleys.length);
        assertArrayEquals(new int[]{20, 100}, valleys[0]);

        startValues.add(40);
        valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);
        assertNotNull(valleys);
        assertEquals(1, valleys.length);
        assertArrayEquals(new int[]{20, 100}, valleys[0]);
    }

    @Test
    public void testDetectValleys_onlyValleyEnds() {
        endValues.add(20);
        int[][] valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);
        assertNotNull(valleys);
        assertEquals(1, valleys.length);
        assertArrayEquals(new int[]{0, 20}, valleys[0]);

        endValues.add(40);
        valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);
        assertNotNull(valleys);
        assertEquals(1, valleys.length);
        assertArrayEquals(new int[]{0, 40}, valleys[0]);
    }

    @Test
    public void testDetectValleys_OneValley() {
        startValues.add(20);
        endValues.add(40);

        int[][] valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);

        assertNotNull(valleys);
        assertEquals(1, valleys.length);
        assertArrayEquals(new int[]{20, 40}, valleys[0]);

        startValues.add(30);
        endValues.add(50);

        valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);

        assertNotNull(valleys);
        assertEquals(1, valleys.length);
        assertArrayEquals(new int[]{20, 50}, valleys[0]);
    }

    @Test
    public void testDetectValleys_TwoValleys() {
        startValues.add(20);
        startValues.add(60);
        endValues.add(40);
        endValues.add(80);

        int[][] valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);

        assertNotNull(valleys);
        assertEquals(2, valleys.length);
        assertArrayEquals(new int[]{20, 40}, valleys[0]);
        assertArrayEquals(new int[]{60, 80}, valleys[1]);
    }

    @Test
    public void testDetectValleys_TwoValleys_MoreComplex() {
        startValues.add(20);
        startValues.add(21);
        startValues.add(60);
        startValues.add(61);
        endValues.add(39);
        endValues.add(40);
        endValues.add(79);
        endValues.add(80);

        int[][] valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);

        assertNotNull(valleys);
        assertEquals(2, valleys.length);
        assertArrayEquals(new int[]{20, 40}, valleys[0]);
        assertArrayEquals(new int[]{60, 80}, valleys[1]);
    }

    @Test
    public void testDetectValleys_ComplexValleys() {
        startValues.add(20);
        startValues.add(21);
        startValues.add(22);
        startValues.add(60);
        startValues.add(61);
        startValues.add(95);
        endValues.add(5);
        endValues.add(40);
        endValues.add(41);
        endValues.add(79);
        endValues.add(80);

        int[][] valleys = CloudShadowFlagger2.detectValleys(startValues, endValues, 100);

        assertNotNull(valleys);
        assertEquals(4, valleys.length);
        assertArrayEquals(new int[]{0, 5}, valleys[0]);
        assertArrayEquals(new int[]{20, 41}, valleys[1]);
        assertArrayEquals(new int[]{60, 80}, valleys[2]);
        assertArrayEquals(new int[]{95, 100}, valleys[3]);
    }

}