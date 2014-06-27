package org.esa.beam.dataio.s2.update;

/**
* @author Norman Fomferra
*/
public class S2WavebandInfo {
    final int bandId;
    final double wavelength;
    final double bandwidth;
    final double solarIrradiance;
    final double quantificationValue;
    final double reflecUnit;

    S2WavebandInfo(int bandId, double wavelength,
                   double bandwidth, double solarIrradiance, int quantificationValue, double reflecUnit) {
        this.bandId = bandId;
        this.wavelength = wavelength;
        this.bandwidth = bandwidth;
        this.solarIrradiance = solarIrradiance;
        this.quantificationValue = quantificationValue;
        this.reflecUnit = reflecUnit;
    }
}
