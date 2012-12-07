package org.esa.beam.dataio.s2;

/**
* @author Norman Fomferra
*/
public class S2WavebandInfo {
    final int bandId;
    final String bandName;
    final double centralWavelength;
    final double bandWidth;
    final S2Resolution resolution;

    S2WavebandInfo(int bandId, String bandName, double centralWavelength, double bandWidth, S2Resolution resolution) {
        this.bandId = bandId;
        this.bandName = bandName;
        this.centralWavelength = centralWavelength;
        this.bandWidth = bandWidth;
        this.resolution = resolution;
    }
}
