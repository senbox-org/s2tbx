package org.esa.beam.dataio.s3.olci;/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import java.io.IOException;
import java.util.Properties;

final class SpectralBandProperties {

    private final int spectralBandCount;
    private final float[] wavelengths;
    private final float[] bandwidths;

    SpectralBandProperties() {
        final Properties properties = new Properties();

        try {
            properties.load(SpectralBandProperties.class.getResourceAsStream("spectralBands.properties"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        spectralBandCount = 21;
        wavelengths = new float[spectralBandCount];
        for (int i = 0; i < wavelengths.length; i++) {
            wavelengths[i] = Float.parseFloat(properties.getProperty("wavelengths." + i));
        }
        bandwidths = new float[spectralBandCount];
        for (int i = 0; i < bandwidths.length; i++) {
            bandwidths[i] = Float.parseFloat(properties.getProperty("bandwidths." + i));
        }
    }

    float getWavelength(int i) {
        return wavelengths[i];
    }

    float getBandwidth(int i) {
        return bandwidths[i];
    }

    int getSpectralBandCount() {
        return spectralBandCount;
    }

}
