package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.datamodel.Product;

import java.awt.Rectangle;
import java.util.Arrays;

/**
 * @author Grit Kirches
 * @author Tonio Fincke
 * @author Michael Paperin
 */
class CloudShadowIDAnalyzer {

    private int[] flagArray;
    private int sourceWidth;
    private int[] cloudShadowIDArray;
    private int[] cloudLongShadowIDArray;
    private int[][] cloudShadowIdBorderRectangle;
    private int sourceHeight;
    private int arraySize;

    static int clusterCount = S2IdepixCloudShadowOp.clusterCountDefine;
    static final int maxIterCount = 30;

    void identifyCloudShadowAreas(Product sourceProduct, Rectangle sourceRectangle, float[][] sourceBands,
                                  int[] flagArray, int[] cloudShadowIDArray, int[] cloudLongShadowIDArray,
                                  int[][] cloudShadowIdBorderRectangle, int cloudIndexTable, Mode mode) {

        this.flagArray = flagArray;
        this.cloudShadowIDArray = cloudShadowIDArray;
        this.cloudLongShadowIDArray = cloudLongShadowIDArray;
        this.cloudShadowIdBorderRectangle = cloudShadowIdBorderRectangle;

        AnalyzerMode analyzerMode = new AnalyzerModeFactory().getAnalyzerMode(mode, sourceBands);

        sourceWidth = sourceRectangle.width;
        sourceHeight = sourceRectangle.height;

        final int productWidth = sourceProduct.getSceneRasterWidth();
        final int productHeight = sourceProduct.getSceneRasterHeight();

        int maxRectangleWidth = -1;
        int maxRectangleHeight = -1;

        for (int cloudIndex = 0; cloudIndex < cloudIndexTable; cloudIndex++) {
            if ((cloudShadowIdBorderRectangle[cloudIndex][0] != productWidth + 1) || (cloudShadowIdBorderRectangle[cloudIndex][1] != -1) ||
                    (cloudShadowIdBorderRectangle[cloudIndex][2] != productHeight + 1) || (cloudShadowIdBorderRectangle[cloudIndex][3] != -1)) {
                maxRectangleWidth = Math.max(maxRectangleWidth, cloudShadowIdBorderRectangle[cloudIndex][1] - cloudShadowIdBorderRectangle[cloudIndex][0]);
                maxRectangleHeight = Math.max(maxRectangleHeight, cloudShadowIdBorderRectangle[cloudIndex][3] - cloudShadowIdBorderRectangle[cloudIndex][2]);
            }
        }

        arraySize = (maxRectangleWidth + 1) * (maxRectangleHeight + 1);
        analyzerMode.initArrays(arraySize);

        for (int cloudIndex = SegmentationCloud.NO_SHADOW + 1; cloudIndex < cloudIndexTable; cloudIndex++) {
            analyzerMode.resetCounters();
            if ((cloudShadowIdBorderRectangle[cloudIndex][0] != productWidth + 1) ||
                    (cloudShadowIdBorderRectangle[cloudIndex][1] != -1) ||
                    (cloudShadowIdBorderRectangle[cloudIndex][2] != productHeight + 1) ||
                    (cloudShadowIdBorderRectangle[cloudIndex][3] != -1)) {
                analyzerMode.resetArrays();
                for (int j = cloudShadowIdBorderRectangle[cloudIndex][2]; j <= cloudShadowIdBorderRectangle[cloudIndex][3]; j++) {
                    for (int i = cloudShadowIdBorderRectangle[cloudIndex][0]; i <= cloudShadowIdBorderRectangle[cloudIndex][1]; i++) {
                        int index = j * (sourceWidth) + i;
                        if (cloudShadowIDArray[index] == cloudIndex) {
                            analyzerMode.doIterationStep(index, i, j);
                        }
                    }
                }
                analyzerMode.doCloudShadowAnalysis(clusterCount * 2 + 1, cloudIndex);
            }
        }
    }

