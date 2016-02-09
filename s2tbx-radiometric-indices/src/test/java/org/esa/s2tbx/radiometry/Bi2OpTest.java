package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/9/2016.
 */


public class Bi2OpTest extends BaseIndexOpTest<Bi2Op> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "GREEN", "RED", "NIR" }, 3, 3, new float[] { 530, 650, 850 }, new float[] { 1, 2, 3 }, new float[] { 9, 10, 11 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("greenFactor", 1.0f);
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                2.160246f, 3.109126f, 4.082482f,
                5.066228f, 6.055300f, 7.047458f,
                8.041558f, 9.036961f, 10.033277f } );
        super.setUp();
    }
}
