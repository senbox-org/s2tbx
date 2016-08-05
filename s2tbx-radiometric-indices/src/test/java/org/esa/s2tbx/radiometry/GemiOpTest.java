package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by kraftek on 2/8/2016.
 */
public class GemiOpTest extends BaseIndexOpTest<GemiOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                0.0f, 2.707644f, 2.249722f,
                2.091528f, 2.010337f, 1.960665f,
                1.927050f, 1.902755f, 1.884358f } );
        super.setUp();
    }
}
