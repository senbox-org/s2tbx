package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 20/6/2017.
 */
public class MultiplyMatrix extends Matrix {
    private final Matrix firstMatrix;
    private final Matrix secondMatrix;

    public MultiplyMatrix(Matrix firstMatrix, Matrix secondMatrix) {
        super();

        if (firstMatrix.getColumnCount() != secondMatrix.getRowCount()) {
            throw new IllegalArgumentException("The column count " + firstMatrix.getColumnCount() +" of the first matrix does not match the row count " + secondMatrix.getRowCount() +" of the second matrix.");
        }

        this.firstMatrix = firstMatrix;
        this.secondMatrix = secondMatrix;
    }

    @Override
    public float getValueAt(int rowIndex, int columnIndex) {
        float sum = 0.0f;
        for (int k=0;k<this.firstMatrix.getColumnCount();k++) {
            sum += this.firstMatrix.getValueAt(rowIndex, k) * this.secondMatrix.getValueAt(k, columnIndex);
        }
        return sum;
    }

    @Override
    public int getRowCount() {
        return this.firstMatrix.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return this.secondMatrix.getColumnCount();
    }
}
