package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class Ndi45OpTest extends BaseIndexOpTest<Ndi45Op> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED (B4)", "RED (B5)" }, 3, 3, new float[] { 665, 705 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redB4Factor", 1.0f);
            put("redB5Factor", 1.0f);
        }});
        setTargetValues(new float[] {
                0.333333f, 0.200000f, 0.142857f,
                0.111111f, 0.090909f, 0.076923f,
                0.066666f, 0.058823f, 0.052631f } );
        super.setUp();
    }
}