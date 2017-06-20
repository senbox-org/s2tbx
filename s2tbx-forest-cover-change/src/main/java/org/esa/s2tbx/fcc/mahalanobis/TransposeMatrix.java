package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 20/6/2017.
 */
public class TransposeMatrix extends Matrix {
    private final Matrix matrix;

    public TransposeMatrix(Matrix matrix) {
        super(null, 0, 0);

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

    @Override
    public void setValueAt(int rowIndex, int columnIndex, float cellValue) {
        throw new UnsupportedOperationException("The 'setValueAt' is not implemented.");
    }

    @Override
    public boolean isSquare() {
        throw new UnsupportedOperationException("The 'isSquare' is not implemented.");
    }

    @Override
    public float computeDeterminant() {
        throw new UnsupportedOperationException("The 'computeDeterminant' is not implemented.");
    }
}
