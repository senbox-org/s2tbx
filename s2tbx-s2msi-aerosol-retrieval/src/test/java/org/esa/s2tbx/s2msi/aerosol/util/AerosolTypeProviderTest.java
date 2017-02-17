package org.esa.s2tbx.s2msi.aerosol.util;

import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author Tonio Fincke
 */
public class AerosolTypeProviderTest {

    @Test
    public void testGetClimatologiesProduct() throws IOException {
        final Product climatologiesProduct = AerosolTypeProvider.getClimatologiesProduct();
        assertNotNull(climatologiesProduct);
    }

    @Test
    public void testGetTimeData() {
        float[] times = new float[]{14.f, 45.f, 74.f, 105.f, 135.f, 166.f, 196.f, 227.f, 258.f, 288.f, 319.f, 349.f};

        final AerosolTypeProvider.TimeData timeData_20 = AerosolTypeProvider.getTimeData(20, times);
        assertEquals("1", timeData_20.startMonth);
        assertEquals("2", timeData_20.endMonth);
        assertEquals((6. / 31.), timeData_20.fraction, 1e-8);

        final AerosolTypeProvider.TimeData timeData_203 = AerosolTypeProvider.getTimeData(203, times);
        assertEquals("7", timeData_203.startMonth);
        assertEquals("8", timeData_203.endMonth);
        assertEquals((7. / 31.), timeData_203.fraction, 1e-8);

        final AerosolTypeProvider.TimeData timeData_105 = AerosolTypeProvider.getTimeData(105, times);
        assertEquals("4", timeData_105.startMonth);
        assertEquals("5", timeData_105.endMonth);
        assertEquals(0.0, timeData_105.fraction, 1e-8);

        final AerosolTypeProvider.TimeData timeData_5 = AerosolTypeProvider.getTimeData(5, times);
        assertEquals("12", timeData_5.startMonth);
        assertEquals("1", timeData_5.endMonth);
        assertEquals((21. / 30.), timeData_5.fraction, 1e-7);

        final AerosolTypeProvider.TimeData timeData_360 = AerosolTypeProvider.getTimeData(360, times);
        assertEquals("12", timeData_360.startMonth);
        assertEquals("1", timeData_360.endMonth);
        assertEquals((11. / 30.), timeData_360.fraction, 1e-8);
    }

    @Test
    public void testPerformSpatialInterpolation() {
        double[] values = new double[]{1.0, 2.0, 3.0, 4.0};
        assertEquals(1.0, AerosolTypeProvider.performSpatialInterpolation(new PixelPos(0.5, 0.5), values), 1e-8);
        assertEquals(1.3, AerosolTypeProvider.performSpatialInterpolation(new PixelPos(0.6, 0.6), values), 1e-8);
        assertEquals(2.1, AerosolTypeProvider.performSpatialInterpolation(new PixelPos(1.4, 0.6), values), 1e-8);
        assertEquals(2.9, AerosolTypeProvider.performSpatialInterpolation(new PixelPos(0.6, 1.4), values), 1e-8);
        assertEquals(3.7, AerosolTypeProvider.performSpatialInterpolation(new PixelPos(1.4, 1.4), values), 1e-8);
    }

}