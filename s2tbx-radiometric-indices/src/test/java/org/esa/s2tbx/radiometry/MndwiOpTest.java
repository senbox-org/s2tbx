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

public class MndwiOpTest extends BaseIndexOpTest<MndwiOp> {

    @Override
    public void setUp() throws Exception {
        setupBands(new String[] { "GREEN", "MIR" }, 3, 3, new float[] { 550, 5000 }, new float[] { 1, 2 }, new float[] { 9, 10 });
        setOperatorParameters(new HashMap<String, Float>() {{
            put("greenFactor", 1.0f);
            put("mirFactor", 1.0f);
        }});
        setTargetValues(new float[] {
                -0.333333f, -0.200000f, -0.142857f,
                -0.111111f, -0.090909f, -0.076923f,
                -0.066666f, -0.058823f, -0.052631f } );
        super.setUp();
    }
}