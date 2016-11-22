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