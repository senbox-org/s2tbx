package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.util.SystemUtils;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Grit Kirches
 * @author Tonio Fincke
 * @author Michael Paperin
 * @author Dagmar Müller
 */
class CloudShadowFlaggerCombination {

    private static Logger logger = SystemUtils.LOG;

    private int[] flagArray;
    private int bestOffset;

    private int width;
    private int height;

    private double meanReflShift;
    private int cloudSize;
    private List<Integer> cloud;
    private Point2D[] cloudPath;

    // for testing, which cluster to use: the one, which mean distance is closer to the shift, which has been calculated before...
    //private int offsetCloudShift;

    private final static int CLUSTER_COUNT = S2IdepixPostCloudShadowOp.clusterCountDefine;

    void flagCloudShadowAreas(float[][] sourceBands, int[] flagArray, Map<Integer, List<Integer>> potentialShadowPositions,
                              Map<Integer, List<Integer>> offsetAtPotentialShadow, Map<Integer, List<Integer>> cloudList,
                              int bestOffset, Mode mode, int sourceWidth, int sourceHeight, int[] shadowIDArray, Point2D[] cloudPath) {

        this.flagArray = flagArray;
        this.bestOffset = bestOffset;
        this.width = sourceWidth;
        this.height = sourceHeight;
        this.cloudPath = cloudPath;

        AnalyzerMode analyzerMode = new AnalyzerModeFactory().getAnalyzerMode(mode, sourceBands);

        for (int key : potentialShadowPositions.keySet()) {
            /*
            positions and offsetAtPosition can contain duplicates!
            Removing duplicates, Keeping the smaller offset at a position...
             */
            List<Integer> positions = potentialShadowPositions.get(key);
            List<Integer> offsetAtPos = offsetAtPotentialShadow.get(key);

            List<Integer> noduplicatesPositions = new ArrayList<>(new LinkedHashSet<>(positions));

            if (noduplicatesPositions.size() < positions.size()) {
                int test[] = new int[flagArray.length];
                for (int i = 0; i < positions.size(); i++) {
                    int off = offsetAtPos.get(i);
                    int ind = positions.get(i);
                    if (ind < test.length) {
                        if (test[ind] > off || test[ind] == 0) {
                            test[ind] = off;
                        }
                    } else
                        logger.info("Index: " + ind + " outside range");
                }

                List<Integer> noduplicatesOffsets = new ArrayList<>();
                for (int i : noduplicatesPositions) {
                    noduplicatesOffsets.add(test[i]);
                }

                positions.clear();
                positions.addAll(noduplicatesPositions);
                offsetAtPos.clear();
                offsetAtPos.addAll(noduplicatesOffsets);
            }

            //caution! the cloud list has a different length!
            this.cloud = cloudList.get(key);
            this.cloudSize = cloud.size();
            this.meanReflShift = setMeanRefl(cloud, bestOffset, sourceBands[1], cloudPath);


            analyzerMode.initArrays(positions.size());
            for (int i = 0; i < positions.size(); i++) {

                int index = positions.get(i);

                int offset = offsetAtPos.get(i);
                analyzerMode.doIterationStep(index, offset);
            }
            analyzerMode.doCloudShadowAnalysis(CLUSTER_COUNT * 2 + 1, shadowIDArray, sourceBands[1]);
        }

        /*
         combining shifted and clustered cloud shadow: new flag cloud_shadow_comb
         after adjusting the shifted cloud, test against dark clusters.
          - coinciding pixels between dark cluster and shifted cloud?
          - if yes:
             - leave these clusters, switch off not-coincinding ones.
          - if no:
             - cluster is probably another dark pixel on the surface, but not a shadow.
         */

        if (bestOffset > 0) {
            int test[] = new int[flagArray.length];
            for (int i = 0; i < flagArray.length; i++) {
                if ((flagArray[i] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG) {
                    test[i] = 1;
                }
            }
            FindContinuousAreas testContinuousShadow = new FindContinuousAreas(test);
            Map<Integer, List<Integer>> clusteredShadowTileID = testContinuousShadow.computeAreaID(width, height, shadowIDArray, false);

            setCombinedCloudShadowFlagOnTile(clusteredShadowTileID);
        }

    }

    private double setMeanRefl(List<Integer> cloud, int bestOffset, float[] sourceBand, Point2D[] cloudPath) {
        int N = 0;
        double refl = 0.;

        for (int index : cloud) {
            int[] x = revertIndexToXY(index, width);

            int x1 = x[0] + (int) cloudPath[bestOffset].getX();
            int y1 = x[1] + (int) cloudPath[bestOffset].getY();

            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;

            if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {

                refl += sourceBand[index1];
                N += 1;
            }
        }

        double out = 0;
        if (N > 0) out = refl / N;

        return out;
    }

    private void switchOffShiftedCloudShadowFlag(List<Integer> cloud, int Offset, Point2D[] cloudPath) {
        for (int index : cloud) {
            int[] x = revertIndexToXY(index, width);

            int x1 = x[0] + (int) cloudPath[Offset].getX();
            int y1 = x[1] + (int) cloudPath[Offset].getY();

            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;

            if (((flagArray[index1] & PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG) == PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG)) {
                flagArray[index1] -= PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG;
            }
        }
    }

    private void setShiftedCloudShadowFlag(List<Integer> cloud, int Offset, Point2D[] cloudPath) {
        for (int index : cloud) {
            int[] x = revertIndexToXY(index, width);

            int x1 = x[0] + (int) cloudPath[Offset].getX();
            int y1 = x[1] + (int) cloudPath[Offset].getY();

            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;

            if (!((flagArray[index1] & PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG) == PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {
                flagArray[index1] += PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG;
            }
        }
    }

    private void setCombinedCloudShadowFlagOnTile(Map<Integer, List<Integer>> clusteredShadowTileID) {
        //if a continuous clustered shadow coincides with a shifted (adjusted) shadow, keep it.

        List<Integer> coincideKey = new ArrayList<>();

        for (int key : clusteredShadowTileID.keySet()) {
            List<Integer> positions = clusteredShadowTileID.get(key);

            for (Integer position : positions) {
                int ind = position;
                if (((flagArray[ind] & PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG) == PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG)) {
                    coincideKey.add(key);
                    break;
                }
            }
        }

        if (coincideKey.size() > 0) {
            for (int key : coincideKey) {
                List<Integer> positions = clusteredShadowTileID.get(key);

                for (int index1 : positions) {
                    if (!((flagArray[index1] & PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG) == PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG) &&
                            !((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                            !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {
                        flagArray[index1] += PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG;
                    }
                }

            }
        }


    }

    private void setANDTestCombinedCloudShadowFlag(List<Integer> cloud, int Offset, int indexForOffset, Point2D[] cloudPath, Map<Integer, List<Integer>> ListShadowID) {

        List<Integer> indexShiftedCloud = new ArrayList<>();
        if (Offset == 0) {
            //shifted cloud as is (at BestOffset)
            Offset = bestOffset;
        }

        for (int index : cloud) {
            int[] x = revertIndexToXY(index, width);

            int x1 = x[0] + (int) cloudPath[Offset].getX();
            int y1 = x[1] + (int) cloudPath[Offset].getY();

            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;

            if ( //!((flagArray[index1] & PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG) == PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                            !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {
                //flagArray[index1] += PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG;
                indexShiftedCloud.add(index1);
            }
        }


        //coinciding pixels between cluster and shift?
        List<Integer> coincideKey = new ArrayList<>();
        for (int key : ListShadowID.keySet()) {
            List<Integer> positions = ListShadowID.get(key);
            int test[] = new int[flagArray.length];
            for (int i = 0; i < positions.size(); i++) {
                int ind = positions.get(i);
                if (ind < test.length) {
                    test[ind] += 1;
                }
            }

            for (int i = 0; i < indexShiftedCloud.size(); i++) {
                int ind = indexShiftedCloud.get(i);
                if (ind < test.length) {
                    if (test[ind] > 0) {
                        coincideKey.add(key);
                        //break;
                    }
                }
            }
        }

        List<Integer> noduplCoincideKey = new ArrayList<>(new LinkedHashSet<>(coincideKey));

        if (noduplCoincideKey.size() > 0) {
            for (int key : noduplCoincideKey) {
                List<Integer> positions = ListShadowID.get(key);

                for (int index1 : positions) {
                    if (!((flagArray[index1] & PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG) == PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG) &&
                            !((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                            !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {
                        flagArray[index1] += PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG;
                    }
                }

            }
        }


        if (indexShiftedCloud.size() > 0) {
            for (int index1 : indexShiftedCloud) {
                if (!((flagArray[index1] & PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG) == PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG) &&
                        !((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                        !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {
                    flagArray[index1] += PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG;

                }
            }
        }

    }

    private int[] revertIndexToXY(int index, int width) {
        int y = Math.floorDiv(index, width);
        int x = index - y * width;
        return new int[]{x, y};
    }

    private static float[] nonCloudMeans(float[][] clusterData, int[] flagArray, boolean onWater, boolean onLand) {
        float[] means = new float[clusterData.length];
        int validCounter = 0;
        for (int i = 0; i < clusterData[0].length; i++) {
            boolean valid = !((flagArray[i] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG);
            if (!onWater) {
                valid = valid && !((flagArray[i] & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG);
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
            boolean valid = !((flagArray[i] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[i] & PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG) == PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG);
            if (!onWater) {
                valid = valid && !((flagArray[i] & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG);
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
            boolean valid = !((flagArray[i] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[i] & PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG) == PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG);
            if (!onWater) {
                valid = valid && !((flagArray[i] & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG);
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

    private void analyseCloudShadows(int counter, int minNumberMemberCluster, double[][] arrayBands, int[] arrayIndexes, int[] arrayOffsets,
                                     double[] minArrayBands, float[] thresholds, double mean, int[] shadowIDArray, float[] sourceBand) {

        if (counter > minNumberMemberCluster) {
           /*
           test with counter > minNumberMemberCluster && cloudSize >3: leads to missing stripes of shadows.
            */
//            analysePotentialCloudShadowArea_sigma(counter, arrayBands, arrayIndexes, thresholds);
//            analysePotentialCloudShadowArea_percentiles(counter, arrayBands, arrayIndexes, mean);
            analysePotentialCloudShadowArea_clustering(counter, arrayBands, arrayIndexes, arrayOffsets, shadowIDArray, sourceBand);
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
                for (int i = 0; i < numSteps - 2; i++) {
                    final int currentIndex = startIndex + (int) ((i + 1) * stepper);
                    diffs[i] = sortedBand[currentIndex] -
                            sortedBand[startIndex + (int) (i * stepper)];
                    if (maxDiff < diffs[i]) {
                        maxDiff = diffs[i];
                        maxDiffIndex = i;
                    }
                }
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
        return sortedBand[startIndex] + (sortedBand[endIndex] - sortedBand[startIndex]) / 2;
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

    private void analysePotentialCloudShadowArea_clustering(int counter, double[][] arrayBands, int[] arrayIndexes,
                                                            int[] arrayOffsets, int[] shadowIDArray, float[] sourceBand) {
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
        int counterWhiteness = (int) (Math.floor(counter * S2IdepixPreCloudShadowOp.OUTLIER_THRESHOLD));
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
        double averageDistance = maxDist / (numberOfClusters - 1);
        for (int i = 0; i < sortedCluster.size() - 2; i++) {
            if (sortedCluster.get(i + 1) - sortedCluster.get(i) > averageDistance) {
                break;
            }
        }
        double threshold = sortedCluster.get(0) + (sortedCluster.get(1) - sortedCluster.get(0)) / 2;
        List<Integer> shadowIndex = new ArrayList<>();
        List<Integer> shadowOffset = new ArrayList<>();
        List<Double> shadowRefl = new ArrayList<>();

        for (int j = 0; j < counter; j++) { //potential cloud shadow
            //cloudTestArray[arrayIndexes[j]] = key; //potential cloud shadow in the analysis
            if (band[j] < threshold) {
                int flagIndex = arrayIndexes[j];
                if (bestOffset > 0) {
                    if (arrayOffsets[j] < 3 * bestOffset && arrayOffsets[j] > 0) {
                        shadowOffset.add(arrayOffsets[j]);
                        shadowIndex.add(flagIndex);

                        shadowRefl.add(band[j]);
                    }
                    if (!((flagArray[flagIndex] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)
                            && arrayOffsets[j] < 3 * bestOffset && arrayOffsets[j] > 0 && cloudSize > 1) {
                        flagArray[flagIndex] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                    }
                } else {
                    if (arrayOffsets[j] > 0) {
                        shadowOffset.add(arrayOffsets[j]);
                        shadowIndex.add(flagIndex);
                        shadowRefl.add(band[j]);
                    }
                    if (!((flagArray[flagIndex] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)
                            && arrayOffsets[j] > 0 && cloudSize > 1) {
                        flagArray[flagIndex] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                    }
                }
            }
        }



        /*
        Checking cloud shadow results of the shifted mask against the clusters.
        Can only work, if bestOffset >0!!
        •	1) Find all continuous clusters of dark pixels.
        •	compare position of shifted cloud mask and cluster; and reflectance values for both.
        */


        if (bestOffset > 0 && shadowIndex.size() > 20 && cloudSize > 1) {
            //duplicates are removed!
            //initialize with shadow flags from clustering.
            int test[] = new int[flagArray.length];
            for (Integer aShadowIndex : shadowIndex) {
                test[aShadowIndex] = 1;
            }
            //find continuous cluster

            FindContinuousAreas testContinuousShadow = new FindContinuousAreas(test);
            Map<Integer, List<Integer>> listShadowID = testContinuousShadow.computeAreaID(width, height, shadowIDArray, false);
            if (listShadowID.size() > 1) {
                Map<Integer, Integer> meanOffsetClust = new HashMap<>();

                //calculate mean offset and mean Refl for each of the continuous shadow areas from the clustering
                for (int i : listShadowID.keySet()) {

                    List<Integer> pos = listShadowID.get(i);
                    double meanRefl = 0.;
                    int meanOffset = 0;
                    int N = 0;
                    for (int j : pos) {

                        shadowIDArray[j] = i;
                        int k = shadowIndex.indexOf(j);
                        if (k > 0) {
                            meanOffset += shadowOffset.get(k);
                            meanRefl += shadowRefl.get(k);
                            N += 1;
                        }
                    }

                    if (N > 0) {
                        meanOffsetClust.put(i, meanOffset / N);
                    }

                }
                Map<Integer, Double> clusterTest = new HashMap<>();
                double minRefl = 0.;
                for (int i : meanOffsetClust.keySet()) {

                    double meanRefl = setMeanRefl(cloud, meanOffsetClust.get(i), sourceBand, cloudPath);
                    clusterTest.put(i, meanRefl);

                    if (minRefl == 0 || meanRefl < minRefl) minRefl = meanRefl;
                }
                if (minRefl > 0) {
                    if (minRefl < meanReflShift) {
                        for (int index : clusterTest.keySet()) {
                            if (clusterTest.get(index) == minRefl) {
                                int offset = meanOffsetClust.get(index);
                                if (offset < 2 * bestOffset) {
                                    //remove flags for shifted shadow for bestOffset
                                    switchOffShiftedCloudShadowFlag(cloud, bestOffset, cloudPath);
                                    //add flags for shifted shadow for this offset.
                                    setShiftedCloudShadowFlag(cloud, offset, cloudPath);
                                }
                            }
                        }
                    }
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
        return 4;
    }

    interface AnalyzerMode {

        void initArrays(int size);

        void doIterationStep(int index, int offset);

        void doCloudShadowAnalysis(int minNumberMemberCluster, int[] shadowIDArray, float[] sourceBand);

    }

    private class LandWaterAnalyzerMode implements AnalyzerMode {

        private final float[][] sourceBands;
        int counterA;
        int counterB;
        private double[][] arrayBands;
        private int[][] arrayIndexes;
        private int[][] arrayOffsets;
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
            arrayOffsets = new int[2][size];
            minArrayBands = new double[2];
            counterA = 0;
            counterB = 0;
            for (int i = 0; i < 2; i++) {
                Arrays.fill(arrayBands[i], Double.NaN);
                Arrays.fill(arrayIndexes[i], -1);
                Arrays.fill(arrayOffsets[i], -1);
                minArrayBands[i] = Double.MAX_VALUE;
            }
        }

        @Override
        public void doIterationStep(int index, int offset) {
            final int flag = flagArray[index];
            arrayBands[0][counterA] = sourceBands[0][index];
            arrayBands[1][counterB] = sourceBands[1][index];

            if (arrayBands[0][counterA] >= 1e-8 && !Double.isNaN(arrayBands[0][counterA]) &&
                    (flag & PreparationMaskBand.LAND_FLAG) == PreparationMaskBand.LAND_FLAG) {
                arrayIndexes[0][counterA] = index;
                arrayOffsets[0][counterA] = offset;

                if (arrayBands[0][counterA] < minArrayBands[0]) {
                    minArrayBands[0] = arrayBands[0][counterA];
                }
                counterA++;
            } else if (arrayBands[1][counterB] >= 1e-8 && !Double.isNaN(arrayBands[1][counterB]) &&
                    (flag & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG) {
                arrayIndexes[1][counterB] = index;
                arrayOffsets[1][counterB] = offset;

                if (arrayBands[1][counterB] < minArrayBands[1]) {
                    minArrayBands[1] = arrayBands[1][counterB];
                }
                counterB++;
            }
        }

        @Override
        public void doCloudShadowAnalysis(int minNumberMemberCluster, int[] shadowIDArray, float[] sourceBand) {
            analyseCloudShadows(counterA, minNumberMemberCluster, new double[][]{arrayBands[0]}, arrayIndexes[0], arrayOffsets[0],
                    new double[]{minArrayBands[0]}, landThreshholds, landMean, shadowIDArray, sourceBand);
            analyseCloudShadows(counterB, minNumberMemberCluster, new double[][]{arrayBands[1]}, arrayIndexes[1], arrayOffsets[1],
                    new double[]{minArrayBands[1]}, waterThreshholds, waterMean, shadowIDArray, sourceBand);
        }

    }

    private class MultiBandAnalyzerMode implements AnalyzerMode {

        private final float[][] sourceBands;
        int counter;
        private double[] arrayBandA;
        private double[] arrayBandB;
        private int[] arrayIndexes;
        private int[] arrayOffsets;
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
            arrayOffsets = new int[size];
            counter = 0;
            Arrays.fill(arrayBandA, Double.NaN);
            Arrays.fill(arrayBandB, Double.NaN);
            Arrays.fill(arrayIndexes, -1);
            Arrays.fill(arrayOffsets, -1);

            minArrayBandA = Double.MAX_VALUE;
            minArrayBandB = Double.MAX_VALUE;
            minArrayBandAB = Double.MAX_VALUE;
        }

        @Override
        public void doIterationStep(int index, int offset) {
            arrayBandA[counter] = sourceBands[0][index];
            arrayBandB[counter] = sourceBands[1][index];

            if (arrayBandA[counter] < -0.99 || arrayBandB[counter] < -0.99) {
                arrayBandA[counter] = 1.0; //Double.NaN;
                arrayBandB[counter] = 1.0; //Double.NaN;
            }
            arrayIndexes[counter] = index;
            arrayOffsets[counter] = offset;
            if ((Math.pow(arrayBandA[counter], 2) + Math.pow(arrayBandB[counter], 2)) < minArrayBandAB) {
                minArrayBandAB = Math.pow(arrayBandA[counter], 2) + Math.pow(arrayBandB[counter], 2);
                minArrayBandA = arrayBandA[counter];
                minArrayBandB = arrayBandB[counter];
            }
            counter++;
        }

        @Override
        public void doCloudShadowAnalysis(int minNumberMemberCluster, int[] shadowIDarray, float[] sourceBand) {
            analyseCloudShadows(counter, minNumberMemberCluster, new double[][]{arrayBandA, arrayBandB}, arrayIndexes, arrayOffsets,
                    new double[]{minArrayBandA, minArrayBandB}, thresholds, mean, shadowIDarray, sourceBand);
        }

    }

    private class SingleBandAnalyzerMode implements AnalyzerMode {

        int counter;
        private final float[] sourceBand;
        private double[] arrayBandA;
        private int[] arrayIndexes;
        private int[] arrayOffsets;
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
            arrayOffsets = new int[size];
            counter = 0;
            Arrays.fill(arrayBandA, Double.NaN);
            Arrays.fill(arrayIndexes, -1);
            minArrayBandA = Double.MAX_VALUE;
        }

        @Override
        public void doIterationStep(int index, int offset) {
            arrayBandA[counter] = sourceBand[index];

            if (arrayBandA[counter] < -0.99) arrayBandA[counter] = 1.0; //Double.NaN;

            arrayIndexes[counter] = index;
            arrayOffsets[counter] = offset;

            if (arrayBandA[counter] < minArrayBandA) {
                minArrayBandA = arrayBandA[counter];
            }
            counter++;
        }

        @Override
        public void doCloudShadowAnalysis(int minNumberMemberCluster, int[] shadowIDarray, float[] sourceBand) {
            analyseCloudShadows(counter, minNumberMemberCluster, new double[][]{arrayBandA}, arrayIndexes, arrayOffsets,
                    new double[]{minArrayBandA}, thresholds, mean, shadowIDarray, sourceBand);
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
