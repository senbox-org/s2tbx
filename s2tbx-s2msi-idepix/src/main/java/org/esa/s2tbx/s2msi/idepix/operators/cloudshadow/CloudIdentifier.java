package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import java.util.HashMap;
import java.util.Map;

/**
 * todo: add comment
 */
class CloudIdentifier {

    static final int NO_SHADOW = 0;
    private final int[] flagArray;

    CloudIdentifier(int[] flagArray) {
        this.flagArray = flagArray;
    }

    int computeCloudID(int sourceWidth, int sourceHeight, int[] cloudIdArray) {

        int id = 0;
        Map<Integer, Integer> idToOffset = new HashMap<>();

        // todo segmentation without border and after this applying own cloud-border processing

        //first pixel
        if (isCloud(0)) {
            cloudIdArray[0] = ++id;
            idToOffset.put(id, 0);
        }

        // first row
        for (int i = 1; i < sourceWidth; i++) {
            if (isCloud(i)) {
                if (cloudIdArray[i - 1] != 0) {
                    cloudIdArray[i] = cloudIdArray[i - 1];
                } else {
                    cloudIdArray[i] = ++id;
                    idToOffset.put(id, i);
                }
            }
        }

        for (int j = 1; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                int index = j * (sourceWidth) + i;
                if (isCloud(index)) {
                    int leftNeighbour = cloudIdArray[index - 1];
                    int upperNeighbour = cloudIdArray[index - sourceWidth];
                    if (i == 0) {
                        if (upperNeighbour == 0) {
                            cloudIdArray[index] = ++id;
                            idToOffset.put(id, index);
                        } else {
                            cloudIdArray[index] = upperNeighbour;
                        }
                    } else if (leftNeighbour == 0 && upperNeighbour == 0) {
                        cloudIdArray[index] = ++id;
                        idToOffset.put(id, index);
                    } else if (upperNeighbour == 0 || leftNeighbour == upperNeighbour) {
                        cloudIdArray[index] = leftNeighbour;
                    } else if (leftNeighbour == 0) {
                        cloudIdArray[index] = upperNeighbour;
                    } else {
                        cloudIdArray[index] = upperNeighbour;
                        for (int k = idToOffset.get(leftNeighbour); k < index; k++) {
                            if (cloudIdArray[k] == leftNeighbour) {
                                cloudIdArray[k] = upperNeighbour;
                            }
                        }
                        idToOffset.remove(leftNeighbour);
                    }
                }
            }
        }
        return idToOffset.size();
    }

    private boolean isCloud(int index) {
        return ((flagArray[index] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                (!((flagArray[index] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG));
    }
}


