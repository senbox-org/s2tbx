package org.esa.snap.utils.matrix;

/**
 * @author Jean Coravu
 */
public class ByteMatrix {
    private final byte data[];
    private final int rowCount;
    private final int columnCount;

    public ByteMatrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;

        this.data = new byte[rowCount * columnCount];
    }

    public byte getValueAt(int rowIndex, int columnIndex) {
        return this.data[(rowIndex * this.columnCount) + columnIndex];
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public void setValueAt(int rowIndex, int columnIndex, byte value) {
        this.data[(rowIndex * this.columnCount) + columnIndex] = value;
    }
}
