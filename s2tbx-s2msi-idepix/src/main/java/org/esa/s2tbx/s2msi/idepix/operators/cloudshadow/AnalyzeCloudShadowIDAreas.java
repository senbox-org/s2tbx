package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.datamodel.Product;

import java.awt.Rectangle;
import java.util.Arrays;

/**
 * todo: add comment
 */
class AnalyzeCloudShadowIDAreas {

    static int clusterCount = S2IdepixCloudShadowOp.clusterCountDefine;
    static final int maxIterCount = 30;

    private AnalyzeCloudShadowIDAreas() {
    }

    static void identifyCloudShadowArea(Product sourceProduct, Rectangle sourceRectangle, float[] landClusterSamples,
                                        float[] waterClusterSamples, int[] flagArray,
                                        int[] cloudShadowIDArray, int[] cloudLongShadowIDArray,
                                        int[][] cloudShadowIdBorderRectangle, int cloudIndexTable) {

        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int sourceLength = sourceRectangle.width * sourceRectangle.height;

        int productHeight = sourceProduct.getSceneRasterHeight();
        int productWidth = sourceProduct.getSceneRasterWidth();

        int maxRectangleWidth = -1;
        int maxRectangleHeight = -1;
        int landCounter;
        int waterCounter;
        int minNumberMemberCluster;


        int[] dummyLongCloudShadowIDArray = new int[sourceLength];
        Arrays.fill(dummyLongCloudShadowIDArray, sourceLength);

        // cloudShadowIdBorderRectangle  - position of source array (e.g.  0,0 - > -CloudShadowOp.searchBorderRadius, -CloudShadowOp.searchBorderRadius in sourceRectangle,
        // CloudShadowOp.searchBorderRadius, CloudShadowOp.searchBorderRadius in targetRectangle

        // define maximum rectangle width and maximum rectangle height
        for (int cloudIndex = 0; cloudIndex < cloudIndexTable; cloudIndex++) {
            // todo check if
            if ((cloudShadowIdBorderRectangle[cloudIndex][0] != productWidth + 1) || (cloudShadowIdBorderRectangle[cloudIndex][1] != -1) ||
                    (cloudShadowIdBorderRectangle[cloudIndex][2] != productHeight + 1) || (cloudShadowIdBorderRectangle[cloudIndex][3] != -1)) {
                maxRectangleWidth = Math.max(maxRectangleWidth, cloudShadowIdBorderRectangle[cloudIndex][1] - cloudShadowIdBorderRectangle[cloudIndex][0]);
                maxRectangleHeight = Math.max(maxRectangleHeight, cloudShadowIdBorderRectangle[cloudIndex][3] - cloudShadowIdBorderRectangle[cloudIndex][2]);
            }
        }

        double[] landCloudShadow = new double[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(landCloudShadow, Double.NaN);
        double[] waterCloudShadow = new double[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(waterCloudShadow, Double.NaN);
        int[] landXPositions = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(landXPositions, -1);
        int[] landYPositions = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(landYPositions, -1);
        int[] waterXPositions = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(waterXPositions, -1);
        int[] waterYPositions = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(waterYPositions, -1);


        for (int cloudIndex = SegmentationCloud.NO_SHADOW + 1; cloudIndex < cloudIndexTable; cloudIndex++) {
            landCounter = 0;
            waterCounter = 0;

            // todo invert if
            if ((cloudShadowIdBorderRectangle[cloudIndex][0] != productWidth + 1) || (cloudShadowIdBorderRectangle[cloudIndex][1] != -1) ||
                    (cloudShadowIdBorderRectangle[cloudIndex][2] != productHeight + 1) || (cloudShadowIdBorderRectangle[cloudIndex][3] != -1)) {
                Arrays.fill(landCloudShadow, Double.NaN);
                Arrays.fill(waterCloudShadow, Double.NaN);
                Arrays.fill(landXPositions, -1);
                Arrays.fill(waterXPositions, -1);
                Arrays.fill(landYPositions, -1);
                Arrays.fill(waterYPositions, -1);
                double minLandValue = Double.MAX_VALUE;
                double minWaterValue = Double.MAX_VALUE;

                for (int j = cloudShadowIdBorderRectangle[cloudIndex][2]; j <= cloudShadowIdBorderRectangle[cloudIndex][3]; j++) {
                    for (int i = cloudShadowIdBorderRectangle[cloudIndex][0]; i <= cloudShadowIdBorderRectangle[cloudIndex][1]; i++) {

                        if (cloudShadowIDArray[j * (sourceWidth) + i] == cloudIndex) {

                            final int flag = flagArray[j * (sourceWidth) + i];

                            landCloudShadow[landCounter] = landClusterSamples[j * (sourceWidth) + i];
                            waterCloudShadow[waterCounter] = waterClusterSamples[j * (sourceWidth) + i];

                            if (landCloudShadow[landCounter] >= 1e-8 && !Double.isNaN(landCloudShadow[landCounter]) &&
                                    flag == PreparationMaskBand.LAND_FLAG) {
                                landXPositions[landCounter] = i;
                                landYPositions[landCounter] = j;

                                if (landCloudShadow[landCounter] < minLandValue) {
                                    minLandValue = landCloudShadow[landCounter];
                                }
                                landCounter++;
                            } else if (waterCloudShadow[waterCounter] >= 1e-8 && !Double.isNaN(waterCloudShadow[waterCounter]) &&
                                    flag == PreparationMaskBand.OCEAN_FLAG) {
                                waterXPositions[waterCounter] = i;
                                waterYPositions[waterCounter] = j;

                                if (waterCloudShadow[waterCounter] < minWaterValue) {
                                    minWaterValue = waterCloudShadow[waterCounter];
                                }
                                waterCounter++;
                            }
                        }
                    }
                }

                minNumberMemberCluster = clusterCount * 2 + 1;

                analyseCloudShadows(landClusterSamples, flagArray, cloudShadowIDArray, cloudLongShadowIDArray,
                                    cloudShadowIdBorderRectangle, sourceWidth, sourceHeight, landCounter,
                                    minNumberMemberCluster, landCloudShadow, landXPositions, landYPositions, cloudIndex,
                                    minLandValue);
                analyseCloudShadows(waterClusterSamples, flagArray, cloudShadowIDArray, cloudLongShadowIDArray,
                                    cloudShadowIdBorderRectangle, sourceWidth, sourceHeight, waterCounter,
                                    minNumberMemberCluster, waterCloudShadow, waterXPositions, waterYPositions, cloudIndex,
                                    minWaterValue);
            }
        }
    }

    private static void analyseCloudShadows(float[] sourceBand, int[] flagArray, int[] cloudShadowIDArray,
                                            int[] cloudLongShadowIDArray, int[][] cloudShadowIdBorderRectangle,
                                            int sourceWidth, int sourceHeight, int counter, int minNumberMemberCluster,
                                            double[] cloudShadow, int[] xPositions, int[] yPositions, int cloudIndex,
                                            double minValue) {
        // minimum number of potential shadow points for the cluster analysis per cluster
        if (counter > minNumberMemberCluster && counter < S2IdepixCloudShadowOp.CloudShadowFragmentationThreshold) {
            analysePotentialCloudShadowArea(flagArray, sourceWidth, counter, cloudShadow,
                                            xPositions, yPositions);
        } else if (counter >= S2IdepixCloudShadowOp.CloudShadowFragmentationThreshold) {
            analyseLongCloudShadows(sourceWidth, sourceHeight, cloudShadowIDArray, cloudShadowIdBorderRectangle,
                                    cloudIndex, cloudShadow, xPositions, yPositions, flagArray, sourceBand,
                                    minNumberMemberCluster, cloudLongShadowIDArray);
        } else {
            analyseSmallCloudShadows(flagArray, cloudShadow, minValue, sourceWidth, counter, xPositions, yPositions);
        }
    }

    private static void analyseLongCloudShadows(int sourceWidth, int sourceHeight, int[] cloudShadowIDArray,
                                                int[][] cloudShadowIdBorderRectangle, int cloudIndex,
                                                double[] cloudShadow, int[] xPositions, int[] yPositions, int[] flagArray,
                                                float[] sourceBand, int minNumberMemberCluster,
                                                int[] cloudLongShadowIDArray) {
        final int sourceLength = sourceWidth * sourceHeight;
        int[] dummyLongCloudShadowIDArray = new int[sourceLength];
        //will be filled in SegmentationLongCloudClass Arrays.fill(cloudIdArray, ....);
        Arrays.fill(dummyLongCloudShadowIDArray, sourceLength);
        //bc cloud shadow cloudIndexTable = max of ID
        int counterTableLongShadow = SegmentationLongCloudShadow.computeLongCloudShadowID(
                sourceWidth,
                sourceHeight,
                cloudShadowIDArray,
                cloudShadowIdBorderRectangle,
                dummyLongCloudShadowIDArray,
                cloudIndex);

        for (int longCloudIndex = SegmentationLongCloudShadow.NO_SHADOW + 1; longCloudIndex < counterTableLongShadow; longCloudIndex++) {

            int counter = 0;
            Arrays.fill(cloudShadow, Double.NaN);
            Arrays.fill(xPositions, -1);
            Arrays.fill(yPositions, -1);
            double minValue = Double.MAX_VALUE;
            for (int j = cloudShadowIdBorderRectangle[cloudIndex][2]; j <= cloudShadowIdBorderRectangle[cloudIndex][3]; j++) {
                for (int i = cloudShadowIdBorderRectangle[cloudIndex][0]; i <= cloudShadowIdBorderRectangle[cloudIndex][1]; i++) {
                    if (dummyLongCloudShadowIDArray[j * (sourceWidth) + i] == longCloudIndex) {
                        final int flag = flagArray[j * (sourceWidth) + i];
                        if (flag == PreparationMaskBand.LAND_FLAG) {
                            cloudShadow[counter] = sourceBand[j * (sourceWidth) + i];
                            if (cloudShadow[counter] < minValue) {
                                minValue = cloudShadow[counter];
                            }
                            xPositions[counter] = i;
                            yPositions[counter] = j;
                            counter++;
                        }

                    }
                }
            }

            if (counter > minNumberMemberCluster) { // minimum number of potential shadow points for the cluster analysis per cluster
                analysePotentialCloudShadowArea(flagArray, sourceWidth, counter, cloudShadow, xPositions, yPositions);
            } else if (counter > 0) {
                analyseSmallCloudShadows(flagArray, cloudShadow, minValue, sourceWidth, counter, xPositions, yPositions);
            }
            for (int z = 0; z < sourceLength; z++) {
                cloudLongShadowIDArray[z] += 1000 * cloudIndex + dummyLongCloudShadowIDArray[z];
            }
        }
    }

    private static void analyseSmallCloudShadows(int[] flagArray, double[] cloudShadow, double minValue,
                                                 int sourceWidth, int counter, int[] xPositions, int[] yPositions) {
        int indexkk;
        for (int kkk = 0; kkk < counter; kkk++) {
            indexkk = yPositions[kkk] * sourceWidth + xPositions[kkk];
            if (flagArray[indexkk] < PreparationMaskBand.CLOUD_SHADOW_FLAG && cloudShadow[kkk] <= minValue) {
                flagArray[indexkk] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
            }
        }
    }

    private static void analysePotentialCloudShadowArea(int[] flagArray,
                                                        int sourceWidth,
                                                        int counter,
                                                        double[] cloudShadow,
                                                        int[] xPositions,
                                                        int[] yPositions) {
        double darkness;
        int darkestClusterNumber;
        double whiteness;
        int containerNumber;
        int index;

        double distance;
        double temp; // sort bandA (ascending) and select the element (94% of the sorted values)
        double[] arraySortedBand = new double[counter];

        // todo adaption to more bands for all sensors
        System.arraycopy(cloudShadow, 0, arraySortedBand, 0, counter);

        Arrays.sort(arraySortedBand);
        int counterWhiteness = (int) (Math.floor(counter * S2IdepixCloudShadowOp.OUTLIER_THRESHOLD));
        if (counterWhiteness >= counter) counterWhiteness = counter - 1;
        double thresholdWhiteness = arraySortedBand[counterWhiteness];
        double darkestLandValue = arraySortedBand[0];

        // add 2.5% of darkest values to shadow array but at least one pixel is added
        int addedDarkValues = 1 + (int) Math.floor(0.025 * counterWhiteness + 0.5);

        double[] clusterableLand = new double[counterWhiteness + addedDarkValues];
        Arrays.fill(clusterableLand, darkestLandValue);

//        double[] arrayClusterableBandB = new double[counterWhiteness + addedDarkValues];

        int countIntern = 0;
        for (int dd = 0; dd < counter; dd++) {
            if (cloudShadow[dd] < thresholdWhiteness && countIntern < counterWhiteness) {
                clusterableLand[countIntern] = cloudShadow[dd];
//                arrayClusterableBandB[countIntern] = arrayBandB[dd];
                countIntern++;
            }
        }

        double[][] imageData = new double[S2IdepixCloudShadowOp.SENSOR_BAND_CLUSTERING][counter];
        imageData[0] = clusterableLand; //band1data;
        //imageData[1] = arrayClusterableBandB;

        ClusteringKMeans computeClustering = new ClusteringKMeans();
        double[][] clusterCentroidArray = computeClustering.computedKMeansCluster(imageData);

        darkness = Double.MAX_VALUE;
        darkestClusterNumber = -1;
        whiteness = Double.MIN_VALUE;
        // todo adaption for more bands required

        // search for a darkest cluster
        for (int kk = 0; kk < clusterCount; kk++) {
            if ((clusterCentroidArray[kk][0] < darkness)) {
                darkness = clusterCentroidArray[kk][0];
                darkestClusterNumber = kk;
            }
            if ((clusterCentroidArray[kk][0] > whiteness)) {
                whiteness = clusterCentroidArray[kk][0];
            }
        }

        // distance analysis BandValue in relation to the CentroidValue
        // assign membership of BandValue to Cluster
        containerNumber = -1;
        for (int gg = 0; gg < counter; gg++) {
            distance = Double.MAX_VALUE;
            for (int ff = 0; ff < clusterCount; ff++) {
                temp = Math.abs(clusterCentroidArray[ff][0] - cloudShadow[gg]);
                if (temp < distance) {
                    distance = temp;
                    containerNumber = ff;
                }
            }

            if (containerNumber == darkestClusterNumber && whiteness - darkness > S2IdepixCloudShadowOp.Threshold_Whiteness_Darkness) {
                index = (yPositions[gg] * sourceWidth + xPositions[gg]);
                if (flagArray[index] < PreparationMaskBand.CLOUD_SHADOW_FLAG) {
                    flagArray[index] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                }
            }
        }
    }
}

