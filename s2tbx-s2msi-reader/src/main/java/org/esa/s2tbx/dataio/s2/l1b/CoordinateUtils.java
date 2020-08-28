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

package org.esa.s2tbx.dataio.s2.l1b;

import org.locationtech.jts.geom.Coordinate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.esa.snap.core.util.Guardian;

import java.util.ArrayList;
import java.util.List;

/**
 * @author opicas-p
 */
public class CoordinateUtils {

    /**
     * todo move this to a utility class
     *
     * @param in
     * @param size
     * @return
     */
    public static List<double[]> arraySplitter(List<Double> in, int size) {
        Guardian.assertTrue("Multiple size", (in.size() % size) == 0);

        List<double[]> result = new ArrayList<double[]>();
        for (int i = 0; i < in.size() / size; i++) {
            double[] item = new double[]{in.get(i * size), in.get(i * size + 1), in.get(i * size + 2)};
            result.add(item);
        }

        return result;
    }

    public static List<Coordinate> as2DCoordinates(List<Double> in) {
        List<double[]> tr = arraySplitter(in, 2);

        List<Coordinate> result = new ArrayList<Coordinate>();
        for (int i = 0; i < tr.size(); i++) {
            Coordinate c = new Coordinate(tr.get(i)[0], tr.get(i)[1]);
            result.add(c);
        }

        return result;
    }

    public static List<Coordinate> as3DCoordinates(List<Double> in) {
        List<double[]> tr = arraySplitter(in, 3);

        List<Coordinate> result = new ArrayList<Coordinate>();
        for (int i = 0; i < tr.size(); i++) {
            Coordinate c = new Coordinate(tr.get(i)[0], tr.get(i)[1], tr.get(i)[2]);
            result.add(c);
        }

        return result;
    }

    // fixme Add unit test
    public static double distanceToSegment(Vector3D v, Vector3D w, Vector3D p) {
        // Return minimum distance between line segment vw and point p
        final double l2 = Vector3D.distanceSq(v, w);  // i.e. |w-v|^2 -  avoid a sqrt
        if (l2 == 0.0) return Vector3D.distance(p, v);   // v == w case
        // Consider the line extending the segment, parameterized as v + t (w - v).
        // We find projection of point p onto the line.
        // It falls where t = [(p-v) . (w-v)] / |w-v|^2
        double t = Vector3D.dotProduct(p.subtract(v), w.subtract(v)) / l2;
        if (t < 0.0) return Vector3D.distance(p, v);       // Beyond the 'v' end of the segment
        else if (t > 1.0) return Vector3D.distance(p, w);  // Beyond the 'w' end of the segment
        Vector3D projection = v.add(w.subtract(v).scalarMultiply(t));  // Projection falls on the segment
        return Vector3D.distance(p, projection);
    }

    public static double[] getOrdinate(List<Coordinate> coordinates, int index) {
        double[] result = new double[coordinates.size()];

        int i = 0;
        for (Coordinate c : coordinates) {
            result[i] = c.getOrdinate(index);
            i = i + 1;
        }

        return result;
    }

    public static double[] convertFloatsToDoubles(float[] input) {
        if (input == null) {
            return null;
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    public static float[] convertDoublesToFloats(double[] input) {
        if (input == null) {
            return null;
        }
        float[] output = new float[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i];
        }
        return output;
    }

    public static double[] getLatitudes(List<Coordinate> coordinates) {
        return getOrdinate(coordinates, 0);
    }

    public static double[] getLongitudes(List<Coordinate> coordinates) {
        return getOrdinate(coordinates, 1);
    }
}
