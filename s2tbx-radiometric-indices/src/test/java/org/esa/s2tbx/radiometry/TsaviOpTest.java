package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class TsaviOpTest extends BaseIndexOpTest<TsaviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
            put("slope", 0.5f);
            put("intercept", 0.5f);
            put("adjustment", 0.08f);
        }});
        setTargetValues(new float[] {
                0.303030f, 0.238095f, 0.215053f,
                0.203252f, 0.196078f, 0.191256f,
                0.187793f, 0.185185f, 0.183150f } );
        super.setUp();
    }
}