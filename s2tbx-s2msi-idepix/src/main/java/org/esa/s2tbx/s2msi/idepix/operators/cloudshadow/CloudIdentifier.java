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

        //first pixel (top left)
        if (isCloud(0)) {
            cloudIdArray[0] = ++id;
            idToOffset.put(id, 0);
        }

        // first row (top row)
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



        //adjusting the numbering by using the other direction (from bottom upwards).


        for (int j = sourceHeight-2; j >= 0; j--) {
            for (int i = sourceWidth-2; i >= 0; i--) {
                int index = j * (sourceWidth) + i;

                if (isCloud(index)) {
                    int rightNeighbour = cloudIdArray[index + 1];

                    if (rightNeighbour > 0){
                        cloudIdArray[index] = rightNeighbour;
                    }

                }
            }
        }

        int count = 1;
        int k = 0;
        //for (int k=0; k <10; k++) {
        while (count >0 && k <10) {    //test in all four next neighbours, from bottom right to top left

            count = 0;

            for (int j = sourceHeight - 2; j > 0; j--) {
                for (int i = sourceWidth - 2; i > 0; i--) {
                    int index = j * (sourceWidth) + i;

                    if (isCloud(index)) {
                        int lowerNeighbour = cloudIdArray[index + sourceWidth];
                        int upperNeighbour = cloudIdArray[index - sourceWidth];
                        int leftNeighbour = cloudIdArray[index - 1];
                        int rightNeighbour = cloudIdArray[index + 1];
                        int center = cloudIdArray[index];

                        int thisMax = lowerNeighbour;
                        if (leftNeighbour > thisMax) thisMax = leftNeighbour;
                        if (rightNeighbour > thisMax) thisMax = rightNeighbour;
                        if (upperNeighbour > thisMax) thisMax = upperNeighbour;
                        if (center > thisMax) thisMax = center;

                        //if (lowerNeighbour > 0 || upperNeighbour > 0 || leftNeighbour > 0 || rightNeighbour > 0) {
                        if(center < thisMax) {
                            cloudIdArray[index] = thisMax;
                            count++;
                        } else if (lowerNeighbour > 0 && lowerNeighbour < thisMax) {
                            cloudIdArray[index + sourceWidth] = thisMax;
                            count++;
                        } else if (upperNeighbour > 0 && upperNeighbour < thisMax) {
                            cloudIdArray[index - sourceWidth] = thisMax;
                            count++;
                        } else if (leftNeighbour > 0 && leftNeighbour < thisMax) {
                            cloudIdArray[index - 1] = thisMax;
                            count++;
                        } else if (rightNeighbour > 0 && rightNeighbour < thisMax) {
                            cloudIdArray[index + 1] = thisMax;
                            count++;
                        }


                    }
                }
            }

            //test in all four next neighbours, from top left to bottom right (reversed to the iterations before)
            for (int j = 1; j < sourceHeight - 1; j++) {
                for (int i = 1; i < sourceWidth - 1; i++) {
                    int index = j * (sourceWidth) + i;
                    if (isCloud(index)) {
                        int lowerNeighbour = cloudIdArray[index + sourceWidth];
                        int upperNeighbour = cloudIdArray[index - sourceWidth];
                        int leftNeighbour = cloudIdArray[index - 1];
                        int rightNeighbour = cloudIdArray[index + 1];

                        int thisMax = lowerNeighbour;
                        if (leftNeighbour > thisMax) thisMax = leftNeighbour;
                        if (rightNeighbour > thisMax) thisMax = rightNeighbour;
                        if (upperNeighbour > thisMax) thisMax = upperNeighbour;
                        int center = cloudIdArray[index];

                        //if (lowerNeighbour > 0 || upperNeighbour > 0 || leftNeighbour > 0 || rightNeighbour > 0) {
                        if(center < thisMax) {
                            cloudIdArray[index] = thisMax;
                            count++;
                        } else if (lowerNeighbour > 0 && lowerNeighbour < thisMax) {
                            cloudIdArray[index + sourceWidth] = thisMax;
                            count++;
                        } else if (upperNeighbour > 0 && upperNeighbour < thisMax) {
                            cloudIdArray[index - sourceWidth] = thisMax;
                            count++;
                        } else if (leftNeighbour > 0 && leftNeighbour < thisMax) {
                            cloudIdArray[index - 1] = thisMax;
                            count++;
                        } else if (rightNeighbour > 0 && rightNeighbour < thisMax) {
                            cloudIdArray[index + 1] = thisMax;
                            count++;
                        }


                    }
                }
            }

            //correct edges; at i=0 and i=sourceWidth-1
            for (int j = 0; j < sourceHeight; j++) {
                int index1 = j * (sourceWidth) ;
                int index2 = j * (sourceWidth) + sourceWidth-1 ;
                if (isCloud(index1)){

                    int rightNeighbour = cloudIdArray[index1 + 1];
                    int center = cloudIdArray[index1];

                    if(j==0){
                        int lowerNeighbour = cloudIdArray[index1 + sourceWidth];

                        int thisMax = lowerNeighbour;
                        if (rightNeighbour > thisMax) thisMax = rightNeighbour;
                        if(center > thisMax) thisMax = center;

                        if(center < thisMax) {
                            cloudIdArray[index1] = thisMax;
                            count++;
                        } else if (rightNeighbour > 0 && rightNeighbour < thisMax) {
                            cloudIdArray[index1 + 1] = thisMax;
                            count++;
                        } else if (lowerNeighbour > 0 && lowerNeighbour < thisMax) {
                            cloudIdArray[index1 + sourceWidth] = thisMax;
                            count++;
                        }
                    } else if(j==sourceHeight-1){
                        int upperNeighbour = cloudIdArray[index1 - sourceWidth];

                        int thisMax = upperNeighbour;
                        if (rightNeighbour > thisMax) thisMax = rightNeighbour;
                        if(center > thisMax) thisMax = center;

                        if(center < thisMax) {
                            cloudIdArray[index1] = thisMax;
                            count++;
                        } else if (rightNeighbour > 0 && rightNeighbour < thisMax) {
                            cloudIdArray[index1 + 1] = thisMax;
                            count++;
                        } else if (upperNeighbour > 0 && upperNeighbour < thisMax) {
                            cloudIdArray[index1 - sourceWidth] = thisMax;
                            count++;
                        }
                    } else {
                        int lowerNeighbour = cloudIdArray[index1 + sourceWidth];
                        int upperNeighbour = cloudIdArray[index1 - sourceWidth];

                        int thisMax = lowerNeighbour;
                        if (rightNeighbour > thisMax) thisMax = rightNeighbour;
                        if (upperNeighbour > thisMax) thisMax = upperNeighbour;
                        if(center > thisMax) thisMax = center;

                        if(center < thisMax) {
                            cloudIdArray[index1] = thisMax;
                            count++;
                        } else if (rightNeighbour > 0 && rightNeighbour < thisMax) {
                            cloudIdArray[index1 + 1] = thisMax;
                            count++;
                        } else if (lowerNeighbour > 0 && lowerNeighbour < thisMax) {
                            cloudIdArray[index1 + sourceWidth] = thisMax;
                            count++;
                        } else if (upperNeighbour > 0 && upperNeighbour < thisMax) {
                            cloudIdArray[index1 - sourceWidth] = thisMax;
                            count++;
                        }
                    }
                }
                if (isCloud(index2)){

                    int leftNeighbour = cloudIdArray[index2 - 1];
                    int center = cloudIdArray[index2];

                    if(j==0){
                        int lowerNeighbour = cloudIdArray[index2 + sourceWidth];

                        int thisMax = lowerNeighbour;
                        if (leftNeighbour > thisMax) thisMax = leftNeighbour;
                        if(center > thisMax) thisMax = center;

                        if(center < thisMax) {
                            cloudIdArray[index2] = thisMax;
                            count++;
                        } else if (leftNeighbour > 0 && leftNeighbour < thisMax) {
                            cloudIdArray[index2 - 1] = thisMax;
                            count++;
                        } else if (lowerNeighbour > 0 && lowerNeighbour < thisMax) {
                            cloudIdArray[index2 + sourceWidth] = thisMax;
                            count++;
                        }
                    } else if(j==sourceHeight-1){
                        int upperNeighbour = cloudIdArray[index2 - sourceWidth];

                        int thisMax = upperNeighbour;
                        if (leftNeighbour > thisMax) thisMax = leftNeighbour;
                        if(center > thisMax) thisMax = center;

                        if(center < thisMax) {
                            cloudIdArray[index2] = thisMax;
                            count++;
                        } else if (leftNeighbour > 0 && leftNeighbour < thisMax) {
                            cloudIdArray[index2 - 1] = thisMax;
                            count++;
                        } else if (upperNeighbour > 0 && upperNeighbour < thisMax) {
                            cloudIdArray[index2 - sourceWidth] = thisMax;
                            count++;
                        }
                    } else {
                        int lowerNeighbour = cloudIdArray[index2 + sourceWidth];
                        int upperNeighbour = cloudIdArray[index2 - sourceWidth];

                        int thisMax = lowerNeighbour;
                        if (leftNeighbour > thisMax) thisMax = leftNeighbour;
                        if (upperNeighbour > thisMax) thisMax = upperNeighbour;
                        if(center > thisMax) thisMax = center;

                        if(center < thisMax) {
                            cloudIdArray[index2] = thisMax;
                            count++;
                        } else if (leftNeighbour > 0 && leftNeighbour < thisMax) {
                            cloudIdArray[index2 - 1] = thisMax;
                            count++;
                        } else if (lowerNeighbour > 0 && lowerNeighbour < thisMax) {
                            cloudIdArray[index2 + sourceWidth] = thisMax;
                            count++;
                        } else if (upperNeighbour > 0 && upperNeighbour < thisMax) {
                            cloudIdArray[index2 - sourceWidth] = thisMax;
                            count++;
                        }
                    }
                }
            }


            k++;
            /*System.out.print(k );
            System.out.print(' ' );
            System.out.println(count );*/
        }


        return idToOffset.size();
    }


    private boolean isCloud(int index) {
        return (((flagArray[index] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)
                //|| ((flagArray[index] & PreparationMaskBand.CLOUD_BUFFER_FLAG) == PreparationMaskBand.CLOUD_BUFFER_FLAG)
                )&&
                   (!((flagArray[index] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG));
    }
}


