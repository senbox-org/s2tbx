package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/9/2016.
 */

public class GndviOpTest extends BaseIndexOpTest<GndviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "GREEN", "NIR" }, 3, 3, new float[] { 550, 780 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("greenFactor", 1.0f);
            put("nirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                0.333333f, 0.200000f, 0.142857f,
                0.111111f, 0.090909f, 0.076923f,
                0.066666f, 0.058823f, 0.052631f } );
        super.setUp();
    }
}
