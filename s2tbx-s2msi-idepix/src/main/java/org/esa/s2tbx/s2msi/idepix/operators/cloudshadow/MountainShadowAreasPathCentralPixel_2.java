package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.math.MathUtils;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Tonio Fincke
 */
public class MountainShadowAreasPathCentralPixel_2 {

    private static final boolean SHADOW_ADAPTER_SZA = true;
    private static final int MEAN_EARTH_RADIUS = 6372000;

    static void identifyMountainShadowArea(Product sourceProduct,
                                           Rectangle sourceRectangle, Rectangle targetRectangle,
                                           float[] sourceSunZenith, float[] sourceSunAzimuth,
                                           float[] sourceLatitude, float[] sourceLongitude, float[] sourceAltitude,
                                           int[] flagArray) {
        identifyMountainShadowArea(sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight(),
                                   sourceRectangle, targetRectangle, sourceSunZenith, sourceSunAzimuth,
                                   sourceLatitude, sourceLongitude, sourceAltitude, flagArray);
    }

    static void identifyMountainShadowArea(int productWidth, int productHeight,
                                           Rectangle sourceRectangle, Rectangle targetRectangle,
                                           float[] sourceSunZenith, float[] sourceSunAzimuth,
                                           float[] sourceLatitude, float[] sourceLongitude, float[] sourceAltitude,
                                           int[] flagArray) {
        int x0TargetCenter = (int) (targetRectangle.width / 2.);
        int y0TargetCenter = (int) (targetRectangle.width / 2.);
        int x0SourceCenter = x0TargetCenter + S2IdepixCloudShadowOp.searchBorderRadius;
        int y0SourceCenter = y0TargetCenter + S2IdepixCloudShadowOp.searchBorderRadius;

        double sunZenithDegree = sourceSunZenith[y0SourceCenter * sourceRectangle.width + x0SourceCenter];
        double sunZenithIntermediate;
        if (SHADOW_ADAPTER_SZA) {
            sunZenithIntermediate = sunZenithDegree * (2. * Math.pow(((90. - sunZenithDegree) / 90), 3) + 1.);
        } else {
            sunZenithIntermediate = sunZenithDegree;
        }
        double sunZenithRad = Math.min(89.0, sunZenithIntermediate) * MathUtils.DTOR;

        final float sunAzimuth = sourceSunAzimuth[y0SourceCenter * sourceRectangle.width + x0SourceCenter];
        double sunAzimuthRad = sunAzimuth * MathUtils.DTOR;

        final List<Float> altitudes = Arrays.asList(ArrayUtils.toObject(sourceAltitude));
        float maxAltitude = Collections.max(altitudes);
        float minAltitude = Collections.min(altitudes);

        final Point2D[] relativePath = CloudShadowUtils.getRelativePath(minAltitude, sunZenithRad, sunAzimuthRad,
                                                                        maxAltitude, sourceRectangle, targetRectangle,
                                                                        productWidth, productHeight,
                                                                        S2IdepixCloudShadowOp.spatialResolution);
        int xOffset = targetRectangle.x - sourceRectangle.x;
        int yOffset = targetRectangle.y - sourceRectangle.y;
        int i = 0;
        int width = sourceRectangle.x + sourceRectangle.width - targetRectangle.x;
        int height = sourceRectangle.y + sourceRectangle.height - targetRectangle.y;
        int max = width + height;
        if (sunAzimuth < 90) {
            while (i < max) {
                int x = xOffset + Math.min(i, width - 1);
                int y = height + Math.min(-1, height - 2 - i);
                while (x >= xOffset && y >= yOffset) {
                    identifyMountainShadow(x, y, relativePath, sourceRectangle, sunZenithRad, sourceAltitude,
                                           sourceLongitude, sourceLatitude, flagArray);
                    x--;
                    y--;
                }
                i++;
            }
        } else if (sunAzimuth < 180) {
            while (i < max) {
                int x = xOffset + Math.min(i, width - 1);
                int y = yOffset + Math.max(0, i - height + 1);
                while (x >= xOffset && y < height) {
                    identifyMountainShadow(x, y, relativePath, sourceRectangle, sunZenithRad, sourceAltitude,
                                           sourceLongitude, sourceLatitude, flagArray);
                    x--;
                    y++;
                }
                i++;
            }
        } else if (sunAzimuth < 270) {
            while (i < max) {
                int x = Math.max(0, width - 1 - i);
                int y = yOffset + Math.max(0, i - 9);
//                while (x < xOffset + width && y < yOffset + height) {
                while (x < width && y < height) {
                    identifyMountainShadow(x, y, relativePath, sourceRectangle, sunZenithRad, sourceAltitude,
                                           sourceLongitude, sourceLatitude, flagArray);
                    x++;
                    y++;
                }
                i++;
            }
        } else {
            while (i < max) {
                int x = Math.max(0, width - 1 - i);
                int y = height + Math.min(-1, height - 2 - i);
                while (x < height && y >= yOffset) {
                    identifyMountainShadow(x, y, relativePath, sourceRectangle, sunZenithRad, sourceAltitude,
                                           sourceLongitude, sourceLatitude, flagArray);
                    x++;
                    y--;
                }
                i++;
            }
        }
    }

