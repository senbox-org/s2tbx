package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 20/6/2017.
 */
public class CofactorMatrix extends Matrix {
    private final Matrix matrix;

    public CofactorMatrix(Matrix matrix) {
        super();

        this.matrix = matrix;
    }

    @Override
    public float getValueAt(int rowIndex, int columnIndex) {
        SubMatrix minorMatrix = new SubMatrix(this.matrix, rowIndex, columnIndex);
        return changeSign(rowIndex) * changeSign(columnIndex) * minorMatrix.computeDeterminant();
    }

    @Override
    public int getRowCount() {
        return this.matrix.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return this.matrix.getColumnCount();
    }

    private static int changeSign(int i) {
        return (i%2 == 0) ? 1 : -1;
    }
}
