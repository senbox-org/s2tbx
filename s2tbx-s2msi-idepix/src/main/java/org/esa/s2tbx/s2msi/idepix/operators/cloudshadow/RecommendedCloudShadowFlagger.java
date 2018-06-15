package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;


import java.awt.Rectangle;

class RecommendedCloudShadowFlagger {

    static void setRecommendedCloudShadowFlag(int bestOffset, int[] flagArray, Rectangle sourceRectangle) {
        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        if (bestOffset > 0) {
            for (int x0 = 0; x0 < sourceWidth; x0++) {
                for (int y0 = 0; y0 < sourceHeight; y0++) {
                    int index = y0 * sourceWidth + x0;
                    //combine SHIFTED_CLOUD_SHADOW_IN_GAPS with CLOUD_SHADOW_COMB
                    if (!((flagArray[index] & PreparationMaskBand.RECOMMENDED_CLOUD_SHADOW_FLAG) == PreparationMaskBand.RECOMMENDED_CLOUD_SHADOW_FLAG) &&
                            !((flagArray[index] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                            !((flagArray[index] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG) &&
                            ((flagArray[index] & PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG) == PreparationMaskBand.CLOUD_SHADOW_COMB_FLAG ||
                                    (flagArray[index] & PreparationMaskBand.SHIFTED_CLOUD_SHADOW_GAPS_FLAG) == PreparationMaskBand.SHIFTED_CLOUD_SHADOW_GAPS_FLAG)
                            ) {
                        flagArray[index] += PreparationMaskBand.RECOMMENDED_CLOUD_SHADOW_FLAG;
                    }

                }
            }
        } else {
            for (int x0 = 0; x0 < sourceWidth; x0++) {
                for (int y0 = 0; y0 < sourceHeight; y0++) {
                    int index = y0 * sourceWidth + x0;
                    //if bestOffset==0, no shifted information is available. The recommended cloud shadow is the clustered shadow.
                    if (!((flagArray[index] & PreparationMaskBand.RECOMMENDED_CLOUD_SHADOW_FLAG) == PreparationMaskBand.RECOMMENDED_CLOUD_SHADOW_FLAG) &&
                            !((flagArray[index] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                            !((flagArray[index] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG) &&
                            ((flagArray[index] & PreparationMaskBand.CLOUD_SHADOW_FLAG) == PreparationMaskBand.CLOUD_SHADOW_FLAG)
                            ) {
                        flagArray[index] += PreparationMaskBand.RECOMMENDED_CLOUD_SHADOW_FLAG;
                    }

                }
            }
        }
    }


}