    private void analyseCloudShadows(float[][] sourceBands, int counter, int minNumberMemberCluster,
                                     double[][] arrayBands, int[] arrayXPos, int[] arrayYPos, int cloudIndex,
                                     double[] minArrayBands, PixelValidator pixelValidator) {
        // minimum number of potential shadow points for the cluster analysis per cluster
        if (counter > minNumberMemberCluster && counter < S2IdepixCloudShadowOp.CloudShadowFragmentationThreshold) {
            analysePotentialCloudShadowArea(counter, arrayBands, arrayXPos, arrayYPos);
        } else if (counter >= S2IdepixCloudShadowOp.CloudShadowFragmentationThreshold) {
            analyseLongCloudShadows(cloudIndex, sourceBands, minNumberMemberCluster, pixelValidator);
        } else {
            analyseSmallCloudShadows(arrayBands, minArrayBands, counter, arrayXPos, arrayYPos);
        }
    }

    private void analyseSmallCloudShadows(double[][] arrayBands, double[] minArrayBands, int counter,
                                          int[] arrayXPos, int[] arrayYPos) {
        int index;
        for (int i = 0; i < counter; i++) {
            index = arrayYPos[i] * sourceWidth + arrayXPos[i];
            if (!((flagArray[index] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)) {
                for (int j = 0; j < arrayBands.length; j++) {
                    if (arrayBands[j][i] <= minArrayBands[j]) {
                        break;
                    }
                }
                flagArray[index] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
            }
        }
    }

    private void analyseLongCloudShadows(int cloudIndex, float[][] sourceBands, int minNumberMemberCluster,
                                         PixelValidator pixelValidator) {
        final int sourceLength = sourceWidth * sourceHeight;
        int[] dummyLongCloudShadowIDArray = new int[sourceLength];
        //will be filled in SegmentationLongCloudClass Arrays.fill(cloudIdArray, ....);
        Arrays.fill(dummyLongCloudShadowIDArray, sourceLength);
        //bc cloud shadow cloudIndexTable = max of ID
        int counterTableLongShadow = SegmentationLongCloudShadow.computeLongCloudShadowID(sourceWidth, sourceHeight,
                                                                                          cloudShadowIDArray,
                                                                                          cloudShadowIdBorderRectangle,
                                                                                          dummyLongCloudShadowIDArray,
                                                                                          cloudIndex);
        double[][] arrayBands = new double[sourceBands.length][arraySize];
        int[] arrayXPos = new int[arraySize];
        int[] arrayYPos = new int[arraySize];
        double[] minArrayBands = new double[arrayBands.length];
        for (int longCloudIndex = SegmentationLongCloudShadow.NO_SHADOW + 1; longCloudIndex < counterTableLongShadow; longCloudIndex++) {
            int counter = 0;
            for (int j = 0; j < arrayBands.length; j++) {
                Arrays.fill(arrayBands[j], Double.NaN);
                minArrayBands[j] = Double.MAX_VALUE;
            }
            Arrays.fill(arrayXPos, -1);
            Arrays.fill(arrayYPos, -1);
            double minArrayBand = Double.MAX_VALUE;
            for (int j = cloudShadowIdBorderRectangle[cloudIndex][2]; j <= cloudShadowIdBorderRectangle[cloudIndex][3]; j++) {
                for (int i = cloudShadowIdBorderRectangle[cloudIndex][0]; i <= cloudShadowIdBorderRectangle[cloudIndex][1]; i++) {
                    final int index = j * (sourceWidth) + i;
                    if (dummyLongCloudShadowIDArray[index] == longCloudIndex && pixelValidator.isPixelValid(index)) {
                        double temp = 0;
                        for (int k = 0; k < arrayBands.length; k++) {
                            arrayBands[k][counter] = sourceBands[k][index];
                            temp += Math.pow(arrayBands[k][counter], Math.min(2, arrayBands.length));
                        }
                        if (temp < minArrayBand) {
                            minArrayBand = temp;
                            for (int k = 0; k < minArrayBands.length; k++) {
                                minArrayBands[k] = arrayBands[k][counter];
                            }
                        }
                        arrayXPos[counter] = i;
                        arrayYPos[counter] = j;
                        counter++;
                    }
                }
            }

            if (counter > minNumberMemberCluster) { // minimum number of potential shadow points for the cluster analysis per cluster
                analysePotentialCloudShadowArea(counter, arrayBands, arrayXPos, arrayYPos);
            } else if (counter > 0) {
                analyseSmallCloudShadows(arrayBands, minArrayBands, counter, arrayXPos, arrayYPos);
            }
            for (int z = 0; z < sourceLength; z++) {
                cloudLongShadowIDArray[z] += 1000 * cloudIndex + dummyLongCloudShadowIDArray[z];
            }
        }
    }

    private void analysePotentialCloudShadowArea(int counter, double[][] arrayBands, int[] arrayXPos, int[] arrayYPos) {
        double[] band = new double[counter];
        double darkestBand = Double.MAX_VALUE;
        double[] darkestBands = new double[arrayBands.length];
        for (int i = 0; i < counter; i++) {
            for (double[] arrayBand : arrayBands) {
                band[i] += Math.pow(arrayBand[i], Math.min(2, arrayBands.length));
            }
            if (band[i] < darkestBand) {
                darkestBand = band[i];
                for (int j = 0; j < arrayBands.length; j++) {
                    darkestBands[j] = arrayBands[j][i];
                }
            }
        }
        int counterWhiteness = (int) (Math.floor(counter * S2IdepixCloudShadowOp.OUTLIER_THRESHOLD));
        if (counterWhiteness >= counter) counterWhiteness = counter - 1;
        double[] sortedBand = band.clone();
        Arrays.sort(sortedBand);
        double thresholdWhiteness = sortedBand[counterWhiteness];

        //todo 0.5% or 2.5% ?
        // add 2.5% of darkest values to shadow array but at least one pixel is added
        int addedDarkValues = 1 + (int) Math.floor(0.025 * counterWhiteness + 0.5);

        double[][] arrayClusterableBands = new double[arrayBands.length][counterWhiteness + addedDarkValues];
        for (int i = 0; i < arrayClusterableBands.length; i++) {
            Arrays.fill(arrayClusterableBands[i], darkestBands[i]);
        }
        int countIntern = 0;
        int index = 0;
        while (index < counter && countIntern < counterWhiteness) {
            if (band[index] < thresholdWhiteness) {
                for (int j = 0; j < arrayClusterableBands.length; j++) {
                    arrayClusterableBands[j][countIntern] = arrayBands[j][index];
                }
                countIntern++;
            }
            index++;
        }

        double[][] clusterCentroidArray = ClusteringKMeans.computedKMeansCluster(arrayClusterableBands);

        double darkness = Double.MAX_VALUE;
        int darkestClusterNumber = -1;
        double whiteness = Double.MIN_VALUE;

        // search for a darkest cluster
        for (int i = 0; i < clusterCount; i++) {
            double candidate = 0;
            for (int j = 0; j < arrayBands.length; j++) {
                candidate += Math.pow(clusterCentroidArray[i][j], 2);
            }
            candidate = Math.sqrt(candidate);
            if (candidate < darkness) {
                darkness = candidate;
                darkestClusterNumber = i;
            }
            if (candidate > whiteness) {
                whiteness = candidate;
            }
        }

        // distance analysis BandValue in relation to the CentroidValue
        // assign membership of BandValue to Cluster
        int containerNumber = -1;
        for (int gg = 0; gg < counter; gg++) {
            double distance = Double.MAX_VALUE;
            for (int ff = 0; ff < clusterCount; ff++) {
                double temp = 0;
                for (int i = 0; i < arrayBands.length; i++) {
                    temp += Math.pow(clusterCentroidArray[ff][i] - arrayBands[i][gg], 2);
                }
                temp = Math.sqrt(temp);
                if (temp < distance) {
                    distance = temp;
                    containerNumber = ff;
                }
            }

            if (containerNumber == darkestClusterNumber && whiteness - darkness > S2IdepixCloudShadowOp.Threshold_Whiteness_Darkness) {
                int flagIndex = (arrayYPos[gg] * sourceWidth + arrayXPos[gg]);
                if (!((flagArray[flagIndex] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)) {
                    flagArray[flagIndex] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                }
            }
        }
    }

    interface AnalyzerMode {

        void initArrays(int size);

        void resetCounters();

        void resetArrays();

        void doIterationStep(int index, int i, int j);

        void doCloudShadowAnalysis(int minNumberMemberCluster, int cloudIndex);

    }

    private class LandWaterAnalyzerMode implements AnalyzerMode {

        private final float[][] sourceBands;
        int counterA;
        int counterB;
        private double[][] arrayBands;
        private int[][] arrayXPoses;
        private int[][] arrayYPoses;
        private double[] minArrayBands;

        LandWaterAnalyzerMode(float[][] sourceBands) {
            if (sourceBands.length != 2) {
                throw new IllegalArgumentException("Two Band required for land water analysis mode");
            }
            this.sourceBands = sourceBands;
        }

        @Override
        public void initArrays(int size) {
            arrayBands = new double[2][size];
            arrayXPoses = new int[2][size];
            arrayYPoses = new int[2][size];
            minArrayBands = new double[2];
        }

        @Override
        public void resetCounters() {
            counterA = 0;
            counterB = 0;
        }

        @Override
        public void resetArrays() {
            for (int i = 0; i < 2; i++) {
                Arrays.fill(arrayBands[i], Double.NaN);
                Arrays.fill(arrayXPoses[i], -1);
                Arrays.fill(arrayYPoses[i], -1);
                minArrayBands[i] = Double.MAX_VALUE;
            }
        }

        @Override
        public void doIterationStep(int index, int i, int j) {
            final int flag = flagArray[index];
            arrayBands[0][counterA] = sourceBands[0][index];
            arrayBands[1][counterB] = sourceBands[1][index];

            if (arrayBands[0][counterA] >= 1e-8 && !Double.isNaN(arrayBands[0][counterA]) &&
                    (flag & PreparationMaskBand.LAND_FLAG) == PreparationMaskBand.LAND_FLAG) {
                arrayXPoses[0][counterA] = i;
                arrayYPoses[0][counterA] = j;

                if (arrayBands[0][counterA] < minArrayBands[0]) {
                    minArrayBands[0] = arrayBands[0][counterA];
                }
                counterA++;
            } else if (arrayBands[1][counterB] >= 1e-8 && !Double.isNaN(arrayBands[1][counterB]) &&
                    (flag & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG) {
                arrayXPoses[1][counterB] = i;
                arrayYPoses[1][counterB] = j;

                if (arrayBands[1][counterB] < minArrayBands[1]) {
                    minArrayBands[1] = arrayBands[1][counterB];
                }
                counterB++;
            }
        }

        @Override
        public void doCloudShadowAnalysis(int minNumberMemberCluster, int cloudIndex) {
            analyseCloudShadows(new float[][]{sourceBands[0]}, counterA, minNumberMemberCluster,
                                new double[][]{arrayBands[0]}, arrayXPoses[0], arrayYPoses[0], cloudIndex,
                                new double[]{minArrayBands[0]}, new LandPixelValidator());
            analyseCloudShadows(new float[][]{sourceBands[1]}, counterB, minNumberMemberCluster,
                                new double[][]{arrayBands[1]}, arrayXPoses[1], arrayYPoses[1], cloudIndex,
                                new double[]{minArrayBands[1]}, new WaterPixelValidator());
        }

    }

    private class MultiBandAnalyzerMode implements AnalyzerMode {

        private final float[][] sourceBands;
        int counter;
        private double[] arrayBandA;
        private double[] arrayBandB;
        private int[] arrayXPos;
        private int[] arrayYPos;
        private double minArrayBandA = Double.MAX_VALUE;
        private double minArrayBandB = Double.MAX_VALUE;
        private double minArrayBandAB = Double.MAX_VALUE;

        MultiBandAnalyzerMode(float[][] sourceBands) {
            this.sourceBands = sourceBands;
        }

        @Override
        public void initArrays(int size) {
            arrayBandA = new double[size];
            arrayBandB = new double[size];
            arrayXPos = new int[size];
            arrayYPos = new int[size];
        }

        @Override
        public void resetCounters() {
            counter = 0;
        }

        @Override
        public void resetArrays() {
            Arrays.fill(arrayBandA, Double.NaN);
            Arrays.fill(arrayBandB, Double.NaN);
            Arrays.fill(arrayXPos, -1);
            Arrays.fill(arrayYPos, -1);
            minArrayBandA = Double.MAX_VALUE;
            minArrayBandB = Double.MAX_VALUE;
            minArrayBandAB = Double.MAX_VALUE;
        }

        @Override
        public void doIterationStep(int index, int i, int j) {
            arrayBandA[counter] = sourceBands[0][index];
            arrayBandB[counter] = sourceBands[1][index];

            if (arrayBandA[counter] < -0.99 || arrayBandB[counter] < -0.99) {
                arrayBandA[counter] = 1.0; //Double.NaN;
                arrayBandB[counter] = 1.0; //Double.NaN;
            }
            arrayXPos[counter] = i;
            arrayYPos[counter] = j;
            if ((Math.pow(arrayBandA[counter], 2) + Math.pow(arrayBandB[counter], 2)) < minArrayBandAB) {
                minArrayBandAB = Math.pow(arrayBandA[counter], 2) + Math.pow(arrayBandB[counter], 2);
                minArrayBandA = arrayBandA[counter];
                minArrayBandB = arrayBandB[counter];
            }
            counter++;
        }

        @Override
        public void doCloudShadowAnalysis(int minNumberMemberCluster, int cloudIndex) {
            analyseCloudShadows(sourceBands, counter, minNumberMemberCluster,
                                new double[][]{arrayBandA, arrayBandB}, arrayXPos, arrayYPos, cloudIndex,
                                new double[]{minArrayBandA, minArrayBandB}, new EmptyPixelValidator());
        }

    }

    private class SingleBandAnalyzerMode implements AnalyzerMode {

        int counter;
        private final float[] sourceBand;
        private double[] arrayBandA;
        private int[] arrayXPos;
        private int[] arrayYPos;
        private double minArrayBandA;

        SingleBandAnalyzerMode(float[][] sourceBands) {
            this.sourceBand = sourceBands[0];
        }

        @Override
        public void initArrays(int size) {
            arrayBandA = new double[size];
            arrayXPos = new int[size];
            arrayYPos = new int[size];
        }

        @Override
        public void resetCounters() {
            counter = 0;
        }

        @Override
        public void resetArrays() {
            Arrays.fill(arrayBandA, Double.NaN);
            Arrays.fill(arrayXPos, -1);
            Arrays.fill(arrayYPos, -1);
            minArrayBandA = Double.MAX_VALUE;
        }

        @Override
        public void doIterationStep(int index, int i, int j) {
            arrayBandA[counter] = sourceBand[index];

            if (arrayBandA[counter] < -0.99) arrayBandA[counter] = 1.0; //Double.NaN;

            arrayXPos[counter] = i;
            arrayYPos[counter] = j;

            if (arrayBandA[counter] < minArrayBandA) {
                minArrayBandA = arrayBandA[counter];
            }
            counter++;
        }

        @Override
        public void doCloudShadowAnalysis(int minNumberMemberCluster, int cloudIndex) {
            analyseCloudShadows(new float[][]{sourceBand}, counter, minNumberMemberCluster, new double[][]{arrayBandA},
                                arrayXPos, arrayYPos, cloudIndex, new double[]{minArrayBandA}, new EmptyPixelValidator());
        }

    }

    private class AnalyzerModeFactory {

        AnalyzerMode getAnalyzerMode(Mode mode, float[][] sourceBands) {
            switch (mode) {
                case LAND_WATER:
                    return new LandWaterAnalyzerMode(sourceBands);
                case MULTI_BAND:
                    return new MultiBandAnalyzerMode(sourceBands);
                case SINGLE_BAND:
                    return new SingleBandAnalyzerMode(sourceBands);
            }
            throw new IllegalArgumentException("Unknown analyzer mode");
        }

    }

    private interface PixelValidator {
        boolean isPixelValid(int index);
    }

    private class EmptyPixelValidator implements PixelValidator {
        @Override
        public boolean isPixelValid(int index) {
            return true;
        }
    }

    private class LandPixelValidator implements PixelValidator {
        @Override
        public boolean isPixelValid(int index) {
            return flagArray[index] == PreparationMaskBand.LAND_FLAG;
        }
    }

    private class WaterPixelValidator implements PixelValidator {
        @Override
        public boolean isPixelValid(int index) {
            return flagArray[index] == PreparationMaskBand.WATER_FLAG;
        }
    }

}
