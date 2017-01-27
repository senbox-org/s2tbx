package org.esa.s2tbx.s2msi.aerosol.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Tonio Fincke
 */
public class PixelGeometryTest {

    @Test
    public void testGetRelativeAzi() throws Exception {
        assertEquals(0.0, PixelGeometry.getRelativeAzi(0, 0), 1e-8);
        assertEquals(180.0, PixelGeometry.getRelativeAzi(180, 0), 1e-8);
        assertEquals(180.0, PixelGeometry.getRelativeAzi(0, 180), 1e-8);
        assertEquals(0.0, PixelGeometry.getRelativeAzi(180, 180), 1e-8);
        assertEquals(1.0, PixelGeometry.getRelativeAzi(180, 181), 1e-8);
        assertEquals(179.0, PixelGeometry.getRelativeAzi(0, 181), 1e-8);
        assertEquals(1.0, PixelGeometry.getRelativeAzi(179, 180), 1e-8);
        assertEquals(179.0, PixelGeometry.getRelativeAzi(179, 0), 1e-8);
        assertEquals(0.0, PixelGeometry.getRelativeAzi(63, 63), 1e-8);
        assertEquals(27.0, PixelGeometry.getRelativeAzi(63, 90), 1e-8);
        assertEquals(27.0, PixelGeometry.getRelativeAzi(90, 63), 1e-8);
        assertEquals(90.0, PixelGeometry.getRelativeAzi(90, 0), 1e-8);
        assertEquals(90.0, PixelGeometry.getRelativeAzi(90, 180), 1e-8);
        assertEquals(90.0, PixelGeometry.getRelativeAzi(180, 90), 1e-8);
        assertEquals(90.0, PixelGeometry.getRelativeAzi(0, 90), 1e-8);
    }

}