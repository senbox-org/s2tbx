package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class Msavi2OpTest extends BaseIndexOpTest<Msavi2Op> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                0.938447f, 0.798437f, 0.727998f,
                0.684927f, 0.655711f, 0.634540f,
                0.618472f, 0.605852f, 0.595673f } );
        super.setUp();
    }
}
