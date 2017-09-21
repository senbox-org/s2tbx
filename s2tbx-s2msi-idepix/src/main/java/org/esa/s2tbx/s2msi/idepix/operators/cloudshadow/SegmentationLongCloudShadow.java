package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import java.util.Arrays;

/**
 * todo: add comment
 */
class SegmentationLongCloudShadow {

    static final int NO_SHADOW = 0;

    static int computeLongCloudShadowID(int sourceWidth, int sourceHeight, int[] cloudShadowIDArray,
                                        int[][] cloudShadowIdBorderRectangle, int[] longCloudShadowIDArray,
                                        int cloudIndex) {


        int counterID = 1;
        int counterTable = NO_SHADOW;

        int sourceLength = sourceHeight * sourceWidth / 4;
        int index;
        int indexi;
        int leftNeighbour = sourceLength;
        int upperNeighbour = sourceLength;

        int[][] countLongShadowIDArray = new int[2][sourceLength + 1];
        for (int m = 0; m <= counterID; m++) {
            countLongShadowIDArray[0][m] = m;
            countLongShadowIDArray[1][m] = m;
        }

        // todo segmentation without border and after this applying own cloud-border processing
        Arrays.fill(longCloudShadowIDArray, sourceLength);

        for (int j = cloudShadowIdBorderRectangle[cloudIndex][2]; j <= cloudShadowIdBorderRectangle[cloudIndex][3]; j++) {
            for (int i = cloudShadowIdBorderRectangle[cloudIndex][0]; i <= cloudShadowIdBorderRectangle[cloudIndex][1]; i++) {
                index = j * sourceWidth + i;
                if (cloudShadowIDArray[index] == cloudIndex) {
                    if (i != 0) leftNeighbour = longCloudShadowIDArray[index - 1];
                    if (j != 0) upperNeighbour = longCloudShadowIDArray[index - sourceWidth];
                    if (leftNeighbour == sourceLength && upperNeighbour == sourceLength) {
                        longCloudShadowIDArray[index] = counterID;
                        counterID++;
                    } else {
                        longCloudShadowIDArray[index] = Math.min(leftNeighbour, upperNeighbour);
                        if (upperNeighbour != sourceLength && leftNeighbour != sourceLength && upperNeighbour != leftNeighbour) {
                            countLongShadowIDArray[0][counterTable] = leftNeighbour;
                            countLongShadowIDArray[1][counterTable] = upperNeighbour;
                            counterTable++;
                        }
                    }
                }
            }
        }


        int[] assignmentLongCloudArray = new int[counterID + 1];
        for (int m = 0; m <= counterID; m++) {
            assignmentLongCloudArray[m] = m;
        }

        int minValue;
        int maxValue;
        for (int kk = 0; kk < counterTable; kk++) {

            minValue = Math.min(countLongShadowIDArray[0][kk], countLongShadowIDArray[1][kk]);
            maxValue = Math.max(countLongShadowIDArray[0][kk], countLongShadowIDArray[1][kk]);
            assignmentLongCloudArray[maxValue] = minValue;

            for (int kkk = 0; kkk < counterTable; kkk++) {
                if (countLongShadowIDArray[0][kkk] == maxValue) countLongShadowIDArray[0][kkk] = minValue;
                if (countLongShadowIDArray[1][kkk] == maxValue) countLongShadowIDArray[1][kkk] = minValue;
            }

            for (int kkkk = 0; kkkk < counterID; kkkk++) {
                if (assignmentLongCloudArray[kkkk] == maxValue) assignmentLongCloudArray[kkkk] = minValue;
            }
        }


        for (int jj = 0; jj < sourceHeight; jj++) {
            for (int ii = 0; ii < sourceWidth; ii++) {
                indexi = jj * (sourceWidth) + ii;
                if (longCloudShadowIDArray[indexi] == sourceLength) {
                    longCloudShadowIDArray[indexi] = SegmentationLongCloudShadow.NO_SHADOW;
                } else {
                    longCloudShadowIDArray[indexi] = assignmentLongCloudArray[longCloudShadowIDArray[indexi]];
                }
            }
        }

        // todo one pixel wide rand is not considered
        // new cloud border  + added as cloud

//        System.arraycopy(cloudIdArray, 0, preparedArray, 0, sourceHeight * sourceWidth);
//        int temp;
//        for (int jjj = 1; jjj < sourceHeight - 1; jjj++) {
//            for (int iii = 1; iii < sourceWidth - 1; iii++) {
//                indexii = jjj * (sourceWidth) + iii;
//                dummy = counterID + 100;
//                if (cloudIdArray[indexii] == 0) {
//                    for (int iw = -1; iw <= 1; iw++) {
//                        for (int jw = -1; jw <= 1; jw++) {
//                           temp=indexii + iw + jw * sourceWidth;
//                            if ((iw == 0) && (jw == 0)) continue;
//                                if ((cloudIdArray[temp] < dummy) && (cloudIdArray[temp] > 0)) {
//                                    dummy = cloudIdArray[temp];
//                                }
//
//                        }
//                    }
//                    if (dummy != counterID + 100) {
//                        preparedArray[indexii] = dummy;
//                        flagArray[indexii] += PreparationMaskBand.CLOUD_FLAG;
//                    }
//                }
//            }
//        }
//        System.arraycopy(preparedArray, 0, cloudIdArray, 0, sourceHeight * sourceWidth);
        //System.out.printf("LongCloudShadowID counterTable  counterID:  %d %d %d\n", cloudIndex, counterTable,counterID);
        return Math.max(counterTable, counterID);
    }
}


