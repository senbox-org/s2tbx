package org.esa.s2tbx.fcc.mahalanobis;

import org.esa.s2tbx.fcc.intern.PixelSourceBands;

import java.util.ArrayList;
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

    public void computerCenterPoint() {
        int numberOfPoints = this.points.size();
        double meanValueB4Band = 0.0f;
        double meanValueB8Band = 0.0f;
        double meanValueB11Band = 0.0f;
        double meanValueB12Band = 0.0f;
        for (int i=0; i<numberOfPoints; i++) {
            PixelSourceBands point = this.points.get(i);
            meanValueB4Band += point.getMeanValueB4Band();
            meanValueB8Band += point.getMeanValueB8Band();
            meanValueB11Band += point.getMeanValueB11Band();
            meanValueB12Band += point.getStandardDeviationValueB8Band();
        }
        meanValueB4Band = meanValueB4Band / numberOfPoints;
        meanValueB8Band = meanValueB8Band / numberOfPoints;
        meanValueB11Band = meanValueB11Band / numberOfPoints;
        meanValueB12Band = meanValueB12Band / numberOfPoints;

        System.out.println("meanValueB4Band="+meanValueB4Band+"  meanValueB8Band="+meanValueB8Band);

        Matrix matrix = new Matrix(numberOfPoints, 2);
        for (int i=0; i<numberOfPoints; i++) {
            PixelSourceBands point = this.points.get(i);
            matrix.setValueAt(i, 0, (float)(point.getMeanValueB4Band() - meanValueB4Band));
            matrix.setValueAt(i, 1, (float)(point.getMeanValueB8Band() - meanValueB8Band));
//            matrix.setValueAt(i, 2, (point.getValueB11Band() - meanValueB11Band));
//            matrix.setValueAt(i, 3, (point.getValueB12Band() - meanValueB12Band));
        }
        Matrix transposeMatrix = MatrixUtils.transpose(matrix);

        Matrix quadraticMatrix = MatrixUtils.multiply(transposeMatrix, matrix);
        Matrix covarianceMatrix = computeCovariance(quadraticMatrix, matrix.getRowCount());
        Matrix inverseMatrix = MatrixUtils.inverse(covarianceMatrix);
        Matrix resultMatrix = MatrixUtils.multiply(matrix, inverseMatrix);
        Matrix squaredMahalanobisMatrix = MatrixUtils.multiply(resultMatrix, transposeMatrix);
    }

    private Matrix computeCovariance(Matrix matrix, int inputRowCount) {
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
