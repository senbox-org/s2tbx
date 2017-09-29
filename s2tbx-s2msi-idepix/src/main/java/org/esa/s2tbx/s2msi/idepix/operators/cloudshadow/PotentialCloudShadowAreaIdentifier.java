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
class PotentialCloudShadowAreaIdentifier {

    private static final double MAXCLOUD_TOP = S2IdepixCloudShadowOp.maxcloudTop;

    static int[][] identifyPotentialCloudShadows(int productHeight, int productWidth, Rectangle sourceRectangle,
                                                 Rectangle targetRectangle, float[] sourceSunZenith,
                                                 float[] sourceSunAzimuth, float[] sourceLatitude,
                                                 float[] sourceLongitude, float[] sourceAltitude,
                                                 int[] flagArray, int[] cloudIDArray, int[] cloudShadowIDArray,
                                                 int numClouds) {
        int x0SourceCenter = sourceRectangle.width / 2;
        int y0SourceCenter = sourceRectangle.height / 2;
        int sourceCenterIndex = y0SourceCenter * sourceRectangle.width + x0SourceCenter;

        final float sunAzimuth = sourceSunAzimuth[sourceCenterIndex];
        double sunAzimuthRad = sunAzimuth * MathUtils.DTOR;
        double sunZenithDegree = sourceSunZenith[sourceCenterIndex];

        final List<Float> altitudes = Arrays.asList(ArrayUtils.toObject(sourceAltitude));
        float minAltitude = Collections.min(altitudes);

        double sunZenithCloudRad = sunZenithDegree * MathUtils.DTOR;
        Point2D[] cloudPath = CloudShadowUtils.getRelativePath(minAltitude, sunZenithCloudRad, sunAzimuthRad,
                                                               MAXCLOUD_TOP, sourceRectangle, targetRectangle,
                                                               productHeight, productWidth,
                                                               S2IdepixCloudShadowOp.spatialResolution, true, false);
        int[][] cloudShadowIdBorderRectangle = new int[numClouds][4];
        for (int i = 0; i < numClouds; i++) {
            cloudShadowIdBorderRectangle[i][0] = productWidth + 1;
            cloudShadowIdBorderRectangle[i][1] = -1;
            cloudShadowIdBorderRectangle[i][2] = productHeight + 1;
            cloudShadowIdBorderRectangle[i][3] = -1;
        }

        int i = 0;
        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        if (sunAzimuth < 90) {
            //start at upper right
            int xOffset = targetRectangle.x - sourceRectangle.x;
            int xLimit = sourceRectangle.x + sourceWidth - targetRectangle.x;
            int yLimit = targetRectangle.y - sourceRectangle.y + targetRectangle.height;
            int max = xLimit + yLimit - 1;
            while (i < max) {
                int x = Math.max(xOffset, sourceWidth - 1 - i);
                int y = Math.max(0, i - yLimit + 1);
                while (x < sourceWidth && y < yLimit) {
                    identifyPotentialCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude, sourceLatitude,
                                                 sourceAltitude, flagArray, sunZenithCloudRad, cloudIDArray,
                                                 cloudShadowIDArray, cloudShadowIdBorderRectangle);
                    x++;
                    y++;
                }
                i++;
            }
        } else if (sunAzimuth < 180) {
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
                    identifyPotentialCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude, sourceLatitude,
                                                 sourceAltitude, flagArray, sunZenithCloudRad, cloudIDArray,
                                                 cloudShadowIDArray, cloudShadowIdBorderRectangle);
                    x++;
                    y--;
                }
                i++;
            }
        } else if (sunAzimuth < 270) {
            //start at lower left
            int yOffset = targetRectangle.y - sourceRectangle.y;
            int xLimit = targetRectangle.x - sourceRectangle.x + targetRectangle.width;
            int yLimit = sourceRectangle.y + sourceHeight - targetRectangle.y;
            int max = xLimit + yLimit - 1;
            while (i < max) {
                int x = Math.min(i, xLimit - 1);
                int y = sourceHeight + Math.min(-1, yLimit - 2 - i);
                while (x >= 0 && y >= yOffset) {
                    identifyPotentialCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude, sourceLatitude,
                                                 sourceAltitude, flagArray, sunZenithCloudRad, cloudIDArray,
                                                 cloudShadowIDArray, cloudShadowIdBorderRectangle);
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
                    identifyPotentialCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude, sourceLatitude,
                                                 sourceAltitude, flagArray, sunZenithCloudRad, cloudIDArray,
                                                 cloudShadowIDArray, cloudShadowIdBorderRectangle);
                    x--;
                    y++;
                }
                i++;
            }
        }
        return cloudShadowIdBorderRectangle;
    }

    private static void identifyPotentialCloudShadow(int x0, int y0, int height, int width,
                                                     Point2D[] cloudPath, float[] longitude, float[] latitude,
                                                     float[] altitude, int[] flagArray, double sunZenithRad,
                                                     int[] cloudIDArray, int[] cloudShadowIDArray,
                                                     int[][] cloudShadowIdBorderRectangle) {
        int index0 = y0 * width + x0;
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }
        for (int i = 1; i < cloudPath.length; i++) {
            int x1 = x0 + (int) cloudPath[i].getX();
            int y1 = y0 + (int) cloudPath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;
            // todo add function to not
            if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    (!((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG))) {

                double[] cloudExtent = CloudVerticalExtent.getCloudVerticalExtentSentinal2();

                double[] distAltArray = CloudShadowUtils.computeDistance(index0, index1, longitude, latitude, altitude);
                double dist = distAltArray[0];
                double minAltitude = distAltArray[1];

                double cloudSearchPointHeight = dist * Math.tan(((Math.PI / 2. - sunZenithRad)));
                cloudSearchPointHeight = cloudSearchPointHeight + (altitude[index1] - minAltitude);
                if (cloudExtent[0] <= cloudSearchPointHeight && cloudSearchPointHeight <= cloudExtent[1]) {
                    cloudShadowIDArray[index1] = cloudIDArray[index0];

                    int minX0 = Math.min(cloudShadowIdBorderRectangle[cloudShadowIDArray[index1]][0], x1);
                    int maxX0 = Math.max(cloudShadowIdBorderRectangle[cloudShadowIDArray[index1]][1], x1);
                    int minY0 = Math.min(cloudShadowIdBorderRectangle[cloudShadowIDArray[index1]][2], y1);
                    int maxY0 = Math.max(cloudShadowIdBorderRectangle[cloudShadowIDArray[index1]][3], y1);

                    cloudShadowIdBorderRectangle[cloudShadowIDArray[index1]][0] = Math.min(minX0, maxX0);
                    cloudShadowIdBorderRectangle[cloudShadowIDArray[index1]][1] = Math.max(minX0, maxX0);
                    cloudShadowIdBorderRectangle[cloudShadowIDArray[index1]][2] = Math.min(minY0, maxY0);
                    cloudShadowIdBorderRectangle[cloudShadowIDArray[index1]][3] = Math.max(minY0, maxY0);
                }
            }
        }
    }

}
