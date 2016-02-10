package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class SaviOpTest extends BaseIndexOpTest<SaviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
            put("soilCorrectionFactor", 0.5f);
        }});
        setTargetValues(new float[] {
                0.428571f, 0.272727f, 0.200000f,
                0.157894f, 0.130434f, 0.111111f,
                0.096774f, 0.085714f, 0.076923f } );
        super.setUp();
    }
}
