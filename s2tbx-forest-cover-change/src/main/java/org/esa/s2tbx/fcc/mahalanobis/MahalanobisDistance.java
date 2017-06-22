package org.esa.s2tbx.fcc.mahalanobis;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.esa.s2tbx.fcc.ForestCoverChangeOp;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jcoravu on 6/6/2017.
 */
public class MahalanobisDistance {
    private static final Logger logger = Logger.getLogger(MahalanobisDistance.class.getName());

    public static Object2FloatOpenHashMap<PixelSourceBands> computeMahalanobisSquareMatrix(Collection<PixelSourceBands> points) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute the Mahalanobis distance for " + points.size() + " regions");
        }

        float meanValueB4Band = 0.0f;
        float meanValueB8Band = 0.0f;
        float meanValueB11Band = 0.0f;
        float standardDeviationValueB11Band = 0.0f;
        for (PixelSourceBands point : points) {
            meanValueB4Band += point.getMeanValueB4Band();
            meanValueB8Band += point.getMeanValueB8Band();
            meanValueB11Band += point.getMeanValueB11Band();
            standardDeviationValueB11Band += point.getStandardDeviationValueB8Band();
        }

        int numberOfPoints = points.size();

        meanValueB4Band = meanValueB4Band / (float)numberOfPoints;
        meanValueB8Band = meanValueB8Band / (float)numberOfPoints;
        meanValueB11Band = meanValueB11Band / (float)numberOfPoints;
        standardDeviationValueB11Band = standardDeviationValueB11Band / (float)numberOfPoints;

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "The centroid points are : meanValueB4Band="+meanValueB4Band+", meanValueB8Band="+meanValueB8Band+", meanValueB11Band="+meanValueB11Band+", standardDeviationValueB11Band="+standardDeviationValueB11Band+", numberOfPoints="+numberOfPoints);
        }

        TrimmingStatisticsMatrix trimmingStatisticsMatrix = new TrimmingStatisticsMatrix(points, meanValueB4Band, meanValueB8Band, meanValueB11Band, standardDeviationValueB11Band);

        Matrix inverseMatrix = computeInverseMatrix(trimmingStatisticsMatrix);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "computeMahalanobisSquareMatrix matrix="+trimmingStatisticsMatrix+", inverseMatrix="+inverseMatrix);
        }

        if (inverseMatrix != null) {
            float[] values = computeSquaredMahalanobisMatrix(trimmingStatisticsMatrix, inverseMatrix);
            Object2FloatOpenHashMap<PixelSourceBands> result = new Object2FloatOpenHashMap<PixelSourceBands>();
            for (int i=0; i<values.length; i++) {
                PixelSourceBands point = trimmingStatisticsMatrix.getPointAt(i);
                result.put(point, (float)Math.sqrt(values[i]));
            }
            return result;
        }
        return null;
    }

    //TODO Jean new method
    private static float[] computeSquaredMahalanobisMatrix(Matrix matrix, Matrix inverseMatrix) {

        MultiplyMatrix resultMatrix = new MultiplyMatrix(matrix, inverseMatrix);
        TransposeMatrix transposeMatrix = new TransposeMatrix(matrix);
        MultiplyMatrix squaredMahalanobisMatrix = new MultiplyMatrix(resultMatrix, transposeMatrix);
        int squaredMahalanobisMatrixRowCount = squaredMahalanobisMatrix.getRowCount();
        int squaredMahalanobisMatrixColumnCount = squaredMahalanobisMatrix.getColumnCount();
        if (squaredMahalanobisMatrixRowCount != squaredMahalanobisMatrixColumnCount) {
            throw new IllegalArgumentException("Wrong squared Mahalanobis squaredMahalanobisMatrixRowCount="+squaredMahalanobisMatrixRowCount+" squaredMahalanobisMatrixColumnCount="+squaredMahalanobisMatrixColumnCount);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "computeSquaredMahalanobisMatrix squaredMahalanobisMatrixRowCount="+squaredMahalanobisMatrixRowCount+" matrix="+matrix+", inverseMatrix="+inverseMatrix);
        }

        float[] values = new float[squaredMahalanobisMatrixRowCount];
        for (int i=0; i<squaredMahalanobisMatrix.getRowCount(); i++) {
            values[i] = squaredMahalanobisMatrix.getValueAt(i, i);

            if (i % 1000 == 0 && logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "values["+i+"]="+values[i]+"  squaredMahalanobisMatrixRowCount="+squaredMahalanobisMatrixRowCount);
            }
        }
        return values;
    }

    private static Matrix computeInverseMatrix(Matrix matrix) {
        TransposeMatrix transposeMatrix = new TransposeMatrix(matrix);
//        Matrix quadraticMatrix = MatrixUtils.multiply(transposeMatrix, matrix);
        MultiplyMatrix quadraticMatrix = new MultiplyMatrix(transposeMatrix, matrix);
        //Matrix covarianceMatrix = computeCovariance(quadraticMatrix, matrix.getRowCount());
        float value = 1.0f / (float)(matrix.getRowCount() - 1);
        MultiplyByConstantMatrix covarianceMatrix = new MultiplyByConstantMatrix(quadraticMatrix, value);
        return MatrixUtils.inverse(covarianceMatrix);
    }

//    private static Matrix computeCovariance(Matrix matrix, int inputRowCount) {
//        float value = 1.0f / (float)(inputRowCount-1);
//        int rowCount = matrix.getRowCount();
//        int columnCount = matrix.getColumnCount();
//        Matrix result = new Matrix(rowCount, columnCount);
//        for (int i=0; i<rowCount; i++) {
//            for (int j=0; j<columnCount; j++) {
//                result.setValueAt(i, j, value * matrix.getValueAt(i, j));
//            }
//        }
//        return result;
//    }

    public static void main(String args[]) {
        System.out.println("MahalanobisDistance main method");
//        COMPUTE X=t({1,2,3,4,5,6,7,8,9,10}).
//        COMPUTE Y=t({1,2,2,3,3,3,4,4,4,4}).

        List<PixelSourceBands> points = new ArrayList<PixelSourceBands>();
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

        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
        points.add(p5);
        points.add(p6);
        points.add(p7);
        points.add(p8);
        points.add(p9);
        points.add(p10);

        computeMahalanobisSquareMatrix(points);
    }
}
