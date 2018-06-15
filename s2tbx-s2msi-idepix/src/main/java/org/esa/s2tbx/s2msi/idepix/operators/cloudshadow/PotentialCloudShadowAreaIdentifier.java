package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.math.MathUtils;
import org.geotools.xml.xsi.XSISimpleTypes;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Tonio Fincke
 * @author Dagmar Mueller
 */
class PotentialCloudShadowAreaIdentifier {

    private static Logger logger = SystemUtils.LOG;

    private static final double MAXCLOUD_TOP = S2IdepixPreCloudShadowOp.maxcloudTop;
    private static final double MINCLOUD_BASE = S2IdepixPreCloudShadowOp.mincloudBase;

    static Map[] identifyPotentialCloudShadowsPLUS(Rectangle sourceRectangle, Rectangle targetRectangle,
                                                   float sourceSunZenith, float sourceSunAzimuth,
                                                   float[] sourceLatitude, float[] sourceLongitude,
                                                   float[] sourceAltitude, int[] flagArray, int[] cloudIDArray,
                                                   Point2D[] cloudPath) {
        double sunZenithCloudRad = (double) sourceSunZenith * MathUtils.DTOR;
        final Map<Integer, List<Integer>> indexToPositions = new HashMap<>();
        final Map<Integer, List<Integer>> offsetAtPositions = new HashMap<>();

        int i = 0;
        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;

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
        for (i = xOffset; i < sourceWidth; i++) {
            for (int j = yOffset; j < sourceHeight; j++) {
                identifyPotentialCloudShadowPLUS(i, j, sourceHeight, sourceWidth, cloudPath, sourceLongitude,
                        sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                        cloudIDArray, indexToPositions, offsetAtPositions);
            }
        }

//        /*
//
//        The lists 'indexToPositions' contains duplicated pixel, because the search depth at a cloud border is
//        two pixels deep (which is necessary to avoid Moire-Effects).
//        The duplicates need to be identified in indexToPosition, but they need to be removed in both lists.
//        They are not duplicates in the 'offsetAtPositions' list.
//        For each List per cloudID individually, but based on indexToPositions.
//        CAUTION: It might be necessary to adjust the incrementation of x, y so that the first entry of a
//        duplicated position is also the one with the smallest offset.
//        It is not checked, whether the offset of the duplicated pixel is the smallest one (which it should be).
//
//        No SOLUTION:
//        checking the lists is no solution! contains() is very time consuming!
//         */

        // REMOVE the duplicates!
        // formerly done in CloudShadowFlaggerCombination

        for (int key : indexToPositions.keySet()) {
            /*
            positions and offsetAtPosition can contain duplicates!
            Removing duplicates, Keeping the smaller offset at a position...
             */
            List<Integer> positions = indexToPositions.get(key);
            List<Integer> offsetAtPos = offsetAtPositions.get(key);

            List<Integer> noduplicatesPositions = new ArrayList<>(new LinkedHashSet<>(positions));

            if (noduplicatesPositions.size() < positions.size()) {
                int test[] = new int[flagArray.length];
                for (int k = 0; k < positions.size(); k++) {
                    int off = offsetAtPos.get(k);
                    int ind = positions.get(k);
                    if (ind < test.length) {
                        if (test[ind] > off || test[ind] == 0) {
                            test[ind] = off;
                        }
                    } else
                        logger.fine("Index: " + ind + " outside range");
                }

                List<Integer> noduplicatesOffsets = new ArrayList<>();
                for (int k : noduplicatesPositions) {
                    noduplicatesOffsets.add(test[k]);
                }

                positions.clear();
                positions.addAll(noduplicatesPositions);
                offsetAtPos.clear();
                offsetAtPos.addAll(noduplicatesOffsets);
            }
        }
        final Map[] output = new Map[2];
        output[0] = indexToPositions;
        output[1] = offsetAtPositions;
        return output;
    }

    private static void identifyPotentialCloudShadowPLUS(int x0, int y0, int height, int width, Point2D[] cloudPath,
                                                         float[] longitude, float[] latitude, float[] altitude,
                                                         int[] flagArray, double sunZenithRad, int[] cloudIDArray,
                                                         Map<Integer, List<Integer>> indexToPositions,
                                                         Map<Integer, List<Integer>> offsetAtPositions) {
        int index0 = y0 * width + x0;
        //start from a cloud pixel, otherwise stop.
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }

        int x1 = x0 + (int) cloudPath[1].getX();
        int y1 = y0 + (int) cloudPath[1].getY();
        int x2 = x0 + (int) cloudPath[2].getX();
        int y2 = y0 + (int) cloudPath[2].getY();
        // cloud edge is used at least 2 pixels deep, otherwise gaps occur due to orientation of cloud edge and cloud path.
        // (Moire-Effect)
        if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0 || x2 >= width || y2 >= height || x2 < 0 || y2 < 0 ||
                ((flagArray[y1 * width + x1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG &&
                        (flagArray[y2 * width + x2] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }
        // Initialising List for indexToPositions
        List<Integer> positions;
        if (indexToPositions.containsKey(cloudIDArray[index0])) {
            positions = indexToPositions.get(cloudIDArray[index0]);
        } else {
            positions = new ArrayList<>();
            indexToPositions.put(cloudIDArray[index0], positions);
        }
        // Initialising List for offsetAtPositions
        List<Integer> offsets;
        if (offsetAtPositions.containsKey(cloudIDArray[index0])) {
            offsets = offsetAtPositions.get(cloudIDArray[index0]);
        } else {
            offsets = new ArrayList<>();
            offsetAtPositions.put(cloudIDArray[index0], offsets);
        }
        for (int i = 1; i < cloudPath.length; i++) {
            x1 = x0 + (int) cloudPath[i].getX();
            y1 = y0 + (int) cloudPath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;

            if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)
                    ) {

                //Dagmar: is fixated to minimum and latitude-dependent maximum
                double[] distAltArray = CloudShadowUtils.computeDistance(index0, index1, longitude, latitude, altitude);
                double dist = distAltArray[0];
                double minAltitude = distAltArray[1];
                double cloudSearchPointHeight = dist * Math.tan(((Math.PI / 2. - sunZenithRad)));
                if (altitude[index1] < 0 || Double.isNaN(altitude[index1])) {
                    cloudSearchPointHeight -= minAltitude;
                } else {
                    cloudSearchPointHeight = cloudSearchPointHeight + (altitude[index1] - minAltitude);
                }
                // Dagmar: flag is set only, if not already potential_cloud_shadow. Otherwise, it gets turned off.
                if (MINCLOUD_BASE <= cloudSearchPointHeight && cloudSearchPointHeight <= MAXCLOUD_TOP) {
                    if (!((flagArray[index1] & PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG) == PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG)) {
                        flagArray[index1] += PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG;
                    }
                    positions.add(index1);
                    offsets.add(i);
                }
            }
        }
        if (positions.size() == 0) {
            indexToPositions.remove(cloudIDArray[index0]);
            offsetAtPositions.remove(cloudIDArray[index0]);
        }
    }
}
