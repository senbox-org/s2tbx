package org.esa.s2tbx.coregistration;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

/**
 * Created by ramonag on 5/30/2017.
 */
public class MatrixUtils {



    public static int max(int[] v){
        int m = v[0];
        for(int i=1;i<v.length;i++){
            if(m<v[i]){
                m=v[i];
            }
        }
        return m;
    }

    public static int min(int[] v){
        int m = v[0];
        for(int i=1;i<v.length;i++){
            if(m>v[i]){
                m=v[i];
            }
        }
        return m;
    }

    public static float[][] rank_sup(float[][] I,int rank){
        int nl=I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];
        float[][] tmp = new float[nl][nc];
        for(int i=0;i<nl;i++){
            for (int j=0;j<nc;j++){
                R[i][j]=0;
            }
        }
        //int range_rad = max(rad)-min(rad);
        for(int i =0;i<rank;i++) {//range(1,rad+1)
            for (int j = 0; j < rank; j++) {//range(rad+1)
                //tmp = np.concatenate([I[i:,j:], np.zeros([nl-i,j])], axis=1)
                //tmp = np.concatenate([tmp,np.zeros([i,nc])],axis = 0)

                for (int i1 = 0; i1 < nl; i1++) {

                    for (int j1 = 0; j1 < nc; j1++) {
                        if (i1 <= i && j1 <= j) {
                            tmp[i1][j1] = I[i1][j1];
                        } else {
                            tmp[i1][j1] = 0;
                            R[i1][j1] = R[i1][j1] + 1;
                        }
                    }
                }
                for (int i1 = 0; i1 < nl; i1++) {

                    for (int j1 = 0; j1 < nc; j1++) {
                        if (i1 > i && j1 > j) {
                            tmp[i1][j1] = I[i1][j1];
                        } else {
                            tmp[i1][j1] = 0;
                            R[i1][j1] = R[i1][j1] + 1;
                        }
                    }
                }
            }
        }
        for(int i =0;i<rank;i++) {//range(1,rad+1)
            for (int j = 0; j < rank; j++) {//range(rad+1)

                for (int i1 = 0; i1 < nl; i1++) {

                    for (int j1 = 0; j1 < nc; j1++) {
                        if (i1 > i && j1 <= j) {
                            tmp[i1][j1] = I[i1][j1];
                        } else {
                            tmp[i1][j1] = 0;
                            R[i1][j1] = R[i1][j1] + 1;
                        }
                    }

                    for (int j1 = 0; j1 < nc; j1++) {
                        if (i1 <= i && j1 > j) {
                            tmp[i1][j1] = I[i1][j1];
                        } else {
                            tmp[i1][j1] = 0;
                            R[i1][j1] = R[i1][j1] + 1;
                        }
                    }
                }
            }
        }
        return R;
    }

    public static float[][] rank_inf(float[][] I,int rank){
        int nl=I.length;
        int nc = I[0].length;
        float[][] R = new float[nl][nc];
        float[][] tmp = new float[nl][nc];
        for(int i=0;i<nl;i++){

            for (int j=0;j<nc;j++){
                R[i][j]=0;
            }
        }
        //int range_rad = max(rad)-min(rad);
        for(int i =0;i<rank;i++) {//range(1,rad+1)
            for (int j = 0; j < rank; j++) {//range(rad+1)
                //tmp = np.concatenate([I[i:,j:], np.zeros([nl-i,j])], axis=1)
                //tmp = np.concatenate([tmp,np.zeros([i,nc])],axis = 0)

                for (int i1 = 0; i1 < nl; i1++) {

                    for (int j1 = 0; j1 < nc; j1++) {
                        if (i1 <= i && j1 <= j) {
                            tmp[i1][j1] = I[i1][j1];
                            R[i1][j1] = R[i1][j1] + 1;
                        } else {
                            tmp[i1][j1] = 0;
                        }
                    }
                }
                for (int i1 = 0; i1 < nl; i1++) {

                    for (int j1 = 0; j1 < nc; j1++) {
                        if (i1 > i && j1 > j) {
                            tmp[i1][j1] = I[i1][j1];
                            R[i1][j1] = R[i1][j1] + 1;
                        } else {
                            tmp[i1][j1] = 0;
                        }
                    }
                }
            }
        }
        for(int i =0;i<rank;i++) {//range(1,rad+1)
            for (int j = 0; j < rank; j++) {//range(rad+1)

                for (int i1 = 0; i1 < nl; i1++) {

                    for (int j1 = 0; j1 < nc; j1++) {
                        if (i1 > i && j1 <= j) {
                            tmp[i1][j1] = I[i1][j1];
                            R[i1][j1] = R[i1][j1] + 1;
                        } else {
                            tmp[i1][j1] = 0;
                        }
                    }

                    for (int j1 = 0; j1 < nc; j1++) {
                        if (i1 <= i && j1 > j) {
                            tmp[i1][j1] = I[i1][j1];
                            R[i1][j1] = R[i1][j1] + 1;
                        } else {
                            tmp[i1][j1] = 0;
                        }
                    }
                }
            }
        }
        return R;
    }

    //convolve from MathArrays 3.3
    public static double[] convolve(double[] x, double[] h)
            throws NullArgumentException,
            NoDataException {
        MathUtils.checkNotNull(x);
        MathUtils.checkNotNull(h);


        final int xLen = x.length;
        final int hLen = h.length;


        if (xLen == 0 || hLen == 0) {
            throw new NoDataException();
        }


        // initialize the output array
        final int totalLength = xLen + hLen - 1;
        final double[] y = new double[totalLength];


        // straightforward implementation of the convolution sum
        for (int n = 0; n < totalLength; n++) {
            double yn = 0;
            int k = FastMath.max(0, n + 1 - xLen);
            int j = n - k;
            while (k < hLen && j >= 0) {
                yn += x[j--] * h[k++];
            }
            y[n] = yn;
        }

        return y;
    }

}
