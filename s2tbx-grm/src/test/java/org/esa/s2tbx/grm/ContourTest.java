package org.esa.s2tbx.grm;

import org.esa.s2tbx.grm.segmentation.Contour;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Jean Coravu.
 */
public class ContourTest {

    public ContourTest() {
    }

    @Test
    public void testContour() {
        Contour contour = new Contour();

        contour.pushTop();
        contour.pushTop();
        contour.pushTop();

        contour.pushRight();
        contour.pushRight();
        contour.pushRight();
        contour.pushRight();

        contour.pushBottom();
        contour.pushBottom();
        contour.pushBottom();
        contour.pushBottom();

        contour.pushLeft();
        contour.pushLeft();
        contour.pushLeft();
        contour.pushLeft();

        contour.pushTop();

        assertEquals(32, contour.size());
        assertEquals(Contour.TOP_MOVE_INDEX, contour.getMove(0));
        assertEquals(Contour.TOP_MOVE_INDEX, contour.getMove(2));
        assertEquals(Contour.RIGHT_MOVE_INDEX,contour.getMove(5));
        assertEquals(Contour.BOTTOM_MOVE_INDEX, contour.getMove(8));
        assertEquals(Contour.BOTTOM_MOVE_INDEX, contour.getMove(9));
        assertEquals(Contour.LEFT_MOVE_INDEX, contour.getMove(12));
        assertEquals(Contour.LEFT_MOVE_INDEX, contour.getMove(14));
        assertEquals(Contour.TOP_MOVE_INDEX, contour.getMove(15));
    }
}
