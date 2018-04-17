package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;

public class ShiftingCloudBULKalongCloudPath {

    private static final double MAXCLOUD_TOP = S2IdepixPreCloudShadowOp.maxcloudTop;
    private static final double MINCLOUD_BASE = S2IdepixPreCloudShadowOp.mincloudBase;

    private double sumValue;
    private int N;

    private double[] meanValuesPath;

    //static Collection<List<Integer>>
    public void ShiftingCloudBULKalongCloudPath(Rectangle sourceRectangle,
                                                Rectangle targetRectangle, float sourceSunZenith,
                                                float sourceSunAzimuth,
                                                float[][] sourceBands,
                                                int[] flagArray,  Point2D[] cloudPath) {
        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;

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
        /*System.out.print("cloudPath.length ");
        System.out.print(cloudPath.length);
        System.out.println();*/


        meanValuesPath = new double[cloudPath.length];
        int[] NPath = new int[cloudPath.length];

        sumValue = 0;
        N = 0;

        for (int path_i = 1; path_i < cloudPath.length; path_i++) {
            //for (int path_i = 1; path_i < 10; path_i++) {
            //for (int path_i = 10; path_i < 11; path_i++) {
            //collect index per cloudID and cloudpath step.
            // - setup index of water or land pixels at cloud path step. just like identifyPotentialCloudShadow, but without cloudPath iteration. This is fixed to the path_i step.

            //Map<Integer, List<Integer>> indexShiftedMask = new HashMap<>();

            for(int x0 = xOffset; x0 < sourceWidth ; x0++){
                for ( int y0 = yOffset; y0< sourceHeight; y0++){
                    //each pixel needs to be tested, whether it is cloud or not.
                    //based on identifyPotentialCloudShadow()

                    simpleShiftedCloudMask_and_meanRefl_alongPath(x0, y0, sourceHeight, sourceWidth, cloudPath, path_i, flagArray, sourceBands[0]);

                }
            }

            meanValuesPath[path_i]= sumValue/N;
            NPath[path_i]=N;
        }

        /*
        Find darkest value in meanValuesPath, and shift the cloud mask along the cloud path for the respective offset.
         */
        double darkValue = 1.;
        int darkIndex = 0;
        for (int i = 1; i<cloudPath.length; i++){
            //System.out.println(meanValuesPath[i]);
            if (meanValuesPath[i] < darkValue){
                darkValue = meanValuesPath[i];
                darkIndex = i;
            }
        }

        /*System.out.print(' ');
        System.out.println(darkIndex);*/

        //set the cloud shadow flag for each ID according to the offset determined in bestPosition.
        // iterate through all positions. for each cloud pixel set the accordingly shifted pixel along the cloud path to cloud shadow.
        // not a good idea! needs the information of all.
        /*
        for(int x0 = xOffset; x0 < sourceWidth ; x0++){
            for ( int y0 = yOffset; y0< sourceHeight; y0++){

                setShiftedCloudBULK(x0, y0, sourceHeight, sourceWidth, cloudPath, flagArray, darkIndex);

            }
        }*/



    }

    public void setTileShiftedCloudBULK(Rectangle sourceRectangle,
                                        Rectangle targetRectangle,
                                        float sourceSunAzimuth,
                                        int[] flagArray,  Point2D[] cloudPath, int darkIndex){
        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;

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
        for(int x0 = xOffset; x0 < sourceWidth ; x0++){
            for ( int y0 = yOffset; y0< sourceHeight; y0++){

                setShiftedCloudBULK(x0, y0, sourceHeight, sourceWidth, cloudPath, flagArray, darkIndex);
                setPotentialCloudShadowMask(x0, y0, sourceHeight, sourceWidth, cloudPath, flagArray);

            }
        }
    }


    private void simpleShiftedCloudMask_and_meanRefl_alongPath(int x0, int y0, int height, int width, Point2D[] cloudPath, int end_path_i,
                                                               int[] flagArray, float[] sourceBand) {
        int index0 = y0 * width + x0;
        //start from a cloud pixel, otherwise stop.
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }

        //double meanRefl = 0.;
        //int N = 0;


        for (int i=end_path_i; i<end_path_i+1; i++){


            int x1 = x0 + (int) cloudPath[i].getX();
            int y1 = y0 + (int) cloudPath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;

            if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {

                if(!((flagArray[index1] & PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG) == PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG)) {
                    flagArray[index1] += PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG;
                    this.sumValue += sourceBand[index1];
                    this.N +=1;
                }
            }
        }
    }

    private static void setShiftedCloudBULK(int x0, int y0, int height, int width, Point2D[] cloudPath,
                                            int[] flagArray, int darkIndex) {
        int index0 = y0 * width + x0;
        //start from a cloud pixel, otherwise stop.
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }


        int x1 = x0 + (int) cloudPath[darkIndex].getX();
        int y1 = y0 + (int) cloudPath[darkIndex].getY();
        if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
            //break; only necessary in the for-loop, which is no longer used.
            return;
        }
        int index1 = y1 * width + x1;

        if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {


            if(!((flagArray[index1] & PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG) == PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG)) {
                flagArray[index1] += PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG;
            }
        }
    }

    private static void setPotentialCloudShadowMask(int x0, int y0, int height, int width, Point2D[] cloudPath,
                                                    int[] flagArray){
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


        for (int i=1; i<cloudPath.length; i++){

            x1 = x0 + (int) cloudPath[i].getX();
            y1 = y0 + (int) cloudPath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }


            int index1 = y1 * width + x1;

            if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {

                if(!((flagArray[index1] & PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG) == PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG)) {
                    flagArray[index1] += PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG;
                }
            }
        }
    }

    public double[] getMeanReflectanceAlongPath() {
        return meanValuesPath;
    }

    /*public int getTileid() {
        return tileid;
    }*/


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
