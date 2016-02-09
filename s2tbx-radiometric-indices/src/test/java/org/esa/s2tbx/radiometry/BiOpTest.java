package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/9/2016.
 */

public class BiOpTest extends BaseIndexOpTest<BiOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "GREEN", "RED" }, 3, 3, new float[] { 530, 650 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("greenFactor", 1.0f);
            put("redFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                1.581138f, 2.549509f, 3.535533f,
                4.527692f, 5.522680f, 6.519202f,
                7.516648f, 8.514693f, 9.513148f } );
        super.setUp();
    }
}