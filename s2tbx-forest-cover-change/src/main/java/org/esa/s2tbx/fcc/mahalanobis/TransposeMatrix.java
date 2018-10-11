package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 20/6/2017.
 */
public class TransposeMatrix extends Matrix {
    private final Matrix matrix;

    public TransposeMatrix(Matrix matrix) {
        super();

        this.matrix = matrix;
    }

    @Override
    public float getValueAt(int rowIndex, int columnIndex) {
        return this.matrix.getValueAt(columnIndex, rowIndex);
    }

    @Override
    public int getRowCount() {
        return this.matrix.getColumnCount();
    }

    @Override
    public int getColumnCount() {
        return this.matrix.getRowCount();
    }
}
