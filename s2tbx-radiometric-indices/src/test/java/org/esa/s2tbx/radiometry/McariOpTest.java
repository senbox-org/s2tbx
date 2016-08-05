package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class McariOpTest extends BaseIndexOpTest<McariOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "GREEN (B3)", "RED (B4)", "RED (B5)" }, 3, 3, new float[] { 560, 665, 705 }, new float[] { 1, 2, 3 }, new float[] { 9, 10, 11 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("greenFactor", 1.0f);
            put("red1Factor", 1.0f);
            put("red2Factor", 1.0f);
        }});
        setTargetValues(new float[] {
                0.6f, 0.6f, 0.6f,
                0.6f, 0.6f, 0.6f,
                0.6f, 0.6f, 0.6f } );
        super.setUp();
    }
}