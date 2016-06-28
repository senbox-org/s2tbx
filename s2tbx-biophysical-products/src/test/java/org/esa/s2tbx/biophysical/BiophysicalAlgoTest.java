/*
 * Copyright (C) 2011 Brockmann Consult GmbH (info@brockmann-consult.de)
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.biophysical;

import org.esa.snap.runtime.Engine;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Norman
 */
public class BiophysicalAlgoTest {

    @Before
    public void setup() {
        // Ensure activator has executed
        Engine.start(false);
    }

    @Test
    public void testLAI() throws IOException {
        testIndicator(BiophysicalIndicator.LAI);
    }

    @Test
    public void testCW() throws IOException {
        // No test case for Cw
        //testIndicator(BiophysicalIndicator.LAI_Cw);
    }

    @Test
    public void testCAB() throws IOException {
        testIndicator(BiophysicalIndicator.LAI_Cab);
    }

    @Test
    public void testFAPAR() throws IOException {
        testIndicator(BiophysicalIndicator.FAPAR);
    }

    @Test
    public void testFCOVER() throws IOException {
        // No test case for FCOVER
        //testIndicator(BiophysicalIndicator.FCOVER);
    }

    private void testIndicator(BiophysicalIndicator indicator) throws IOException {
        BiophysicalAuxdata indicatorData = BiophysicalAuxdata.makeBiophysicalAuxdata(indicator);
        BiophysicalAlgo indicatorAlgo = new BiophysicalAlgo(indicatorData);

        double [][] testData = indicatorData.getCoeffs(BiophysicalAuxdata.BiophysicalIndicatorCoeffs.TEST_CASES);
        for (int testCase = 0; testCase < testData.length; ++testCase) {
            double[] testCaseData = testData[testCase];

            double[] input = new double[testCaseData.length -1];
            for (int i = 0; i < testCaseData.length -1; ++i) {
                input[i] = testCaseData[i];
            }

            BiophysicalAlgo.Result result = indicatorAlgo.process(input);

            double computedIndicator = result.getOutputValue();
            double expectedIndicator = testCaseData[testCaseData.length -1];


            if (result.isInputOutOfRange()) {
                //System.err.println("Input out of range");
            }
            else if (result.isOutputTooLow()) {
                //System.err.println("Output too low");
            }
            else if (result.isOutputThresholdedToMinOutput()) {
                //System.err.println("Output Thresholded to Min Output");
            }
            else if (result.isOutputThresholdedToMaxOutput()) {
                //System.err.println("Output Thresholded to Max Output");
            }
            else if (result.isOutputTooHigh()) {
                //System.err.println("Output too high");
            }
            else {
                try {
                    if (!Double.isNaN(expectedIndicator))
                    {
                        assertEquals(expectedIndicator, computedIndicator, 1E-2);
                    }
                } catch (AssertionError e) {
                    System.err.println("Error detected during validation. Input was : " + Arrays.toString(input));
                    throw e;
                }
            }

        }
    }

}
