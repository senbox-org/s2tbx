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

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.util.math.MathUtils;
import org.esa.snap.runtime.Engine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Julien Malik
 */
public class BiophysicalOpTest {

    private OperatorSpi spi;
    private int width;
    private int height;


    @Before
    public void setUp() throws Exception {
        Engine.start(false);
        spi = new BiophysicalOp.Spi();
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(spi);
    }

    @After
    public void tearDown() throws Exception {
        GPF.getDefaultInstance().getOperatorSpiRegistry().removeOperatorSpi(spi);
    }

    @Test
    public void testOpLAI() throws Exception {
        Product sourceProduct = createTestProduct(BiophysicalVariable.LAI, BiophysicalModel.S2B);

        Map<String, Object> parameters = new HashMap<String, Object>() {{
            put("computeLAI", true);
            put("computeCab", false);
            put("computeCw", false);
            put("computeFapar", false);
            put("computeFcover", false);
            put("sensor", "S2B");
        }};

        Product targetProduct = GPF.createProduct("BiophysicalOp", parameters, sourceProduct);
        assertNotNull(targetProduct);
        Band biophysicalVariableBand = targetProduct.getBand("lai");
        assertNotNull(biophysicalVariableBand);

        Band flagBand = targetProduct.getBand("lai_flags");
        assertNotNull(flagBand);

        float[] actualOutput = new float[width*height];
        biophysicalVariableBand.readPixels(0, 0, width, height, actualOutput, ProgressMonitor.NULL);

        int[] actualFlag = new int[width*height];
        flagBand.readPixels(0, 0, width, height, actualFlag, ProgressMonitor.NULL);

        Band expectedBand = sourceProduct.getBand("expected_output");
        float[] expectedOutput = new float[width*height];
        expectedBand.readPixels(0, 0, width, height, expectedOutput, ProgressMonitor.NULL);

        for (int i = 0; i < width*height; i++) {
            float actual = actualOutput[i];
            int flag = actualFlag[i];
            float expected = expectedOutput[i];

            if (flag == 0) {
                assertEquals(expected, actual, 1E-3f);
            }
        }
    }

    public Product createTestProduct(BiophysicalVariable biophysicalVariable, BiophysicalModel biophysicalModel) throws IOException {
        BiophysicalAuxdata auxdata = BiophysicalAuxdata.makeBiophysicalAuxdata(biophysicalVariable, biophysicalModel);
        double [][] testCases = auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.TEST_CASES);

        width = testCases.length;
        height = 1;

        Product product = new Product("name", "type", width, height);

        for (BiophysicalOp.L2BInput input : BiophysicalOp.L2BInput.values()) {
            product.addBand( new Band(input.getBandName(), ProductData.TYPE_FLOAT32, width, height) );
            float [] bandData = new float[width*height];
            for (int i = 0; i < testCases.length; ++i) {
                switch (input) {
                    case B3:
                    case B4:
                    case B5:
                    case B6:
                    case B7:
                    case B8A:
                    case B11:
                    case B12:
                        bandData[i] = (float) testCases[i][input.ordinal()];
                        break;
                    case VIEW_ZENITH:
                        bandData[i] = (float) Math.acos(testCases[i][8]) * MathUtils.RTOD_F;
                        break;
                    case SUN_ZENITH:
                        bandData[i] = (float) Math.acos(testCases[i][9]) * MathUtils.RTOD_F;
                        break;
                    case SUN_AZIMUTH:
                        bandData[i] = (float) Math.acos(testCases[i][10]) * MathUtils.RTOD_F;
                        break;
                    case VIEW_AZIMUTH:
                        bandData[i] = 0.0f;
                        break;
                    default:
                        break;
                }
                product.getBand(input.getBandName()).setData(ProductData.createInstance(bandData));
            }
        }

        Band expected_output = new Band("expected_output", ProductData.TYPE_FLOAT32, width, height);
        product.addBand(expected_output);
        float [] expectedOutput = new float[width*height];
        for (int i = 0; i < testCases.length; ++i) {
            expectedOutput[i] = (float)testCases[i][11];
        }
        expected_output.setData(ProductData.createInstance(expectedOutput));

        return product;
    }
}
