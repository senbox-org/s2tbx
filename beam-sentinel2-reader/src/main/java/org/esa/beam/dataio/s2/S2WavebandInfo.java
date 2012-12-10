package org.esa.beam.dataio.s2;

/**
* @author Norman Fomferra
*/
public class S2WavebandInfo {
    final int bandId;
    final String bandName;
    final double wavelength;
    final double bandwidth;
    final double solarIrradiance;
    final double quantificationValue;
    final SpatialResolution resolution;

    S2WavebandInfo(int bandId, String bandName, SpatialResolution resolution, double wavelength, double bandwidth, double solarIrradiance, int quantificationValue) {
        this.bandId = bandId;
        this.bandName = bandName;
        this.wavelength = wavelength;
        this.bandwidth = bandwidth;
        this.solarIrradiance = solarIrradiance;
        this.resolution = resolution;
        this.quantificationValue = quantificationValue;
    }
}
