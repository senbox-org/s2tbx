package org.esa.s2tbx.fcc;

import org.esa.s2tbx.fcc.mahalanobis.MultiplyByConstantMatrix;
import org.esa.s2tbx.fcc.mahalanobis.MultiplyMatrix;
import org.esa.s2tbx.fcc.mahalanobis.StorageMatrix;
import org.esa.s2tbx.fcc.mahalanobis.SubMatrix;
import org.esa.s2tbx.fcc.mahalanobis.TransposeMatrix;
import org.esa.s2tbx.fcc.trimming.MovingWindowTileParallelComputing;
import org.esa.snap.utils.matrix.IntMatrix;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Jean Coravu.
 */
public class MovingWindowTest {

    public MovingWindowTest() {
    }

    @Test
    public void testMovingWindowTileParallelComputing() throws Exception {
        IntMatrix colorFillerMatrix = new IntMatrix(100, 100);
        int windowWidth = 30;
        int windowHeight = 30;
        int movingStepWidth = 30;
        int movingStepHeight = 30;
        int tileWidth = 20;
        int tileHeight = 20;

        System.out.println("testMovingWindowTileParallelComputing tileWidth="+tileWidth+", tileHeight="+tileHeight+", movingStepWidth="+movingStepWidth+ ", movingStepHeight="+movingStepHeight);
        System.out.println("");

//        MovingWindowTileParallelComputing tiles = new MovingWindowTileParallelComputing(colorFillerMatrix, windowWidth, windowHeight, movingStepWidth, movingStepHeight, tileWidth, tileHeight, null, null);
//        tiles.executeInParallel(0, null);
    }
}
