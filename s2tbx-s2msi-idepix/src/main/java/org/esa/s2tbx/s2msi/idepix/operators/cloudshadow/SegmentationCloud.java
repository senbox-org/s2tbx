package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import java.util.Arrays;

/**
 * todo: add comment
 *
 */
class SegmentationCloud {

    static final int NO_SHADOW = 0;
    private static final int NO_CLOUD = 0;

    static int computeCloudID(int sourceWidth,
                                     int sourceHeight,
                                     int[] flagArray,
                                     int[] cloudIdArray) {
        int counterID = 1;

        int counterTable = NO_CLOUD;

        int sourceLength = sourceHeight * sourceWidth;
        int index;
        int indexi;
        int leftNeighbour = sourceLength;
        int upperNeighbour = sourceLength;

        int[][] countIDArray = new int[2][sourceLength + 1];
        for (int m = 0; m <= counterID; m++) {
            countIDArray[0][m] = m;
            countIDArray[1][m] = m;
        }

        // todo segmentation without border and after this applying own cloud-border processing
        Arrays.fill(cloudIdArray, sourceLength);

        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                index = j * (sourceWidth) + i;

                if (((flagArray[index] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                        (!((flagArray[index] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG))) {

                    if (i != 0) leftNeighbour = cloudIdArray[index - 1];
                    if (j != 0) upperNeighbour = cloudIdArray[index - sourceWidth];
                    if (leftNeighbour == sourceLength && upperNeighbour == sourceLength) {
                        cloudIdArray[index] = counterID;
                        counterID++;
                    } else {
                        cloudIdArray[index] = Math.min(leftNeighbour, upperNeighbour);
                        if (upperNeighbour != sourceLength && leftNeighbour != sourceLength && upperNeighbour != leftNeighbour) {
                            countIDArray[0][counterTable] = leftNeighbour;
                            countIDArray[1][counterTable] = upperNeighbour;
                            counterTable++;
                        }
                    }
                }
            }
        }

        int[] assignmentArray = new int[counterID + 1];
        for (int m = 0; m <= counterID; m++) {
            assignmentArray[m] = m;
        }

        int minValue;
        int maxValue;
        for (int kk = 0; kk < counterTable; kk++) {

            minValue = Math.min(countIDArray[0][kk], countIDArray[1][kk]);
            maxValue = Math.max(countIDArray[0][kk], countIDArray[1][kk]);
            assignmentArray[maxValue] = minValue;

            for (int kkk = 0; kkk < counterTable; kkk++) {
                if (countIDArray[0][kkk] == maxValue) countIDArray[0][kkk] = minValue;
                if (countIDArray[1][kkk] == maxValue) countIDArray[1][kkk] = minValue;
            }

            for (int kkkk = 0; kkkk < counterID; kkkk++) {
                if (assignmentArray[kkkk] == maxValue) assignmentArray[kkkk] = minValue;
            }
        }


        for (int jj = 0; jj < sourceHeight; jj++) {
            for (int ii = 0; ii < sourceWidth; ii++) {
                indexi = jj * (sourceWidth) + ii;
                if (cloudIdArray[indexi] == sourceLength) {
                    cloudIdArray[indexi] = SegmentationCloud.NO_SHADOW;
                } else {
                    cloudIdArray[indexi] = assignmentArray[cloudIdArray[indexi]];
                }
            }
        }

        return Math.max(counterTable,counterID);
    }
}


