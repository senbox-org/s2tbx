package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class MtciOpTest extends BaseIndexOpTest<MtciOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED (B4)", "RED (B5)", "NIR (B6)" }, 3, 3, new float[] { 665, 705, 740 }, new float[] { 1, 2, 3 }, new float[] { 9, 10, 11 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redB4Factor", 1.0f);
            put("redB5Factor", 1.0f);
            put("nirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f } );
        super.setUp();
    }
}