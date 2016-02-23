package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/9/2016.
 */



public class ArviOpTest extends BaseIndexOpTest<ArviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "BLUE", "RED", "NIR" }, 3, 3, new float[] { 475, 650, 850 }, new float[] { 1, 2, 4 }, new float[] { 9, 10, 12 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("blueFactor", 1.0f);
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
            put("gammaParameter", 1.0f);
        }});
        setTargetValues(new float[] {
                0.142857f, 0.111111f, 0.090909f,
                0.076923f, 0.066666f, 0.058823f,
                0.052631f, 0.047619f, 0.043478f } );
        super.setUp();
    }
}
