package org.esa.s2tbx.coregistration;

/**
 * @author R. Manda
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

    public static int[][] rank_sup(float[][] I, int rank) {
        int nl = I.length;
        int nc = I[0].length;
        int[][] R = new int[nl][nc];
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
                for (int i1 = 0; i1 < nl - i; i1++) {
                    for (int j1 = 0; j1 < nc - j - 1; j1++) {

                        int iw = i1 + i;
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

    public static int[][] rank_inf(float[][] I, int rank) {
        int nl = I.length;
        int nc = I[0].length;
        int[][] R = new int[nl][nc];
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

                        if (tmp[iw][jw] < I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }
                    }
                }

                tmp = new float[nl][nc];
                for (int i1 = 0; i1 < nl - i; i1++) {
                    for (int j1 = 0; j1 < nc - j - 1; j1++) {

                        int iw = i1 + i;
                        int jw = j1 + j + 1;
                        tmp[iw][jw] = I[i1][j1];

                        if (tmp[iw][jw] < I[iw][jw]) {
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

                        if (tmp[iw][jw] < I[iw][jw]) {
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

                        if (tmp[iw][jw] < I[iw][jw]) {
                            R[iw][jw] = R[iw][jw] + 1;
                        }
                    }
                }
            }
        }
        return R;
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
