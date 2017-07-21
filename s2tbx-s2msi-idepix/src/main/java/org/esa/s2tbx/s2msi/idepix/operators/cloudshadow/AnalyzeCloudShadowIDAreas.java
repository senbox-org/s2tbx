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

    static void identifyCloudShadowArea(Product sourceProduct, Rectangle sourceRectangle, float[] sourceBandA,
                                        float[] sourceBandB, int[] flagArray,
                                        int[] cloudShadowIDArray, int[] cloudLongShadowIDArray,
                                        int[][] cloudShadowIdBorderRectangle, int cloudIndexTable) {

        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int sourceLength = sourceRectangle.width * sourceRectangle.height;

        int productHeight = sourceProduct.getSceneRasterHeight();
        int productWidth = sourceProduct.getSceneRasterWidth();

        int maxRectangleWidth = -1;
        int maxRectangleHeight = -1;
        int counterA;
        int counterB;
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

        double[] arrayBandA = new double[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(arrayBandA, Double.NaN);
        double[] arrayBandB = new double[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(arrayBandB, Double.NaN);
        int[] arrayXPos = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(arrayXPos, -1);
        int[] arrayYPos = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(arrayYPos, -1);
        int[] arrayXPosA = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(arrayXPosA, -1);
        int[] arrayYPosA = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(arrayYPosA, -1);
        int[] arrayXPosB = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(arrayXPosB, -1);
        int[] arrayYPosB = new int[(maxRectangleWidth + 1) * (maxRectangleHeight + 1)];
        Arrays.fill(arrayYPosB, -1);


        for (int cloudIndex = SegmentationCloud.NO_SHADOW + 1; cloudIndex < cloudIndexTable; cloudIndex++) {
            counterA = 0;
            counterB = 0;

            // todo invert if
            if ((cloudShadowIdBorderRectangle[cloudIndex][0] != productWidth + 1) || (cloudShadowIdBorderRectangle[cloudIndex][1] != -1) ||
                    (cloudShadowIdBorderRectangle[cloudIndex][2] != productHeight + 1) || (cloudShadowIdBorderRectangle[cloudIndex][3] != -1)) {
                Arrays.fill(arrayBandA, Double.NaN);
                Arrays.fill(arrayBandB, Double.NaN);
                Arrays.fill(arrayXPosA, -1);
                Arrays.fill(arrayXPosB, -1);
                Arrays.fill(arrayYPosA, -1);
                Arrays.fill(arrayYPosB, -1);
                double minArrayBandA = Double.MAX_VALUE;
                double minArrayBandB = Double.MAX_VALUE;

                for (int j = cloudShadowIdBorderRectangle[cloudIndex][2]; j <= cloudShadowIdBorderRectangle[cloudIndex][3]; j++) {
                    for (int i = cloudShadowIdBorderRectangle[cloudIndex][0]; i <= cloudShadowIdBorderRectangle[cloudIndex][1]; i++) {

                        if (cloudShadowIDArray[j * (sourceWidth) + i] == cloudIndex) {

                            final int flag = flagArray[j * (sourceWidth) + i];

                            arrayBandA[counterA] = sourceBandA[j * (sourceWidth) + i];
                            arrayBandB[counterB] = sourceBandB[j * (sourceWidth) + i];

                            if (arrayBandA[counterA] >= 1e-8 && !Double.isNaN(arrayBandA[counterA]) &&
                                    flag == PreparationMaskBand.LAND_FLAG) {
                                arrayXPosA[counterA] = i;
                                arrayYPosA[counterA] = j;

                                if (arrayBandA[counterA] < minArrayBandA) {
                                    minArrayBandA = arrayBandA[counterA];
                                }
                                counterA++;
                            } else if (arrayBandB[counterB] >= 1e-8 && !Double.isNaN(arrayBandB[counterB]) &&
                                    flag == PreparationMaskBand.OCEAN_FLAG) {
                                arrayXPosB[counterB] = i;
                                arrayYPosB[counterB] = j;

                                if (arrayBandB[counterB] < minArrayBandB) {
                                    minArrayBandB = arrayBandB[counterB];
                                }
                                counterB++;
                            }
                        }
                    }
                }

                minNumberMemberCluster = clusterCount * 2 + 1;

                analyseCloudShadows(sourceBandA, flagArray, cloudShadowIDArray, cloudLongShadowIDArray,
                                    cloudShadowIdBorderRectangle, sourceWidth, sourceHeight, counterA,
                                    minNumberMemberCluster, arrayBandA, arrayXPosA, arrayYPosA, cloudIndex,
                                    minArrayBandA);
                analyseCloudShadows(sourceBandB, flagArray, cloudShadowIDArray, cloudLongShadowIDArray,
                                    cloudShadowIdBorderRectangle, sourceWidth, sourceHeight, counterB,
                                    minNumberMemberCluster, arrayBandB, arrayXPosB, arrayYPosB, cloudIndex,
                                    minArrayBandB);
            }
        }
    }

    private static void analyseCloudShadows(float[] sourceBand, int[] flagArray, int[] cloudShadowIDArray,
                                            int[] cloudLongShadowIDArray, int[][] cloudShadowIdBorderRectangle,
                                            int sourceWidth, int sourceHeight, int counter, int minNumberMemberCluster,
                                            double[] arrayBand, int[] arrayXPos, int[] arrayYPos, int cloudIndex,
                                            double minArrayBand) {
        // minimum number of potential shadow points for the cluster analysis per cluster
        if (counter > minNumberMemberCluster && counter < S2IdepixCloudShadowOp.CloudShadowFragmentationThreshold) {
            analysePotentialCloudShadowArea(flagArray, sourceWidth, counter, arrayBand,
                                            arrayXPos, arrayYPos);
        } else if (counter >= S2IdepixCloudShadowOp.CloudShadowFragmentationThreshold) {
            analyseLongCloudShadows(sourceWidth, sourceHeight, cloudShadowIDArray, cloudShadowIdBorderRectangle,
                                    cloudIndex, arrayBand, arrayXPos, arrayYPos, flagArray, sourceBand,
                                    minNumberMemberCluster, cloudLongShadowIDArray);
        } else {
            analyseSmallCloudShadows(flagArray, arrayBand, minArrayBand, sourceWidth, counter, arrayXPos, arrayYPos);
        }
    }

    private static void analyseLongCloudShadows(int sourceWidth, int sourceHeight, int[] cloudShadowIDArray,
                                                int[][] cloudShadowIdBorderRectangle, int cloudIndex,
                                                double[] arrayBand, int[] arrayXPos, int[] arrayYPos, int[] flagArray,
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
            Arrays.fill(arrayBand, Double.NaN);
            Arrays.fill(arrayXPos, -1);
            Arrays.fill(arrayYPos, -1);
            double minArrayBand = Double.MAX_VALUE;
            for (int j = cloudShadowIdBorderRectangle[cloudIndex][2]; j <= cloudShadowIdBorderRectangle[cloudIndex][3]; j++) {
                for (int i = cloudShadowIdBorderRectangle[cloudIndex][0]; i <= cloudShadowIdBorderRectangle[cloudIndex][1]; i++) {
                    if (dummyLongCloudShadowIDArray[j * (sourceWidth) + i] == longCloudIndex) {
                        final int flag = flagArray[j * (sourceWidth) + i];
                        if (flag == PreparationMaskBand.LAND_FLAG) {
                            arrayBand[counter] = sourceBand[j * (sourceWidth) + i];
                            if (arrayBand[counter] < minArrayBand) {
                                minArrayBand = arrayBand[counter];
                            }
                            arrayXPos[counter] = i;
                            arrayYPos[counter] = j;
                            counter++;
                        }

                    }
                }
            }

            if (counter > minNumberMemberCluster) { // minimum number of potential shadow points for the cluster analysis per cluster
                analysePotentialCloudShadowArea(flagArray, sourceWidth, counter,
                                                arrayBand, arrayXPos, arrayYPos);
            } else if (counter > 0) {
                analyseSmallCloudShadows(flagArray, arrayBand, minArrayBand, sourceWidth,
                                         counter, arrayXPos, arrayYPos);
            }
            for (int z = 0; z < sourceLength; z++) {
                cloudLongShadowIDArray[z] += 1000 * cloudIndex + dummyLongCloudShadowIDArray[z];
            }
        }
    }

    private static void analyseSmallCloudShadows(int[] flagArray, double[] arrayBandA, double minArrayBandA,
                                                 int sourceWidth, int counter, int[] arrayXPos, int[] arrayYPos) {
        int indexkk;
        for (int kkk = 0; kkk < counter; kkk++) {
            indexkk = arrayYPos[kkk] * sourceWidth + arrayXPos[kkk];
            if (flagArray[indexkk] < PreparationMaskBand.CLOUD_SHADOW_FLAG && arrayBandA[kkk] <= minArrayBandA) {
                flagArray[indexkk] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
            }
        }
    }

    private static void analysePotentialCloudShadowArea(int[] flagArray,
                                                        int sourceWidth,
                                                        int counter,
                                                        double[] arrayBandA,
                                                        int[] arrayXPos,
                                                        int[] arrayYPos) {
        double darkness;
        int darkestClusterNumber;
        double whiteness;
        int containerNumber;
        int index;

        double distance;
        double temp; // sort bandA (ascending) and select the element (94% of the sorted values)
        double[] arraySortedBand = new double[counter];

        // todo adaption to more bands for all sensors
        System.arraycopy(arrayBandA, 0, arraySortedBand, 0, counter);

        Arrays.sort(arraySortedBand);
        int counterWhiteness = (int) (Math.floor(counter * S2IdepixCloudShadowOp.OUTLIER_THRESHOLD));
        if (counterWhiteness >= counter) counterWhiteness = counter - 1;
        double thresholdWhiteness = arraySortedBand[counterWhiteness];
        double darkestBandA = arraySortedBand[0];

        // add 2.5% of darkest values to shadow array but at least one pixel is added
        int addedDarkValues = 1 + (int) Math.floor(0.025 * counterWhiteness + 0.5);

        double[] arrayClusterableBandA = new double[counterWhiteness + addedDarkValues];
        Arrays.fill(arrayClusterableBandA, darkestBandA);

//        double[] arrayClusterableBandB = new double[counterWhiteness + addedDarkValues];

        int countIntern = 0;
        for (int dd = 0; dd < counter; dd++) {
            if (arrayBandA[dd] < thresholdWhiteness && countIntern < counterWhiteness) {
                arrayClusterableBandA[countIntern] = arrayBandA[dd];
//                arrayClusterableBandB[countIntern] = arrayBandB[dd];
                countIntern++;
            }
        }

        double[][] imageData = new double[S2IdepixCloudShadowOp.SENSOR_BAND_CLUSTERING][counter];
        imageData[0] = arrayClusterableBandA; //band1data;
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
                temp = Math.abs(clusterCentroidArray[ff][0] - arrayBandA[gg]);
                if (temp < distance) {
                    distance = temp;
                    containerNumber = ff;
                }
            }

            if (containerNumber == darkestClusterNumber && whiteness - darkness > S2IdepixCloudShadowOp.Threshold_Whiteness_Darkness) {
                index = (arrayYPos[gg] * sourceWidth + arrayXPos[gg]);
                if (flagArray[index] < PreparationMaskBand.CLOUD_SHADOW_FLAG) {
                    flagArray[index] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
                }
            }
        }
    }
}

