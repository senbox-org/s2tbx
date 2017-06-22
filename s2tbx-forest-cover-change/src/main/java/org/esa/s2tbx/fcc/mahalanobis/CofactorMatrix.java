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

//    public static Matrix cofactor(Matrix matrix) {
//        Matrix cofactorMatrix = new Matrix(matrix.getRowCount(), matrix.getColumnCount());
//
//        for (int i=0;i<matrix.getRowCount();i++) {
//            for (int j=0; j<matrix.getColumnCount();j++) {
//                SubMatrix minorMatrix = new SubMatrix(matrix, i, j);
//                cofactorMatrix.setValueAt(i, j, changeSign(i) * changeSign(j) * minorMatrix.computeDeterminant());
//            }
//        }
//
//        return cofactorMatrix;
//    }

    private static int changeSign(int i) {
        return (i%2 == 0) ? 1 : -1;
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
}
