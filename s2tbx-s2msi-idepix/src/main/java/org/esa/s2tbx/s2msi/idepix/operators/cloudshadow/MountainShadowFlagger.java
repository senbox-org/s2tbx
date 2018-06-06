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

    static void flagMountainShadowArea(int productWidth, int productHeight, Rectangle sourceRectangle,
                                       Rectangle targetRectangle, float sourceSunZenith, float sourceSunAzimuth,
                                       float[] sourceAltitude, int[] flagArray, float minAltitude, float maxAltitude, Point2D[] relativePath) {
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
        /*final Point2D[] relativePath = CloudShadowUtils.getRelativePath(minAltitude, sunZenithRad, sunAzimuthRad,
                                                                        maxAltitude, sourceRectangle, targetRectangle,
                                                                        productHeight, productWidth,
                                                                        S2IdepixPreCloudShadowOp.spatialResolution, false,
                                                                        true);
                                                                        */
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

        if (sourceSunAzimuth < 90) {
            //start at upper right(not necessary, direction of search is appointed in identifyPotentialCloudShadow)
            xOffset = targetRectangle.x - sourceRectangle.x;
        } else if (sourceSunAzimuth < 180) {
            //start at lower right (not necessary, direction of search is appointed in identifyPotentialCloudShadow)
            xOffset = targetRectangle.x - sourceRectangle.x;
            yOffset = targetRectangle.y - sourceRectangle.y;
        } else if (sourceSunAzimuth < 270) {
            //start at lower left (not necessary, direction of search is appointed in identifyPotentialCloudShadow)
            yOffset = targetRectangle.y - sourceRectangle.y;
        }
        for(int i = xOffset; i < sourceWidth ; i++){
            for ( int j = yOffset; j< sourceHeight; j++){
                identifyMountainShadow(i, j, relativePath, relativeMinMountainHeights, sourceHeight, sourceWidth,
                        sourceAltitude, flagArray, maxAltitude);
            }
        }

        /*int xOffset = targetRectangle.x - sourceRectangle.x;
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
                    identifyMountainShadow(x, y, relativePath, relativeMinMountainHeights, sourceHeight, sourceWidth,
                                           sourceAltitude, flagArray, maxAltitude);
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
                    identifyMountainShadow(x, y, relativePath, relativeMinMountainHeights, sourceHeight, sourceWidth,
                                           sourceAltitude, flagArray, maxAltitude);
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
                    identifyMountainShadow(x, y, relativePath, relativeMinMountainHeights, sourceHeight, sourceWidth,
                                           sourceAltitude, flagArray, maxAltitude);
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
                    identifyMountainShadow(x, y, relativePath, relativeMinMountainHeights, sourceHeight, sourceWidth,
                                           sourceAltitude, flagArray, maxAltitude);
                    x++;
                    y--;
                }
                i++;
            }
        }*/
    }

    static double[] getRelativeMinMountainHeights(Point2D[] relativePath, double spatialResolution, double sunZenith) {
        double[] relativeMinMountainHeights = new double[relativePath.length];
        //final double sunFactor = Math.tan(Math.PI / 2. - sunZenith);
        final double sunFactor = Math.tan(sunZenith);
        for (int i = 0; i < relativePath.length; i++) {
            final double x = relativePath[i].getX();
            final double y = relativePath[i].getY();
            double dist = Math.sqrt(Math.pow(spatialResolution * x, 2) + Math.pow(spatialResolution * y, 2));
            //relativeMinMountainHeights[i] = dist * sunFactor;
            relativeMinMountainHeights[i] = dist / sunFactor;
        }
        return relativeMinMountainHeights;
    }

    private static void identifyMountainShadow(int x0, int y0, Point2D[] relativePath,
                                               double[] relativeMinMountainHeights, int height, int width,
                                               float[] sourceAltitude, int[] flagArray, float maxAltitude) {
        int index0 = y0 * width + x0;
        float altitude0 = sourceAltitude[index0];
        if (((flagArray[index0] & PreparationMaskBand.MOUNTAIN_SHADOW_FLAG) == PreparationMaskBand.MOUNTAIN_SHADOW_FLAG)
                || Float.isNaN(altitude0)) {
            return;
        }
        for (int i = 1; i < relativePath.length; i++) {
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
            //if (altitude1 - altitude0 > relativeMinMountainHeights[i]) {
            if (altitude0 - altitude1 > relativeMinMountainHeights[i]) {
                if (!((flagArray[index1] & PreparationMaskBand.MOUNTAIN_SHADOW_FLAG) ==
                        PreparationMaskBand.MOUNTAIN_SHADOW_FLAG)) {
                    flagArray[index1] += PreparationMaskBand.MOUNTAIN_SHADOW_FLAG;
                }

                //for (int j = 1; j < i; j++) {
                /*for (int j = i; j < relativePath.length; j++) {
                    int x2 = x0 + (int) relativePath[j].getX();
                    int y2 = y0 + (int) relativePath[j].getY();
                    final Point2D.Double relativePos = new Point2D.Double(x1 - x2, y1 - y2);
                    if (org.esa.snap.core.util.ArrayUtils.isMemberOf(relativePos, relativePath)) {
                        int index2 = y2 * width + x2;
                        if (!((flagArray[index2] & PreparationMaskBand.MOUNTAIN_SHADOW_FLAG) ==
                                PreparationMaskBand.MOUNTAIN_SHADOW_FLAG)) {
                            flagArray[index2] += PreparationMaskBand.MOUNTAIN_SHADOW_FLAG;
                        }
                    }
                }*/
                return;
            }
        }
    }

}
