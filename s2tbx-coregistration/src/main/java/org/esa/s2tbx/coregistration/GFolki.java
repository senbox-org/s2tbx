package org.esa.s2tbx.coregistration;

import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.SmoothingPolynomialBicubicSplineInterpolator;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

/**
 * Created by ramonag on 4/5/2017.
 */
public class GFolki {

    //init
    int iteration=2;
    int[] radius = {32, 24, 16, 8};
    int rank=4;
    int levels=5;
    //fin init

    public GFolki(){

    }

    public void process(float[][] I0, float[][] I1, float[][] u, float[][] v){
        //if (rank > 0){
            float[][] R0 = MatrixUtils.rank_sup(I0, rank);
            float[][] R1i = MatrixUtils.rank_inf(I1, rank);
            float[][] R1s = MatrixUtils.rank_sup(I1, rank);

        //}

        if(u.length == 0){
            u = new float[I0.length][I0[0].length];
            for(int i=0;i<I0.length;i++){
                for(int j=0;j<I0[0].length;j++){
                    u[i][j]=0;
                }
            }
        }
        if(v.length == 0){
            v = new float[I1.length][I1[0].length];
            for(int i=0;i<I1.length;i++){
                for(int j=0;j<I1[0].length;j++){
                    v[i][j]=0;
                }
            }
        }

        SmoothingPolynomialBicubicSplineInterpolator iterpolator = new SmoothingPolynomialBicubicSplineInterpolator();
        double[] xs = new double[I0.length];
        for(int i=0;i<I0.length;i++){
            xs[i]=i;
        }
        double[] ys = new double[I0[0].length];
        for(int i=0;i<I0[0].length;i++){
            ys[i]=i;
        }
        double[][] R0d = new double[I0.length][I0[0].length];
        for (int i1 = 0; i1 < I0.length; i1++) {

            for (int j1 = 0; j1 < I0[0].length; j1++) {
                R0d[i1][j1] = (new Float(R0[i1][j1])).doubleValue();
            }
        }
        BicubicSplineInterpolatingFunction f = iterpolator.interpolate(xs, ys, R0d);
        double[][] ans = new double[I0.length][I0[0].length];
        for (int i = 0; i < ans.length; i++) {
            for (int j = 0; j < ans[0].length; j++) {
                ans[i][j] = f.partialDerivativeY(i, j);
            }
        }

        //MatrixUtils.convolve()


        /*
        for rad in radius:
         W = lambda x : conv2Sep(x, np.ones([2*rad+1,1]) / 2*rad + 1)
         Ixx = W(Ix*Ix)
         Iyy = W(Iy*Iy)
         Ixy = W(Ix*Iy)
         D   = Ixx*Iyy - Ixy**2
         for i in range(iteration):
             I1w = interp2(I1,x+u,y+v)
             crit1 = conv2Sep(np.abs(I0-I1w), np.ones([2*rank+1,1]))
             crit2 = conv2Sep(np.abs(1-I0-I1w), np.ones([2*rank+1,1]))
             R1w = interp2(R1s,x+u,y+v)
             R1w_1 = interp2(R1i,x+u,y+v)
             R1w[crit1 > crit2] = R1w_1[crit1 > crit2]
             it = R0 - R1w + u*Ix + v*Iy
             Ixt = W(Ix * it)
             Iyt = W(Iy * it)
             u = (Iyy * Ixt - Ixy * Iyt)/ D
             v = (Ixx * Iyt - Ixy * Ixt) /D
             unvalid = np.isnan(u)|np.isinf(u)|np.isnan(v)|np.isinf(v)
             u[unvalid] = 0
             v[unvalid] = 0
     return u,v

         */
    }

}
