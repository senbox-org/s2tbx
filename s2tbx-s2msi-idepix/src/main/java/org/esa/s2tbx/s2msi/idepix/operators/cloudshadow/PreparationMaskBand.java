package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import java.awt.*;

/**
 * todo: add comment
 *
 */
class PreparationMaskBand {

    static final int INVALID_FLAG = (int) (Math.pow(2, S2IdepixCloudShadowOp.F_INVALID)+ 0.1);
    static final int MOUNTAIN_SHADOW_FLAG = (int) (Math.pow(2, S2IdepixCloudShadowOp.F_MOUNTAIN_SHADOW)+ 0.1);
    static final int CLOUD_SHADOW_FLAG = (int) (Math.pow(2, S2IdepixCloudShadowOp.F_CLOUD_SHADOW)+ 0.1);
    static final int POTENTIAL_HAZE = (int) (Math.pow(2, S2IdepixCloudShadowOp.F_HAZE)+ 0.1);
    static final int CLOUD_FLAG = (int) (Math.pow(2, S2IdepixCloudShadowOp.F_CLOUD)+ 0.1);
    static final int LAND_FLAG = (int) (Math.pow(2, S2IdepixCloudShadowOp.F_LAND)+ 0.1);
    static final int WATER_FLAG = (int) (Math.pow(2, S2IdepixCloudShadowOp.F_WATER) + 0.1);
    static final int CLOUD_BUFFER_FLAG = (int) (Math.pow(2, S2IdepixCloudShadowOp.F_CLOUD_BUFFER) + 0.1);

    static void prepareMaskBand(int productWidth, int productHeight, Rectangle tileSourceRectangle, int[] flagArray,
                                       FlagDetector flagDetector) {

//        int growingCloud = S2IdepixCloudShadowOp.GROWING_CLOUD;

        int sourceHeight = tileSourceRectangle.height;
        int sourceWidth = tileSourceRectangle.width;

        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {

                if ((tileSourceRectangle.x + i) >= 0 && (tileSourceRectangle.y + j) >= 0 &&
                        (tileSourceRectangle.x + i) < productWidth && (tileSourceRectangle.y + j) < productHeight) {

                    if (flagDetector.isInvalid(i, j)) {
                        flagArray[j * sourceWidth + i] = INVALID_FLAG;
                    } else {
                        if (flagDetector.isLand(i, j)) {
                            flagArray[j * (sourceWidth) + i] += LAND_FLAG;
                        } else {
                            flagArray[j * sourceWidth + i] += WATER_FLAG;
                        }

//                        if (flagDetector.isCloud(i, j) || flagDetector.isCloudBuffer(i, j)) {
                        if (flagDetector.isCloud(i, j)) {
                            flagArray[j * (sourceWidth) + i] += CLOUD_FLAG;
                        }
                    }
                }
            }
        }
//        int index;
//        int temp;
//        int[] preparedArray = new int[sourceHeight * sourceWidth];
//
//        System.arraycopy(flagArray, 0, preparedArray, 0, sourceHeight * sourceWidth);


//        for (int j = growingCloud; j < sourceHeight - growingCloud; j++) {
//            for (int i = growingCloud; i < sourceWidth - growingCloud; i++) {
//                index = j * (sourceWidth) + i;
//                if (((flagArray[index] & CLOUD_FLAG) == CLOUD_FLAG) &&
//                        (!( (flagArray[index] & INVALID_FLAG) == INVALID_FLAG))) {
//                    for (int iw = -growingCloud; iw <= growingCloud; iw++) {
//                        for (int jw = -growingCloud; jw <= growingCloud; jw++) {
//                            temp = index + iw + jw * sourceWidth;
//                            if (!((flagArray[temp] & CLOUD_FLAG) == CLOUD_FLAG)) {
//                                preparedArray[temp] = flagArray[temp] + CLOUD_FLAG;
//                                preparedArray[temp] += CLOUD_BUFFER_FLAG;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        System.arraycopy(preparedArray, 0, flagArray, 0, sourceHeight * sourceWidth);
    }
}
