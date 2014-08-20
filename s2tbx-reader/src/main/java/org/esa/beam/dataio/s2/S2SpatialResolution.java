package org.esa.beam.dataio.s2;

/**
 * @author Norman Fomferra
 */
public enum S2SpatialResolution {
    R10M(0, 10), R20M(1, 20), R60M(2, 60);

    public final int id;
    public final int resolution;

    S2SpatialResolution(int id, int resolution) {
        this.id = id;
        this.resolution = resolution;
    }

    public static S2SpatialResolution valueOfId(int id) {
        for (S2SpatialResolution value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("id=" + id);
    }

    public static S2SpatialResolution valueOfResolution(int resolution) {
        for (S2SpatialResolution value : values()) {
            if (value.resolution == resolution) {
                return value;
            }
        }
        throw new IllegalArgumentException("resolution=" + resolution);
    }
}
