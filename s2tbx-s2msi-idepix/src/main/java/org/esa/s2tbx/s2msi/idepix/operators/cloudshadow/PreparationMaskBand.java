package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import java.awt.*;

/**
 * todo: add comment
 *
 */
public class PreparationMaskBand {

    static final int INVALID_FLAG = 10000;
    static final int CLOUD_FLAG = 1000;
    static final int CLOUD_SHADOW_FLAG = 100;
    static final int LAND_FLAG = 10;
    static final int OCEAN_FLAG = 1;

    private PreparationMaskBand() {
    }

    public static void prepareMaskBand(int productWidth,
                                       int productHeight,
                                       Rectangle tileSourceRectangle,
                                       int[] flagArray,
                                       FlagDetector flagDetector) {

        int growingCloud = S2IdepixCloudShadowOp.GROWING_CLOUD;

        int sourceHeight = tileSourceRectangle.height;
        int sourceWidth = tileSourceRectangle.width;

        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {

                if ((tileSourceRectangle.x + i) >= 0 && (tileSourceRectangle.y + j) >= 0 &&
                        (tileSourceRectangle.x + i) < productWidth && (tileSourceRectangle.y + j) < productHeight) {

                    if (flagDetector.isInvalid(i, j)) {
                        flagArray[j * sourceWidth + i] = PreparationMaskBand.INVALID_FLAG;
                    } else {
                        if (flagDetector.isLand(i, j)) {
                            flagArray[j * (sourceWidth) + i] += PreparationMaskBand.LAND_FLAG;
                        } else {
                            flagArray[j * sourceWidth + i] += PreparationMaskBand.OCEAN_FLAG;
                        }

                        if (flagDetector.isCloud(i, j) || flagDetector.isCloudBuffer(i, j)) {
                            flagArray[j * (sourceWidth) + i] += PreparationMaskBand.CLOUD_FLAG;
                        }
                    }
                }
            }
        }
        int index;
        int temp;
        int[] preparedArray = new int[sourceHeight * sourceWidth];

        System.arraycopy(flagArray, 0, preparedArray, 0, sourceHeight * sourceWidth);


        for (int j = growingCloud; j < sourceHeight - growingCloud; j++) {
            for (int i = growingCloud; i < sourceWidth - growingCloud; i++) {
                index = j * (sourceWidth) + i;
                if (flagArray[index] >= PreparationMaskBand.CLOUD_FLAG && flagArray[index] <= PreparationMaskBand.INVALID_FLAG) {
                    for (int iw = -growingCloud; iw <= growingCloud; iw++) {
                        for (int jw = -growingCloud; jw <= growingCloud; jw++) {
                            temp = index + iw + jw * sourceWidth;
                            if (flagArray[temp] < PreparationMaskBand.CLOUD_FLAG) {
                                preparedArray[temp] = flagArray[temp] + PreparationMaskBand.CLOUD_FLAG;
                            }
                        }
                    }
                }
            }
        }
        System.arraycopy(preparedArray, 0, flagArray, 0, sourceHeight * sourceWidth);
    }
}
