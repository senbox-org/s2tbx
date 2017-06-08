package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 6/6/2017.
 */
public class Matrix {
    private double data[][];

    public Matrix(int rowCount, int columnCount) {
        this.data = new double[rowCount][columnCount];

        for (int i=0; i<rowCount; i++) {
            this.data[i] = new double[columnCount];
        }
    }

    public void setValueAt(int rowIndex, int columnIndex, double cellValue) {
        this.data[rowIndex][columnIndex] = cellValue;
    }

    public double getValueAt(int rowIndex, int columnIndex) {
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
