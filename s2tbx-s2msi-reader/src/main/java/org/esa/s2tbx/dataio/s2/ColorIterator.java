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
}
