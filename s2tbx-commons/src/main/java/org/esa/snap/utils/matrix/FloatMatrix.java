package org.esa.snap.utils.matrix;

/**
 * @author Jean Coravu
 */
public class FloatMatrix {
    private final float data[];
    private final int rowCount;
    private final int columnCount;

    public FloatMatrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;

        this.data = new float[rowCount * columnCount];
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

    public void setValueAt(int rowIndex, int columnIndex, float value) {
        this.data[(rowIndex * this.columnCount) + columnIndex] = value;
    }
}
