package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 6/6/2017.
 */
public class Matrix {
    private float data[][];

    public Matrix(int rowCount, int columnCount) {
        this.data = new float[rowCount][columnCount];

        for (int i=0; i<rowCount; i++) {
            this.data[i] = new float[columnCount];
        }
    }

    public void setValueAt(int rowIndex, int columnIndex, float cellValue) {
        this.data[rowIndex][columnIndex] = cellValue;
    }

    public float getValueAt(int rowIndex, int columnIndex) {
        return this.data[rowIndex][columnIndex];
    }

    public int getRowCount() {
        return this.data.length;
    }

    public int getColumnCount() {
        return this.data[0].length;
    }

    public boolean isSquare() {
        return getRowCount() == getColumnCount();
    }

    public float computeDeterminant() {
        if (!isSquare()) {
            throw new IllegalArgumentException("The matrix must be square.");
        }
        return determinant(this.data);
    }

    private static float determinant(float[][] matrix) {
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        } else {
            float det = 0.0f;
            for (int j = 0; j < n; j++) {
                det += Math.pow(-1, j) * matrix[0][j] * determinant(minor(matrix, 0, j));
            }
            return det;
        }
    }

    /**
     * Computing the minor of the matrix m without the i-th row and the j-th
     * column
     *
     * @param matrix input matrix
     * @param i removing the i-th row of m
     * @param j removing the j-th column of m
     * @return minor of m
     */
    private static float[][] minor(float[][] matrix, int i, int j) {
        int n = matrix.length;
        float[][] minor = new float[n-1][n-1];
        // index for minor matrix position:
        int r = 0, s = 0;
        for (int k = 0; k < n; k++) {
            float[] row = matrix[k];
            if (k != i) {
                for (int l = 0; l < row.length; l++) {
                    if (l != j) {
                        minor[r][s++] = row[l];
                    }
                }
                r++;
                s = 0;
            }
        }
        return minor;
    }
}
