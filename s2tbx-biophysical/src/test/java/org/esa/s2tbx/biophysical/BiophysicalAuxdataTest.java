/*
 * Copyright (C) 2011 Brockmann Consult GmbH (info@brockmann-consult.de)
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.biophysical;

import org.esa.snap.runtime.Engine;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Norman
 */
public class BiophysicalAuxdataTest {

    @Before
    public void setup() {
        // Ensure activator has executed
        Engine.start(false);
    }

    @Test
    public void readAuxdata() throws Exception {
        BiophysicalAuxdata laiData = BiophysicalAuxdata.makeBiophysicalAuxdata(BiophysicalVariable.LAI,BiophysicalModel.S2A);
        double [][] normalisation = laiData.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.NORMALISATION);
        assertNotNull(normalisation);
        assertEquals(11, normalisation.length);
        assertEquals(2, normalisation[0].length);
        assertEquals(0.239015274639, normalisation[0][1], 1E-8);
    }
}