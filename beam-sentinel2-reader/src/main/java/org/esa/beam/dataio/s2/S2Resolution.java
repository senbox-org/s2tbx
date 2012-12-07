package org.esa.beam.dataio.s2;

/**
* @author Norman Fomferra
*/
public enum S2Resolution {
    R10M(0, 10.0), R20M(1, 20.0), R60M(2, 60.0);

    public final int id;
    public final double res;

    S2Resolution(int id, double res) {
        this.id = id;
        this.res = res;
    }
}
