package org.esa.beam.dataio.s2;

/**
* @author Norman Fomferra
*/
public class S2WavebandInfo {
    final int bandId;
    final String bandName;
    final double centralWavelength;
    final double bandwidth;
    final SpatialResolution resolution;

    S2WavebandInfo(int bandId, String bandName, double centralWavelength, double bandwidth, SpatialResolution resolution) {
        this.bandId = bandId;
        this.bandName = bandName;
        this.centralWavelength = centralWavelength;
        this.bandwidth = bandwidth;
        this.resolution = resolution;
    }
}
