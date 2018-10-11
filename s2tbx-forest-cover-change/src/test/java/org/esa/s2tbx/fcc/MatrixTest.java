package org.esa.s2tbx.fcc;

import org.esa.s2tbx.fcc.mahalanobis.MultiplyByConstantMatrix;
import org.esa.s2tbx.fcc.mahalanobis.MultiplyMatrix;
import org.esa.s2tbx.fcc.mahalanobis.StorageMatrix;
import org.esa.s2tbx.fcc.mahalanobis.SubMatrix;
import org.esa.s2tbx.fcc.mahalanobis.TransposeMatrix;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Jean Coravu.
 */
public class MatrixTest {

    public MatrixTest() {
    }

    @Test
    public void testMatrix() {
        StorageMatrix matrix = new StorageMatrix(4, 5);
        matrix.setValueAt(0, 0, 1f);
        matrix.setValueAt(0, 1, 2f);
        matrix.setValueAt(0, 2, 3f);
        matrix.setValueAt(0, 3, 4f);
        matrix.setValueAt(0, 4, 5f);

        matrix.setValueAt(1, 0, 6f);
        matrix.setValueAt(1, 1, 7f);
        matrix.setValueAt(1, 2, 8f);
        matrix.setValueAt(1, 3, 9f);
        matrix.setValueAt(1, 4, 10f);

        matrix.setValueAt(2, 0, 11f);
        matrix.setValueAt(2, 1, 12f);
        matrix.setValueAt(2, 2, 13f);
        matrix.setValueAt(2, 3, 14f);
        matrix.setValueAt(2, 4, 15f);

        matrix.setValueAt(3, 0, 16f);
        matrix.setValueAt(3, 1, 17f);
        matrix.setValueAt(3, 2, 18f);
        matrix.setValueAt(3, 3, 19f);
        matrix.setValueAt(3, 4, 20f);

        assertEquals(4, matrix.getRowCount());
        assertEquals(5, matrix.getColumnCount());

        assertEquals(7.0f, matrix.getValueAt(1, 1), 0.0f);
        assertEquals(18.0f, matrix.getValueAt(3, 2), 0.0f);
        assertEquals(15.0f, matrix.getValueAt(2, 4), 0.0f);
        assertEquals(6.0f, matrix.getValueAt(1, 0), 0.0f);
        assertEquals(4.0f, matrix.getValueAt(0, 3), 0.0f);
        assertEquals(20.0f, matrix.getValueAt(3, 4), 0.0f);
    }

    @Test
    public void testMultiplyByConstantMatrix() {
        StorageMatrix matrix = new StorageMatrix(4, 5);
        matrix.setValueAt(0, 0, 1f);
        matrix.setValueAt(0, 1, 2f);
        matrix.setValueAt(0, 2, 3f);
        matrix.setValueAt(0, 3, 4f);
        matrix.setValueAt(0, 4, 5f);

        matrix.setValueAt(1, 0, 6f);
        matrix.setValueAt(1, 1, 7f);
        matrix.setValueAt(1, 2, 8f);
        matrix.setValueAt(1, 3, 9f);
        matrix.setValueAt(1, 4, 10f);

        matrix.setValueAt(2, 0, 11f);
        matrix.setValueAt(2, 1, 12f);
        matrix.setValueAt(2, 2, 13f);
        matrix.setValueAt(2, 3, 14f);
        matrix.setValueAt(2, 4, 15f);

        matrix.setValueAt(3, 0, 16f);
        matrix.setValueAt(3, 1, 17f);
        matrix.setValueAt(3, 2, 18f);
        matrix.setValueAt(3, 3, 19f);
        matrix.setValueAt(3, 4, 20f);

        MultiplyByConstantMatrix multiplyByConstantMatrix = new MultiplyByConstantMatrix(matrix, 2.5f);

        assertEquals(4, matrix.getRowCount());
        assertEquals(5, matrix.getColumnCount());

        assertEquals(4, multiplyByConstantMatrix.getRowCount());
        assertEquals(5, multiplyByConstantMatrix.getColumnCount());

        assertEquals(22.5f, multiplyByConstantMatrix.getValueAt(1, 3), 0.0f);
        assertEquals(25.0f, multiplyByConstantMatrix.getValueAt(1, 4), 0.0f);
        assertEquals(40.0f, multiplyByConstantMatrix.getValueAt(3, 0), 0.0f);
        assertEquals(17.5f, multiplyByConstantMatrix.getValueAt(1, 1), 0.0f);
        assertEquals(32.5f, multiplyByConstantMatrix.getValueAt(2, 2), 0.0f);
        assertEquals(42.5f, multiplyByConstantMatrix.getValueAt(3, 1), 0.0f);
    }

