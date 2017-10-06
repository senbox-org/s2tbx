package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Grit Kirches
 * @author Tonio Fincke
 * @author Michael Paperin
 */
class CloudShadowIDFlagger {

    private int[] flagArray;

    static int clusterCount = S2IdepixCloudShadowOp.clusterCountDefine;
    static final int maxIterCount = 30;

    void flagCloudShadowAreas(float[][] sourceBands, int[] flagArray, Collection<List<Integer>> potentialShadowPositions,
                              Mode mode) {

        this.flagArray = flagArray;

        AnalyzerMode analyzerMode = new AnalyzerModeFactory().getAnalyzerMode(mode, sourceBands);

        for (List<Integer> positions : potentialShadowPositions) {
            analyzerMode.initArrays(positions.size());
            for (int index : positions) {
                analyzerMode.doIterationStep(index);
            }
            analyzerMode.doCloudShadowAnalysis(clusterCount * 2 + 1);
        }
    }

    private void analyseCloudShadows(int counter, int minNumberMemberCluster, double[][] arrayBands, int[] arrayIndexes,
                                     double[] minArrayBands) {
        if (counter > minNumberMemberCluster) {
            analysePotentialCloudShadowArea(counter, arrayBands, arrayIndexes);
        } else if (counter > 0) {
            analyseSmallCloudShadows(arrayBands, minArrayBands, counter, arrayIndexes);
        }
    }

    private void analyseSmallCloudShadows(double[][] arrayBands, double[] minArrayBands, int counter,
                                          int[] arrayIndexes) {
        for (int i = 0; i < counter; i++) {
            int index = arrayIndexes[i];
            if (!((flagArray[index] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)) {
                boolean flag = true;
                for (int j = 0; j < arrayBands.length; j++) {
                    if (Math.abs(arrayBands[j][i] - minArrayBands[j]) < 1e-8) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    flagArray[index] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                    break;
                }
            }
        }
    }

    private void analysePotentialCloudShadowArea(int counter, double[][] arrayBands, int[] arrayIndexes) {
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

        // add 0.5% of darkest values to shadow array but at least one pixel is added
        int addedDarkValues = 1 + (int) Math.floor(0.05 * counterWhiteness + 0.5);

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
                int flagIndex = arrayIndexes[gg];
                if (!((flagArray[flagIndex] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)) {
                    flagArray[flagIndex] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                }
            }
        }
    }

    interface AnalyzerMode {

        void initArrays(int size);

        void doIterationStep(int index);

        void doCloudShadowAnalysis(int minNumberMemberCluster);

    }

    private class LandWaterAnalyzerMode implements AnalyzerMode {

        private final float[][] sourceBands;
        int counterA;
        int counterB;
        private double[][] arrayBands;
        private int[][] arrayIndexes;
        private double[] minArrayBands;

        LandWaterAnalyzerMode(float[][] sourceBands) {
            if (sourceBands.length != 2) {
                throw new IllegalArgumentException("Two bands required for land water analysis mode");
            }
            this.sourceBands = sourceBands;
        }

        @Override
        public void initArrays(int size) {
            arrayBands = new double[2][size];
            arrayIndexes = new int[2][size];
            minArrayBands = new double[2];
            counterA = 0;
            counterB = 0;
            for (int i = 0; i < 2; i++) {
                Arrays.fill(arrayBands[i], Double.NaN);
                Arrays.fill(arrayIndexes[i], -1);
                minArrayBands[i] = Double.MAX_VALUE;
            }
        }

