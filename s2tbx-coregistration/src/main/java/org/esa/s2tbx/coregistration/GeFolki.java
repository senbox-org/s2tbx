package org.esa.s2tbx.coregistration;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.descriptive.rank.Min;

import javax.media.jai.JAI;

/**
 * Created by ramonag on 5/4/2017.
 */
@Deprecated
public class GeFolki {
    //init
    int levels = 6;
    int iter = 2;
    boolean contrast = false;
    int rank = 4;

    public GeFolki(){

    }

    public void process(float[][] I0, float[][] I1){
        applyContrast(I0, I1);
        float[][][] pyrI0 = pyramid(I0, levels);
        float[][][] pyrI1 = pyramid(I1, levels);

        float[][] u;
        float[][] v;

        float[][] I1_inf;
        float[][] I1_sup;


        for(int n = pyrI0.length-1 ; n>=0 ; n--){
            float[][] J0 = pyrI0[n];
            float[][] J1 = pyrI1[n];

            float[][] H0;
            float[][] H1;
            if(contrast) {
                H0 = ClaheNew.process(J0);
                H1 = ClaheNew.process(J1);
            } else {
                H0 = J0;
                H1 = J1;
            }

            int row = J0.length;
            int col = J0[0].length;

            float[][] X = new float[row][col];
            float[][] Y = new float[row][col];
            meshgrid(X, Y, row, col);

            if(n != pyrI0.length - 1){
                 //u interpolare si inmultire
                //v interpolare si inmultire

            } else {
                //init u,v cu 0
                u = new float[J0.length][J0[0].length];
                v = new float[J0.length][J0[0].length];
                for(int i=0;i<J0.length;i++) {
                    for (int j = 0; j < J0[0].length; j++) {
                        u[i][j] = 0;
                        v[i][j] = 0;
                    }
                }
            }

            if (rank != 0){
                //I0=rank
                //I1_sup
                //I1_inf
            } else {
                I0 = J0;
                I1_sup = J1;
                I1_inf = I1;//////functie !!! -I1;
            }

            //[Ix, Iy] = gradient(I0); din formula de gradient
            //Ixx = (Ix.*Ix);
            //Iyy = (Iy.*Iy);
            //Ixy = (Ix.*Iy);
            //inmultire!!!



        }

    }

    private void meshgrid(float[][] X, float[][] Y, int row, int col) {
        for(int i=0;i<row;i++) {
            for (int j = 0; j < col; j++) {
                X[i][j] = j + 1;
                Y[i][j] = i + 1;
            }
        }
    }

    private float[][][] pyramid(float[][] I, int levelsP){
        float[][][] result = new float[levelsP+1][I.length][I[0].length];
        result[0] = I;
        for (int k=0;k<levelsP;k++){
            result[k+1] = pyramBurt(result[k]);
        }
        return result;
    }

    private float[][] pyramBurt(float[][] M){
        float a = 0.4f;
        float aComp = 0.25f-a/2.0f;
        float[] burt1D = {aComp,0.25f,a,0.25f,aComp};
        float[][] N = convSep(M, burt1D);
        float[][] R = new float[N.length/2][N[0].length/2];

        for(int i=0;i<N.length;i=i+2) {
            for (int j = 0; j < N[0].length; j=j+2) {
                R[i][j] = N[i][j];
            }
        }
        return R;
    }

    private float[][] convSep(float[][] I, float[] fen){
        int row = I.length;
        float rad = (fen.length-1f)/2f;
        int radI = (int) rad;// partea intreaga!!!!!!
        float[][] J = new float[I.length+2*row][I[0].length+2*radI];
        for(int i=0;i<J.length;i++){
            for(int j=0;j<J[0].length;j++){
                if(i>=row && i<I.length+row &&
                        j>=rad && j<I[0].length+rad){
                    J[i][j] = I[i-row][j-radI];
                } else {
                    J[i][j] = 0;
                }
            }
        }
//CONV2.......!!!!!
        return J;
    }

    private float[][] convolution(float[][] I0, float[][] I1){
            int smallWidth = I0[0].length - I1[0].length + 1;
            int smallHeight = I0.length - I1.length + 1;
            float[][] output = new float[smallWidth][smallHeight];
            for (int i = 0; i < smallWidth; ++i) {
                for (int j = 0; j < smallHeight; ++j) {
                    output[i][j] = 0;
                }
            }
            for (int i = 0; i < smallWidth; ++i) {
                for (int j = 0; j < smallHeight; ++j) {
                    //output[i][j] = singlePixelConvolution(input, i, j, kernel,
                    //        kernelWidth, kernelHeight);
                }
            }
            return output;
    }

    private void applyContrast(float[][] I0, float[][] I1){
        if(contrast){
            float minI0 = MatrixUtils.min(I0);
            float maxI0 = MatrixUtils.max(I0);
            float difI0 = maxI0 - minI0;
            float minI1 = MatrixUtils.min(I1);
            float maxI1 = MatrixUtils.max(I1);
            float difI1 = maxI1 - minI1;

            for(int i=1;i<I0.length;i++) {
                for (int j = 0; j < I0[0].length; j++) {
                    I0[i][j] = (I0[i][j] - minI0) / difI0;
                }
            }
            for(int i=1;i<I1.length;i++) {
                for (int j = 0; j < I1[0].length; j++) {
                    I1[i][j] = (I1[i][j] - minI1) / difI1;
                }
            }
        }
    }
}
