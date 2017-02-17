package org.esa.s2tbx.s2msi.aerosol;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Tonio Fincke
 */
public class S2AerosolOpTest {

    @Test
    public void getWvlIndexes() throws Exception {
        String[] reflectanceBandNames = new String[]{"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B8A", "B9",
                "B10", "B11", "B12"};
        int[] expectedBandIndexes = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        assertArrayEquals(expectedBandIndexes, S2AerosolOp.getBandIndexes(reflectanceBandNames));

        reflectanceBandNames = new String[]{"B1", "B3", "B4", "B6", "B7", "B8", "B9", "B10", "B11", "B12"};
        expectedBandIndexes = new int[]{0, 2, 3, 5, 6, 7, 9, 10, 11, 12};
        assertArrayEquals(expectedBandIndexes, S2AerosolOp.getBandIndexes(reflectanceBandNames));
    }

}