        @Override
        public void doIterationStep(int index) {
            final int flag = flagArray[index];
            arrayBands[0][counterA] = sourceBands[0][index];
            arrayBands[1][counterB] = sourceBands[1][index];

            if (arrayBands[0][counterA] >= 1e-8 && !Double.isNaN(arrayBands[0][counterA]) &&
                    (flag & PreparationMaskBand.LAND_FLAG) == PreparationMaskBand.LAND_FLAG) {
                arrayIndexes[0][counterA] = index;

                if (arrayBands[0][counterA] < minArrayBands[0]) {
                    minArrayBands[0] = arrayBands[0][counterA];
                }
                counterA++;
            } else if (arrayBands[1][counterB] >= 1e-8 && !Double.isNaN(arrayBands[1][counterB]) &&
                    (flag & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG) {
                arrayIndexes[1][counterB] = index;

                if (arrayBands[1][counterB] < minArrayBands[1]) {
                    minArrayBands[1] = arrayBands[1][counterB];
                }
                counterB++;
            }
        }

        @Override
        public void doCloudShadowAnalysis(int minNumberMemberCluster) {
            analyseCloudShadows(counterA, minNumberMemberCluster, new double[][]{arrayBands[0]}, arrayIndexes[0],
                                new double[]{minArrayBands[0]});
            analyseCloudShadows(counterB, minNumberMemberCluster, new double[][]{arrayBands[1]}, arrayIndexes[1],
                                new double[]{minArrayBands[1]});
        }

    }

    private class MultiBandAnalyzerMode implements AnalyzerMode {

        private final float[][] sourceBands;
        int counter;
        private double[] arrayBandA;
        private double[] arrayBandB;
        private int[] arrayIndexes;
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
            arrayIndexes = new int[size];
            counter = 0;
            Arrays.fill(arrayBandA, Double.NaN);
            Arrays.fill(arrayBandB, Double.NaN);
            Arrays.fill(arrayIndexes, -1);
            minArrayBandA = Double.MAX_VALUE;
            minArrayBandB = Double.MAX_VALUE;
            minArrayBandAB = Double.MAX_VALUE;
        }

        @Override
        public void doIterationStep(int index) {
            arrayBandA[counter] = sourceBands[0][index];
            arrayBandB[counter] = sourceBands[1][index];

            if (arrayBandA[counter] < -0.99 || arrayBandB[counter] < -0.99) {
                arrayBandA[counter] = 1.0; //Double.NaN;
                arrayBandB[counter] = 1.0; //Double.NaN;
            }
            arrayIndexes[counter] = index;
            if ((Math.pow(arrayBandA[counter], 2) + Math.pow(arrayBandB[counter], 2)) < minArrayBandAB) {
                minArrayBandAB = Math.pow(arrayBandA[counter], 2) + Math.pow(arrayBandB[counter], 2);
                minArrayBandA = arrayBandA[counter];
                minArrayBandB = arrayBandB[counter];
            }
            counter++;
        }

        @Override
        public void doCloudShadowAnalysis(int minNumberMemberCluster) {
            analyseCloudShadows(counter, minNumberMemberCluster, new double[][]{arrayBandA, arrayBandB},arrayIndexes,
                                new double[]{minArrayBandA, minArrayBandB});
        }

    }

    private class SingleBandAnalyzerMode implements AnalyzerMode {

        int counter;
        private final float[] sourceBand;
        private double[] arrayBandA;
        private int[] arrayIndexes;
        private double minArrayBandA;

        SingleBandAnalyzerMode(float[][] sourceBands) {
            this.sourceBand = sourceBands[0];
        }

        @Override
        public void initArrays(int size) {
            arrayBandA = new double[size];
            arrayIndexes = new int[size];
            counter = 0;
            Arrays.fill(arrayBandA, Double.NaN);
            Arrays.fill(arrayIndexes, -1);
            minArrayBandA = Double.MAX_VALUE;
        }

        @Override
        public void doIterationStep(int index) {
            arrayBandA[counter] = sourceBand[index];

            if (arrayBandA[counter] < -0.99) arrayBandA[counter] = 1.0; //Double.NaN;

            arrayIndexes[counter] = index;

            if (arrayBandA[counter] < minArrayBandA) {
                minArrayBandA = arrayBandA[counter];
            }
            counter++;
        }

        @Override
        public void doCloudShadowAnalysis(int minNumberMemberCluster) {
            analyseCloudShadows(counter, minNumberMemberCluster, new double[][]{arrayBandA}, arrayIndexes,
                                new double[]{minArrayBandA});
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

}
