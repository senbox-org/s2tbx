package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class MsaviOpTest extends BaseIndexOpTest<MsaviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
            put("slope", 0.5f);
        }});
        setTargetValues(new float[] {
                0.428571f, 0.285714f, 0.214953f,
                0.172413f, 0.143968f, 0.123595f,
                0.108280f, 0.096345f, 0.086782f } );
        super.setUp();
    }
}
