package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
 * todo: add comment
 */
class FindContinuousAreas {

    private final int[] flagArray;

    FindContinuousAreas(int[] flagArray) {
        this.flagArray = flagArray;

        int N =0;
        for ( int i=0; i<flagArray.length; i++){
            if (flagArray[i]==1) N++;
        }
        //System.out.println("testN: "+N);
    }

    Map<Integer, List<Integer>> computeAreaID(int sourceWidth, int sourceHeight, int[] cloudIdArray, boolean useFlagBand) {

        int id = 0;
        Map<Integer, List<Integer>> output = new HashMap<>();

        //first pixel (top left)
        if (isTarget(0, useFlagBand)) {
            cloudIdArray[0] = ++id;
        }

        // first row (top row)
        for (int i = 1; i < sourceWidth; i++) {
            if (isTarget(i, useFlagBand)) {
                if (cloudIdArray[i - 1] != 0) {
                    cloudIdArray[i] = cloudIdArray[i - 1];
                } else {
                    cloudIdArray[i] = ++id;
                }
            }
        }

        for (int j = 1; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                int index = j * (sourceWidth) + i;
                if (isTarget(index, useFlagBand)) {
                    int leftNeighbour = cloudIdArray[index - 1];
                    int upperNeighbour = cloudIdArray[index - sourceWidth];
                    if (i == 0) {
                        if (upperNeighbour == 0) {
                            cloudIdArray[index] = ++id;
                        } else {
                            cloudIdArray[index] = upperNeighbour;
                        }
                    } else if (leftNeighbour == 0 && upperNeighbour == 0) {
                        cloudIdArray[index] = ++id;
                    } else if (upperNeighbour == 0 || leftNeighbour == upperNeighbour) {
                        cloudIdArray[index] = leftNeighbour;
                    } else if (leftNeighbour == 0) {
                        cloudIdArray[index] = upperNeighbour;
                    } else {
                        cloudIdArray[index] = upperNeighbour;
                    }
                }
            }
        }



        //adjusting the numbering by using the other direction (from bottom upwards).


        for (int j = sourceHeight-2; j >= 0; j--) {
            for (int i = sourceWidth-2; i >= 0; i--) {
                int index = j * (sourceWidth) + i;

                if (isTarget(index, useFlagBand)) {
                    int rightNeighbour = cloudIdArray[index + 1];

                    if (rightNeighbour > 0){
                        cloudIdArray[index] = rightNeighbour;
                    }

                }
            }
        }

        int count = 1;
        int k = 0;

        //reverting directions during the search repeatedly allows to identify spiraling areas as continuous areas.

        while (count >0 && k <10) {    //test in all four next neighbours, from bottom right to top left

            count = 0;

            for (int j = sourceHeight - 2; j > 0; j--) {
                for (int i = sourceWidth - 2; i > 0; i--) {
                    int index = j * (sourceWidth) + i;

                    if (isTarget(index, useFlagBand)) {
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
                    if (isTarget(index, useFlagBand)) {
                        int lowerNeighbour = cloudIdArray[index + sourceWidth];
                        int upperNeighbour = cloudIdArray[index - sourceWidth];
                        int leftNeighbour = cloudIdArray[index - 1];
                        int rightNeighbour = cloudIdArray[index + 1];

                        int thisMax = lowerNeighbour;
                        if (leftNeighbour > thisMax) thisMax = leftNeighbour;
                        if (rightNeighbour > thisMax) thisMax = rightNeighbour;
                        if (upperNeighbour > thisMax) thisMax = upperNeighbour;
                        int center = cloudIdArray[index];

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
                if (isTarget(index1, useFlagBand)){

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
                if (isTarget(index2, useFlagBand)){

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

        // setting up the output list.
        for (int j = 0; j < sourceHeight ; j++) {
            for (int i = 0; i < sourceWidth ; i++) {
                int index = j * (sourceWidth) + i;
                List<Integer> positions;
                if(cloudIdArray[index]>0){
                    if (output.containsKey(cloudIdArray[index])) {
                        positions = output.get(cloudIdArray[index]);
                    } else {
                        positions = new ArrayList<>();
                        output.put(cloudIdArray[index], positions);
                    }
                    positions.add(index);
                }
            }
        }

        return output;
    }


    private boolean isTarget(int index, boolean usePreparationBand) {
        if (usePreparationBand) {
            return ((flagArray[index] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                   (!((flagArray[index] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG));
        }
        else return (flagArray[index]==1);

    }
}


