package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.util.math.MathUtils;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Tonio Fincke
 */
class MountainShadowFlagger {

    private static final boolean SHADOW_ADAPTER_SZA = true;

    static void flagMountainShadowArea(int productWidth, int productHeight,
                                       Rectangle sourceRectangle, Rectangle targetRectangle,
                                       float sourceSunZenith, float sourceSunAzimuth,
                                       float[] sourceLatitude, float[] sourceLongitude, float[] sourceAltitude,
                                       int[] flagArray) {
        final int sourceWidth = sourceRectangle.width;
        final int sourceHeight = sourceRectangle.height;

        double sunZenithIntermediate;
        if (SHADOW_ADAPTER_SZA) {
            sunZenithIntermediate = (double) sourceSunZenith * (2. * Math.pow(((90. - (double) sourceSunZenith) / 90), 3) + 1.);
        } else {
            sunZenithIntermediate = (double) sourceSunZenith;
        }
        double sunZenithRad = Math.min(89.0, sunZenithIntermediate) * MathUtils.DTOR;

        double sunAzimuthRad = sourceSunAzimuth * MathUtils.DTOR;

        final List<Float> altitudes = Arrays.asList(ArrayUtils.toObject(sourceAltitude));
        float maxAltitude = Math.max(0, Collections.max(altitudes));
        float minAltitude = Math.max(0, Collections.min(altitudes));

        final Point2D[] relativePath = CloudShadowUtils.getRelativePath(minAltitude, sunZenithRad, sunAzimuthRad,
                                                                        maxAltitude, sourceRectangle, targetRectangle,
                                                                        productHeight, productWidth,
                                                                        S2IdepixCloudShadowOp.spatialResolution, false,
                                                                        true);
        int xOffset = targetRectangle.x - sourceRectangle.x;
        int yOffset = targetRectangle.y - sourceRectangle.y;
        int width = targetRectangle.width;
        int height = targetRectangle.height;
        int xLimit = xOffset + width;
        int yLimit = yOffset + height;
        int max = width + height - 1;
        int i = 0;
        if (sourceSunAzimuth < 90) {
            while (i < max) {
                int x = xOffset + Math.min(i, width - 1);
                int y = yLimit + Math.min(-1, height - 2 - i);
                while (x >= xOffset && y >= yOffset) {
                    identifyMountainShadow(x, y, relativePath, sourceHeight, sourceWidth, sunZenithRad, sourceAltitude,
                                           sourceLongitude, sourceLatitude, flagArray);
                    x--;
                    y--;
                }
                i++;
            }
        } else if (sourceSunAzimuth < 180) {
            while (i < max) {
                int x = xOffset + Math.min(i, width - 1);
                int y = yOffset + Math.max(0, i - height + 1);
                while (x >= xOffset && y < yLimit) {
                    identifyMountainShadow(x, y, relativePath, sourceHeight, sourceWidth, sunZenithRad, sourceAltitude,
                                           sourceLongitude, sourceLatitude, flagArray);
                    x--;
                    y++;
                }
                i++;
            }
        } else if (sourceSunAzimuth < 270) {
            while (i < max) {
                int x = xOffset + Math.max(0, width - 1 - i);
                int y = yOffset + Math.max(0, i - height + 1);
                while (x < xLimit && y < yLimit) {
                    identifyMountainShadow(x, y, relativePath, sourceHeight, sourceWidth, sunZenithRad, sourceAltitude,
                                           sourceLongitude, sourceLatitude, flagArray);
                    x++;
                    y++;
                }
                i++;
            }
        } else {
            while (i < max) {
                int x = xOffset + Math.max(0, width - 1 - i);
                int y = yLimit + Math.min(-1, height - 2 - i);
                while (x < xLimit && y >= yOffset) {
                    identifyMountainShadow(x, y, relativePath, sourceHeight, sourceWidth, sunZenithRad, sourceAltitude,
                                           sourceLongitude, sourceLatitude, flagArray);
                    x++;
                    y--;
                }
                i++;
            }
        }
    }

    private static void identifyMountainShadow(int x0, int y0, Point2D[] relativePath, int height, int width,
                                               double sunZenith, float[] sourceAltitude, float[] sourceLongitude,
                                               float[] sourceLatitude, int[] flagArray) {
        int index0 = y0 * width + x0;
        if (((flagArray[index0] & PreparationMaskBand.MOUNTAIN_SHADOW_FLAG) == PreparationMaskBand.MOUNTAIN_SHADOW_FLAG)) {
            return;
        }
        for (int i = 1; i < relativePath.length; i++) {
            int x1 = x0 + (int) relativePath[i].getX();
            int y1 = y0 + (int) relativePath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            //todo compute mountain extent correctly (however that is)
            double[] mountainExtent = MountainVerticalExtent.
                    getMountainVerticalExtent(width, sourceAltitude, y1, x1);
            double mountainBase = mountainExtent[0];
            double mountainTop = mountainExtent[1];
            int index1 = y1 * width + x1;
            double[] distAltArray = CloudShadowUtils.computeDistance(index0, index1, sourceLongitude, sourceLatitude,
                                                                     sourceAltitude);
            double minAltitude = distAltArray[1];
            double mountainSearchPointHeight = distAltArray[0] * Math.tan(((Math.PI / 2. - sunZenith)));
            if (sourceAltitude[index0] < 0 || Double.isNaN(sourceAltitude[index0])) {
                mountainSearchPointHeight -= minAltitude;
            } else {
                mountainSearchPointHeight = mountainSearchPointHeight + (sourceAltitude[index0] - minAltitude);
            }
            if (mountainBase <= mountainSearchPointHeight && mountainSearchPointHeight <= (mountainTop - minAltitude)) {
                flagArray[index0] += PreparationMaskBand.MOUNTAIN_SHADOW_FLAG;
                for (int j = 1; j < i; j++) {
                    int x2 = x0 + (int) relativePath[j].getX();
                    int y2 = y0 + (int) relativePath[j].getY();
                    final Point2D.Double relativePos = new Point2D.Double(x1 - x2, y1 - y2);
                    if (org.esa.snap.core.util.ArrayUtils.isMemberOf(relativePos, relativePath)) {
                        int index2 = y2 * width + x2;
                        flagArray[index2] += PreparationMaskBand.MOUNTAIN_SHADOW_FLAG;
                    }
                }
                return;
            }
        }
    }

}
