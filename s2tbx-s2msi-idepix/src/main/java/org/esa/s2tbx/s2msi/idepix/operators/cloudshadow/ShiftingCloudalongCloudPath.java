package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.util.math.MathUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class ShiftingCloudalongCloudPath {

    private static final double MAXCLOUD_TOP = S2IdepixPreCloudShadowOp.maxcloudTop;
    private static final double MINCLOUD_BASE = S2IdepixPreCloudShadowOp.mincloudBase;

    //static Collection<List<Integer>>
    void shiftingCloudalongCloudPath(Rectangle sourceRectangle,
                                     Rectangle targetRectangle, float sourceSunZenith,
                                     float sourceSunAzimuth, float[] sourceLatitude,
                                     float[] sourceLongitude, float[] sourceAltitude,
                                     float[][] sourceBands,
                                     int[] flagArray, int[] cloudIDArray, Point2D[] cloudPath) {
        double sunZenithCloudRad = (double) sourceSunZenith * MathUtils.DTOR;
        //final Map<Integer, List<Integer>> indexToPositions = new HashMap<>();
        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;

        System.out.println(sourceSunAzimuth);

        //search rectangle: tile + extension in certain directions
        int xOffset = 0;
        int yOffset = 0;
        if (sourceSunAzimuth < 90) {
            xOffset = targetRectangle.x - sourceRectangle.x;
        } else if (sourceSunAzimuth < 180) {
            xOffset = targetRectangle.x - sourceRectangle.x;
            yOffset = targetRectangle.y - sourceRectangle.y;
        } else if (sourceSunAzimuth < 270) {
            yOffset = targetRectangle.y - sourceRectangle.y;
        }

        //iteration over cloud path positions.
        // cloudPath.length, cloudPath[i].getX() or .getY()
        //

        final Map<Integer, List<Double>> meanValuesPath = new HashMap<>();
        final Map<Integer, List<Integer>> NPath = new HashMap<>();

        System.out.println(cloudPath.length);
        System.out.println();

        for (int path_i = 1; path_i < cloudPath.length; path_i++) {
            //for (int path_i = 10; path_i < 11; path_i++) {
            //collect index per cloudID and cloudpath step.
            // - setup index of water or land pixels at cloud path step. just like identifyPotentialCloudShadow, but without cloudPath iteration. This is fixed to the path_i step.

            Map<Integer, List<Integer>> indexToPositions = new HashMap<>();

            for(int x0 = xOffset; x0 < sourceWidth ; x0++){
                for ( int y0 = yOffset; y0< sourceHeight; y0++){
                    //each pixel needs to be tested, whether it is cloud or not.
                    //based on identifyPotentialCloudShadow()

                    identifyPotentialCloudShadow_atPosition(x0, y0, sourceHeight, sourceWidth, cloudPath, path_i, sourceLongitude,
                                                            sourceLatitude, sourceAltitude, flagArray, sunZenithCloudRad,
                                                            cloudIDArray, indexToPositions);

                }
            }


            //calculate mean reflectance for each cloud ID. similar to flagCloudShadowAreas
            PotentialShadowAnalyzerMode shadowAnalyser = new PotentialShadowAnalyzerMode(sourceBands, flagArray);



            for ( int key : indexToPositions.keySet()) {    //positions.

                List<Integer> positions = indexToPositions.get(key);
                double setMeanValue=1.;

                if(positions.size() > 0){
                    shadowAnalyser.initArrays(positions.size());
                    for (int index : positions) {
                        shadowAnalyser.doIterationStep(index);
                    }
                    double[] mean = shadowAnalyser.calculateMean();
                    setMeanValue = mean[0];
                }
                List<Double> meanValues;
                if (meanValuesPath.containsKey(key)) {
                    meanValues = meanValuesPath.get(key);
                } else {
                    meanValues = new ArrayList<>();
                    meanValuesPath.put(key, meanValues);
                }
                meanValues.add(setMeanValue);

                List<Integer> N;
                if (NPath.containsKey(key)) {
                    N = NPath.get(key);
                } else {
                    N = new ArrayList<>();
                    NPath.put(key, N);
                }
                N.add(positions.size());

                //cloudIDArray[positions.get(0)]


                // this results have to be stored, probably in another list.
                // in the end, I need the mean reflectance values as a function of the cloudpath-Step, for each cloudID.
                // the best offset for the cloud-mask with that ID is at the minimum of the mean reflectance.
            }

        }

        final Map<Integer, List<Integer>> bestPosition = new HashMap<>();

        for ( int key : meanValuesPath.keySet()) {
            //clouds with full cloud path inside the source area have a list of mean values (or 1) attached, which has the length cloudPath.length-1
            List<Double> meanValues = meanValuesPath.get(key);
            List<Integer> N = NPath.get(key);


            //if (meanValues.size()==cloudPath.length-1){
            if (meanValues.size()> 2){
                double[] cumMean= new double[meanValues.size()-1];
                double[] cumN= new double[meanValues.size()-1];
                cumMean[0] = meanValues.get(0)*N.get(0);
                cumN[0] = N.get(0);

                double darkValue = 1.;
                if (cumMean[0]< darkValue){
                    darkValue = cumMean[0];
                }
                int darkIndex = 0;

                for (int i = 1; i<meanValues.size()-1; i++){
                    cumN[i] = cumN[i-1]+N.get(i);
                    cumMean[i] = (meanValues.get(i)*N.get(i) + cumMean[i-1]*cumN[i-1])/cumN[i];

                    if (cumMean[i]< darkValue){
                        darkValue = cumMean[i];
                        darkIndex = i;
                    }
                }

                List<Integer> best;
                if (bestPosition.containsKey(key)) {
                    best = bestPosition.get(key);
                } else {
                    best = new ArrayList<>();
                    bestPosition.put(key, best);
                }
                best.add(darkIndex);

                //Minimum in cumMean gives the ideal offset along the path.

            }

            /*System.out.print(key);
            System.out.print(' ');
            System.out.println(bestPosition.get(key));*/
        }

        //set the cloud shadow flag for each ID according to the offset determined in bestPosition.
        // iterate through all positions. for each cloud pixel set the accordingly shifted pixel along the cloud path to cloud shadow.

        for(int x0 = xOffset; x0 < sourceWidth ; x0++){
            for ( int y0 = yOffset; y0< sourceHeight; y0++){

                setShiftedCloud(x0, y0, sourceHeight, sourceWidth, cloudPath, flagArray, cloudIDArray, bestPosition);

            }
        }



    }

    private static void identifyPotentialCloudShadow_atPosition(int x0, int y0, int height, int width, Point2D[] cloudPath, int path_i,
                                                                float[] longitude, float[] latitude, float[] altitude,
                                                                int[] flagArray, double sunZenithRad, int[] cloudIDArray,
                                                                Map<Integer, List<Integer>> indexToPositions) {
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

        //no iteration over cloudPath, but fixed step.
        x1 = x0 + (int) cloudPath[path_i].getX();
        y1 = y0 + (int) cloudPath[path_i].getY();
        if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
            //break; only necessary in the for-loop, which is no longer used.
            return;
        }
        int index1 = y1 * width + x1;

        List<Integer> positions;
        if (indexToPositions.containsKey(cloudIDArray[index0])) {
            positions = indexToPositions.get(cloudIDArray[index0]);
        } else {
            positions = new ArrayList<>();
            indexToPositions.put(cloudIDArray[index0], positions);
        }

        if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {

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
            //if (cloudExtent[0] <= cloudSearchPointHeight && cloudSearchPointHeight <= cloudExtent[1]) {
            //if (MINCLOUD_BASE <= cloudSearchPointHeight && cloudSearchPointHeight <= MAXCLOUD_TOP) {
            // todo: why is the cloud SearchPointHeight lower than mincloud_base?
            if (cloudSearchPointHeight <= MAXCLOUD_TOP) {
                if(!((flagArray[index1] & PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG) == PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG)) {
                    flagArray[index1] += PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG;
                }
                positions.add(index1);
            }
        }
    }

    private static void setShiftedCloud(int x0, int y0, int height, int width, Point2D[] cloudPath,
                                        int[] flagArray, int[] cloudIDArray, Map<Integer, List<Integer>> bestPosition) {
        int index0 = y0 * width + x0;
        //start from a cloud pixel, otherwise stop.
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }

        List<Integer> bestOffset;
        if (bestPosition.containsKey(cloudIDArray[index0])) {
            bestOffset = bestPosition.get(cloudIDArray[index0]);

            int x1 = x0 + (int) cloudPath[bestOffset.get(0)].getX();
            int y1 = y0 + (int) cloudPath[bestOffset.get(0)].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                //break; only necessary in the for-loop, which is no longer used.
                return;
            }
            int index1 = y1 * width + x1;

            if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {

                if(!((flagArray[index1] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)) {
                    flagArray[index1] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                }
            }
        }
    }

    private class PotentialShadowAnalyzerMode {

        private final float[][] sourceBands;
        int counterA;
        int counterB;
        private double[][] arrayBands;
        private int[][] arrayIndexes;
        private double[] minArrayBands;
        private int[] flagArray;
        double[] mean;
        //private final float[] landThreshholds;
        //private final float[] waterThreshholds;
        //private final double landMean;
        //private final double waterMean;

        PotentialShadowAnalyzerMode(float[][] sourceBands, int[] flagArray) {
            if (sourceBands.length != 2) {
                throw new IllegalArgumentException("Two bands required for land water analysis mode");
            }
            this.sourceBands = sourceBands;
            this.flagArray = flagArray;
            //landThreshholds = getThresholds(sourceBands, false, true);
            //waterThreshholds = getThresholds(sourceBands, true, false);
            //landMean = nonCloudMeans(sourceBands, flagArray, false, true)[0];
            //waterMean = nonCloudMeans(sourceBands, flagArray, true, false)[1];
        }

        public void initArrays(int size) {
            arrayBands = new double[2][size];
            arrayIndexes = new int[2][size];
            minArrayBands = new double[2];
            mean = new double[2];
            counterA = 0;
            counterB = 0;
            for (int i = 0; i < 2; i++) {
                Arrays.fill(arrayBands[i], Double.NaN);
                Arrays.fill(arrayIndexes[i], -1);
                minArrayBands[i] = Double.MAX_VALUE;
                mean[i] = 0.;
            }
        }

        public void doIterationStep(int index) {
            // from LandWaterAnalyzerMode
            // land and water pixels are handled independently.
            // might not be necessary in this case
            // two bands are analysed. geo-positioning is not identical in different S2-bands!
            final int flag = flagArray[index];
            arrayBands[0][counterA] = sourceBands[0][index];
            arrayBands[1][counterA] = sourceBands[1][index];

            if (arrayBands[0][counterA] >= 1e-8 && !Double.isNaN(arrayBands[0][counterA]) &&
                    (((flag & PreparationMaskBand.LAND_FLAG) == PreparationMaskBand.LAND_FLAG) ||
                            (flag & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG)) {
                arrayIndexes[0][counterA] = index;

                if (arrayBands[0][counterA] < minArrayBands[0]) {
                    minArrayBands[0] = arrayBands[0][counterA];
                }
                counterA++;
            }
            /*else if (arrayBands[1][counterB] >= 1e-8 && !Double.isNaN(arrayBands[1][counterB]) &&
                    (flag & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG) {
                arrayIndexes[1][counterB] = index;

                if (arrayBands[1][counterB] < minArrayBands[1]) {
                    minArrayBands[1] = arrayBands[1][counterB];
                }
                counterB++;
            }*/
        }

        public double[] calculateMean() {
            for (int i = 0; i < counterA; i++) {
                mean[0] +=arrayBands[0][i];
                mean[1] +=arrayBands[1][i];
            }

            for (int j = 0; j < arrayBands.length; j++) {
                mean[j] /= counterA;
            }
            return mean;
        }

    }

}
