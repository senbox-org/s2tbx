package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.util.math.MathUtils;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tonio Fincke
 */
class PotentialCloudShadowAreaIdentifier {

    private static final double MAXCLOUD_TOP = S2IdepixCloudShadowOp.maxcloudTop;

    static Collection<List<Integer>> identifyPotentialCloudShadows(int productHeight, int productWidth, Rectangle sourceRectangle,
                                                                   Rectangle targetRectangle, float[] sourceSunZenith,
                                                                   float[] sourceSunAzimuth, float[] sourceLatitude,
                                                                   float[] sourceLongitude, float[] sourceAltitude,
                                                                   int[] flagArray, int[] cloudIDArray) {
        int x0SourceCenter = sourceRectangle.width / 2;
        int y0SourceCenter = sourceRectangle.height / 2;
        int sourceCenterIndex = y0SourceCenter * sourceRectangle.width + x0SourceCenter;

        final float sunAzimuth = sourceSunAzimuth[sourceCenterIndex];
        double sunAzimuthRad = sunAzimuth * MathUtils.DTOR;
        float sunZenithDegree = sourceSunZenith[sourceCenterIndex];
        double sunZenithCloudRad = sunZenithDegree * MathUtils.DTOR;
        final List<Float> altitudes = Arrays.asList(ArrayUtils.toObject(sourceAltitude));
        float minAltitude = Collections.min(altitudes);
        Point2D[] cloudPath = CloudShadowUtils.getRelativePath(minAltitude, sunZenithCloudRad, sunAzimuthRad,
                                                               MAXCLOUD_TOP, sourceRectangle, targetRectangle,
                                                               productHeight, productWidth,
                                                               S2IdepixCloudShadowOp.spatialResolution, true, false);
        return identifyPotentialCloudShadows(sourceRectangle, targetRectangle, sunZenithDegree, sunAzimuth,
                                             sourceLatitude, sourceLongitude, sourceAltitude, flagArray, cloudIDArray,
                                             cloudPath);
    }

    static Collection<List<Integer>> identifyPotentialCloudShadows(Rectangle sourceRectangle,
                                                                   Rectangle targetRectangle, float sourceSunZenith,
                                                                   float sourceSunAzimuth, float[] sourceLatitude,
                                                                   float[] sourceLongitude, float[] sourceAltitude,
                                                                   int[] flagArray, int[] cloudIDArray,
                                                                   Point2D[] cloudPath) {
        double sunZenithCloudRad = (double) sourceSunZenith * MathUtils.DTOR;
        final Map<Integer, List<Integer>> indexToPositions = new HashMap<>();
        int i = 0;
        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        if (sourceSunAzimuth < 90) {
            //start at upper right
            int xOffset = targetRectangle.x - sourceRectangle.x;
            int xLimit = sourceRectangle.x + sourceWidth - targetRectangle.x;
            int yLimit = targetRectangle.y - sourceRectangle.y + targetRectangle.height;
            int max = xLimit + yLimit - 1;
            while (i < max) {
                int x = Math.max(xOffset, sourceWidth - 1 - i);
                int y = Math.max(0, i - yLimit + 1);
                while (x < sourceWidth && y < yLimit) {
                    identifyPotentialCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude,
                                                 sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                                                 cloudIDArray, indexToPositions);
                    x++;
                    y++;
                }
                i++;
            }
        } else if (sourceSunAzimuth < 180) {
            //start at lower right
            int xOffset = targetRectangle.x - sourceRectangle.x;
            int yOffset = targetRectangle.y - sourceRectangle.y;
            int xLimit = sourceRectangle.x + sourceWidth - targetRectangle.x;
            int yLimit = sourceRectangle.y + sourceHeight - targetRectangle.y;
            int max = xLimit + yLimit - 1;
            while (i < max) {
                int x = Math.max(xOffset, sourceWidth - 1 - i);
                int y = sourceHeight + Math.min(-1, yLimit - 2 - i);
                while (x < sourceWidth && y >= yOffset) {
                    identifyPotentialCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude,
                                                 sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                                                 cloudIDArray, indexToPositions);
                    x++;
                    y--;
                }
                i++;
            }
        } else if (sourceSunAzimuth < 270) {
            //start at lower left
            int yOffset = targetRectangle.y - sourceRectangle.y;
            int xLimit = targetRectangle.x - sourceRectangle.x + targetRectangle.width;
            int yLimit = sourceRectangle.y + sourceHeight - targetRectangle.y;
            int max = xLimit + yLimit - 1;
            while (i < max) {
                int x = Math.min(i, xLimit - 1);
                int y = sourceHeight + Math.min(-1, yLimit - 2 - i);
                while (x >= 0 && y >= yOffset) {
                    identifyPotentialCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude,
                                                 sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                                                 cloudIDArray, indexToPositions);
                    x--;
                    y--;
                }
                i++;
            }
        } else {
            //start at upper left
            int xLimit = targetRectangle.x - sourceRectangle.x + targetRectangle.width;
            int yLimit = targetRectangle.y - sourceRectangle.y + targetRectangle.height;
            int max = xLimit + yLimit - 1;
            while (i < max) {
                int x = Math.min(i, xLimit - 1);
                int y = Math.max(0, i - yLimit + 1);
                while (x >= 0 && y < yLimit) {
                    identifyPotentialCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude,
                                                 sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                                                 cloudIDArray, indexToPositions);
                    x--;
                    y++;
                }
                i++;
            }
        }
//        final List<Integer>[] positions = new List<Integer>[indexToPositions.size()];
//        return indexToPositions.values().toArray(new List<Integer>[indexToPositions.size()]);
        return indexToPositions.values();
    }

    private static void identifyPotentialCloudShadow(int x0, int y0, int height, int width, Point2D[] cloudPath,
                                                     float[] longitude, float[] latitude, float[] altitude,
                                                     int[] flagArray, double sunZenithRad, int[] cloudIDArray,
                                                     Map<Integer, List<Integer>> indexToPositions) {
        int index0 = y0 * width + x0;
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }
        List<Integer> positions;
        if (indexToPositions.containsKey(cloudIDArray[index0])) {
            positions = indexToPositions.get(cloudIDArray[index0]);
        } else {
            positions = new ArrayList<>();
            indexToPositions.put(cloudIDArray[index0], positions);
        }
        int x1 = x0 + (int) cloudPath[1].getX();
        int y1 = y0 + (int) cloudPath[1].getY();
        if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0 ||
                (flagArray[y1 * width + x1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) {
            return;
        }
        for (int i = 1; i < cloudPath.length; i++) {
            x1 = x0 + (int) cloudPath[i].getX();
            y1 = y0 + (int) cloudPath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;
            if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    (!((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) &&
                    !positions.contains(index1)) {
                double[] cloudExtent = CloudVerticalExtent.getCloudVerticalExtentSentinel2();

                double[] distAltArray = CloudShadowUtils.computeDistance(index0, index1, longitude, latitude, altitude);
                double dist = distAltArray[0];
                double minAltitude = distAltArray[1];

                double cloudSearchPointHeight = dist * Math.tan(((Math.PI / 2. - sunZenithRad)));
                if (altitude[index1] < 0 || Double.isNaN(altitude[index1])) {
                    cloudSearchPointHeight -= minAltitude;
                } else {
                    cloudSearchPointHeight = cloudSearchPointHeight + (altitude[index1] - minAltitude);
                }
                if (cloudExtent[0] <= cloudSearchPointHeight && cloudSearchPointHeight <= cloudExtent[1]) {
                    flagArray[index1] += PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG;
                    positions.add(index1);
                }
            }
        }
    }

}
