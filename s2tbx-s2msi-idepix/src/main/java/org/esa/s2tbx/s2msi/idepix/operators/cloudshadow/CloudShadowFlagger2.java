package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.util.math.MathUtils;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tonio Fincke
 */
class CloudShadowFlagger2 {

    static void flagCloudShadows(Rectangle sourceRectangle, Rectangle targetRectangle,
                                 float sourceSunZenith, float sourceSunAzimuth,
                                 float[] sourceLatitude, float[] sourceLongitude,
                                 float[] sourceAltitude, int[] flagArray,
                                 Point2D[] cloudPath, float[] b8a) {
        double sunZenithCloudRad = (double) sourceSunZenith * MathUtils.DTOR;
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
                    flagCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude,
                                    sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                                    b8a);
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
                    flagCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude,
                                    sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                                    b8a);
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
                    flagCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude,
                                    sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                                    b8a);
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
                    flagCloudShadow(x, y, sourceHeight, sourceWidth, cloudPath, sourceLongitude,
                                    sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                                    b8a);
                    x--;
                    y++;
                }
                i++;
            }
        }
    }

    private static void flagCloudShadow(int x0, int y0, int height, int width, Point2D[] cloudPath,
                                        float[] longitude, float[] latitude, float[] altitude,
                                        int[] flagArray, double sunZenithRad, float[] b8a) {
        int index0 = y0 * width + x0;
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }
        int x1 = x0 + (int) cloudPath[1].getX();
        int y1 = y0 + (int) cloudPath[1].getY();
        if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0 ||
                (flagArray[y1 * width + x1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) {
            return;
        }
        float[] b8aPath = new float[cloudPath.length];
        float[] b8aPathSlope = new float[cloudPath.length];
        Arrays.fill(b8aPath, Float.NaN);
        b8aPath[0] = b8a[index0];
        final ArrayList<Integer> valleyStarts = new ArrayList<>();
        final ArrayList<Integer> valleyEnds = new ArrayList<>();
        for (int i = 1; i < cloudPath.length; i++) {
            x1 = x0 + (int) cloudPath[i].getX();
            y1 = y0 + (int) cloudPath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;
            if (!isFlagged(flagArray, index1) &&
                    !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {
                b8aPath[i] = b8a[index1];
                if (i > 1 && !Float.isNaN(b8aPath[i - 2]) && !Float.isNaN(b8aPath[i - 1])) {
                    b8aPathSlope[i - 1] = (b8aPath[i - 2] - b8aPath[i]) / Math.max(b8aPath[i - 2], b8aPath[i]);
                    if (b8aPathSlope[i - 1] > 0.7) {
                        fillCloudShadowArea(x1, y1, flagArray, b8a, width, height);
                    } else if (b8aPathSlope[i - 1] < -0.7) {
                        int xPrev = x0 + (int) cloudPath[i - 2].getX();
                        int yPrev = y0 + (int) cloudPath[i - 2].getY();
                        if (!isFlagged(flagArray, index(xPrev, yPrev, width))) {
                            fillCloudShadowArea(xPrev, yPrev, flagArray, b8a, width, height);
                        }
                    }
                }
            }
        }
    }

    private static boolean isFlagged(int[] flagArray, int index) {
        return (flagArray[index] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG ||
                (flagArray[index] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG;
    }

    private static void fillCloudShadowArea(int x, int y, int[] flagArray, float[] b8a, int width, int height) {
        int index = index(x, y, width);
        flagArray[index] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
        int lowerY = y > 0 ? y - 1 : y;
        int upperY = y < height - 1 ? y + 1 : y;
        int lowerIndex = index(x, lowerY, width);
        int upperIndex = index(x, upperY, width);
        int leftX = x > 0 ? x - 1 : x;
        int rightX = x < width - 1 ? x + 1 : x;
        int leftIndex = index(leftX, y, width);
        int rightIndex = index(rightX, y, width);
        if (!isFlagged(flagArray, upperIndex) &&
                isCloseTo(lowerIndex, upperIndex, b8a) &&
                isCloseTo(index, upperIndex, b8a)) {
            fillCloudShadowArea(x, upperY, flagArray, b8a, width, height);
        }
        if (!isFlagged(flagArray, lowerIndex) &&
                isCloseTo(upperIndex, lowerIndex, b8a) &&
                isCloseTo(index, lowerIndex, b8a)) {
            fillCloudShadowArea(x, lowerY, flagArray, b8a, width, height);
        }
        if (!isFlagged(flagArray, rightIndex) &&
                isCloseTo(leftIndex, rightIndex, b8a) &&
                isCloseTo(index, rightIndex, b8a)) {
            fillCloudShadowArea(rightX, y, flagArray, b8a, width, height);
        }
        if (!isFlagged(flagArray, leftIndex) &&
                isCloseTo(rightIndex, leftIndex, b8a) &&
                isCloseTo(index, leftIndex, b8a)) {
            fillCloudShadowArea(leftX, y, flagArray, b8a, width, height);
        }
    }

    private static int index(int x, int y, int width) {
        return y * width + x;
    }

    private static boolean isCloseTo(int fromIndex, int toIndex, float[] b8a) {
        final float slope = (b8a[toIndex] - b8a[fromIndex]) / Math.max(b8a[fromIndex], b8a[toIndex]);
        return slope < 0.1;
    }

    static int[][] detectValleys(List<Integer> starts, List<Integer> ends, int maxPos) {
        if (starts.size() == 0 && ends.size() == 0) {
            return new int[0][0];
        }
        if (starts.size() == 0) {
            return new int[][]{{0, ends.get(ends.size() - 1)}};
        }
        if (ends.size() == 0) {
            return new int[][]{{starts.get(0), maxPos}};
        }
        int startCounter = 0;
        int endCounter = 0;
        int end = ends.get(0);
        int start = 0;
        int nextStart;
        int nextEnd;
        if (end > starts.get(0)) {
            start = starts.get(0);
        }
        List<int[]> valleyList = new ArrayList<>();
        while (startCounter < starts.size()) {
            while (startCounter < starts.size() && starts.get(startCounter) < end) {
                startCounter++;
            }
            if (startCounter < starts.size()) {
                nextStart = starts.get(startCounter);
            } else {
                nextStart = maxPos;
            }
            while (endCounter < ends.size() && ends.get(endCounter) < nextStart) {
                end = ends.get(endCounter);
                endCounter++;
            }
            if (endCounter < ends.size()) {
                nextEnd = ends.get(endCounter);
            } else {
                nextEnd = maxPos;
            }
            valleyList.add(new int[]{start, end});
            start = nextStart;
            end = nextEnd;
        }
        return valleyList.toArray(new int[valleyList.size()][]);
    }

}
