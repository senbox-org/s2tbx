package org.esa.s2tbx.s2msi.idepix.operators.mountainshadow;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Tonio Fincke
 */
public class SlopeAspectOrientationOpTest {

    @Test
    public void computeSlopeAndAspect() {
        float[] altitude = new float[]{
                10.0f, 10.0f, 15.0f, 17.5f, 12.5f, 12.5f,
                10.0f, 10.0f, 15.0f, 17.5f, 12.5f, 12.5f,
                12.0f, 12.0f, 14.0f, 16.0f, 13.0f, 13.0f,
                13.0f, 13.0f, 11.0f, 13.0f, 14.0f, 14.0f,
                14.0f, 14.0f, 12.0f, 14.0f, 11.0f, 11.0f,
                14.0f, 14.0f, 12.0f, 14.0f, 11.0f, 11.0f};
        final float[] slopeAndAspect_7 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 7, 10, 6);
        final float[] slopeAndAspect_8 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 8, 10, 6);
        final float[] slopeAndAspect_9 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 9, 10, 6);
        final float[] slopeAndAspect_10 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 10, 10, 6);
        final float[] slopeAndAspect_13 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 13, 10, 6);
        final float[] slopeAndAspect_14 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 14, 10, 6);
        final float[] slopeAndAspect_15 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 15, 10, 6);
        final float[] slopeAndAspect_16 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 16, 10, 6);
        final float[] slopeAndAspect_19 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 19, 10, 6);
        final float[] slopeAndAspect_20 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 20, 10, 6);
        final float[] slopeAndAspect_21 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 21, 10, 6);
        final float[] slopeAndAspect_22 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 22, 10, 6);
        final float[] slopeAndAspect_25 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 25, 10, 6);
        final float[] slopeAndAspect_26 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 26, 10, 6);
        final float[] slopeAndAspect_27 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 27, 10, 6);
        final float[] slopeAndAspect_28 = SlopeAspectOrientationOp.computeSlopeAndAspect(altitude, 28, 10, 6);

        float[] expectedSlope = new float[]{
                0.21798114f, 0.32035214f, 0.11440312f, 0.22131443f,
                0.10711748f, 0.22345093f, 0.14396477f, 0.124354996f,
                0.070593186f, 0.070593186f, 0.11134102f, 0.11134102f,
                0.11134102f, 0.049958397f, 0.0f, 0.14048971f};
        float[] expectedAspect = {
                -1.28474486f, -1.62733972f, 1.96140337f, 1.57079637f,
                -0.95054686f, -2.12064958f, 3.01189017f, 1.57079637f,
                0.78539819f, -2.3561945f, -2.67794514f, 2.67794514f,
                1.10714877f, -0.f, -3.14159274f, 2.3561945f};

        assertEquals(slopeAndAspect_7[0], expectedSlope[0], 1e-7);
        assertEquals(slopeAndAspect_8[0], expectedSlope[1], 1e-7);
        assertEquals(slopeAndAspect_9[0], expectedSlope[2], 1e-7);
        assertEquals(slopeAndAspect_10[0], expectedSlope[3], 1e-7);
        assertEquals(slopeAndAspect_13[0], expectedSlope[4], 1e-7);
        assertEquals(slopeAndAspect_14[0], expectedSlope[5], 1e-7);
        assertEquals(slopeAndAspect_15[0], expectedSlope[6], 1e-7);
        assertEquals(slopeAndAspect_16[0], expectedSlope[7], 1e-7);
        assertEquals(slopeAndAspect_19[0], expectedSlope[8], 1e-7);
        assertEquals(slopeAndAspect_20[0], expectedSlope[9], 1e-7);
        assertEquals(slopeAndAspect_21[0], expectedSlope[10], 1e-7);
        assertEquals(slopeAndAspect_22[0], expectedSlope[11], 1e-7);
        assertEquals(slopeAndAspect_25[0], expectedSlope[12], 1e-7);
        assertEquals(slopeAndAspect_26[0], expectedSlope[13], 1e-7);
        assertEquals(slopeAndAspect_27[0], expectedSlope[14], 1e-7);
        assertEquals(slopeAndAspect_28[0], expectedSlope[15], 1e-7);

        assertEquals(slopeAndAspect_7[1], expectedAspect[0], 1e-8);
        assertEquals(slopeAndAspect_8[1], expectedAspect[1], 1e-8);
        assertEquals(slopeAndAspect_9[1], expectedAspect[2], 1e-8);
        assertEquals(slopeAndAspect_10[1], expectedAspect[3], 1e-8);
        assertEquals(slopeAndAspect_13[1], expectedAspect[4], 1e-8);
        assertEquals(slopeAndAspect_14[1], expectedAspect[5], 1e-8);
        assertEquals(slopeAndAspect_15[1], expectedAspect[6], 1e-8);
        assertEquals(slopeAndAspect_16[1], expectedAspect[7], 1e-8);
        assertEquals(slopeAndAspect_19[1], expectedAspect[8], 1e-8);
        assertEquals(slopeAndAspect_20[1], expectedAspect[9], 1e-8);
        assertEquals(slopeAndAspect_21[1], expectedAspect[10], 1e-8);
        assertEquals(slopeAndAspect_22[1], expectedAspect[11], 1e-8);
        assertEquals(slopeAndAspect_25[1], expectedAspect[12], 1e-8);
        assertEquals(slopeAndAspect_26[1], expectedAspect[13], 1e-8);
        assertEquals(slopeAndAspect_27[1], expectedAspect[14], 1e-8);
        assertEquals(slopeAndAspect_28[1], expectedAspect[15], 1e-8);
    }

    @Test
    public void testComputeOrientation() {
        float[] latitudes = new float[]{50.0f, 50.01f, 50.02f, 50.03f,
                50.1f, 50.11f, 50.12f, 50.13f,
                50.2f, 50.21f, 50.22f, 50.23f,
                50.3f, 50.31f, 50.32f, 50.33f};
        float[] longitudes = new float[]{10.0f, 10.2f, 10.4f, 10.6f,
                10.01f, 10.21f, 10.41f, 10.61f,
                10.02f, 10.22f, 10.42f, 10.62f,
                10.03f, 10.23f, 10.43f, 10.63f};
        final float orientation_1 = SlopeAspectOrientationOp.computeOrientation(latitudes, longitudes, 1);
        final float orientation_2 = SlopeAspectOrientationOp.computeOrientation(latitudes, longitudes, 2);
        final float orientation_5 = SlopeAspectOrientationOp.computeOrientation(latitudes, longitudes, 5);
        final float orientation_6 = SlopeAspectOrientationOp.computeOrientation(latitudes, longitudes, 6);
        final float orientation_9 = SlopeAspectOrientationOp.computeOrientation(latitudes, longitudes, 9);
        final float orientation_10 = SlopeAspectOrientationOp.computeOrientation(latitudes, longitudes, 10);
        final float orientation_13 = SlopeAspectOrientationOp.computeOrientation(latitudes, longitudes, 13);
        final float orientation_14 = SlopeAspectOrientationOp.computeOrientation(latitudes, longitudes, 14);
        assertEquals(-0.07763171, orientation_1, 1e-8);
        assertEquals(-0.07764761, orientation_2, 1e-8);
        assertEquals(-0.07779299, orientation_5, 1e-8);
        assertEquals(-0.07780917, orientation_6, 1e-8);
        assertEquals(-0.07795518, orientation_9, 1e-8);
        assertEquals(-0.07797144, orientation_10, 1e-8);
        assertEquals(-0.07811809, orientation_13, 1e-8);
        assertEquals(-0.07813445, orientation_14, 1e-8);
    }

}