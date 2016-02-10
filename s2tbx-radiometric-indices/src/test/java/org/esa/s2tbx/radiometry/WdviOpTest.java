package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class WdviOpTest extends BaseIndexOpTest<WdviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
            put("slopeSoilLine", 0.5f);
        }});
        setTargetValues(new float[] {
                1.5f, 2.0f, 2.5f,
                3.0f, 3.5f, 4.0f,
                4.5f, 5.0f, 5.5f } );
        super.setUp();
    }
}