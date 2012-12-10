package org.esa.beam.dataio.s2;

/**
* @author Norman Fomferra
*/
public class S2WavebandInfo {
    final int bandId;
    final String bandName;
    final double wavelength;
    final double bandwidth;
    final double solarIrradiances;
    final double scalingFactor;
    final SpatialResolution resolution;

    S2WavebandInfo(int bandId, String bandName, SpatialResolution resolution, double wavelength, double bandwidth, double solarIrradiances, double scalingFactor) {
        this.bandId = bandId;
        this.bandName = bandName;
        this.wavelength = wavelength;
        this.bandwidth = bandwidth;
        this.solarIrradiances = solarIrradiances;
        this.resolution = resolution;
        this.scalingFactor = scalingFactor;
    }
}
