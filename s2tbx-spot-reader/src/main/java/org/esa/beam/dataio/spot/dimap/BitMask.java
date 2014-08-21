package org.esa.beam.dataio.spot.dimap;

import java.awt.*;

/**
 * The enumeration type {@code Flags} is a representation of
 * the flags used by SPOT scene images.
 *
 */
public class BitMask {

    /**
     * Default nodata bitmask.
     */
    public static BitMask NODATA = new BitMask("NODATA", 0, "No data pixel", Color.red);
    /**
     * Saturation flag.
     */
    public static BitMask SATURATED = new BitMask("SATURATED", 255, "Saturated pixel", Color.orange);

    private final String name;
    private final int mask;
    private final Color color;
    private final float transparency;
    private final String description;

    public BitMask(final String name, final int mask, final String description, final Color color) {
        this(name, mask, description, color, 0.5f);
    }

    public BitMask(final String name, final int mask, final String description, final Color color, final float transparency) {
        this.name = name;
        this.mask = mask;
        this.color = color;
        this.transparency = transparency;
        this.description = description;
    }

    public final String getName() { return name; }

    /**
     * Returns the bit mask associated with the flag.
     *
     * @return the bit mask.
     */
    public final int getMask() {
        return mask;
    }

    /**
     * Returns the textual description of the flag.
     *
     * @return the textual description.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Returns the color associated with this flag (useful for colored bit mask layer).
     *
     * @return the color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the transparency associated with this flag (useful for colored bit mask layer).
     *
     * @return the transparency.
     */
    public final float getTransparency() {
        return transparency;
    }

    /**
     * Tests a bit pattern for the status of the flag.
     *
     * @param value the bit pattern.
     *
     * @return true if the flag is set, false otherwise.
     */
    public final boolean isSet(final int value) {
        return (value & mask) != 0;
    }

    @Override
    public final String toString() {
        return name.toLowerCase();
    }

}