    @Test
    public void testMultiplyMatrix() {
        StorageMatrix firstMatrix = new StorageMatrix(2, 3);
        firstMatrix.setValueAt(0, 0, 1f);
        firstMatrix.setValueAt(0, 1, 2f);
        firstMatrix.setValueAt(0, 2, 3f);

        firstMatrix.setValueAt(1, 0, 4f);
        firstMatrix.setValueAt(1, 1, 5f);
        firstMatrix.setValueAt(1, 2, 6f);

        StorageMatrix secondMatrix = new StorageMatrix(3, 4);
        secondMatrix.setValueAt(0, 0, 7f);
        secondMatrix.setValueAt(0, 1, 8f);
        secondMatrix.setValueAt(0, 2, 9f);
        secondMatrix.setValueAt(0, 2, 10f);

        secondMatrix.setValueAt(1, 0, 11f);
        secondMatrix.setValueAt(1, 1, 12f);
        secondMatrix.setValueAt(1, 2, 13f);
        secondMatrix.setValueAt(1, 2, 14f);

        secondMatrix.setValueAt(2, 0, 15f);
        secondMatrix.setValueAt(2, 1, 16f);
        secondMatrix.setValueAt(2, 2, 17f);
        secondMatrix.setValueAt(2, 2, 18f);

        MultiplyMatrix multiplyMatrix = new MultiplyMatrix(firstMatrix, secondMatrix);

        assertEquals(2, firstMatrix.getRowCount());
        assertEquals(3, firstMatrix.getColumnCount());

        assertEquals(3, secondMatrix.getRowCount());
        assertEquals(4, secondMatrix.getColumnCount());

        assertEquals(2, multiplyMatrix.getRowCount());
        assertEquals(4, multiplyMatrix.getColumnCount());

        assertEquals(0.0f, multiplyMatrix.getValueAt(1, 3), 0.0f);
        assertEquals(74.0f, multiplyMatrix.getValueAt(0, 0), 0.0f);
        assertEquals(92.0f, multiplyMatrix.getValueAt(0, 2), 0.0f);
        assertEquals(188.0f, multiplyMatrix.getValueAt(1, 1), 0.0f);
        assertEquals(80.0f, multiplyMatrix.getValueAt(0, 1), 0.0f);
        assertEquals(218.0f, multiplyMatrix.getValueAt(1, 2), 0.0f);
    }

    @Test
    public void testTransposeMatrix() {
        StorageMatrix matrix = new StorageMatrix(3, 4);
        matrix.setValueAt(0, 0, 1f);
        matrix.setValueAt(0, 1, 2f);
        matrix.setValueAt(0, 2, 3f);
        matrix.setValueAt(0, 3, 4f);

        matrix.setValueAt(1, 0, 5f);
        matrix.setValueAt(1, 1, 6f);
        matrix.setValueAt(1, 2, 7f);
        matrix.setValueAt(1, 3, 8f);

        matrix.setValueAt(2, 0, 9f);
        matrix.setValueAt(2, 1, 10f);
        matrix.setValueAt(2, 2, 11f);
        matrix.setValueAt(2, 3, 12f);

        TransposeMatrix transposeMatrix = new TransposeMatrix(matrix);

        assertEquals(3, matrix.getRowCount());
        assertEquals(4, matrix.getColumnCount());

        assertEquals(4, transposeMatrix.getRowCount());
        assertEquals(3, transposeMatrix.getColumnCount());

        assertEquals(12.0f, transposeMatrix.getValueAt(3, 2), 0.0f);
        assertEquals(10.0f, transposeMatrix.getValueAt(1, 2), 0.0f);
        assertEquals(9.0f, transposeMatrix.getValueAt(0, 2), 0.0f);
        assertEquals(2.0f, transposeMatrix.getValueAt(1, 0), 0.0f);
        assertEquals(5.0f, transposeMatrix.getValueAt(0, 1), 0.0f);
        assertEquals(10.0f, transposeMatrix.getValueAt(1, 2), 0.0f);
        assertEquals(8.0f, transposeMatrix.getValueAt(3, 1), 0.0f);
        assertEquals(4.0f, transposeMatrix.getValueAt(3, 0), 0.0f);
    }

    @Test
    public void testSubMatrix() {
        StorageMatrix matrix = new StorageMatrix(3, 4);
        matrix.setValueAt(0, 0, 1f);
        matrix.setValueAt(0, 1, 2f);
        matrix.setValueAt(0, 2, 3f);
        matrix.setValueAt(0, 3, 4f);

        matrix.setValueAt(1, 0, 5f);
        matrix.setValueAt(1, 1, 6f);
        matrix.setValueAt(1, 2, 7f);
        matrix.setValueAt(1, 3, 8f);

        matrix.setValueAt(2, 0, 9f);
        matrix.setValueAt(2, 1, 10f);
        matrix.setValueAt(2, 2, 11f);
        matrix.setValueAt(2, 3, 12f);

        SubMatrix subMatrix = new SubMatrix(matrix, 0, 1);

        assertEquals(3, matrix.getRowCount());
        assertEquals(4, matrix.getColumnCount());

        assertEquals(2, subMatrix.getRowCount());
        assertEquals(3, subMatrix.getColumnCount());

        assertEquals(5.0f, subMatrix.getValueAt(0, 0), 0.0f);
        assertEquals(7.0f, subMatrix.getValueAt(0, 1), 0.0f);
        assertEquals(8.0f, subMatrix.getValueAt(0, 2), 0.0f);
        assertEquals(9.0f, subMatrix.getValueAt(1, 0), 0.0f);
        assertEquals(11.0f, subMatrix.getValueAt(1, 1), 0.0f);
        assertEquals(12.0f, subMatrix.getValueAt(1, 2), 0.0f);
    }
}
