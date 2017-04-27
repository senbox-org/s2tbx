/*
 *
 *  * Copyright (C) 2016 CS ROMANIA
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.radiometry;

import java.util.HashMap;

/**
 * Created by dmihailescu on 2/10/2016.
 */

public class McariOpTest extends BaseIndexOpTest<McariOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "GREEN (B3)", "RED (B4)", "RED (B5)" }, 3, 3, new float[] { 560, 665, 705 }, new float[] { 1, 2, 3 }, new float[] { 9, 10, 11 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("greenFactor", 1.0f);
            put("red1Factor", 1.0f);
            put("red2Factor", 1.0f);
        }});
        setTargetValues(new float[] {
                0.9f, 0.8f, 0.75f,
                0.72f, 0.7f, 0.685714f,
                0.675f, 0.666666f, 0.66f } );
        super.setUp();
    }
}