package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 6/6/2017.
 */
public class MatrixUtils {

    private MatrixUtils() {
    }

    public static Matrix inverse(Matrix matrix) {
        float matrixDeterminant = matrix.computeDeterminant();
        if (matrixDeterminant == 0.0f) {
            return null;
        }
        //Matrix cofactorMatrix = cofactor(matrix);
        CofactorMatrix cofactorMatrix = new CofactorMatrix(matrix);
        //Matrix transposeMatrix = transpose(cofactorMatrix);
        TransposeMatrix transposeMatrix = new TransposeMatrix(cofactorMatrix);
        float constant = 1.0f / matrixDeterminant;
        return new MultiplyByConstantMatrix(transposeMatrix, constant);
        //return multiplyByConstant(transposeMatrix, 1.0f/matrixDeterminant);
    }
}
