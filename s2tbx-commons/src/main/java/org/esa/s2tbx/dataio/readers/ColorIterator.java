package org.esa.s2tbx.dataio.readers;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class used to define a list of colors for the masks to be coloured
 */

public class ColorIterator {
    final static ArrayList<Color> colors;

    static {
        colors = new ArrayList<Color>();
        colors.add(Color.red);
        colors.add(Color.red.darker());
        colors.add(Color.blue);
        colors.add(Color.blue.darker());
        colors.add(Color.green);
        colors.add(Color.green.darker());
        colors.add(Color.yellow);
        colors.add(Color.yellow.darker());
        colors.add(Color.magenta);
        colors.add(Color.magenta.darker());
        colors.add(Color.pink);
        colors.add(Color.pink.darker());
        colors.add(Color.cyan);
        colors.add(Color.cyan.darker());
        colors.add(Color.orange);
        colors.add(Color.orange.darker());
        colors.add(Color.blue.darker().darker());
        colors.add(Color.green.darker().darker());
        colors.add(Color.yellow.darker().darker());
        colors.add(Color.magenta.darker().darker());
        colors.add(Color.pink.darker().darker());
    }

    public static Iterator<Color> create() {
        return colors.iterator();
    }
}
