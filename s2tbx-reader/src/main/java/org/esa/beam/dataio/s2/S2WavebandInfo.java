package org.esa.beam.dataio.s2;

/**
* @author Norman Fomferra
*/
public class S2WavebandInfo {
    final int bandId;
    final String bandName;
    final S2SpatialResolution resolution;
    final double wavelength;
    final double bandwidth;

    S2WavebandInfo(int bandId, String bandName, S2SpatialResolution resolution, double wavelength,
                   double bandwidth) {
        this.bandId = bandId;
        this.bandName = bandName;
        this.wavelength = wavelength;
        this.bandwidth = bandwidth;
        this.resolution = resolution;
    }
}
