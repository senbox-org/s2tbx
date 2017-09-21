package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

/**
 * @author Tonio Fincke
 */
public class CloudShadowUtils {

    static int[] getPotentialMaxShadowPosition(double surfaceAlt, double sza, double saa,
                                                            double maxAlt,
                                                            int x0SourceArray, int y0SourceArray,
                                                            int x0TargetArray, int y0TargetArray, int productHeight,
                                                            int productWidth, int sourceHeight, int sourceWidth,
                                                            int targetHeight, int targetWidth,
                                                            double spatialResolution, boolean caseKeyBorder) {


        int X0;
        int Y0;
        int X1;
        int Y1;
        double factor;
        int[] path = new int[4];
        double iteratedeltaProjX;
        double iteratedeltaProjY;

        double deltaProjX = ((maxAlt - surfaceAlt) * Math.tan(sza) * Math.cos(saa - Math.PI / 2.)) / spatialResolution;
        double deltaProjY = ((maxAlt - surfaceAlt) * Math.tan(sza) * Math.sin(saa - Math.PI / 2.)) / spatialResolution;


        int divisor = (int) (Math.max(Math.abs(deltaProjX), Math.abs(deltaProjX)) * 2 + 0.5);
        if (caseKeyBorder) {
            X0 = x0TargetArray;
            Y0 = y0TargetArray;
            X1 = X0 + (int) (deltaProjX + 0.5);
            Y1 = Y0 + (int) (deltaProjY + 0.5);

            if (X1 > productWidth || X1 > targetWidth || Y1 > productHeight || Y1 > targetHeight || X1 < 0 || Y1 < 0) {
                iteratedeltaProjX = deltaProjX / divisor;
                iteratedeltaProjY = deltaProjY / divisor;
                for (int kk = 0; kk < (int) (maxAlt) - 1; kk++) {
                    factor = divisor - kk;
                    X1 = X0 + (int) (factor * iteratedeltaProjX + 0.5);
                    Y1 = Y0 + (int) (factor * iteratedeltaProjY + 0.5);
                    if (X1 < productWidth && X1 < targetWidth && Y1 < productHeight && Y1 < targetHeight && X1 > 0 && Y1 > 0) {
                        break;
                    } else {
                        X1 = X0;
                        Y1 = Y0;
                    }
                }
            }
        } else {
            X0 = x0SourceArray;
            Y0 = y0SourceArray;
            X1 = X0 + (int) (deltaProjX + 0.5);
            Y1 = Y0 + (int) (deltaProjY + 0.5);


            if (X1 > productWidth || X1 > sourceWidth || Y1 > productHeight || Y1 > sourceHeight || X1 < 0 || Y1 < 0) {
                iteratedeltaProjX = deltaProjX / divisor;
                iteratedeltaProjY = deltaProjY / divisor;
                for (int kk = 0; kk < (int) (maxAlt) - 1; kk++) {
                    factor = divisor - kk;
                    X1 = X0 + (int) (factor * iteratedeltaProjX + 0.5);
                    Y1 = Y0 + (int) (factor * iteratedeltaProjY + 0.5);
                    if (X1 < productWidth && X1 < sourceWidth && Y1 < productHeight && Y1 < sourceHeight && X1 > 0 && Y1 > 0) {
                        break;
                    } else {
                        X1 = X0;
                        Y1 = Y0;
                    }
                }
            }
        }

        path[0] = X0;
        path[1] = Y0;
        path[2] = X1;
        path[3] = Y1;

        return path;
    }

