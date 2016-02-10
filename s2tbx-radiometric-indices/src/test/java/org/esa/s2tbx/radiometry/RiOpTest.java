package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class RiOpTest extends BaseIndexOpTest<RiOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "GREEN", "RED" }, 3, 3, new float[] { 530, 625 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("greenFactor", 1.0f);
            put("redFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                4.000000f, 1.125000f, 0.592592f,
                0.390625f, 0.288000f, 0.226851f,
                0.186588f, 0.158203f, 0.137174f } );
        super.setUp();
    }
}