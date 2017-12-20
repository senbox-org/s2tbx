package org.esa.s2tbx.mapper;

import com.bc.ceres.core.Assert;
import org.esa.snap.core.gpf.annotations.Parameter;

/**
 * Created by rdumitrascu on 12/18/2017.
 */
public class Spectrum {
    @Parameter(pattern = "[a-zA-Z_0-9]*")
    private String name;
    @Parameter
    private int xPixelPosition;
    @Parameter
    private int yPixelPosition;

    public Spectrum(String name, int xPixelPosition , int yPixelPosition) {
        assert name != null;

        this.name = name;
        this.xPixelPosition = xPixelPosition;
        this.yPixelPosition = yPixelPosition;
    }

    public String getName() {
        return name;
    }

    public int getXPixelPosition() {
        return xPixelPosition;
    }

    public int getYPixelPosition() {
        return yPixelPosition;
    }

    @Override
    public String toString() {
        return getName();
    }
}
