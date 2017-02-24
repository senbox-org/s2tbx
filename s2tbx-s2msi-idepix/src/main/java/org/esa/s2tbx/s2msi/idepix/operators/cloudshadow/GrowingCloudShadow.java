package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

/**
 * todo: add comment
 *
 */
class GrowingCloudShadow {

    private GrowingCloudShadow() {
    }

    static void computeCloudShadowBorder(int sourceWidth,
                                         int sourceHeight,
                                         int[] flagArray) {

        int[] preparedArray = new int[flagArray.length];
        int index;
        int indexij;
        int jjj;
        int iii;


        System.arraycopy(flagArray, 0, preparedArray, 0, sourceHeight * sourceWidth);
        for (int j = S2IdepixCloudShadowOp.searchBorderRadius; j < sourceHeight - S2IdepixCloudShadowOp.searchBorderRadius; j++) {
            for (int i = S2IdepixCloudShadowOp.searchBorderRadius; i < sourceWidth - S2IdepixCloudShadowOp.searchBorderRadius; i++) {
                index = j * (sourceWidth) + i;
                if (flagArray[index] == 101 || flagArray[index] == 110) {
                    for (int jj = -1; jj <= 1; jj++) {
                        for (int ii = -1; ii <= 1; ii++) {
                            jjj = j + jj;
                            iii = i + ii;
                            if (jjj >= S2IdepixCloudShadowOp.searchBorderRadius
                                    && jjj < sourceHeight - S2IdepixCloudShadowOp.searchBorderRadius
                                    && iii >= S2IdepixCloudShadowOp.searchBorderRadius
                                    && iii < sourceWidth - S2IdepixCloudShadowOp.searchBorderRadius) {
                                indexij = jjj * (sourceWidth) + iii;
                                if (flagArray[indexij] == PreparationMaskBand.OCEAN_FLAG) {
                                    preparedArray[indexij] = 500;
                                }
                            }
                        }
                    }
                }
            }
        }

        System.arraycopy(preparedArray, 0, flagArray, 0, sourceHeight * sourceWidth);
    }
}