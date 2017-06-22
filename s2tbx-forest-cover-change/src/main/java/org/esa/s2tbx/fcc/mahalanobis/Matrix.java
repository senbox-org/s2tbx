package org.esa.s2tbx.fcc.mahalanobis;

/**
 * Created by jcoravu on 6/6/2017.
 */
public abstract class Matrix {

    protected Matrix() {
    }

    public abstract float getValueAt(int rowIndex, int columnIndex);

    public abstract int getRowCount();

    public abstract int getColumnCount();

    @Override
    public String toString() {
        return getClass().getSimpleName()+"[rowCount="+getRowCount()+", columnCount="+getColumnCount()+"]";
    }

    public final boolean isSquare() {
        return (getRowCount() == getColumnCount());
    }

    public final float computeDeterminant() {
        if (!isSquare()) {
            throw new IllegalArgumentException("The matrix must be square.");
        }
        return determinant(this);
    }

    private static float determinant(Matrix squareMatrix) {
        int squareMatrixSize = squareMatrix.getRowCount();
        if (squareMatrixSize == 1) {
            return squareMatrix.getValueAt(0, 0);
        } else {
            float det = 0.0f;
            for (int j = 0; j < squareMatrixSize; j++) {
                SubMatrix minorMatrix = new SubMatrix(squareMatrix, 0, j);
                det += Math.pow(-1, j) * squareMatrix.getValueAt(0, j) * determinant(minorMatrix);
            }
            return det;
        }
    }
}
