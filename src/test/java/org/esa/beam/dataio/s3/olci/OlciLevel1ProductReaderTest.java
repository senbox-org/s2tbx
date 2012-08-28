/*
 * Copyright (c) 2012. Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 */

package org.esa.beam.dataio.s3.olci;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OlciLevel1ProductReaderTest {

    @Test
    public void testPatchFileName() {
        assertEquals("radiancesOa03.nc", OlciLevel1ProductReader.patchFileName("radianceOa3.nc"));
        assertEquals("radiancesOa13.nc", OlciLevel1ProductReader.patchFileName("radianceOa13.nc"));
        assertEquals("qualityFlags.nc", OlciLevel1ProductReader.patchFileName("qualityFlags.nc"));
    }

    @Test
    public void testSpectralBandsProperties() {
        final float[] wavelengths = new float[21];
        final float[] bandwidths = new float[21];
        OlciLevel1ProductReader.getSpectralBandsProperties(wavelengths, bandwidths);

        assertEquals(400.0, wavelengths[0], 0.0);
        assertEquals(412.5, wavelengths[1], 0.0);
        assertEquals(1020.0, wavelengths[20], 0.0);

        assertEquals(400.0, wavelengths[0], 0.0);
        assertEquals(412.5, wavelengths[1], 0.0);
        assertEquals(1020.0, wavelengths[20], 0.0);
    }
}
