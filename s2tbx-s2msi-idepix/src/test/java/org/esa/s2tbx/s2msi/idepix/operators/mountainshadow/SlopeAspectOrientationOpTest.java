package org.esa.s2tbx.s2msi.idepix.operators.mountainshadow;

import org.junit.Test;

import java.awt.Rectangle;

import static junit.framework.Assert.assertEquals;

/**
 * @author Tonio Fincke
 */
public class SlopeAspectOrientationOpTest {

    @Test
    public void getSourceRectangle_extendEverywhere() throws Exception {
        final Rectangle targetRectangle = new Rectangle(1, 1, 4, 4);
        final Rectangle sourceRectangle = SlopeAspectOrientationOp.getSourceRectangle(targetRectangle, 6, 6);
        assertEquals(0, sourceRectangle.x);
        assertEquals(0, sourceRectangle.y);
        assertEquals(6, sourceRectangle.width);
        assertEquals(6, sourceRectangle.height);
    }

    @Test
    public void getSourceRectangle_extendNowhere() throws Exception {
        final Rectangle targetRectangle = new Rectangle(0, 0, 4, 4);
        final Rectangle sourceRectangle = SlopeAspectOrientationOp.getSourceRectangle(targetRectangle, 4, 4);
        assertEquals(0, sourceRectangle.x);
        assertEquals(0, sourceRectangle.y);
        assertEquals(4, sourceRectangle.width);
        assertEquals(4, sourceRectangle.height);
    }

    @Test
    public void getSourceRectangle_extendAtRandom() throws Exception {
        final Rectangle targetRectangle = new Rectangle(3, 0, 3, 5);
        final Rectangle sourceRectangle = SlopeAspectOrientationOp.getSourceRectangle(targetRectangle, 6, 5);
        assertEquals(2, sourceRectangle.x);
        assertEquals(0, sourceRectangle.y);
        assertEquals(4, sourceRectangle.width);
        assertEquals(5, sourceRectangle.height);
    }

}