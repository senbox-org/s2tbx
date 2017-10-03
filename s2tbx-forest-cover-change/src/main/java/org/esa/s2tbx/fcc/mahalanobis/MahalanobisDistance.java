package org.esa.s2tbx.fcc.mahalanobis;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.s2tbx.fcc.trimming.PixelSourceBands;
import org.esa.snap.utils.AbstractArrayCellsParallelComputing;
import org.esa.snap.utils.AbstractMatrixCellsParallelComputing;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class MahalanobisDistance {
    private static final Logger logger = Logger.getLogger(MahalanobisDistance.class.getName());

    public static Int2ObjectMap<PixelSourceBands> computeValidRegionsInParallel(int threadCount, Executor threadPool,
                                                                     Int2ObjectMap<PixelSourceBands> validRegionStatistics, double cumulativeProbability)
                                                                     throws Exception {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start computing the Mahalanobis distance: valid region count: " + validRegionStatistics.size() + ", Chi distribution: "+cumulativeProbability+ ", thread count: " + threadCount);
        }

        TrimmingStatisticsMatrix trimmingStatisticsMatrix = new TrimmingStatisticsMatrix(validRegionStatistics);

        Matrix inverseMatrix = computeInverseMatrix(threadCount, threadPool, trimmingStatisticsMatrix);
        Int2ObjectMap<PixelSourceBands> result = null;
        if (inverseMatrix == null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Finish computing the Mahalanobis distance: valid region count: " + validRegionStatistics.size() + ", removed region count: "+0+", Chi distribution: "+cumulativeProbability+ ", thread count: " + threadCount+", no inverse matrix");
            }
        } else {
            MahalanobisDistanceHelper mahalanobisDistanceHelper = new MahalanobisDistanceHelper(trimmingStatisticsMatrix, inverseMatrix, cumulativeProbability);
            result = mahalanobisDistanceHelper.computeCellsInParallel(threadCount, threadPool);

            if (logger.isLoggable(Level.FINE)) {
                int removedRegionCount = validRegionStatistics.size() - result.size();
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Finish computing the Mahalanobis distance: valid region count: " + result.size() + ", removed region count: "+removedRegionCount+ ", Chi distribution: "+cumulativeProbability+ ", thread count: " + threadCount);
            }

            WeakReference<Matrix> reference = new WeakReference<Matrix>(inverseMatrix);
            reference.clear();
        }

        WeakReference<TrimmingStatisticsMatrix> reference = new WeakReference<TrimmingStatisticsMatrix>(trimmingStatisticsMatrix);
        reference.clear();

        return result;
    }

    private static Matrix computeInverseMatrix(int threadCount, Executor threadPool, Matrix matrix) throws Exception {
        TransposeMatrix transposeMatrix = new TransposeMatrix(matrix);
        MultiplyMatrix quadraticMatrix = new MultiplyMatrix(transposeMatrix, matrix);
        float value = 1.0f / (float)(matrix.getRowCount() - 1);
        MultiplyByConstantMatrix covarianceMatrix = new MultiplyByConstantMatrix(quadraticMatrix, value);

        float matrixDeterminant = covarianceMatrix.computeDeterminant();
        if (matrixDeterminant == 0.0f) {
            return null;
        }
        CofactorMatrix cofactorMatrix = new CofactorMatrix(covarianceMatrix);
        TransposeMatrix transposeCofactorMatrix = new TransposeMatrix(cofactorMatrix);
        float constant = 1.0f / matrixDeterminant;
        Matrix result = new MultiplyByConstantMatrix(transposeCofactorMatrix, constant);

        StorageMatrixHelper storageMatrixHelper = new StorageMatrixHelper(result);
        return storageMatrixHelper.computeMatrixCellsInParallel(threadCount, threadPool);
    }

    private static class StorageMatrixHelper extends AbstractMatrixCellsParallelComputing {
        private final Matrix inputMatrix;
        private final StorageMatrix storageMatrix;

        StorageMatrixHelper(Matrix inputMatrix) {
            super(inputMatrix.getRowCount(), inputMatrix.getColumnCount());

            this.inputMatrix = inputMatrix;
            this.storageMatrix = new StorageMatrix(this.inputMatrix.getRowCount(), this.inputMatrix.getColumnCount());
        }

        @Override
        protected final void runTile(int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException, InterruptedException {
            float cellValue = this.inputMatrix.getValueAt(localRowIndex, localColumnIndex);
            this.storageMatrix.setValueAt(localRowIndex, localColumnIndex, cellValue);
        }

        StorageMatrix computeMatrixCellsInParallel(int threadCount, Executor threadPool) throws Exception {
            super.executeInParallel(threadCount, threadPool);

            return this.storageMatrix;
        }
    }

    private static class MahalanobisDistanceHelper extends AbstractArrayCellsParallelComputing {
        private final TrimmingStatisticsMatrix trimmingStatisticsMatrix;
        private final MultiplyMatrix squaredMahalanobisMatrix;
        private final Int2ObjectMap<PixelSourceBands> validStatistics;
        private final double cumulativeProbability;

        MahalanobisDistanceHelper(TrimmingStatisticsMatrix trimmingStatisticsMatrix, Matrix inverseMatrix, double cumulativeProbability) {
            super(trimmingStatisticsMatrix.getRowCount());

            MultiplyMatrix resultMatrix = new MultiplyMatrix(trimmingStatisticsMatrix, inverseMatrix);
            TransposeMatrix transposeMatrix = new TransposeMatrix(trimmingStatisticsMatrix);
            this.squaredMahalanobisMatrix = new MultiplyMatrix(resultMatrix, transposeMatrix);
            if (this.squaredMahalanobisMatrix.getRowCount() != trimmingStatisticsMatrix.getRowCount()) {
                throw new IllegalArgumentException("Wrong size");
            }
            if (!this.squaredMahalanobisMatrix.isSquare()) {
                throw new IllegalArgumentException("The Mahalanobis matrix is not a squared matrix.");
            }

            this.trimmingStatisticsMatrix = trimmingStatisticsMatrix;
            this.cumulativeProbability = cumulativeProbability;
            this.validStatistics = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();
        }

        @Override
        protected void computeCell(int localIndex) {
            float squareDistance = this.squaredMahalanobisMatrix.getValueAt(localIndex, localIndex);
            if (Math.sqrt(squareDistance) <= this.cumulativeProbability) {
                synchronized (this.validStatistics) {
                    this.validStatistics.put(this.trimmingStatisticsMatrix.getRegionKeyAt(localIndex), this.trimmingStatisticsMatrix.getRegionMeanPixelsAt(localIndex));
                }
            }

            if (localIndex % 1000 == 0 && logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, ""); // add an empty line
                logger.log(Level.FINER, "Mahalanobis distance computation: index: "+localIndex+", total regions: "+this.trimmingStatisticsMatrix.getRowCount()+", distance: " +Math.sqrt(squareDistance)+", chi distribution: "+this.cumulativeProbability);
            }
        }

        public Int2ObjectMap<PixelSourceBands> computeCellsInParallel(int threadCount, Executor threadPool) throws Exception {
            super.executeInParallel(threadCount, threadPool);

            return this.validStatistics;
        }
    }
}
