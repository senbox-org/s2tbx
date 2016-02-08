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
                0.0f, 3.988636f, 3.587500f,
                3.462719f, 3.403533f, 3.369444f,
                3.347446f, 3.332143f, 3.320913f } );
        super.setUp();
    }
}
