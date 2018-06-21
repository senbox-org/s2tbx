package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.util.math.MathUtils;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * @author Tonio Fincke
 * @author Dagmar Müller
 */
class MountainShadowFlagger {

    private static final boolean SHADOW_ADAPTER_SZA = true;

    static void flagMountainShadowArea(Rectangle sourceRectangle, float sourceSunZenith, float[] sourceAltitude,
                                       int[] flagArray, float minAltitude, float maxAltitude, Point2D[] relativePath) {
        final int sourceWidth = sourceRectangle.width;
        final int sourceHeight = sourceRectangle.height;
        //GK: SunZenith is reduced to find more than the core shadow.
        double sunZenithIntermediate;
        if (SHADOW_ADAPTER_SZA) {
            sunZenithIntermediate =
                    (double) sourceSunZenith * (2. * Math.pow(((90. - (double) sourceSunZenith) / 90), 3) + 1.);
        } else {
            sunZenithIntermediate = (double) sourceSunZenith;
        }
        double sunZenithRad = Math.min(89.0, sunZenithIntermediate) * MathUtils.DTOR;
        if (relativePath.length < 2) {
            return;
        }
        final double[] relativeMinMountainHeights = getRelativeMinMountainHeights(relativePath,
                S2IdepixPreCloudShadowOp.spatialResolution,
                sunZenithRad);
        if (maxAltitude - minAltitude < relativeMinMountainHeights[1]) {
            return;
        }
        int xOffset = 0;
        int yOffset = 0;

        int relPathDeltaX1 = (int) relativePath[1].getX();
        int ip = 2;
        while (relPathDeltaX1 == 0 && ip < relativePath.length) {
            relPathDeltaX1 = (int) relativePath[ip].getX();
            ip++;
        }
        int relPathDeltaY1 = (int) relativePath[1].getY();
        ip = 2;
        while (relPathDeltaY1 == 0 && ip < relativePath.length) {
            relPathDeltaY1 = (int) relativePath[ip].getY();
            ip++;
        }
        if (relPathDeltaX1 > 0) {
            if (relPathDeltaY1 > 0) {
                for (int i = xOffset; i < sourceWidth; i++) {
                    for (int j = yOffset; j < sourceHeight; j++) {
                        identifyMountainShadow(i, j, relativePath, relativeMinMountainHeights, sourceHeight, sourceWidth,
                                sourceAltitude, flagArray, maxAltitude);
                    }
                }
            } else {
                for (int i = xOffset; i < sourceWidth; i++) {
                    for (int j = sourceHeight - 1; j > yOffset - 1; j--) {
                        identifyMountainShadow(i, j, relativePath, relativeMinMountainHeights, sourceHeight, sourceWidth,
                                sourceAltitude, flagArray, maxAltitude);
                    }
                }
            }
        } else {
            if (relPathDeltaY1 > 0) {
                for (int i = sourceWidth - 1; i > xOffset - 1; i--) {
                    for (int j = yOffset; j < sourceHeight; j++) {
                        identifyMountainShadow(i, j, relativePath, relativeMinMountainHeights, sourceHeight, sourceWidth,
                                sourceAltitude, flagArray, maxAltitude);
                    }
                }
            } else {
                for (int i = sourceWidth - 1; i > xOffset - 1; i--) {
                    for (int j = sourceHeight - 1; j > yOffset - 1; j--) {
                        identifyMountainShadow(i, j, relativePath, relativeMinMountainHeights, sourceHeight, sourceWidth,
                                sourceAltitude, flagArray, maxAltitude);
                    }
                }
            }
        }
    }

    static double[] getRelativeMinMountainHeights(Point2D[] relativePath, double spatialResolution, double sunZenith) {
        double[] relativeMinMountainHeights = new double[relativePath.length];
        final double sunFactor = Math.tan(sunZenith);
        for (int i = 0; i < relativePath.length; i++) {
            final double x = relativePath[i].getX();
            final double y = relativePath[i].getY();
            double dist = Math.sqrt(Math.pow(spatialResolution * x, 2) + Math.pow(spatialResolution * y, 2));
            relativeMinMountainHeights[i] = dist / sunFactor;
        }
        return relativeMinMountainHeights;
    }

    private static void identifyMountainShadow(int x0, int y0, Point2D[] relativePath,
                                               double[] relativeMinMountainHeights, int height, int width,
                                               float[] sourceAltitude, int[] flagArray, float maxAltitude) {
        int index0 = y0 * width + x0;
        float altitude0 = sourceAltitude[index0];
        if (Float.isNaN(altitude0)) {
            return;
        }

        for (int i = 0; i < relativePath.length; i++) {
            if (altitude0 + relativeMinMountainHeights[i] > maxAltitude) {
                return;
            }
            int x1 = x0 + (int) relativePath[i].getX();
            int y1 = y0 + (int) relativePath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;
            float altitude1 = sourceAltitude[index1];
            if (Float.isNaN(altitude1)) {
                continue;
            }
            if (altitude0 - altitude1 > relativeMinMountainHeights[i]) {
                if (!((flagArray[index1] & PreparationMaskBand.MOUNTAIN_SHADOW_FLAG) ==
                        PreparationMaskBand.MOUNTAIN_SHADOW_FLAG)) {
                    flagArray[index1] += PreparationMaskBand.MOUNTAIN_SHADOW_FLAG;
                }
            }
        }
    }

}
