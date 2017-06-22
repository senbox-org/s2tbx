package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 20/6/2017.
 */
public class MultiplyByConstantMatrix extends Matrix {
    private final Matrix matrix;
    private final float constant;

    public MultiplyByConstantMatrix(Matrix matrix, float constant) {
        super();

        this.matrix = matrix;
        this.constant = constant;
    }

    @Override
    public float getValueAt(int rowIndex, int columnIndex) {
        return matrix.getValueAt(rowIndex, columnIndex) * constant;
    }

    @Override
    public int getRowCount() {
        return this.matrix.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return this.matrix.getColumnCount();
    }
}
