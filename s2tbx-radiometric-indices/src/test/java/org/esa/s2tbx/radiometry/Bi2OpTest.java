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
