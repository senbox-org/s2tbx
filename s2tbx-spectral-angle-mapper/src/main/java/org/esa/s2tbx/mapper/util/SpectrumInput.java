package org.esa.s2tbx.mapper.util;

import org.esa.snap.core.gpf.annotations.Parameter;

/**
 *
 * @author Razvan Dumitrascu
 */
public class SpectrumInput {
    @Parameter(pattern = "[a-zA-Z_0-9]*")
    private String name;
    @Parameter
    private int[] xPixelPolygonPositions;
    @Parameter
    private int[] yPixelPolygonPositions;

    public SpectrumInput(String name, int[] xPixelPolygonPositions, int[] yPixelPolygonPositions) {
        assert name != null;

        this.name = name;
        this.xPixelPolygonPositions = xPixelPolygonPositions;
        this.yPixelPolygonPositions = yPixelPolygonPositions;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public int[] getXPixelPolygonPositions() {
        return xPixelPolygonPositions;
    }

    public int[] getYPixelPolygonPositions() {
        return yPixelPolygonPositions;
    }
}
