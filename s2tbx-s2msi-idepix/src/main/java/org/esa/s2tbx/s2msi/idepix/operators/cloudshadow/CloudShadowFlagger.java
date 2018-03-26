package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Grit Kirches
 * @author Tonio Fincke
 * @author Michael Paperin
 */
class CloudShadowFlagger {

    private int[] flagArray;

    final static int CLUSTER_COUNT = S2IdepixCloudShadowOp.clusterCountDefine;

    void flagCloudShadowAreas(float[][] sourceBands, int[] flagArray, Collection<List<Integer>> potentialShadowPositions,
                              Mode mode) {

        this.flagArray = flagArray;

        AnalyzerMode analyzerMode = new AnalyzerModeFactory().getAnalyzerMode(mode, sourceBands);


        for (List<Integer> positions : potentialShadowPositions) {
            analyzerMode.initArrays(positions.size());
            for (int index : positions) {
                analyzerMode.doIterationStep(index);
            }
            analyzerMode.doCloudShadowAnalysis(CLUSTER_COUNT * 2 + 1);
        }
    }

    private static float[] nonCloudMeans(float[][] clusterData, int[] flagArray, boolean onWater, boolean onLand) {
        float[] means = new float[clusterData.length];
        int validCounter = 0;
        for (int i = 0; i < clusterData[0].length; i++) {
            boolean valid = !((flagArray[i] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG);
            if (!onWater) {
                valid =  valid && !((flagArray[i] & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG);
            }
            if (!onLand) {
                valid = valid && !((flagArray[i] & PreparationMaskBand.LAND_FLAG) == PreparationMaskBand.LAND_FLAG);
            }
            if (valid) {
                for (int j = 0; j < clusterData.length; j++) {
                    means[j] += clusterData[j][i];
                }
                validCounter++;
            }
        }
        for (int j = 0; j < clusterData.length; j++) {
            means[j] /= validCounter;
        }
        return means;
    }

    private float[] getThresholds(float[][] clusterData, boolean onWater, boolean onLand) {
        float[] means = new float[clusterData.length];
        int validCounter = 0;
        for (int i = 0; i < clusterData[0].length; i++) {
            boolean valid = !((flagArray[i] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG);
            if (!onWater) {
                valid =  valid && !((flagArray[i] & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG);
            }
            if (!onLand) {
                valid = valid && !((flagArray[i] & PreparationMaskBand.LAND_FLAG) == PreparationMaskBand.LAND_FLAG);
            }
            if (valid) {
                for (int j = 0; j < clusterData.length; j++) {
                    means[j] += clusterData[j][i];
                }
                validCounter++;
            }
        }
        for (int j = 0; j < clusterData.length; j++) {
            means[j] /= validCounter;
        }
        float[] sigmas = new float[clusterData.length];
        for (int i = 0; i < clusterData[0].length; i++) {
            boolean valid = !((flagArray[i] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG);
            if (!onWater) {
                valid =  valid && !((flagArray[i] & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG);
            }
            if (!onLand) {
                valid = valid && !((flagArray[i] & PreparationMaskBand.LAND_FLAG) == PreparationMaskBand.LAND_FLAG);
            }
            if (valid) {
                for (int j = 0; j < clusterData.length; j++) {
                    sigmas[j] += Math.pow(means[j] - clusterData[j][i], 2);
                }
            }
        }
        float[] thresholds = new float[clusterData.length];
        for (int j = 0; j < clusterData.length; j++) {
            sigmas[j] /= validCounter;
            sigmas[j] = (float) Math.sqrt(sigmas[j]);
            thresholds[j] = means[j] - sigmas[j];
        }
        return thresholds;
    }

    private void analyseCloudShadows(int counter, int minNumberMemberCluster, double[][] arrayBands, int[] arrayIndexes,
                                     double[] minArrayBands, float[] thresholds, double mean) {
        if (counter > minNumberMemberCluster) {
//            analysePotentialCloudShadowArea_sigma(counter, arrayBands, arrayIndexes, thresholds);
//            analysePotentialCloudShadowArea_percentiles(counter, arrayBands, arrayIndexes, mean);
            analysePotentialCloudShadowArea_clustering(counter, arrayBands, arrayIndexes);
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

    private double getDarkestClusterThreshold(double[] sortedBand, double mean) {
        int numSteps = 32;
        int startIndex = (int) ((sortedBand.length - 1) * 0.05);
        int finalIndex = (int) ((sortedBand.length - 1) * 0.95);
        int endIndex = finalIndex;

        double ratio = (sortedBand[endIndex] - sortedBand[startIndex]) / sortedBand[endIndex];
        while (ratio > 0.4) {
            while (endIndex - startIndex > 2 && numSteps > 1) {
                while (endIndex - startIndex < numSteps * 3) {
                    numSteps /= 2;
                }
                if (numSteps == 1) {
                    break;
                }
                double[] diffs = new double[numSteps - 1];
                double stepper = (endIndex - startIndex) / numSteps;
                int maxDiffIndex = 0;
                double maxDiff = Double.NEGATIVE_INFINITY;
                final StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < numSteps - 2; i++) {
                    final int currentIndex = startIndex + (int) ((i + 1) * stepper);
                    diffs[i] = sortedBand[currentIndex] -
                            sortedBand[startIndex + (int) (i * stepper)];
                    stringBuilder.append(diffs[i]).append(", ");
                    if (maxDiff < diffs[i]) {
                        maxDiff = diffs[i];
                        maxDiffIndex = i;
                    }
                }
                System.out.println(stringBuilder.toString());
                endIndex = startIndex + (int) ((maxDiffIndex + 1) * stepper);
                startIndex = startIndex + (int) ((maxDiffIndex) * stepper);
            }
            ratio = (sortedBand[finalIndex] - sortedBand[endIndex]) / sortedBand[finalIndex];
            if (ratio > 0.4 && sortedBand[endIndex] < mean) {
                startIndex = endIndex;
                endIndex = finalIndex;
                numSteps = 32;
            }
        }
        final double threshold = sortedBand[startIndex] + (sortedBand[endIndex] - sortedBand[startIndex]) / 2;
//        System.out.println("threshold = " + threshold);
        return threshold;
    }

    private void analysePotentialCloudShadowArea_percentiles(int counter, double[][] arrayBands, int[] arrayIndexes,
                                                             double mean) {
        double[] band = new double[counter];
        for (int i = 0; i < counter; i++) {
            for (double[] arrayBand : arrayBands) {
                band[i] += Math.pow(arrayBand[i], Math.min(2, arrayBands.length));
            }
        }
        final double[] sortedBand = band.clone();
        Arrays.sort(sortedBand);
        double threshold = getDarkestClusterThreshold(sortedBand, mean);
        for (int j = 0; j < counter; j++) {
            if (band[j] < threshold) {
                int flagIndex = arrayIndexes[j];
                if (!((flagArray[flagIndex] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)) {
                    flagArray[flagIndex] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                }
            }
        }
    }

    private void analysePotentialCloudShadowArea_clustering(int counter, double[][] arrayBands, int[] arrayIndexes) {
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

        final List<Double>[] clusterableLists = new List[arrayBands.length];
        for (int i = 0; i < clusterableLists.length; i++) {
            clusterableLists[i] = new ArrayList<>();
        }
        for (int i = 0; i < band.length; i++) {
            if (band[i] < thresholdWhiteness) {
                for (int j = 0; j < clusterableLists.length; j++) {
                    clusterableLists[j].add(arrayBands[j][i]);
                }
            }
        }

        // add 0.5% of darkest values to shadow array but at least one pixel is added
        int addedDarkValues = 1 + (int) Math.floor(0.05 * counterWhiteness + 0.5);

        double[][] arrayClusterableBands = new double[clusterableLists.length][clusterableLists[0].size() + addedDarkValues];
        for (int i = 0; i < arrayClusterableBands.length; i++) {
            Arrays.fill(arrayClusterableBands[i], darkestBands[i]);
        }
        for (int i = 0; i < clusterableLists.length; i++) {
            for (int j = 0; j < clusterableLists[0].size(); j++) {
                arrayClusterableBands[i][j] = clusterableLists[i].get(j);
            }
        }

        final int numberOfClusters = getRecommendedNumberOfClusters(arrayClusterableBands[0]);

        double[][] clusterCentroidArray = ClusteringKMeans.computedKMeansCluster(numberOfClusters, arrayClusterableBands);

        final ArrayList<Double> sortedCluster = new ArrayList<>();
        for (int i = 0; i < numberOfClusters; i++) {
            double clusterCentroid = 0;
            for (double clusterCentroidArr : clusterCentroidArray[i]) {
                clusterCentroid += Math.pow(clusterCentroidArr, Math.min(2, arrayBands.length));
            }
            int j;
            for (j = 0; j < i; j++) {
                if (clusterCentroid < sortedCluster.get(j)) {
                    break;
                }
            }
            sortedCluster.add(j, clusterCentroid);
        }

        double maxDist = sortedCluster.get(sortedCluster.size() - 1) - sortedCluster.get(0);
//        double relativeVariation = maxDist / sortedCluster.get(sortedCluster.size() - 1);
//        if (relativeVariation < 0.4) {
//             too little variation <- probably no cloud here
//            return;
//        }


        double averageDistance = maxDist / (numberOfClusters - 1);
        for (int i = 0; i < sortedCluster.size() - 2; i++) {
            if (sortedCluster.get(i + 1) - sortedCluster.get(i) > averageDistance) {
                break;
            }
        }
//        double threshold = sortedCluster.get(i) + (sortedCluster.get(i + 1) - sortedCluster.get(i)) / 2;
        double threshold = sortedCluster.get(0) + (sortedCluster.get(1) - sortedCluster.get(0)) / 2;
        for (int j = 0; j < counter; j++) {
            if (band[j] < threshold) {
                int flagIndex = arrayIndexes[j];
                if (!((flagArray[flagIndex] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)) {
                    flagArray[flagIndex] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                }
            }
        }
    }

    private void analysePotentialCloudShadowArea_sigma(int counter, double[][] arrayBands, int[] arrayIndexes,
                                                       float[] threshholds) {
        for (int j = 0; j < counter; j++) {
            boolean valid = true;
            for (int i = 0; i < arrayBands.length; i++) {
                if (arrayBands[i][j] > threshholds[i]) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                int flagIndex = arrayIndexes[j];
                if (!((flagArray[flagIndex] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)) {
                    flagArray[flagIndex] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                }
            }
        }
    }

    private static int getRecommendedNumberOfClusters(double[] values) {
        //return Math.max(4, (int) Math.log(values.length) * 2);
        return 4;
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
        private final float[] landThreshholds;
        private final float[] waterThreshholds;
        private final double landMean;
        private final double waterMean;

        LandWaterAnalyzerMode(float[][] sourceBands) {
            if (sourceBands.length != 2) {
                throw new IllegalArgumentException("Two bands required for land water analysis mode");
            }
            this.sourceBands = sourceBands;
            landThreshholds = getThresholds(sourceBands, false, true);
            waterThreshholds = getThresholds(sourceBands, true, false);
            landMean = nonCloudMeans(sourceBands, flagArray, false, true)[0];
            waterMean = nonCloudMeans(sourceBands, flagArray, true, false)[1];
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
                                new double[]{minArrayBands[0]}, landThreshholds, landMean);
            analyseCloudShadows(counterB, minNumberMemberCluster, new double[][]{arrayBands[1]}, arrayIndexes[1],
                                new double[]{minArrayBands[1]}, waterThreshholds, waterMean);
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
        private final float[] thresholds;
        private double mean;

        MultiBandAnalyzerMode(float[][] sourceBands) {
            this.sourceBands = sourceBands;
            thresholds = getThresholds(sourceBands, true, true);
            final float[] means = nonCloudMeans(sourceBands, flagArray, true, true);
            for (float mean1 : means) {
                mean += Math.pow(mean1, 2);
            }
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
            analyseCloudShadows(counter, minNumberMemberCluster, new double[][]{arrayBandA, arrayBandB}, arrayIndexes,
                                new double[]{minArrayBandA, minArrayBandB}, thresholds, mean);
        }

    }

    private class SingleBandAnalyzerMode implements AnalyzerMode {

        int counter;
        private final float[] sourceBand;
        private double[] arrayBandA;
        private int[] arrayIndexes;
        private double minArrayBandA;
        private final float[] thresholds;
        private final double mean;

        SingleBandAnalyzerMode(float[][] sourceBands) {
            this.sourceBand = sourceBands[0];
            thresholds = getThresholds(sourceBands, true, true);
            mean = nonCloudMeans(sourceBands, flagArray, true, true)[0];
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
                                new double[]{minArrayBandA}, thresholds, mean);
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
