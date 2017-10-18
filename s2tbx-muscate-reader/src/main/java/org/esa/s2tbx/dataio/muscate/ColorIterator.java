package org.esa.s2tbx.dataio.muscate;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by obarrile on 20/02/2017.
 */
public class ColorIterator {

    static ArrayList<Color> colors;
    static Iterator<Color> colorIterator;

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
        colorIterator = colors.iterator();
    }

    public static Color next() {
        if (!colorIterator.hasNext()) {
            reset();
        }
        return colorIterator.next();
    }

    public static void reset() {
        colorIterator = colors.iterator();
    }
}

