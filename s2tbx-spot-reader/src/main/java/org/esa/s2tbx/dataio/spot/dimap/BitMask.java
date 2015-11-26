/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.spot.dimap;

import java.awt.*;

/**
 * The enumeration type {@code Flags} is a representation of
 * the flags used by SPOT scene images.
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

    public final String getName() {
        return name;
    }

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
