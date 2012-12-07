package org.esa.beam.dataio.s3.synergy;
/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import org.esa.beam.dataio.s3.LonLatFunction;
import org.esa.beam.framework.datamodel.RationalFunctionModel;
import org.esa.beam.util.math.ArcDistanceCalculator;
import org.esa.beam.util.math.DistanceCalculator;
import org.esa.beam.util.math.MathUtils;
import org.esa.beam.util.math.Rotator;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

final class LonLatTiePointFunction implements LonLatFunction {

    private static final int LAT = 0;
    private static final int LON = 1;
    private static final int F = 2;

    private final RationalFunctionApproximation[] approximations;

    public LonLatTiePointFunction(double[] lonData, double[] latData, double[] functionData, int colCount,
                                  double accuracy) {
        approximations = createApproximations(lonData, latData, functionData, colCount, accuracy);
    }

    @Override
    public double getValue(Point2D p) {
        if (approximations != null) {
            final double lon = p.getX();
            final double lat = p.getY();
            if (lon >= -180.0 && lon <= 180.0 && lat >= -90.0 && lat <= 90.0) {
                final RationalFunctionApproximation a = findBestApproximation(lat, lon);
                if (a != null) {
                    a.getRotator().transform(p);
                    return a.getModel().getValue(p.getY(), p.getX());
                }
            }
        }
        return Double.NaN;
    }

    RationalFunctionApproximation findBestApproximation(double lat, double lon) {
        RationalFunctionApproximation bestApproximation = null;
        if (approximations.length == 1) {
            bestApproximation = approximations[0];
        } else {
            double minDistance = Double.MAX_VALUE;
            for (final RationalFunctionApproximation a : approximations) {
                final double distance = a.getDistance(lat, lon);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestApproximation = a;
                }
            }
        }
        return bestApproximation;
    }

    static RationalFunctionApproximation[] createApproximations(double[] lonData, double[] latData,
                                                                double[] functionData, int colCount,
                                                                double accuracy) {
        final int rowCount = functionData.length / colCount;
        final int tileCountX = 2;
        final int tileCountY = (2 * rowCount) / colCount;

        final Rectangle[] rectangles = MathUtils.subdivideRectangle(colCount, rowCount, tileCountX, tileCountY, 1);
        final RationalFunctionApproximation[] approximations = new RationalFunctionApproximation[rectangles.length];

        for (int i = 0; i < rectangles.length; i++) {
            final double[][] data = extractWarpPoints(lonData, latData, functionData, rectangles[i]);
            final RationalFunctionApproximation approximation = createApproximation(data, accuracy);
            if (approximation == null) {
                return null;
            }
            approximations[i] = approximation;
        }

        return approximations;
    }

    static RationalFunctionApproximation createApproximation(double[][] data, double accuracy) {
        final Point2D centerPoint = Rotator.calculateCenter(data, LON, LAT);
        final double centerLon = centerPoint.getX();
        final double centerLat = centerPoint.getY();

        final Rotator rotator = new Rotator(centerLon, centerLat);
        rotator.transform(data, LON, LAT);

        final int[] indices = new int[]{LAT, LON, F};

        final RationalFunctionModel model = findBestModel(data, indices, accuracy);
        if (model == null) {
            return null;
        }

        return new RationalFunctionApproximation(model, rotator,
                                                 new ArcDistanceCalculator(centerLon, centerLat));
    }

    static double[][] extractWarpPoints(double[] lonData, double[] latData, double[] functionData, Rectangle r) {
        final int minX = r.x;
        final int minY = r.y;
        final int pointCountX = r.width;
        final int pointCountY = r.height;
        final List<double[]> pointList = new ArrayList<double[]>(pointCountX * pointCountY);

        for (int j = 0, k = 0; j < pointCountY; j++) {
            final int y = minY + j;
            final int offsetY = y * pointCountX;
            for (int i = 0; i < pointCountX; i++, k++) {
                final int x = minX + i;
                final double lat = latData[x + offsetY];
                final double lon = lonData[x + offsetY];
                final double f = functionData[x + offsetY];
                if (lon >= -180.0 && lon <= 180.0 && lat >= -90.0 && lat <= 90.0 && !Double.isNaN(f)) {
                    final double[] point = new double[3];
                    point[LAT] = lat;
                    point[LON] = lon;
                    point[F] = f;
                    pointList.add(point);
                }
            }
        }

        return pointList.toArray(new double[pointList.size()][3]);
    }

    static RationalFunctionModel findBestModel(double[][] data, int[] indexes, double accuracy) {
        RationalFunctionModel bestModel = null;
        for (int degreeP = 0; degreeP <= 4; degreeP++) {
            for (int degreeQ = 0; degreeQ <= degreeP; degreeQ++) {
                final int termCountP = RationalFunctionModel.getTermCountP(degreeP);
                final int termCountQ = RationalFunctionModel.getTermCountQ(degreeQ);
                if (data.length >= termCountP + termCountQ) {
                    final RationalFunctionModel model = createModel(degreeP, degreeQ, data, indexes);
                    if (bestModel == null || model.getRmse() < bestModel.getRmse()) {
                        bestModel = model;
                    }
                    if (bestModel.getRmse() < accuracy) {
                        break;
                    }
                }
            }
        }
        return bestModel;
    }

    static RationalFunctionModel createModel(int degreeP, int degreeQ, double[][] data, int[] indexes) {
        final int ix = indexes[0];
        final int iy = indexes[1];
        final int iz = indexes[2];
        final double[] x = new double[data.length];
        final double[] y = new double[data.length];
        final double[] g = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            x[i] = data[i][ix];
            y[i] = data[i][iy];
            g[i] = data[i][iz];
        }

        return new RationalFunctionModel(degreeP, degreeQ, x, y, g);
    }

    static final class RationalFunctionApproximation {

        private final RationalFunctionModel model;
        private final Rotator rotator;
        private final DistanceCalculator calculator;

        public RationalFunctionApproximation(RationalFunctionModel model,
                                             Rotator rotator,
                                             DistanceCalculator calculator) {
            this.model = model;
            this.rotator = rotator;
            this.calculator = calculator;
        }

        public RationalFunctionModel getModel() {
            return model;
        }

        public double getDistance(double lat, double lon) {
            return calculator.distance(lon, lat);
        }

        public Rotator getRotator() {
            return rotator;
        }
    }
}
