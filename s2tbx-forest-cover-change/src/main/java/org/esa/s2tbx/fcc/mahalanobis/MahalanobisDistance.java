package org.esa.s2tbx.fcc.mahalanobis;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by jcoravu on 6/6/2017.
 */
public class MahalanobisDistance {
    private List<PixelSourceBands> points;

    public MahalanobisDistance() {
        this.points = new ArrayList<PixelSourceBands>();
    }

    public void addPoint(PixelSourceBands pointToAdd) {
        this.points.add(pointToAdd);
    }

    public static Object2DoubleMap<PixelSourceBands> computeMahalanobisSquareMatrix(Collection<PixelSourceBands> points) {
        double meanValueB4Band = 0.0d;
        double meanValueB8Band = 0.0d;
        double meanValueB11Band = 0.0d;
        double standardDeviationValueB11Band = 0.0d;
        for (PixelSourceBands point : points) {
            meanValueB4Band += point.getMeanValueB4Band();
            meanValueB8Band += point.getMeanValueB8Band();
            meanValueB11Band += point.getMeanValueB11Band();
            standardDeviationValueB11Band += point.getStandardDeviationValueB8Band();
        }

        int numberOfPoints = points.size();

        meanValueB4Band = meanValueB4Band / (double)numberOfPoints;
        meanValueB8Band = meanValueB8Band / (double)numberOfPoints;
        meanValueB11Band = meanValueB11Band / (double)numberOfPoints;
        standardDeviationValueB11Band = standardDeviationValueB11Band / (double)numberOfPoints;

        System.out.println("meanValueB4Band="+meanValueB4Band+"  meanValueB8Band="+meanValueB8Band);

        Matrix matrix = new Matrix(numberOfPoints, 4);
        Int2ObjectMap<PixelSourceBands> map = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();
        int index = -1;
        for (PixelSourceBands point : points) {
            index++;
            map.put(index, point);
            matrix.setValueAt(index, 0, (point.getMeanValueB4Band() - meanValueB4Band));
            matrix.setValueAt(index, 1, (point.getMeanValueB8Band() - meanValueB8Band));
            matrix.setValueAt(index, 2, (point.getMeanValueB11Band() - meanValueB11Band));
            matrix.setValueAt(index, 3, (point.getStandardDeviationValueB8Band() - standardDeviationValueB11Band));
        }
        Matrix transposeMatrix = MatrixUtils.transpose(matrix);

        Matrix quadraticMatrix = MatrixUtils.multiply(transposeMatrix, matrix);
        Matrix covarianceMatrix = computeCovariance(quadraticMatrix, matrix.getRowCount());
        Matrix inverseMatrix = MatrixUtils.inverse(covarianceMatrix);
        Matrix resultMatrix = MatrixUtils.multiply(matrix, inverseMatrix);
        Matrix squaredMahalanobisMatrix = MatrixUtils.multiply(resultMatrix, transposeMatrix);
        Object2DoubleMap<PixelSourceBands> result = new Object2DoubleOpenHashMap<PixelSourceBands>();
        int matrixSize = squaredMahalanobisMatrix.getColumnCount();
        for (int i=0; i<matrixSize; i++) {
            double value = squaredMahalanobisMatrix.getValueAt(i, i);
            PixelSourceBands point = map.get(i);
            result.put(point, Math.sqrt(value));
        }
        return result;
    }

    public void computerCenterPoint() {
        computeMahalanobisSquareMatrix(this.points);
    }

    private static Matrix computeCovariance(Matrix matrix, int inputRowCount) {
        float value = 1.0f / (float)(inputRowCount-1);
        int rowCount = matrix.getRowCount();
        int columnCount = matrix.getColumnCount();
        Matrix result = new Matrix(rowCount, columnCount);
        for (int i=0; i<rowCount; i++) {
            for (int j=0; j<columnCount; j++) {
                result.setValueAt(i, j, value * matrix.getValueAt(i, j));
            }
        }
        return result;
    }

    public static void main(String args[]) {
        System.out.println("MahalanobisDistance main method");
//        COMPUTE X=t({1,2,3,4,5,6,7,8,9,10}).
//        COMPUTE Y=t({1,2,2,3,3,3,4,4,4,4}).

        PixelSourceBands p1 = new PixelSourceBands(1, 1, 0, 0);
        PixelSourceBands p2 = new PixelSourceBands(2, 2, 0, 0);
        PixelSourceBands p3 = new PixelSourceBands(3, 2, 0, 0);
        PixelSourceBands p4 = new PixelSourceBands(4, 3, 0, 0);
        PixelSourceBands p5 = new PixelSourceBands(5, 3, 0, 0);
        PixelSourceBands p6 = new PixelSourceBands(6, 3, 0, 0);
        PixelSourceBands p7 = new PixelSourceBands(7, 4, 0, 0);
        PixelSourceBands p8 = new PixelSourceBands(8, 4, 0, 0);
        PixelSourceBands p9 = new PixelSourceBands(9, 4, 0, 0);
        PixelSourceBands p10 = new PixelSourceBands(10, 4, 0, 0);

        MahalanobisDistance mahalanobisDistance = new MahalanobisDistance();
        mahalanobisDistance.addPoint(p1);
        mahalanobisDistance.addPoint(p2);
        mahalanobisDistance.addPoint(p3);
        mahalanobisDistance.addPoint(p4);
        mahalanobisDistance.addPoint(p5);
        mahalanobisDistance.addPoint(p6);
        mahalanobisDistance.addPoint(p7);
        mahalanobisDistance.addPoint(p8);
        mahalanobisDistance.addPoint(p9);
        mahalanobisDistance.addPoint(p10);

        mahalanobisDistance.computerCenterPoint();
    }
}
