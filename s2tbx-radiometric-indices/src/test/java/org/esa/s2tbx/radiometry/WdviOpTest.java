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

public class WdviOpTest extends BaseIndexOpTest<WdviOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "RED", "NIR" }, 3, 3, new float[] { 650, 850 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("redFactor", 1.0f);
            put("nirFactor", 1.0f);
            put("slopeSoilLine", 0.5f);
        }});
        setTargetValues(new float[] {
                1.5f, 2.0f, 2.5f,
                3.0f, 3.5f, 4.0f,
                4.5f, 5.0f, 5.5f } );
        super.setUp();
    }
}