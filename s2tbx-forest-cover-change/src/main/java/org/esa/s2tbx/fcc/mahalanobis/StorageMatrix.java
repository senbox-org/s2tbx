package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 23/6/2017.
 */
public class StorageMatrix extends Matrix {
    private final float data[];
    private final int rowCount;
    private final int columnCount;

    public StorageMatrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;

        this.data = new float[rowCount * columnCount];
    }

    @Override
    public float getValueAt(int rowIndex, int columnIndex) {
        return this.data[(rowIndex * this.columnCount) + columnIndex];
    }

    @Override
    public int getRowCount() {
        return this.rowCount;
    }

    @Override
    public int getColumnCount() {
        return this.columnCount;
    }

    public void setValueAt(int rowIndex, int columnIndex, float value) {
        this.data[(rowIndex * this.columnCount) + columnIndex] = value;
    }
}
