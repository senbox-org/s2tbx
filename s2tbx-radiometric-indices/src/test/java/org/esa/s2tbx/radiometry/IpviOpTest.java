package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/9/2016.
 */

public class IpviOpTest extends BaseIndexOpTest<IpviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                0.666666f, 0.600000f, 0.571428f,
                0.555555f, 0.545454f, 0.538461f,
                0.533333f, 0.529411f, 0.526315f } );
        super.setUp();
    }
}