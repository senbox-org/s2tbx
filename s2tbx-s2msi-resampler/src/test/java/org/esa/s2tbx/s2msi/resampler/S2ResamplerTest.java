package org.esa.s2tbx.s2msi.resampler;

import org.junit.Assert;
import org.junit.Test;


/**
 * Created by obarrile on 15/05/2017.
 */
public class S2ResamplerTest {
    
    @Test
    public void testExtendData() {

        // 1.2 2.3 5.1
        // NaN 2.4 5.3
        // NaN NaN 5.4

        float[] input = new float[] {1.2f, 2.3f, 5.1f, Float.NaN, 2.4f, 5.3f, Float.NaN, Float.NaN, 5.4f};
        float[] output = S2ResamplerUtils.extendDataV2(input, 3, 3);
        float[] expectedOutput = new float[] {3.6f, 2.9f, 2.2f, 4.9f, 7.6f,
                                            0.1f, 1.2f, 2.3f, 5.1f, 7.9f,
                                            -3.4f, -0.5f, 2.4f, 5.3f, 8.2f,
                                            -6.9f, -2.2f, 2.5f, 5.4f, 8.5f,
                                            -10.4f, -0.3f, 2.6f, 5.5f, 8.8f};
        Assert.assertArrayEquals(output, expectedOutput, 1E-4f);
    }

    @Test
    public void testExtendData2() {

        // 1.2 NaN NaN
        // NaN NaN NaN
        // NaN NaN NaN

        float[] input = new float[] {1.2f, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN};
        float[] output = S2ResamplerUtils.extendDataV2(input, 3, 3);
        float[] expectedOutput = new float[] {1.2f, 1.2f, 1.2f, 1.2f, 0.0f,
                1.2f, 1.2f, 1.2f, 1.2f, 0.0f,
                1.2f, 1.2f, 1.2f, 1.2f, 0.0f,
                1.2f, 1.2f, 1.2f, 1.2f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        Assert.assertArrayEquals(output, expectedOutput, 1E-4f);
    }

}
