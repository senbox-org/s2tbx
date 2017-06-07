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
}
