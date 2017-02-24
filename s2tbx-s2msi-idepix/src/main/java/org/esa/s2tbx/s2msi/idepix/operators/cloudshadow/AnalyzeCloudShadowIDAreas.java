package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.datamodel.Product;

import java.awt.*;
import java.util.Arrays;

/**
 * todo: add comment
 *
 */
class AnalyzeCloudShadowIDAreas {

    static int clusterCount = S2IdepixCloudShadowOp.clusterCountDefine;
    static final int maxIterCount = 30;

    private AnalyzeCloudShadowIDAreas() {
    }

    static void identifyCloudShadowArea(Product sourceProduct, Rectangle sourceRectangle, float[] sourceBandA,
                                        float[] sourceBandB, int[] flagArray,
                                        int[] cloudShadowIDArray, int[] cloudLongShadowIDArray,
                                        int[][] cloudShadowIdBorderRectangle, int counterTable) {

        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int sourceLength = sourceRectangle.width * sourceRectangle.height;

        int smallShadowsNumber = 0;

        int productHeight = sourceProduct.getSceneRasterHeight();
        int productWidth = sourceProduct.getSceneRasterWidth();

        int maxRectangleWidth = -1;
        int maxRectangleHeight = -1;
        int counter;
        int counterLong;
        int minNumberMemberCluster;
        int counterTableLongShadow;


        int[] dummyLongCloudShadowIDArray = new int[sourceLength];
        Arrays.fill(dummyLongCloudShadowIDArray, sourceLength);

        // cloudShadowIdBorderRectangle  - position of source array (e.g.  0,0 - > -CloudShadowOp.searchBorderRadius, -CloudShadowOp.searchBorderRadius in sourceRectangle,
        // CloudShadowOp.searchBorderRadius, CloudShadowOp.searchBorderRadius in targetRectangle
        for (int bc = 0; bc < counterTable; bc++) {
            if ((cloudShadowIdBorderRectangle[bc][0] != productWidth + 1) || (cloudShadowIdBorderRectangle[bc][1] != -1) ||
                    (cloudShadowIdBorderRectangle[bc][2] != productHeight + 1) || (cloudShadowIdBorderRectangle[bc][3] != -1)) {
                maxRectangleWidth = Math.max(maxRectangleWidth, cloudShadowIdBorderRectangle[bc][1] - cloudShadowIdBorderRectangle[bc][0]);
                maxRectangleHeight = Math.max(maxRectangleHeight, cloudShadowIdBorderRectangle[bc][3] - cloudShadowIdBorderRectangle[bc][2]);
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


        for (int bc = SegmentationCloud.NO_SHADOW + 1; bc < counterTable; bc++) {
            counter = 0;

            if ((cloudShadowIdBorderRectangle[bc][0] != productWidth + 1) || (cloudShadowIdBorderRectangle[bc][1] != -1) ||
                    (cloudShadowIdBorderRectangle[bc][2] != productHeight + 1) || (cloudShadowIdBorderRectangle[bc][3] != -1)) {
                Arrays.fill(arrayBandA, Double.NaN);
                Arrays.fill(arrayBandB, Double.NaN);
                Arrays.fill(arrayXPos, -1);
                Arrays.fill(arrayYPos, -1);
                double minArrayBandA = Double.MAX_VALUE;
                double minArrayBandB = Double.MAX_VALUE;

                for (int j = cloudShadowIdBorderRectangle[bc][2]; j <= cloudShadowIdBorderRectangle[bc][3]; j++) {
                    for (int i = cloudShadowIdBorderRectangle[bc][0]; i <= cloudShadowIdBorderRectangle[bc][1]; i++) {

                        if (cloudShadowIDArray[j * (sourceWidth) + i] == bc) {

                            arrayBandA[counter] = sourceBandA[j * (sourceWidth) + i];
                            arrayBandB[counter] = sourceBandB[j * (sourceWidth) + i];

                            if (arrayBandA[counter] < -0.99) arrayBandA[counter] = 1.0; //Double.NaN;
                            if (arrayBandB[counter] < -0.99) arrayBandB[counter] = 1.0; //Double.NaN;

                            arrayXPos[counter] = i;
                            arrayYPos[counter] = j;

                            if (arrayBandA[counter] < minArrayBandA) {
                                minArrayBandA = arrayBandA[counter];
                            }
                            if (arrayBandB[counter] < minArrayBandB) {
                                minArrayBandB = arrayBandB[counter];
                            }
                            counter++;
                            //System.out.printf("BC-ID: %d X0 %d,  Y0 %d  \n  ",bc, X0, Y0);

                        }
                    }
                }

                //System.out.printf("ShadowId %d,  number of pixels in the potential cloud shadow area %d  \n  ", bc, counter);

                minNumberMemberCluster = clusterCount * 2 + 1;

                if (counter > minNumberMemberCluster && counter < S2IdepixCloudShadowOp.CloudShadowFragmentationThreshold) { // minimum number of potential shadow points for the cluster analysis per cluster
                    analysePotentialCloudShadowArea(flagArray, sourceWidth, counter, arrayBandA,
                            arrayXPos, arrayYPos);
                } else {
                    if (counter >= S2IdepixCloudShadowOp.CloudShadowFragmentationThreshold) {

                        //will be filled in SegmentationLongCloudClass Arrays.fill(cloudIdArray, ....);
                        Arrays.fill(dummyLongCloudShadowIDArray, sourceLength);
                        //bc cloud shadow counterTable = max of ID
                        counterTableLongShadow = SegmentationLongCloudShadow.computeLongCloudShadowID(
                                sourceWidth,
                                sourceHeight,
                                cloudShadowIDArray,
                                cloudShadowIdBorderRectangle,
                                dummyLongCloudShadowIDArray,
                                bc);

                        for (int bcLong = SegmentationLongCloudShadow.NO_SHADOW + 1; bcLong < counterTableLongShadow; bcLong++) {

                            counterLong = 0;
                            Arrays.fill(arrayBandA, Double.NaN);
                            Arrays.fill(arrayBandB, Double.NaN);
                            Arrays.fill(arrayXPos, -1);
                            Arrays.fill(arrayYPos, -1);
                            minArrayBandA = Double.MAX_VALUE;
                            minArrayBandB = Double.MAX_VALUE;
                            for (int j = cloudShadowIdBorderRectangle[bc][2]; j <= cloudShadowIdBorderRectangle[bc][3]; j++) {
                                for (int i = cloudShadowIdBorderRectangle[bc][0]; i <= cloudShadowIdBorderRectangle[bc][1]; i++) {
                                    if (dummyLongCloudShadowIDArray[j * (sourceWidth) + i] == bcLong) {
                                        arrayBandA[counterLong] = sourceBandA[j * (sourceWidth) + i];
                                        arrayBandB[counterLong] = sourceBandB[j * (sourceWidth) + i];
                                        arrayXPos[counterLong] = i;
                                        arrayYPos[counterLong] = j;
                                        if (arrayBandA[counterLong] < minArrayBandA) {
                                            minArrayBandA = arrayBandA[counterLong];
                                        }
                                        if (arrayBandB[counterLong] < minArrayBandB) {
                                            minArrayBandB = arrayBandB[counterLong];
                                        }
                                        counterLong++;
                                    }
                                }
                            }

                            if (counterLong > minNumberMemberCluster) { // minimum number of potential shadow points for the cluster analysis per cluster
                                analysePotentialCloudShadowArea(flagArray, sourceWidth, counterLong, arrayBandA,
                                        arrayXPos, arrayYPos);
                            } else {
                                if (counterLong > 0) {
                                    smallShadowsNumber = analyseSmallCloudShadows(flagArray, arrayBandA,
                                            minArrayBandA, sourceWidth, smallShadowsNumber,
                                            counterLong, arrayXPos, arrayYPos);
                                }
                            }
                        }

                        for (int z = 0; z < sourceLength; z++) {
                            cloudLongShadowIDArray[z] += 1000 * bc + dummyLongCloudShadowIDArray[z];
                        }

                    } else {
                        smallShadowsNumber = analyseSmallCloudShadows(flagArray, arrayBandA,
                                minArrayBandA, sourceWidth, smallShadowsNumber,
                                counter, arrayXPos, arrayYPos);

                    }
                }
            }
        }

    }

    private static int analyseSmallCloudShadows(int[] flagArray, double[] arrayBandA,
                                                double minArrayBandA, int sourceWidth,
                                                int smallShadowsNumber, int counter, int[] arrayXPos, int[] arrayYPos) {
        int indexkk;
        smallShadowsNumber++;


        for (int kkk = 0; kkk < counter; kkk++) {
            indexkk = arrayYPos[kkk] * sourceWidth + arrayXPos[kkk];
            if (flagArray[indexkk] < PreparationMaskBand.CLOUD_SHADOW_FLAG && arrayBandA[kkk] <= minArrayBandA) {
                flagArray[indexkk] += PreparationMaskBand.CLOUD_SHADOW_FLAG;
            }
        }
        return smallShadowsNumber;
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

