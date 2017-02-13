package org.esa.s2tbx.s2msi.wv;

import org.esa.snap.core.util.math.FracIndex;
import org.esa.snap.core.util.math.LookupTable;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * @author Tonio Fincke
 */
public class WaterVapourLUTAccessorTest {

    @Test
    public void readLut() throws Exception {

        final LookupTable waterVapourLookupTable = WaterVapourLUTAccessor.readLut();

        assertEquals(6, waterVapourLookupTable.getDimensionCount());

        final double[] surfaceReflectanceValues = waterVapourLookupTable.getDimension(0).getSequence();
        assertArrayEquals(new double[]{0.05, 0.1, 0.2, 0.3, 0.4, 0.6, 0.8}, surfaceReflectanceValues, 1e-7);

        final double[] sunZenithValues = waterVapourLookupTable.getDimension(1).getSequence();
        assertArrayEquals(new double[]{0.0, 15.0, 30.0, 45.0, 60.0}, sunZenithValues, 1e-8);

        final double[] viewZenithValues = waterVapourLookupTable.getDimension(2).getSequence();
        assertArrayEquals(new double[]{0.0, 5.0, 10.0, 15.0, 20.0}, viewZenithValues, 1e-8);

        final double[] azimuthValues = waterVapourLookupTable.getDimension(3).getSequence();
        assertArrayEquals(new double[]{0.0, 45.0, 90.0, 125.0, 180.0}, azimuthValues, 1e-8);

        final double[] altitudeValues = waterVapourLookupTable.getDimension(4).getSequence();
        assertArrayEquals(new double[]{0.0, 0.5, 1.0, 1.5, 2.0, 3.0, 4.0}, altitudeValues, 1e-8);

        final double[] bValues = waterVapourLookupTable.getDimension(5).getSequence();
        assertArrayEquals(new double[]{0.0, 1.0}, bValues, 1e-8);

        assertEquals(waterVapourLookupTable.getValue(0.05, 0.0, 0.0, 0.0, 0.0, 0.0), -2.74805004884, 1e-8);
        assertEquals(waterVapourLookupTable.getValue(0.8, 60.0, 20.0, 180.0, 4.0, 1.0), -0.0675674520453, 1e-8);

        final double[] readValues = getInterpolatedValues(0.2, 60.0, 5.0, 90.0, 1.0, waterVapourLookupTable);
        assertArrayEquals(new double[]{-1.9659631802, -0.0193600447985}, readValues, 1e-8);

        final double[] interpolatedValues = getInterpolatedValues(0.125, 32.34, 17.5, 123.0, 3.5, waterVapourLookupTable);
        assertArrayEquals(new double[]{-2.748810179439154, -0.10919799238301532}, interpolatedValues, 1e-8);
    }

    private double[] getInterpolatedValues(double surfRef, double sza, double vza, double relAzi, double altitude,
                                           LookupTable waterVapourLookupTable) {
        double[] coordinates = new double[]{surfRef, sza, vza, relAzi, altitude};
        final FracIndex[] fracIndexes = FracIndex.createArray(5);
        for (int i = 0; i < 5; i++) {
            LookupTable.computeFracIndex(waterVapourLookupTable.getDimension(i), coordinates[i], fracIndexes[i]);
        }
        return waterVapourLookupTable.getValues(fracIndexes);
    }

}