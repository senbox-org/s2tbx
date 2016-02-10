package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class TndviOpTest extends BaseIndexOpTest<TndviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                0.912870f, 0.836660f, 0.801783f,
                0.781735f, 0.768706f, 0.759554f,
                0.752772f, 0.747545f, 0.743391f } );
        super.setUp();
    }
}