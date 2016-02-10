package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class PviOpTest extends BaseIndexOpTest<PviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
            put("angleSoilLineNIRAxis", 45.0f);
        }});

        setTargetValues(new float[] {
                0.707106f, 0.707106f, 0.707106f,
                0.707106f, 0.707106f, 0.707106f,
                0.707106f, 0.707106f, 0.707106f } );
        super.setUp();
    }
}