package org.esa.s2tbx.biophysical;

import java.awt.*;

/**
 * Created by jmalik on 23/06/16.
 */
public enum BiophysicalFlag {

    INPUT_OUT_OF_RANGE("INPUT_OUT_OF_RANGE", 0, "Input is out of definition domain", Color.RED, BiophysicalFlag.DEFAULT_TRANSPARENCY),
    OUTPUT_THRESHOLDED_TO_MIN_OUTPUT("OUTPUT_THRESHOLDED_TO_MIN_OUTPUT", 1, "Output is lesser than minimum output, but within tolerance", Color.GREEN, BiophysicalFlag.DEFAULT_TRANSPARENCY),
    OUTPUT_THRESHOLDED_TO_MAX_OUTPUT("OUTPUT_THRESHOLDED_TO_MAX_OUTPUT", 2, "Output is greater than maximum output, but within tolerance", Color.BLUE, BiophysicalFlag.DEFAULT_TRANSPARENCY),
    OUTPUT_TOO_LOW("OUTPUT_TOO_LOW", 3, "Output is too low", Color.YELLOW, BiophysicalFlag.DEFAULT_TRANSPARENCY),
    OUTPUT_TOO_HIGH("OUTPUT_TOO_HIGH", 4, "Output is too high", Color.ORANGE, BiophysicalFlag.DEFAULT_TRANSPARENCY);

    private final String name;
    private final int bitIndex;
    private final String description;
    private final Color color;
    private final double transparency;

    private final static double DEFAULT_TRANSPARENCY = 0.7;

    BiophysicalFlag(String name, int bitIndex, String description, Color color, double transparency) {
        this.name = name;
        this.bitIndex = bitIndex;
        this.description = description;
        this.color = color;
        this.transparency = transparency;
    }

    public String getName() {
        return name;
    }

    public int getBitIndex() {
        return bitIndex;
    }

    public int getFlagValue() {
        return (int)Math.pow(2, bitIndex);
    }

    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }

    public double getTransparency() {
        return transparency;
    }
}