    private static void identifyMountainShadow(int x0, int y0, Point2D[] relativePath, Rectangle sourceRectangle,
                                               double sunZenith, float[] sourceAltitude, float[] sourceLongitude,
                                               float[] sourceLatitude, int[] flagArray) {
        int index0 = y0 * sourceRectangle.width + x0;
        if (((flagArray[index0] & PreparationMaskBand.MOUNTAIN_SHADOW_FLAG) == PreparationMaskBand.MOUNTAIN_SHADOW_FLAG)) {
            return;
        }
        for (int i = 1; i < relativePath.length; i++) {
            int x1 = x0 + (int) relativePath[i].getX();
            int y1 = y0 + (int) relativePath[i].getY();
            if (y1 < sourceRectangle.height && x1 < sourceRectangle.width && y1 >= 0 && x1 >= 0) {
                //todo compute mountain extent correctly (however that is)
                double[] mountainExtent = MountainVerticalExtent.
                        getMountainVerticalExtent(sourceRectangle, sourceAltitude, y1, x1);
                double mountainBase = mountainExtent[0];
                double mountainTop = mountainExtent[1];
                int index1 = y1 * sourceRectangle.width + x1;
                double[] distAltArray = computeDistance(index0, index1, sourceLongitude, sourceLatitude,
                                                        sourceAltitude);
                double minAltitude = distAltArray[1];
                double mountainSearchPointHeight = distAltArray[0] * Math.tan(((Math.PI / 2. - sunZenith)));
                mountainSearchPointHeight = mountainSearchPointHeight + (sourceAltitude[index0] - minAltitude);
                if (mountainBase <= mountainSearchPointHeight && mountainSearchPointHeight <= (mountainTop - minAltitude)) {
                    flagArray[index0] += PreparationMaskBand.MOUNTAIN_SHADOW_FLAG;
                    for (int j = 1; j < i; j++) {
                        int x2 = x0 + (int) relativePath[j].getX();
                        int y2 = y0 + (int) relativePath[j].getY();
                        final Point2D.Double relativePos = new Point2D.Double(x1 - x2, y1 - y2);
                        if (org.esa.snap.core.util.ArrayUtils.isMemberOf(relativePos, relativePath)) {
                            int index2 = y2 * sourceRectangle.width + x2;
                            flagArray[index2] += PreparationMaskBand.MOUNTAIN_SHADOW_FLAG;
                        }
                    }
                    return;
                }
            }
        }
    }

    private static double[] computeDistance(int index0, int indexPath, float[] sourceLongitude, float[] sourceLatitude,
                                            float[] sourceAltitude) {
        double k = Math.PI / 180.0;
        double geoPos1Lon = sourceLongitude[index0];
        double geoPos1Lat = sourceLatitude[index0];
        double geoPos2Lon = sourceLongitude[indexPath];
        double geoPos2Lat = sourceLatitude[indexPath];
        double minAltitude = (double) Math.min(sourceAltitude[index0], sourceAltitude[indexPath]);

        double cosPos1Lat = Math.cos(geoPos1Lat * k);
        double cosPos2Lat = Math.cos(geoPos2Lat * k);
        double sinPos1Lat = Math.sin(geoPos1Lat * k);
        double sinPos2Lat = Math.sin(geoPos2Lat * k);
        double delta = (geoPos2Lon - geoPos1Lon) * k;
        double cosDelta = Math.cos(delta);
        double sinDelta = Math.sin(delta);
        double y = Math.sqrt(Math.pow(cosPos2Lat * sinDelta, 2) +
                                     Math.pow(cosPos1Lat * sinPos2Lat - sinPos1Lat * cosPos2Lat * cosDelta, 2));
        double x = sinPos1Lat * sinPos2Lat + cosPos1Lat * cosPos2Lat * cosDelta;
        double ad = Math.atan2(y, x);
        double[] distAltArray = new double[3];
        distAltArray[0] = ad * (MEAN_EARTH_RADIUS + minAltitude);
        distAltArray[1] = minAltitude;
        return distAltArray;
    }

}
