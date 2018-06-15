package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

class CloudBulkShifter {

    private double[] sumValue;
    private int[] N;
    private int NCloudLand;
    private int NCloudWater;
    private int NValidPixel;

    private double[][] meanValuesPath;

    void shiftCloudBulkAlongCloudPathType(Rectangle sourceRectangle, Rectangle targetRectangle,
                                                 float sourceSunAzimuth, float[][] sourceBands,
                                                 int[] flagArray, Point2D[] cloudPath) {
        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;

        //search rectangle: tile + extension in certain directions
        int xOffset = 0;
        int yOffset = 0;
        if (sourceSunAzimuth < 90) {
            xOffset = targetRectangle.x - sourceRectangle.x;
        } else if (sourceSunAzimuth < 180) {
            xOffset = targetRectangle.x - sourceRectangle.x;
            yOffset = targetRectangle.y - sourceRectangle.y;
        } else if (sourceSunAzimuth < 270) {
            yOffset = targetRectangle.y - sourceRectangle.y;
        }

        meanValuesPath = new double[3][cloudPath.length];

        sumValue = new double[3]; // Positions: 0: all, 1: only land, 2: only water
        N = new int[3];
        NCloudLand = 0;
        NCloudWater = 0;
        NValidPixel = 0;

        for (int path_i = 1; path_i < cloudPath.length; path_i++) {
            //collect index per cloudID and cloudpath step.
            // - setup index of water or land pixels at cloud path step.
            // just like identifyPotentialCloudShadow, but without cloudPath iteration. This is fixed to the path_i step.
            for (int x0 = xOffset; x0 < sourceWidth; x0++) {
                for (int y0 = yOffset; y0 < sourceHeight; y0++) {
                    //each pixel needs to be tested, whether it is cloud or not.
                    //based on identifyPotentialCloudShadow()
                    simpleShiftedCloudMask_and_meanRefl_alongPath(x0, y0, sourceHeight, sourceWidth, cloudPath, path_i,
                            flagArray, sourceBands[1]);
                }
            }
            for (int j = 0; j < 3; j++) {
                meanValuesPath[j][path_i] = sumValue[j] / N[j];
            }
        }
    }

    private void simpleShiftedCloudMask_and_meanRefl_alongPath(int x0, int y0, int height, int width,
                                                               Point2D[] cloudPath, int end_path_i,
                                                               int[] flagArray, float[] sourceBand) {
        int index0 = y0 * width + x0;
        //start from a cloud pixel, otherwise stop.
        if (!((flagArray[index0] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {
            NValidPixel++;
        }
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }
        if (((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            if (((flagArray[index0] & PreparationMaskBand.LAND_FLAG) == PreparationMaskBand.LAND_FLAG)) {
                NCloudLand++;
            }
            if (((flagArray[index0] & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG)) {
                NCloudWater++;
            }
        }
        for (int i = end_path_i; i < end_path_i + 1; i++) {
            int x1 = x0 + (int) cloudPath[i].getX();
            int y1 = y0 + (int) cloudPath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;
            if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {

                if (!((flagArray[index1] & PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG) == PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG)) {
                    flagArray[index1] += PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG;
                    this.sumValue[0] += sourceBand[index1];
                    this.N[0] += 1;

                    if (((flagArray[index1] & PreparationMaskBand.LAND_FLAG) == PreparationMaskBand.LAND_FLAG)) {
                        this.sumValue[1] += sourceBand[index1];
                        this.N[1] += 1;
                    }
                    if (((flagArray[index1] & PreparationMaskBand.WATER_FLAG) == PreparationMaskBand.WATER_FLAG)) {
                        this.sumValue[2] += sourceBand[index1];
                        this.N[2] += 1;
                    }
                }
            }
        }
    }

    static void setTileShiftedCloudBulk(Rectangle sourceRectangle,
                                        Rectangle targetRectangle,
                                        float sourceSunAzimuth,
                                        int[] flagArray, Point2D[] cloudPath, int darkIndex) {
        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;

        //search rectangle: tile + extension in certain directions
        int xOffset = 0;
        int yOffset = 0;
        if (sourceSunAzimuth < 90) {
            xOffset = targetRectangle.x - sourceRectangle.x;
        } else if (sourceSunAzimuth < 180) {
            xOffset = targetRectangle.x - sourceRectangle.x;
            yOffset = targetRectangle.y - sourceRectangle.y;
        } else if (sourceSunAzimuth < 270) {
            yOffset = targetRectangle.y - sourceRectangle.y;
        }
        for (int x0 = xOffset; x0 < sourceWidth; x0++) {
            for (int y0 = yOffset; y0 < sourceHeight; y0++) {
                setShiftedCloudBULK(x0, y0, sourceHeight, sourceWidth, cloudPath, flagArray, darkIndex);
                setPotentialCloudShadowMask(x0, y0, sourceHeight, sourceWidth, cloudPath, flagArray);

            }
        }
    }

    private static void setShiftedCloudBULK(int x0, int y0, int height, int width, Point2D[] cloudPath,
                                            int[] flagArray, int darkIndex) {
        int index0 = y0 * width + x0;
        //start from a cloud pixel, otherwise stop.
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }


        int x1 = x0 + (int) cloudPath[darkIndex].getX();
        int y1 = y0 + (int) cloudPath[darkIndex].getY();
        if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
            //break; only necessary in the for-loop, which is no longer used.
            return;
        }
        int index1 = y1 * width + x1;

        if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {


            if (!((flagArray[index1] & PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG) == PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG)) {
                flagArray[index1] += PreparationMaskBand.SHIFTED_CLOUD_SHADOW_FLAG;
            }
        }
    }


    private static void setPotentialCloudShadowMask(int x0, int y0, int height, int width, Point2D[] cloudPath,
                                                    int[] flagArray) {
        int index0 = y0 * width + x0;
        //start from a cloud pixel, otherwise stop.
        if (!((flagArray[index0] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }
        int x1 = x0 + (int) cloudPath[1].getX();
        int y1 = y0 + (int) cloudPath[1].getY();
        int x2 = x0 + (int) cloudPath[2].getX();
        int y2 = y0 + (int) cloudPath[2].getY();
        // cloud edge is used at least 2 pixels deep, otherwise gaps occur due to orientation of cloud edge and cloud path.
        // (Moire-Effect)
        if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0 || x2 >= width || y2 >= height || x2 < 0 || y2 < 0 ||
                ((flagArray[y1 * width + x1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG &&
                        (flagArray[y2 * width + x2] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
            return;
        }
        for (int i = 1; i < cloudPath.length; i++) {
            x1 = x0 + (int) cloudPath[i].getX();
            y1 = y0 + (int) cloudPath[i].getY();
            if (x1 >= width || y1 >= height || x1 < 0 || y1 < 0) {
                break;
            }
            int index1 = y1 * width + x1;
            if (!((flagArray[index1] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG) &&
                    !((flagArray[index1] & PreparationMaskBand.INVALID_FLAG) == PreparationMaskBand.INVALID_FLAG)) {

                if (!((flagArray[index1] & PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG) == PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG)) {
                    flagArray[index1] += PreparationMaskBand.POTENTIAL_CLOUD_SHADOW_FLAG;
                }
            }
        }
    }

    double[][] getMeanReflectanceAlongPath() {
        return meanValuesPath;
    }

    int getNCloudOverWater() {
        return NCloudWater;
    }

    int getNCloudOverLand() {
        return NCloudLand;
    }

    int getNValidPixel() {
        return NValidPixel;
    }

}
