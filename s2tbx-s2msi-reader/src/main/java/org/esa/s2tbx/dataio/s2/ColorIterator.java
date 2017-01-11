/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jmalik on 18/11/15.
 */
public class ColorIterator {

    static ArrayList<Color> colors;
    static Iterator<Color> colorIterator;

    static {
        colors = new ArrayList<>();
        colors.add(Color.red);
        colors.add(Color.red.darker());
        colors.add(Color.red.darker().darker());
        colors.add(Color.blue);
        colors.add(Color.blue.darker());
        colors.add(Color.blue.darker().darker());
        colors.add(Color.green);
        colors.add(Color.green.darker());
        colors.add(Color.green.darker().darker());
        colors.add(Color.yellow);
        colors.add(Color.yellow.darker());
        colors.add(Color.yellow.darker().darker());
        colors.add(Color.magenta);
        colors.add(Color.magenta.darker());
        colors.add(Color.magenta.darker().darker());
        colors.add(Color.pink);
        colors.add(Color.pink.darker());
        colors.add(Color.pink.darker().darker());
        colorIterator = colors.iterator();
    }

    public static Color next() {
        if (!colorIterator.hasNext()) {
            colorIterator = colors.iterator();
        }
        return colorIterator.next();
    }

    public static void reset() {
        colorIterator = colors.iterator();
    }
}
