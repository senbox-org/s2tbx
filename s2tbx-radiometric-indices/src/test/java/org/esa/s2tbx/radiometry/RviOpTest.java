package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class RviOpTest extends BaseIndexOpTest<RviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 620, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                2.000000f, 1.500000f, 1.333333f,
                1.250000f, 1.200000f, 1.166666f,
                1.142857f, 1.125000f, 1.111111f } );
        super.setUp();
    }
}