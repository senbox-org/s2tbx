package org.esa.beam.dataio.s2;

/**
* @author Norman Fomferra
*/
public class S2L1bWavebandInfo {
    final int bandId;
    final String bandName;
    final S2L1bSpatialResolution resolution;
    final double wavelength;
    final double bandwidth;

    S2L1bWavebandInfo(int bandId, String bandName, S2L1bSpatialResolution resolution, double wavelength,
                      double bandwidth) {
        this.bandId = bandId;
        this.bandName = bandName;
        this.wavelength = wavelength;
        this.bandwidth = bandwidth;
        this.resolution = resolution;
    }
}
