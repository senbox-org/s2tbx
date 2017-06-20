//package org.esa.s2tbx.fcc.mahalanobis;
//
///**
// * Created by jcoravu on 6/6/2017.
// */
//public class Matrix {
//    private final float data[][];
//
//    public Matrix(int rowCount, int columnCount) {
//        this.data = new float[rowCount][columnCount];
//
//        for (int i=0; i<rowCount; i++) {
//            this.data[i] = new float[columnCount];
//        }
//    }
//
//    protected Matrix(float data[][]) {
//        this.data = data;
//    }
//
//    public void setValueAt(int rowIndex, int columnIndex, float cellValue) {
//        this.data[rowIndex][columnIndex] = cellValue;
//    }
//
//    public float getValueAt(int rowIndex, int columnIndex) {
//        return this.data[rowIndex][columnIndex];
//    }
//
//    public int getRowCount() {
//        return this.data.length;
//    }
//
//    public int getColumnCount() {
//        return this.data[0].length;
//    }
//
//    public boolean isSquare() {
//        return getRowCount() == getColumnCount();
//    }
//
//    public float computeDeterminant() {
//        if (!isSquare()) {
//            throw new IllegalArgumentException("The matrix must be square.");
//        }
//        return determinant(this.data);
//    }
//
//    private static float determinant(float[][] matrix) {
//        int n = matrix.length;
//        if (n == 1) {
//            return matrix[0][0];
//        } else {
//            float det = 0.0f;
//            for (int j = 0; j < n; j++) {
//                float[][] minorData = minor(matrix, 0, j);
//                det += Math.pow(-1, j) * matrix[0][j] * determinant(minorData);
//            }
//            return det;
//        }
//    }
//
//    /**
//     * Computing the minor of the matrix m without the i-th row and the j-th
//     * column
//     *
//     * @param matrix input matrix
//     * @param i removing the i-th row of m
//     * @param j removing the j-th column of m
//     * @return minor of m
//     */
//    private static float[][] minor(float[][] matrix, int i, int j) {
//        int n = matrix.length;
//        float[][] minor = new float[n-1][n-1];
//        // index for minor matrix position:
//        int r = 0, s = 0;
//        for (int k = 0; k < n; k++) {
//            float[] row = matrix[k];
//            if (k != i) {
//                for (int l = 0; l < row.length; l++) {
//                    if (l != j) {
//                        minor[r][s++] = row[l];
//                    }
//                }
//                r++;
//                s = 0;
//            }
//        }
//        return minor;
//    }
//}

package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 6/6/2017.
 */
public class Matrix {
    private final float data[];
    private final int rowCount;
    private final int columnCount;

    public Matrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.data = new float[rowCount * columnCount];
    }

    protected Matrix(float data[], int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.data = data;
    }

    public void setValueAt(int rowIndex, int columnIndex, float cellValue) {
        this.data[(rowIndex * this.columnCount) + columnIndex] = cellValue;
    }

    public float getValueAt(int rowIndex, int columnIndex) {
        return this.data[(rowIndex * this.columnCount) + columnIndex];
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public boolean isSquare() {
        return this.rowCount == this.columnCount;
    }

    public float computeDeterminant() {
        if (!isSquare()) {
            throw new IllegalArgumentException("The matrix must be square.");
        }
        return determinantNew(this.data, this.rowCount);
    }

//    private static float determinant(float[][] matrix) {
//        int n = matrix.length;
//        if (n == 1) {
//            return matrix[0][0];
//        } else {
//            float det = 0.0f;
//            for (int j = 0; j < n; j++) {
//                float[][] minorData = minor(matrix, 0, j);
//                det += Math.pow(-1, j) * matrix[0][j] * determinant(minorData);
//            }
//            return det;
//        }
//    }

    private float determinantNew(float[] matrix, int size) {
        if (size == 1) {
            return matrix[0];
        } else {
            float det = 0.0f;
            for (int j = 0; j < size; j++) {
                float[] minorData = minorNew(matrix, size, 0, j);
                det += Math.pow(-1, j) * matrix[(0 * size) + j] * determinantNew(minorData, size-1);
            }
            return det;
        }
    }

//    private static float[][] minor(float[][] matrix, int i, int j) {
//        int n = matrix.length;
//        float[][] minor = new float[n-1][n-1];
//        // index for minor matrix position:
//        int r = 0, s = 0;
//        for (int k = 0; k < n; k++) {
//            float[] row = matrix[k];
//            if (k != i) {
//                for (int l = 0; l < row.length; l++) {
//                    if (l != j) {
//                        minor[r][s++] = row[l];
//                    }
//                }
//                r++;
//                s = 0;
//            }
//        }
//        return minor;
//    }

    private static float[] minorNew(float[] squareMatrix, int size, int rowIndexToExclude, int columnIndexToExclude) {
        int newSize = size - 1;
        float[] minor = new float[newSize * newSize];
        int rowIndex = 0;
        for (int k = 0; k < size; k++) {
            if (k != rowIndexToExclude) {
                int columnIndex = 0;
                for (int l = 0; l < size; l++) {
                    if (l != columnIndexToExclude) {
                        minor[(rowIndex * newSize) + columnIndex] = squareMatrix[(k * size) + l];
                        columnIndex++;
                    }
                }
                rowIndex++;
            }
        }
        return minor;
    }
}