    static void getLinePoints(int x0, int y0, int x1, int y1, int sourceHeight, int sourceWidth,
                                      int targetHeight, int targetWidth, int pathWidth, int pathHeight,
                                      int[] linePointsArray, boolean caseKeyBorder) {

        int x0SourceProduct;
        int y0SourceProduct;
        int x1SourceProduct;
        int y1SourceProduct;
        if (caseKeyBorder) {
            if (x0 == x1 && y0 == y1) {
                x0SourceProduct = 0;
                y0SourceProduct = 0;
                x1SourceProduct = 0;
                y1SourceProduct = 0;
            } else if (x0 == x1 && y0 < y1) {
                x0SourceProduct = 0;
                y0SourceProduct = 0;
                x1SourceProduct = 0;
                y1SourceProduct = pathHeight;
            } else if (x0 == x1 && y0 > y1) {
                x0SourceProduct = 0;
                y0SourceProduct = targetHeight - 1;
                x1SourceProduct = 0;
                y1SourceProduct = targetHeight - 1 + pathHeight; // pathHeigth <0
            } else if (x0 < x1 && y0 == y1) {
                x0SourceProduct = 0;
                y0SourceProduct = 0;
                x1SourceProduct = pathWidth;
                y1SourceProduct = 0;
            } else if (x0 < x1 && y0 < y1) {
                x0SourceProduct = 0;
                y0SourceProduct = 0;
                x1SourceProduct = pathWidth;
                y1SourceProduct = pathHeight;
            } else if (x0 < x1 && y0 > y1) {
                x0SourceProduct = 0;
                y0SourceProduct = targetHeight - 1;
                x1SourceProduct = pathWidth;
                y1SourceProduct = targetHeight - 1 + pathHeight; // pathHeigth <0
            } else if (x0 > x1 && y0 == y1) {
                x0SourceProduct = targetWidth - 1;
                y0SourceProduct = 0;
                x1SourceProduct = targetWidth - 1 + pathWidth; // pathWidth <0
                y1SourceProduct = 0;
            } else if (x0 > x1 && y0 < y1) {
                x0SourceProduct = targetWidth - 1;
                y0SourceProduct = 0;
                x1SourceProduct = targetWidth - 1 + pathWidth; // pathWidth <0
                y1SourceProduct = pathHeight;
            } else {
                x0SourceProduct = targetWidth - 1;
                y0SourceProduct = targetHeight - 1;
                x1SourceProduct = targetWidth - 1 + pathWidth; // pathWidth <0
                y1SourceProduct = targetHeight - 1 + pathHeight; // pathHeigth <0
            }
        } else {
            if (x0 == x1 && y0 == y1) {
                x0SourceProduct = 0;
                y0SourceProduct = 0;
                x1SourceProduct = 0;
                y1SourceProduct = 0;
            } else if (x0 == x1 && y0 < y1) {
                x0SourceProduct = 0;
                y0SourceProduct = 0;
                x1SourceProduct = 0;
                y1SourceProduct = pathHeight;
            } else if (x0 == x1 && y0 > y1) {
                x0SourceProduct = 0;
                y0SourceProduct = sourceHeight - 1;
                x1SourceProduct = 0;
                y1SourceProduct = sourceHeight - 1 + pathHeight; // pathHeight <0
            } else if (x0 < x1 && y0 == y1) {
                x0SourceProduct = 0;
                y0SourceProduct = 0;
                x1SourceProduct = pathWidth;
                y1SourceProduct = 0;
            } else if (x0 < x1 && y0 < y1) {
                x0SourceProduct = 0;
                y0SourceProduct = 0;
                x1SourceProduct = pathWidth;
                y1SourceProduct = pathHeight;
            } else if (x0 < x1 && y0 > y1) {
                x0SourceProduct = 0;
                y0SourceProduct = sourceHeight - 1;
                x1SourceProduct = pathWidth;
                y1SourceProduct = sourceHeight - 1 + pathHeight; // pathHeigth <0
            } else if (x0 > x1 && y0 == y1) {
                x0SourceProduct = sourceWidth - 1;
                y0SourceProduct = 0;
                x1SourceProduct = sourceWidth - 1 + pathWidth; // pathWidth <0
                y1SourceProduct = 0;
            } else if (x0 > x1 && y0 < y1) {
                x0SourceProduct = sourceWidth - 1;
                y0SourceProduct = 0;
                x1SourceProduct = sourceWidth - 1 + pathWidth; // pathWidth <0
                y1SourceProduct = pathHeight;
            } else {
                x0SourceProduct = sourceWidth - 1;
                y0SourceProduct = sourceHeight - 1;
                x1SourceProduct = sourceWidth - 1 + pathWidth; // pathWidth <0
                y1SourceProduct = sourceHeight - 1 + pathHeight; // pathHeigth <0
            }
        }
        linePointsArray[0] = x0SourceProduct;
        linePointsArray[1] = y0SourceProduct;
        linePointsArray[2] = x1SourceProduct;
        linePointsArray[3] = y1SourceProduct;

    }

    static int[] getIndicesPathArray(int x0Center, int y0Center, int x1Center, int y1Center,
                                             int x0, int y0, int ii, int jj, int pathWidth, int pathHeight,
                                             int[] indicesPathArray) {

        int xi;
        int yj;
        int xPath;
        int yPath;

        if (x0Center == x1Center && y0Center == y1Center) {
            xPath = x0;
            yPath = y0;
            xi = 0;
            yj = 0;
        } else if (x0Center == x1Center && y0Center < y1Center) {
            xPath = x0;
            yPath = y0 + jj;
            xi = 0;
            yj = jj;
        } else if (x0Center == x1Center && y0Center > y1Center) {
            xPath = x0;
            yPath = y0 - jj;
            xi = 0;
            yj = pathHeight - jj;
        } else if (x0Center < x1Center && y0Center == y1Center) {
            xPath = x0 + ii;
            yPath = y0;
            xi = ii;
            yj = 0;
        } else if (x0Center < x1Center && y0Center < y1Center) {
            xPath = x0 + ii;
            yPath = y0 + jj;
            xi = ii;
            yj = jj;
        } else if (x0Center < x1Center && y0Center > y1Center) {
            xPath = x0 + ii;
            yPath = y0 - jj;
            xi = ii;
            yj = pathHeight - jj;
        } else if (x0Center > x1Center && y0Center == y1Center) {
            xPath = x0 - ii;
            yPath = y0;
            xi = pathWidth - ii;
            yj = 0;
        } else if (x0Center > x1Center && y0Center < y1Center) {
            xPath = x0 - ii;
            yPath = y0 + jj;
            xi = pathWidth - ii;
            yj = jj;
        } else {
            xPath = x0 - ii;
            yPath = y0 - jj;
            xi = pathWidth - ii;
            yj = pathHeight - jj;
        }

        indicesPathArray[0] = xPath;
        indicesPathArray[1] = yPath;
        indicesPathArray[2] = xi;
        indicesPathArray[3] = yj;

        return indicesPathArray;
    }

}
