package org.esa.s2tbx.dataio.s2;

import java.util.concurrent.TimeUnit;

/**
 * A simple time probe for measuring elapsed time between two points in the code
 *
 * @author Julien Malik
 */
public class TimeProbe {
    long starts;

    public static TimeProbe start() {
        return new TimeProbe();
    }

    private TimeProbe() {
        reset();
    }

    public TimeProbe reset() {
        starts = System.nanoTime();
        return this;
    }

    public long elapsed(TimeUnit unit) {
        long ends = System.nanoTime();
        return unit.convert(ends - starts,  TimeUnit.NANOSECONDS);
    }
}
