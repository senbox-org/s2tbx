package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 20/6/2017.
 */
public class SubMatrix extends Matrix {
    private final Matrix matrix;
    private final int excludingRowIndex;
    private final int excludingColumnIndex;

    public SubMatrix(Matrix matrix, int excludingRowIndex, int excludingColumnIndex) {
        super();

        this.matrix = matrix;
        this.excludingRowIndex = excludingRowIndex;
        this.excludingColumnIndex = excludingColumnIndex;
    }

    @Override
    public float getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < this.excludingRowIndex) {
            if (columnIndex < this.excludingColumnIndex) {
                return this.matrix.getValueAt(rowIndex, columnIndex);
            } else {
                return this.matrix.getValueAt(rowIndex, columnIndex + 1);
            }
        } else {
            if (columnIndex < this.excludingColumnIndex) {
                return this.matrix.getValueAt(rowIndex + 1, columnIndex);
            } else {
                return this.matrix.getValueAt(rowIndex + 1, columnIndex + 1);
            }
        }
    }

    @Override
    public int getRowCount() {
        return this.matrix.getRowCount() - 1;
    }

    @Override
    public int getColumnCount() {
        return this.matrix.getColumnCount() - 1;
    }
}
