package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 6/6/2017.
 */
public class MatrixUtils {

    private MatrixUtils() {
    }

    public static Matrix transpose(Matrix matrix) {
        Matrix transposedMatrix = new Matrix(matrix.getColumnCount(), matrix.getRowCount());
        for (int i=0;i<matrix.getRowCount();i++) {
            for (int j=0;j<matrix.getColumnCount();j++) {
                transposedMatrix.setValueAt(j, i, matrix.getValueAt(i, j));
            }
        }
        return transposedMatrix;
    }

    public static Matrix multiplyByConstant(Matrix matrix, float constant) {
        Matrix mat = new Matrix(matrix.getRowCount(), matrix.getColumnCount());
        for (int i = 0; i < matrix.getRowCount(); i++) {
            for (int j = 0; j < matrix.getColumnCount(); j++) {
                mat.setValueAt(i, j, matrix.getValueAt(i, j) * constant);
            }
        }
        return mat;
    }

    private static int changeSign(int i) {
        return (i%2 == 0) ? 1 : -1;
    }

    private static Matrix createSubMatrix(Matrix matrix, int excludingRowIndex, int excludingColumnIndex) {
        Matrix mat = new Matrix(matrix.getRowCount()-1, matrix.getColumnCount()-1);
        int row = -1;
        for (int i=0;i<matrix.getRowCount();i++) {
            if (i == excludingRowIndex) {
                continue;
            }
            row++;
            int column = -1;
            for (int j=0;j<matrix.getColumnCount();j++) {
                if (j == excludingColumnIndex) {
                    continue;
                }
                mat.setValueAt(row, ++column, matrix.getValueAt(i, j));
            }
        }
        return mat;
    }

    private static float determinant(Matrix matrix) {
        return matrix.computeDeterminant();
    }

    public static Matrix inverse(Matrix matrix) {
        Matrix tempMatrix = transpose(cofactor(matrix));
        float matrixDeterminant = determinant(matrix);
        if (matrixDeterminant == 0.0f) {
            return null;
        }
        return multiplyByConstant(tempMatrix, 1.0f/matrixDeterminant);
    }

    public static Matrix subtract(Matrix matrix1, Matrix matrix2) {
        return add(matrix1, multiplyByConstant(matrix2, -1));
    }

    public static Matrix add(Matrix matrix1, Matrix matrix2) {
        if (matrix1.getColumnCount() != matrix2.getColumnCount() || matrix1.getRowCount() != matrix2.getRowCount()) {
            throw new IllegalArgumentException("The two matrices must have the same dimension.");
        }
        Matrix sumMatrix = new Matrix(matrix1.getRowCount(), matrix1.getColumnCount());
        for (int i=0; i<matrix1.getRowCount();i++) {
            for (int j=0;j<matrix1.getColumnCount();j++)
                sumMatrix.setValueAt(i, j, matrix1.getValueAt(i, j) + matrix2.getValueAt(i,j));

        }
        return sumMatrix;
    }

    public static Matrix cofactor(Matrix matrix) {
        Matrix mat = new Matrix(matrix.getRowCount(), matrix.getColumnCount());
        for (int i=0;i<matrix.getRowCount();i++) {
            for (int j=0; j<matrix.getColumnCount();j++) {
                mat.setValueAt(i, j, changeSign(i) * changeSign(j) * determinant(createSubMatrix(matrix, i, j)));
            }
        }

        return mat;
    }

    public static Matrix multiply(Matrix matrix1, Matrix matrix2)  {
        Matrix multipliedMatrix = new Matrix(matrix1.getRowCount(), matrix2.getColumnCount());

        for (int i=0;i<multipliedMatrix.getRowCount();i++) {
            for (int j=0;j<multipliedMatrix.getColumnCount();j++) {
                float sum = 0.0f;
                for (int k=0;k<matrix1.getColumnCount();k++) {
                    sum += matrix1.getValueAt(i, k) * matrix2.getValueAt(k, j);
                }
                multipliedMatrix.setValueAt(i, j, sum);
            }
        }
        return multipliedMatrix;
    }

}
