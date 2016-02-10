package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class NdpiOpTest extends BaseIndexOpTest<NdpiOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "GREEN", "SWIR" }, 3, 3, new float[] { 550, 1600 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("greenFactor", 1.0f);
            put("swirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                -0.333333f, -0.200000f, -0.142857f,
                -0.111111f, -0.090909f, -0.076923f,
                -0.066666f, -0.058823f, -0.052631f } );
        super.setUp();
    }
}