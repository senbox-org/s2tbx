package org.esa.s2tbx.coregistration;

import javax.media.jai.InterpolationBilinear;

/**
 * Utility class for matrix-based operations.
 */
public class MatrixUtils {

    public static float max(float[][] v) {
        float m = v[0][0];
        for (int i = 1; i < v.length; i++) {
            for (int j = 0; j < v[0].length; j++) {
                if (m < v[i][j]) {
                    m = v[i][j];
                }
            }
        }
        return m;
    }

    public static float min(float[][] v) {
        float m = v[0][0];
        for (int i = 1; i < v.length; i++) {
            for (int j = 0; j < v[0].length; j++) {
                if (m > v[i][j]) {
                    m = v[i][j];
                }
            }
        }
        return m;
    }

    public static int max(int[] v) {
        int m = v[0];
        for (int i = 1; i < v.length; i++) {
            if (m < v[i]) {
                m = v[i];
            }
        }
        return m;
    }

    public static int min(int[] v) {
        int m = v[0];
        for (int i = 1; i < v.length; i++) {
            if (m > v[i]) {
                m = v[i];
            }
        }
        return m;
    }

    public static float[][] rank_sup(float[][] I, int rank) {
        int nl = I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];
        float[][] tmp;// = new float[nl][nc];

        //int range_rad = max(rad)-min(rad);
        for (int i = 0; i < rank; i++) {//range(1,rad+1)
            for (int j = -1; j < rank; j++) {//range(rad+1)
                //tmp = np.concatenate([I[i:,j:], np.zeros([nl-i,j])], axis=1)
                //tmp = np.concatenate([tmp,np.zeros([i,nc])],axis = 0)

                tmp = new float[nl][nc];
                for (int i1 = i + 1; i1 < nl; i1++) {
                    for (int j1 = j + 1; j1 < nc; j1++) {

                        int iw = i1 - i - 1;
                        int jw = j1 - j - 1;
                        tmp[iw][jw] = I[i1][j1];

                        if (tmp[iw][jw] > I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }
                    }
                }

                tmp = new float[nl][nc];
                for (int i1 = 0; i1 < nl - i - 1; i1++) {
                    for (int j1 = 0; j1 < nc - j - 1; j1++) {

                        int iw = i1 + i + 1;
                        int jw = j1 + j + 1;
                        tmp[iw][jw] = I[i1][j1];

                        if (tmp[iw][jw] > I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }
                    }
                }
            }
        }
        for (int i = -1; i < rank; i++) {//range(1,rad+1)
            for (int j = 0; j < rank; j++) {//range(rad+1)

                tmp = new float[nl][nc];
                for (int i1 = 0; i1 < nl - i - 1; i1++) {
                    for (int j1 = j + 1; j1 < nc; j1++) {

                        int iw = i1 + i + 1;
                        int jw = j1 - j - 1;
                        tmp[iw][jw] = I[i1][j1];

                        if (tmp[iw][jw] > I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }
                    }
                }

                tmp = new float[nl][nc];
                for (int i1 = i + 1; i1 < nl; i1++) {
                    for (int j1 = 0; j1 < nc - j - 1; j1++) {

                        int iw = i1 - i - 1;
                        int jw = j1 + j + 1;
                        tmp[iw][jw] = I[i1][j1];

                        if (tmp[iw][jw] > I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }
                    }
                }
            }
        }
        return R;
    }

    private static void printMatrix(float[][] tmp) {
        /*
        System.out.println(tmp.length+" X "+tmp[0].length);
        for(int i=0;i<tmp.length;i++){
            for (int j=0;j<tmp[i].length;j++){
                System.out.print(tmp[i][j]+"  ");
            }
            System.out.println();
        }*/
    }

    public static float[][] rank_inf(float[][] I, int rank) {
        int nl = I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];
        float[][] tmp = new float[nl][nc];
        for (int i = 0; i < nl; i++) {
            for (int j = 0; j < nc; j++) {
                R[i][j] = 0;
            }
        }
        //int range_rad = max(rad)-min(rad);
        for (int i = 0; i < rank; i++) {//range(1,rad+1)
            for (int j = -1; j < rank; j++) {//range(rad+1)
                //tmp = np.concatenate([I[i:,j:], np.zeros([nl-i,j])], axis=1)
                //tmp = np.concatenate([tmp,np.zeros([i,nc])],axis = 0)

                tmp = new float[nl][nc];
                for (int i1 = i + 1; i1 < nl; i1++) {
                    for (int j1 = j + 1; j1 < nc; j1++) {

                        int iw = i1 - i - 1;
                        int jw = j1 - j - 1;
                        tmp[iw][jw] = I[i1][j1];

                        /*if (tmp[iw][jw] < I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }*/
                    }
                }
                for (int i1 = 0; i1 < nl; i1++) {
                    for (int j1 = 0; j1 < nc; j1++) {
                        if (tmp[i1][j1] < I[i1][j1]) {
                            R[i1][j1] = R[i1][j1] + 1;
                        }
                    }
                }
                //System.out.println("i="+i+"   j="+j);
                printMatrix(tmp);

                tmp = new float[nl][nc];
                for (int i1 = 0; i1 < nl - i - 1; i1++) {
                    for (int j1 = 0; j1 < nc - j - 1; j1++) {

                        int iw = i1 + i + 1;
                        int jw = j1 + j + 1;
                        tmp[iw][jw] = I[i1][j1];

                        /*if (tmp[iw][jw] < I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }*/
                    }
                }

                for (int i1 = 0; i1 < nl; i1++) {
                    for (int j1 = 0; j1 < nc; j1++) {
                        if (tmp[i1][j1] < I[i1][j1]) {
                            R[i1][j1] = R[i1][j1] + 1;
                        }
                    }
                }
                //printMatrix(tmp);
            }
        }
        for (int i = -1; i < rank; i++) {//range(1,rad+1)
            for (int j = 0; j < rank; j++) {//range(rad+1)

                tmp = new float[nl][nc];
                for (int i1 = 0; i1 < nl - i - 1; i1++) {
                    for (int j1 = j + 1; j1 < nc; j1++) {

                        int iw = i1 + i + 1;
                        int jw = j1 - j - 1;
                        tmp[iw][jw] = I[i1][j1];

                        /*if (tmp[iw][jw] < I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }*/
                    }
                }

                for (int i1 = 0; i1 < nl; i1++) {
                    for (int j1 = 0; j1 < nc; j1++) {
                        if (tmp[i1][j1] < I[i1][j1]) {
                            R[i1][j1] = R[i1][j1] + 1;
                        }
                    }
                }
                //System.out.println("i="+i+"   j="+j);
                //printMatrix(tmp);

                tmp = new float[nl][nc];
                for (int i1 = i + 1; i1 < nl; i1++) {
                    for (int j1 = 0; j1 < nc - j - 1; j1++) {

                        int iw = i1 - i - 1;
                        int jw = j1 + j + 1;
                        tmp[iw][jw] = I[i1][j1];

                        /*if (tmp[iw][jw] < I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }*/
                    }
                }

                for (int i1 = 0; i1 < nl; i1++) {
                    for (int j1 = 0; j1 < nc; j1++) {
                        if (tmp[i1][j1] < I[i1][j1]) {
                            R[i1][j1] = R[i1][j1] + 1;
                        }
                    }
                }
                printMatrix(tmp);
            }
        }
        return R;
    }

    public static float[][] subsample(float[][] source) {
        int sourceWidth = source[0].length;
        int sourceHeight = source.length;
        int destWidth = (sourceWidth + 1) / 2;
        int destHeight = (sourceHeight + 1) / 2;
        float[][] result = new float[destHeight][destWidth];
        for (int i = 0; i < destHeight; i++) {
            for (int j = 0; j < destWidth; j++) {
                result[i][j] = source[i * 2][j * 2];
            }
        }
        return result;
    }

    public static float[][] interp2(float[][] source, float[][] xq, float[][] yq) {
        InterpolationBilinear interpolation = new InterpolationBilinear();

        int width = xq[0].length;
        int height = xq.length;
        float[][] result = new float[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                float srcX = xq[i][j];
                float srcY = yq[i][j];

                if (srcX < 0 || srcX >= width || srcY < 0 || srcY >= height) {
                    result[i][j] = Float.NaN;
                    continue;
                }

                int x0 = (int) srcX;
                int y0 = (int) srcY;
                int x1 = x0 + 1;
                int y1 = y0 + 1;

                float fracX = srcX - (int) srcX;
                float fracY = srcY - (int) srcY;

                if (fracX == 0.0 && fracY == 0.0) {
                    result[i][j] = source[y0][x0];
                    continue;
                }

                if (fracX == 0.0) {
                    x1 = x0;
                }
                if (fracY == 0.0) {
                    y1 = y0;
                }

                if (x1 >= width || y1 >= height) {
                    result[i][j] = Float.NaN;
                    continue;
                }

                float s00 = source[y0][x0];
                float s01 = source[y0][x1];
                float s10 = source[y1][x0];
                float s11 = source[y1][x1];

                result[i][j] = interpolation.interpolate(s00, s01, s10, s11, fracX, fracY);
            }
        }

        return result;
    }

    public static float[][] gradientrow(float[][] source) {
        // TODO check width == 1
        int width = source[0].length;
        int height = source.length;
        float[][] result = new float[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 1; j < width - 1; j++) {
                result[i][j] = (source[i][j + 1] - source[i][j - 1]) / 2;
            }
            result[i][0] = (source[i][1] - source[i][0]);
            result[i][width - 1] = (source[i][width - 1] - source[i][width - 2]);
        }
        return result;
    }

    public static float[][] gradientcol(float[][] source) {
        // TODO check height == 1
        int width = source[0].length;
        int height = source.length;
        float[][] result = new float[height][width];
        for (int i = 1; i < height - 1; i++) {
            for (int j = 0; j < width; j++) {
                result[i][j] = (source[i + 1][j] - source[i - 1][j]) / 2;
            }
        }
        for (int j = 0; j < width; j++) {
            result[0][j] = (source[1][j] - source[0][j]);
            result[height - 1][j] = (source[height - 1][j] - source[height - 2][j]);
        }
        return result;
    }

    public static float[][] gradientRow(float[][] I) {
        int nl = I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];

        for (int i = 0; i < nl; i++) {
            for (int j = 1; j < nc - 1; j++) {
                R[i][j] = (I[i][j + 1] - I[i][j - 1]) / 2;
            }

            R[i][0] = I[i][1] - I[i][0];
            R[i][nc] = I[i][nc] - I[i][nc - 1];
        }
        return R;
    }

    public static float[][] gradientColumn(float[][] I) {
        int nl = I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];

        for (int i = 1; i < nl - 1; i++) {
            for (int j = 0; j < nc; j++) {
                R[i][j] = (I[i + 1][j] - I[i - 1][j]) / 2;
            }
        }
        for (int j = 0; j < nc; j++) {
            R[0][j] = I[1][j] - I[0][j];
            R[nl][j] = I[nl][j] - I[nl - 1][j];
        }
        return R;
    }

    public static float[][] simpleMultiply(float[][] I, float[][] J) {
        //TODO check if the same number of rows and cols
        int nl = I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];

        for (int i = 0; i < nl; i++) {
            for (int j = 0; j < nc; j++) {
                R[i][j] = (I[i][j] * J[i][j]);
            }
        }
        return R;
    }

    public static float[][] simpleDivide(float[][] I, float[][] J, float defaultValue) {
        //TODO check if the same number of rows and cols
        int nl = I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];

        for (int i = 0; i < nl; i++) {
            for (int j = 0; j < nc; j++) {
                if (J[i][j] == 0) {
                    R[i][j] = defaultValue;
                } else {
                    R[i][j] = (float) (I[i][j] / J[i][j]);
                }
            }
        }
        return R;
    }

    public static float[][] sum(float[][] I, float[][] J) {
        //TODO check if the same number of rows and cols
        int nl = I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];

        for (int i = 0; i < nl; i++) {
            for (int j = 0; j < nc; j++) {
                R[i][j] = (I[i][j] + J[i][j]);
            }
        }
        return R;
    }

    public static float[][] difference(float[][] I, float[][] J) {
        //TODO check if the same number of rows and cols
        int nl = I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];

        for (int i = 0; i < nl; i++) {
            for (int j = 0; j < nc; j++) {
                R[i][j] = (I[i][j] - J[i][j]);
            }
        }
        return R;
    }

    public static void meshgrid(float[][] X, float[][] Y, int row, int col) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                X[i][j] = j + 1;
                Y[i][j] = i + 1;
            }
        }
    }
}
